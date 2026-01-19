# Structure Placement

World generation places structures through two systems: surface prefabs from biome containers and unique prefabs for globally rare structures. The `PrefabPopulator` handles both, using weighted selection and placement conditions to create natural-looking distributions.

## Quick Navigation

| Section | Description |
|---------|-------------|
| [PrefabPopulator](#prefabpopulator) | Prefab placement pipeline |
| [Surface Prefabs](#surface-prefabs) | Biome-based structure placement |
| [UniquePrefabGenerator](#uniqueprefabgenerator) | Globally unique structures |
| [Placement Conditions](#placement-conditions) | Terrain and spacing requirements |
| [Weighted Selection](#weighted-selection) | Prefab variety through weights |

---

## PrefabPopulator

The `PrefabPopulator` is the third generation stage, responsible for placing structures after terrain and caves are generated.

### Generation Pipeline

```
PrefabPopulator Pipeline
│
├─ 1. collectBiomes(chunk)
│     ├─ Sample biomes at multiple positions in chunk
│     └─ Gather all unique biomes affecting chunk
│
├─ 2. collectPrefabs(biomes)
│     ├─ Get PrefabContainer from each biome
│     ├─ Evaluate density at positions
│     └─ Build placement candidate list
│
├─ 3. evaluateConditions(candidates)
│     ├─ Check terrain slope
│     ├─ Check height range
│     ├─ Check flat area requirement
│     ├─ Check spacing from other prefabs
│     └─ Filter to valid placements
│
├─ 4. generatePrefabs(valid)
│     ├─ Select prefab from weighted list
│     ├─ Apply rotation
│     ├─ Fit to heightmap if enabled
│     └─ Place with PREFAB priority
│
└─ 5. processUniquePrefabs(chunk)
      ├─ Check zone's UniquePrefabContainer
      ├─ Evaluate global exclusion radius
      └─ Place unique structures
```

### Priority Integration

Surface prefabs use `PRIORITY_PREFAB` (8), while unique prefabs use `PRIORITY_UNIQUE_PREFAB` (10):

```
Priority Resolution:
│
├─ Terrain (LAYER = 2)
├─ Covers (COVER = 3)
├─ Water (WATER = 4)
├─ Caves (CAVE = 5)
│
├─ Surface Prefabs (PREFAB = 8)      ← Trees, rocks, small structures
│
└─ Unique Prefabs (UNIQUE = 10)      ← Dungeons, temples, villages
```

---

## Surface Prefabs

Surface prefabs are placed based on biome `PrefabContainer` configurations. They include trees, rocks, vegetation clusters, and small structures.

### PrefabContainer Properties

| Property | Type | Description |
|----------|------|-------------|
| `Prefabs` | array | List of prefab placement rules |
| `GlobalDensityModifier` | float | Multiplier for all prefab densities |
| `InheritParent` | boolean | Include parent biome's prefabs |

### Prefab Entry Properties

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `PrefabList` | string | - | Reference to PrefabList file |
| `PrefabId` | string | - | Single prefab reference |
| `Density` | float | 0.01 | Spawn density (0.0-1.0) |
| `Weight` | float | 1.0 | Selection weight |
| `MinSpacing` | int | 4 | Minimum blocks between instances |
| `MaxSpacing` | int | - | Maximum blocks between instances |
| `Conditions` | object | - | Placement requirements |
| `FitHeightmap` | boolean | true | Adjust to terrain height |
| `AllowPartialPlacement` | boolean | false | Place if partially blocked |
| `RotationMode` | string | `"Random"` | Rotation selection |
| `YOffset` | int | 0 | Vertical placement offset |

### Basic Surface Prefab Example

```json
{
  "PrefabContainer": {
    "Prefabs": [
      {
        "PrefabList": "Trees_Oak",
        "Density": 0.04,
        "MinSpacing": 5,
        "Conditions": {
          "SlopeMax": 25,
          "MinFlatArea": 3
        }
      },
      {
        "PrefabList": "Rocks_Forest",
        "Density": 0.02,
        "MinSpacing": 8,
        "FitHeightmap": true
      }
    ]
  }
}
```

### PrefabList Reference

PrefabLists point to directories of prefab files:

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

The prefab populator randomly selects from all prefabs in the referenced directories.

> **See also:** [Prefab API](prefabs.md#prefablist-files)

---

## UniquePrefabGenerator

Unique prefabs are globally rare structures that appear at most once (or a limited number of times) in the entire world. They're configured per-zone through `UniquePrefabContainer`.

### Configuration Location

Unique prefab configurations:
- Zone-level: `Server/WorldGen/Zone/*.json` → `UniquePrefabContainer`
- Standalone: `Server/WorldGen/UniquePrefab/*.json`

### UniquePrefabContainer Properties

| Property | Type | Description |
|----------|------|-------------|
| `Prefabs` | array | List of unique prefab configurations |
| `GlobalEnabled` | boolean | Enable/disable all unique prefabs |

### UniquePrefabConfiguration Properties

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `PrefabId` | string | - | Prefab file reference |
| `ExclusionRadius` | int | 500 | Minimum distance from other unique prefabs |
| `MinDistanceFromSpawn` | int | 0 | Minimum distance from world spawn |
| `MaxDistanceFromSpawn` | int | - | Maximum distance from world spawn |
| `MaxWorldInstances` | int | 1 | Maximum instances in entire world |
| `PlacementConditions` | object | - | Terrain requirements |
| `FitHeightmap` | boolean | true | Adjust to terrain |
| `ShowOnMap` | boolean | false | Display map marker |
| `MapIcon` | string | - | Icon for map marker |
| `MapLabel` | string | - | Label for map marker |
| `SpawnAttempts` | int | 100 | Placement attempts before giving up |
| `RequiredZones` | array | - | Zones where prefab can appear |
| `BiomeMask` | array | - | Valid biomes for placement |

### Placement Conditions (Unique)

| Property | Type | Description |
|----------|------|-------------|
| `MinFlatArea` | int | Required flat area in blocks |
| `HeightRange` | [min, max] | Valid Y-level range |
| `SlopeMax` | float | Maximum terrain slope |
| `AvoidWater` | boolean | Cannot touch water |
| `RequireWater` | boolean | Must be near water |
| `WaterDistance` | int | Distance from water requirement |
| `AvoidCaves` | boolean | Cannot be above caves |
| `TerrainType` | string | Required terrain type |

### Basic Unique Prefab

```json
{
  "UniquePrefabContainer": {
    "Prefabs": [
      {
        "PrefabId": "Structures/Dungeons/Ancient_Temple",
        "ExclusionRadius": 800,
        "MinDistanceFromSpawn": 1500,
        "MaxWorldInstances": 1,
        "ShowOnMap": true,
        "MapIcon": "Icons/Map/Temple.png",
        "PlacementConditions": {
          "MinFlatArea": 30,
          "HeightRange": [70, 100],
          "SlopeMax": 10,
          "AvoidWater": true
        }
      }
    ]
  }
}
```

### Multiple Unique Structures

```json
{
  "UniquePrefabContainer": {
    "GlobalEnabled": true,
    "Prefabs": [
      {
        "PrefabId": "Structures/Villages/Forest_Village_Large",
        "ExclusionRadius": 1000,
        "MinDistanceFromSpawn": 800,
        "MaxWorldInstances": 3,
        "ShowOnMap": true,
        "MapIcon": "Icons/Map/Village.png",
        "MapLabel": "Village",
        "PlacementConditions": {
          "MinFlatArea": 50,
          "HeightRange": [65, 90],
          "SlopeMax": 15
        }
      },
      {
        "PrefabId": "Structures/Towers/Wizard_Tower",
        "ExclusionRadius": 600,
        "MinDistanceFromSpawn": 1200,
        "MaxWorldInstances": 5,
        "ShowOnMap": true,
        "PlacementConditions": {
          "MinFlatArea": 15,
          "HeightRange": [80, 150]
        }
      },
      {
        "PrefabId": "Structures/Ruins/Ancient_Portal",
        "ExclusionRadius": 2000,
        "MinDistanceFromSpawn": 3000,
        "MaxWorldInstances": 1,
        "ShowOnMap": true,
        "MapIcon": "Icons/Map/Portal.png",
        "PlacementConditions": {
          "MinFlatArea": 20,
          "AvoidWater": true,
          "AvoidCaves": true
        }
      }
    ]
  }
}
```

### Spawn Distance Visualization

```
World Spawn (0, 0)
        │
        │ MinDistanceFromSpawn = 1000
        ▼
┌───────────────────────────────────────┐
│ ╭─────────────────────────────────╮   │
│ │    Cannot spawn here           │   │
│ │         (too close)            │   │
│ ╰─────────────────────────────────╯   │
│                                        │
│ ─────── Valid spawn zone ──────────   │
│                                        │
│ ╭─────────────────────────────────╮   │
│ │    Cannot spawn here           │   │
│ │         (too far)              │   │
│ ╰─────────────────────────────────╯   │
│        MaxDistanceFromSpawn = 5000    │
└───────────────────────────────────────┘
```

---

## Placement Conditions

Both surface and unique prefabs support placement conditions that ensure structures appear in appropriate terrain.

### Common Condition Properties

| Property | Type | Description |
|----------|------|-------------|
| `SlopeMin` | float | Minimum terrain slope (degrees) |
| `SlopeMax` | float | Maximum terrain slope (degrees) |
| `HeightRange` | [min, max] | Valid Y-level range |
| `MinFlatArea` | int | Required flat area (blocks) |
| `AvoidWater` | boolean | Cannot place in/near water |
| `RequireWater` | boolean | Must be near water |
| `WaterDistance` | [min, max] | Distance from water |
| `AvoidEdge` | int | Distance from biome edge |
| `RequiredBiomes` | array | Adjacent biomes required |
| `TerrainBlocks` | array | Required surface block types |

### Slope-Based Placement

```json
{
  "Conditions": {
    "SlopeMax": 15
  }
}
```

```
Slope = 0° (flat)      Slope = 30° (steep)    Slope = 60° (cliff)
  ─────────────          ╱                      ╱
                        ╱                      ╱
  ✓ Allowed            ╱                      ╱
                      ╱ ✓ If SlopeMax > 30   ╱ ✗ Usually blocked
```

### Flat Area Requirement

`MinFlatArea` specifies how many adjacent blocks must be within 1 Y-level:

```json
{
  "Conditions": {
    "MinFlatArea": 9
  }
}
```

```
MinFlatArea = 9 requires 3x3 flat area:

✓ Valid:               ✗ Invalid:
┌───┬───┬───┐          ┌───┬───┬───┐
│ 0 │ 0 │ 0 │          │ 0 │ 0 │ 2 │
├───┼───┼───┤          ├───┼───┼───┤
│ 0 │ 0 │ 0 │          │ 0 │ 0 │ 1 │
├───┼───┼───┤          ├───┼───┼───┤
│ 0 │ 0 │ 0 │          │ 0 │ 1 │ 3 │
└───┴───┴───┘          └───┴───┴───┘
(numbers = Y offset)
```

### Water Proximity

```json
{
  "Conditions": {
    "RequireWater": true,
    "WaterDistance": [5, 20]
  }
}
```

Places prefab between 5 and 20 blocks from water.

### Biome Edge Avoidance

```json
{
  "Conditions": {
    "AvoidEdge": 16
  }
}
```

Prevents placement within 16 blocks of biome boundary.

---

## Weighted Selection

When multiple prefabs can spawn at a position, weights determine selection probability.

### Weight Calculation

```
Selection probability = weight / sum(all_weights)

Example:
  Oak_Tree:    weight = 0.6
  Pine_Tree:   weight = 0.3
  Birch_Tree:  weight = 0.1

  Total = 1.0

  Oak probability   = 0.6 / 1.0 = 60%
  Pine probability  = 0.3 / 1.0 = 30%
  Birch probability = 0.1 / 1.0 = 10%
```

### PrefabWeights Class

```java
PrefabWeights weights = new PrefabWeights();
weights.setWeight("Oak_Large", 0.4);
weights.setWeight("Oak_Medium", 0.4);
weights.setWeight("Oak_Small", 0.2);

// Select random prefab
String[] prefabs = {"Oak_Large", "Oak_Medium", "Oak_Small"};
String selected = weights.get(prefabs, name -> name, random);
```

### Weighted PrefabList Example

```json
{
  "PrefabContainer": {
    "Prefabs": [
      {
        "PrefabList": "Trees_Oak",
        "Density": 0.04,
        "Weight": 0.6,
        "MinSpacing": 5
      },
      {
        "PrefabList": "Trees_Pine",
        "Density": 0.04,
        "Weight": 0.3,
        "MinSpacing": 6
      },
      {
        "PrefabList": "Trees_Birch",
        "Density": 0.04,
        "Weight": 0.1,
        "MinSpacing": 5
      }
    ]
  }
}
```

With identical density, the prefab type is selected based on weights: 60% oak, 30% pine, 10% birch.

---

## Rotation Modes

Prefabs can be rotated during placement for variety.

### Rotation Mode Values

| Mode | Description |
|------|-------------|
| `"None"` | No rotation (north-facing) |
| `"Random"` | Random 90° increments |
| `"AlignToSlope"` | Face downhill |
| `"FaceCenter"` | Face biome/zone center |
| `"FaceWater"` | Face nearest water |
| `"FaceAway"` | Face away from something |

### Random Rotation

```json
{
  "RotationMode": "Random"
}
```

Selects randomly from 0°, 90°, 180°, 270°.

### Slope-Aligned Rotation

```json
{
  "RotationMode": "AlignToSlope"
}
```

Rotates prefab to face downhill, useful for structures on slopes.

---

## Fit Heightmap

The `FitHeightmap` property adjusts prefab placement to match terrain.

### Behavior

```json
{
  "FitHeightmap": true
}
```

When enabled:
1. Samples terrain height at multiple points under prefab
2. Adjusts prefab Y-position to average terrain height
3. For large prefabs, may adjust individual sections

```
FitHeightmap: true            FitHeightmap: false

    ┌────┐                        ┌────┐
    │    │                        │    │
────┼────┼────                ────┴────┴────
  Follows terrain            Floats above terrain
```

### Partial Placement

```json
{
  "AllowPartialPlacement": true
}
```

When enabled, allows prefab placement even if some blocks would be blocked by existing terrain or structures. Blocked blocks are skipped.

---

## Complete Biome Prefab Example

```json
{
  "Id": "Biome_Enchanted_Forest",
  "PrefabContainer": {
    "GlobalDensityModifier": 1.2,
    "Prefabs": [
      {
        "PrefabList": "Trees_Enchanted_Large",
        "Density": 0.02,
        "Weight": 0.3,
        "MinSpacing": 12,
        "Conditions": {
          "SlopeMax": 20,
          "MinFlatArea": 6
        },
        "RotationMode": "Random"
      },
      {
        "PrefabList": "Trees_Enchanted_Medium",
        "Density": 0.04,
        "Weight": 0.5,
        "MinSpacing": 7,
        "Conditions": {
          "SlopeMax": 25,
          "MinFlatArea": 4
        }
      },
      {
        "PrefabList": "Trees_Enchanted_Small",
        "Density": 0.06,
        "Weight": 0.2,
        "MinSpacing": 4,
        "Conditions": {
          "SlopeMax": 30
        }
      },
      {
        "PrefabList": "Rocks_Crystal",
        "Density": 0.01,
        "MinSpacing": 15,
        "Conditions": {
          "SlopeMin": 10,
          "SlopeMax": 45
        },
        "FitHeightmap": true
      },
      {
        "PrefabId": "Structures/Forest/Fairy_Ring",
        "Density": 0.002,
        "MinSpacing": 60,
        "Conditions": {
          "MinFlatArea": 12,
          "AvoidEdge": 20
        }
      },
      {
        "PrefabId": "Structures/Forest/Fallen_Giant_Tree",
        "Density": 0.003,
        "MinSpacing": 40,
        "Conditions": {
          "MinFlatArea": 8
        },
        "RotationMode": "Random"
      },
      {
        "PrefabList": "Vegetation_Mushroom_Clusters",
        "Density": 0.015,
        "MinSpacing": 8,
        "Conditions": {
          "SlopeMax": 25,
          "HeightRange": [60, 90]
        }
      }
    ]
  }
}
```

---

## Complete Unique Prefab Example

```json
{
  "Id": "Zone_Emerald_Grove",
  "UniquePrefabContainer": {
    "GlobalEnabled": true,
    "Prefabs": [
      {
        "PrefabId": "Structures/Temples/Forest_Temple",
        "ExclusionRadius": 1200,
        "MinDistanceFromSpawn": 2000,
        "MaxDistanceFromSpawn": 8000,
        "MaxWorldInstances": 1,
        "SpawnAttempts": 200,
        "ShowOnMap": true,
        "MapIcon": "Icons/Map/Temple_Forest.png",
        "MapLabel": "Ancient Temple",
        "BiomeMask": ["Biome_Forest", "Biome_Deep_Forest"],
        "PlacementConditions": {
          "MinFlatArea": 40,
          "HeightRange": [70, 100],
          "SlopeMax": 8,
          "AvoidWater": true,
          "AvoidCaves": true,
          "AvoidEdge": 30
        }
      },
      {
        "PrefabId": "Structures/Villages/Forest_Village",
        "ExclusionRadius": 800,
        "MinDistanceFromSpawn": 500,
        "MaxWorldInstances": 5,
        "SpawnAttempts": 150,
        "ShowOnMap": true,
        "MapIcon": "Icons/Map/Village.png",
        "PlacementConditions": {
          "MinFlatArea": 60,
          "HeightRange": [65, 85],
          "SlopeMax": 12,
          "RequireWater": true,
          "WaterDistance": [10, 50]
        }
      },
      {
        "PrefabId": "Structures/Ruins/Overgrown_Tower",
        "ExclusionRadius": 400,
        "MinDistanceFromSpawn": 1000,
        "MaxWorldInstances": 8,
        "ShowOnMap": true,
        "PlacementConditions": {
          "MinFlatArea": 12,
          "HeightRange": [75, 120],
          "SlopeMax": 20
        }
      },
      {
        "PrefabId": "Structures/Natural/Giant_Tree",
        "ExclusionRadius": 500,
        "MinDistanceFromSpawn": 800,
        "MaxWorldInstances": 10,
        "ShowOnMap": false,
        "BiomeMask": ["Biome_Deep_Forest"],
        "PlacementConditions": {
          "MinFlatArea": 20,
          "HeightRange": [70, 95],
          "SlopeMax": 15
        }
      }
    ]
  }
}
```

---

## Related Documentation

- [World Generation Overview](worldgen.md) - Pipeline and priority system
- [Biomes](worldgen-biomes.md) - PrefabContainer in biome context
- [Zones](worldgen-zones.md) - UniquePrefabContainer in zone context
- [Prefab API](prefabs.md) - Java API for prefabs
- [Prefab Categories](prefabs-categories.md) - Full taxonomy of prefab files
