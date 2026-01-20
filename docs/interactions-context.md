# InteractionContext

> **Prerequisites:** Read [interactions.md](interactions.md) and [Operation System](interactions-operations.md) first.
>
> **See also:** [Item Definitions](items.md) for `InteractionVars`, [entities.md](entities.md#interactionmanager) for `InteractionManager`.

`InteractionContext` is the execution state container passed to operations during interaction execution. It provides access to entities, items, targeting data, and flow control.

## Overview

When an interaction runs, `InteractionContext` carries:

- **Entity references** - The owning entity, executing entity, and targets
- **Item state** - The held item being used
- **Meta store** - Key-value data like hit locations and targets
- **InteractionVars** - Item-defined variables for customization
- **Flow control** - Labels and jump capabilities
- **Chain management** - Current chain and entry tracking

Understanding `InteractionContext` is essential for:
- Accessing targets selected by `Selector` interactions
- Reading item-specific variables
- Passing data between operations
- Implementing custom interactions

---

## InteractionContext Class

**Package:** `com.hypixel.hytale.server.core.modules.interaction.interaction`

### Core Methods

```java
public class InteractionContext {
    // Entity access
    Ref<EntityStore> getEntity();           // Entity executing the interaction
    Ref<EntityStore> getOwningEntity();     // Entity that owns the interaction chain
    Ref<EntityStore> getTargetEntity();     // Current target (from Selector)

    // Item access
    ItemStack getHeldItem();                // The item being used
    int getHeldItemSlot();                  // Slot index of held item
    ItemType getOriginalItemType();         // Item type when chain started

    // Meta store (dynamic data)
    <T> T getMeta(MetaKey<T> key);
    <T> void setMeta(MetaKey<T> key, T value);
    MetaStore getMetaStore();

    // InteractionVars (item-defined variables)
    InteractionVars getInteractionVars();
    void setInteractionVarsGetter(InteractionVarsGetter getter);

    // Flow control
    void jump(Label label);
    int getOperationCounter();
    void setLabels(Label... labels);
    void advanceOperation();

    // Chain management
    InteractionChain getChain();
    InteractionEntry getEntry();
    InteractionContext fork();
}
```

---

## Entity References

### Owning vs Executing Entity

In most cases, these are the same entity. They differ in delegated interactions:

| Method | Description | Example |
|--------|-------------|---------|
| `getOwningEntity()` | Entity that initiated the chain | Player who summoned a minion |
| `getEntity()` | Entity currently executing | The minion attacking |

```java
@Override
public void tick(..., InteractionContext context, ...) {
    Ref<EntityStore> owner = context.getOwningEntity();
    Ref<EntityStore> executor = context.getEntity();

    // Usually the same
    if (owner.equals(executor)) {
        // Direct execution
    } else {
        // Delegated (e.g., summon, pet, turret)
    }
}
```

### Target Entity

Set by `Selector` interactions (melee hitbox, raycast, AOE):

```java
@Override
public void tick(..., InteractionContext context, ...) {
    Ref<EntityStore> target = context.getTargetEntity();

    if (target != null && target.isValid()) {
        // Apply effect to target
        LivingEntity targetEntity = target.get(LivingEntity.class);
        if (targetEntity != null) {
            // Deal damage, apply effect, etc.
        }
    }
}
```

---

## Item Access

### Held Item

```java
@Override
public void tick(..., InteractionContext context, ...) {
    ItemStack heldItem = context.getHeldItem();

    if (heldItem != null && !heldItem.isEmpty()) {
        ItemType itemType = heldItem.getItemType();
        int count = heldItem.getCount();

        // Access item data
        // ...
    }
}
```

### Original Item Type

Tracks the item type when the chain started. Useful for detecting item swaps:

```java
@Override
public void tick(..., InteractionContext context, ...) {
    ItemType original = context.getOriginalItemType();
    ItemStack current = context.getHeldItem();

    if (current != null && !current.getItemType().equals(original)) {
        // Item changed during interaction - might want to cancel
    }
}
```

### Held Item Slot

```java
int slot = context.getHeldItemSlot();
// Use for inventory operations
```

---

## Meta Store

The meta store is a key-value map for passing data between operations. Standard keys are defined on the `Interaction` class.

### Standard Meta Keys

| Key | Type | Set By | Description |
|-----|------|--------|-------------|
| `TARGET_ENTITY` | `Ref<EntityStore>` | Selector | Entity hit by selector |
| `HIT_LOCATION` | `Vector4d` | Selector | World position of hit |
| `HIT_DETAIL` | `String` | Selector | Hit detail info |
| `TARGET_BLOCK` | `BlockPosition` | Block targeting | Block being interacted with |
| `TARGET_BLOCK_RAW` | `BlockPosition` | Block targeting | Raw block position |
| `TARGET_SLOT` | `Integer` | Inventory ops | Target inventory slot |
| `TIME_SHIFT` | `Float` | Timing ops | Time offset |
| `DAMAGE` | `Damage` | Damage ops | Damage calculation result |

### Reading Meta Values

```java
@Override
public void tick(..., InteractionContext context, ...) {
    // Get target from selector
    Ref<EntityStore> target = context.getMeta(Interaction.TARGET_ENTITY);

    // Get hit position
    Vector4d hitPos = context.getMeta(Interaction.HIT_LOCATION);

    // Get damage info
    Damage damage = context.getMeta(Interaction.DAMAGE);

    if (target != null && hitPos != null) {
        // Spawn hit effect at location
    }
}
```

### Writing Meta Values

```java
@Override
public void tick(..., InteractionContext context, ...) {
    // Store data for later operations
    context.setMeta(Interaction.TARGET_ENTITY, foundTarget);
    context.setMeta(Interaction.HIT_LOCATION, hitPosition);
}
```

### Custom Meta Keys

For custom interactions, create your own `MetaKey`:

```java
// Define a custom key (typically static final)
public static final MetaKey<MyData> MY_CUSTOM_DATA =
    MetaKey.create("my_plugin:custom_data", MyData.class);

// Write
context.setMeta(MY_CUSTOM_DATA, new MyData(...));

// Read
MyData data = context.getMeta(MY_CUSTOM_DATA);
```

---

## InteractionVars

`InteractionVars` are item-defined variables that customize interaction behavior. They allow a single interaction definition to behave differently based on the item using it.

### Accessing InteractionVars

```java
@Override
public void tick(..., InteractionContext context, ...) {
    InteractionVars vars = context.getInteractionVars();

    // Get typed values
    float damage = vars.getFloat("Damage", 10.0f);  // Default 10
    String effectId = vars.getString("EffectId", "none");
    int count = vars.getInt("HitCount", 1);
}
```

### Item Definition with InteractionVars

Items define vars in their JSON:

```json
{
  "Type": "Item",
  "InteractionVars": {
    "Damage": 25.0,
    "EffectId": "hytale:burning",
    "HitCount": 3
  }
}
```

### Common InteractionVars Patterns

Weapon damage scaling:
```json
{
  "InteractionVars": {
    "Damage": 15,
    "DamageStat": "Vigor",
    "KnockbackForce": 800
  }
}
```

Ability customization:
```json
{
  "InteractionVars": {
    "ProjectileSpeed": 50,
    "ProjectileCount": 3,
    "SpreadAngle": 15
  }
}
```

See [items.md](items.md) for full `InteractionVars` documentation.

---

## Flow Control

### Jumping to Labels

Operations can jump to labels set during compilation:

```java
@Override
public void tick(..., InteractionContext context, ...) {
    if (conditionFailed) {
        // Jump to skip label (set via addOperation(..., skipLabel))
        context.jump(skipLabel);
    }
}
```

### Advancing Operations

Signal that the current operation is complete:

```java
@Override
public void tick(..., InteractionContext context, ...) {
    elapsed += deltaTime;
    if (elapsed >= duration) {
        context.advanceOperation();  // Move to next operation
    }
}
```

### Operation Counter

Track current position in the operation array:

```java
int currentOp = context.getOperationCounter();
```

---

## Chain Management

### InteractionChain

The chain represents the full execution context:

```java
InteractionChain chain = context.getChain();

// Chain state
boolean isComplete = chain.isComplete();
InteractionState state = chain.getState();

// Chain identification
String chainId = chain.getId();
```

### InteractionEntry

The entry is the starting point configuration:

```java
InteractionEntry entry = context.getEntry();

// Get the root interaction
RootInteraction root = entry.getRootInteraction();
```

### Forking Contexts

Create a new context for parallel/branched execution:

```java
InteractionContext forkedContext = context.fork();
// forkedContext shares meta store but has independent operation tracking
```

---

## Creating Contexts

### Factory Methods

Contexts are typically created by the interaction system, but can be created manually:

```java
// Create context for an entity
InteractionContext context = InteractionContext.create(
    entityRef,           // Executing entity
    ownerRef,            // Owning entity (usually same)
    heldItem,            // Item being used
    heldItemSlot,        // Slot index
    interactionVars      // Vars from item
);
```

### Context in Custom Interactions

When implementing custom interactions, you receive the context:

```java
@Override
public void tick(Ref<EntityStore> ref, LivingEntity entity, boolean isFirstTick,
                 float deltaTime, InteractionType type, InteractionContext context,
                 CooldownHandler cooldown) {
    // Context is fully initialized
    // Access any data you need
}
```

---

## Usage Examples

### Complete Custom Operation

```java
public class ApplyBurnOp implements Operation {
    @Override
    public void tick(Ref<EntityStore> ref, LivingEntity entity, boolean isFirstTick,
                     float deltaTime, InteractionType type, InteractionContext context,
                     CooldownHandler cooldown) {
        if (!isFirstTick) {
            context.advanceOperation();
            return;
        }

        // Get target from previous Selector operation
        Ref<EntityStore> target = context.getMeta(Interaction.TARGET_ENTITY);
        if (target == null || !target.isValid()) {
            context.advanceOperation();
            return;
        }

        // Get burn duration from item's InteractionVars
        InteractionVars vars = context.getInteractionVars();
        float burnDuration = vars.getFloat("BurnDuration", 5.0f);

        // Apply burn effect to target
        LivingEntity targetEntity = target.get(LivingEntity.class);
        if (targetEntity != null) {
            // Apply effect logic...
        }

        context.advanceOperation();
    }

    // ... other Operation methods
}
```

### Conditional Branching with Meta

```java
public class CheckCriticalHitOp implements Operation {
    private final Label critLabel;
    private final Label normalLabel;

    public CheckCriticalHitOp(Label critLabel, Label normalLabel) {
        this.critLabel = critLabel;
        this.normalLabel = normalLabel;
    }

    @Override
    public void tick(..., InteractionContext context, ...) {
        Damage damage = context.getMeta(Interaction.DAMAGE);

        if (damage != null && damage.isCritical()) {
            context.jump(critLabel);
        } else {
            context.jump(normalLabel);
        }
    }
}
```

---

## Related Documentation

- [Operation System](interactions-operations.md) - Execution model and OperationsBuilder
- [interactions.md](interactions.md) - Interaction types and configuration
- [items.md](items.md) - Item definitions and InteractionVars
- [entities.md](entities.md#interactionmanager) - InteractionManager component
