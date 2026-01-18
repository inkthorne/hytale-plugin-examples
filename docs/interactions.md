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
