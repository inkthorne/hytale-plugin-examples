# World Generation

World generation in Hytale uses a data-driven pipeline that creates terrain, caves, and structures through a layered system of zones, biomes, and populators. Each stage adds content with configurable priority levels to ensure proper block resolution.

## Quick Navigation

| Category | File | Description |
|----------|------|-------------|
| [Zones](worldgen-zones.md) | `worldgen-zones.md` | Zone system, Voronoi distribution, discovery UI |
| [Biomes](worldgen-biomes.md) | `worldgen-biomes.md` | Biome types, containers, interpolation |
| [Terrain](worldgen-terrain.md) | `worldgen-terrain.md` | Static/dynamic layers, block population |
| [Caves](worldgen-caves.md) | `worldgen-caves.md` | Cave types, nodes, shapes, corridors |
| [Prefabs](worldgen-prefabs.md) | `worldgen-prefabs.md` | Surface/unique structure placement |

---

## Generation Pipeline

World generation proceeds through a series of populators, each responsible for a specific type of content:

```
┌─────────────────────────────────────────────────────────────────┐
│                    World Generation Pipeline                     │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  1. BlockPopulator                                               │
│     └─ Generates terrain layers, surface covers                  │
│                                                                  │
│  2. CavePopulator                                                │
│     └─ Carves cave systems, places cave prefabs                  │
│                                                                  │
│  3. PrefabPopulator                                              │
│     └─ Places surface structures from biome containers           │
│                                                                  │
│  4. WaterPopulator                                               │
│     └─ Fills water bodies, handles fluid simulation              │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### BlockPopulator

The first stage generates base terrain using the zone and biome system:

1. **Zone Selection** - Determines which zone applies at each position using Voronoi-based pattern generation
2. **Biome Selection** - Within each zone, selects biomes using the zone's `BiomePatternGenerator`
3. **Column Generation** - For each block column, generates filling blocks and terrain layers
4. **Cover Placement** - Applies surface covers (grass, plants, debris) based on biome rules

> **See also:** [Terrain Layers](worldgen-terrain.md)

### CavePopulator

The second stage carves cave systems into the terrain:

1. **Entry Node Placement** - Determines cave entry points based on `CaveType` conditions
2. **Node Expansion** - Recursively generates cave nodes following corridor connections
3. **Shape Carving** - Each node's shape (cylinder, ellipsoid, pipe, etc.) carves out terrain
4. **Surface Application** - Applies cave covers to walls, floors, and ceilings
5. **Cave Prefabs** - Places structures within cave nodes

> **See also:** [Cave System](worldgen-caves.md)

### PrefabPopulator

The third stage places surface structures:

1. **Biome Collection** - Gathers all biomes affecting the chunk
2. **Prefab Collection** - Collects prefab containers from each biome
3. **Placement Evaluation** - Tests placement conditions for each prefab
4. **Structure Placement** - Places selected prefabs respecting block priority

> **See also:** [Prefab Placement](worldgen-prefabs.md)

### WaterPopulator

The final stage handles fluid generation:

1. **Water Container Processing** - Reads water configuration from biomes
2. **Surface Level Calculation** - Determines water surface heights
3. **Fluid Filling** - Fills terrain depressions with configured fluid types

> **See also:** [WaterContainer](worldgen-biomes.md#watercontainer)

---

## Block Priority System

When multiple generation stages attempt to place blocks at the same position, the block priority system resolves conflicts. Higher priority values take precedence.

| Priority | Value | Description |
|----------|-------|-------------|
| `NONE` | 0 | No priority (can be overwritten by anything) |
| `FILLING` | 1 | Base filling blocks (stone, dirt) |
| `LAYER` | 2 | Terrain layer blocks |
| `COVER` | 3 | Surface cover blocks (grass, plants) |
| `WATER` | 4 | Water and fluid blocks |
| `CAVE` | 5 | Cave carving (removes terrain) |
| `CAVE_FILLING` | 6 | Cave filling blocks |
| `CAVE_COVER` | 7 | Cave surface covers |
| `PREFAB` | 8 | Surface prefab blocks |
| `CAVE_PREFAB` | 9 | Cave prefab blocks |
| `UNIQUE_PREFAB` | 10 | Globally unique structure blocks |

### Priority Resolution

```
Example: A cave carves through terrain with a prefab

Position (10, 45, 20):
  - BlockPopulator places "Stone" with priority LAYER (2)
  - CavePopulator carves with priority CAVE (5)
  - Result: Cave wins, block is removed

Position (10, 46, 20):
  - BlockPopulator places "Grass" with priority COVER (3)
  - PrefabPopulator places "Fence" with priority PREFAB (8)
  - Result: Prefab wins, "Fence" is placed
```

The `BlockPriorityChunk` class tracks priorities during generation:

```java
// Internal priority tracking (simplified)
public class BlockPriorityChunk {
    private byte[] priorities;  // One byte per block

    public boolean canPlace(int x, int y, int z, int priority) {
        return priority >= getPriority(x, y, z);
    }

    public void setBlock(int x, int y, int z, int blockType, int priority) {
        if (canPlace(x, y, z, priority)) {
            // Place block and update priority
        }
    }
}
```

---

## File Locations

World generation configuration files are stored in the assets:

| Content Type | Path | Description |
|--------------|------|-------------|
| Zones | `Server/WorldGen/Zone/` | Zone definitions with biome patterns |
| Biomes | `Server/WorldGen/Biome/` | Biome definitions |
| Cave Types | `Server/WorldGen/Cave/` | Cave system configurations |
| Cave Nodes | `Server/WorldGen/CaveNode/` | Cave node templates |
| Layers | `Server/WorldGen/Layer/` | Terrain layer definitions |
| Unique Prefabs | `Server/WorldGen/UniquePrefab/` | Globally unique structure configs |
| Prefab Lists | `Server/PrefabList/` | Prefab directory references |

### Assets.zip Structure

```
Assets.zip/
├── Prefabs/                    # World generation prefabs
│   ├── Trees/                  # Tree variants by biome
│   ├── Rock_Formations/        # Rock and boulder prefabs
│   ├── Structures/             # Buildings, ruins, dungeons
│   └── Vegetation/             # Plants, flowers, bushes
└── Server/
    └── WorldGen/
        ├── Zone/               # Zone configurations
        ├── Biome/              # Biome definitions
        ├── Cave/               # Cave type definitions
        ├── CaveNode/           # Cave node templates
        ├── Layer/              # Terrain layers
        └── UniquePrefab/       # Unique structure configs
```

---

## Quick Start Example

This example shows a minimal zone → biome → terrain layer chain:

### Zone Definition

```json
{
  "Id": "Zone_Emerald_Grove",
  "Name": "Emerald Grove",
  "BiomePatternGenerator": {
    "Type": "Voronoi",
    "Biomes": [
      { "BiomeId": "Biome_Forest", "Weight": 0.6 },
      { "BiomeId": "Biome_Meadow", "Weight": 0.4 }
    ],
    "CellSize": 128
  },
  "CaveGenerator": {
    "CaveTypes": [
      { "CaveTypeId": "Cave_Standard", "Frequency": 0.02 }
    ]
  },
  "DiscoveryConfig": {
    "DisplayOnDiscover": true,
    "Icon": "Icons/Zones/EmeraldGrove.png",
    "SoundEventId": "SFX_Zone_Discover"
  }
}
```

### Biome Definition

```json
{
  "Id": "Biome_Forest",
  "LayerContainer": {
    "Filling": "Stone",
    "StaticLayers": [
      { "Block": "Dirt", "Height": 4 }
    ],
    "DynamicLayers": [
      { "Block": "Grass", "MinHeight": 1, "MaxHeight": 1 }
    ]
  },
  "CoverContainer": {
    "Covers": [
      {
        "Block": "Grass_Tall",
        "Density": 0.3,
        "Conditions": { "SlopeMax": 30 }
      }
    ]
  },
  "PrefabContainer": {
    "Prefabs": [
      {
        "PrefabList": "Trees_Oak",
        "Density": 0.05,
        "MinSpacing": 4
      }
    ]
  }
}
```

### Terrain Layer

```json
{
  "Id": "Layer_Dirt",
  "Block": "Dirt",
  "Height": {
    "Type": "Constant",
    "Value": 4
  }
}
```

---

## Class Hierarchy Overview

```
World Generation Classes
├── Zone System
│   ├── Zone                      (record: id, name, configs)
│   ├── ZonePatternGenerator      (Voronoi zone distribution)
│   ├── ZoneDiscoveryConfig       (UI discovery settings)
│   └── ZoneBiomeResult           (zone + biome at position)
│
├── Biome System
│   ├── Biome (abstract)          (base with containers)
│   ├── TileBiome                 (weighted selection)
│   ├── CustomBiome               (procedural)
│   ├── BiomePatternGenerator     (biome distribution)
│   └── BiomeInterpolation        (smooth transitions)
│
├── Terrain System
│   ├── LayerContainer            (filling, layers)
│   ├── StaticLayer               (fixed-height)
│   ├── DynamicLayer              (noise-based)
│   └── BlockPopulator            (places terrain)
│
├── Cave System
│   ├── CaveType                  (cave definition)
│   ├── CaveNodeType              (node template)
│   ├── CaveNode                  (instantiated node)
│   ├── CaveNodeShape             (carving shape)
│   └── CavePopulator             (places caves)
│
└── Prefab System
    ├── PrefabPopulator           (places structures)
    ├── UniquePrefabGenerator     (globally unique)
    └── UniquePrefabConfiguration (placement rules)
```

---

## Related Documentation

- [Zones](worldgen-zones.md) - Zone records, pattern generators, discovery
- [Biomes](worldgen-biomes.md) - Biome types and containers
- [Terrain](worldgen-terrain.md) - Layer system and block population
- [Caves](worldgen-caves.md) - Cave types, nodes, and shapes
- [Prefabs](worldgen-prefabs.md) - Structure placement
- [Prefab API](prefabs.md) - Java API for prefabs
- [Block System](blocks.md) - Block types and properties
