# Interactions API

> **Note:** For `InteractionManager` (the entity component that manages interaction chains), see [entities.md](entities.md#interactionmanager).

## Class Hierarchy
```
Interaction (abstract)
  implements Operation, JsonAssetWithMap, NetworkSerializable
  └── SimpleInteraction
        └── SimpleInstantInteraction (abstract)
              └── ProjectileInteraction (see projectiles.md)
```

## Interaction
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

---

## SimpleInteraction
**Package:** `com.hypixel.hytale.server.core.modules.interaction.interaction.config`

A basic interaction with synchronization support.

**Extends:** `Interaction`

### Key Methods
```java
// Data synchronization
WaitForDataFrom getWaitForDataFrom()   // Client/Server/None
boolean needsRemoteSync()              // Whether to sync across network

// Execution
void compile(OperationsBuilder builder)
boolean walk(Collector collector, InteractionContext context)
```

### Codec
```java
public static final BuilderCodec<SimpleInteraction> CODEC;
```

---

## SimpleInstantInteraction
**Package:** `com.hypixel.hytale.server.core.modules.interaction.interaction.config`

Abstract base class for instant interactions (no duration, immediate effect).

**Extends:** `SimpleInteraction`

### Key Methods
```java
// Inherited from SimpleInteraction
WaitForDataFrom getWaitForDataFrom()
boolean needsRemoteSync()
```

### Codec
```java
public static final BuilderCodec<SimpleInstantInteraction> CODEC;
```

### Usage
This is the base class for interactions that execute immediately, such as:
- Projectile launches (see `ProjectileInteraction` in projectiles.md)
- Instant abilities
- Quick actions

---

## InteractionType
**Package:** `com.hypixel.hytale.protocol`

Enum representing the type of interaction trigger.

See [Player Documentation](player.md) for full details.

---

## Usage Examples

### Getting an Interaction
```java
// Get interaction from assets
Interaction swordSwing = Interaction.getInteractionOrUnknown("hytale:sword_swing");

if (!swordSwing.isUnknown()) {
    float duration = swordSwing.getRunTime();
    InteractionRules rules = swordSwing.getRules();
}
```

### Checking Interaction Settings per GameMode
```java
Interaction interaction = Interaction.getInteractionOrUnknown("my_interaction");
Map<GameMode, InteractionSettings> settings = interaction.getSettings();

InteractionSettings survivalSettings = settings.get(GameMode.Survival);
if (survivalSettings != null) {
    // Use survival-specific settings
}
```

### Using Meta Keys in Custom Interactions
```java
// During interaction execution, access meta data
Ref<EntityStore> target = context.getMeta(Interaction.TARGET_ENTITY);
Vector4d hitLocation = context.getMeta(Interaction.HIT_LOCATION);
Damage damage = context.getMeta(Interaction.DAMAGE);
```

---

## Attack Chain Timing

Attack chains allow sequential attacks to flow together as combos. The timing between attacks is controlled by properties in the interaction configuration files.

### Key Properties

| Property | Location | Purpose |
|----------|----------|---------|
| `ChainingAllowance` | Chain JSON files | Time window (seconds) before chain resets |
| `Cooldown` | Root interaction files | Minimum time between attacks |
| `ClickQueuingTimeout` | Root interaction files | Input buffer for queuing next attack |

### ChainingAllowance

Defines how long (in seconds) the player has to execute the next attack before the chain breaks and resets.

**File location:** `Server/Item/Interactions/Weapons/{WeaponType}/Primary/*_Chain.json`

**Example:**
```json
{
  "Type": "Chaining",
  "ChainingAllowance": 2,
  "ChainId": "Sword_Swings",
  "Next": [...]
}
```

**Values by weapon:**
| Weapon | ChainingAllowance |
|--------|-------------------|
| Sword | 2.0s |
| Battleaxe | 2.9s |
| Daggers | 1.2s |

### Cooldown & Click Queuing

Configured in Root Interaction files: `Server/Item/RootInteractions/Weapons/{WeaponType}/`

```json
{
  "RequireNewClick": true,
  "ClickQueuingTimeout": 0.2,
  "Cooldown": { "Cooldown": 0.25 },
  "Interactions": ["Weapon_Sword_Primary"]
}
```

- **Cooldown**: Minimum delay between attacks (prevents spam)
- **ClickQueuingTimeout**: Buffer window to queue next attack input
- **RequireNewClick**: If true, must click again to chain (holding won't auto-chain)

---

## Wielding Interactions (Blocking/Guarding)

The `Wielding` interaction type enables blocking and guarding mechanics for shields and weapons.

**File locations:** `Server/Item/Interactions/Weapons/{WeaponType}/Secondary/Guard/*_Guard_Wield.json`

### Basic Structure

```json
{
  "Type": "Wielding",
  "AngledWielding": {
    "Angle": 0,
    "AngleDistance": 90,
    "DamageModifiers": { "Physical": 0 }
  },
  "StaminaCost": {
    "CostType": "Damage",
    "Cost": 0.5
  },
  "BlockedEffects": {
    "WorldSoundEventId": "SFX_Shield_T2_Impact",
    "WorldParticles": [{ "SystemId": "Shield_Block" }]
  },
  "BlockedInteractions": {
    "Interactions": [...]
  },
  "Failed": {
    "Interactions": [...]
  }
}
```

### Key Properties

| Property | Description |
|----------|-------------|
| `AngledWielding` | Configures angle-based damage reduction |
| `DamageModifiers` | Reduce incoming damage by type (0 = full block, 0.5 = 50% reduction) |
| `StaminaCost` | Stamina consumption when blocking |
| `BlockedEffects` | Visual/audio effects when block succeeds |
| `BlockedInteractions` | Interactions to trigger on successful block |
| `Failed` | What happens when guard breaks (stamina depleted) |

### AngledWielding

Controls directional blocking based on attack angle:

```json
"AngledWielding": {
  "Angle": 0,
  "AngleDistance": 90,
  "DamageModifiers": { "Physical": 0, "Magical": 0.5 }
}
```

- **Angle**: Center angle of the blocking arc (0 = forward)
- **AngleDistance**: Half-width of the blocking arc in degrees
- **DamageModifiers**: Multipliers per damage type (0 = full block, 1 = no reduction)

### StaminaCost

Stamina consumption can be based on damage blocked:

```json
"StaminaCost": {
  "CostType": "Damage",
  "Cost": 0.5
}
```

- **CostType**: `"Damage"` = cost per point of damage blocked
- **Cost**: Multiplier for stamina drain

### BlockedEffects

Effects triggered on successful block (sounds, particles, camera effects):

```json
"BlockedEffects": {
  "WorldSoundEventId": "SFX_Shield_T2_Impact",
  "WorldParticles": [
    { "SystemId": "Shield_Block" }
  ]
}
```

### BlockedInteractions

Trigger additional interactions when a block succeeds. This enables mechanics like granting stats on successful blocks:

```json
"BlockedInteractions": {
  "Interactions": [
    {
      "Type": "ChangeStat",
      "StatModifiers": {
        "SignatureEnergy": 5
      }
    },
    {
      "Type": "ApplyForce",
      "Force": [0, 5, -10]
    }
  ]
}
```

### Failed (Guard Break)

Interactions triggered when stamina is depleted during a block:

```json
"Failed": {
  "Interactions": [
    { "Type": "Stagger" }
  ]
}
```

---

## ChangeStat Interaction

The `ChangeStat` interaction modifies entity stats like health, stamina, or signature energy.

**Example locations:**
- `Server/Entity/Effects/Potion/*_Regen.json`
- Used in `BlockedInteractions` for granting stats on block

### Basic Structure

```json
{
  "Type": "ChangeStat",
  "StatModifiers": {
    "SignatureEnergy": 5,
    "Stamina": 10
  }
}
```

### StatModifiers

A map of stat names to modification values:

```json
"StatModifiers": {
  "SignatureEnergy": 5,
  "Stamina": 10,
  "Health": -5
}
```

**Available stats:**
- `SignatureEnergy` - Ultimate/signature ability resource
- `Stamina` - Used for blocking, sprinting, etc.
- `Health` - Entity health
- `Mana` - Magic resource (if applicable)

### Behaviour Options

Control how the stat is modified:

```json
{
  "Type": "ChangeStat",
  "StatModifiers": {
    "Health": 50
  },
  "Behaviour": "Set"
}
```

| Behaviour | Description |
|-----------|-------------|
| `Add` | Add value to current stat (default) |
| `Set` | Set stat to exact value |

### ValueType Options

Control whether the value is absolute or percentage-based:

```json
{
  "Type": "ChangeStat",
  "StatModifiers": {
    "Health": 25
  },
  "ValueType": "Percent"
}
```

| ValueType | Description |
|-----------|-------------|
| (default) | Absolute value |
| `Percent` | Percentage of max stat |

### Example: Grant Signature Energy on Block

Combine `Wielding` with `BlockedInteractions` and `ChangeStat`:

```json
{
  "Type": "Wielding",
  "BlockedInteractions": {
    "Interactions": [
      {
        "Type": "ChangeStat",
        "StatModifiers": {
          "SignatureEnergy": 5
        }
      }
    ]
  },
  "AngledWielding": {
    "Angle": 0,
    "AngleDistance": 90,
    "DamageModifiers": { "Physical": 0 }
  },
  "BlockedEffects": {
    "WorldSoundEventId": "SFX_Shield_T2_Impact"
  }
}
```

This grants 5 signature energy each time the player successfully blocks an attack.

---

## Complete Interaction Type Reference

This section documents all interaction types available in the Hytale interaction system, organized by category.

### Class Hierarchy Overview

```
Interaction (abstract)
├── SimpleInteraction
│   └── SimpleInstantInteraction
└── RootInteraction
```

**Package:** `com.hypixel.hytale.server.core.modules.interaction.interaction.config`

---

## Combat Interactions

### DamageEntity

**Package:** `config/server/DamageEntityInteraction`

The core interaction for dealing damage to entities. Supports damage calculation, effects, knockback, and stat modifications.

#### Basic Structure

```json
{
  "Type": "DamageEntity",
  "DamageParameters": {
    "DamageAmount": 10,
    "DamageCauseId": "Physical"
  }
}
```

#### Full Structure with All Options

```json
{
  "Type": "DamageEntity",
  "DamageParameters": {
    "DamageAmount": 10,
    "DamageCauseId": "Physical",
    "DamageCalculator": {
      "Type": "Standard",
      "BaseDamage": 10,
      "CriticalMultiplier": 1.5,
      "CriticalChance": 0.1
    }
  },
  "EntityStatsOnHit": [
    { "EntityStatId": "SignatureEnergy", "Amount": 1 }
  ],
  "DamageEffects": {
    "ModelParticles": [
      { "SystemId": "Blood_Splatter", "Bone": "Chest" }
    ],
    "WorldParticles": [
      { "SystemId": "Impact_Sparks" }
    ],
    "LocalSoundEventId": "SFX_Hit_Flesh",
    "WorldSoundEventId": "SFX_Impact_Metal",
    "PlayerSoundEventId": "SFX_Player_Hit",
    "CameraEffectId": "Camera_Shake_Light",
    "StaminaDrainMultiplier": 1.0,
    "Knockback": {
      "Force": 0.5,
      "RelativeX": -5,
      "RelativeZ": -5,
      "VelocityY": 5
    }
  },
  "TargetSelector": {
    "Type": "AOECircle",
    "Radius": 3.0
  }
}
```

#### DamageParameters Properties

| Property | Type | Description |
|----------|------|-------------|
| `DamageAmount` | float | Base damage value |
| `DamageCauseId` | string | Damage type (Physical, Magical, Fire, etc.) |
| `DamageCalculator` | object | Optional custom damage calculation |

#### DamageEffects Properties

| Property | Type | Description |
|----------|------|-------------|
| `ModelParticles` | array | Particles attached to hit entity's model bones |
| `WorldParticles` | array | Particles spawned at hit location in world space |
| `LocalSoundEventId` | string | Sound played for the attacker only |
| `WorldSoundEventId` | string | Sound played at hit location for all nearby |
| `PlayerSoundEventId` | string | Sound played for hit player specifically |
| `CameraEffectId` | string | Camera shake or effect on hit |
| `StaminaDrainMultiplier` | float | Multiplier for stamina drain on hit |
| `Knockback` | object | Knockback configuration (see [Knockback System](combat.md#knockback-system)) |

#### EntityStatsOnHit

Grants stats to the attacker on successful hit:

```json
"EntityStatsOnHit": [
  { "EntityStatId": "SignatureEnergy", "Amount": 1 },
  { "EntityStatId": "Stamina", "Amount": 5 }
]
```

---

### ApplyForce

**Package:** `config/client/ApplyForceInteraction`

Applies physics force to entities, used for knockback, launches, and movement effects.

#### Structure

```json
{
  "Type": "ApplyForce",
  "Force": [0, 10, -5],
  "ForceType": "Impulse",
  "RelativeToFacing": true
}
```

#### Properties

| Property | Type | Description |
|----------|------|-------------|
| `Force` | [x, y, z] | Force vector to apply |
| `ForceType` | string | `Impulse` (instant) or `Continuous` (over time) |
| `RelativeToFacing` | boolean | If true, force is relative to entity facing direction |
| `Duration` | float | Duration for continuous forces |

#### Example: Launch Pad Effect

```json
{
  "Type": "ApplyForce",
  "Force": [0, 25, 0],
  "ForceType": "Impulse",
  "RelativeToFacing": false
}
```

---

### ApplyEffect

**Package:** `config/none/ApplyEffectInteraction`

Applies status effects to entities (buffs, debuffs, damage over time, etc.).

#### Structure

```json
{
  "Type": "ApplyEffect",
  "EffectId": "hytale:poison",
  "Duration": 10,
  "Amplifier": 1,
  "Target": "Self"
}
```

#### Properties

| Property | Type | Description |
|----------|------|-------------|
| `EffectId` | string | ID of the effect to apply |
| `Duration` | float | Duration in seconds |
| `Amplifier` | int | Effect strength/level (0 = level 1) |
| `Target` | string | `Self`, `Target`, or `AOE` |
| `ShowParticles` | boolean | Whether to show effect particles |

#### Example: Apply Burning Effect on Hit

```json
{
  "Type": "Serial",
  "Interactions": [
    {
      "Type": "DamageEntity",
      "DamageParameters": {
        "DamageAmount": 5,
        "DamageCauseId": "Fire"
      }
    },
    {
      "Type": "ApplyEffect",
      "EffectId": "hytale:burning",
      "Duration": 5,
      "Amplifier": 0,
      "Target": "Target"
    }
  ]
}
```

---

### ClearEntityEffect

**Package:** `config/server/ClearEntityEffectInteraction`

Removes status effects from entities.

#### Structure

```json
{
  "Type": "ClearEntityEffect",
  "EffectId": "hytale:poison",
  "Target": "Self"
}
```

#### Properties

| Property | Type | Description |
|----------|------|-------------|
| `EffectId` | string | Specific effect to remove, or omit for all |
| `Target` | string | `Self` or `Target` |
| `EffectCategory` | string | Remove all effects of a category (optional) |

#### Example: Cleanse Potion

```json
{
  "Type": "ClearEntityEffect",
  "EffectCategory": "Debuff",
  "Target": "Self"
}
```

---

### InterruptInteraction

**Package:** `config/server/InterruptInteraction`

Cancels the current interaction chain on the target entity.

#### Structure

```json
{
  "Type": "Interrupt",
  "Target": "Target"
}
```

Used for stagger effects, crowd control, or cancelling enemy attacks.

---

## Control Flow Interactions

These interactions control the execution order and branching of interaction chains.

### Serial

**Package:** `config/none/SerialInteraction`

Executes interactions sequentially, one after another.

#### Structure

```json
{
  "Type": "Serial",
  "Interactions": [
    { "Type": "DamageEntity", "DamageParameters": { "DamageAmount": 5 } },
    { "Type": "ApplyEffect", "EffectId": "hytale:slow", "Duration": 3 }
  ]
}
```

Each interaction completes before the next begins.

---

### Parallel

**Package:** `config/none/ParallelInteraction`

Executes multiple interactions concurrently.

#### Structure

```json
{
  "Type": "Parallel",
  "Interactions": [
    { "Type": "ApplyEffect", "EffectId": "hytale:burning", "Duration": 5 },
    { "Type": "ApplyEffect", "EffectId": "hytale:slow", "Duration": 5 }
  ]
}
```

All interactions start simultaneously.

---

### Condition

**Package:** `config/none/ConditionInteraction`

Conditional branching based on various conditions.

#### Structure

```json
{
  "Type": "Condition",
  "Condition": {
    "Type": "HasEffect",
    "EffectId": "hytale:burning"
  },
  "Then": {
    "Type": "DamageEntity",
    "DamageParameters": { "DamageAmount": 20, "DamageCauseId": "Fire" }
  },
  "Else": {
    "Type": "DamageEntity",
    "DamageParameters": { "DamageAmount": 10, "DamageCauseId": "Fire" }
  }
}
```

#### Condition Types

| Type | Description | Parameters |
|------|-------------|------------|
| `HasEffect` | Check if entity has status effect | `EffectId` |
| `IsBlocking` | Check if entity is blocking | - |
| `IsOnGround` | Check if entity is grounded | - |
| `HasItem` | Check if entity has item | `ItemId` |
| `Random` | Random chance | `Chance` (0-1) |

---

### StatsCondition

**Package:** `config/none/StatsConditionInteraction`

Branch based on entity stat values.

#### Structure

```json
{
  "Type": "StatsCondition",
  "Stat": "Health",
  "Operator": "LessThan",
  "Value": 50,
  "ValueType": "Percent",
  "Then": {
    "Type": "DamageEntity",
    "DamageParameters": { "DamageAmount": 30 }
  },
  "Else": {
    "Type": "DamageEntity",
    "DamageParameters": { "DamageAmount": 10 }
  }
}
```

#### Properties

| Property | Type | Description |
|----------|------|-------------|
| `Stat` | string | Stat to check (Health, Stamina, SignatureEnergy) |
| `Operator` | string | `LessThan`, `GreaterThan`, `Equals`, `LessOrEqual`, `GreaterOrEqual` |
| `Value` | float | Value to compare against |
| `ValueType` | string | `Absolute` or `Percent` (of max) |

#### Example: Execute Low Health Enemies

```json
{
  "Type": "StatsCondition",
  "Stat": "Health",
  "Operator": "LessThan",
  "Value": 25,
  "ValueType": "Percent",
  "Then": {
    "Type": "Serial",
    "Interactions": [
      {
        "Type": "DamageEntity",
        "DamageParameters": { "DamageAmount": 999, "DamageCauseId": "Physical" }
      },
      {
        "Type": "SendMessage",
        "Message": "Executed!"
      }
    ]
  }
}
```

---

### EffectCondition

**Package:** `config/none/EffectConditionInteraction`

Branch based on active status effects.

#### Structure

```json
{
  "Type": "EffectCondition",
  "EffectId": "hytale:burning",
  "Target": "Target",
  "Then": {
    "Type": "DamageEntity",
    "DamageParameters": { "DamageAmount": 20, "DamageCauseId": "Fire" }
  },
  "Else": {
    "Type": "ApplyEffect",
    "EffectId": "hytale:burning",
    "Duration": 5
  }
}
```

---

### Repeat

**Package:** `config/none/RepeatInteraction`

Loop execution of interactions.

#### Structure

```json
{
  "Type": "Repeat",
  "Count": 3,
  "Interval": 0.5,
  "Interaction": {
    "Type": "DamageEntity",
    "DamageParameters": { "DamageAmount": 5 }
  }
}
```

#### Properties

| Property | Type | Description |
|----------|------|-------------|
| `Count` | int | Number of repetitions |
| `Interval` | float | Delay between repetitions (seconds) |
| `Interaction` | object | Interaction to repeat |

---

### Select

**Package:** `config/none/SelectInteraction`

Random selection from multiple interactions.

#### Structure

```json
{
  "Type": "Select",
  "Interactions": [
    {
      "Weight": 1,
      "Interaction": { "Type": "ApplyEffect", "EffectId": "hytale:burning" }
    },
    {
      "Weight": 1,
      "Interaction": { "Type": "ApplyEffect", "EffectId": "hytale:frozen" }
    },
    {
      "Weight": 2,
      "Interaction": { "Type": "ApplyEffect", "EffectId": "hytale:poison" }
    }
  ]
}
```

#### Properties

| Property | Type | Description |
|----------|------|-------------|
| `Weight` | float | Selection weight (higher = more likely) |
| `Interaction` | object | Interaction to execute if selected |

In this example, poison has twice the chance of being selected.

---

## Entity Interactions

### SpawnPrefab

**Package:** `config/server/SpawnPrefabInteraction`

Spawns entities at specified locations.

#### Structure

```json
{
  "Type": "SpawnPrefab",
  "PrefabId": "hytale:skeleton",
  "Position": "Target",
  "Count": 1,
  "Offset": [0, 0, 0]
}
```

#### Properties

| Property | Type | Description |
|----------|------|-------------|
| `PrefabId` | string | Entity prefab ID to spawn |
| `Position` | string | `Self`, `Target`, or `HitLocation` |
| `Count` | int | Number of entities to spawn |
| `Offset` | [x, y, z] | Position offset from spawn point |
| `SpawnVelocity` | [x, y, z] | Initial velocity for spawned entity |
| `InheritVelocity` | boolean | Inherit spawner's velocity |

#### Example: Summon Minions on Ability

```json
{
  "Type": "SpawnPrefab",
  "PrefabId": "hytale:minion_skeleton",
  "Position": "Self",
  "Count": 3,
  "Offset": [0, 0, 2]
}
```

---

### RemoveEntity

**Package:** `config/none/RemoveEntityInteraction`

Despawns/removes entities from the world.

#### Structure

```json
{
  "Type": "RemoveEntity",
  "Target": "Target",
  "Delay": 0
}
```

#### Properties

| Property | Type | Description |
|----------|------|-------------|
| `Target` | string | `Self` or `Target` |
| `Delay` | float | Delay before removal (seconds) |

---

### LaunchProjectile

**Package:** `config/server/LaunchProjectileInteraction`

Fires projectiles from an entity.

#### Structure

```json
{
  "Type": "LaunchProjectile",
  "ProjectileId": "hytale:arrow",
  "Speed": 50,
  "SpawnOffset": [0, 1.5, 0.5],
  "AimType": "Forward"
}
```

#### Properties

| Property | Type | Description |
|----------|------|-------------|
| `ProjectileId` | string | Projectile prefab ID |
| `Speed` | float | Initial projectile speed |
| `SpawnOffset` | [x, y, z] | Offset from entity position |
| `AimType` | string | `Forward`, `AtTarget`, `AtCursor` |
| `Spread` | float | Random spread angle (degrees) |
| `Count` | int | Number of projectiles |
| `Gravity` | float | Gravity multiplier |

See [projectiles.md](projectiles.md) for more projectile details.

---

### SendMessage

**Package:** `config/none/SendMessageInteraction`

Sends chat messages to players.

#### Structure

```json
{
  "Type": "SendMessage",
  "Message": "Critical Hit!",
  "Target": "Self",
  "MessageType": "ActionBar"
}
```

#### Properties

| Property | Type | Description |
|----------|------|-------------|
| `Message` | string | Text to display |
| `Target` | string | `Self`, `Target`, or `All` |
| `MessageType` | string | `Chat`, `ActionBar`, or `Title` |

---

## Target Selectors

Target selectors determine which entities are affected by an interaction. Used in `DamageEntity` and other targeting interactions.

### AOECircleSelector

Selects entities within a circular area on the horizontal plane.

```json
"TargetSelector": {
  "Type": "AOECircle",
  "Radius": 5.0,
  "Center": "Self",
  "MaxTargets": 10,
  "IncludeSelf": false
}
```

| Property | Type | Description |
|----------|------|-------------|
| `Radius` | float | Circle radius |
| `Center` | string | `Self`, `Target`, or `HitLocation` |
| `MaxTargets` | int | Maximum entities to select |
| `IncludeSelf` | boolean | Whether to include the caster |

---

### AOECylinderSelector

Selects entities within a cylindrical volume.

```json
"TargetSelector": {
  "Type": "AOECylinder",
  "Radius": 3.0,
  "Height": 4.0,
  "Center": "Self"
}
```

| Property | Type | Description |
|----------|------|-------------|
| `Radius` | float | Cylinder radius |
| `Height` | float | Cylinder height |
| `Center` | string | Center position reference |

---

### RaycastSelector

Selects entities along a line trace.

```json
"TargetSelector": {
  "Type": "Raycast",
  "Range": 20.0,
  "Width": 0.5,
  "Pierce": false
}
```

| Property | Type | Description |
|----------|------|-------------|
| `Range` | float | Maximum raycast distance |
| `Width` | float | Ray thickness (0 = line) |
| `Pierce` | boolean | Continue through entities |

---

### StabSelector

Selects entities in a stab attack shape (narrow cone).

```json
"TargetSelector": {
  "Type": "Stab",
  "Range": 2.5,
  "Width": 0.8
}
```

Used for thrusting weapons like spears and rapiers.

---

### HorizontalSelector

Selects entities in a horizontal sweep arc.

```json
"TargetSelector": {
  "Type": "Horizontal",
  "Range": 3.0,
  "Angle": 120,
  "Height": 2.0
}
```

| Property | Type | Description |
|----------|------|-------------|
| `Range` | float | Sweep distance |
| `Angle` | float | Arc width in degrees |
| `Height` | float | Vertical extent |

Used for slashing weapons like swords and axes.

---

## UI Interactions

### OpenPage

**Package:** `config/server/OpenPageInteraction`

Opens a built-in UI page.

```json
{
  "Type": "OpenPage",
  "PageId": "hytale:inventory"
}
```

---

### OpenCustomUI

**Package:** `config/server/OpenCustomUIInteraction`

Opens a custom UI page.

```json
{
  "Type": "OpenCustomUI",
  "PageId": "myplugin:custom_menu"
}
```

See [ui.md](ui.md) for custom UI details.

---

## Inventory Interactions

### EquipItem

**Package:** `config/server/EquipItemInteraction`

Equips an item to an equipment slot.

```json
{
  "Type": "EquipItem",
  "Slot": "MainHand",
  "ItemId": "hytale:iron_sword"
}
```

---

### ModifyInventory

**Package:** `config/server/ModifyInventoryInteraction`

Adds or removes items from inventory.

```json
{
  "Type": "ModifyInventory",
  "Operation": "Add",
  "ItemId": "hytale:gold_coin",
  "Count": 10
}
```

| Property | Type | Description |
|----------|------|-------------|
| `Operation` | string | `Add`, `Remove`, or `Set` |
| `ItemId` | string | Item ID |
| `Count` | int | Item count |
| `Slot` | int | Specific slot (optional) |

---

## Block Interactions

### BreakBlock

**Package:** `config/client/BreakBlockInteraction`

Breaks a block at the target location.

```json
{
  "Type": "BreakBlock",
  "Target": "TargetBlock",
  "DropItems": true
}
```

---

### PlaceBlock

**Package:** `config/client/PlaceBlockInteraction`

Places a block at the target location.

```json
{
  "Type": "PlaceBlock",
  "Target": "TargetBlock",
  "BlockId": "hytale:stone"
}
```

---

## State Interactions

### ChangeState

**Package:** `config/client/ChangeStateInteraction`

Changes an entity's state machine state.

```json
{
  "Type": "ChangeState",
  "StateId": "Stunned",
  "Duration": 2.0
}
```

Used for stagger, stun, and other state-based effects.

---

### ChainingInteraction

**Package:** `config/client/ChainingInteraction`

Enables combo attack chains. See [Attack Chain Timing](#attack-chain-timing) above for details.

```json
{
  "Type": "Chaining",
  "ChainingAllowance": 2,
  "ChainId": "Sword_Swings",
  "Next": [...]
}
```

---

### ChargingInteraction

**Package:** `config/client/ChargingInteraction`

Enables charged attacks that scale with hold duration.

```json
{
  "Type": "Charging",
  "MinChargeTime": 0.5,
  "MaxChargeTime": 2.0,
  "ChargedInteraction": {
    "Type": "DamageEntity",
    "DamageParameters": {
      "DamageAmount": 30
    }
  },
  "PartialInteraction": {
    "Type": "DamageEntity",
    "DamageParameters": {
      "DamageAmount": 10
    }
  }
}
```

| Property | Type | Description |
|----------|------|-------------|
| `MinChargeTime` | float | Minimum charge for partial effect |
| `MaxChargeTime` | float | Time to reach full charge |
| `ChargedInteraction` | object | Interaction at full charge |
| `PartialInteraction` | object | Interaction if released early |

---

## Physics Interactions

### LaunchPadInteraction

**Package:** `config/server/LaunchPadInteraction`

Specialized launch pad physics for bouncing entities.

```json
{
  "Type": "LaunchPad",
  "LaunchVelocity": [0, 20, 0],
  "PreserveHorizontal": true
}
```
