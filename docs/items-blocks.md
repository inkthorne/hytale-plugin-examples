# Block Items

> Part of the [Items API](items.md). For common item properties, see [Items Reference](items.md#common-properties).
>
> **See also:** [Drop System](drops.md) for container loot tables

## Quick Navigation

| Category | Count | Examples | Key Features |
|----------|-------|----------|--------------|
| [Basic Blocks](#basic-blocks) | 100+ | Rock_Stone, Cloth_Block_Wool_Blue | DrawType: Cube, Textures |
| [Furniture & Lighting](#furniture--lighting) | 50+ | Torch, Lantern, Candle | Light, Particles, On/Off states |
| [Doors & Ladders](#doors--ladders) | 20+ | Furniture_Crude_Door, Ladder | IsDoor, IsClimbable, ConnectedBlockRuleSet |
| [Containers](#containers) | 15+ | Chest_Small, Chest_Large | State: container, Capacity |
| [Crafting Benches](#crafting-benches) | 10+ | Furnace, WorkBench | Bench config, Processing |
| [Farming Blocks](#farming-blocks) | 20+ | Template_Crop_Block | Farming stages, Growth modifiers |

---

## BlockType Properties

Items with placeable blocks define their block configuration in the `BlockType` property. This section covers all core BlockType properties.

### Core Properties

| Property | Type | Description |
|----------|------|-------------|
| `Material` | string | Block material type: `Solid` (collision) or `Empty` (no collision) |
| `DrawType` | string | How the block is rendered: `Cube`, `Model`, or `Invisible` |
| `Opacity` | string | Light blocking: `Opaque`, `Transparent`, or `Translucent` |
| `BlockSoundSetId` | string | Sound set for footsteps and impacts |
| `BlockParticleSetId` | string | Particle set for breaking effects |

### Material

Determines collision behavior:

```json
{
  "BlockType": {
    "Material": "Solid"
  }
}
```

| Material | Description |
|----------|-------------|
| `Empty` | No collision, entities pass through |
| `Solid` | Full collision, blocks entity movement |

### DrawType

Controls block rendering method:

```json
{
  "BlockType": {
    "DrawType": "Cube"
  }
}
```

| DrawType | Description | Use Case |
|----------|-------------|----------|
| `Cube` | Standard 6-face cube | Rock, dirt, wood blocks |
| `Model` | Custom 3D model | Furniture, torches, plants |
| `Invisible` | No rendering | Trigger blocks, air |

### Opacity

Controls light transmission:

```json
{
  "BlockType": {
    "Opacity": "Transparent"
  }
}
```

| Opacity | Description |
|---------|-------------|
| `Opaque` | Blocks all light |
| `Transparent` | Allows full light through |
| `Translucent` | Partially blocks light |

---

## Textures & Models

### Cube Textures

For `DrawType: Cube`, textures are defined per face:

```json
{
  "BlockType": {
    "DrawType": "Cube",
    "Textures": [
      {
        "Face": "All",
        "Texture": "Blocks/Stone/Rock_Stone.png"
      }
    ]
  }
}
```

**Face Values:**

| Face | Description |
|------|-------------|
| `All` | Apply to all faces |
| `Top` | Top face (+Y) |
| `Bottom` | Bottom face (-Y) |
| `North`, `South`, `East`, `West` | Side faces |
| `Sides` | All four side faces |

**Multi-Face Example (Grass Block):**

```json
{
  "BlockType": {
    "DrawType": "Cube",
    "Textures": [
      { "Face": "Top", "Texture": "Blocks/Soil/Grass_Top.png" },
      { "Face": "Bottom", "Texture": "Blocks/Soil/Dirt.png" },
      { "Face": "Sides", "Texture": "Blocks/Soil/Grass_Side.png" }
    ]
  }
}
```

### Custom Models

For `DrawType: Model`, specify a 3D model:

```json
{
  "BlockType": {
    "DrawType": "Model",
    "CustomModel": "Blocks/Furniture/Torch.blockymodel",
    "CustomModelScale": 1.0,
    "CustomModelTexture": [
      { "Texture": "Blocks/Furniture/Torch_Texture.png" }
    ]
  }
}
```

| Property | Type | Description |
|----------|------|-------------|
| `CustomModel` | string | Path to .blockymodel file |
| `CustomModelScale` | float | Model scale multiplier |
| `CustomModelAnimation` | string | Default animation to play |
| `CustomModelTexture` | array | Texture(s) for the model |

---

## Light Emission

Blocks can emit colored light:

```json
{
  "BlockType": {
    "Light": {
      "Color": "#FFAA44",
      "Radius": 12
    }
  }
}
```

| Property | Type | Description |
|----------|------|-------------|
| `Color` | string | Hex color of emitted light |
| `Radius` | int | Light range in blocks (0-15) |

**Shorthand:**

```json
{
  "BlockType": {
    "LightEmission": 12
  }
}
```

Uses default warm light color with specified radius.

---

## Particles

Attach particle systems to blocks:

```json
{
  "BlockType": {
    "Particles": [
      {
        "SystemId": "Torch_Flame",
        "TargetNodeName": "Flame_Point",
        "Scale": 1.0,
        "Color": "#FF8800"
      }
    ]
  }
}
```

| Property | Type | Description |
|----------|------|-------------|
| `SystemId` | string | Particle system ID |
| `TargetNodeName` | string | Model node to attach to |
| `Scale` | float | Particle scale multiplier |
| `Color` | string | Particle tint color |
| `PositionOffset` | object | Position offset from node |

---

## Rotation & Placement

### VariantRotation

Controls automatic rotation variants:

```json
{
  "BlockType": {
    "VariantRotation": "NESW"
  }
}
```

| VariantRotation | Description |
|-----------------|-------------|
| `None` | No rotation variants |
| `NESW` | 4 rotations (North, East, South, West) |
| `YawStep1` | 90-degree increments on yaw |

### PlacementSettings

Configure how blocks are placed:

```json
{
  "BlockType": {
    "PlacementSettings": {
      "RotationMode": "BlockNormal",
      "PlaceInEmptyBlocks": true,
      "AllowRotationKey": true,
      "WallPlacementOverrideBlockId": "Wood_Torch_Wall",
      "CeilingPlacementOverrideBlockId": "Wood_Torch_Ceiling",
      "FloorPlacementOverrideBlockId": "Wood_Torch_Floor"
    }
  }
}
```

| Property | Type | Description |
|----------|------|-------------|
| `RotationMode` | string | How rotation is determined on placement |
| `PlaceInEmptyBlocks` | boolean | Allow placement in air blocks |
| `AllowRotationKey` | boolean | Allow player to rotate with key |
| `WallPlacementOverrideBlockId` | string | Different block when placed on wall |
| `CeilingPlacementOverrideBlockId` | string | Different block when placed on ceiling |
| `FloorPlacementOverrideBlockId` | string | Different block when placed on floor |

**RotationMode Values:**

| RotationMode | Description |
|--------------|-------------|
| `None` | No automatic rotation |
| `BlockNormal` | Align to surface normal |
| `YawStep1` | Rotate based on player yaw |
| `PlayerFacing` | Face toward player |

---

## Support System

Blocks can require support from neighboring blocks to remain placed.

### Directional Support

```json
{
  "BlockType": {
    "Support": {
      "Down": [{ "FaceType": "Full" }],
      "Up": [{ "FaceType": "Full" }]
    }
  }
}
```

**Support Directions:**

| Direction | Description |
|-----------|-------------|
| `Down` | Block below (-Y) |
| `Up` | Block above (+Y) |
| `North`, `South`, `East`, `West` | Adjacent blocks |

### FaceType Support

```json
{
  "BlockType": {
    "Support": {
      "Down": [{ "FaceType": "Full" }]
    }
  }
}
```

| FaceType | Description |
|----------|-------------|
| `Full` | Full solid face required |
| `Branch` | Branch-shaped support |
| `Fence` | Fence-type support |
| `Wall` | Wall-type support |
| `Beam` | Beam-type support |
| `Rope` | Rope connection |

### Tag-Based Support

Support based on block tags:

```json
{
  "BlockType": {
    "Support": {
      "Down": [{
        "Tags": {
          "Type": "Soil",
          "SubType": "Planter"
        }
      }]
    }
  }
}
```

### BlockTypeId Support

Support from specific block types:

```json
{
  "BlockType": {
    "Support": {
      "Down": [{ "BlockTypeId": "Soil_Dirt_Tilled" }]
    }
  }
}
```

### SupportDropType

What happens when support is lost:

```json
{
  "BlockType": {
    "SupportDropType": "Drop"
  }
}
```

| SupportDropType | Description |
|-----------------|-------------|
| `Drop` | Drop as item |
| `Destroy` | Destroy without drop |
| `None` | Remain floating |

---

## Block States

Blocks can have multiple states (on/off, open/close, growth stages).

### State Definition

```json
{
  "BlockType": {
    "State": {
      "StateType": "OnOff",
      "DefaultState": "On",
      "States": {
        "On": {
          "Light": { "Color": "#FFAA44", "Radius": 12 },
          "Particles": [{ "SystemId": "Torch_Flame" }],
          "CustomModel": "Blocks/Furniture/Torch_Lit.blockymodel"
        },
        "Off": {
          "Light": null,
          "Particles": [],
          "CustomModel": "Blocks/Furniture/Torch_Unlit.blockymodel"
        }
      }
    }
  }
}
```

| Property | Type | Description |
|----------|------|-------------|
| `StateType` | string | Type of state system |
| `DefaultState` | string | Initial state when placed |
| `States` | object | State-specific property overrides |

**Common StateTypes:**

| StateType | States | Use Case |
|-----------|--------|----------|
| `OnOff` | On, Off | Torches, lanterns |
| `OpenClose` | Open, Close | Doors, chests |
| `Growth` | Stage_0 through Stage_N | Crops |
| `Container` | Empty, Filled | Storage |

### State-Specific Properties

States can override any BlockType property:

```json
{
  "States": {
    "Open": {
      "CustomModel": "Blocks/Door_Open.blockymodel",
      "Material": "Empty",
      "HitboxType": "None"
    },
    "Close": {
      "CustomModel": "Blocks/Door_Closed.blockymodel",
      "Material": "Solid",
      "HitboxType": "Door"
    }
  }
}
```

---

## Connected Blocks

Blocks that visually connect to neighbors (chests, fences, doors).

### ConnectedBlockRuleSet

```json
{
  "BlockType": {
    "ConnectedBlockRuleSet": {
      "TemplateShapeAssetId": "ChestConnectedBlockTemplate",
      "MergeMode": "Horizontal",
      "MaxMergeCount": 2
    }
  }
}
```

| Property | Type | Description |
|----------|------|-------------|
| `TemplateShapeAssetId` | string | Template defining connection rules |
| `MergeMode` | string | How blocks merge: `Horizontal`, `Vertical`, `All` |
| `MaxMergeCount` | int | Maximum blocks that can merge |

**Double Chest Example:**

Small chests merge horizontally into double chests:

```json
{
  "BlockType": {
    "ConnectedBlockRuleSet": {
      "TemplateShapeAssetId": "ChestConnectedBlockTemplate",
      "MergeMode": "Horizontal",
      "MaxMergeCount": 2,
      "MergedBlockId": "Furniture_Ancient_Chest_Double"
    }
  }
}
```

---

## Block Interactions

Blocks can define interactions for player use.

### Use Interaction

```json
{
  "BlockType": {
    "Interactions": {
      "Use": "Block_Use_Toggle_OnOff"
    }
  }
}
```

### ChangeState Interaction

Toggle block state:

```json
{
  "Type": "ChangeState",
  "Target": "TargetBlock",
  "States": {
    "On": "Off",
    "Off": "On"
  }
}
```

### Common Block Interactions

| Interaction | Description | Example Block |
|-------------|-------------|---------------|
| `Block_Use_Toggle_OnOff` | Toggle on/off state | Torch, lantern |
| `Block_Use_Open_Container` | Open container UI | Chest |
| `Block_Use_Open_Door` | Toggle door open/close | Door |
| `Block_Use_Craft` | Open crafting UI | Workbench, furnace |

---

## Special Block Properties

### IsDoor

Marks a block as a door for pathfinding and AI:

```json
{
  "BlockType": {
    "IsDoor": true
  }
}
```

### IsClimbable

Marks a block as climbable (ladders, vines):

```json
{
  "BlockType": {
    "MovementSettings": {
      "IsClimbable": true,
      "ClimbSpeed": 4.0
    }
  }
}
```

### Bench Configuration

For crafting stations:

```json
{
  "BlockType": {
    "Bench": {
      "Type": "Crafting",
      "Categories": ["Weapon_Sword", "Tool"],
      "TierLevel": 1,
      "ProcessingTime": 1.0
    }
  }
}
```

| Property | Type | Description |
|----------|------|-------------|
| `Type` | string | Bench type: `Crafting`, `Smelting`, `Cooking` |
| `Categories` | array | Recipe categories this bench supports |
| `TierLevel` | int | Bench tier (1-3) |
| `ProcessingTime` | float | Base crafting speed multiplier |

### Farming Configuration

For crop blocks:

```json
{
  "BlockType": {
    "Farming": {
      "GrowthStages": 5,
      "GrowthTime": 120.0,
      "RequiresWater": true,
      "WaterRadius": 4,
      "HarvestDrops": [
        { "ItemId": "Plant_Crop_Carrot", "Quantity": [1, 3] },
        { "ItemId": "Plant_Crop_Carrot_Seed", "Quantity": [0, 2] }
      ]
    }
  }
}
```

| Property | Type | Description |
|----------|------|-------------|
| `GrowthStages` | int | Number of growth stages |
| `GrowthTime` | float | Base time in seconds per stage |
| `RequiresWater` | boolean | Needs water block nearby |
| `WaterRadius` | int | Maximum water detection distance |
| `HarvestDrops` | array | Items dropped when harvested |

### Container Configuration

For storage blocks:

```json
{
  "BlockType": {
    "State": {
      "StateType": "container"
    },
    "Components": {
      "container": {
        "ItemContainer": {
          "Capacity": 18
        },
        "Droplist": "Drop_Chest_Contents"
      }
    }
  }
}
```

| Property | Type | Description |
|----------|------|-------------|
| `Capacity` | int | Number of inventory slots |
| `Droplist` | string | What drops when destroyed (see [Drop System](drops.md)) |

---

## Basic Blocks

Simple solid cube blocks for building.

### Rock_Stone

**Location:** `Server/Item/Items/Rock/Stone/Rock_Stone.json`

Basic stone block with cube rendering.

```json
{
  "TranslationProperties": {
    "Name": "server.items.Rock_Stone.name"
  },
  "Quality": "Common",
  "MaxStack": 99,
  "Categories": ["Items.Blocks"],
  "BlockType": {
    "Material": "Solid",
    "DrawType": "Cube",
    "Opacity": "Opaque",
    "Textures": [
      { "Face": "All", "Texture": "Blocks/Stone/Rock_Stone.png" }
    ],
    "BlockSoundSetId": "Stone",
    "BlockParticleSetId": "Stone"
  }
}
```

### Cloth_Block_Wool_Blue

**Location:** `Server/Item/Items/Cloth/Wool/Cloth_Block_Wool_Blue.json`

Colored wool block with recipe.

```json
{
  "TranslationProperties": {
    "Name": "server.items.Cloth_Block_Wool_Blue.name"
  },
  "Quality": "Common",
  "MaxStack": 99,
  "Categories": ["Items.Blocks"],
  "BlockType": {
    "Material": "Solid",
    "DrawType": "Cube",
    "Opacity": "Opaque",
    "Textures": [
      { "Face": "All", "Texture": "Blocks/Cloth/Wool_Blue.png" }
    ],
    "BlockSoundSetId": "Cloth",
    "BlockParticleSetId": "Cloth"
  },
  "Recipe": {
    "TimeSeconds": 2.0,
    "Input": [
      { "ItemId": "Ingredient_Wool", "Quantity": 4 },
      { "ItemId": "Ingredient_Dye_Blue", "Quantity": 1 }
    ],
    "BenchRequirement": [{
      "Type": "Crafting",
      "Categories": ["Cloth"],
      "Id": "Loom_Bench"
    }]
  }
}
```

---

## Furniture & Lighting

Decorative blocks with light emission and particle effects.

### Furniture_Crude_Torch

**Location:** `Server/Item/Items/Furniture/Crude/Unique/Furniture_Crude_Torch.json`

Light source with on/off states and wall placement override.

```json
{
  "TranslationProperties": {
    "Name": "server.items.Furniture_Crude_Torch.name"
  },
  "Quality": "Common",
  "MaxStack": 25,
  "Categories": ["Items.Furniture"],
  "BlockType": {
    "Material": "Empty",
    "DrawType": "Model",
    "Opacity": "Transparent",
    "CustomModel": "Blocks/Furniture/Crude/Torch.blockymodel",
    "CustomModelTexture": [
      { "Texture": "Blocks/Furniture/Crude/Torch_Texture.png" }
    ],
    "PlacementSettings": {
      "RotationMode": "BlockNormal",
      "PlaceInEmptyBlocks": true,
      "WallPlacementOverrideBlockId": "Wood_Torch_Wall"
    },
    "Support": {
      "Down": [{ "FaceType": "Full" }]
    },
    "State": {
      "StateType": "OnOff",
      "DefaultState": "On",
      "States": {
        "On": {
          "Light": { "Color": "#FFAA44", "Radius": 12 },
          "Particles": [
            { "SystemId": "Torch_Flame", "TargetNodeName": "Flame" },
            { "SystemId": "Torch_Smoke", "TargetNodeName": "Flame" }
          ]
        },
        "Off": {
          "Light": null,
          "Particles": []
        }
      }
    },
    "Interactions": {
      "Use": "Block_Use_Toggle_OnOff"
    },
    "BlockSoundSetId": "Wood",
    "BlockParticleSetId": "Wood"
  },
  "Recipe": {
    "TimeSeconds": 1.0,
    "Input": [
      { "ResourceTypeId": "Wood_Trunk", "Quantity": 1 },
      { "ItemId": "Ingredient_Coal", "Quantity": 1 }
    ]
  }
}
```

### Wood_Torch_Wall

**Location:** `Server/Item/Items/Wood/Wood_Torch_Wall.json`

Wall-mounted torch variant (placed automatically when torch targets wall).

```json
{
  "TranslationProperties": {
    "Name": "server.items.Wood_Torch_Wall.name"
  },
  "Quality": "Technical",
  "BlockType": {
    "Material": "Empty",
    "DrawType": "Model",
    "CustomModel": "Blocks/Furniture/Crude/Torch_Wall.blockymodel",
    "VariantRotation": "NESW",
    "Support": {
      "West": [{ "FaceType": "Full" }]
    },
    "State": {
      "StateType": "OnOff",
      "DefaultState": "On",
      "States": {
        "On": {
          "Light": { "Color": "#FFAA44", "Radius": 12 },
          "Particles": [{ "SystemId": "Torch_Flame", "TargetNodeName": "Flame" }]
        },
        "Off": {
          "Light": null,
          "Particles": []
        }
      }
    },
    "Interactions": {
      "Use": "Block_Use_Toggle_OnOff"
    }
  }
}
```

### Deco_Lantern

**Location:** `Server/Item/Items/Deco/Deco_Lantern.json`

Ceiling/floor lantern with complex support options.

```json
{
  "TranslationProperties": {
    "Name": "server.items.Deco_Lantern.name"
  },
  "Quality": "Common",
  "MaxStack": 25,
  "Categories": ["Items.Furniture"],
  "BlockType": {
    "Material": "Empty",
    "DrawType": "Model",
    "Opacity": "Transparent",
    "CustomModel": "Blocks/Deco/Lantern_Floor.blockymodel",
    "PlacementSettings": {
      "RotationMode": "YawStep1",
      "AllowRotationKey": true,
      "CeilingPlacementOverrideBlockId": "Deco_Lantern_Ceiling"
    },
    "Support": {
      "Down": [
        { "FaceType": "Full" },
        { "FaceType": "Fence" },
        { "FaceType": "Wall" }
      ]
    },
    "Light": { "Color": "#FFE4AA", "Radius": 14 },
    "Particles": [
      { "SystemId": "Lantern_Glow", "TargetNodeName": "Light_Point" }
    ],
    "BlockSoundSetId": "Metal",
    "BlockParticleSetId": "Metal"
  },
  "Recipe": {
    "TimeSeconds": 3.0,
    "Input": [
      { "ItemId": "Ingredient_Bar_Iron", "Quantity": 2 },
      { "ItemId": "Ingredient_Glass", "Quantity": 1 }
    ],
    "BenchRequirement": [{
      "Type": "Crafting",
      "Categories": ["Furniture"],
      "Id": "Furniture_Bench"
    }]
  }
}
```

---

## Doors & Ladders

Interactive blocks for navigation.

### Furniture_Crude_Door

**Location:** `Server/Item/Items/Furniture/Crude/Furniture_Crude_Door.json`

Door with open/close states and connected block support.

```json
{
  "TranslationProperties": {
    "Name": "server.items.Furniture_Crude_Door.name"
  },
  "Quality": "Common",
  "MaxStack": 10,
  "Categories": ["Items.Furniture"],
  "BlockType": {
    "Material": "Solid",
    "DrawType": "Model",
    "IsDoor": true,
    "CustomModel": "Blocks/Furniture/Crude/Door_Closed.blockymodel",
    "VariantRotation": "NESW",
    "PlacementSettings": {
      "RotationMode": "PlayerFacing"
    },
    "Support": {
      "Down": [{ "FaceType": "Full" }]
    },
    "ConnectedBlockRuleSet": {
      "TemplateShapeAssetId": "DoorConnectedBlockTemplate",
      "MergeMode": "Vertical",
      "MaxMergeCount": 2
    },
    "State": {
      "StateType": "OpenClose",
      "DefaultState": "Close",
      "States": {
        "Open": {
          "Material": "Empty",
          "CustomModel": "Blocks/Furniture/Crude/Door_Open.blockymodel",
          "CustomModelAnimation": "Open"
        },
        "Close": {
          "Material": "Solid",
          "CustomModel": "Blocks/Furniture/Crude/Door_Closed.blockymodel"
        }
      }
    },
    "Interactions": {
      "Use": "Block_Use_Open_Door"
    },
    "BlockSoundSetId": "Wood_Door",
    "BlockParticleSetId": "Wood"
  },
  "Recipe": {
    "TimeSeconds": 2.0,
    "Input": [
      { "ResourceTypeId": "Wood_Trunk", "Quantity": 6 }
    ],
    "BenchRequirement": [{
      "Type": "Crafting",
      "Categories": ["Furniture"],
      "Id": "Furniture_Bench"
    }]
  }
}
```

### Furniture_Ancient_Ladder

**Location:** `Server/Item/Items/Furniture/Ancient/Furniture_Ancient_Ladder.json`

Climbable ladder with BlockNormal rotation.

```json
{
  "TranslationProperties": {
    "Name": "server.items.Furniture_Ancient_Ladder.name"
  },
  "Quality": "Common",
  "MaxStack": 25,
  "Categories": ["Items.Furniture"],
  "BlockType": {
    "Material": "Empty",
    "DrawType": "Model",
    "Opacity": "Transparent",
    "CustomModel": "Blocks/Furniture/Ancient/Ladder.blockymodel",
    "VariantRotation": "NESW",
    "PlacementSettings": {
      "RotationMode": "BlockNormal"
    },
    "Support": {
      "West": [{ "FaceType": "Full" }]
    },
    "MovementSettings": {
      "IsClimbable": true,
      "ClimbSpeed": 4.0
    },
    "ConnectedBlockRuleSet": {
      "TemplateShapeAssetId": "LadderConnectedBlockTemplate",
      "MergeMode": "Vertical"
    },
    "BlockSoundSetId": "Wood",
    "BlockParticleSetId": "Wood"
  },
  "Recipe": {
    "TimeSeconds": 1.5,
    "Input": [
      { "ResourceTypeId": "Wood_Trunk", "Quantity": 3 }
    ]
  }
}
```

---

## Containers

Storage blocks with inventory.

### Furniture_Ancient_Chest_Small

**Location:** `Server/Item/Items/Furniture/Ancient/Furniture_Ancient_Chest_Small.json`

Small chest that merges into double chest.

```json
{
  "TranslationProperties": {
    "Name": "server.items.Furniture_Ancient_Chest_Small.name"
  },
  "Quality": "Common",
  "MaxStack": 1,
  "Categories": ["Items.Furniture"],
  "BlockType": {
    "Material": "Solid",
    "DrawType": "Model",
    "CustomModel": "Blocks/Furniture/Ancient/Chest_Small.blockymodel",
    "VariantRotation": "NESW",
    "PlacementSettings": {
      "RotationMode": "PlayerFacing"
    },
    "Support": {
      "Down": [{ "FaceType": "Full" }]
    },
    "ConnectedBlockRuleSet": {
      "TemplateShapeAssetId": "ChestConnectedBlockTemplate",
      "MergeMode": "Horizontal",
      "MaxMergeCount": 2,
      "MergedBlockId": "Furniture_Ancient_Chest_Double"
    },
    "State": {
      "StateType": "container"
    },
    "Components": {
      "container": {
        "ItemContainer": {
          "Capacity": 18
        },
        "Droplist": "Drop_Container_Contents"
      }
    },
    "Interactions": {
      "Use": "Block_Use_Open_Container"
    },
    "BlockSoundSetId": "Wood_Chest",
    "BlockParticleSetId": "Wood"
  },
  "Recipe": {
    "TimeSeconds": 3.0,
    "Input": [
      { "ResourceTypeId": "Wood_Trunk", "Quantity": 8 },
      { "ItemId": "Ingredient_Bar_Iron", "Quantity": 1 }
    ],
    "BenchRequirement": [{
      "Type": "Crafting",
      "Categories": ["Furniture"],
      "Id": "Furniture_Bench"
    }]
  }
}
```

### Double Chest

When two small chests are placed adjacent horizontally, they merge:

```json
{
  "BlockType": {
    "Components": {
      "container": {
        "ItemContainer": {
          "Capacity": 36
        }
      }
    }
  }
}
```

---

## Crafting Benches

Interactive stations for crafting.

### Bench_Furnace

**Location:** `Server/Item/Items/Bench/Bench_Furnace.json`

Smelting station with processing states.

```json
{
  "TranslationProperties": {
    "Name": "server.items.Bench_Furnace.name"
  },
  "Quality": "Common",
  "MaxStack": 1,
  "Categories": ["Items.Furniture"],
  "BlockType": {
    "Material": "Solid",
    "DrawType": "Model",
    "CustomModel": "Blocks/Bench/Furnace.blockymodel",
    "VariantRotation": "NESW",
    "PlacementSettings": {
      "RotationMode": "PlayerFacing"
    },
    "Support": {
      "Down": [{ "FaceType": "Full" }]
    },
    "Bench": {
      "Type": "Smelting",
      "Categories": ["Smelting"],
      "TierLevel": 1,
      "ProcessingTime": 1.0,
      "FuelRequired": true,
      "FuelSlots": 1
    },
    "State": {
      "StateType": "Processing",
      "DefaultState": "Idle",
      "States": {
        "Idle": {
          "Light": null,
          "Particles": []
        },
        "Active": {
          "Light": { "Color": "#FF6622", "Radius": 8 },
          "Particles": [
            { "SystemId": "Furnace_Fire", "TargetNodeName": "Fire_Point" },
            { "SystemId": "Furnace_Smoke", "TargetNodeName": "Chimney" }
          ],
          "CustomModelAnimation": "Working"
        }
      }
    },
    "Interactions": {
      "Use": "Block_Use_Craft"
    },
    "BlockSoundSetId": "Stone",
    "BlockParticleSetId": "Stone"
  },
  "Recipe": {
    "TimeSeconds": 5.0,
    "Input": [
      { "ItemId": "Rock_Stone", "Quantity": 20 },
      { "ItemId": "Ingredient_Coal", "Quantity": 5 }
    ]
  }
}
```

### Bench Configuration Properties

| Property | Type | Description |
|----------|------|-------------|
| `Type` | string | `Crafting`, `Smelting`, `Cooking`, `Alchemy` |
| `Categories` | array | Recipe categories this bench supports |
| `TierLevel` | int | Bench tier (1-3, affects available recipes) |
| `ProcessingTime` | float | Speed multiplier (lower = faster) |
| `FuelRequired` | boolean | Requires fuel to operate |
| `FuelSlots` | int | Number of fuel item slots |

---

## Farming Blocks

Crop blocks with growth stages.

### Template_Crop_Block

**Location:** `Server/Item/Items/Plant/Crop/_Template/Template_Crop_Block.json`

Base template for all crop blocks.

```json
{
  "Quality": "Template",
  "BlockType": {
    "Material": "Empty",
    "DrawType": "Model",
    "Opacity": "Transparent",
    "Support": {
      "Down": [{
        "Tags": {
          "Type": "Soil",
          "SubType": "Tilled"
        }
      }]
    },
    "SupportDropType": "Drop",
    "Farming": {
      "GrowthStages": 4,
      "GrowthTime": 180.0,
      "RequiresWater": true,
      "WaterRadius": 4,
      "GrowthModifiers": {
        "Watered": 2.0,
        "Fertilized": 1.5
      }
    },
    "State": {
      "StateType": "Growth",
      "DefaultState": "Stage_0",
      "States": {
        "Stage_0": {
          "CustomModel": "Blocks/Plant/Crop/Crop_Stage_0.blockymodel"
        },
        "Stage_1": {
          "CustomModel": "Blocks/Plant/Crop/Crop_Stage_1.blockymodel"
        },
        "Stage_2": {
          "CustomModel": "Blocks/Plant/Crop/Crop_Stage_2.blockymodel"
        },
        "Stage_3": {
          "CustomModel": "Blocks/Plant/Crop/Crop_Stage_3.blockymodel"
        }
      }
    },
    "TickProcedure": "Crop_Growth_Tick",
    "BlockSoundSetId": "Plant",
    "BlockParticleSetId": "Plant"
  }
}
```

### Growth Modifiers

| Modifier | Multiplier | Description |
|----------|------------|-------------|
| `Watered` | 2.0 | Growth speed when watered |
| `Fertilized` | 1.5 | Growth speed when fertilized |
| `Sunlight` | 1.2 | Growth speed in direct light |

### Example Child: Plant_Crop_Carrot_Block

```json
{
  "Parent": "Template_Crop_Block",
  "TranslationProperties": {
    "Name": "server.items.Plant_Crop_Carrot_Block.name"
  },
  "Quality": "Technical",
  "BlockType": {
    "Farming": {
      "GrowthStages": 4,
      "GrowthTime": 120.0,
      "HarvestDrops": [
        { "ItemId": "Plant_Crop_Carrot", "Quantity": [1, 3] },
        { "ItemId": "Plant_Crop_Carrot_Seed", "Quantity": [0, 2], "Chance": 0.5 }
      ]
    },
    "State": {
      "States": {
        "Stage_0": { "CustomModel": "Blocks/Plant/Crop/Carrot/Carrot_Stage_0.blockymodel" },
        "Stage_1": { "CustomModel": "Blocks/Plant/Crop/Carrot/Carrot_Stage_1.blockymodel" },
        "Stage_2": { "CustomModel": "Blocks/Plant/Crop/Carrot/Carrot_Stage_2.blockymodel" },
        "Stage_3": { "CustomModel": "Blocks/Plant/Crop/Carrot/Carrot_Stage_3.blockymodel" }
      }
    }
  }
}
```

---

## Block Item Patterns

### Creating Custom Solid Blocks

```json
{
  "TranslationProperties": {
    "Name": "server.items.My_Custom_Block.name"
  },
  "Quality": "Common",
  "MaxStack": 99,
  "Categories": ["Items.Blocks"],
  "Icon": "Icons/Blocks/My_Block.png",
  "BlockType": {
    "Material": "Solid",
    "DrawType": "Cube",
    "Opacity": "Opaque",
    "Textures": [
      { "Face": "All", "Texture": "Blocks/Custom/My_Block.png" }
    ],
    "BlockSoundSetId": "Stone",
    "BlockParticleSetId": "Stone"
  }
}
```

### Creating Furniture with States

```json
{
  "TranslationProperties": {
    "Name": "server.items.My_Lamp.name"
  },
  "Quality": "Common",
  "MaxStack": 10,
  "Categories": ["Items.Furniture"],
  "BlockType": {
    "Material": "Empty",
    "DrawType": "Model",
    "CustomModel": "Blocks/Furniture/My_Lamp.blockymodel",
    "PlacementSettings": {
      "RotationMode": "YawStep1",
      "AllowRotationKey": true
    },
    "Support": {
      "Down": [{ "FaceType": "Full" }]
    },
    "State": {
      "StateType": "OnOff",
      "DefaultState": "On",
      "States": {
        "On": {
          "Light": { "Color": "#FFFFFF", "Radius": 14 }
        },
        "Off": {
          "Light": null
        }
      }
    },
    "Interactions": {
      "Use": "Block_Use_Toggle_OnOff"
    }
  }
}
```

### Creating Light Sources

```json
{
  "BlockType": {
    "Material": "Empty",
    "DrawType": "Model",
    "Light": {
      "Color": "#FFAA44",
      "Radius": 12
    },
    "Particles": [
      {
        "SystemId": "Fire_Small",
        "TargetNodeName": "Flame_Point"
      }
    ]
  }
}
```

### Creating Interactive Containers

```json
{
  "BlockType": {
    "Material": "Solid",
    "DrawType": "Model",
    "CustomModel": "Blocks/Container/My_Box.blockymodel",
    "State": {
      "StateType": "container"
    },
    "Components": {
      "container": {
        "ItemContainer": {
          "Capacity": 27
        },
        "Droplist": "Drop_Container_Contents"
      }
    },
    "Interactions": {
      "Use": "Block_Use_Open_Container"
    }
  }
}
```

---

## Block_Secondary Interaction

**Location:** `Server/Item/Interactions/Block_Secondary.json`

Default interaction for block placement:

```json
{
  "Type": "Serial",
  "Interactions": [
    {
      "Type": "PlaceBlock",
      "Target": "TargetBlock",
      "ConsumeItem": true
    }
  ]
}
```

This interaction is assigned to the `Secondary` slot for most placeable items.

---

## Related Documentation

- [Items Reference](items.md) - Common properties and systems
- [Blocks API](blocks.md) - BlockType class and events
- [Drop System](drops.md) - Loot tables for containers and blocks
- [Interactions API](interactions.md) - Block interactions
- [Entity & World Interactions](interactions-world.md) - PlaceBlock, BreakBlock
- [Tools Reference](items-tools.md) - Block-breaking tools
