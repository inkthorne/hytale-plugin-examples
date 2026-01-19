# Combat & Effects Interactions

> Part of the [Interactions API](interactions.md). For base interaction properties, see [Reference](interactions.md#reference).

## Quick Navigation

| Interaction | Description |
|-------------|-------------|
| [SimpleInteraction](#simpleinteraction) | Delays, animations, sounds, and flow control |
| [DamageEntity](#damageentity) | Deal damage with effects, knockback, and stat grants |
| [ApplyForce](#applyforce) | Apply physics forces for knockback and launches |
| [ApplyEffect](#applyeffect) | Apply status effects (buffs, debuffs, DoT) |
| [ClearEntityEffect](#clearentityeffect) | Remove status effects from entities |
| [ChangeStat](#changestat) | Modify health, stamina, signature energy |
| [InterruptInteraction](#interruptinteraction) | Cancel an entity's current interaction chain |

---

## SimpleInteraction

**Package:** `com.hypixel.hytale.server.core.modules.interaction.interaction.config`

**Class hierarchy:** `SimpleInteraction` → `Interaction`

**Protocol class:** `com.hypixel.hytale.protocol.SimpleInteraction` (handles client-server synchronization)

A fundamental building block interaction that does nothing other than provide base interaction features. Despite its simplicity, it's one of the most versatile interaction types, used for delays, triggering animations, playing sounds, and controlling flow between other interactions.

### Purpose

SimpleInteraction serves as:
- **Delay mechanism** - Creates timed pauses between interactions via `RunTime`
- **Animation trigger** - Plays item/player animations via `Effects.ItemAnimationId`
- **Audio controller** - Plays sounds via `Effects.WorldSoundEventId` and `Effects.LocalSoundEventId`
- **Visual effects** - Spawns particles and trails via `Effects.Particles` and `Effects.Trails`
- **Flow control** - Chains interactions via `Next` and handles failures via `Failed`
- **No-op placeholder** - Acts as an empty interaction when no action is needed

### Inherited Properties (from Interaction)

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `RunTime` | float | `0` | Duration in seconds before completing |
| `Effects` | InteractionEffects | - | Visual and audio effects configuration |
| `HorizontalSpeedMultiplier` | float | `1.0` | Movement speed modifier during interaction (0.0-1.0) |
| `ViewDistance` | double | - | View distance modifier |
| `CancelOnItemChange` | boolean | `false` | Cancel if held item changes |
| `Settings` | Map<GameMode, InteractionSettings> | - | Per-gamemode settings |
| `Rules` | InteractionRules | - | Interaction rules |
| `Camera` | InteractionCameraSettings | - | Camera settings during interaction |

### SimpleInteraction-Specific Properties

| Property | Type | Default | Validator | Description |
|----------|------|---------|-----------|-------------|
| `Next` | string/object | - | Late validator (VALIDATOR_CACHE) | Interaction(s) to run when this interaction succeeds |
| `Failed` | string/object | - | Late validator (VALIDATOR_CACHE) | Interaction(s) to run when this interaction fails |

### Effects Configuration

The `Effects` object supports these properties:

| Property | Type | Description |
|----------|------|-------------|
| `ItemAnimationId` | string | Animation to play on the held item |
| `ItemPlayerAnimationsId` | string | Player animation set ID |
| `WorldSoundEventId` | string | Sound audible to all nearby players |
| `LocalSoundEventId` | string | Sound only the executing player hears |
| `OnFinishLocalSoundEventId` | string | Sound played when interaction finishes |
| `ClearAnimationOnFinish` | boolean | Stop animation when interaction ends |
| `ClearSoundEventOnFinish` | boolean | Stop sound when interaction ends |
| `WaitForAnimationToFinish` | boolean | Wait for animation before completing |
| `Particles` | array | Particles attached to model bones |
| `FirstPersonParticles` | array | Particles for first-person view |
| `Trails` | array | Weapon trail effects |
| `CameraEffectId` | string | Camera effect (shake, zoom, etc.) |
| `MovementEffects` | object | Movement modification effects |
| `StartDelay` | float | Delay before effects begin |

### Sounds (World vs Local)

**World sounds** are audible to all nearby players - use for attack impacts, explosions, and actions others should hear:

```json
{
  "Type": "Simple",
  "Effects": {
    "WorldSoundEventId": "SFX_Light_Melee_T2_Swing"
  }
}
```

**Local sounds** are only heard by the executing player - use for UI feedback, personal notifications:

```json
{
  "Type": "Simple",
  "RunTime": 0,
  "Effects": {
    "LocalSoundEventId": "SFX_Consume_Bread_Local",
    "ClearSoundEventOnFinish": true
  }
}
```

### Particles & Trails

**Trail effects** for weapons:

```json
{
  "Type": "Simple",
  "RunTime": 0.177,
  "Effects": {
    "Trails": [
      {
        "PositionOffset": { "X": 0.4, "Y": -0.2, "Z": 0 },
        "RotationOffset": { "Pitch": 0, "Roll": 90, "Yaw": 0 },
        "TargetNodeName": "Handle",
        "TrailId": "Small_Default"
      }
    ],
    "WorldSoundEventId": "SFX_Light_Melee_T2_Swing"
  }
}
```

### WaitForDataFrom Enum

Controls client-server synchronization behavior (accessible via `getWaitForDataFrom()`):

| Value | Description |
|-------|-------------|
| `Client` | Wait for data from the client |
| `Server` | Wait for data from the server |
| `None` | No synchronization needed (default for SimpleInteraction) |

### Key Methods

```java
// Synchronization
WaitForDataFrom getWaitForDataFrom()   // Returns None by default
boolean needsRemoteSync()              // True if Next or Failed need sync

// Execution flow
void compile(OperationsBuilder builder)
boolean walk(Collector collector, InteractionContext context)
```

### Complete Examples

**Basic delay:**

```json
{
  "Type": "Simple",
  "RunTime": 0.2,
  "$Comment": "Delay before next consume cycle can start to prevent sound overlap"
}
```

**Animation trigger with sound:**

```json
{
  "Type": "Simple",
  "RunTime": 0.177,
  "Effects": {
    "ItemAnimationId": "SwingDown",
    "WorldSoundEventId": "SFX_Light_Melee_T2_Swing"
  }
}
```

**Flow control with Next:**

```json
{
  "Type": "Simple",
  "Next": {
    "Type": "UseBlock",
    "Failed": "Block_Attack"
  }
}
```

**Empty no-op (failure handler):**

```json
{
  "Type": "Charging",
  "FailOnDamage": true,
  "Next": { "4.0": "..." },
  "Failed": {
    "Type": "Simple"
  }
}
```

**Prepare delay before combat:**

```json
{
  "Type": "Simple",
  "Effects": {
    "ItemAnimationId": "SwingDown"
  },
  "$Comment": "Prepare Delay",
  "RunTime": 0.244,
  "Next": {
    "Type": "Parallel",
    "Interactions": [
      { "Interactions": ["Axe_Swing_Down_Damage"] },
      { "Interactions": ["Axe_Swing_Down_Effect"] }
    ]
  }
}
```

### Common Patterns

| Pattern | Use Case | Key Properties |
|---------|----------|----------------|
| **Delay** | Pause between chain steps | `RunTime` only |
| **Animation trigger** | Play weapon/item animation | `Effects.ItemAnimationId` |
| **Sound effect** | Audio feedback | `Effects.WorldSoundEventId`, `Effects.LocalSoundEventId` |
| **Visual effect** | Trails, particles | `Effects.Trails`, `Effects.Particles` |
| **Flow control** | Chain to next interaction | `Next` |
| **No-op** | Empty failure handler | Empty `{"Type": "Simple"}` |
| **Prepare phase** | Wind-up before attack | `RunTime` + `Effects.ItemAnimationId` |

### Technical Notes

- **Default behavior** - Without `Next` or `Failed`, the interaction completes immediately after `RunTime` elapses
- **Sync behavior** - `getWaitForDataFrom()` returns `None`, meaning SimpleInteraction doesn't inherently require client-server sync. However, if `Next` or `Failed` reference interactions that need sync, `needsRemoteSync()` returns true.
- **Tick behavior** - On each tick, if state is `Failed` and labels exist, jumps to the failure label (index 0)
- **Protocol** - Serializes `next` and `failed` as integer indices referencing the interaction asset map
- **Inheritance** - `SimpleInstantInteraction` extends this class for instant (no duration) interactions

### Related Interactions

- [Interaction](interactions.md#interaction-base-class) - Base class providing inherited properties
- [Serial](interactions-flow.md#serial) - Often used to chain multiple SimpleInteractions
- [Parallel](interactions-flow.md#parallel) - Execute SimpleInteractions concurrently
- [ChargingInteraction](interactions-combo.md#charginginteraction) - Uses SimpleInteraction for `Failed` handlers

---

## DamageEntity

**Package:** `config/server/DamageEntityInteraction`

The core interaction for dealing damage to entities. Supports damage calculation, effects, knockback, and stat modifications.

### Basic Structure

```json
{
  "Type": "DamageEntity",
  "DamageParameters": {
    "DamageAmount": 10,
    "DamageCauseId": "Physical"
  }
}
```

### Full Structure with All Options

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

### DamageParameters Properties

| Property | Type | Description |
|----------|------|-------------|
| `DamageAmount` | float | Base damage value |
| `DamageCauseId` | string | Damage type (Physical, Magical, Fire, etc.) |
| `DamageCalculator` | object | Optional custom damage calculation |

### DamageEffects Properties

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

### EntityStatsOnHit

Grants stats to the attacker on successful hit:

```json
"EntityStatsOnHit": [
  { "EntityStatId": "SignatureEnergy", "Amount": 1 },
  { "EntityStatId": "Stamina", "Amount": 5 }
]
```

---

## ApplyForce

**Package:** `config/client/ApplyForceInteraction`

Applies physics force to entities, used for knockback, launches, and movement effects.

### Structure

```json
{
  "Type": "ApplyForce",
  "Force": [0, 10, -5],
  "ForceType": "Impulse",
  "RelativeToFacing": true
}
```

### Properties

| Property | Type | Description |
|----------|------|-------------|
| `Force` | [x, y, z] | Force vector to apply |
| `ForceType` | string | `Impulse` (instant) or `Continuous` (over time) |
| `RelativeToFacing` | boolean | If true, force is relative to entity facing direction |
| `Duration` | float | Duration for continuous forces |

### Example: Launch Pad Effect

```json
{
  "Type": "ApplyForce",
  "Force": [0, 25, 0],
  "ForceType": "Impulse",
  "RelativeToFacing": false
}
```

---

## ApplyEffect

**Package:** `config/none/ApplyEffectInteraction`

Applies status effects to entities (buffs, debuffs, damage over time, etc.).

> **See also:** [Effects Reference](effects-stats.md#effects-status-effects) for the complete effect asset JSON structure including stat modifiers, application effects, and damage resistance.

### Structure

```json
{
  "Type": "ApplyEffect",
  "EffectId": "hytale:poison",
  "Duration": 10,
  "Amplifier": 1,
  "Target": "Self"
}
```

### Properties

| Property | Type | Description |
|----------|------|-------------|
| `EffectId` | string | ID of the effect to apply |
| `Duration` | float | Duration in seconds |
| `Amplifier` | int | Effect strength/level (0 = level 1) |
| `Target` | string | `Self`, `Target`, or `AOE` |
| `ShowParticles` | boolean | Whether to show effect particles |

### Example: Apply Burning Effect on Hit

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

## ClearEntityEffect

**Package:** `config/server/ClearEntityEffectInteraction`

Removes status effects from entities.

### Structure

```json
{
  "Type": "ClearEntityEffect",
  "EffectId": "hytale:poison",
  "Target": "Self"
}
```

### Properties

| Property | Type | Description |
|----------|------|-------------|
| `EffectId` | string | Specific effect to remove, or omit for all |
| `Target` | string | `Self` or `Target` |
| `EffectCategory` | string | Remove all effects of a category (optional) |

### Example: Cleanse Potion

```json
{
  "Type": "ClearEntityEffect",
  "EffectCategory": "Debuff",
  "Target": "Self"
}
```

---

## ChangeStat

Modifies entity stats like health, stamina, or signature energy.

> **See also:** [Stat Definitions](effects-stats.md#stat-definitions) for the complete stat asset JSON structure including regeneration rules, conditions, and min/max value effects.

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

## InterruptInteraction

**Package:** `config/server/InterruptInteraction`

Cancels the current interaction chain on the target entity.

### Structure

```json
{
  "Type": "Interrupt",
  "Target": "Target"
}
```

Used for stagger effects, crowd control, or cancelling enemy attacks.
