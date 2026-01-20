# Entity & World Interactions

> Part of the [Interactions API](interactions.md). For base interaction properties, see [Reference](interactions.md#reference).

## Quick Navigation

| Interaction | Description |
|-------------|-------------|
| [SpawnPrefab](#spawnprefab) | Spawn entities at locations |
| [RemoveEntity](#removeentity) | Despawn entities from the world |
| [LaunchProjectile](#launchprojectile) | Fire projectiles |
| [SendMessage](#sendmessage) | Send chat messages to players |
| [RunRootInteraction](#runrootinteraction) | Dynamically execute another root interaction |
| [UI Interactions](#ui-interactions) | Open UI pages (OpenPage, OpenCustomUI) |
| [Inventory Interactions](#inventory-interactions) | Manage inventory and equipment |
| [Block Interactions](#block-interactions) | Break or place blocks |
| [ChangeState](#changestate) | Change entity state machine state |
| [LaunchPadInteraction](#launchpadinteraction) | Launch pad physics |
| [WieldingInteraction](#wieldinginteraction) | Blocking and guarding mechanics |

---

## SpawnPrefab

**Package:** `config/server/SpawnPrefabInteraction`

Spawns entities at specified locations.

### Structure

```json
{
  "Type": "SpawnPrefab",
  "PrefabId": "hytale:skeleton",
  "Position": "Target",
  "Count": 1,
  "Offset": [0, 0, 0]
}
```

### Properties

| Property | Type | Description |
|----------|------|-------------|
| `PrefabId` | string | Entity prefab ID to spawn |
| `Position` | string | `Self`, `Target`, or `HitLocation` |
| `Count` | int | Number of entities to spawn |
| `Offset` | [x, y, z] | Position offset from spawn point |
| `SpawnVelocity` | [x, y, z] | Initial velocity for spawned entity |
| `InheritVelocity` | boolean | Inherit spawner's velocity |

### Example: Summon Minions on Ability

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

## RemoveEntity

**Package:** `config/none/RemoveEntityInteraction`

Despawns/removes entities from the world.

### Structure

```json
{
  "Type": "RemoveEntity",
  "Target": "Target",
  "Delay": 0
}
```

### Properties

| Property | Type | Description |
|----------|------|-------------|
| `Target` | string | `Self` or `Target` |
| `Delay` | float | Delay before removal (seconds) |

---

## LaunchProjectile

**Package:** `config/server/LaunchProjectileInteraction`

Fires projectiles from an entity.

### Structure

```json
{
  "Type": "LaunchProjectile",
  "ProjectileId": "hytale:arrow",
  "Speed": 50,
  "SpawnOffset": [0, 1.5, 0.5],
  "AimType": "Forward"
}
```

### Properties

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

## SendMessage

**Package:** `config/none/SendMessageInteraction`

Sends chat messages to players.

### Structure

```json
{
  "Type": "SendMessage",
  "Message": "Critical Hit!",
  "Target": "Self",
  "MessageType": "ActionBar"
}
```

### Properties

| Property | Type | Description |
|----------|------|-------------|
| `Message` | string | Text to display |
| `Target` | string | `Self`, `Target`, or `All` |
| `MessageType` | string | `Chat`, `ActionBar`, or `Title` |

---

## RunRootInteraction

**Package:** `config/none/RunRootInteraction`

**Class hierarchy:** `RunRootInteraction` → `SimpleInstantInteraction` → `SimpleInteraction` → `Interaction`

**Protocol class:** `RunRootInteraction` (handles client-server synchronization)

Dynamically executes another RootInteraction by its string ID. This acts as a redirect/delegation mechanism, allowing one interaction to programmatically invoke a completely different root interaction defined elsewhere. Unlike inline interactions or string references within a chain, RunRootInteraction explicitly triggers a full root interaction with its own cooldowns, settings, and interaction chain.

### Core Properties

| Property | Type | Default | Validator | Description |
|----------|------|---------|-----------|-------------|
| `Type` | string | Required | - | Always `"RunRootInteraction"` |
| `RootInteraction` | string | Required | `nonNull`, late validator | ID of the root interaction to execute |

The `RootInteraction` validator ensures:
1. The property cannot be null or empty (`nonNull`)
2. The ID must reference a valid RootInteraction asset (late validation against `RootInteraction.VALIDATOR_CACHE`)

### How RunRoot Works

When `RunRootInteraction` executes, the following steps occur internally:

1. **State finalization** - Sets the current interaction state to `Finished`
2. **Root lookup** - Retrieves the target RootInteraction using `RootInteraction.getRootInteractionOrUnknown(rootInteraction)`
3. **Execution** - Calls `context.execute(RootInteraction)` to run the referenced root interaction

```
RunRootInteraction executes:
┌─────────────────────────────────────────────────────────────┐
│ Current Chain: Weapon_Special_Ability                       │
│   └─> RunRootInteraction { RootInteraction: "Root_Dodge" }  │
│                    │                                        │
│                    ▼                                        │
│   1. Set state = Finished                                   │
│   2. Lookup "Root_Dodge" from RootInteraction assets        │
│   3. context.execute(Root_Dodge)                            │
│                    │                                        │
│                    ▼                                        │
│ New Chain: Root_Dodge                                       │
│   └─> Interactions: ["Dodge"]                               │
│       └─> Cooldown, RequireNewClick, etc. from Root_Dodge   │
└─────────────────────────────────────────────────────────────┘
```

**Key behavior:** The referenced root interaction executes with its own configuration (cooldowns, settings, rules). This is different from simply referencing an interaction by string ID within a chain, which would use the parent chain's configuration.

### When to Use RunRoot

- **Mode/stance switching** - Switch between different combat modes that have separate root interaction configurations
- **NPC behavior delegation** - AI systems that need to trigger player-style root interactions
- **Plugin integration** - Plugins that define custom root interactions and need to invoke them from other interactions
- **Fallback behavior** - When a condition fails, delegate to a different root interaction entirely
- **Cross-item interactions** - Allow one item's interaction to invoke another item's root interaction pattern

### Complete Examples

**Basic usage - trigger dodge from ability:**

```json
{
  "Type": "Serial",
  "Interactions": [
    {
      "Type": "ApplyEffect",
      "EffectId": "hytale:invulnerability",
      "Duration": 0.5
    },
    {
      "Type": "RunRootInteraction",
      "RootInteraction": "Dodge"
    }
  ]
}
```

This grants brief invulnerability, then executes the Dodge root interaction with all its configured cooldowns and settings.

**Conditional mode switch:**

```json
{
  "Type": "StatsCondition",
  "Stat": "SignatureEnergy",
  "Operator": "GreaterOrEqual",
  "Value": 100,
  "ValueType": "Absolute",
  "Then": {
    "Type": "Serial",
    "Interactions": [
      {
        "Type": "ChangeStat",
        "StatModifiers": { "SignatureEnergy": -100 }
      },
      {
        "Type": "RunRootInteraction",
        "RootInteraction": "Root_Weapon_Sword_Signature_Vortexstrike"
      }
    ]
  },
  "Else": {
    "Type": "SendMessage",
    "Message": "Not enough signature energy!"
  }
}
```

When signature energy is full, consumes it and switches to the signature ability's root interaction.

**NPC attack delegation:**

```json
{
  "Type": "Select",
  "Interactions": [
    {
      "Weight": 3,
      "Interaction": {
        "Type": "RunRootInteraction",
        "RootInteraction": "Root_NPC_Attack_Melee"
      }
    },
    {
      "Weight": 1,
      "Interaction": {
        "Type": "RunRootInteraction",
        "RootInteraction": "Root_NPC_Shield_Block"
      }
    }
  ]
}
```

NPC randomly selects between attack and block behaviors, each with their own root interaction configuration.

**Combo finisher with root switch:**

```json
{
  "Type": "Chaining",
  "ChainId": "Sword_Primary",
  "ChainingAllowance": 2,
  "Next": [
    "Sword_Swing_1",
    "Sword_Swing_2",
    {
      "Type": "FirstClick",
      "Click": "Sword_Swing_3",
      "Held": {
        "Type": "Serial",
        "Interactions": [
          {
            "Type": "RunRootInteraction",
            "RootInteraction": "Root_Weapon_Sword_Secondary_Guard"
          }
        ]
      }
    }
  ]
}
```

On the third combo hit, holding switches to the guard root interaction instead of continuing the attack.

### Common Patterns

| Pattern | Use Case | Implementation |
|---------|----------|----------------|
| **Signature ability** | Full energy triggers special move | StatsCondition → ChangeStat → RunRoot |
| **Stance switch** | Toggle between attack/defense modes | RunRoot to different weapon root |
| **NPC behavior tree** | AI delegates to player-style attacks | Select/Condition → RunRoot |
| **Combo branch** | Final hit branches to different root | Chaining → FirstClick → RunRoot |
| **Fallback action** | Default behavior when main fails | Condition Else → RunRoot |

### Technical Notes

- **Instant execution** - RunRootInteraction extends `SimpleInstantInteraction`, meaning it executes immediately with no duration. The target root interaction then manages its own timing.

- **Context preservation** - The `InteractionContext` is preserved when executing the target root interaction, maintaining entity references, held item info, and meta data.

- **Network synchronization** - The protocol class handles syncing the root interaction ID (as an integer index) between client and server.

- **Cooldown independence** - The target root interaction uses its own cooldown configuration, not the cooldown of the interaction that contains RunRoot.

- **Unknown handling** - If the RootInteraction ID doesn't exist, `getRootInteractionOrUnknown()` returns an "unknown" placeholder rather than crashing, though the late validator should catch this at asset load time.

### Related Interactions

- [Root Interaction Configuration](interactions.md#root-interaction-configuration) - The target type that RunRoot invokes
- [Replace](interactions-flow.md#replace) - Alternative for variable-based interaction substitution within the same chain
- [Serial](interactions-flow.md#serial) - Often wraps RunRoot with setup/teardown interactions
- [StatsCondition](interactions-flow.md#statscondition) - Common guard before RunRoot to check resource availability

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

## ChangeState

**Package:** `config/client/ChangeStateInteraction`

Changes block or entity state machine state. Used for toggleable blocks (torches, lanterns), traps, and temporary state effects.

### Core Properties

| Property | Type | Description |
|----------|------|-------------|
| `Changes` | object | State transition map defining from→to state mappings |
| `Effects` | object | Sound/particle effects triggered on state change |
| `RunTime` | float | Duration in seconds before `Next` interaction executes |
| `Next` | interaction | Chained interaction to execute after `RunTime` |
| `UpdateBlockState` | boolean | Force visual state update after change |

### State Transition Map (Changes)

The `Changes` property defines a mapping where keys are current states and values are target states:

```json
{
  "Changes": {
    "default": "Off",
    "Off": "default"
  }
}
```

This creates a toggle: when in `default` state, transition to `Off`; when in `Off`, transition back to `default`.

**State naming conventions:**
- `default` - The initial/primary state (lit torch, open door)
- `Off` - Disabled/inactive state (extinguished torch)
- `Closed` - For traps and containers
- Custom states defined in block's `State.Definitions`

### Integration with Block State Definitions

ChangeState works with the block's state machine defined in its BlockType configuration:

```json
{
  "State": {
    "Definitions": {
      "On": { "CanProvideSupport": { "Up": true } },
      "Off": { "CanProvideSupport": { "Up": false } }
    }
  }
}
```

Each state in `Definitions` can override block properties like collision, light emission, and support behavior. The `Changes` map references these state names.

### Examples

#### Simple Toggle (Torch)

Basic on/off toggle for a wall torch:

```json
{
  "Type": "ChangeState",
  "Changes": {
    "default": "Off",
    "Off": "default"
  }
}
```

#### Multi-State Transition (Colored Lantern)

Transition any non-default state back to default:

```json
{
  "Type": "ChangeState",
  "Changes": {
    "Off": "default",
    "Blue": "default",
    "Green": "default",
    "Red": "default"
  }
}
```

#### One-Way Transition (Trap)

Irreversible state change for triggered traps:

```json
{
  "Type": "ChangeState",
  "Changes": {
    "default": "Closed"
  }
}
```

#### Timed State Change (Geyser)

Temporary state with automatic reversion using `RunTime` and `Next`:

```json
{
  "Type": "ChangeState",
  "Changes": {
    "default": "Erupting"
  },
  "RunTime": 3,
  "Next": {
    "Type": "ChangeState",
    "Changes": {
      "Erupting": "default"
    }
  }
}
```

The geyser enters `Erupting` state, waits 3 seconds, then returns to `default`.

#### With Sound Effects (Trophy)

State change with audio feedback:

```json
{
  "Type": "ChangeState",
  "Changes": {
    "default": "Off",
    "Off": "default"
  },
  "Effects": {
    "LocalSoundEventId": "hytale:deco.toggle"
  }
}
```

### File Locations

Example assets using ChangeState:
- `data/BlockTypes/Light_Sources/Wood_Torch_Wall.json` - Simple toggle
- `data/BlockTypes/Light_Sources/Lantern_Blue.json` - Multi-state
- `data/BlockTypes/Traps/Survival_Trap_Snapjaw.json` - One-way trap
- `data/BlockTypes/Nature/Prototype_Geyser.json` - Timed with RunTime/Next
- `data/BlockTypes/Decorative/Deco_Trophy_Harvest.json` - Effects property

### Related

- [BlockCondition](interactions-conditions.md#blockcondition) - Check current block state
- [State.Definitions](items-blocks.md#state-definitions) - Define block states and their property overrides

---

## LaunchPadInteraction

**Package:** `config/server/LaunchPadInteraction`

Specialized launch pad physics for bouncing entities.

```json
{
  "Type": "LaunchPad",
  "LaunchVelocity": [0, 20, 0],
  "PreserveHorizontal": true
}
```

---

## WieldingInteraction

**Package:** `config/client/WieldingInteraction`

**Class hierarchy:** `WieldingInteraction` → `ChargingInteraction` → `SimpleInteraction` → `Interaction`

Enables blocking and guarding mechanics for shields and weapons. When active, the player holds a defensive stance that reduces or negates incoming damage based on attack angle. The interaction inherits from ChargingInteraction, providing hold-duration behavior, movement speed reduction, and animation support. Wielding integrates with stamina systems—blocking consumes stamina proportional to damage blocked, and stamina depletion triggers guard break effects.

### Core Properties

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `Type` | string | Required | Always `"Wielding"` |
| `AngledWielding` | object | - | Directional blocking configuration with damage/knockback modifiers |
| `DamageModifiers` | object | - | Direct damage reduction (alternative to AngledWielding for simpler configs) |
| `StaminaCost` | object | - | Stamina consumption per damage blocked |
| `BlockedEffects` | object | - | Visual/audio effects when block succeeds |
| `BlockedInteractions` | object | - | Interactions triggered on successful block |
| `Forks` | object | - | Branching interactions while blocking (e.g., shield bash) |
| `Failed` | object | - | Interactions triggered on guard break (stamina depleted) |
| `Next` | Interaction | - | Interaction to run when guard ends normally |
| `Effects` | object | - | Animation/sound for guard start (inherited from ChargingInteraction) |

**Inherited from ChargingInteraction:**

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `RunTime` | float | - | Maximum duration in seconds (omit for indefinite hold) |
| `AllowIndefiniteHold` | boolean | `true` | If `true`, block can be held indefinitely |
| `CancelOnOtherClick` | boolean | `false` | If `true`, interaction cancels when another input is pressed |
| `FailOnDamage` | boolean | `false` | If `true`, interaction ends when hit (even if blocked) |
| `HorizontalSpeedMultiplier` | float | `1.0` | Movement speed while blocking (0.0-1.0) |
| `DisplayProgress` | boolean | - | Show guard duration indicator |

### File Locations

**Player weapon guards:**
```
Server/Item/Interactions/Weapons/{WeaponType}/Secondary/Guard/*_Guard_Wield.json
```

Weapon types with guard: Sword, Shield, Battleaxe, Daggers, Mace, Crossbow, Shortbow

**NPC blocks:**
```
Server/Item/Interactions/NPCs/{Type}/{NPC}/*_Block.json
```

NPCs like Skeleton Knight, Outlander Brute use simpler block configurations.

**Root interactions:**
```
Server/Item/RootInteractions/Weapons/{WeaponType}/Root_Weapon_{Type}_Secondary_Guard.json
```

### AngledWielding

Controls directional blocking based on attack angle, with separate modifiers for damage and knockback:

```json
"AngledWielding": {
  "Angle": 0,
  "AngleDistance": 90,
  "DamageModifiers": {
    "Physical": 0,
    "Projectile": 0,
    "Poison": 0
  },
  "KnockbackModifiers": {
    "Physical": 0.25,
    "Projectile": 0.25
  }
}
```

| Property | Type | Description |
|----------|------|-------------|
| `Angle` | float | Center angle of the blocking arc (0 = forward) |
| `AngleDistance` | float | Half-width of the blocking arc in degrees |
| `DamageModifiers` | object | Multipliers per damage type (0 = full block, 1 = no reduction) |
| `KnockbackModifiers` | object | Multipliers per damage type for knockback reduction |

**Real values from weapon assets:**

| Weapon | DamageModifiers | KnockbackModifiers | Notes |
|--------|-----------------|--------------------|----|
| Sword | Physical: 0, Projectile: 0, Poison: 0 | Physical: 0.25, Projectile: 0.25 | Full damage block, 75% knockback reduction |
| Shield | Physical: 0, Projectile: 0, Poison: 0 | Physical: 0.25, Projectile: 0.25 | Same as sword |
| Battleaxe | Physical: 0, Projectile: 0, Poison: 0 | Physical: 0.25, Projectile: 0.25 | Heavy weapon guard |
| Unarmed | Physical: 0.8, Projectile: 0.8 | - | 20% damage reduction only |
| NPC Skeleton Knight | Physical: 0.2, Projectile: 0.2 | - | 80% damage reduction |
| NPC Outlander Brute | Physical: 0, Projectile: 0 | - | Full block |

### DamageModifiers (Top-Level)

For simpler configurations (commonly used by NPCs), damage modifiers can be specified at the top level instead of inside AngledWielding:

```json
{
  "Type": "Wielding",
  "DamageModifiers": {
    "Physical": 0.2,
    "Projectile": 0.2
  }
}
```

This format blocks from all angles with uniform damage reduction.

### Forks (Guard Branching)

The `Forks` system allows branching to different interactions while blocking is active. This enables mechanics like shield bash (primary click during guard).

```json
"Forks": {
  "Primary": {
    "Type": "Replace",
    "Variable": "Weapon",
    "Next": "Guard_Bash"
  }
}
```

| Fork Key | Trigger | Common Use |
|----------|---------|------------|
| `Primary` | Primary click while blocking | Shield bash, guard counter |
| `Secondary` | Secondary click while blocking | Alternate guard action |

**Shield Bash Pattern:**

The Primary fork typically uses Replace to select the correct bash animation based on weapon type:

```json
"Forks": {
  "Primary": {
    "Type": "Replace",
    "Variable": "Weapon",
    "Next": {
      "Sword": "Sword_Guard_Bash",
      "Shield": "Shield_Guard_Bash",
      "Battleaxe": "Battleaxe_Guard_Bash"
    }
  }
}
```

### Effects (Guard Start)

Inherited from ChargingInteraction, the `Effects` object configures the animation and sound when entering guard stance:

```json
"Effects": {
  "ItemAnimationId": "Guard",
  "ClearAnimationOnFinish": true,
  "WorldSoundEventId": "SFX_Shield_T2_Raise",
  "LocalSoundEventId": "SFX_Shield_T2_Raise_Local"
}
```

| Property | Type | Description |
|----------|------|-------------|
| `ItemAnimationId` | string | Animation to play on held item when guard starts |
| `ClearAnimationOnFinish` | boolean | Stop animation when guard ends |
| `WorldSoundEventId` | string | Sound event audible to nearby players |
| `LocalSoundEventId` | string | Sound event only the blocking player hears |

### StaminaCost

Stamina consumption when blocking damage:

```json
"StaminaCost": {
  "CostType": "Damage",
  "Value": 7
}
```

| Property | Type | Description |
|----------|------|-------------|
| `CostType` | string | `"Damage"` = cost per point of damage blocked |
| `Value` | float | Stamina consumed per damage point blocked |

**Real values:** Most weapons use `Value: 7` for their guard stamina cost.

### BlockedEffects

Effects triggered on each successful block (sounds, particles):

```json
"BlockedEffects": {
  "WorldSoundEventId": "SFX_Shield_T2_Impact",
  "LocalSoundEventId": "SFX_Shield_T2_Impact_Local",
  "WorldParticles": [
    { "SystemId": "Shield_Block" }
  ]
}
```

### BlockedInteractions

Interactions triggered when a block succeeds. This enables mechanics like:
- Granting signature energy on successful blocks
- Applying knockback to attackers
- Setting chain flags for counter-attack windows

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
      "Type": "ChainFlag",
      "ChainId": "Sword_Combat",
      "Flag": "Counter_Ready"
    }
  ]
}
```

**Parry Example (from Debug_Stick_Parry):**

A parry is a short-duration wielding that triggers special interactions on block:

```json
{
  "Type": "Wielding",
  "RunTime": 0.3,
  "AngledWielding": {
    "Angle": 0,
    "AngleDistance": 180,
    "DamageModifiers": { "Physical": 0 }
  },
  "BlockedInteractions": {
    "Interactions": [
      {
        "Type": "SendMessage",
        "Message": "Perfect Parry!"
      },
      {
        "Type": "ApplyForce",
        "Target": "Attacker",
        "Force": [0, 5, -15]
      },
      {
        "Type": "ChainFlag",
        "ChainId": "Debug_Combat",
        "Flag": "Parry_Counter"
      }
    ]
  }
}
```

### Failed (Guard Break)

Interactions triggered when stamina is depleted while blocking:

```json
"Failed": {
  "Interactions": [
    {
      "Type": "ChangeState",
      "StateId": "Staggered",
      "Duration": 1.0
    },
    {
      "Type": "PlaySound",
      "SoundId": "SFX_Guard_Break"
    }
  ]
}
```

Guard break typically applies a stagger state, leaving the player vulnerable.

### Next (Post-Guard)

The `Next` property specifies an interaction to run when guard ends normally (not from guard break). Common use: reset stamina regeneration delay.

```json
"Next": {
  "Type": "ChangeStat",
  "Behaviour": "Set",
  "StatModifiers": {
    "StaminaRegenDelay": -1
  }
}
```

This pattern resets the stamina regen delay timer when guard ends, allowing stamina to begin regenerating.

### Complete Examples

**Full Sword Guard Configuration:**

```json
{
  "Type": "Wielding",
  "Effects": {
    "ItemAnimationId": "Guard",
    "ClearAnimationOnFinish": true,
    "WorldSoundEventId": "SFX_Sword_T2_Guard_Raise",
    "LocalSoundEventId": "SFX_Sword_T2_Guard_Raise_Local"
  },
  "AngledWielding": {
    "Angle": 0,
    "AngleDistance": 90,
    "DamageModifiers": {
      "Physical": 0,
      "Projectile": 0,
      "Poison": 0
    },
    "KnockbackModifiers": {
      "Physical": 0.25,
      "Projectile": 0.25
    }
  },
  "StaminaCost": {
    "CostType": "Damage",
    "Value": 7
  },
  "BlockedEffects": {
    "WorldSoundEventId": "SFX_Sword_T2_Impact",
    "LocalSoundEventId": "SFX_Sword_T2_Impact_Local",
    "WorldParticles": [
      { "SystemId": "Sword_Block_Sparks" }
    ]
  },
  "BlockedInteractions": {
    "Interactions": [
      {
        "Type": "ChangeStat",
        "StatModifiers": {
          "SignatureEnergy": 3
        }
      }
    ]
  },
  "Forks": {
    "Primary": {
      "Type": "Replace",
      "Variable": "Weapon",
      "Next": "Sword_Guard_Bash"
    }
  },
  "Failed": {
    "Interactions": [
      {
        "Type": "ChangeState",
        "StateId": "Staggered",
        "Duration": 0.8
      }
    ]
  },
  "Next": {
    "Type": "ChangeStat",
    "Behaviour": "Set",
    "StatModifiers": {
      "StaminaRegenDelay": -1
    }
  }
}
```

**Simple NPC Block:**

```json
{
  "Type": "Wielding",
  "DamageModifiers": {
    "Physical": 0.2,
    "Projectile": 0.2
  },
  "BlockedEffects": {
    "WorldSoundEventId": "SFX_Metal_Block"
  }
}
```

**Timed Parry Window:**

```json
{
  "Type": "Wielding",
  "RunTime": 0.25,
  "AllowIndefiniteHold": false,
  "AngledWielding": {
    "Angle": 0,
    "AngleDistance": 120,
    "DamageModifiers": { "Physical": 0 }
  },
  "Effects": {
    "ItemAnimationId": "Parry_Start",
    "ClearAnimationOnFinish": true
  },
  "BlockedInteractions": {
    "Interactions": [
      {
        "Type": "Serial",
        "Interactions": [
          {
            "Type": "ApplyForce",
            "Target": "Attacker",
            "Force": [0, 3, -12]
          },
          {
            "Type": "ChangeState",
            "Target": "Attacker",
            "StateId": "Staggered",
            "Duration": 0.5
          },
          {
            "Type": "ChainFlag",
            "ChainId": "Combat",
            "Flag": "Perfect_Parry"
          }
        ]
      }
    ]
  },
  "Failed": {
    "Interactions": [
      {
        "Type": "SendMessage",
        "Message": "Parry missed!"
      }
    ]
  }
}
```

### Common Patterns

| Pattern | Key Properties | Use Case |
|---------|----------------|----------|
| **Standard Guard** | `AngledWielding` + `StaminaCost` + `Forks.Primary` | Sword/shield blocking with bash option |
| **Simple NPC Block** | `DamageModifiers` only | Basic AI blocking |
| **Parry Window** | `RunTime: 0.25`, `BlockedInteractions` with counter | Timing-based defensive option |
| **Energy-Building Block** | `BlockedInteractions` with `ChangeStat` | Blocking charges signature meter |
| **Counter Setup** | `BlockedInteractions` with `ChainFlag` | Successful block unlocks counter-attack |

### Technical Notes

- **Inheritance** - WieldingInteraction inherits all properties from ChargingInteraction, including movement speed modifiers, progress display, and the `Next` map system. However, Wielding typically uses `AllowIndefiniteHold: true` by default.

- **Stamina Integration** - When `StaminaCost` is configured with `CostType: "Damage"`, each point of damage blocked consumes `Value` stamina. When stamina reaches zero, the `Failed` branch triggers.

- **Directional Blocking** - The `Angle` and `AngleDistance` create a blocking arc. Attacks from within this arc apply `DamageModifiers`; attacks from outside bypass the block entirely.

- **Forks Execution** - When a Fork triggers (e.g., Primary click during guard), the Wielding interaction ends and the forked interaction executes. The guard does not resume automatically.

- **Guard Break Recovery** - The `Failed` interactions should include a state change (stagger/stun) that prevents immediate re-blocking, creating a vulnerability window.

### Related Interactions

- [ChargingInteraction](interactions-combo.md#charginginteraction) - Parent class providing hold-duration behavior
- [ChainFlagInteraction](interactions-combo.md#chainflaginteraction) - Set flags from BlockedInteractions for counter-attack systems
- [ChangeState](interactions-world.md#changestate) - Used in Failed for guard break stagger
- [ChangeStat](interactions-stat.md#changestat) - Modify stamina, signature energy on block
- [Replace](interactions-flow.md#replace) - Used in Forks for weapon-specific bash attacks
