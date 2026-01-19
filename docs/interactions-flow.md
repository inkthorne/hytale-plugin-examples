# Control Flow Interactions

> Part of the [Interactions API](interactions.md). For base interaction properties, see [Reference](interactions.md#reference).

## Quick Navigation

| Interaction | Description |
|-------------|-------------|
| [Serial](#serial) | Execute interactions sequentially |
| [Parallel](#parallel) | Execute interactions concurrently |
| [Condition](#condition) | Conditional branching |
| [StatsCondition](#statscondition) | Branch based on entity stat values |
| [EffectCondition](#effectcondition) | Branch based on active status effects |
| [Repeat](#repeat) | Loop execution of interactions |
| [Select](#select) | Random weighted selection |
| [Replace](#replace) | Variable substitution for templates |
| [Target Selectors](#target-selectors) | AOE, raycast, and sweep targeting |

---

## Serial

**Package:** `config/none/SerialInteraction`

Executes multiple interactions sequentially, one after another. Each interaction in the sequence must complete before the next one begins. This is the fundamental building block for multi-step abilities, consumables, combo finishers, and any interaction that requires ordered execution of multiple effects.

### Core Properties

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `Type` | string | Required | Always `"Serial"` |
| `Interactions` | array | Required | List of interactions to execute in order |

### Interactions Array Format

The `Interactions` property accepts an array where each entry can be:

1. **Inline interaction object** - Full interaction definition
2. **String reference** - Path to another interaction file
3. **Mixed format** - Combination of both

**Inline interaction objects:**

```json
{
  "Type": "Serial",
  "Interactions": [
    { "Type": "DamageEntity", "DamageParameters": { "DamageAmount": 5 } },
    { "Type": "ApplyEffect", "EffectId": "hytale:slow", "Duration": 3 }
  ]
}
```

**String references:**

```json
{
  "Type": "Serial",
  "Interactions": [
    "Sword_Damage_Light",
    "Sword_Sound_Hit",
    "Sword_Particles_Slash"
  ]
}
```

**Mixed format:**

```json
{
  "Type": "Serial",
  "Interactions": [
    "Prepare_Animation",
    { "Type": "DamageEntity", "DamageParameters": { "DamageAmount": 10 } },
    "Cleanup_Effects"
  ]
}
```

### Execution Behavior

Serial interactions execute **synchronously in order**. Each interaction must fully complete before the next one begins. This differs from [Parallel](#parallel) which starts all interactions simultaneously.

**Execution flow:**

```
Serial Start
    │
    ▼
┌─────────────────┐
│ Interaction 1   │──► Wait for completion
└─────────────────┘
    │
    ▼
┌─────────────────┐
│ Interaction 2   │──► Wait for completion
└─────────────────┘
    │
    ▼
┌─────────────────┐
│ Interaction 3   │──► Wait for completion
└─────────────────┘
    │
    ▼
Serial Complete
```

**Important timing considerations:**

- Interactions with `RunTime` will block until that duration completes
- Instant interactions (like stat changes) complete immediately
- Nested Serial blocks execute their full sequence before continuing
- If any interaction fails, subsequent interactions may still execute (no short-circuit)

### Deep Nesting Patterns

Serial interactions can be nested within other control flow structures for complex multi-step behaviors.

**Serial inside `Next` blocks (Charging):**

```json
{
  "Type": "Charging",
  "FailOnDamage": true,
  "Next": {
    "2.0": {
      "Type": "Serial",
      "Interactions": [
        { "Type": "ModifyInventory", "AdjustHeldItemQuantity": -1 },
        { "Type": "ApplyEffect", "EffectId": "hytale:regeneration", "Duration": 30 }
      ]
    }
  }
}
```

**Serial inside `Then`/`Else` blocks (Condition):**

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
      { "Type": "DamageEntity", "DamageParameters": { "DamageAmount": 999 } },
      { "Type": "SendMessage", "Message": "Executed!" }
    ]
  }
}
```

**Serial inside `Failed` blocks (Charging):**

```json
{
  "Type": "Charging",
  "FailOnDamage": true,
  "Failed": {
    "Type": "Serial",
    "Interactions": [
      { "Type": "PlaySound", "SoundId": "action_canceled" },
      { "Type": "ClearItemAnimation" }
    ]
  },
  "Next": { "1.0": "Consume_Complete" }
}
```

**Serial inside Serial (deeply nested):**

```json
{
  "Type": "Serial",
  "Interactions": [
    {
      "Type": "Serial",
      "Interactions": [
        "Prepare_Phase_1",
        "Execute_Phase_1"
      ]
    },
    {
      "Type": "Serial",
      "Interactions": [
        "Prepare_Phase_2",
        "Execute_Phase_2"
      ]
    }
  ]
}
```

### Complete Examples

**Dodge Mechanic (from Dodge_Left.json):**

A dodge combines movement, animation, effects, and stat changes in sequence:

```json
{
  "Type": "Serial",
  "Interactions": [
    {
      "Type": "Simple",
      "RunTime": 0.4,
      "Effects": {
        "AnimationTreeParameter": {
          "ParameterId": "DodgeDirection",
          "Value": "Left"
        },
        "TriggerAnimation": "Dodge",
        "LocalSoundEventId": "hytale:sounds/player/dodge_whoosh"
      },
      "Next": {
        "Type": "ApplyMovementImpulse",
        "Direction": "Left",
        "Force": 8.0
      }
    },
    {
      "Type": "ApplyEffect",
      "EffectId": "hytale:invulnerable",
      "Duration": 0.3
    },
    {
      "Type": "ModifyStat",
      "Stat": "Stamina",
      "Amount": -15
    }
  ]
}
```

**Double Jump (from Double_Jump.json):**

Sequential checks and actions for an aerial ability:

```json
{
  "Type": "Serial",
  "Interactions": [
    {
      "Type": "StatsCondition",
      "Stat": "Jumps",
      "Operator": "GreaterThan",
      "Value": 0,
      "Then": {
        "Type": "Serial",
        "Interactions": [
          {
            "Type": "ApplyMovementImpulse",
            "Direction": "Up",
            "Force": 6.5
          },
          {
            "Type": "ModifyStat",
            "Stat": "Jumps",
            "Amount": -1
          },
          {
            "Type": "Simple",
            "Effects": {
              "TriggerAnimation": "DoubleJump",
              "WorldSoundEventId": "hytale:sounds/player/jump"
            }
          }
        ]
      }
    }
  ]
}
```

**Consumable with Charge (from Consume_Charge.json):**

A consumable that requires holding, then executes multiple effects:

```json
{
  "Type": "Serial",
  "Interactions": [
    {
      "Type": "Charging",
      "FailOnDamage": true,
      "HorizontalSpeedMultiplier": 0.3,
      "DisplayProgress": true,
      "Effects": {
        "ItemAnimationId": "Consume"
      },
      "Next": {
        "0": {
          "Type": "Serial",
          "Interactions": [
            { "Type": "ClearItemAnimation" }
          ]
        },
        "2.0": {
          "Type": "Serial",
          "Interactions": [
            { "Type": "ModifyInventory", "AdjustHeldItemQuantity": -1 },
            { "Type": "ApplyEffect", "EffectId": "hytale:satiated", "Duration": 120 },
            { "Type": "ModifyStat", "Stat": "Health", "Amount": 20 },
            { "Type": "PlaySound", "SoundId": "hytale:sounds/player/eat_finish" }
          ]
        }
      }
    }
  ]
}
```

**Signature Ability (from Weapon_Sword_Signature_Vortexstrike.json):**

A powerful ability with stamina cost, animation, damage, and cleanup:

```json
{
  "Type": "Serial",
  "Interactions": [
    {
      "Type": "StatsCondition",
      "Stat": "SignatureEnergy",
      "Operator": "GreaterOrEqual",
      "Value": 100,
      "Then": {
        "Type": "Serial",
        "Interactions": [
          { "Type": "ModifyStat", "Stat": "SignatureEnergy", "Amount": -100 },
          {
            "Type": "Simple",
            "RunTime": 1.2,
            "Effects": {
              "ItemAnimationId": "Vortexstrike",
              "WorldSoundEventId": "hytale:sounds/weapons/sword_signature"
            },
            "Next": {
              "Type": "DamageEntity",
              "TargetSelector": {
                "Type": "AOECircle",
                "Radius": 4.0,
                "Center": "Self"
              },
              "DamageParameters": {
                "DamageAmount": 35,
                "DamageCauseId": "Physical"
              }
            }
          },
          { "Type": "ApplyEffect", "EffectId": "hytale:slow", "Target": "Self", "Duration": 0.5 }
        ]
      },
      "Else": {
        "Type": "SendMessage",
        "Message": "Not enough energy!"
      }
    }
  ]
}
```

**Arrow Volley (deep nesting example):**

A charged ability that fires multiple projectiles in sequence:

```json
{
  "Type": "Charging",
  "AllowIndefiniteHold": true,
  "Next": {
    "0": "Bow_Cancel",
    "1.5": {
      "Type": "Serial",
      "Interactions": [
        { "Type": "ConsumeAmmo", "AmmoType": "arrow", "Count": 5 },
        {
          "Type": "Repeat",
          "Count": 5,
          "Interval": 0.1,
          "Interaction": {
            "Type": "Serial",
            "Interactions": [
              {
                "Type": "LaunchProjectile",
                "ProjectileId": "arrow",
                "Speed": 45,
                "SpreadAngle": 15
              },
              {
                "Type": "Simple",
                "Effects": {
                  "WorldSoundEventId": "hytale:sounds/weapons/bow_release"
                }
              }
            ]
          }
        },
        {
          "Type": "Simple",
          "RunTime": 0.8,
          "Effects": {
            "ItemAnimationId": "Bow_Recover"
          }
        }
      ]
    }
  }
}
```

### Serial vs Parallel Comparison

| Aspect | Serial | Parallel |
|--------|--------|----------|
| **Execution order** | Sequential (1 → 2 → 3) | Simultaneous (1, 2, 3 all at once) |
| **Timing** | Total time = sum of all interactions | Total time = longest interaction |
| **Dependencies** | Each step can depend on previous | No ordering guarantees |
| **Use case** | Multi-step abilities, state changes | Multiple simultaneous effects |
| **Failure handling** | Subsequent steps still execute | All started regardless of failures |

**When to use Serial:**
- Stat changes that must happen before damage
- Consuming items before applying effects
- Animations that must play in sequence
- Any ordered multi-step process

**When to use Parallel:**
- Applying multiple status effects at once
- Playing multiple sounds/particles simultaneously
- Independent effects that don't need ordering

**Parallel example for reference:**

```json
{
  "Type": "Parallel",
  "Interactions": [
    { "Type": "ApplyEffect", "EffectId": "hytale:burning", "Duration": 5 },
    { "Type": "ApplyEffect", "EffectId": "hytale:slow", "Duration": 5 },
    { "Type": "PlaySound", "SoundId": "fire_ignite" }
  ]
}
```

All three effects start at the same instant rather than one after another.

### Common Patterns

| Pattern | Description | Example Use |
|---------|-------------|-------------|
| **Sequential actions** | Multiple effects in order | Consume item → apply buff → play sound |
| **Combo finishers** | Multi-hit or multi-effect attacks | Damage → knockback → particle burst |
| **Stat changes before ability** | Resource consumption | Spend stamina → execute attack |
| **Variable injection with Replace** | Template customization | Set variable → execute template |
| **Conditional then actions** | Multiple effects on condition pass | Check health → heal → message → sound |
| **Cleanup sequences** | Restore state after ability | Clear animation → reset cooldown → remove buff |

### Integration with Replace

Serial is commonly used with [Replace](#replace) to create reusable templates:

```json
{
  "Type": "Serial",
  "Interactions": [
    {
      "Type": "Replace",
      "Var": "DamageAmount",
      "DefaultValue": { "Interactions": [] }
    },
    {
      "Type": "Replace",
      "Var": "EffectToApply",
      "DefaultOk": true,
      "DefaultValue": {
        "Interactions": ["No_Effect"]
      }
    }
  ]
}
```

Items or abilities calling this template provide their own `DamageAmount` and `EffectToApply` values.

### Related Interactions

- [Parallel](#parallel) - Execute interactions simultaneously instead of sequentially
- [Condition](#condition) - Conditional branching (often contains Serial in Then/Else)
- [StatsCondition](#statscondition) - Stat-based branching (often contains Serial in Then/Else)
- [Replace](#replace) - Variable substitution for templates
- [Repeat](#repeat) - Execute a Serial block multiple times

---

## Parallel

**Package:** `config/none/ParallelInteraction`

Executes multiple interactions concurrently.

### Structure

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

## Condition

**Package:** `config/none/ConditionInteraction`

Conditional branching based on various conditions.

### Structure

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

### Condition Types

| Type | Description | Parameters |
|------|-------------|------------|
| `HasEffect` | Check if entity has status effect | `EffectId` |
| `IsBlocking` | Check if entity is blocking | - |
| `IsOnGround` | Check if entity is grounded | - |
| `HasItem` | Check if entity has item | `ItemId` |
| `Random` | Random chance | `Chance` (0-1) |

---

## StatsCondition

**Package:** `config/none/StatsConditionInteraction`

Branch based on entity stat values.

### Structure

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

### Properties

| Property | Type | Description |
|----------|------|-------------|
| `Stat` | string | Stat to check (Health, Stamina, SignatureEnergy) |
| `Operator` | string | `LessThan`, `GreaterThan`, `Equals`, `LessOrEqual`, `GreaterOrEqual` |
| `Value` | float | Value to compare against |
| `ValueType` | string | `Absolute` or `Percent` (of max) |

### Example: Execute Low Health Enemies

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

## EffectCondition

**Package:** `config/none/EffectConditionInteraction`

Branch based on active status effects.

### Structure

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

## Repeat

**Package:** `config/none/RepeatInteraction`

Loop execution of interactions.

### Structure

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

### Properties

| Property | Type | Description |
|----------|------|-------------|
| `Count` | int | Number of repetitions |
| `Interval` | float | Delay between repetitions (seconds) |
| `Interaction` | object | Interaction to repeat |

---

## Select

**Package:** `config/none/SelectInteraction`

Random selection from multiple interactions.

### Structure

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

### Properties

| Property | Type | Description |
|----------|------|-------------|
| `Weight` | float | Selection weight (higher = more likely) |
| `Interaction` | object | Interaction to execute if selected |

In this example, poison has twice the chance of being selected.

---

## Replace

**Package:** `config/none/ReplaceInteraction`

Variable substitution for creating reusable interaction templates. Looks up a variable from the interaction context and executes its value, or falls back to a default.

### Structure

```json
{
  "Type": "Replace",
  "Var": "EffectName",
  "DefaultValue": {
    "Interactions": ["Fallback_Effect"]
  },
  "DefaultOk": true
}
```

### Properties

| Property | Type | Description |
|----------|------|-------------|
| `Var` | string | Variable name to look up from context |
| `DefaultValue` | object | Fallback interaction(s) if variable isn't set |
| `DefaultOk` | boolean | If `true`, silently uses default when variable missing. If `false`, logs SEVERE error then uses default. |

### DefaultOk Behavior

| `DefaultOk` | Variable Missing | Result |
|-------------|------------------|--------|
| `true` | Yes | Silently uses `DefaultValue` |
| `false`/omitted | Yes | Logs SEVERE error, then uses `DefaultValue` |
| either | No | Uses the variable's value |

### Example: Reusable Consumable Template

Create a generic consume template that items can customize:

**Consume_Template.json:**
```json
{
  "Type": "Serial",
  "Interactions": [
    {
      "Type": "Charging",
      "FailOnDamage": true,
      "Next": {
        "2.0": {
          "Type": "Serial",
          "Interactions": [
            {
              "Type": "ModifyInventory",
              "AdjustHeldItemQuantity": -1
            },
            {
              "Type": "Replace",
              "Var": "Effect",
              "DefaultValue": {
                "Interactions": ["No_Effect"]
              }
            }
          ]
        }
      }
    }
  ]
}
```

Items referencing this template provide their own `Effect` variable to inject custom behavior (healing, buffs, etc.) without duplicating the consume logic.

---

## Target Selectors

Target selectors determine which entities are affected by an interaction. Used in `DamageEntity` and other targeting interactions.

### Overview

| Selector | Shape | Use Case |
|----------|-------|----------|
| `AOECircle` | Circular area | Ground slams, explosions |
| `AOECylinder` | Cylindrical volume | Vertical area attacks |
| `Raycast` | Line trace | Projectiles, beams |
| `Stab` | Narrow cone | Thrusting weapons |
| `Horizontal` | Horizontal arc | Slashing weapons |

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
