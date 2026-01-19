# Zone System

Zones are the top-level organizational unit in world generation, dividing the world into distinct regions with unique biomes, caves, and environmental characteristics. Each zone controls which biomes can appear within it and how they're distributed.

## Quick Navigation

| Section | Description |
|---------|-------------|
| [Zone Record](#zone-record) | Core zone properties and configuration |
| [ZonePatternGenerator](#zonepatterngenerator) | Voronoi-based zone distribution |
| [BiomePatternGenerator](#biomepatterngenerator) | Biome distribution within zones |
| [ZoneDiscoveryConfig](#zonediscoveryconfig) | Discovery UI and notifications |
| [UniquePrefabContainer](#uniqueprefabcontainer) | Zone-specific unique structures |
| [CaveGenerator Config](#cavegenerator-config) | Cave type assignments |

---

## Zone Record

Zones are defined as JSON records with the following structure:

| Property | Type | Required | Description |
|----------|------|----------|-------------|
| `Id` | string | Yes | Unique zone identifier |
| `Name` | string | Yes | Display name for discovery UI |
| `BiomePatternGenerator` | object | Yes | Controls biome distribution within zone |
| `CaveGenerator` | object | No | Cave type configuration for zone |
| `DiscoveryConfig` | object | No | UI settings when player discovers zone |
| `UniquePrefabContainer` | object | No | Globally unique structures in zone |
| `EnvironmentOverride` | string | No | Environment settings override |

### File Location

Zone files are stored in `Server/WorldGen/Zone/`:

```
Server/WorldGen/Zone/
├── Zone_Emerald_Grove.json
├── Zone_Howling_Sands.json
├── Zone_Borea.json
└── Zone_Devastated_Lands.json
```

### Basic Zone Example

```json
{
  "Id": "Zone_Emerald_Grove",
  "Name": "Emerald Grove",
  "BiomePatternGenerator": {
    "Type": "Voronoi",
    "Biomes": [
      { "BiomeId": "Biome_Forest", "Weight": 0.5 },
      { "BiomeId": "Biome_Meadow", "Weight": 0.3 },
      { "BiomeId": "Biome_River", "Weight": 0.2 }
    ],
    "CellSize": 128,
    "Jitter": 0.4
  },
  "CaveGenerator": {
    "CaveTypes": [
      { "CaveTypeId": "Cave_Limestone", "Frequency": 0.03 }
    ]
  },
  "DiscoveryConfig": {
    "DisplayOnDiscover": true,
    "Icon": "Icons/Zones/EmeraldGrove.png",
    "SoundEventId": "SFX_Zone_Discovery_Forest"
  }
}
```

---

## ZonePatternGenerator

The `ZonePatternGenerator` determines how zones are distributed across the world map. It uses Voronoi-based cell partitioning to create natural-looking region boundaries.

### Properties

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `Type` | string | `"Voronoi"` | Pattern generation algorithm |
| `Zones` | array | - | Weighted list of zones to distribute |
| `CellSize` | int | 512 | Size of Voronoi cells in blocks |
| `Jitter` | float | 0.5 | Randomness in cell boundaries (0.0-1.0) |
| `Seed` | long | - | Random seed for reproducibility |
| `BorderBlend` | int | 16 | Blending distance at zone borders |

### Zone Entry Properties

Each entry in the `Zones` array:

| Property | Type | Description |
|----------|------|-------------|
| `ZoneId` | string | Reference to zone definition |
| `Weight` | float | Relative frequency (higher = more common) |
| `MinDistance` | int | Minimum distance from spawn (blocks) |
| `MaxDistance` | int | Maximum distance from spawn (blocks) |
| `RequiredNeighbors` | array | Zone IDs that must be adjacent |
| `ExcludedNeighbors` | array | Zone IDs that cannot be adjacent |

### World-Level Zone Distribution

The zone pattern generator is configured at the world level:

```json
{
  "ZonePatternGenerator": {
    "Type": "Voronoi",
    "CellSize": 1024,
    "Jitter": 0.45,
    "BorderBlend": 32,
    "Zones": [
      {
        "ZoneId": "Zone_Emerald_Grove",
        "Weight": 1.0,
        "MaxDistance": 2000
      },
      {
        "ZoneId": "Zone_Howling_Sands",
        "Weight": 0.8,
        "MinDistance": 1500,
        "ExcludedNeighbors": ["Zone_Borea"]
      },
      {
        "ZoneId": "Zone_Borea",
        "Weight": 0.6,
        "MinDistance": 3000,
        "RequiredNeighbors": ["Zone_Emerald_Grove"]
      },
      {
        "ZoneId": "Zone_Devastated_Lands",
        "Weight": 0.3,
        "MinDistance": 5000
      }
    ]
  }
}
```

### Voronoi Cell Visualization

```
┌──────────────────────────────────────────────────┐
│                                                   │
│     Zone A          ╱╲          Zone B           │
│                   ╱    ╲                          │
│     ●           ╱        ╲         ●              │
│   (seed)      ╱    Zone   ╲     (seed)            │
│             ╱       C       ╲                     │
│            ╱                  ╲                   │
│          ╱          ●          ╲                  │
│        ╱         (seed)          ╲                │
│      ╱                             ╲              │
│    ╱                                 ╲            │
├──────────────────────────────────────────────────┤
│  Cell boundaries form where points are equidistant │
│  from neighboring seeds. Jitter randomizes seeds.  │
└──────────────────────────────────────────────────┘
```

---

## BiomePatternGenerator

Within each zone, the `BiomePatternGenerator` controls how biomes are distributed. This creates local variation within a zone's overall theme.

### Properties

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `Type` | string | `"Voronoi"` | Pattern algorithm |
| `Biomes` | array | - | Weighted biome list |
| `CellSize` | int | 64 | Biome cell size (smaller than zones) |
| `Jitter` | float | 0.5 | Cell boundary randomness |
| `NoiseScale` | float | 0.01 | Additional noise for variation |
| `HeightInfluence` | float | 0.0 | How much terrain height affects biome selection |

### Biome Entry Properties

| Property | Type | Description |
|----------|------|-------------|
| `BiomeId` | string | Reference to biome definition |
| `Weight` | float | Relative frequency |
| `HeightRange` | [min, max] | Height range where biome appears |
| `SlopeRange` | [min, max] | Slope range (degrees) for biome |
| `NoiseThreshold` | float | Minimum noise value to appear |

### Height-Based Biome Selection

```json
{
  "BiomePatternGenerator": {
    "Type": "Voronoi",
    "CellSize": 96,
    "HeightInfluence": 0.7,
    "Biomes": [
      {
        "BiomeId": "Biome_Beach",
        "Weight": 1.0,
        "HeightRange": [60, 65]
      },
      {
        "BiomeId": "Biome_Forest",
        "Weight": 1.0,
        "HeightRange": [65, 120]
      },
      {
        "BiomeId": "Biome_Alpine",
        "Weight": 0.8,
        "HeightRange": [120, 180]
      },
      {
        "BiomeId": "Biome_Snow_Peak",
        "Weight": 0.6,
        "HeightRange": [180, 256]
      }
    ]
  }
}
```

### Slope-Based Selection

Slope values are in degrees (0 = flat, 90 = vertical):

```json
{
  "Biomes": [
    {
      "BiomeId": "Biome_Meadow",
      "Weight": 1.0,
      "SlopeRange": [0, 20]
    },
    {
      "BiomeId": "Biome_Rocky_Slope",
      "Weight": 0.8,
      "SlopeRange": [20, 45]
    },
    {
      "BiomeId": "Biome_Cliff",
      "Weight": 0.6,
      "SlopeRange": [45, 90]
    }
  ]
}
```

---

## ZoneDiscoveryConfig

Controls the UI notification when a player first enters a zone.

### Properties

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `DisplayOnDiscover` | boolean | true | Show discovery notification |
| `Icon` | string | - | Path to zone icon image |
| `SoundEventId` | string | - | Sound to play on discovery |
| `Duration` | float | 3.0 | Notification display time (seconds) |
| `FadeIn` | float | 0.5 | Fade in duration |
| `FadeOut` | float | 0.5 | Fade out duration |
| `TextColor` | string | `"#FFFFFF"` | Zone name text color |
| `SubtitleKey` | string | - | Translation key for subtitle |

### Example Configuration

```json
{
  "DiscoveryConfig": {
    "DisplayOnDiscover": true,
    "Icon": "Icons/Zones/HowlingSands.png",
    "SoundEventId": "SFX_Zone_Discovery_Desert",
    "Duration": 4.0,
    "FadeIn": 0.8,
    "FadeOut": 1.0,
    "TextColor": "#FFD700",
    "SubtitleKey": "zone.howling_sands.subtitle"
  }
}
```

### Discovery UI Layout

```
┌────────────────────────────────────────┐
│                                        │
│     ┌──────┐                           │
│     │ ICON │   ZONE NAME               │
│     └──────┘   Subtitle text           │
│                                        │
└────────────────────────────────────────┘
         ↑ FadeIn → Display → FadeOut ↑
```

### Java Event Integration

Zone discovery fires a `DiscoverZoneEvent`:

```java
import com.hypixel.hytale.server.core.adventure.event.DiscoverZoneEvent;

eventRegistry.register(DiscoverZoneEvent.class, event -> {
    PlayerRef player = event.getPlayer();
    ZoneDiscoveryInfo zoneInfo = event.getZoneDiscoveryInfo();

    player.sendMessage(Message.raw(
        "Discovered: " + zoneInfo.getZoneName()
    ));
});
```

> **See also:** [Adventure Events](adventure.md#discoveryzoneevent)

---

## UniquePrefabContainer

Zones can specify globally unique structures that appear only once in the entire world within that zone.

### Properties

| Property | Type | Description |
|----------|------|-------------|
| `Prefabs` | array | List of unique prefab configurations |

### Unique Prefab Entry

| Property | Type | Description |
|----------|------|-------------|
| `PrefabId` | string | Prefab file reference |
| `ExclusionRadius` | int | Minimum distance from other unique prefabs |
| `MinDistanceFromSpawn` | int | Minimum distance from world spawn |
| `MaxDistanceFromSpawn` | int | Maximum distance from world spawn |
| `PlacementConditions` | object | Terrain requirements |
| `ShowOnMap` | boolean | Display marker on world map |
| `MapIcon` | string | Icon for map marker |

### Example

```json
{
  "UniquePrefabContainer": {
    "Prefabs": [
      {
        "PrefabId": "Structures/Dungeons/Ancient_Temple",
        "ExclusionRadius": 500,
        "MinDistanceFromSpawn": 1000,
        "MaxDistanceFromSpawn": 5000,
        "ShowOnMap": true,
        "MapIcon": "Icons/Map/Temple.png",
        "PlacementConditions": {
          "MinFlatArea": 20,
          "HeightRange": [70, 100],
          "AvoidWater": true
        }
      },
      {
        "PrefabId": "Structures/Villages/Forest_Village",
        "ExclusionRadius": 800,
        "MinDistanceFromSpawn": 500,
        "ShowOnMap": true,
        "MapIcon": "Icons/Map/Village.png"
      }
    ]
  }
}
```

> **See also:** [Unique Prefab Placement](worldgen-prefabs.md#uniqueprefabgenerator)

---

## CaveGenerator Config

Each zone specifies which cave types can generate within it.

### Properties

| Property | Type | Description |
|----------|------|-------------|
| `CaveTypes` | array | List of cave type assignments |
| `GlobalModifier` | float | Multiplier for all cave frequencies |

### Cave Type Entry

| Property | Type | Description |
|----------|------|-------------|
| `CaveTypeId` | string | Reference to cave type definition |
| `Frequency` | float | Spawn frequency (0.0-1.0) |
| `HeightRange` | [min, max] | Vertical range for cave entries |

### Example

```json
{
  "CaveGenerator": {
    "GlobalModifier": 1.0,
    "CaveTypes": [
      {
        "CaveTypeId": "Cave_Limestone",
        "Frequency": 0.04,
        "HeightRange": [30, 80]
      },
      {
        "CaveTypeId": "Cave_Crystal",
        "Frequency": 0.01,
        "HeightRange": [10, 50]
      },
      {
        "CaveTypeId": "Cave_Lava",
        "Frequency": 0.005,
        "HeightRange": [5, 30]
      }
    ]
  }
}
```

> **See also:** [Cave System](worldgen-caves.md)

---

## ZoneBiomeResult

At runtime, the world generator produces `ZoneBiomeResult` objects that link a zone with its selected biome at a specific position.

### Java Class

```java
public record ZoneBiomeResult(
    Zone zone,
    Biome biome,
    float biomeStrength,
    BiomeInterpolation interpolation
) {
    // Get the zone ID
    public String getZoneId() { return zone.id(); }

    // Get the biome ID
    public String getBiomeId() { return biome.getId(); }

    // Get interpolation weight (for blending)
    public float getStrength() { return biomeStrength; }
}
```

### Usage in Generation

```java
// Get zone and biome at position
ZoneBiomeResult result = worldGen.getZoneBiome(worldX, worldZ);

Zone zone = result.zone();
Biome biome = result.biome();
float strength = result.biomeStrength();

// Use biome data for terrain generation
LayerContainer layers = biome.getLayerContainer();
CoverContainer covers = biome.getCoverContainer();
```

---

## Complete Zone Example

A full zone definition with all features:

```json
{
  "Id": "Zone_Howling_Sands",
  "Name": "Howling Sands",

  "BiomePatternGenerator": {
    "Type": "Voronoi",
    "CellSize": 96,
    "Jitter": 0.5,
    "HeightInfluence": 0.3,
    "Biomes": [
      {
        "BiomeId": "Biome_Desert",
        "Weight": 0.6,
        "HeightRange": [64, 90]
      },
      {
        "BiomeId": "Biome_Desert_Dunes",
        "Weight": 0.25,
        "HeightRange": [70, 110]
      },
      {
        "BiomeId": "Biome_Desert_Oasis",
        "Weight": 0.1,
        "HeightRange": [60, 70]
      },
      {
        "BiomeId": "Biome_Desert_Canyon",
        "Weight": 0.05,
        "SlopeRange": [30, 90]
      }
    ]
  },

  "CaveGenerator": {
    "GlobalModifier": 0.8,
    "CaveTypes": [
      {
        "CaveTypeId": "Cave_Sandstone",
        "Frequency": 0.03,
        "HeightRange": [20, 70]
      },
      {
        "CaveTypeId": "Cave_Tomb",
        "Frequency": 0.008,
        "HeightRange": [30, 60]
      }
    ]
  },

  "UniquePrefabContainer": {
    "Prefabs": [
      {
        "PrefabId": "Structures/Desert/Pyramid_Large",
        "ExclusionRadius": 1000,
        "MinDistanceFromSpawn": 2000,
        "ShowOnMap": true,
        "MapIcon": "Icons/Map/Pyramid.png",
        "PlacementConditions": {
          "MinFlatArea": 40,
          "HeightRange": [70, 90],
          "AvoidWater": true
        }
      },
      {
        "PrefabId": "Structures/Desert/Oasis_Temple",
        "ExclusionRadius": 600,
        "MinDistanceFromSpawn": 1500,
        "ShowOnMap": true
      }
    ]
  },

  "DiscoveryConfig": {
    "DisplayOnDiscover": true,
    "Icon": "Icons/Zones/HowlingSands.png",
    "SoundEventId": "SFX_Zone_Discovery_Desert",
    "Duration": 4.0,
    "FadeIn": 0.6,
    "FadeOut": 0.8,
    "TextColor": "#F4A460",
    "SubtitleKey": "zone.howling_sands.subtitle"
  },

  "EnvironmentOverride": "Environment_Desert"
}
```

---

## Related Documentation

- [World Generation Overview](worldgen.md) - Pipeline and priority system
- [Biomes](worldgen-biomes.md) - Biome types and containers
- [Caves](worldgen-caves.md) - Cave system configuration
- [Unique Prefabs](worldgen-prefabs.md#uniqueprefabgenerator) - Unique structure placement
- [Adventure Events](adventure.md) - Zone discovery events
