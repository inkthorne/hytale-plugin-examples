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
| `Type` | string | Required | - | Always `"RunRoot"` |
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
      "Type": "RunRoot",
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
        "Type": "RunRoot",
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
        "Type": "RunRoot",
        "RootInteraction": "Root_NPC_Attack_Melee"
      }
    },
    {
      "Weight": 1,
      "Interaction": {
        "Type": "RunRoot",
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
            "Type": "RunRoot",
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

> **Inheritance:** `WieldingInteraction` extends `ChargingInteraction`, which is why properties like `RunTime`, `FailOnDamage`, `allowIndefiniteHold`, and `cancelOnOtherClick` are available.

| Property | Description |
|----------|-------------|
| `AngledWielding` | Configures angle-based damage reduction |
| `DamageModifiers` | Reduce incoming damage by type (0 = full block, 0.5 = 50% reduction) |
| `StaminaCost` | Stamina consumption when blocking |
| `BlockedEffects` | Visual/audio effects when block succeeds |
| `BlockedInteractions` | Interactions to trigger on successful block |
| `Failed` | What happens when guard breaks (stamina depleted) |
| `RunTime` | Fixed duration in seconds (omit for hold-to-block) |
| `FailOnDamage` | If `true`, interaction ends when hit (even if blocked) |
| `allowIndefiniteHold` | If `true`, block can be held indefinitely (default behavior) |
| `cancelOnOtherClick` | If `true`, interaction cancels when another input is pressed |

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
