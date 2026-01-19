# Control Flow Interactions

> Part of the [Interactions API](interactions.md). For base interaction properties, see [Reference](interactions.md#reference).

## Quick Navigation

| Interaction | Description |
|-------------|-------------|
| [Serial](#serial) | Execute interactions sequentially |
| [Parallel](#parallel) | Execute interactions concurrently |
| [Condition](#condition) | Game mode and movement state branching |
| [StatsCondition](#statscondition) | Branch based on entity stat values |
| [EffectCondition](#effectcondition) | Branch based on active status effects |
| [BlockCondition](#blockcondition) | Branch based on block type/state/tag |
| [CooldownCondition](#cooldowncondition) | Branch based on cooldown completion |
| [TriggerCooldown](#triggercooldown) | Start a cooldown timer |
| [ResetCooldown](#resetcooldown) | Reset a cooldown timer |
| [MovementCondition](#movementcondition) | Direction-based input branching |
| [DestroyCondition](#destroycondition) | Check if block is destroyable |
| [PlacementCountCondition](#placementcountcondition) | Branch based on block placement count |
| [Repeat](#repeat) | Loop execution of interactions |
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
          "Repeat": 5,
          "RunTime": 0.1,
          "ForkInteractions": {
            "Interactions": [
              {
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

Executes multiple interactions concurrently. Unlike [Serial](#serial) which waits for each interaction to complete before starting the next, Parallel starts all interactions at the same time. This is essential for separating independent concerns like damage logic and visual effects, allowing them to run simultaneously without blocking each other.

### Core Properties

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `Type` | string | Required | Always `"Parallel"` |
| `Interactions` | array | Required | List of interactions to execute concurrently |

### Interactions Array Format

The `Interactions` property accepts an array where each entry can be:

1. **Inline interaction object** - Full interaction definition
2. **String reference** - Path to another interaction file
3. **Mixed format** - Combination of both

**Inline interaction objects:**

```json
{
  "Type": "Parallel",
  "Interactions": [
    { "Type": "ApplyEffect", "EffectId": "hytale:burning", "Duration": 5 },
    { "Type": "ApplyEffect", "EffectId": "hytale:slow", "Duration": 5 }
  ]
}
```

**String references:**

```json
{
  "Type": "Parallel",
  "Interactions": [
    "Attack_Damage_Branch",
    "Attack_Visual_Branch",
    "Attack_Sound_Branch"
  ]
}
```

**Mixed format:**

```json
{
  "Type": "Parallel",
  "Interactions": [
    "NPC_Attack_Damage",
    { "Type": "Simple", "Effects": { "WorldSoundEventId": "attack_swoosh" } },
    "NPC_Attack_Particles"
  ]
}
```

### Execution Behavior

Parallel interactions use a **fork-based execution model** that provides true concurrency:

1. **First interaction** executes synchronously on the main context
2. **Remaining interactions** fork with duplicated contexts and run asynchronously
3. **Parent completes immediately** after forking - it does not wait for child interactions

**Key timing characteristic:** The total duration equals the duration of the **longest** interaction, not the sum. This is fundamentally different from Serial where total time = sum of all interactions.

**Execution flow:**

```
ParallelInteraction.tick0()
    │
    ├─► Execute interactions[0] on main context (SYNC)
    │
    ├─► Fork interactions[1] with duplicate context (ASYNC)
    │
    ├─► Fork interactions[2] with duplicate context (ASYNC)
    │
    └─► Mark parent as Finished (returns immediately)
        All forked interactions continue independently
```

**Important execution details:**

- The parent Parallel interaction marks itself as `Finished` immediately after forking
- Forked interactions continue running independently of the parent
- There is no built-in mechanism to wait for all forks to complete
- Changes made in one fork do **not** affect other forks (isolated contexts)

### Context Behavior

Understanding context duplication is critical for advanced Parallel usage:

| Interaction | Context | Notes |
|-------------|---------|-------|
| First (`interactions[0]`) | Shared with parent | Changes affect the original context |
| Subsequent (forked) | Duplicated copy | Changes are isolated to that fork |

**Example implications:**

```json
{
  "Type": "Parallel",
  "Interactions": [
    { "Type": "ModifyStat", "Stat": "Health", "Amount": -10 },
    { "Type": "ModifyStat", "Stat": "Health", "Amount": -10 }
  ]
}
```

In this example:
- First interaction modifies health on the main context (applies to entity)
- Second interaction modifies health on a **duplicated** context
- The entity only receives **one** 10-damage hit, not two

For damage that must stack, use Serial instead or design your interactions to work independently.

### Deep Nesting Patterns

Parallel interactions can be nested within other control flow structures.

**Parallel inside Serial (common pattern):**

```json
{
  "Type": "Serial",
  "Interactions": [
    { "Type": "ModifyStat", "Stat": "Stamina", "Amount": -20 },
    {
      "Type": "Parallel",
      "Interactions": [
        "Attack_Damage_Logic",
        "Attack_Visual_Effects"
      ]
    },
    { "Type": "ClearItemAnimation" }
  ]
}
```

This pattern ensures stamina is consumed first, then damage and visuals happen concurrently, then cleanup occurs after.

**Parallel inside `Then`/`Else` blocks (Condition):**

```json
{
  "Type": "StatsCondition",
  "Stat": "Health",
  "Operator": "LessThan",
  "Value": 50,
  "ValueType": "Percent",
  "Then": {
    "Type": "Parallel",
    "Interactions": [
      { "Type": "DamageEntity", "DamageParameters": { "DamageAmount": 50 } },
      { "Type": "ApplyEffect", "EffectId": "hytale:bleeding", "Duration": 10 },
      { "Type": "PlaySound", "SoundId": "critical_hit" }
    ]
  }
}
```

**Parallel inside `Next` blocks (Charging):**

```json
{
  "Type": "Charging",
  "FailOnDamage": true,
  "Next": {
    "1.5": {
      "Type": "Parallel",
      "Interactions": [
        { "Type": "DamageEntity", "DamageParameters": { "DamageAmount": 25 } },
        {
          "Type": "Simple",
          "Effects": {
            "WorldSoundEventId": "heavy_attack",
            "TriggerAnimation": "Slam"
          }
        }
      ]
    }
  }
}
```

### Complete Examples

**Basic Multiple Effects:**

Apply multiple status effects simultaneously:

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

**NPC Melee Attack Pattern (Damage + Visuals Separation):**

This pattern separates damage logic from visual effects, a common design in Hytale's NPC attacks:

```json
{
  "Type": "Parallel",
  "Interactions": [
    {
      "Type": "Serial",
      "Interactions": [
        {
          "Type": "Simple",
          "RunTime": 0.3
        },
        {
          "Type": "DamageEntity",
          "TargetSelector": {
            "Type": "Horizontal",
            "Range": 2.5,
            "Angle": 90
          },
          "DamageParameters": {
            "DamageAmount": 15,
            "DamageCauseId": "Physical"
          }
        }
      ]
    },
    {
      "Type": "Serial",
      "Interactions": [
        {
          "Type": "Simple",
          "RunTime": 0.8,
          "Effects": {
            "TriggerAnimation": "Attack_Swing",
            "TrailEffectId": "weapon_trail"
          }
        },
        {
          "Type": "Simple",
          "Effects": {
            "WorldSoundEventId": "sword_whoosh"
          }
        }
      ]
    }
  ]
}
```

**Branch 1 (Damage):** Waits 0.3 seconds (wind-up), then applies damage to entities in a horizontal arc.

**Branch 2 (Visuals):** Plays the full 0.8-second animation with a weapon trail, then plays the sound.

This separation allows:
- Independent timing control for damage window vs. animation duration
- Easy modification of one aspect without affecting the other
- Cleaner organization of concerns

**AOE Ground Slam with Effects:**

A powerful ground slam that combines damage with visual feedback:

```json
{
  "Type": "Parallel",
  "Interactions": [
    {
      "Type": "DamageEntity",
      "TargetSelector": {
        "Type": "AOECircle",
        "Radius": 4.0,
        "Center": "Self",
        "IncludeSelf": false
      },
      "DamageParameters": {
        "DamageAmount": 30,
        "DamageCauseId": "Physical",
        "KnockbackForce": 8.0
      }
    },
    {
      "Type": "Simple",
      "Effects": {
        "TriggerAnimation": "Stomp",
        "WorldSoundEventId": "ground_slam",
        "ParticleEffectId": "dust_explosion"
      }
    },
    {
      "Type": "ApplyEffect",
      "TargetSelector": {
        "Type": "AOECircle",
        "Radius": 4.0,
        "Center": "Self"
      },
      "EffectId": "hytale:stagger",
      "Duration": 1.5
    }
  ]
}
```

All three branches (damage, animation/particles, debuff) execute simultaneously.

**Variable Replacement in Parallel:**

Using [Replace](#replace) within Parallel for customizable attack templates:

```json
{
  "Type": "Parallel",
  "Interactions": [
    {
      "Type": "Replace",
      "Var": "DamageBranch",
      "DefaultOk": true,
      "DefaultValue": {
        "Interactions": ["Default_Damage"]
      }
    },
    {
      "Type": "Replace",
      "Var": "EffectsBranch",
      "DefaultOk": true,
      "DefaultValue": {
        "Interactions": ["Default_Effects"]
      }
    }
  ]
}
```

Items or abilities can provide custom `DamageBranch` and `EffectsBranch` values to inject specific behavior while sharing the parallel execution structure.

**Projectile Impact with Multiple Effects:**

When a projectile hits, apply damage, effects, and visuals simultaneously:

```json
{
  "Type": "Parallel",
  "Interactions": [
    {
      "Type": "DamageEntity",
      "DamageParameters": {
        "DamageAmount": 20,
        "DamageCauseId": "Projectile"
      }
    },
    { "Type": "ApplyEffect", "EffectId": "hytale:slow", "Duration": 3 },
    { "Type": "ApplyEffect", "EffectId": "hytale:poison", "Duration": 5 },
    {
      "Type": "Simple",
      "Effects": {
        "ParticleEffectId": "poison_splash",
        "WorldSoundEventId": "poison_impact"
      }
    }
  ]
}
```

### Error Handling

Parallel execution has specific error handling behavior:

| Scenario | Behavior |
|----------|----------|
| One branch fails | Other branches continue independently |
| Parent interaction | Completes immediately regardless of fork outcomes |
| Fork throws exception | Exception is isolated to that fork |
| Missing referenced interaction | Only that branch fails to execute |

**Important:** There is no built-in synchronization point for waiting on all forks to complete. If you need to ensure all parallel branches finish before continuing, you must design your interaction flow accordingly (e.g., using `RunTime` on a wrapping Simple interaction).

### Common Patterns

| Pattern | Description | Example Use |
|---------|-------------|-------------|
| **Damage + Visuals separation** | One branch for damage logic, another for effects | NPC attacks, weapon abilities |
| **Multiple status effects** | Apply several effects at once | Elemental weapons, potions |
| **AOE with feedback** | Damage selector + particles + sound | Ground slams, explosions |
| **Template branches** | Replace variables for customizable forks | Reusable attack templates |
| **Conditional parallel effects** | Parallel inside Then/Else blocks | Critical hit bonuses |

### When to Use Parallel vs Serial

| Aspect | Serial | Parallel |
|--------|--------|----------|
| **Execution order** | Sequential (1 → 2 → 3) | Simultaneous (1, 2, 3 all at once) |
| **Timing** | Total time = sum of all interactions | Total time = longest interaction |
| **Dependencies** | Each step can depend on previous | No ordering guarantees |
| **Context** | Shared context throughout | First shares, rest get duplicates |
| **Use case** | Multi-step abilities, state changes | Multiple simultaneous effects |
| **Failure handling** | Subsequent steps still execute | All started regardless of failures |

**When to use Parallel:**
- Applying multiple status effects at once
- Separating damage logic from visual effects
- Playing multiple sounds/particles simultaneously
- Independent effects that don't need ordering
- Reducing total execution time (parallel = max duration, not sum)

**When to use Serial:**
- Stat changes that must happen before damage
- Consuming items before applying effects
- Animations that must play in sequence
- Any ordered multi-step process
- When effects must modify the same context

### Related Interactions

- [Serial](#serial) - Execute interactions sequentially instead of concurrently
- [Condition](#condition) - Conditional branching (can contain Parallel in Then/Else)
- [StatsCondition](#statscondition) - Stat-based branching (can contain Parallel in Then/Else)
- [Replace](#replace) - Variable substitution for template branches
- [Repeat](#repeat) - Execute interactions multiple times (can wrap Parallel)

---

## Condition

**Package:** `config/none/ConditionInteraction`

The base Condition interaction provides branching based on game mode and entity movement states (jumping, swimming, crouching, running, flying). It evaluates the current state of an entity and branches to either `Next` (condition passed) or `Failed` (condition did not pass).

### Core Properties

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `Type` | string | Required | Always `"Condition"` |
| `RequiredGameMode` | string | `null` | Game mode that must be active (`Creative`, `Survival`, `Adventure`) |
| `Jumping` | boolean | `null` | If set, entity must be/not be jumping |
| `Swimming` | boolean | `null` | If set, entity must be/not be swimming |
| `Crouching` | boolean | `null` | If set, entity must be/not be crouching |
| `Running` | boolean | `null` | If set, entity must be/not be running (sprinting) |
| `Flying` | boolean | `null` | If set, entity must be/not be flying |
| `Next` | interaction | `null` | Interaction to execute when condition passes |
| `Failed` | interaction | `null` | Interaction to execute when condition fails |

### Branching Behavior

Unlike most condition interactions that use `Then`/`Else`, the base Condition uses `Next`/`Failed`:

- **Next**: Executed when ALL specified conditions are met
- **Failed**: Executed when ANY specified condition is not met
- Unset properties (`null`) are not checked - only explicitly set conditions are evaluated

### Execution Flow

```
Condition Evaluation
    │
    ├─► Check RequiredGameMode (if set)
    │       └─► Mismatch? → Execute Failed
    │
    ├─► Check Jumping (if set)
    │       └─► Mismatch? → Execute Failed
    │
    ├─► Check Swimming (if set)
    │       └─► Mismatch? → Execute Failed
    │
    ├─► Check Crouching (if set)
    │       └─► Mismatch? → Execute Failed
    │
    ├─► Check Running (if set)
    │       └─► Mismatch? → Execute Failed
    │
    ├─► Check Flying (if set)
    │       └─► Mismatch? → Execute Failed
    │
    └─► All checks passed → Execute Next
```

### Examples

**Game Mode Restriction:**

Only allow ability in Creative mode:

```json
{
  "Type": "Condition",
  "RequiredGameMode": "Creative",
  "Next": {
    "Type": "SpawnPrefab",
    "PrefabId": "debug_entity"
  },
  "Failed": {
    "Type": "SendMessage",
    "Message": "Creative mode only!"
  }
}
```

**Aerial Combat Ability:**

Special attack that only works while jumping:

```json
{
  "Type": "Condition",
  "Jumping": true,
  "Next": {
    "Type": "Serial",
    "Interactions": [
      {
        "Type": "ApplyMovementImpulse",
        "Direction": "Down",
        "Force": 15.0
      },
      {
        "Type": "DamageEntity",
        "TargetSelector": { "Type": "AOECircle", "Radius": 3.0 },
        "DamageParameters": { "DamageAmount": 40 }
      }
    ]
  },
  "Failed": "Ground_Attack_Normal"
}
```

**Aquatic Boost:**

Faster swimming when already in water:

```json
{
  "Type": "Condition",
  "Swimming": true,
  "Next": {
    "Type": "ApplyEffect",
    "EffectId": "hytale:dolphins_grace",
    "Duration": 10
  }
}
```

**Stealth Attack:**

Bonus damage when attacking from crouch:

```json
{
  "Type": "Condition",
  "Crouching": true,
  "Next": {
    "Type": "DamageEntity",
    "DamageParameters": { "DamageAmount": 50, "DamageCauseId": "Physical" }
  },
  "Failed": {
    "Type": "DamageEntity",
    "DamageParameters": { "DamageAmount": 20, "DamageCauseId": "Physical" }
  }
}
```

**Sprint Attack:**

Momentum-based damage scaling:

```json
{
  "Type": "Condition",
  "Running": true,
  "Next": {
    "Type": "Serial",
    "Interactions": [
      {
        "Type": "DamageEntity",
        "DamageParameters": { "DamageAmount": 35, "KnockbackForce": 12.0 }
      },
      {
        "Type": "Simple",
        "Effects": { "WorldSoundEventId": "charge_impact" }
      }
    ]
  },
  "Failed": "Attack_Normal"
}
```

**Multiple Conditions:**

All specified conditions must be true:

```json
{
  "Type": "Condition",
  "RequiredGameMode": "Survival",
  "Running": true,
  "Jumping": false,
  "Next": "Sprint_Slide_Start",
  "Failed": "Movement_Normal"
}
```

This checks: Survival mode AND sprinting AND NOT jumping.

### Related Interactions

- [StatsCondition](#statscondition) - Branch based on stat values
- [EffectCondition](#effectcondition) - Branch based on active effects
- [MovementCondition](#movementcondition) - Branch based on movement direction input

---

## StatsCondition

**Package:** `config/none/StatsConditionInteraction`

Branch based on entity stat values. Compares a stat against a threshold value using configurable operators. Supports both absolute values and percentages of maximum. Essential for resource gating, execute mechanics, and stat-based ability variations.

### Core Properties

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `Type` | string | Required | Always `"StatsCondition"` |
| `Stat` | string | Required | Stat identifier to check |
| `Operator` | string | Required | Comparison operator |
| `Value` | float | Required | Value to compare against |
| `ValueType` | string | `"Absolute"` | How to interpret `Value` |
| `Lenient` | boolean | `false` | If true, passes when stat doesn't exist |
| `Then` | interaction | `null` | Interaction when condition is true |
| `Else` | interaction | `null` | Interaction when condition is false |

### Operator Reference

| Operator | Symbol | Description |
|----------|--------|-------------|
| `LessThan` | `<` | Stat value is less than threshold |
| `GreaterThan` | `>` | Stat value is greater than threshold |
| `Equals` | `==` | Stat value equals threshold |
| `LessOrEqual` | `<=` | Stat value is less than or equal to threshold |
| `GreaterOrEqual` | `>=` | Stat value is greater than or equal to threshold |

### ValueType Reference

| ValueType | Description |
|-----------|-------------|
| `Absolute` | Compare against the raw stat value |
| `Percent` | Compare against percentage of maximum (0-100) |

### Lenient Mode

When `Lenient` is `true`, the condition passes if the stat doesn't exist on the entity. This is useful for optional stats that not all entities have.

```json
{
  "Type": "StatsCondition",
  "Stat": "CustomAbilityCharge",
  "Operator": "GreaterOrEqual",
  "Value": 100,
  "Lenient": true,
  "Then": "Execute_Ability",
  "Else": "Charge_More"
}
```

If an entity doesn't have `CustomAbilityCharge`, it will execute `Then` instead of failing.

### Common Stats

| Stat | Description |
|------|-------------|
| `Health` | Current health points |
| `Stamina` | Current stamina points |
| `SignatureEnergy` | Signature ability charge |
| `Mana` | Magic resource (if applicable) |
| `Hunger` | Hunger level |
| `Temperature` | Entity temperature |

### Examples

**Execute Mechanic (Low Health Finisher):**

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
        "Type": "Simple",
        "Effects": {
          "WorldSoundEventId": "execute_sound",
          "ParticleEffectId": "execute_particles"
        }
      }
    ]
  },
  "Else": {
    "Type": "DamageEntity",
    "DamageParameters": { "DamageAmount": 20, "DamageCauseId": "Physical" }
  }
}
```

**Stamina Cost Check:**

Verify stamina before executing ability:

```json
{
  "Type": "StatsCondition",
  "Stat": "Stamina",
  "Operator": "GreaterOrEqual",
  "Value": 25,
  "Then": {
    "Type": "Serial",
    "Interactions": [
      { "Type": "ModifyStat", "Stat": "Stamina", "Amount": -25 },
      "Dodge_Execute"
    ]
  },
  "Else": {
    "Type": "SendMessage",
    "Message": "Not enough stamina!"
  }
}
```

**Signature Energy Threshold:**

```json
{
  "Type": "StatsCondition",
  "Stat": "SignatureEnergy",
  "Operator": "GreaterOrEqual",
  "Value": 100,
  "Then": {
    "Type": "Serial",
    "Interactions": [
      { "Type": "ModifyStat", "Stat": "SignatureEnergy", "Amount": -100 },
      "Signature_Ability_Execute"
    ]
  },
  "Else": {
    "Type": "Simple",
    "Effects": { "WorldSoundEventId": "ability_not_ready" }
  }
}
```

**Full Health Check:**

```json
{
  "Type": "StatsCondition",
  "Stat": "Health",
  "Operator": "Equals",
  "Value": 100,
  "ValueType": "Percent",
  "Then": "Apply_Full_Health_Bonus",
  "Else": "Apply_Normal_Effect"
}
```

**Critical Health Warning:**

```json
{
  "Type": "StatsCondition",
  "Stat": "Health",
  "Operator": "LessOrEqual",
  "Value": 20,
  "ValueType": "Percent",
  "Then": {
    "Type": "Parallel",
    "Interactions": [
      { "Type": "ApplyEffect", "EffectId": "hytale:critical_health_warning", "Duration": 5 },
      { "Type": "Simple", "Effects": { "WorldSoundEventId": "heartbeat_warning" } }
    ]
  }
}
```

**Nested Stats Checks:**

Multiple resource requirements:

```json
{
  "Type": "StatsCondition",
  "Stat": "Stamina",
  "Operator": "GreaterOrEqual",
  "Value": 50,
  "Then": {
    "Type": "StatsCondition",
    "Stat": "Mana",
    "Operator": "GreaterOrEqual",
    "Value": 30,
    "Then": {
      "Type": "Serial",
      "Interactions": [
        { "Type": "ModifyStat", "Stat": "Stamina", "Amount": -50 },
        { "Type": "ModifyStat", "Stat": "Mana", "Amount": -30 },
        "Hybrid_Ability_Execute"
      ]
    },
    "Else": { "Type": "SendMessage", "Message": "Not enough mana!" }
  },
  "Else": { "Type": "SendMessage", "Message": "Not enough stamina!" }
}
```

### Related Interactions

- [Condition](#condition) - Game mode and movement state branching
- [EffectCondition](#effectcondition) - Branch based on active effects
- [ModifyStat](interactions-combat.md#changestat) - Modify stat values

---

## EffectCondition

**Package:** `config/none/EffectConditionInteraction`

Branch based on whether an entity has active status effects. Supports checking for multiple effects with configurable match modes (`All` or `None`). Use this for effect-based combat bonuses, immunity checks, and tiered buff systems.

### Core Properties

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `Type` | string | Required | Always `"EffectCondition"` |
| `EntityEffectIds` | array | Required | List of effect IDs to check |
| `Match` | string | `"All"` | Match mode: `"All"` or `"None"` |
| `EntityTarget` | string | `"Self"` | Which entity to check (`"Self"`, `"Target"`) |
| `Then` | interaction | `null` | Interaction when condition is true |
| `Else` | interaction | `null` | Interaction when condition is false |

### Match Modes

| Mode | Description |
|------|-------------|
| `All` | Entity must have ALL specified effects |
| `None` | Entity must have NONE of the specified effects |

### EntityTarget Reference

| Target | Description |
|--------|-------------|
| `Self` | Check the entity executing the interaction |
| `Target` | Check the target entity (from context) |

### Execution Behavior

```
EffectCondition Evaluation
    │
    ├─► Resolve EntityTarget (Self or Target)
    │
    ├─► For each effect in EntityEffectIds:
    │       └─► Check if entity has effect
    │
    ├─► Match Mode: All
    │       └─► ALL effects present? → Then
    │       └─► ANY effect missing? → Else
    │
    └─► Match Mode: None
            └─► NO effects present? → Then
            └─► ANY effect present? → Else
```

### Examples

**Single Effect Check:**

```json
{
  "Type": "EffectCondition",
  "EntityEffectIds": ["hytale:burning"],
  "EntityTarget": "Target",
  "Then": {
    "Type": "DamageEntity",
    "DamageParameters": { "DamageAmount": 30, "DamageCauseId": "Fire" }
  },
  "Else": {
    "Type": "ApplyEffect",
    "EffectId": "hytale:burning",
    "Duration": 5
  }
}
```

If target is burning, deal bonus fire damage. Otherwise, ignite them.

**Multiple Effects Check (All):**

Combo system requiring multiple debuffs:

```json
{
  "Type": "EffectCondition",
  "EntityEffectIds": ["hytale:burning", "hytale:poisoned", "hytale:frozen"],
  "Match": "All",
  "EntityTarget": "Target",
  "Then": {
    "Type": "Serial",
    "Interactions": [
      {
        "Type": "DamageEntity",
        "DamageParameters": { "DamageAmount": 100, "DamageCauseId": "Elemental" }
      },
      { "Type": "ClearEntityEffect", "EffectId": "hytale:burning" },
      { "Type": "ClearEntityEffect", "EffectId": "hytale:poisoned" },
      { "Type": "ClearEntityEffect", "EffectId": "hytale:frozen" },
      { "Type": "Simple", "Effects": { "ParticleEffectId": "elemental_explosion" } }
    ]
  },
  "Else": "Normal_Attack"
}
```

**Immunity Check (None):**

Prevent effect stacking:

```json
{
  "Type": "EffectCondition",
  "EntityEffectIds": ["hytale:immunity"],
  "Match": "None",
  "EntityTarget": "Target",
  "Then": {
    "Type": "ApplyEffect",
    "EffectId": "hytale:stun",
    "Duration": 3
  },
  "Else": {
    "Type": "Simple",
    "Effects": { "WorldSoundEventId": "ability_blocked" }
  }
}
```

Only apply stun if target doesn't have immunity.

**Tiered Buff System (Meat_TierCheck pattern):**

Check for food buff tiers:

```json
{
  "Type": "EffectCondition",
  "EntityEffectIds": ["hytale:well_fed_tier3"],
  "Match": "None",
  "EntityTarget": "Self",
  "Then": {
    "Type": "EffectCondition",
    "EntityEffectIds": ["hytale:well_fed_tier2"],
    "Match": "None",
    "EntityTarget": "Self",
    "Then": {
      "Type": "EffectCondition",
      "EntityEffectIds": ["hytale:well_fed_tier1"],
      "Match": "None",
      "EntityTarget": "Self",
      "Then": "Apply_Tier1_Buff",
      "Else": "Upgrade_To_Tier2"
    },
    "Else": "Upgrade_To_Tier3"
  },
  "Else": "Refresh_Tier3"
}
```

**Self-Buff Check:**

Only allow ability if not already buffed:

```json
{
  "Type": "EffectCondition",
  "EntityEffectIds": ["hytale:enraged"],
  "Match": "None",
  "EntityTarget": "Self",
  "Then": {
    "Type": "Serial",
    "Interactions": [
      { "Type": "ApplyEffect", "EffectId": "hytale:enraged", "Duration": 30 },
      { "Type": "Simple", "Effects": { "TriggerAnimation": "Enrage" } }
    ]
  },
  "Else": {
    "Type": "SendMessage",
    "Message": "Already enraged!"
  }
}
```

**Elemental Weakness:**

Bonus damage against debuffed targets:

```json
{
  "Type": "EffectCondition",
  "EntityEffectIds": ["hytale:wet"],
  "Match": "All",
  "EntityTarget": "Target",
  "Then": {
    "Type": "Parallel",
    "Interactions": [
      { "Type": "DamageEntity", "DamageParameters": { "DamageAmount": 40, "DamageCauseId": "Lightning" } },
      { "Type": "ApplyEffect", "EffectId": "hytale:shocked", "Duration": 3 }
    ]
  },
  "Else": {
    "Type": "DamageEntity",
    "DamageParameters": { "DamageAmount": 20, "DamageCauseId": "Lightning" }
  }
}
```

### Related Interactions

- [Condition](#condition) - Game mode and movement state branching
- [StatsCondition](#statscondition) - Branch based on stat values
- [ApplyEffect](interactions-combat.md#applyeffect) - Apply status effects
- [ClearEntityEffect](interactions-combat.md#clearentityeffect) - Remove status effects

---

## BlockCondition

**Package:** `config/client/BlockConditionInteraction`

Branch based on block type, state, or tag at a target position. Uses a `Matchers` array with `BlockMatcher` objects that can check block IDs, block states, and tags. Supports face-specific matching for directional placement logic.

### Core Properties

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `Type` | string | Required | Always `"BlockCondition"` |
| `Matchers` | array | Required | List of `BlockMatcher` objects |
| `Then` | interaction | `null` | Interaction when any matcher succeeds |
| `Else` | interaction | `null` | Interaction when all matchers fail |

### BlockMatcher Structure

Each `BlockMatcher` in the array has:

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `Face` | string | `"None"` | Which face to check relative to target |
| `StaticFace` | boolean | `false` | If true, face is absolute; if false, face is relative to player |
| `Id` | string | `null` | Exact block ID to match |
| `State` | object | `null` | Block state properties to match |
| `Tag` | string | `null` | Block tag to match |
| `TagIndex` | int | `null` | Specific tag index for multi-tag blocks |

### Face Reference

| Face | Description |
|------|-------------|
| `None` | Check block at target position |
| `Up` | Check block above target |
| `Down` | Check block below target |
| `Left` | Check block to the left |
| `Right` | Check block to the right |
| `Front` | Check block in front |
| `Back` | Check block behind |

### StaticFace Behavior

| StaticFace | Behavior |
|------------|----------|
| `false` | Face directions are relative to player's facing direction |
| `true` | Face directions are absolute world directions |

### Examples

**Seed Planting Validation:**

Check if target block is farmland before planting:

```json
{
  "Type": "BlockCondition",
  "Matchers": [
    {
      "Face": "Down",
      "Tag": "hytale:farmland"
    }
  ],
  "Then": {
    "Type": "Serial",
    "Interactions": [
      { "Type": "PlaceBlock", "BlockId": "hytale:wheat_seeds" },
      { "Type": "ModifyInventory", "AdjustHeldItemQuantity": -1 }
    ]
  },
  "Else": {
    "Type": "SendMessage",
    "Message": "Seeds must be planted on farmland!"
  }
}
```

**Specific Block ID Check:**

Only interact with specific block type:

```json
{
  "Type": "BlockCondition",
  "Matchers": [
    {
      "Id": "hytale:crafting_table"
    }
  ],
  "Then": {
    "Type": "OpenPage",
    "PageId": "crafting_ui"
  }
}
```

**Block State Check:**

Check for specific block state (e.g., open door):

```json
{
  "Type": "BlockCondition",
  "Matchers": [
    {
      "Id": "hytale:wooden_door",
      "State": { "open": "true" }
    }
  ],
  "Then": "Close_Door_Interaction",
  "Else": "Open_Door_Interaction"
}
```

**Multiple Matchers (OR logic):**

Plant can be placed on multiple soil types:

```json
{
  "Type": "BlockCondition",
  "Matchers": [
    { "Face": "Down", "Tag": "hytale:farmland" },
    { "Face": "Down", "Tag": "hytale:grass" },
    { "Face": "Down", "Id": "hytale:dirt" }
  ],
  "Then": "Plant_Seed",
  "Else": {
    "Type": "SendMessage",
    "Message": "Cannot plant here!"
  }
}
```

**Wall Placement Check:**

Check for solid block behind for wall-mounted items:

```json
{
  "Type": "BlockCondition",
  "Matchers": [
    {
      "Face": "Back",
      "StaticFace": false,
      "Tag": "hytale:solid"
    }
  ],
  "Then": "Place_Wall_Torch",
  "Else": {
    "Type": "SendMessage",
    "Message": "Requires a solid wall behind!"
  }
}
```

### Related Interactions

- [DestroyCondition](#destroycondition) - Check if block is destroyable
- [PlacementCountCondition](#placementcountcondition) - Check block placement limits
- [Block Interactions](interactions-world.md#block-interactions) - Break or place blocks

---

## CooldownCondition

**Package:** `config/client/CooldownConditionInteraction`

Branch based on whether a cooldown has completed. Checks if the specified cooldown timer has elapsed, allowing time-gated abilities and rate limiting.

### Core Properties

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `Type` | string | Required | Always `"CooldownCondition"` |
| `Id` | string | Required | Cooldown identifier to check |
| `Next` | interaction | `null` | Interaction when cooldown is ready |
| `Failed` | interaction | `null` | Interaction when cooldown is active |

### Execution Flow

```
CooldownCondition
    │
    ▼
┌─────────────────────────┐
│ Check cooldown by Id    │
└─────────────────────────┘
    │
    ├─► Cooldown elapsed (ready) ──► Execute Next
    │
    └─► Cooldown active ──► Execute Failed
```

### Execution Behavior

CooldownCondition checks if the specified cooldown timer has expired:

- **Next**: Executed when cooldown has elapsed (ability is ready)
- **Failed**: Executed when cooldown is still active (ability on cooldown)

Cooldowns are typically started using [TriggerCooldown](#triggercooldown) and can be reset using [ResetCooldown](#resetcooldown).

### Examples

**NPC Poison Attack (from Spider.json):**

Check if poison cooldown has elapsed before applying poison effect:

```json
{
  "Type": "CooldownCondition",
  "Id": "Spider_Poison",
  "Next": {
    "Type": "Serial",
    "Interactions": [
      {
        "Type": "TriggerCooldown",
        "Cooldown": {
          "Id": "Spider_Poison",
          "Cooldown": 8
        }
      },
      {
        "Type": "ApplyEffect",
        "EffectId": "hytale:poison",
        "Duration": 4
      },
      {
        "Type": "DamageEntity",
        "DamageParameters": { "DamageAmount": 5 }
      }
    ]
  },
  "Failed": {
    "Type": "DamageEntity",
    "DamageParameters": { "DamageAmount": 5 }
  }
}
```

**Boss Special Attack (from Snapdragon.json):**

Cooldown-gated fire breath attack:

```json
{
  "Type": "CooldownCondition",
  "Id": "Snapdragon_FireBreath",
  "Next": {
    "Type": "Serial",
    "Interactions": [
      {
        "Type": "TriggerCooldown",
        "Cooldown": {
          "Id": "Snapdragon_FireBreath",
          "Cooldown": 12
        }
      },
      "Snapdragon_FireBreath_Execute"
    ]
  },
  "Failed": "Snapdragon_BasicAttack"
}
```

**Conditional Damage Bonus:**

Apply bonus damage only when cooldown is ready:

```json
{
  "Type": "CooldownCondition",
  "Id": "critical_strike",
  "Next": {
    "Type": "Serial",
    "Interactions": [
      {
        "Type": "DamageEntity",
        "DamageParameters": { "DamageAmount": 25 }
      },
      {
        "Type": "TriggerCooldown",
        "Cooldown": {
          "Id": "critical_strike",
          "Cooldown": 5
        }
      }
    ]
  },
  "Failed": {
    "Type": "DamageEntity",
    "DamageParameters": { "DamageAmount": 10 }
  }
}
```

### Related Interactions

- [TriggerCooldown](#triggercooldown) - Start a cooldown timer
- [ResetCooldown](#resetcooldown) - Reset a cooldown timer
- [Condition](#condition) - Base conditional branching
- [StatsCondition](#statscondition) - Resource-based gating

---

## TriggerCooldown

**Package:** `config/client/TriggerCooldownInteraction`

Start a cooldown timer. Used to initiate time-gated abilities that can later be checked with [CooldownCondition](#cooldowncondition).

### Core Properties

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `Type` | string | Required | Always `"TriggerCooldown"` |
| `Cooldown` | object | Required | [InteractionCooldown](#interactioncooldown-configuration) configuration |

### InteractionCooldown Configuration

The `Cooldown` property uses the InteractionCooldown configuration object:

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `Id` | string | `null` | Cooldown identifier (used to check with CooldownCondition) |
| `Cooldown` | float | Required | Duration in seconds |
| `ClickBypass` | boolean | `false` | If true, clicking bypasses the cooldown |
| `Charges` | float[] | `null` | Array of charge times for charged abilities |

### Examples

**Basic Cooldown Start:**

```json
{
  "Type": "TriggerCooldown",
  "Cooldown": {
    "Id": "ability_dash",
    "Cooldown": 5
  }
}
```

**NPC Attack Cooldown (from Spider.json):**

```json
{
  "Type": "TriggerCooldown",
  "Cooldown": {
    "Id": "Spider_Poison",
    "Cooldown": 8
  }
}
```

**Cooldown with Click Bypass (from RootInteractions):**

Used in block interactions where clicking can bypass the wait:

```json
{
  "Type": "TriggerCooldown",
  "Cooldown": {
    "Id": "BlockInteraction_Creative",
    "Cooldown": 0.0,
    "ClickBypass": true
  }
}
```

**Cooldown without Id:**

Anonymous cooldown (cannot be checked with CooldownCondition):

```json
{
  "Type": "TriggerCooldown",
  "Cooldown": {
    "Cooldown": 1.5
  }
}
```

### Usage Pattern

TriggerCooldown is typically used inside the `Next` branch of a CooldownCondition:

```json
{
  "Type": "CooldownCondition",
  "Id": "my_ability",
  "Next": {
    "Type": "Serial",
    "Interactions": [
      {
        "Type": "TriggerCooldown",
        "Cooldown": {
          "Id": "my_ability",
          "Cooldown": 10
        }
      },
      "Execute_Ability"
    ]
  },
  "Failed": "Ability_NotReady_Feedback"
}
```

### Related Interactions

- [CooldownCondition](#cooldowncondition) - Check if cooldown has elapsed
- [ResetCooldown](#resetcooldown) - Reset a cooldown timer

---

## ResetCooldown

**Package:** `config/client/ResetCooldownInteraction`

Reset a cooldown timer, making it immediately ready. Used to cancel active cooldowns or refresh ability availability.

### Core Properties

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `Type` | string | Required | Always `"ResetCooldown"` |
| `Cooldown` | object | Required | [InteractionCooldown](#interactioncooldown-configuration) configuration |

### Examples

**Reset Named Cooldown:**

```json
{
  "Type": "ResetCooldown",
  "Cooldown": {
    "Id": "ability_dash",
    "Cooldown": 0
  }
}
```

**Reset on Parry (from Debug_Stick_Parry.json):**

Successful parry resets attack cooldown:

```json
{
  "Type": "Serial",
  "Interactions": [
    "Parry_Success_Effects",
    {
      "Type": "ResetCooldown",
      "Cooldown": {
        "Id": "attack_cooldown",
        "Cooldown": 0
      }
    }
  ]
}
```

**Reset Anonymous Cooldown (from Bomb_Throw.json):**

```json
{
  "Type": "ResetCooldown",
  "Cooldown": {
    "Cooldown": 1
  }
}
```

### Usage Patterns

**Reset on Kill:**

```json
{
  "Type": "Serial",
  "Interactions": [
    {
      "Type": "DamageEntity",
      "DamageParameters": { "DamageAmount": 100, "Lethal": true }
    },
    {
      "Type": "ResetCooldown",
      "Cooldown": {
        "Id": "execute_ability",
        "Cooldown": 0
      }
    }
  ]
}
```

**Emergency Reset Consumable:**

```json
{
  "Type": "Serial",
  "Interactions": [
    { "Type": "ModifyInventory", "AdjustHeldItemQuantity": -1 },
    {
      "Type": "ResetCooldown",
      "Cooldown": {
        "Id": "ultimate_ability",
        "Cooldown": 0
      }
    }
  ]
}
```

### Related Interactions

- [CooldownCondition](#cooldowncondition) - Check if cooldown has elapsed
- [TriggerCooldown](#triggercooldown) - Start a cooldown timer

---

## MovementCondition

**Package:** `config/client/MovementConditionInteraction`

Branch based on player movement input direction. Provides eight directional branches plus a failed branch, enabling direction-based combat abilities like directional dodges, strafing attacks, and movement-responsive mechanics.

### Core Properties

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `Type` | string | Required | Always `"MovementCondition"` |
| `Forward` | interaction | `null` | Interaction when moving forward |
| `Back` | interaction | `null` | Interaction when moving backward |
| `Left` | interaction | `null` | Interaction when moving left |
| `Right` | interaction | `null` | Interaction when moving right |
| `ForwardLeft` | interaction | `null` | Interaction when moving forward-left diagonal |
| `ForwardRight` | interaction | `null` | Interaction when moving forward-right diagonal |
| `BackLeft` | interaction | `null` | Interaction when moving backward-left diagonal |
| `BackRight` | interaction | `null` | Interaction when moving backward-right diagonal |
| `Failed` | interaction | `null` | Interaction when no movement or no matching direction |

### Direction Detection

Directions are based on player input relative to camera facing:

```
        Forward
           ↑
   ForwardLeft  ForwardRight
        ↖   ↗
Left  ←       →  Right
        ↙   ↘
   BackLeft    BackRight
           ↓
         Back
```

### Execution Behavior

1. Reads current movement input direction
2. Matches to closest of 8 cardinal/diagonal directions
3. Executes corresponding branch interaction
4. If no movement input or no branch defined for direction, executes `Failed`

### Examples

**Directional Dodge System:**

```json
{
  "Type": "MovementCondition",
  "Forward": "Dodge_Forward",
  "Back": "Dodge_Back",
  "Left": "Dodge_Left",
  "Right": "Dodge_Right",
  "ForwardLeft": "Dodge_Forward_Left",
  "ForwardRight": "Dodge_Forward_Right",
  "BackLeft": "Dodge_Back_Left",
  "BackRight": "Dodge_Back_Right",
  "Failed": "Dodge_Back"
}
```

**Directional Attack Variations:**

```json
{
  "Type": "MovementCondition",
  "Forward": {
    "Type": "Serial",
    "Interactions": [
      { "Type": "ApplyMovementImpulse", "Direction": "Forward", "Force": 5.0 },
      { "Type": "DamageEntity", "DamageParameters": { "DamageAmount": 25 } }
    ]
  },
  "Back": {
    "Type": "Serial",
    "Interactions": [
      { "Type": "ApplyMovementImpulse", "Direction": "Back", "Force": 3.0 },
      { "Type": "DamageEntity", "DamageParameters": { "DamageAmount": 15 } }
    ]
  },
  "Left": "Slash_Left",
  "Right": "Slash_Right",
  "Failed": "Slash_Neutral"
}
```

**Simple Four-Direction Dodge:**

Only handle cardinal directions, default others to Failed:

```json
{
  "Type": "MovementCondition",
  "Forward": {
    "Type": "Serial",
    "Interactions": [
      { "Type": "ApplyMovementImpulse", "Direction": "Forward", "Force": 8.0 },
      { "Type": "ModifyStat", "Stat": "Stamina", "Amount": -20 },
      { "Type": "ApplyEffect", "EffectId": "hytale:invulnerable", "Duration": 0.3 }
    ]
  },
  "Back": {
    "Type": "Serial",
    "Interactions": [
      { "Type": "ApplyMovementImpulse", "Direction": "Back", "Force": 8.0 },
      { "Type": "ModifyStat", "Stat": "Stamina", "Amount": -20 },
      { "Type": "ApplyEffect", "EffectId": "hytale:invulnerable", "Duration": 0.3 }
    ]
  },
  "Left": {
    "Type": "Serial",
    "Interactions": [
      { "Type": "ApplyMovementImpulse", "Direction": "Left", "Force": 8.0 },
      { "Type": "ModifyStat", "Stat": "Stamina", "Amount": -20 },
      { "Type": "ApplyEffect", "EffectId": "hytale:invulnerable", "Duration": 0.3 }
    ]
  },
  "Right": {
    "Type": "Serial",
    "Interactions": [
      { "Type": "ApplyMovementImpulse", "Direction": "Right", "Force": 8.0 },
      { "Type": "ModifyStat", "Stat": "Stamina", "Amount": -20 },
      { "Type": "ApplyEffect", "EffectId": "hytale:invulnerable", "Duration": 0.3 }
    ]
  },
  "Failed": {
    "Type": "SendMessage",
    "Message": "Hold a direction to dodge!"
  }
}
```

**Movement-Based Attack Selection:**

```json
{
  "Type": "StatsCondition",
  "Stat": "Stamina",
  "Operator": "GreaterOrEqual",
  "Value": 15,
  "Then": {
    "Type": "MovementCondition",
    "Forward": "Lunge_Attack",
    "Back": "Retreating_Slash",
    "Left": "Sidestep_Left_Attack",
    "Right": "Sidestep_Right_Attack",
    "Failed": "Standing_Attack"
  },
  "Else": {
    "Type": "SendMessage",
    "Message": "Not enough stamina!"
  }
}
```

### Related Interactions

- [Condition](#condition) - Movement state branching (jumping, running, etc.)
- [ApplyMovementImpulse](interactions-combat.md#applyforce) - Apply movement forces

---

## DestroyCondition

**Package:** `config/server/DestroyConditionInteraction`

Server-side condition that checks if a block at the target position is destroyable. Used to validate block breaking operations before execution, preventing invalid destruction attempts.

**Inheritance:** `DestroyConditionInteraction` → `SimpleBlockInteraction` → `SimpleInteraction` → `Interaction`

### Core Properties

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `Type` | string | Required | Always `"DestroyCondition"` |
| `Next` | interaction | `null` | Interaction when block is destroyable |
| `Failed` | interaction | `null` | Interaction when block is not destroyable |
| `UseLatestTarget` | boolean | `false` | Use the most recent block target from context |

### Execution Flow

```
DestroyCondition
    │
    ▼
┌─────────────────────────┐
│ Check block destroyable │
└─────────────────────────┘
    │
    ├─► Destroyable ──► Execute Next
    │
    └─► Not destroyable ──► Execute Failed
```

DestroyCondition performs server-side validation:

1. Reads target block position from interaction context
2. Checks if block exists and is marked as destroyable
3. Considers block properties, protection zones, and game rules
4. Branches to `Next` if destruction is allowed, `Failed` if blocked

### Examples

**Break Container (Real - from Break_Container.json):**

```json
{
  "Type": "DestroyCondition",
  "Next": {
    "Type": "Parallel",
    "Interactions": [
      {
        "Interactions": [
          {
            "Type": "Simple",
            "RunTime": 0.1,
            "Effects": { "ItemAnimationId": "AttackLeft" }
          }
        ]
      },
      {
        "Interactions": [
          {
            "Type": "Simple",
            "RunTime": 0.1,
            "Effects": { "ItemAnimationId": "SwingLeft" },
            "Next": { "Type": "BreakBlock" }
          }
        ]
      }
    ]
  }
}
```

**Simple Destroy Check:**

```json
{
  "Type": "DestroyCondition",
  "Next": {
    "Type": "BreakBlock"
  },
  "Failed": {
    "Type": "Simple",
    "Effects": { "WorldSoundEventId": "action_denied" }
  }
}
```

**With UseLatestTarget:**

```json
{
  "Type": "DestroyCondition",
  "UseLatestTarget": true,
  "Next": {
    "Type": "BreakBlock",
    "UseLatestTarget": true
  }
}
```

### Related Interactions

- [BlockCondition](#blockcondition) - Check block type/state
- [Block Interactions](interactions-world.md#block-interactions) - Break or place blocks
- [Condition](#condition) - General condition branching (also uses `Next`/`Failed`)

---

## PlacementCountCondition

**Package:** `config/server/PlacementCountConditionInteraction`

Server-side condition that branches based on the count of a specific block type placed in an area or by the player. Used to enforce placement limits for special blocks like teleporters or spawners.

### Core Properties

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `Type` | string | Required | Always `"PlacementCountCondition"` |
| `Block` | string | Required | Block ID to count |
| `Value` | int | Required | Threshold value for comparison |
| `LessThan` | boolean | `true` | If true, passes when count < value; if false, passes when count >= value |
| `Then` | interaction | `null` | Interaction when condition is true |
| `Else` | interaction | `null` | Interaction when condition is false |

### Execution Behavior

| LessThan | Condition | Behavior |
|----------|-----------|----------|
| `true` | count < value | Execute `Then` |
| `true` | count >= value | Execute `Else` |
| `false` | count >= value | Execute `Then` |
| `false` | count < value | Execute `Else` |

### Examples

**Teleporter Placement Limit:**

Only allow placing teleporter if player has fewer than 2:

```json
{
  "Type": "PlacementCountCondition",
  "Block": "hytale:teleporter",
  "Value": 2,
  "LessThan": true,
  "Then": {
    "Type": "Serial",
    "Interactions": [
      { "Type": "PlaceBlock", "BlockId": "hytale:teleporter" },
      { "Type": "ModifyInventory", "AdjustHeldItemQuantity": -1 },
      { "Type": "Simple", "Effects": { "WorldSoundEventId": "teleporter_placed" } }
    ]
  },
  "Else": {
    "Type": "SendMessage",
    "Message": "You can only have 2 teleporters!"
  }
}
```

**Spawner Limit Check:**

```json
{
  "Type": "PlacementCountCondition",
  "Block": "hytale:creature_spawner",
  "Value": 5,
  "LessThan": true,
  "Then": "Place_Spawner",
  "Else": {
    "Type": "SendMessage",
    "Message": "Maximum spawners reached (5)!"
  }
}
```

**Minimum Placement Requirement:**

Check if player has placed at least 4 blocks:

```json
{
  "Type": "PlacementCountCondition",
  "Block": "hytale:ritual_stone",
  "Value": 4,
  "LessThan": false,
  "Then": {
    "Type": "Serial",
    "Interactions": [
      { "Type": "SpawnPrefab", "PrefabId": "ritual_boss" },
      { "Type": "Simple", "Effects": { "WorldSoundEventId": "ritual_complete" } }
    ]
  },
  "Else": {
    "Type": "SendMessage",
    "Message": "Place 4 ritual stones to summon the boss!"
  }
}
```

### Related Interactions

- [BlockCondition](#blockcondition) - Check block type at position
- [Block Interactions](interactions-world.md#block-interactions) - Break or place blocks

---

## Repeat

**Package:** `config/none/RepeatInteraction`

Loop execution of interactions with timing control and optional interruption.

### Structure

```json
{
  "Type": "Repeat",
  "Repeat": 3,
  "RunTime": 0.5,
  "ForkInteractions": {
    "Interactions": [
      {
        "Type": "DamageEntity",
        "DamageParameters": { "DamageAmount": 5 }
      }
    ]
  },
  "Next": {
    "Type": "SendMessage",
    "Message": "Repeat complete"
  }
}
```

### Properties

| Property | Type | Description |
|----------|------|-------------|
| `Repeat` | int | Number of repetitions. Use `-1` for indefinite looping until interrupted |
| `RunTime` | float | Duration of each iteration in seconds |
| `ForkInteractions` | object | Contains `Interactions` array to execute each iteration |
| `Next` | interaction | Interaction to execute after all repetitions complete |
| `HorizontalSpeedMultiplier` | float | Movement speed modifier during repeat (e.g., `0.6` for 60% speed) |
| `Rules` | object | Contains `InterruptedBy` array for early termination |
| `Failed` | interaction | Handler when repeat cannot continue or is interrupted |

### Examples

**Whirlwind Attack (speed-modified combat loop):**

```json
{
  "Type": "Repeat",
  "Repeat": 10,
  "HorizontalSpeedMultiplier": 0.6,
  "ForkInteractions": {
    "Interactions": [
      "Whirlwind_Spin_Effect",
      "Whirlwind_Damage_Selector"
    ]
  }
}
```

**Interruptible Reload (indefinite loop):**

```json
{
  "Type": "Repeat",
  "Repeat": -1,
  "Rules": {
    "InterruptedBy": ["Primary", "Secondary"]
  },
  "ForkInteractions": {
    "Interactions": [
      { "Type": "AddStat", "StatId": "Ammo", "Amount": 1 }
    ]
  },
  "Failed": {
    "Type": "SendMessage",
    "Message": "Reload interrupted"
  }
}
```

**Rapid Strikes (timed iterations):**

```json
{
  "Type": "Repeat",
  "Repeat": 4,
  "RunTime": 0.138,
  "ForkInteractions": {
    "Interactions": [
      "Stab_Left",
      "Stab_Right"
    ]
  }
}
```

### Notes

- Without the `Repeat` property specified, acts as a single fork that waits for completion
- Can be nested within other flow interactions for complex multi-level repetition patterns
- `Rules.InterruptedBy` accepts input names like `"Primary"`, `"Secondary"` to allow player input to break the loop

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
