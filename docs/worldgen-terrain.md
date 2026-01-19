# Terrain Layer System

Terrain layers define how blocks are stacked to create the ground. The layer system uses a filling block as the base, then applies static layers (fixed height) and dynamic layers (noise-based variable height) to create natural-looking terrain with different soil compositions.

## Quick Navigation

| Section | Description |
|---------|-------------|
| [LayerContainer](#layercontainer) | Container structure for terrain layers |
| [StaticLayer](#staticlayer) | Fixed-height terrain layers |
| [DynamicLayer](#dynamiclayer) | Noise-based variable layers |
| [Height Suppliers](#height-suppliers) | Noise functions for layer height |
| [BlockPopulator](#blockpopulator) | Terrain generation pipeline |
| [Block Priority](#block-priority) | Priority resolution system |

---

## LayerContainer

The `LayerContainer` defines terrain composition for a biome. It specifies a filling block for the base terrain, then layers that build upward from the terrain surface.

### Properties

| Property | Type | Required | Description |
|----------|------|----------|-------------|
| `Filling` | string | Yes | Block type for base terrain fill |
| `FillingPriority` | int | No | Priority for filling blocks (default: 1) |
| `StaticLayers` | array | No | Fixed-height layers from surface |
| `DynamicLayers` | array | No | Variable-height noise-based layers |
| `SubsurfaceLayers` | array | No | Layers below main terrain |
| `HeightOffset` | int | No | Vertical offset for all layers |

### Layer Stacking Order

Layers are applied from the terrain surface downward:

```
Surface (y = terrain height)
┌─────────────────────────────────────┐
│ DynamicLayer: Grass (1 block)       │  ← Top layer (dynamic)
├─────────────────────────────────────┤
│ StaticLayer: Dirt (4 blocks)        │  ← Fixed depth
├─────────────────────────────────────┤
│ StaticLayer: Gravel (2 blocks)      │  ← Fixed depth
├─────────────────────────────────────┤
│ Filling: Stone (to bedrock)         │  ← Fills remaining space
└─────────────────────────────────────┘
Bedrock (y = 0)
```

### Basic Example

```json
{
  "LayerContainer": {
    "Filling": "Stone",
    "StaticLayers": [
      { "Block": "Dirt", "Height": 4 },
      { "Block": "Gravel", "Height": 2 }
    ],
    "DynamicLayers": [
      { "Block": "Grass", "MinHeight": 1, "MaxHeight": 1 }
    ]
  }
}
```

---

## StaticLayer

Static layers have a fixed height regardless of terrain shape. They're applied in order from the terrain surface downward.

### Properties

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `Block` | string | - | Block type for this layer |
| `Height` | int | 1 | Layer thickness in blocks |
| `Priority` | int | 2 | Block priority level |
| `Conditions` | object | - | Placement conditions |

### Condition Properties

| Property | Type | Description |
|----------|------|-------------|
| `SlopeMin` | float | Minimum terrain slope (degrees) |
| `SlopeMax` | float | Maximum terrain slope (degrees) |
| `HeightMin` | int | Minimum world height |
| `HeightMax` | int | Maximum world height |
| `NoiseMin` | float | Minimum noise threshold |
| `NoiseMax` | float | Maximum noise threshold |

### Example: Multi-Layer Soil

```json
{
  "StaticLayers": [
    {
      "Block": "Dirt_Rich",
      "Height": 2,
      "Conditions": {
        "SlopeMax": 20
      }
    },
    {
      "Block": "Dirt",
      "Height": 4
    },
    {
      "Block": "Gravel",
      "Height": 1,
      "Conditions": {
        "HeightMin": 50,
        "HeightMax": 80
      }
    },
    {
      "Block": "Clay",
      "Height": 2,
      "Conditions": {
        "HeightMax": 50
      }
    }
  ]
}
```

### Conditional Layer Selection

When layers have conditions, only matching layers are applied:

```
Terrain at y=75, slope=10°:
  ✓ Dirt_Rich (slope ≤ 20°)
  ✓ Dirt (no conditions)
  ✓ Gravel (height 50-80)
  ✗ Clay (height ≤ 50, skipped)

Terrain at y=40, slope=35°:
  ✗ Dirt_Rich (slope > 20°, skipped)
  ✓ Dirt (no conditions)
  ✗ Gravel (height < 50, skipped)
  ✓ Clay (height ≤ 50)
```

---

## DynamicLayer

Dynamic layers use noise functions to vary their height, creating natural variation in terrain composition.

### Properties

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `Block` | string | - | Block type for this layer |
| `MinHeight` | int | 1 | Minimum layer height |
| `MaxHeight` | int | 1 | Maximum layer height |
| `HeightSupplier` | object | - | Noise function for height calculation |
| `Priority` | int | 2 | Block priority level |
| `Conditions` | object | - | Placement conditions |
| `Blend` | boolean | false | Blend with adjacent layers |

### Basic Dynamic Layer

```json
{
  "DynamicLayers": [
    {
      "Block": "Grass",
      "MinHeight": 1,
      "MaxHeight": 1
    }
  ]
}
```

### Variable Height Layer

```json
{
  "DynamicLayers": [
    {
      "Block": "Sand",
      "MinHeight": 2,
      "MaxHeight": 6,
      "HeightSupplier": {
        "Type": "Perlin",
        "Scale": 0.02,
        "Octaves": 2
      }
    }
  ]
}
```

---

## Height Suppliers

Height suppliers (implementing `IDoubleCoordinateSupplier`) generate height values using noise or other algorithms.

### Supplier Types

| Type | Description |
|------|-------------|
| `Constant` | Fixed value at all positions |
| `Perlin` | Perlin noise with configurable scale |
| `Simplex` | Simplex noise (smoother than Perlin) |
| `Voronoi` | Voronoi cell-based noise |
| `Ridged` | Ridged multi-fractal noise |
| `Compound` | Combines multiple suppliers |

### Constant Supplier

Returns the same value everywhere:

```json
{
  "HeightSupplier": {
    "Type": "Constant",
    "Value": 4
  }
}
```

### Perlin Noise Supplier

Classic Perlin noise with octaves for detail:

```json
{
  "HeightSupplier": {
    "Type": "Perlin",
    "Scale": 0.01,
    "Octaves": 3,
    "Persistence": 0.5,
    "Lacunarity": 2.0,
    "Seed": 12345
  }
}
```

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `Scale` | float | 0.01 | Noise frequency (smaller = larger features) |
| `Octaves` | int | 1 | Number of noise layers |
| `Persistence` | float | 0.5 | Amplitude reduction per octave |
| `Lacunarity` | float | 2.0 | Frequency multiplier per octave |
| `Seed` | long | - | Random seed |

### Simplex Noise Supplier

Smoother alternative to Perlin:

```json
{
  "HeightSupplier": {
    "Type": "Simplex",
    "Scale": 0.015,
    "Octaves": 2
  }
}
```

### Voronoi Supplier

Cell-based noise for distinct regions:

```json
{
  "HeightSupplier": {
    "Type": "Voronoi",
    "Scale": 0.005,
    "Jitter": 0.8,
    "DistanceType": "Euclidean"
  }
}
```

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `DistanceType` | string | `"Euclidean"` | Distance calculation method |
| `Jitter` | float | 1.0 | Randomness in cell seed positions |

Distance types: `"Euclidean"`, `"Manhattan"`, `"Chebyshev"`

### Ridged Noise Supplier

Creates ridge-like features (mountains, veins):

```json
{
  "HeightSupplier": {
    "Type": "Ridged",
    "Scale": 0.008,
    "Octaves": 4,
    "Gain": 2.0,
    "Offset": 1.0
  }
}
```

### Compound Supplier

Combines multiple suppliers:

```json
{
  "HeightSupplier": {
    "Type": "Compound",
    "Operation": "Add",
    "Suppliers": [
      {
        "Type": "Perlin",
        "Scale": 0.01,
        "Octaves": 2
      },
      {
        "Type": "Simplex",
        "Scale": 0.05,
        "Weight": 0.3
      }
    ]
  }
}
```

| Operation | Description |
|-----------|-------------|
| `Add` | Sum all supplier values |
| `Multiply` | Multiply all values |
| `Average` | Average all values |
| `Max` | Use highest value |
| `Min` | Use lowest value |

---

## BlockPopulator

The `BlockPopulator` is the first stage of world generation, responsible for placing terrain blocks.

### Generation Pipeline

```
BlockPopulator Pipeline
│
├─ 1. generateBlockColumn(x, z)
│     ├─ Get terrain height from heightmap
│     ├─ Get zone/biome at position
│     ├─ Place filling blocks (y=0 to terrainHeight)
│     ├─ Apply static layers from surface down
│     └─ Apply dynamic layers with noise
│
├─ 2. generateCovers(x, z)
│     ├─ Get biome's CoverContainer
│     ├─ Evaluate cover conditions
│     └─ Place covers on valid surfaces
│
└─ 3. Track block priorities
      └─ BlockPriorityChunk stores priority per block
```

### Column Generation Example

```java
// Simplified BlockPopulator logic
void generateBlockColumn(int x, int z, WorldChunk chunk) {
    int terrainHeight = heightmap.getHeight(x, z);
    ZoneBiomeResult result = getZoneBiome(x, z);
    Biome biome = result.biome();
    LayerContainer layers = biome.getLayerContainer();

    // Fill with base block
    for (int y = 0; y < terrainHeight; y++) {
        chunk.setBlock(x, y, z, layers.getFilling(), PRIORITY_FILLING);
    }

    // Apply layers from surface down
    int currentY = terrainHeight;

    // Dynamic layers first (surface)
    for (DynamicLayer layer : layers.getDynamicLayers()) {
        int height = layer.getHeight(x, z);
        for (int i = 0; i < height && currentY > 0; i++) {
            chunk.setBlock(x, currentY--, z, layer.getBlock(), PRIORITY_LAYER);
        }
    }

    // Static layers below dynamic
    for (StaticLayer layer : layers.getStaticLayers()) {
        if (layer.matchesConditions(x, z, currentY)) {
            for (int i = 0; i < layer.getHeight() && currentY > 0; i++) {
                chunk.setBlock(x, currentY--, z, layer.getBlock(), PRIORITY_LAYER);
            }
        }
    }
}
```

---

## Block Priority

The block priority system prevents later generation stages from overwriting important blocks.

### Priority Levels

| Priority | Constant | Value | Description |
|----------|----------|-------|-------------|
| `NONE` | `PRIORITY_NONE` | 0 | Default, can be overwritten |
| `FILLING` | `PRIORITY_FILLING` | 1 | Base terrain fill |
| `LAYER` | `PRIORITY_LAYER` | 2 | Terrain layers |
| `COVER` | `PRIORITY_COVER` | 3 | Surface covers |
| `WATER` | `PRIORITY_WATER` | 4 | Water/fluids |
| `CAVE` | `PRIORITY_CAVE` | 5 | Cave carving |
| `CAVE_FILLING` | `PRIORITY_CAVE_FILLING` | 6 | Cave fill blocks |
| `CAVE_COVER` | `PRIORITY_CAVE_COVER` | 7 | Cave surfaces |
| `PREFAB` | `PRIORITY_PREFAB` | 8 | Surface prefabs |
| `CAVE_PREFAB` | `PRIORITY_CAVE_PREFAB` | 9 | Cave prefabs |
| `UNIQUE_PREFAB` | `PRIORITY_UNIQUE` | 10 | Unique structures |

### BlockPriorityChunk

Tracks priorities during generation:

```java
public class BlockPriorityChunk {
    private final byte[] priorities;  // 16x256x16 = 65536 bytes

    public boolean canPlace(int x, int y, int z, int priority) {
        return priority >= getPriority(x, y, z);
    }

    public void setBlock(int x, int y, int z, int blockType, int priority) {
        if (canPlace(x, y, z, priority)) {
            // Set block in chunk
            // Update priority tracking
            priorities[index(x, y, z)] = (byte) priority;
        }
    }

    public int getPriority(int x, int y, int z) {
        return priorities[index(x, y, z)] & 0xFF;
    }
}
```

### Priority Resolution Example

```
Position (5, 70, 10) during generation:

1. BlockPopulator places "Stone" (FILLING = 1)
   priorities[5,70,10] = 1, block = Stone

2. BlockPopulator places "Dirt" (LAYER = 2)
   2 >= 1 → allowed
   priorities[5,70,10] = 2, block = Dirt

3. CavePopulator tries to carve (CAVE = 5)
   5 >= 2 → allowed
   priorities[5,70,10] = 5, block = Air

4. PrefabPopulator places "Stone_Brick" (PREFAB = 8)
   8 >= 5 → allowed
   priorities[5,70,10] = 8, block = Stone_Brick

Final: Stone_Brick with priority 8
```

---

## Complete Layer Examples

### Forest Biome Layers

```json
{
  "LayerContainer": {
    "Filling": "Stone",
    "StaticLayers": [
      {
        "Block": "Dirt",
        "Height": 5,
        "Conditions": {
          "SlopeMax": 40
        }
      },
      {
        "Block": "Gravel",
        "Height": 2,
        "Conditions": {
          "SlopeMin": 40
        }
      },
      {
        "Block": "Clay",
        "Height": 1,
        "Conditions": {
          "HeightMin": 60,
          "HeightMax": 70
        }
      }
    ],
    "DynamicLayers": [
      {
        "Block": "Grass",
        "MinHeight": 1,
        "MaxHeight": 1,
        "Conditions": {
          "SlopeMax": 35
        }
      },
      {
        "Block": "Dirt",
        "MinHeight": 1,
        "MaxHeight": 1,
        "Conditions": {
          "SlopeMin": 35
        }
      }
    ]
  }
}
```

### Desert Biome Layers

```json
{
  "LayerContainer": {
    "Filling": "Sandstone",
    "StaticLayers": [
      {
        "Block": "Sandstone_Soft",
        "Height": 6
      },
      {
        "Block": "Sandstone_Hard",
        "Height": 3
      }
    ],
    "DynamicLayers": [
      {
        "Block": "Sand",
        "MinHeight": 3,
        "MaxHeight": 8,
        "HeightSupplier": {
          "Type": "Simplex",
          "Scale": 0.03,
          "Octaves": 2
        }
      }
    ]
  }
}
```

### Mountain Biome Layers

```json
{
  "LayerContainer": {
    "Filling": "Stone_Granite",
    "StaticLayers": [
      {
        "Block": "Gravel",
        "Height": 2,
        "Conditions": {
          "HeightMax": 120
        }
      },
      {
        "Block": "Stone",
        "Height": 3
      }
    ],
    "DynamicLayers": [
      {
        "Block": "Grass_Alpine",
        "MinHeight": 1,
        "MaxHeight": 1,
        "Conditions": {
          "HeightMax": 140,
          "SlopeMax": 30
        }
      },
      {
        "Block": "Snow",
        "MinHeight": 1,
        "MaxHeight": 3,
        "HeightSupplier": {
          "Type": "Perlin",
          "Scale": 0.05,
          "Octaves": 2
        },
        "Conditions": {
          "HeightMin": 140
        }
      }
    ]
  }
}
```

### Swamp Biome Layers

```json
{
  "LayerContainer": {
    "Filling": "Stone",
    "StaticLayers": [
      {
        "Block": "Mud",
        "Height": 3,
        "Conditions": {
          "HeightMax": 65
        }
      },
      {
        "Block": "Dirt_Wet",
        "Height": 4
      },
      {
        "Block": "Clay",
        "Height": 2
      }
    ],
    "DynamicLayers": [
      {
        "Block": "Grass_Swamp",
        "MinHeight": 1,
        "MaxHeight": 1,
        "Conditions": {
          "HeightMin": 65
        }
      },
      {
        "Block": "Mud",
        "MinHeight": 1,
        "MaxHeight": 2,
        "HeightSupplier": {
          "Type": "Voronoi",
          "Scale": 0.02,
          "Jitter": 0.6
        },
        "Conditions": {
          "HeightMax": 65
        }
      }
    ]
  }
}
```

---

## Subsurface Layers

Subsurface layers generate below the main terrain, useful for ore veins and underground features.

### Properties

| Property | Type | Description |
|----------|------|-------------|
| `Block` | string | Block type |
| `HeightRange` | [min, max] | Y-level range |
| `Density` | float | Spawn probability (0.0-1.0) |
| `ClusterSize` | int | Blocks per cluster |
| `NoiseConfig` | object | Noise configuration |

### Example: Ore Distribution

```json
{
  "SubsurfaceLayers": [
    {
      "Block": "Ore_Iron",
      "HeightRange": [5, 64],
      "Density": 0.008,
      "ClusterSize": 8,
      "NoiseConfig": {
        "Type": "Perlin",
        "Scale": 0.1
      }
    },
    {
      "Block": "Ore_Gold",
      "HeightRange": [5, 32],
      "Density": 0.003,
      "ClusterSize": 6
    },
    {
      "Block": "Ore_Diamond",
      "HeightRange": [5, 16],
      "Density": 0.001,
      "ClusterSize": 4
    }
  ]
}
```

---

## Related Documentation

- [World Generation Overview](worldgen.md) - Pipeline and priority system
- [Biomes](worldgen-biomes.md) - LayerContainer in biome context
- [Caves](worldgen-caves.md) - How caves interact with terrain
- [Block System](blocks.md) - Block types and properties
