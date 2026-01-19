# NPC Roles

This document covers NPC role asset definitions, including templates, variants, behaviors, and spawning configurations. These assets are found in `Assets.zip` under `Server/NPC/`.

> **See also:** [NPC API](npc.md) for plugin events and sensor systems

---

## Directory Structure

The NPC system is organized into several directories:

| Directory | Description |
|-----------|-------------|
| `Server/NPC/Roles/` | 897 NPC role definitions (templates and variants) |
| `Server/NPC/Attitude/` | Relationship definitions between NPC groups |
| `Server/NPC/Groups/` | NPC group collections for spawning |
| `Server/NPC/Flocks/` | Flock size configurations |
| `Server/NPC/Spawn/` | Spawn beacon configurations |
| `Server/NPC/Balancing/` | Combat Action Evaluator (CAE) files |
| `Server/NPC/DecisionMaking/` | AI decision conditions |

---

## Role Types

### Abstract Templates

Templates define common behaviors and properties that concrete NPCs inherit from. Found in `Server/NPC/Roles/_Core/Templates/`.

**Core Templates:**

| Template | Description |
|----------|-------------|
| `Template_Predator` | Base predator behavior with hunting AI |
| `Template_Birds_Passive` | Flying passive bird behavior |
| `Template_Birds_Aggressive` | Flying aggressive bird behavior |
| `Template_Aquatic_Surface` | Surface-swimming creature |
| `Template_Aquatic_Submerged` | Underwater creature |
| `Template_Critter` | Small passive creatures |

**Faction Templates:**

| Template | Description |
|----------|-------------|
| `Template_Goblin` | Goblin faction base |
| `Template_Trork` | Trork faction base |
| `Template_Kweebec` | Kweebec faction base |
| `Template_Feran` | Feran faction base |
| `Template_Scarak` | Scarak faction base |
| `Template_Outlander` | Outlander faction base |

### Variants

Variants are concrete NPC definitions that inherit from templates. They use `Reference` to specify the parent template and `Modify` to override properties.

**Example: Cow (Simple Variant)**

```json
{
    "Reference": "Creature/Livestock/_Core/Template_Livestock",
    "Modify": {
        "Appearance": "Cow/Cow",
        "MaxHealth": {
            "Value": 20,
            "Description": "Default health"
        },
        "NameTranslationKey": "npc.cow",
        "DropList": "Creature/Livestock/Cow"
    }
}
```

**Example: Goblin Scrapper (Complex Variant)**

```json
{
    "Reference": "Intelligent/Aggressive/Goblin/_Core/Template_Goblin_Melee",
    "Modify": {
        "Appearance": "Goblin/Goblin_Scrapper",
        "MaxHealth": {
            "Value": 30,
            "Description": "Combat NPC health"
        },
        "Attack": "Goblin_Scrapper_Attack",
        "AttackDistance": 2.5,
        "CombatActionEvaluator": "Intelligent/CAE_Goblin_Scrapper",
        "Instructions": {
            "Reference": "Intelligent/Aggressive/Goblin/_Core/Component_Instruction_Goblin_Combat"
        }
    }
}
```

---

## Parameters System

NPCs use a parameters system for configurable values with descriptions and computed expressions.

### Value and Description

```json
{
    "MaxHealth": {
        "Value": 100,
        "Description": "Maximum health points"
    },
    "MaxSpeed": {
        "Value": 5.0,
        "Description": "Movement speed in blocks per second"
    }
}
```

### Computed Values

Use `Compute` for runtime-evaluated expressions:

```json
{
    "FlockSize": {
        "Compute": "!isEmptyStringArray(FlockArray)",
        "Description": "Whether NPC is part of a flock"
    },
    "ViewRange": {
        "Compute": "BaseViewRange * AlertnessMultiplier",
        "Description": "Dynamic view range based on alertness"
    }
}
```

---

## Key Properties

### Basic Properties

| Property | Type | Description |
|----------|------|-------------|
| `Appearance` | String | Visual model path |
| `MaxHealth` | Number | Maximum health points |
| `DropList` | String | Loot table reference |
| `NameTranslationKey` | String | Localization key for NPC name |
| `Scale` | Number | Size multiplier |

### Movement Properties

| Property | Type | Description |
|----------|------|-------------|
| `MotionControllerList` | Array | Movement controllers (Walk, Fly, Swim) |
| `MaxSpeed` | Number | Maximum movement speed |
| `WanderRadius` | Number | Random movement range from home |
| `ClimbHeight` | Number | Maximum block height for climbing |
| `JumpHeight` | Number | Vertical jump capability |

### Detection Properties

| Property | Type | Description |
|----------|------|-------------|
| `ViewRange` | Number | Visual detection distance |
| `ViewSector` | Number | Field of view angle (degrees) |
| `HearingRange` | Number | Audio detection distance |
| `AbsoluteDetectionRange` | Number | Always-detect distance |

### Combat Properties

| Property | Type | Description |
|----------|------|-------------|
| `AttitudeGroup` | String | NPC's attitude group membership |
| `DefaultPlayerAttitude` | String | Default stance toward players |
| `Attack` | String | Attack definition reference |
| `AttackDistance` | Number | Melee attack range |
| `CombatActionEvaluator` | String | CAE file for intelligent combat |

### Behavior Properties

| Property | Type | Description |
|----------|------|-------------|
| `StartState` | String | Initial AI state |
| `AlwaysSleepAtNight` | Boolean | Force sleep during night |
| `DayTimePeriod` | String | Active time period |
| `Instructions` | Object | Behavior tree definition |

### Memory Properties

| Property | Type | Description |
|----------|------|-------------|
| `IsMemory` | Boolean | Whether NPC has memory system |
| `MemoriesCategory` | String | Memory type category |

---

## Attitude System

Attitudes define relationships between NPC groups. Found in `Server/NPC/Attitude/`.

### Attitude Types

| Attitude | Description |
|----------|-------------|
| `Friendly` | Allied groups, will not attack |
| `Hostile` | Will attack on sight |
| `Ignore` | Neutral, neither ally nor enemy |
| `Revered` | Special respect (leaders, chieftains) |

### Attitude Definition Example

```json
{
    "Type": "AttitudeGroup",
    "AttitudeGroup": "Predators",
    "Attitudes": {
        "Prey": "Hostile",
        "Predators": "Ignore",
        "Player": "Hostile"
    }
}
```

### Role-Based Attitudes

```json
{
    "Type": "AttitudeRoles",
    "Roles": [
        "Creature/Mammal/Wolf",
        "Creature/Mammal/Bear"
    ],
    "Attitudes": {
        "Livestock": "Hostile",
        "Critters": "Hostile"
    }
}
```

---

## NPC Categories

NPCs are organized into a hierarchical category structure:

### Creature

| Subcategory | Examples |
|-------------|----------|
| `Critter/` | Squirrel, Rabbit, Butterfly |
| `Livestock/` | Cow, Sheep, Pig, Chicken |
| `Mammal/` | Wolf, Bear, Deer |
| `Mythic/` | Unique fantasy creatures |
| `Reptile/` | Lizards, Snakes |
| `Vermin/` | Rats, Spiders |

### Aquatic

| Subcategory | Examples |
|-------------|----------|
| `Abyssal/` | Deep sea creatures |
| `Freshwater/` | River and lake fish |
| `Marine/` | Ocean creatures |

### Avian

| Subcategory | Examples |
|-------------|----------|
| `Aerial/` | Flying birds |
| `Fowl/` | Ground birds |
| `Raptor/` | Birds of prey |

### Intelligent

**Aggressive Factions:**

| Faction | Description |
|---------|-------------|
| `Goblin/` | Goblins and variants (Scrapper, Archer, Shaman) |
| `Outlander/` | Human outlaws |
| `Scarak/` | Insectoid faction |
| `Trork/` | Pig-like warriors |

**Neutral Factions:**

| Faction | Description |
|---------|-------------|
| `Feran/` | Beast-folk traders |
| `Kweebec/` | Small forest dwellers |
| `Tuluk/` | Nomadic traders |

### Other Categories

| Category | Description |
|----------|-------------|
| `Elemental/Golem/` | Stone and element golems |
| `Elemental/Spirit/` | Elemental spirits |
| `Undead/` | Zombies, skeletons, ghosts |
| `Void/` | Void creatures |
| `Boss/` | Boss encounters |

---

## Motion Controllers

Motion controllers define how NPCs move through the world.

### Controller Types

| Controller | Description |
|------------|-------------|
| `Walk` | Ground-based movement with pathfinding |
| `Fly` | Aerial movement with 3D pathfinding |
| `Swim` | Aquatic movement (surface or submerged) |

### Configuration Example

```json
{
    "MotionControllerList": [
        {
            "Type": "Walk",
            "MaxSpeed": 5.0,
            "Acceleration": 10.0,
            "TurnSpeed": 180.0
        }
    ]
}
```

### Flying NPC Example

```json
{
    "MotionControllerList": [
        {
            "Type": "Fly",
            "MaxSpeed": 8.0,
            "HoverHeight": 5.0,
            "AscentSpeed": 3.0,
            "DescentSpeed": 4.0
        }
    ]
}
```

---

## Behavior System (Instructions)

The behavior system uses a state machine with instruction trees for AI decision-making.

### States

| State | Description |
|-------|-------------|
| `Idle` | Default passive state |
| `Sleep` | Sleeping/inactive |
| `Alerted` | Noticed potential threat |
| `Combat` | Active combat engagement |
| `Search` | Looking for lost target |
| `ReturnHome` | Returning to spawn area |
| `Flee` | Running from threat |
| `Panic` | Panicked escape behavior |

### Sensors

Sensors detect game state changes:

| Sensor | Description |
|--------|-------------|
| `State` | Current AI state |
| `Any` | Matches any condition |
| `Target` | Target acquisition/loss |
| `Damage` | Received damage |
| `Player` | Player proximity |
| `Mob` | Other NPC proximity |
| `Leash` | Distance from home |
| `Time` | Time of day |
| `Health` | Health thresholds |

### Actions

| Action | Description |
|--------|-------------|
| `State` | Change AI state |
| `PlayAnimation` | Trigger animation |
| `Attack` | Execute attack |
| `JoinFlock` | Join nearby flock |
| `Timeout` | Wait for duration |
| `Sound` | Play sound effect |

### BodyMotion

| Motion | Description |
|--------|-------------|
| `Wander` | Random movement |
| `Seek` | Move toward target |
| `Flee` | Move away from target |
| `MaintainDistance` | Keep distance from target |
| `Nothing` | Stand still |

### Instruction Tree Example

```json
{
    "Instructions": {
        "Type": "Selector",
        "Children": [
            {
                "Type": "Sequence",
                "Sensor": "Damage",
                "Children": [
                    { "Type": "Action", "Action": "State", "State": "Alerted" },
                    { "Type": "Action", "Action": "Sound", "Sound": "Alert" }
                ]
            },
            {
                "Type": "Sequence",
                "Sensor": "Target",
                "Children": [
                    { "Type": "Action", "Action": "State", "State": "Combat" },
                    { "Type": "BodyMotion", "Motion": "Seek" }
                ]
            },
            {
                "Type": "Leaf",
                "Sensor": "Any",
                "BodyMotion": "Wander"
            }
        ]
    }
}
```

---

## Components

Reusable behavior components allow shared logic across NPCs.

### Sensor Components

```json
{
    "Reference": "_Core/Components/Component_Sensor_PlayerDetection",
    "Modify": {
        "ViewRange": 20
    }
}
```

### Instruction Components

```json
{
    "Reference": "Intelligent/Aggressive/Goblin/_Core/Component_Instruction_Goblin_Combat"
}
```

### Action List Components

```json
{
    "ActionList": {
        "Reference": "_Core/Components/Component_ActionList_FleeOnLowHealth",
        "Modify": {
            "HealthThreshold": 0.25
        }
    }
}
```

---

## Groups

Groups define collections of NPC roles for spawning. Found in `Server/NPC/Groups/`.

### Group Definition

```json
{
    "Type": "NPCGroup",
    "IncludeRoles": [
        "Creature/Livestock/Cow",
        "Creature/Livestock/Sheep",
        "Creature/Livestock/Pig"
    ]
}
```

### Wildcard Patterns

```json
{
    "Type": "NPCGroup",
    "IncludeRoles": [
        "Creature/Critter/*"
    ],
    "ExcludeRoles": [
        "Creature/Critter/Butterfly"
    ]
}
```

---

## Flocks

Flocks configure group sizes for spawned NPCs. Found in `Server/NPC/Flocks/`.

### Fixed Size

```json
{
    "Type": "Flock",
    "Size": 5
}
```

### Size Range

```json
{
    "Type": "Flock",
    "MinSize": 3,
    "MaxSize": 8
}
```

### Weighted Sizes

```json
{
    "Type": "Flock",
    "MinSize": 2,
    "SizeWeights": [
        { "Size": 2, "Weight": 1 },
        { "Size": 3, "Weight": 2 },
        { "Size": 4, "Weight": 3 },
        { "Size": 5, "Weight": 2 },
        { "Size": 6, "Weight": 1 }
    ]
}
```

---

## Spawn Beacons

Spawn beacons configure where and how NPCs spawn in the world. Found in `Server/NPC/Spawn/Beacons/`.

### Beacon Properties

| Property | Type | Description |
|----------|------|-------------|
| `Environments` | Array | Biome/environment filters |
| `MinDistanceFromPlayer` | Number | Minimum player distance for spawning |
| `MaxSpawnedNPCs` | Number | Maximum concurrent spawns |
| `SpawnRadius` | Number | Spawn area radius |
| `BeaconRadius` | Number | Beacon activation radius |
| `NPCs` | Array | NPC spawn definitions |
| `LightRanges` | Object | Light level requirements |

### Beacon Example

```json
{
    "Type": "SpawnBeacon",
    "Environments": ["Cave", "Underground"],
    "MinDistanceFromPlayer": 24,
    "MaxSpawnedNPCs": 5,
    "SpawnRadius": 16,
    "BeaconRadius": 48,
    "NPCs": [
        {
            "Role": "Intelligent/Aggressive/Goblin/Goblin_Scrapper",
            "Weight": 3,
            "Flock": "Group_Small"
        },
        {
            "Role": "Intelligent/Aggressive/Goblin/Goblin_Archer",
            "Weight": 2,
            "Flock": "Group_Small"
        },
        {
            "Role": "Intelligent/Aggressive/Goblin/Goblin_Shaman",
            "Weight": 1
        }
    ],
    "LightRanges": {
        "Min": 0,
        "Max": 7
    }
}
```

### Zone-Based Organization

Spawn beacons are organized by zone:

```
Server/NPC/Spawn/Beacons/
├── Zone1/
│   ├── Zone1_Surface/
│   ├── Zone1_Cave/
│   └── Zone1_Dungeon/
├── Zone2/
├── Zone3/
└── Zone4/
```

---

## Combat Action Evaluator (CAE)

The CAE system provides intelligent combat decision-making. Found in `Server/NPC/Balancing/`.

### CAE Structure

```json
{
    "Type": "CombatActionEvaluator",
    "AvailableActions": {
        "LightAttack": {
            "Animation": "Attack_Light",
            "Damage": 5,
            "Cooldown": 0.5
        },
        "HeavyAttack": {
            "Animation": "Attack_Heavy",
            "Damage": 15,
            "Cooldown": 2.0
        },
        "Block": {
            "Animation": "Block",
            "Duration": 1.5
        }
    },
    "ActionSets": [
        {
            "Name": "Aggressive",
            "Actions": ["LightAttack", "LightAttack", "HeavyAttack"]
        },
        {
            "Name": "Defensive",
            "Actions": ["Block", "LightAttack"]
        }
    ],
    "Conditions": {
        "UseAggressive": {
            "Type": "UtilityCurve",
            "Input": "TargetHealthPercent",
            "Curve": "Linear",
            "Min": 0.5,
            "Max": 1.0
        },
        "UseDefensive": {
            "Type": "UtilityCurve",
            "Input": "SelfHealthPercent",
            "Curve": "InverseLinear",
            "Min": 0.0,
            "Max": 0.3
        }
    }
}
```

### Utility Curves

CAE uses utility curves to evaluate action priorities:

| Curve Type | Description |
|------------|-------------|
| `Linear` | Direct proportion |
| `InverseLinear` | Inverse proportion |
| `Quadratic` | Squared scaling |
| `Sigmoid` | S-curve transition |

### Input Variables

| Variable | Description |
|----------|-------------|
| `SelfHealthPercent` | NPC's current health ratio |
| `TargetHealthPercent` | Target's current health ratio |
| `DistanceToTarget` | Distance in blocks |
| `CombatDuration` | Time in combat |
| `AlliesNearby` | Count of nearby allies |
| `EnemiesNearby` | Count of nearby enemies |

---

## Reference Summary

### Key File Locations

| File Type | Path |
|-----------|------|
| Templates | `Server/NPC/Roles/_Core/Templates/` |
| Creatures | `Server/NPC/Roles/Creature/` |
| Intelligent | `Server/NPC/Roles/Intelligent/` |
| Attitudes | `Server/NPC/Attitude/` |
| Spawn Beacons | `Server/NPC/Spawn/Beacons/` |
| Combat Balance | `Server/NPC/Balancing/` |
| Groups | `Server/NPC/Groups/` |
| Flocks | `Server/NPC/Flocks/` |

### Asset Statistics

| Category | Count | Description |
|----------|-------|-------------|
| Total Roles | 897 | NPC behavior definitions |
| Templates | ~50 | Abstract base templates |
| Attitude Files | ~30 | Relationship definitions |
| Group Files | ~80 | Spawn group collections |
| Spawn Beacons | ~100 | Spawn configurations |
| CAE Files | ~30 | Combat balancing |
