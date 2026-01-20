# Operation System

> **Prerequisites:** Read [interactions.md](interactions.md) first for an overview of the interaction system.
>
> **See also:** [InteractionContext](interactions-context.md) for execution state, [interactions.md](interactions.md) for interaction types.

The Operation system is the low-level execution model that powers all interactions. When an interaction runs, it compiles into a sequence of Operations that execute frame-by-frame.

## Overview

Every `Interaction` implements the `Operation` interface. The interaction system:

1. **Compiles** interactions into operation arrays via `OperationsBuilder`
2. **Executes** operations sequentially, calling `tick()` each frame
3. **Controls flow** using labels and conditional jumps
4. **Synchronizes** client/server via `simulateTick()` for prediction

Understanding Operations is essential for:
- Creating custom interactions with complex control flow
- Debugging interaction execution
- Understanding timing and frame-by-frame behavior

---

## Operation Interface

**Package:** `com.hypixel.hytale.server.core.modules.interaction.interaction.operation`

The `Operation` interface defines the contract for executable interaction steps.

### Core Methods

```java
public interface Operation {
    // Server-side execution (called every frame while active)
    void tick(Ref<EntityStore> ref, LivingEntity entity, boolean isFirstTick,
              float deltaTime, InteractionType type, InteractionContext context,
              CooldownHandler cooldown);

    // Client-side prediction (mirrors tick for client simulation)
    void simulateTick(Ref<EntityStore> ref, LivingEntity entity, boolean isFirstTick,
                      float deltaTime, InteractionType type, InteractionContext context,
                      CooldownHandler cooldown);

    // Called when operation becomes active
    void handle(Ref<EntityStore> ref, boolean isStart, float deltaTime,
                InteractionType type, InteractionContext context);

    // Determines client/server sync behavior
    WaitForDataFrom getWaitForDataFrom();

    // Returns conflict resolution rules
    InteractionRules getRules();

    // Tag-based metadata for the operation
    Int2ObjectMap<IntSet> getTags();

    // For wrapped/decorated operations
    Operation getInnerOperation();
}
```

### tick() vs simulateTick()

| Method | Runs On | Purpose |
|--------|---------|---------|
| `tick()` | Server | Authoritative execution with full world access |
| `simulateTick()` | Client | Prediction for responsive feel; may be corrected |

Both methods receive identical parameters:
- `ref` - Entity store reference for the executing entity
- `entity` - The `LivingEntity` performing the operation
- `isFirstTick` - `true` on the first frame of this operation
- `deltaTime` - Time since last frame (for timing calculations)
- `type` - The `InteractionType` (PRIMARY, SECONDARY, etc.)
- `context` - Execution state container (see [InteractionContext](interactions-context.md))
- `cooldown` - Manages cooldown timers (see [Cooldowns](interactions.md#cooldown-system))

### WaitForDataFrom Enum

Controls synchronization between client and server:

| Value | Behavior |
|-------|----------|
| `NONE` | Execute immediately on both client and server |
| `SERVER` | Client waits for server confirmation before executing |
| `CLIENT` | Server waits for client data (rare, for client-authoritative actions) |

---

## OperationsBuilder

**Package:** `com.hypixel.hytale.server.core.modules.interaction.interaction.operation`

`OperationsBuilder` constructs operation sequences with label-based flow control. Interactions override `compile(OperationsBuilder)` to build their operation sequence.

### Building Operation Sequences

```java
public class OperationsBuilder {
    // Add an operation to the sequence
    void addOperation(Operation operation, Label... jumpTargets);

    // Build the final operation array
    Operation[] build();
}
```

### Basic Usage

```java
@Override
public void compile(OperationsBuilder builder) {
    // Operations execute in order
    builder.addOperation(new PlayAnimationOp("SwingDown"));
    builder.addOperation(new WaitOp(0.2f));
    builder.addOperation(new DealDamageOp());
}
```

---

## Label System

Labels enable non-linear control flow within operation sequences. This is how conditional branching, loops, and early exits work.

### Creating Labels

```java
public class OperationsBuilder {
    // Create a label at the current position
    Label createLabel();

    // Create a placeholder label (position set later)
    Label createUnresolvedLabel();

    // Set an unresolved label to the current position
    void resolveLabel(Label label);

    // Jump to a label from current position
    void jump(Label target);
}
```

### Control Flow Patterns

#### Conditional Branch

```java
@Override
public void compile(OperationsBuilder builder) {
    Label skipDamage = builder.createUnresolvedLabel();

    // Check condition - operation may jump to skipDamage
    builder.addOperation(new ConditionalCheckOp(), skipDamage);

    // Damage (skipped if condition fails)
    builder.addOperation(new DealDamageOp());

    // Target for skip
    builder.resolveLabel(skipDamage);

    // Continue with cleanup (always runs)
    builder.addOperation(new CleanupOp());
}
```

#### Early Exit

```java
@Override
public void compile(OperationsBuilder builder) {
    Label exit = builder.createUnresolvedLabel();

    builder.addOperation(new ValidateOp(), exit);  // Jump to exit on failure
    builder.addOperation(new ExecuteOp());
    builder.addOperation(new FinishOp());

    builder.resolveLabel(exit);  // Exit point
}
```

#### Loop

```java
@Override
public void compile(OperationsBuilder builder) {
    Label loopStart = builder.createLabel();  // Mark loop start
    Label loopEnd = builder.createUnresolvedLabel();

    builder.addOperation(new CheckLoopConditionOp(), loopEnd);  // Exit when done
    builder.addOperation(new LoopBodyOp());
    builder.jump(loopStart);  // Jump back to start

    builder.resolveLabel(loopEnd);  // Exit point
}
```

### Runtime Jumps

Operations can trigger jumps at runtime via `InteractionContext`:

```java
// Inside an Operation's tick() method
public void tick(..., InteractionContext context, ...) {
    if (shouldSkip) {
        // Jump to a label set during compilation
        context.jump(skipLabel);
    }
}
```

The labels passed to `addOperation()` become available for the operation to jump to during execution.

---

## How Interactions Compile to Operations

When an interaction chain starts, the system:

1. **Collects** all interactions in the chain
2. **Calls** `compile(OperationsBuilder)` on each
3. **Builds** the final `Operation[]` array
4. **Executes** operations sequentially via `tick()`

### Compilation Example

A `Serial` interaction containing two children compiles like this:

```java
// Serial with children A, B
@Override
public void compile(OperationsBuilder builder) {
    childA.compile(builder);  // Add A's operations
    childB.compile(builder);  // Add B's operations
}
// Result: [A-ops..., B-ops...]
```

A `Parallel` interaction is more complex, using labels to coordinate:

```java
// Parallel with children A, B
@Override
public void compile(OperationsBuilder builder) {
    Label aEnd = builder.createUnresolvedLabel();
    Label bEnd = builder.createUnresolvedLabel();

    // A's operations (jump to aEnd when done)
    childA.compile(builder);
    builder.jump(aEnd);

    // B's operations
    childB.compile(builder);
    builder.resolveLabel(bEnd);

    builder.resolveLabel(aEnd);
    // Continue after both complete
}
```

---

## Execution Flow

### Frame-by-Frame Execution

Each frame during an interaction:

1. **Get current operation** from the operation array
2. **Call** `tick()` with current context
3. **Check** if operation is complete
4. **Advance** to next operation (or jump if label triggered)
5. **Repeat** until all operations complete

### Operation Lifecycle

```
handle(isStart=true)  →  tick() [frame 1]  →  tick() [frame 2]  →  ...  →  handle(isStart=false)
```

- `handle(isStart=true)` - Called when operation becomes active
- `tick()` - Called every frame while active
- `handle(isStart=false)` - Called when operation completes/exits

---

## Custom Operation Implementation

To create a custom operation, implement the `Operation` interface:

```java
public class MyCustomOp implements Operation {
    private final float duration;
    private float elapsed = 0f;

    public MyCustomOp(float duration) {
        this.duration = duration;
    }

    @Override
    public void tick(Ref<EntityStore> ref, LivingEntity entity, boolean isFirstTick,
                     float deltaTime, InteractionType type, InteractionContext context,
                     CooldownHandler cooldown) {
        if (isFirstTick) {
            // Initialize on first frame
            elapsed = 0f;
        }

        elapsed += deltaTime;

        if (elapsed >= duration) {
            // Operation complete - advance to next
            context.advanceOperation();
        }
    }

    @Override
    public void simulateTick(Ref<EntityStore> ref, LivingEntity entity, boolean isFirstTick,
                             float deltaTime, InteractionType type, InteractionContext context,
                             CooldownHandler cooldown) {
        // Client prediction - usually mirrors tick()
        tick(ref, entity, isFirstTick, deltaTime, type, context, cooldown);
    }

    @Override
    public void handle(Ref<EntityStore> ref, boolean isStart, float deltaTime,
                       InteractionType type, InteractionContext context) {
        // Optional: Setup/cleanup logic
    }

    @Override
    public WaitForDataFrom getWaitForDataFrom() {
        return WaitForDataFrom.NONE;  // No sync needed
    }

    @Override
    public InteractionRules getRules() {
        return InteractionRules.DEFAULT;
    }

    @Override
    public Int2ObjectMap<IntSet> getTags() {
        return Int2ObjectMaps.emptyMap();
    }

    @Override
    public Operation getInnerOperation() {
        return null;  // Not wrapping another operation
    }
}
```

---

## Common Operation Patterns

### Timed Operations

Most operations track elapsed time:

```java
@Override
public void tick(..., float deltaTime, ..., InteractionContext context, ...) {
    elapsed += deltaTime;
    if (elapsed >= runTime) {
        context.advanceOperation();
    }
}
```

### Conditional Completion

Some operations complete based on conditions:

```java
@Override
public void tick(..., InteractionContext context, ...) {
    if (targetReached || cancelled) {
        context.advanceOperation();
    }
}
```

### Triggering Effects

Effects typically fire on first tick:

```java
@Override
public void tick(..., boolean isFirstTick, ...) {
    if (isFirstTick) {
        playSound();
        playAnimation();
        spawnParticles();
    }
}
```

---

## Related Documentation

- [InteractionContext](interactions-context.md) - Execution state and data access
- [interactions.md](interactions.md) - Interaction types and configuration
- [interactions-combo.md](interactions-combo.md) - Combo and chaining systems
- [interactions-flow.md](interactions-flow.md) - Control flow interactions
