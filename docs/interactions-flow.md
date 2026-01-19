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

Executes interactions sequentially, one after another.

### Structure

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
