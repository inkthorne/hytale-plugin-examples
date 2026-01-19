# Effects & Stats Reference

This document covers the JSON asset structure for status effects and entity stats.

> **See also:** [ApplyEffect Interaction](interactions-combat.md#applyeffect) for applying effects via interactions, [ChangeStat Interaction](interactions-combat.md#changestat) for modifying stats.

---

## Effects (Status Effects)

**Asset location:** `Server/Entity/Effects/` in Assets.zip

Status effects are temporary modifications applied to entities - buffs, debuffs, damage over time, transformations, and more.

### Core Properties

| Property | Type | Description |
|----------|------|-------------|
| `Duration` | float | Effect duration in seconds |
| `Infinite` | boolean | Effect persists until manually removed |
| `OverlapBehavior` | string | `Overwrite` (replace existing), `Extend` (add duration) |
| `Debuff` | boolean | Marks as negative effect (for cleanse mechanics) |
| `StatusEffectIcon` | string | UI icon path for effect display |
| `RemovalBehavior` | string | How effect is removed (e.g., `Duration`) |

### Damage Immunity & Resistance

| Property | Type | Description |
|----------|------|-------------|
| `Invulnerable` | boolean | Grants complete damage immunity |
| `DamageResistance` | object | Per-damage-type resistance modifiers |

**DamageResistance example:**

```json
{
  "DamageResistance": {
    "Fire": 1.0,
    "Physical": 0.5
  }
}
```

Values represent resistance multiplier (1.0 = immune, 0.5 = 50% reduction, 0 = no resistance).

### Model Transformation

| Property | Type | Description |
|----------|------|-------------|
| `ModelChange` | string | Transform entity into a different model (morph effects) |

**Example (Morph Potion):**

```json
{
  "Duration": 60,
  "ModelChange": "Creatures/Kweebec/Kweebec",
  "RemovalBehavior": "Duration"
}
```

---

### Stat Modifiers

Effects can modify entity stats over time using two systems:

#### Simple StatModifiers

For basic stat changes with optional percentage-based values:

| Property | Type | Description |
|----------|------|-------------|
| `StatModifiers` | object | Map of stat names to modification values |
| `ValueType` | string | `"Percent"` for percentage-based, omit for absolute |

```json
{
  "StatModifiers": {
    "HorizontalSpeed": 0.3
  },
  "ValueType": "Percent"
}
```

#### RawStatModifiers

For complex stat changes with calculation control:

| Property | Type | Description |
|----------|------|-------------|
| `Amount` | number | Value to apply |
| `CalculationType` | string | How to calculate (`Additive`, `Multiplicative`) |
| `Target` | string | Which value to target (`Max`, `Current`) |

```json
{
  "RawStatModifiers": {
    "Health": {
      "Amount": 5,
      "CalculationType": "Additive",
      "Target": "Max"
    }
  }
}
```

#### DamageCalculatorCooldown

| Property | Type | Description |
|----------|------|-------------|
| `DamageCalculatorCooldown` | float | Cooldown between stat modifier ticks (seconds) |

---

### ApplicationEffects

Effects applied when the status effect starts (one-time application):

| Property | Type | Description |
|----------|------|-------------|
| `EntityTopTint` | string | Hex color tint applied to top of entity model |
| `EntityBottomTint` | string | Hex color tint applied to bottom of entity model |
| `HorizontalSpeedMultiplier` | float | Movement speed modifier (0.0-1.0) |
| `LocalSoundEventId` | string | Sound played for the affected entity only |
| `WorldSoundEventId` | string | Sound played for all nearby entities |
| `Particles` | array | Particle systems to spawn on the entity |
| `ScreenEffect` | string | Screen overlay effect for the affected player |

**Movement restriction:**

| Property | Type | Description |
|----------|------|-------------|
| `MovementEffects.DisableAll` | boolean | Completely disable all movement |

**Ability restriction:**

| Property | Type | Description |
|----------|------|-------------|
| `AbilityEffects.Disabled` | array | List of abilities to disable (`Primary`, `Secondary`) |

**Example (Stun Effect):**

```json
{
  "Duration": 2,
  "Debuff": true,
  "ApplicationEffects": {
    "MovementEffects": {
      "DisableAll": true
    },
    "AbilityEffects": {
      "Disabled": ["Primary", "Secondary"]
    },
    "WorldSoundEventId": "SFX_Stun_Apply",
    "Particles": [
      { "SystemId": "Stun_Stars", "Bone": "Head" }
    ]
  }
}
```

---

### StatModifierEffects

Effects triggered each time stat modifiers tick:

| Property | Type | Description |
|----------|------|-------------|
| `WorldParticles` | array | Particles spawned on stat tick |
| `WorldSoundEventId` | string | Sound played on stat tick |

**Example (Regeneration with visual feedback):**

```json
{
  "Duration": 10,
  "StatModifiers": {
    "Health": 2
  },
  "DamageCalculatorCooldown": 1.0,
  "StatModifierEffects": {
    "WorldParticles": [
      { "SystemId": "Heal_Sparkle" }
    ],
    "WorldSoundEventId": "SFX_Heal_Tick"
  }
}
```

---

### Complete Effect Examples

#### Simple Buff (Food Effect)

From `Server/Entity/Effects/Meat_Buff_T1.json`:

```json
{
  "Duration": 60,
  "OverlapBehavior": "Extend",
  "Debuff": false,
  "StatusEffectIcon": "UI/Icons/Effects/Food_Buff",
  "StatModifiers": {
    "HealthRegenRate": 1.5
  },
  "ValueType": "Percent"
}
```

#### Regeneration Potion

From `Server/Entity/Effects/Potion/Potion_Health_Lesser_Regen.json`:

```json
{
  "Duration": 30,
  "OverlapBehavior": "Overwrite",
  "StatusEffectIcon": "UI/Icons/Effects/Regen",
  "StatModifiers": {
    "Health": 1
  },
  "DamageCalculatorCooldown": 1.0,
  "StatModifierEffects": {
    "WorldParticles": [
      { "SystemId": "Regen_Hearts" }
    ]
  }
}
```

#### Stun Effect (Bomb)

From `Server/Entity/Effects/Bomb_Explode_Stun.json`:

```json
{
  "Duration": 3,
  "Debuff": true,
  "StatusEffectIcon": "UI/Icons/Effects/Stunned",
  "ApplicationEffects": {
    "MovementEffects": {
      "DisableAll": true
    },
    "AbilityEffects": {
      "Disabled": ["Primary", "Secondary"]
    },
    "EntityTopTint": "#FFFF00",
    "WorldSoundEventId": "SFX_Stun_Apply"
  }
}
```

#### Morph Effect (Transformation Potion)

From `Server/Entity/Effects/Potion/Potion_Morph_Dog.json`:

```json
{
  "Duration": 120,
  "OverlapBehavior": "Overwrite",
  "StatusEffectIcon": "UI/Icons/Effects/Morph",
  "ModelChange": "Creatures/Dog/Dog",
  "ApplicationEffects": {
    "WorldSoundEventId": "SFX_Morph_Transform",
    "Particles": [
      { "SystemId": "Morph_Smoke" }
    ]
  },
  "RemovalBehavior": "Duration"
}
```

#### Damage Immunity (Dodge Invulnerability)

From `Server/Entity/Effects/Dodge_Invulnerability.json`:

```json
{
  "Duration": 0.3,
  "Infinite": false,
  "Invulnerable": true,
  "ApplicationEffects": {
    "EntityTopTint": "#FFFFFF88",
    "EntityBottomTint": "#FFFFFF88"
  }
}
```

#### Damage Resistance (Fire Immunity)

From `Server/Entity/Effects/Immunity_Fire.json`:

```json
{
  "Duration": 30,
  "StatusEffectIcon": "UI/Icons/Effects/Fire_Resist",
  "DamageResistance": {
    "Fire": 1.0
  },
  "ApplicationEffects": {
    "Particles": [
      { "SystemId": "Fire_Shield_Aura" }
    ]
  }
}
```

---

## Stat Definitions

**Asset location:** `Server/Entity/Stats/` in Assets.zip

Stat definitions configure how entity stats behave - their bounds, regeneration rules, and effects when reaching minimum or maximum values.

### Core Properties

| Property | Type | Description |
|----------|------|-------------|
| `InitialValue` | number | Starting value for the stat |
| `Min` | number | Minimum allowed value (can be negative) |
| `Max` | number | Maximum allowed value |
| `Shared` | boolean | Whether stat value is visible to other players |
| `ResetType` | string | How stat resets (e.g., `MaxValue` to reset to max) |

**Basic stat definition:**

```json
{
  "InitialValue": 100,
  "Min": 0,
  "Max": 100,
  "Shared": true
}
```

---

### Regeneration System

The `Regenerating` array defines automatic stat recovery/drain rules. Each entry creates a regeneration rule that ticks independently.

#### Regenerating Entry Properties

| Property | Type | Description |
|----------|------|-------------|
| `Interval` | float | Seconds between regeneration ticks |
| `Amount` | number | Amount to change per tick (negative for drain) |
| `RegenType` | string | `Additive` (flat amount) or `Percentage` (percent of max) |
| `ClampAtZero` | boolean | Stop regeneration when stat reaches zero |
| `Conditions` | array | Conditions that must be met for this regen to apply |

**Simple regeneration:**

```json
{
  "Regenerating": [
    {
      "Interval": 1.0,
      "Amount": 5,
      "RegenType": "Additive"
    }
  ]
}
```

---

### Condition Types

Conditions control when regeneration rules apply. Multiple conditions in an array must ALL be true.

| Condition ID | Properties | Description |
|--------------|------------|-------------|
| `Alive` | - | Entity must be alive |
| `Player` | `GameMode` | Player must be in specified game mode |
| `Stat` | `Stat`, `Amount`, `Comparison` | Another stat must meet a threshold |
| `NoDamageTaken` | `Delay` | No damage received for X seconds |
| `Suffocating` | - | Entity is suffocating in a block |
| `Sprinting` | - | Entity is sprinting |
| `Gliding` | - | Entity is gliding |
| `Wielding` | - | Entity is wielding/blocking |
| `Charging` | - | Entity is charging an interaction |
| `RegenHealth` | - | Health regeneration is enabled |

All conditions support an `Inverse` property to negate the check:

```json
{
  "Condition": "Sprinting",
  "Inverse": true
}
```

#### Comparison Values

For `Stat` conditions:

| Value | Meaning |
|-------|---------|
| `Gte` | Greater than or equal to (>=) |
| `Lt` | Less than (<) |

**Stat condition example:**

```json
{
  "Condition": "Stat",
  "Stat": "Stamina",
  "Amount": 0,
  "Comparison": "Gte"
}
```

---

### MinValueEffects / MaxValueEffects

Trigger interactions when stat reaches its minimum or maximum value.

| Property | Type | Description |
|----------|------|-------------|
| `TriggerAtZero` | boolean | Whether to trigger when stat hits zero |
| `Interactions` | object | Interactions to run when triggered |

**Example (death on zero health):**

```json
{
  "MinValueEffects": {
    "TriggerAtZero": true,
    "Interactions": {
      "Interactions": [
        { "Type": "Kill" }
      ]
    }
  }
}
```

**Example (visual effect at max energy):**

```json
{
  "MaxValueEffects": {
    "Interactions": {
      "Interactions": [
        {
          "Type": "ApplyEffect",
          "EffectId": "FullEnergy_Glow"
        }
      ]
    }
  }
}
```

---

### Complete Stat Examples

#### Health Stat

From `Server/Entity/Stats/Health.json`:

```json
{
  "InitialValue": 100,
  "Min": 0,
  "Max": 100,
  "Shared": true,
  "ResetType": "MaxValue",
  "Regenerating": [
    {
      "Interval": 1.0,
      "Amount": 1,
      "RegenType": "Additive",
      "Conditions": [
        { "Condition": "Alive" },
        { "Condition": "RegenHealth" },
        { "Condition": "NoDamageTaken", "Delay": 5.0 }
      ]
    },
    {
      "Interval": 0.5,
      "Amount": 10,
      "RegenType": "Additive",
      "Conditions": [
        { "Condition": "Alive" },
        { "Condition": "Player", "GameMode": "Creative" }
      ]
    }
  ],
  "MinValueEffects": {
    "TriggerAtZero": true,
    "Interactions": {
      "Interactions": [
        { "Type": "Kill" }
      ]
    }
  }
}
```

This health stat:
- Starts and caps at 100
- Regenerates 1 HP/second when alive, regen enabled, and no damage taken for 5 seconds
- Regenerates 10 HP/0.5s in Creative mode (fast regen)
- Triggers death when reaching zero

#### Stamina Stat

From `Server/Entity/Stats/Stamina.json`:

```json
{
  "InitialValue": 100,
  "Min": 0,
  "Max": 100,
  "Shared": false,
  "Regenerating": [
    {
      "Interval": 0.1,
      "Amount": 3,
      "RegenType": "Additive",
      "Conditions": [
        { "Condition": "Alive" },
        { "Condition": "Sprinting", "Inverse": true },
        { "Condition": "Wielding", "Inverse": true },
        { "Condition": "Charging", "Inverse": true }
      ]
    },
    {
      "Interval": 0.1,
      "Amount": -5,
      "RegenType": "Additive",
      "ClampAtZero": true,
      "Conditions": [
        { "Condition": "Sprinting" }
      ]
    }
  ],
  "MinValueEffects": {
    "TriggerAtZero": true,
    "Interactions": {
      "Interactions": [
        { "Type": "Stagger" },
        {
          "Type": "ApplyEffect",
          "EffectId": "Exhausted"
        }
      ]
    }
  },
  "MaxValueEffects": {
    "Interactions": {
      "Interactions": [
        {
          "Type": "ClearEntityEffect",
          "EffectId": "Exhausted"
        }
      ]
    }
  }
}
```

This stamina stat:
- Regenerates 3/tick when not sprinting, wielding, or charging
- Drains 5/tick while sprinting
- Triggers stagger and exhaustion effect at zero
- Clears exhaustion when reaching full

#### Mana Stat

From `Server/Entity/Stats/Mana.json`:

```json
{
  "InitialValue": 100,
  "Min": 0,
  "Max": 100,
  "Shared": false,
  "Regenerating": [
    {
      "Interval": 1.0,
      "Amount": 5,
      "RegenType": "Additive",
      "Conditions": [
        { "Condition": "Alive" },
        { "Condition": "NoDamageTaken", "Delay": 3.0 },
        { "Condition": "Charging", "Inverse": true }
      ]
    }
  ]
}
```

This mana stat:
- Regenerates 5/second when alive, not recently damaged, and not charging

#### Oxygen Stat

From `Server/Entity/Stats/Oxygen.json`:

```json
{
  "InitialValue": 100,
  "Min": 0,
  "Max": 100,
  "Shared": false,
  "Regenerating": [
    {
      "Interval": 0.5,
      "Amount": 10,
      "RegenType": "Additive",
      "Conditions": [
        { "Condition": "Suffocating", "Inverse": true }
      ]
    },
    {
      "Interval": 1.0,
      "Amount": -10,
      "RegenType": "Additive",
      "ClampAtZero": true,
      "Conditions": [
        { "Condition": "Suffocating" }
      ]
    }
  ],
  "MinValueEffects": {
    "TriggerAtZero": true,
    "Interactions": {
      "Interactions": [
        {
          "Type": "DamageEntity",
          "DamageParameters": {
            "DamageAmount": 10,
            "DamageCauseId": "Drowning"
          }
        }
      ]
    }
  }
}
```

This oxygen stat:
- Regenerates quickly when not suffocating
- Drains when suffocating
- Deals drowning damage when depleted
