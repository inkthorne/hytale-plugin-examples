# Biome System

Biomes define the visual and gameplay characteristics of terrain within zones. Each biome specifies terrain layers, surface covers, prefab placement, water generation, environment settings, and block tinting. Biomes use a container-based architecture where different aspects of generation are organized into specialized containers.

## Quick Navigation

| Section | Description |
|---------|-------------|
| [Biome Types](#biome-types) | TileBiome vs CustomBiome |
| [LayerContainer](#layercontainer) | Terrain layers (see also [worldgen-terrain.md](worldgen-terrain.md)) |
| [CoverContainer](#covercontainer) | Surface vegetation and debris |
| [PrefabContainer](#prefabcontainer) | Structure placement |
| [WaterContainer](#watercontainer) | Water and fluid generation |
| [TintContainer](#tintcontainer) | Block color variations |
| [EnvironmentContainer](#environmentcontainer) | Sky, lighting, weather |
| [FadeContainer](#fadecontainer) | Biome transition effects |
| [BiomeInterpolation](#biomeinterpolation) | Smooth biome blending |

---

## Biome Types

Hytale has two biome implementations:

### Class Hierarchy

```
Biome (abstract)
├── TileBiome      - Tile-based weighted selection
└── CustomBiome    - Procedural/custom generation
```

### TileBiome

Standard biome type using weighted random selection for covers and prefabs:

```json
{
  "Type": "TileBiome",
  "Id": "Biome_Forest",
  "LayerContainer": { ... },
  "CoverContainer": { ... },
  "PrefabContainer": { ... }
}
```

### CustomBiome

Procedural biome with custom generation logic:

```json
{
  "Type": "CustomBiome",
  "Id": "Biome_Custom_Terrain",
  "GeneratorClass": "com.example.CustomBiomeGenerator",
  "Parameters": {
    "NoiseScale": 0.02,
    "Amplitude": 30
  }
}
```

### Common Properties

All biome types share these base properties:

| Property | Type | Required | Description |
|----------|------|----------|-------------|
| `Type` | string | No | `"TileBiome"` (default) or `"CustomBiome"` |
| `Id` | string | Yes | Unique biome identifier |
| `Parent` | string | No | Parent biome to inherit from |
| `LayerContainer` | object | No | Terrain layer configuration |
| `CoverContainer` | object | No | Surface cover configuration |
| `PrefabContainer` | object | No | Prefab placement configuration |
| `WaterContainer` | object | No | Water generation configuration |
| `TintContainer` | object | No | Block tint configuration |
| `EnvironmentContainer` | object | No | Environment settings |
| `FadeContainer` | object | No | Transition configuration |

### File Location

Biome files are stored in `Server/WorldGen/Biome/`:

```
Server/WorldGen/Biome/
├── Zone1/
│   ├── Biome_Forest.json
│   ├── Biome_Meadow.json
│   └── Biome_River.json
├── Zone2/
│   ├── Biome_Desert.json
│   └── Biome_Oasis.json
└── Common/
    └── Biome_Water.json
```

---

## LayerContainer

Controls terrain generation through filling blocks and layered surfaces.

| Property | Type | Description |
|----------|------|-------------|
| `Filling` | string | Block type for base terrain fill |
| `StaticLayers` | array | Fixed-height terrain layers |
| `DynamicLayers` | array | Noise-based variable layers |

### Basic Example

```json
{
  "LayerContainer": {
    "Filling": "Stone",
    "StaticLayers": [
      { "Block": "Dirt", "Height": 4 },
      { "Block": "Gravel", "Height": 1 }
    ],
    "DynamicLayers": [
      { "Block": "Grass", "MinHeight": 1, "MaxHeight": 1 }
    ]
  }
}
```

> **See also:** [Terrain Layers](worldgen-terrain.md) for complete layer documentation

---

## CoverContainer

Defines surface vegetation, debris, and decorations placed on top of terrain.

### Properties

| Property | Type | Description |
|----------|------|-------------|
| `Covers` | array | List of cover configurations |
| `GlobalDensityModifier` | float | Multiplier for all cover densities |

### Cover Entry Properties

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `Block` | string | - | Block type to place |
| `Density` | float | 0.1 | Spawn density (0.0-1.0) |
| `Conditions` | object | - | Placement conditions |
| `ClusterSize` | int | 1 | Number of blocks per cluster |
| `ClusterSpread` | int | 0 | Spread radius for clusters |
| `YOffset` | int | 1 | Vertical offset from surface |
| `RequiredBase` | array | - | Block types that can support this cover |
| `Weight` | float | 1.0 | Selection weight when multiple covers compete |

### Condition Properties

| Property | Type | Description |
|----------|------|-------------|
| `SlopeMin` | float | Minimum terrain slope (degrees) |
| `SlopeMax` | float | Maximum terrain slope (degrees) |
| `HeightMin` | int | Minimum world height |
| `HeightMax` | int | Maximum world height |
| `LightMin` | int | Minimum light level (0-15) |
| `LightMax` | int | Maximum light level (0-15) |
| `NoiseMin` | float | Minimum noise value |
| `NoiseMax` | float | Maximum noise value |
| `RequiresSky` | boolean | Must have sky access |
| `AvoidWater` | boolean | Cannot be near water |

### Example: Forest Floor

```json
{
  "CoverContainer": {
    "GlobalDensityModifier": 1.0,
    "Covers": [
      {
        "Block": "Grass_Tall",
        "Density": 0.35,
        "Conditions": {
          "SlopeMax": 30,
          "RequiresSky": true
        },
        "RequiredBase": ["Grass"]
      },
      {
        "Block": "Flower_Daisy",
        "Density": 0.05,
        "Conditions": {
          "SlopeMax": 20,
          "LightMin": 10
        },
        "ClusterSize": 3,
        "ClusterSpread": 2
      },
      {
        "Block": "Mushroom_Red",
        "Density": 0.02,
        "Conditions": {
          "LightMax": 8,
          "RequiresSky": false
        },
        "RequiredBase": ["Grass", "Dirt", "Moss"]
      },
      {
        "Block": "Fern",
        "Density": 0.15,
        "Conditions": {
          "SlopeMax": 25,
          "HeightMin": 65,
          "HeightMax": 100
        }
      },
      {
        "Block": "Rock_Small",
        "Density": 0.03,
        "Conditions": {
          "SlopeMin": 15,
          "SlopeMax": 45
        },
        "Weight": 0.5
      }
    ]
  }
}
```

### Example: Desert Surface

```json
{
  "CoverContainer": {
    "Covers": [
      {
        "Block": "Cactus_Small",
        "Density": 0.01,
        "Conditions": {
          "SlopeMax": 15
        },
        "RequiredBase": ["Sand"]
      },
      {
        "Block": "Dead_Bush",
        "Density": 0.02,
        "Conditions": {
          "SlopeMax": 25
        }
      },
      {
        "Block": "Bone_Scatter",
        "Density": 0.005,
        "ClusterSize": 2,
        "ClusterSpread": 1
      }
    ]
  }
}
```

### Cluster Generation

When `ClusterSize` > 1, covers spawn in groups:

```
ClusterSize: 5, ClusterSpread: 2

        X
      X X X
        X

  5 blocks spread within 2-block radius
```

---

## PrefabContainer

Controls placement of structures, trees, rocks, and other prefabs within the biome.

### Properties

| Property | Type | Description |
|----------|------|-------------|
| `Prefabs` | array | List of prefab placement rules |
| `GlobalDensityModifier` | float | Multiplier for all prefab densities |

### Prefab Entry Properties

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `PrefabList` | string | - | Reference to PrefabList file |
| `PrefabId` | string | - | Single prefab file reference |
| `Density` | float | 0.01 | Spawn density (0.0-1.0) |
| `MinSpacing` | int | 4 | Minimum blocks between prefabs |
| `MaxSpacing` | int | - | Maximum blocks between prefabs |
| `Weight` | float | 1.0 | Selection weight |
| `Conditions` | object | - | Placement conditions |
| `FitHeightmap` | boolean | true | Adjust prefab to terrain height |
| `AllowPartialPlacement` | boolean | false | Place even if partially blocked |
| `RotationMode` | string | `"Random"` | Rotation selection mode |

### Rotation Modes

| Mode | Description |
|------|-------------|
| `"None"` | No rotation (always north-facing) |
| `"Random"` | Random 90° rotation |
| `"AlignToSlope"` | Rotate to match terrain slope |
| `"FaceCenter"` | Rotate toward biome center |

### Condition Properties

| Property | Type | Description |
|----------|------|-------------|
| `SlopeMax` | float | Maximum terrain slope |
| `HeightRange` | [min, max] | Valid height range |
| `MinFlatArea` | int | Required flat area (blocks) |
| `AvoidWater` | boolean | Cannot touch water |
| `AvoidEdge` | int | Distance from biome edge |
| `RequiredBiomes` | array | Adjacent biomes required |

### Example: Forest Trees and Rocks

```json
{
  "PrefabContainer": {
    "GlobalDensityModifier": 1.0,
    "Prefabs": [
      {
        "PrefabList": "Trees_Oak",
        "Density": 0.04,
        "MinSpacing": 5,
        "Conditions": {
          "SlopeMax": 25,
          "MinFlatArea": 3
        },
        "RotationMode": "Random"
      },
      {
        "PrefabList": "Trees_Pine",
        "Density": 0.02,
        "MinSpacing": 6,
        "Conditions": {
          "HeightRange": [80, 150],
          "SlopeMax": 30
        }
      },
      {
        "PrefabList": "Rocks_Forest",
        "Density": 0.015,
        "MinSpacing": 8,
        "Conditions": {
          "SlopeMax": 40
        },
        "FitHeightmap": true
      },
      {
        "PrefabId": "Structures/Forest/Fallen_Log",
        "Density": 0.005,
        "MinSpacing": 15,
        "RotationMode": "Random"
      }
    ]
  }
}
```

### PrefabList Reference

PrefabLists are defined in `Server/PrefabList/`:

```json
{
  "Prefabs": [
    {
      "RootDirectory": "Asset",
      "Path": "Trees/Oak/",
      "Recursive": true
    }
  ]
}
```

> **See also:** [Prefab API](prefabs.md#prefablist-files)

---

## WaterContainer

Controls water body generation within the biome.

### Properties

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `Enabled` | boolean | true | Enable water generation |
| `FluidType` | string | `"Water"` | Fluid type to generate |
| `SurfaceLevel` | int | 63 | Default water surface height |
| `DynamicLevel` | boolean | false | Adjust level to terrain |
| `LevelNoise` | object | - | Noise for variable water levels |
| `ShoreBlend` | int | 3 | Shore transition distance |
| `DepthTint` | boolean | true | Apply depth-based tinting |

### Level Noise Properties

| Property | Type | Description |
|----------|------|-------------|
| `Scale` | float | Noise frequency |
| `Amplitude` | int | Height variation range |
| `Offset` | int | Base level offset |

### Example: Standard Lake

```json
{
  "WaterContainer": {
    "Enabled": true,
    "FluidType": "Water",
    "SurfaceLevel": 63,
    "ShoreBlend": 4,
    "DepthTint": true
  }
}
```

### Example: Swamp with Variable Water

```json
{
  "WaterContainer": {
    "Enabled": true,
    "FluidType": "Water_Murky",
    "DynamicLevel": true,
    "LevelNoise": {
      "Scale": 0.02,
      "Amplitude": 3,
      "Offset": 62
    },
    "ShoreBlend": 6
  }
}
```

### Example: Lava Lake

```json
{
  "WaterContainer": {
    "Enabled": true,
    "FluidType": "Lava",
    "SurfaceLevel": 15,
    "ShoreBlend": 1,
    "DepthTint": false
  }
}
```

---

## TintContainer

Applies color variations to blocks based on biome.

### Properties

| Property | Type | Description |
|----------|------|-------------|
| `Tints` | array | Block tint configurations |
| `GlobalTint` | string | Hex color applied to all tintable blocks |

### Tint Entry Properties

| Property | Type | Description |
|----------|------|-------------|
| `Block` | string | Block type to tint |
| `Color` | string | Hex color value |
| `Noise` | object | Optional noise for variation |
| `BlendMode` | string | `"Multiply"`, `"Overlay"`, `"Replace"` |

### Example: Autumn Forest Tints

```json
{
  "TintContainer": {
    "Tints": [
      {
        "Block": "Grass",
        "Color": "#8B7355",
        "BlendMode": "Multiply"
      },
      {
        "Block": "Leaves_Oak",
        "Color": "#FF6B35",
        "Noise": {
          "Scale": 0.1,
          "Colors": ["#FF6B35", "#FFB347", "#FF4500"]
        }
      },
      {
        "Block": "Leaves_Maple",
        "Color": "#DC143C"
      }
    ]
  }
}
```

### Example: Snowy Biome Tints

```json
{
  "TintContainer": {
    "GlobalTint": "#E8E8E8",
    "Tints": [
      {
        "Block": "Grass",
        "Color": "#FFFFFF",
        "BlendMode": "Replace"
      }
    ]
  }
}
```

---

## EnvironmentContainer

Controls sky, lighting, fog, and weather settings for the biome.

### Properties

| Property | Type | Description |
|----------|------|-------------|
| `EnvironmentId` | string | Reference to environment preset |
| `SkyColor` | string | Override sky color (hex) |
| `FogColor` | string | Override fog color (hex) |
| `FogDensity` | float | Fog density (0.0-1.0) |
| `AmbientLight` | float | Ambient light level (0.0-1.0) |
| `SunIntensity` | float | Sun brightness multiplier |
| `Weather` | object | Weather configuration |

### Weather Properties

| Property | Type | Description |
|----------|------|-------------|
| `Type` | string | `"Clear"`, `"Rain"`, `"Snow"`, `"Storm"` |
| `Intensity` | float | Weather intensity (0.0-1.0) |
| `Frequency` | float | How often weather occurs |
| `Duration` | object | Duration range |

### Example: Mystical Forest

```json
{
  "EnvironmentContainer": {
    "EnvironmentId": "Environment_Forest_Mystical",
    "FogColor": "#4A6741",
    "FogDensity": 0.3,
    "AmbientLight": 0.7,
    "Weather": {
      "Type": "Rain",
      "Frequency": 0.3,
      "Duration": {
        "Min": 300,
        "Max": 900
      }
    }
  }
}
```

### Example: Desert Environment

```json
{
  "EnvironmentContainer": {
    "EnvironmentId": "Environment_Desert",
    "SkyColor": "#87CEEB",
    "FogColor": "#F5DEB3",
    "FogDensity": 0.1,
    "SunIntensity": 1.3,
    "Weather": {
      "Type": "Clear",
      "Frequency": 0.95
    }
  }
}
```

---

## FadeContainer

Controls visual transitions when moving between biomes.

### Properties

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `Enabled` | boolean | true | Enable fade transitions |
| `Distance` | int | 16 | Fade distance in blocks |
| `FadeType` | string | `"Linear"` | Fade curve type |
| `AffectsLayers` | boolean | true | Fade terrain layers |
| `AffectsCovers` | boolean | true | Fade surface covers |
| `AffectsTints` | boolean | true | Fade block tints |

### Fade Types

| Type | Description |
|------|-------------|
| `"Linear"` | Constant fade rate |
| `"Smooth"` | Smooth ease in/out |
| `"Sharp"` | Abrupt near boundary |

### Example

```json
{
  "FadeContainer": {
    "Enabled": true,
    "Distance": 24,
    "FadeType": "Smooth",
    "AffectsLayers": true,
    "AffectsCovers": true,
    "AffectsTints": true
  }
}
```

---

## BiomeInterpolation

When multiple biomes meet, `BiomeInterpolation` blends their properties for smooth transitions.

### How Interpolation Works

At positions near biome boundaries, the world generator samples multiple biomes and blends their outputs:

```
Biome A ──────┬────────────────┬────── Biome B
              │    Transition   │
         100% A │ ← Blended → │ 100% B

Position weights:
  - Near A: [A: 0.9, B: 0.1]
  - Middle: [A: 0.5, B: 0.5]
  - Near B: [A: 0.1, B: 0.9]
```

### Interpolated Properties

| Property | Interpolation Method |
|----------|---------------------|
| Terrain height | Weighted average |
| Layer selection | Weighted random |
| Cover density | Weighted probability |
| Prefab placement | Nearest biome wins |
| Tint colors | Color blending |
| Water level | Weighted average |

### Java Integration

```java
// BiomeInterpolation provides blending weights
public class BiomeInterpolation {
    // Get all contributing biomes at position
    public List<BiomeWeight> getBiomesAt(int x, int z);

    // Get primary biome (highest weight)
    public Biome getPrimaryBiome(int x, int z);

    // Get interpolated value
    public float getInterpolatedHeight(int x, int z);

    // Check if position is in transition zone
    public boolean isInTransition(int x, int z);
}

// BiomeWeight holds a biome and its contribution weight
public record BiomeWeight(Biome biome, float weight) {}
```

### Interpolation Example

```java
BiomeInterpolation interp = worldGen.getBiomeInterpolation();

// At transition between Forest and Desert
List<BiomeWeight> weights = interp.getBiomesAt(100, 200);
// weights = [
//   BiomeWeight(Biome_Forest, 0.65),
//   BiomeWeight(Biome_Desert, 0.35)
// ]

// Terrain layer selection uses weighted random:
// 65% chance: select from Forest layers
// 35% chance: select from Desert layers

// Tint color blends:
// finalColor = Forest.tint * 0.65 + Desert.tint * 0.35
```

---

## Complete Biome Example

A full biome definition with all containers:

```json
{
  "Type": "TileBiome",
  "Id": "Biome_Enchanted_Forest",

  "LayerContainer": {
    "Filling": "Stone",
    "StaticLayers": [
      { "Block": "Dirt", "Height": 5 },
      { "Block": "Dirt_Rich", "Height": 2 }
    ],
    "DynamicLayers": [
      {
        "Block": "Grass_Enchanted",
        "MinHeight": 1,
        "MaxHeight": 1
      }
    ]
  },

  "CoverContainer": {
    "GlobalDensityModifier": 1.2,
    "Covers": [
      {
        "Block": "Grass_Tall_Glowing",
        "Density": 0.25,
        "Conditions": {
          "SlopeMax": 25,
          "RequiresSky": true
        }
      },
      {
        "Block": "Flower_Luminous",
        "Density": 0.08,
        "ClusterSize": 4,
        "ClusterSpread": 2,
        "Conditions": {
          "SlopeMax": 20
        }
      },
      {
        "Block": "Mushroom_Glowing",
        "Density": 0.04,
        "Conditions": {
          "LightMax": 10
        }
      },
      {
        "Block": "Crystal_Small",
        "Density": 0.01,
        "Conditions": {
          "SlopeMin": 10,
          "SlopeMax": 40
        }
      }
    ]
  },

  "PrefabContainer": {
    "Prefabs": [
      {
        "PrefabList": "Trees_Enchanted",
        "Density": 0.05,
        "MinSpacing": 6,
        "Conditions": {
          "SlopeMax": 20,
          "MinFlatArea": 4
        }
      },
      {
        "PrefabList": "Rocks_Crystal",
        "Density": 0.01,
        "MinSpacing": 12
      },
      {
        "PrefabId": "Structures/Forest/Fairy_Ring",
        "Density": 0.002,
        "MinSpacing": 50,
        "Conditions": {
          "MinFlatArea": 8
        }
      }
    ]
  },

  "WaterContainer": {
    "Enabled": true,
    "FluidType": "Water_Clear",
    "SurfaceLevel": 63,
    "ShoreBlend": 5,
    "DepthTint": true
  },

  "TintContainer": {
    "Tints": [
      {
        "Block": "Grass_Enchanted",
        "Color": "#00FF7F",
        "BlendMode": "Multiply"
      },
      {
        "Block": "Leaves_Magic",
        "Noise": {
          "Scale": 0.15,
          "Colors": ["#7FFFD4", "#00CED1", "#48D1CC"]
        }
      }
    ]
  },

  "EnvironmentContainer": {
    "EnvironmentId": "Environment_Enchanted",
    "FogColor": "#E0FFFF",
    "FogDensity": 0.15,
    "AmbientLight": 0.85,
    "Weather": {
      "Type": "Clear",
      "Frequency": 0.9
    }
  },

  "FadeContainer": {
    "Enabled": true,
    "Distance": 32,
    "FadeType": "Smooth",
    "AffectsLayers": true,
    "AffectsCovers": true,
    "AffectsTints": true
  }
}
```

---

## Related Documentation

- [World Generation Overview](worldgen.md) - Pipeline and priority system
- [Zones](worldgen-zones.md) - Zone system and biome distribution
- [Terrain Layers](worldgen-terrain.md) - Complete layer documentation
- [Prefab API](prefabs.md) - Prefab loading and placement
- [Block System](blocks.md) - Block types and properties
- [Fluids](fluids.md) - Fluid types and behavior
