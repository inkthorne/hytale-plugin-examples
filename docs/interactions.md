# Interactions API

> **Note:** For `InteractionManager` (the entity component that manages interaction chains), see [entities.md](entities.md#interactionmanager).
>
> **See also:** [Item Definitions](items.md) for how items define and customize interactions via `InteractionVars`.

## Quick Navigation

| Category | File | Description |
|----------|------|-------------|
| [Combo Systems](interactions-combo.md) | `interactions-combo.md` | Chaining, charging, input branching |
| [Combat & Effects](interactions-combat.md) | `interactions-combat.md` | Damage, forces, status effects, animations |
| [Control Flow](interactions-flow.md) | `interactions-flow.md` | Serial, parallel, conditions, targeting |
| [Entity & World](interactions-world.md) | `interactions-world.md` | Spawning, inventory, blocks, wielding |

### All Interactions by Category

**Combo Systems** ([interactions-combo.md](interactions-combo.md))
- [ChainingInteraction](interactions-combo.md#chaininginteraction) - Sequential combo chains with timing windows
- [FirstClickInteraction](interactions-combo.md#firstclickinteraction) - Branch based on tap vs hold input
- [ChargingInteraction](interactions-combo.md#charginginteraction) - Charge-and-release mechanics
- [ChainFlagInteraction](interactions-combo.md#chainflaginteraction) - Set flags for cross-chain communication
- [CancelChainInteraction](interactions-combo.md#cancelchaininteraction) - Reset chain state to beginning

**Combat & Effects** ([interactions-combat.md](interactions-combat.md))
- [SimpleInteraction](interactions-combat.md#simpleinteraction) - Delays, animations, sounds, and flow control
- [Selector](interactions-combat.md#selector) - Target selection for melee attacks (hitboxes)
- [DamageEntity](interactions-combat.md#damageentity) - Deal damage with effects, knockback, and stat grants
- [ApplyForce](interactions-combat.md#applyforce) - Apply physics forces for knockback and launches
- [ApplyEffect](interactions-combat.md#applyeffect) - Apply status effects (buffs, debuffs, DoT)
- [ClearEntityEffect](interactions-combat.md#clearentityeffect) - Remove status effects from entities
- [ChangeStat](interactions-combat.md#changestat) - Modify health, stamina, signature energy
- [InterruptInteraction](interactions-combat.md#interruptinteraction) - Cancel an entity's current interaction chain

**Control Flow** ([interactions-flow.md](interactions-flow.md))
- [Serial](interactions-flow.md#serial) - Execute interactions sequentially
- [Parallel](interactions-flow.md#parallel) - Execute interactions concurrently
- [Condition](interactions-flow.md#condition) - Game mode and movement state branching
- [StatsCondition](interactions-flow.md#statscondition) - Branch based on entity stat values
- [EffectCondition](interactions-flow.md#effectcondition) - Branch based on active status effects
- [BlockCondition](interactions-flow.md#blockcondition) - Branch based on block type/state/tag
- [CooldownCondition](interactions-flow.md#cooldowncondition) - Branch based on cooldown completion
- [MovementCondition](interactions-flow.md#movementcondition) - Direction-based input branching
- [DestroyCondition](interactions-flow.md#destroycondition) - Check if block is destroyable
- [PlacementCountCondition](interactions-flow.md#placementcountcondition) - Branch based on block placement count
- [Repeat](interactions-flow.md#repeat) - Loop execution of interactions
- [Replace](interactions-flow.md#replace) - Variable substitution for templates
- [Target Selectors](interactions-flow.md#target-selectors) - AOE, raycast, and sweep targeting

**Entity & World** ([interactions-world.md](interactions-world.md))
- [SpawnPrefab](interactions-world.md#spawnprefab) - Spawn entities at locations
- [RemoveEntity](interactions-world.md#removeentity) - Despawn entities from the world
- [LaunchProjectile](interactions-world.md#launchprojectile) - Fire projectiles
- [SendMessage](interactions-world.md#sendmessage) - Send chat messages to players
- [RunRootInteraction](interactions-world.md#runrootinteraction) - Dynamically execute another root interaction
- [UI Interactions](interactions-world.md#ui-interactions) - Open UI pages (OpenPage, OpenCustomUI)
- [Inventory Interactions](interactions-world.md#inventory-interactions) - Manage inventory and equipment
- [Block Interactions](interactions-world.md#block-interactions) - Break or place blocks
- [ChangeState](interactions-world.md#changestate) - Change entity state machine state
- [LaunchPadInteraction](interactions-world.md#launchpadinteraction) - Launch pad physics
- [WieldingInteraction](interactions-world.md#wieldinginteraction) - Blocking and guarding mechanics

---

## Quick Start

Interactions are actions entities can perform. Start with `SimpleInteraction` for animations and effects.

### Play an Animation

```json
{
  "Type": "Simple",
  "RunTime": 0.5,
  "Effects": {
    "ItemAnimationId": "SwingDown"
  }
}
```

### Play a Sound Effect

```json
{
  "Type": "Simple",
  "RunTime": 0,
  "Effects": {
    "WorldSoundEventId": "SFX_Light_Melee_T2_Swing"
  }
}
```

### Animation + Sound + Trail

```json
{
  "Type": "Simple",
  "RunTime": 0.177,
  "Effects": {
    "ItemAnimationId": "SwingDown",
    "WorldSoundEventId": "SFX_Light_Melee_T2_Swing",
    "Trails": [{ "TrailId": "Small_Default", "TargetNodeName": "Handle" }]
  }
}
```

### Delay Between Actions

```json
{
  "Type": "Simple",
  "RunTime": 0.2
}
```

### Chain to Another Interaction

```json
{
  "Type": "Simple",
  "RunTime": 0.2,
  "Next": "Sword_Damage_Hit"
}
```

---

## Reference

This section contains technical details, class hierarchies, and complete property tables.

### Class Hierarchy

```
Interaction (abstract)
  implements Operation, JsonAssetWithMap, NetworkSerializable
  └── SimpleInteraction
        └── SimpleInstantInteraction (abstract)
              ├── ProjectileInteraction (see projectiles.md)
              ├── RunRootInteraction
              └── CancelChainInteraction
```

### Interaction Base Class

**Package:** `com.hypixel.hytale.server.core.modules.interaction.interaction.config`

Base class for all interactions. An interaction is an action that an entity can perform (attacks, abilities, item uses, etc.).

**Implements:** `Operation`, `JsonAssetWithMap<String, ...>`, `NetworkSerializable<Interaction>`

### Getting Interactions from Assets

```java
// Get the asset store
AssetStore<String, Interaction, ...> store = Interaction.getAssetStore();

// Get the asset map
IndexedLookupTableAssetMap<String, Interaction> map = Interaction.getAssetMap();

// Get interaction by ID (returns unknown if not found)
Interaction interaction = Interaction.getInteractionOrUnknown("hytale:sword_swing");
int interactionId = Interaction.getInteractionIdOrUnknown("hytale:sword_swing");
```

### Meta Keys

Static meta keys for storing context data during interaction execution:

| Key | Type | Description |
|-----|------|-------------|
| `TARGET_ENTITY` | `Ref<EntityStore>` | Entity being targeted |
| `HIT_LOCATION` | `Vector4d` | Location where hit occurred |
| `HIT_DETAIL` | `String` | Detail about what was hit |
| `TARGET_BLOCK` | `BlockPosition` | Block being targeted |
| `TARGET_BLOCK_RAW` | `BlockPosition` | Raw block position |
| `TARGET_SLOT` | `Integer` | Inventory slot |
| `TIME_SHIFT` | `Float` | Time offset |
| `DAMAGE` | `Damage` | Damage information |

### Key Methods

```java
// Identity
String getId()
boolean isUnknown()

// Configuration
InteractionEffects getEffects()        // Visual/audio effects
float getHorizontalSpeedMultiplier()   // Movement speed during interaction
double getViewDistance()               // View distance modifier
float getRunTime()                     // Duration of interaction
boolean isCancelOnItemChange()         // Cancel when item changes

// Rules and settings
InteractionRules getRules()
Map<GameMode, InteractionSettings> getSettings()

// Execution (called by framework)
void tick(Ref<EntityStore> ref, LivingEntity entity, boolean flag, float time,
          InteractionType type, InteractionContext context, CooldownHandler cooldown)

// Override in subclasses
void compile(OperationsBuilder builder)
boolean walk(Collector collector, InteractionContext context)
boolean needsRemoteSync()

// Network
Interaction toPacket()
```

### Utility Methods

```java
// Check if interaction state indicates failure
static boolean failed(InteractionState state)
```

### SimpleInstantInteraction

**Package:** `com.hypixel.hytale.server.core.modules.interaction.interaction.config`

Abstract base class for instant interactions (no duration, immediate effect).

**Extends:** `SimpleInteraction`

#### Key Methods
```java
// Inherited from SimpleInteraction
WaitForDataFrom getWaitForDataFrom()
boolean needsRemoteSync()
```

#### Codec
```java
public static final BuilderCodec<SimpleInstantInteraction> CODEC;
```

#### Usage
This is the base class for interactions that execute immediately, such as:
- Projectile launches (see `ProjectileInteraction` in projectiles.md)
- Instant abilities
- Quick actions

### InteractionType Enum

**Package:** `com.hypixel.hytale.protocol`

Enum representing the type of interaction trigger.

See [Player Documentation](player.md) for full details.

### Usage Examples

#### Getting an Interaction

```java
// Get interaction from assets
Interaction swordSwing = Interaction.getInteractionOrUnknown("hytale:sword_swing");

if (!swordSwing.isUnknown()) {
    float duration = swordSwing.getRunTime();
    InteractionRules rules = swordSwing.getRules();
}
```

#### Checking Interaction Settings per GameMode

```java
Interaction interaction = Interaction.getInteractionOrUnknown("my_interaction");
Map<GameMode, InteractionSettings> settings = interaction.getSettings();

InteractionSettings survivalSettings = settings.get(GameMode.Survival);
if (survivalSettings != null) {
    // Use survival-specific settings
}
```

#### Using Meta Keys in Custom Interactions

```java
// During interaction execution, access meta data
Ref<EntityStore> target = context.getMeta(Interaction.TARGET_ENTITY);
Vector4d hitLocation = context.getMeta(Interaction.HIT_LOCATION);
Damage damage = context.getMeta(Interaction.DAMAGE);
```

### Root Interaction Configuration

Root interactions are defined in `Server/Item/RootInteractions/` and configure how interactions are triggered:

```json
{
  "RequireNewClick": true,
  "ClickQueuingTimeout": 0.2,
  "Cooldown": { "Cooldown": 0.25 },
  "Interactions": ["Weapon_Sword_Primary"]
}
```

| Property | Type | Description |
|----------|------|-------------|
| `RequireNewClick` | boolean | If true, must click again to chain (holding won't auto-chain) |
| `ClickQueuingTimeout` | float | Buffer window to queue next attack input |
| `Cooldown` | object | Minimum delay between attacks |
| `Interactions` | array | List of interactions to execute |

### Complete Interaction Type Reference

**Class Hierarchy Overview**

```
Interaction (abstract)
├── SimpleInteraction
│   └── SimpleInstantInteraction
└── RootInteraction
```

**Package:** `com.hypixel.hytale.server.core.modules.interaction.interaction.config`
