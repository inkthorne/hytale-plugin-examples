# Cave System

The cave system creates underground structures through a node-based approach. Cave types define entry points and conditions, while cave nodes determine the shape and contents of each chamber. Nodes connect through corridors, creating organic branching cave networks.

## Quick Navigation

| Section | Description |
|---------|-------------|
| [CaveType](#cavetype) | Cave entry points and conditions |
| [CaveNodeType](#cavenodetype) | Node templates with shapes and contents |
| [CaveNodeShape](#cavenodeshape) | Carving shapes (cylinder, ellipsoid, pipe, etc.) |
| [CaveNodeChildEntry](#cavenodechildentry) | Corridor connections between nodes |
| [CaveNodeCoverEntry](#cavenodecoverentry) | Surface materials (walls, floors, ceilings) |
| [CavePrefabContainer](#caveprefabcontainer) | Structure placement within caves |
| [CavePopulator](#cavepopulator) | Cave generation pipeline |

---

## CaveType

A `CaveType` defines a category of cave with its entry conditions, starting node, and placement constraints.

### Properties

| Property | Type | Required | Description |
|----------|------|----------|-------------|
| `Id` | string | Yes | Unique cave type identifier |
| `EntryNode` | string | Yes | Starting CaveNodeType reference |
| `Frequency` | float | No | Base spawn frequency (0.0-1.0) |
| `HeightRange` | [min, max] | No | Valid Y-levels for entry |
| `PitchRange` | [min, max] | No | Entry tunnel pitch angle (degrees) |
| `YawRange` | [min, max] | No | Entry tunnel yaw angle (degrees) |
| `BiomeMask` | array | No | Biomes where cave can spawn |
| `BiomeExclude` | array | No | Biomes where cave cannot spawn |
| `FluidLevel` | int | No | Y-level of fluid flooding |
| `FluidType` | string | No | Fluid type for flooding |
| `MaxDepth` | int | No | Maximum node recursion depth |
| `Priority` | int | No | Cave priority (higher overrides) |

### File Location

Cave types are stored in `Server/WorldGen/Cave/`:

```
Server/WorldGen/Cave/
├── Cave_Limestone.json
├── Cave_Crystal.json
├── Cave_Lava.json
└── Cave_Dungeon.json
```

### Basic CaveType Example

```json
{
  "Id": "Cave_Limestone",
  "EntryNode": "CaveNode_Limestone_Entry",
  "Frequency": 0.04,
  "HeightRange": [20, 80],
  "PitchRange": [-45, -15],
  "YawRange": [0, 360],
  "MaxDepth": 8
}
```

### CaveType with Biome Restrictions

```json
{
  "Id": "Cave_Ice",
  "EntryNode": "CaveNode_Ice_Entry",
  "Frequency": 0.02,
  "HeightRange": [30, 100],
  "BiomeMask": ["Biome_Tundra", "Biome_Snow_Peak", "Biome_Glacier"],
  "FluidLevel": 25,
  "FluidType": "Water_Frozen",
  "MaxDepth": 6
}
```

### CaveType with Lava Flooding

```json
{
  "Id": "Cave_Volcanic",
  "EntryNode": "CaveNode_Volcanic_Entry",
  "Frequency": 0.01,
  "HeightRange": [5, 40],
  "FluidLevel": 15,
  "FluidType": "Lava",
  "Priority": 10,
  "MaxDepth": 5
}
```

---

## CaveNodeType

A `CaveNodeType` is a template for individual cave chambers. Each node has a shape, optional covers, and can spawn child nodes through corridors.

### Properties

| Property | Type | Required | Description |
|----------|------|----------|-------------|
| `Id` | string | Yes | Unique node type identifier |
| `Shape` | object | Yes | Carving shape configuration |
| `Fillings` | array | No | Blocks to place inside carved area |
| `Covers` | array | No | Surface material configurations |
| `Children` | array | No | Child node connections |
| `Prefabs` | object | No | Prefab placement configuration |
| `Priority` | int | No | Block priority for carving |
| `CanBranch` | boolean | No | Allow multiple children (default: true) |
| `MaxInstances` | int | No | Maximum instances in cave system |

### File Location

Cave nodes are stored in `Server/WorldGen/CaveNode/`:

```
Server/WorldGen/CaveNode/
├── Limestone/
│   ├── CaveNode_Limestone_Entry.json
│   ├── CaveNode_Limestone_Chamber.json
│   └── CaveNode_Limestone_Tunnel.json
├── Crystal/
│   └── ...
└── Dungeon/
    └── ...
```

### Basic CaveNodeType

```json
{
  "Id": "CaveNode_Limestone_Chamber",
  "Shape": {
    "Type": "Ellipsoid",
    "RadiusX": [8, 15],
    "RadiusY": [4, 8],
    "RadiusZ": [8, 15]
  },
  "Covers": [
    {
      "Target": "Floor",
      "Block": "Stone_Mossy",
      "Density": 0.4
    },
    {
      "Target": "Ceiling",
      "Block": "Stalactite",
      "Density": 0.15
    }
  ],
  "Children": [
    {
      "NodeType": "CaveNode_Limestone_Tunnel",
      "Chance": 0.7,
      "MaxCount": 3
    }
  ]
}
```

---

## CaveNodeShape

Shapes define how terrain is carved when a node is placed. Each shape type has specific parameters.

### Shape Types

| Type | Description |
|------|-------------|
| `Cylinder` | Vertical or horizontal cylinder |
| `Ellipsoid` | 3D ellipsoid (egg shape) |
| `Pipe` | Curved tube/corridor |
| `Tetrahedron` | Four-sided pyramid |
| `EmptyLine` | Simple straight corridor |
| `Distorted` | Shape with noise distortion |
| `Prefab` | Uses a prefab for shape |

### Cylinder Shape

Carves a cylindrical volume:

```json
{
  "Shape": {
    "Type": "Cylinder",
    "Radius": [4, 8],
    "Height": [6, 12],
    "Axis": "Y",
    "Capped": true
  }
}
```

| Property | Type | Description |
|----------|------|-------------|
| `Radius` | [min, max] | Cylinder radius range |
| `Height` | [min, max] | Cylinder height range |
| `Axis` | string | `"X"`, `"Y"`, or `"Z"` |
| `Capped` | boolean | Flat or rounded ends |

### Ellipsoid Shape

Carves an ellipsoidal (egg-shaped) volume:

```json
{
  "Shape": {
    "Type": "Ellipsoid",
    "RadiusX": [6, 12],
    "RadiusY": [4, 8],
    "RadiusZ": [6, 12],
    "Squash": 0.8
  }
}
```

| Property | Type | Description |
|----------|------|-------------|
| `RadiusX` | [min, max] | X-axis radius range |
| `RadiusY` | [min, max] | Y-axis radius range |
| `RadiusZ` | [min, max] | Z-axis radius range |
| `Squash` | float | Vertical squashing factor |

### Pipe Shape

Carves a curved tube for corridors:

```json
{
  "Shape": {
    "Type": "Pipe",
    "Radius": [2, 4],
    "Length": [10, 25],
    "Curvature": 0.3,
    "Segments": 8
  }
}
```

| Property | Type | Description |
|----------|------|-------------|
| `Radius` | [min, max] | Pipe radius |
| `Length` | [min, max] | Pipe length |
| `Curvature` | float | How much pipe curves (0 = straight) |
| `Segments` | int | Smoothness of curve |

### Tetrahedron Shape

Carves a four-sided pyramid:

```json
{
  "Shape": {
    "Type": "Tetrahedron",
    "Size": [8, 16],
    "Rotation": "Random"
  }
}
```

| Property | Type | Description |
|----------|------|-------------|
| `Size` | [min, max] | Base size range |
| `Rotation` | string | `"None"`, `"Random"`, `"Aligned"` |

### EmptyLine Shape

Simple straight corridor:

```json
{
  "Shape": {
    "Type": "EmptyLine",
    "Width": [3, 5],
    "Height": [3, 5],
    "Length": [8, 20]
  }
}
```

### Distorted Shape

Applies noise distortion to another shape:

```json
{
  "Shape": {
    "Type": "Distorted",
    "BaseShape": {
      "Type": "Ellipsoid",
      "RadiusX": [10, 20],
      "RadiusY": [5, 10],
      "RadiusZ": [10, 20]
    },
    "Noise": {
      "Type": "Perlin",
      "Scale": 0.15,
      "Amplitude": 3
    }
  }
}
```

### Prefab Shape

Uses a prefab to define the carved area:

```json
{
  "Shape": {
    "Type": "Prefab",
    "PrefabId": "CaveShapes/Chamber_Large",
    "Rotation": "Random"
  }
}
```

---

## CaveNodeChildEntry

Child entries define how nodes connect through corridors.

### Properties

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `NodeType` | string | - | Child CaveNodeType reference |
| `Chance` | float | 1.0 | Probability of spawning (0.0-1.0) |
| `MinCount` | int | 0 | Minimum children of this type |
| `MaxCount` | int | 1 | Maximum children of this type |
| `Anchor` | string | `"Random"` | Connection point on parent |
| `Offset` | [x, y, z] | [0,0,0] | Offset from anchor |
| `YawMode` | string | `"Random"` | Yaw angle selection |
| `PitchRange` | [min, max] | [-45, 45] | Pitch angle range |
| `CorridorLength` | [min, max] | [5, 15] | Corridor length before child |
| `CorridorShape` | object | - | Shape for corridor segment |

### Anchor Points

| Anchor | Description |
|--------|-------------|
| `"Random"` | Random point on shape surface |
| `"Center"` | Shape center |
| `"Top"` | Top of shape |
| `"Bottom"` | Bottom of shape |
| `"North"` | North face |
| `"South"` | South face |
| `"East"` | East face |
| `"West"` | West face |

### Yaw Modes

| Mode | Description |
|------|-------------|
| `"Random"` | Random 360° direction |
| `"Opposite"` | Opposite to entry direction |
| `"Perpendicular"` | 90° from entry direction |
| `"Fixed"` | Use YawValue property |

### Basic Child Entry

```json
{
  "Children": [
    {
      "NodeType": "CaveNode_Tunnel",
      "Chance": 0.8,
      "MaxCount": 3,
      "Anchor": "Random",
      "PitchRange": [-30, 10],
      "YawMode": "Random"
    }
  ]
}
```

### Child with Corridor

```json
{
  "Children": [
    {
      "NodeType": "CaveNode_Chamber_Large",
      "Chance": 0.4,
      "MinCount": 0,
      "MaxCount": 1,
      "Anchor": "Bottom",
      "Offset": [0, -2, 0],
      "PitchRange": [-60, -30],
      "CorridorLength": [10, 20],
      "CorridorShape": {
        "Type": "Pipe",
        "Radius": [3, 4]
      }
    }
  ]
}
```

### Multiple Child Types

```json
{
  "Children": [
    {
      "NodeType": "CaveNode_Tunnel",
      "Chance": 0.7,
      "MaxCount": 2,
      "YawMode": "Random"
    },
    {
      "NodeType": "CaveNode_Chamber_Small",
      "Chance": 0.3,
      "MaxCount": 1,
      "Anchor": "Bottom",
      "PitchRange": [-45, -20]
    },
    {
      "NodeType": "CaveNode_Shaft",
      "Chance": 0.2,
      "MaxCount": 1,
      "Anchor": "Bottom",
      "PitchRange": [-90, -75]
    }
  ]
}
```

---

## CaveNodeCoverEntry

Cover entries apply surface materials to cave walls, floors, and ceilings.

### Properties

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `Target` | string | - | Surface type to cover |
| `Block` | string | - | Block type to place |
| `Density` | float | 0.5 | Coverage density (0.0-1.0) |
| `Thickness` | int | 1 | Layer thickness |
| `Conditions` | object | - | Placement conditions |
| `Priority` | int | 7 | Block priority |

### Target Types

| Target | Description |
|--------|-------------|
| `"Floor"` | Horizontal surfaces facing up |
| `"Ceiling"` | Horizontal surfaces facing down |
| `"Wall"` | Vertical surfaces |
| `"All"` | All carved surfaces |

### Basic Cover Entry

```json
{
  "Covers": [
    {
      "Target": "Floor",
      "Block": "Stone_Mossy",
      "Density": 0.6
    }
  ]
}
```

### Complete Cover Configuration

```json
{
  "Covers": [
    {
      "Target": "Floor",
      "Block": "Gravel",
      "Density": 0.4,
      "Thickness": 1
    },
    {
      "Target": "Floor",
      "Block": "Mushroom_Cave",
      "Density": 0.1,
      "Conditions": {
        "LightMax": 5
      }
    },
    {
      "Target": "Ceiling",
      "Block": "Stalactite",
      "Density": 0.2
    },
    {
      "Target": "Ceiling",
      "Block": "Moss_Hanging",
      "Density": 0.15,
      "Conditions": {
        "HeightFromFloor": [4, 20]
      }
    },
    {
      "Target": "Wall",
      "Block": "Stone_Wet",
      "Density": 0.3,
      "Thickness": 2
    },
    {
      "Target": "Wall",
      "Block": "Crystal_Small",
      "Density": 0.05,
      "Conditions": {
        "NoiseMin": 0.7
      }
    }
  ]
}
```

### Cover Conditions

| Property | Type | Description |
|----------|------|-------------|
| `LightMin` | int | Minimum light level |
| `LightMax` | int | Maximum light level |
| `HeightFromFloor` | [min, max] | Distance from floor |
| `HeightFromCeiling` | [min, max] | Distance from ceiling |
| `NoiseMin` | float | Minimum noise value |
| `NoiseMax` | float | Maximum noise value |
| `SlopeMin` | float | Minimum surface slope |
| `SlopeMax` | float | Maximum surface slope |

---

## CavePrefabContainer

Configures structure placement within cave nodes.

### Properties

| Property | Type | Description |
|----------|------|-------------|
| `Prefabs` | array | List of prefab configurations |
| `GlobalChance` | float | Multiplier for all prefab chances |

### Prefab Entry Properties

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `PrefabId` | string | - | Prefab file reference |
| `PrefabList` | string | - | PrefabList reference |
| `Chance` | float | 0.5 | Spawn probability |
| `MaxCount` | int | 1 | Maximum per node |
| `Position` | string | `"Floor"` | Placement position |
| `Rotation` | string | `"Random"` | Rotation mode |
| `Offset` | [x, y, z] | [0,0,0] | Position offset |

### Position Types

| Position | Description |
|----------|-------------|
| `"Floor"` | On cave floor |
| `"Ceiling"` | Hanging from ceiling |
| `"Wall"` | Against wall |
| `"Center"` | Node center |
| `"Random"` | Random valid position |

### Example: Dungeon Room

```json
{
  "Prefabs": {
    "GlobalChance": 1.0,
    "Prefabs": [
      {
        "PrefabId": "Structures/Cave/Chest_Stone",
        "Chance": 0.3,
        "MaxCount": 1,
        "Position": "Floor"
      },
      {
        "PrefabId": "Structures/Cave/Pillar_Broken",
        "Chance": 0.4,
        "MaxCount": 4,
        "Position": "Floor",
        "Rotation": "Random"
      },
      {
        "PrefabList": "CaveDecor_Bones",
        "Chance": 0.2,
        "MaxCount": 3,
        "Position": "Floor"
      },
      {
        "PrefabId": "Structures/Cave/Web_Large",
        "Chance": 0.25,
        "MaxCount": 2,
        "Position": "Ceiling"
      }
    ]
  }
}
```

---

## CavePopulator

The `CavePopulator` is the second generation stage, responsible for carving caves after terrain is generated.

### Generation Pipeline

```
CavePopulator Pipeline
│
├─ 1. collectCaveEntries(chunk)
│     ├─ Get zone's CaveGenerator config
│     ├─ Evaluate frequency at each position
│     └─ Create entry points for matching CaveTypes
│
├─ 2. expandCaveSystem(entry)
│     ├─ Place entry node
│     ├─ Recursively spawn child nodes
│     └─ Track depth to respect MaxDepth
│
├─ 3. carveNode(node)
│     ├─ Apply node shape to terrain
│     ├─ Track carved positions
│     └─ Set block priorities
│
├─ 4. applyCoversSurfaces(node)
│     ├─ Identify floor/wall/ceiling surfaces
│     └─ Apply cover entries
│
└─ 5. placeCavePrefabs(node)
      ├─ Evaluate prefab conditions
      └─ Place prefabs with cave priority
```

### Cave Node Instantiation

```java
// Simplified cave node placement
public class CaveNode {
    private final CaveNodeType type;
    private final Vector3i position;
    private final float yaw;
    private final float pitch;
    private final int depth;

    public void generate(WorldChunk chunk, BlockPriorityChunk priorities) {
        // Carve the shape
        type.getShape().carve(chunk, position, yaw, pitch, priorities);

        // Apply covers to surfaces
        for (CaveNodeCoverEntry cover : type.getCovers()) {
            applyCover(chunk, cover);
        }

        // Place prefabs
        if (type.getPrefabs() != null) {
            placePrefabs(chunk, type.getPrefabs());
        }

        // Spawn children (if depth allows)
        if (depth < maxDepth) {
            for (CaveNodeChildEntry child : type.getChildren()) {
                if (random.nextFloat() < child.getChance()) {
                    spawnChild(chunk, child, depth + 1);
                }
            }
        }
    }
}
```

---

## Complete Cave Examples

### Limestone Cave System

**CaveType:**
```json
{
  "Id": "Cave_Limestone",
  "EntryNode": "CaveNode_Limestone_Entry",
  "Frequency": 0.035,
  "HeightRange": [25, 75],
  "PitchRange": [-40, -10],
  "MaxDepth": 10
}
```

**Entry Node:**
```json
{
  "Id": "CaveNode_Limestone_Entry",
  "Shape": {
    "Type": "Pipe",
    "Radius": [2, 3],
    "Length": [8, 15],
    "Curvature": 0.2
  },
  "Covers": [
    {
      "Target": "All",
      "Block": "Limestone",
      "Density": 0.7
    }
  ],
  "Children": [
    {
      "NodeType": "CaveNode_Limestone_Chamber",
      "Chance": 1.0,
      "MinCount": 1,
      "MaxCount": 1
    }
  ]
}
```

**Chamber Node:**
```json
{
  "Id": "CaveNode_Limestone_Chamber",
  "Shape": {
    "Type": "Distorted",
    "BaseShape": {
      "Type": "Ellipsoid",
      "RadiusX": [8, 18],
      "RadiusY": [5, 10],
      "RadiusZ": [8, 18]
    },
    "Noise": {
      "Type": "Perlin",
      "Scale": 0.2,
      "Amplitude": 2
    }
  },
  "Covers": [
    {
      "Target": "Floor",
      "Block": "Gravel",
      "Density": 0.3
    },
    {
      "Target": "Floor",
      "Block": "Stone_Mossy",
      "Density": 0.4
    },
    {
      "Target": "Ceiling",
      "Block": "Stalactite",
      "Density": 0.2
    },
    {
      "Target": "Floor",
      "Block": "Stalagmite",
      "Density": 0.1
    },
    {
      "Target": "Wall",
      "Block": "Limestone_Wet",
      "Density": 0.5,
      "Thickness": 2
    }
  ],
  "Prefabs": {
    "Prefabs": [
      {
        "PrefabId": "Structures/Cave/Pool_Small",
        "Chance": 0.2,
        "Position": "Floor"
      }
    ]
  },
  "Children": [
    {
      "NodeType": "CaveNode_Limestone_Tunnel",
      "Chance": 0.6,
      "MaxCount": 3,
      "Anchor": "Random",
      "YawMode": "Random",
      "PitchRange": [-20, 20]
    },
    {
      "NodeType": "CaveNode_Limestone_Chamber",
      "Chance": 0.3,
      "MaxCount": 1,
      "Anchor": "Bottom",
      "PitchRange": [-45, -25],
      "CorridorLength": [10, 20]
    }
  ]
}
```

**Tunnel Node:**
```json
{
  "Id": "CaveNode_Limestone_Tunnel",
  "Shape": {
    "Type": "Pipe",
    "Radius": [2, 4],
    "Length": [15, 30],
    "Curvature": 0.4,
    "Segments": 6
  },
  "Covers": [
    {
      "Target": "All",
      "Block": "Limestone",
      "Density": 0.6
    }
  ],
  "Children": [
    {
      "NodeType": "CaveNode_Limestone_Chamber",
      "Chance": 0.5,
      "MaxCount": 1
    },
    {
      "NodeType": "CaveNode_Limestone_Tunnel",
      "Chance": 0.4,
      "MaxCount": 2
    }
  ]
}
```

### Crystal Cave System

```json
{
  "Id": "Cave_Crystal",
  "EntryNode": "CaveNode_Crystal_Entry",
  "Frequency": 0.01,
  "HeightRange": [10, 50],
  "PitchRange": [-60, -30],
  "BiomeMask": ["Biome_Mountain", "Biome_Deep_Forest"],
  "MaxDepth": 5
}
```

```json
{
  "Id": "CaveNode_Crystal_Chamber",
  "Shape": {
    "Type": "Ellipsoid",
    "RadiusX": [12, 25],
    "RadiusY": [8, 15],
    "RadiusZ": [12, 25]
  },
  "Covers": [
    {
      "Target": "Floor",
      "Block": "Crystal_Cluster",
      "Density": 0.15,
      "Conditions": {
        "NoiseMin": 0.6
      }
    },
    {
      "Target": "Ceiling",
      "Block": "Crystal_Hanging",
      "Density": 0.2
    },
    {
      "Target": "Wall",
      "Block": "Stone_Crystal",
      "Density": 0.4,
      "Thickness": 3
    },
    {
      "Target": "All",
      "Block": "Crystal_Small",
      "Density": 0.08
    }
  ],
  "Prefabs": {
    "Prefabs": [
      {
        "PrefabId": "Structures/Cave/Crystal_Formation_Large",
        "Chance": 0.4,
        "MaxCount": 2,
        "Position": "Floor"
      },
      {
        "PrefabId": "Structures/Cave/Crystal_Pillar",
        "Chance": 0.3,
        "MaxCount": 3,
        "Position": "Floor"
      }
    ]
  },
  "Children": [
    {
      "NodeType": "CaveNode_Crystal_Tunnel",
      "Chance": 0.5,
      "MaxCount": 2
    }
  ]
}
```

### Dungeon Cave System

```json
{
  "Id": "Cave_Dungeon",
  "EntryNode": "CaveNode_Dungeon_Entrance",
  "Frequency": 0.005,
  "HeightRange": [30, 60],
  "PitchRange": [-30, -10],
  "MaxDepth": 6,
  "Priority": 15
}
```

```json
{
  "Id": "CaveNode_Dungeon_Room",
  "Shape": {
    "Type": "Prefab",
    "PrefabId": "CaveShapes/Dungeon_Room_Medium",
    "Rotation": "Random"
  },
  "Covers": [
    {
      "Target": "Floor",
      "Block": "Stone_Brick_Cracked",
      "Density": 0.3
    }
  ],
  "Prefabs": {
    "Prefabs": [
      {
        "PrefabId": "Structures/Dungeon/Chest_Iron",
        "Chance": 0.4,
        "MaxCount": 1,
        "Position": "Floor"
      },
      {
        "PrefabId": "Structures/Dungeon/Spawner_Skeleton",
        "Chance": 0.3,
        "MaxCount": 1,
        "Position": "Center"
      },
      {
        "PrefabList": "DungeonDecor_Cobwebs",
        "Chance": 0.6,
        "MaxCount": 5,
        "Position": "Random"
      }
    ]
  },
  "Children": [
    {
      "NodeType": "CaveNode_Dungeon_Corridor",
      "Chance": 0.8,
      "MaxCount": 3,
      "Anchor": "Random"
    },
    {
      "NodeType": "CaveNode_Dungeon_Room",
      "Chance": 0.4,
      "MaxCount": 1,
      "CorridorLength": [8, 15]
    }
  ]
}
```

---

## Related Documentation

- [World Generation Overview](worldgen.md) - Pipeline and priority system
- [Zones](worldgen-zones.md) - CaveGenerator configuration per zone
- [Terrain](worldgen-terrain.md) - How caves interact with terrain
- [Prefab API](prefabs.md) - Prefab loading and placement
- [Block System](blocks.md) - Block types and properties
