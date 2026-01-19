# Prefab Categories

Hytale includes 2,455+ prefab files organized into 9 major categories for world generation. This reference documents the complete taxonomy of prefab types, naming conventions, and directory structures.

> **See also:** [Prefabs API](prefabs.md) for Java API and file format documentation

---

## Quick Navigation

| Category | Count | Description |
|----------|-------|-------------|
| [Trees](#trees) | ~492 | Growth stages, species, biome variants |
| [Rock Formations](#rock-formations) | ~676 | Rocks, arches, fossils, pillars, mushrooms |
| [NPC Structures](#npc-structures) | ~452 | Faction buildings, villages, outposts |
| [Monuments](#monuments) | ~231 | Towers, temples, encounters, camps |
| [Mineshafts](#mineshafts) | ~117 | Modular mine components |
| [Dungeons](#dungeons) | ~84 | Modular dungeon rooms |
| [Caves](#caves) | ~180 | Formations, nests, nodes |
| [Plants](#plants) | ~205 | Bushes, cacti, coral, driftwood |
| [Spawn](#spawn) | ~18 | Player spawn layouts |

**Location:** `Assets.zip > Prefabs/`

---

## Naming Conventions

Prefab files follow consistent naming patterns that encode category, type, variant, and size information.

### General Pattern

```
{Category}_{Type}_{Variant}_{Size}_{Number}.prefab.json.lpf
```

### Common Patterns

| Pattern | Example | Description |
|---------|---------|-------------|
| `{Tree}_Stage_{N}` | `Oak_Stage_2` | Tree growth stage (0-3) |
| `{Type}_{Biome}` | `Rock_Desert` | Biome-specific variant |
| `{Type}_{Material}` | `Rock_Basalt` | Material variant |
| `{Type}_{Size}` | `Rock_Large` | Size variant (Small, Medium, Large) |
| `{Faction}_{Building}` | `Kweebec_House` | Faction structure |
| `{Type}_{Number}` | `Rock_01` | Numbered variants |

### Growth Stage Naming (Trees)

| Stage | Age | Description |
|-------|-----|-------------|
| `Stage_0` | Sapling | Newly planted, small |
| `Stage_1` | Young | Growing, moderate size |
| `Stage_2` | Mature | Full-grown, standard |
| `Stage_3` | Ancient | Large, may have special features |

### Biome Variant Suffixes

| Suffix | Biome |
|--------|-------|
| `_Desert` | Arid/desert regions |
| `_Volcanic` | Volcanic/lava regions |
| `_Swamp` | Wetland/swamp regions |
| `_Winter` | Cold/snow regions |
| `_Eternal` | Magical/eternal regions |
| `_Cave` | Underground variants |
| `_Underwater` | Aquatic variants |

---

## Trees

**Location:** `Prefabs/Trees/`
**Count:** ~492 files

Trees are organized by species, with each species having multiple growth stages and biome variants.

### Directory Structure

```
Prefabs/Trees/
├── Oak/
│   ├── Oak_Stage_0.prefab.json.lpf
│   ├── Oak_Stage_1.prefab.json.lpf
│   ├── Oak_Stage_2.prefab.json.lpf
│   └── Oak_Stage_3.prefab.json.lpf
├── Birch/
├── Fir/
├── Palm/
├── Banyan/
├── Willow/
├── Pine/
├── Maple/
├── Cherry/
├── Acacia/
└── ...
```

### Tree Species

| Species | Stages | Biomes | Notes |
|---------|--------|--------|-------|
| `Oak` | 0-3 | Temperate | Common forest tree |
| `Birch` | 0-3 | Temperate, Winter | White bark |
| `Fir` | 0-3 | Temperate, Winter | Coniferous |
| `Pine` | 0-3 | Temperate, Winter | Tall coniferous |
| `Palm` | 0-3 | Desert, Tropical | Coastal regions |
| `Banyan` | 0-3 | Tropical | Large canopy |
| `Willow` | 0-3 | Swamp, Temperate | Drooping branches |
| `Maple` | 0-3 | Temperate | Autumn colors |
| `Cherry` | 0-3 | Temperate | Flowering |
| `Acacia` | 0-3 | Savanna, Desert | Flat canopy |
| `Mangrove` | 0-3 | Swamp | Water roots |
| `Redwood` | 0-3 | Temperate | Giant trees |
| `Cypress` | 0-3 | Swamp | Swamp tree |
| `Baobab` | 0-3 | Savanna | Thick trunk |
| `Jungle` | 0-3 | Tropical | Dense canopy |

### Tree Variants

| Variant | Description |
|---------|-------------|
| `_Dead` | Leafless, decayed appearance |
| `_Moss` | Covered in moss |
| `_Autumn` | Fall foliage colors |
| `_Snow` | Snow-covered branches |
| `_Fruit` | Bearing fruit |

### Example: Oak Tree at Stage 2

**File:** `Prefabs/Trees/Oak/Oak_Stage_2_01.prefab.json.lpf`

This prefab contains:
- Trunk blocks with appropriate rotations
- Branch blocks extending outward
- Leaf blocks forming the canopy
- Anchor at base of trunk for proper ground placement

---

## Rock Formations

**Location:** `Prefabs/Rock_Formations/`
**Count:** ~676 files

Rock formations provide natural terrain features including standalone rocks, arches, fossils, pillars, and geological features.

### Directory Structure

```
Prefabs/Rock_Formations/
├── Rocks/
│   ├── Stone/
│   │   ├── Rock_Stone_Small_01.prefab.json.lpf
│   │   ├── Rock_Stone_Medium_01.prefab.json.lpf
│   │   └── Rock_Stone_Large_01.prefab.json.lpf
│   ├── Basalt/
│   ├── Sandstone/
│   ├── Quartzite/
│   └── Volcanic/
├── Arches/
├── Fossils/
├── Pillars/
├── Mushrooms/
├── Hotsprings/
└── Crystals/
```

### Subcategories

| Subcategory | Count | Description |
|-------------|-------|-------------|
| `Rocks/` | ~300 | Standard rock formations |
| `Arches/` | ~40 | Natural stone arches |
| `Fossils/` | ~50 | Exposed fossil formations |
| `Pillars/` | ~80 | Tall stone columns |
| `Mushrooms/` | ~60 | Giant mushroom formations |
| `Hotsprings/` | ~30 | Geothermal features |
| `Crystals/` | ~100 | Crystal clusters |

### Material Variants

| Material | Biomes | Appearance |
|----------|--------|------------|
| `Stone` | Universal | Gray, standard rock |
| `Basalt` | Volcanic | Dark, columnar |
| `Sandstone` | Desert | Tan, layered |
| `Quartzite` | Mountains | White, crystalline |
| `Granite` | Mountains | Speckled gray |
| `Limestone` | Caves | Light gray, porous |
| `Obsidian` | Volcanic | Black, glassy |

### Size Variants

| Size | Approximate Dimensions |
|------|------------------------|
| `Small` | 1-3 blocks |
| `Medium` | 4-8 blocks |
| `Large` | 9-20 blocks |
| `Massive` | 20+ blocks |

### Example: Sandstone Arch

**File:** `Prefabs/Rock_Formations/Arches/Arch_Sandstone_01.prefab.json.lpf`

Contains sandstone blocks arranged in an arch formation, with anchor at the base center for proper terrain integration.

---

## NPC Structures

**Location:** `Prefabs/Npc/`
**Count:** ~452 files

NPC structures represent buildings and camps for various factions. Each faction has distinct architectural styles and building types.

### Directory Structure

```
Prefabs/Npc/
├── Kweebec/
│   ├── Houses/
│   │   ├── Kweebec_House_Small_01.prefab.json.lpf
│   │   ├── Kweebec_House_Medium_01.prefab.json.lpf
│   │   └── Kweebec_House_Large_01.prefab.json.lpf
│   ├── Shops/
│   ├── Wells/
│   └── Decorations/
├── Scarak/
├── Trork/
├── Yeti/
├── Feran/
└── Outlander/
```

### Factions

| Faction | Style | Biomes | Notes |
|---------|-------|--------|-------|
| `Kweebec` | Organic, wood | Forest | Friendly forest dwellers |
| `Scarak` | Hive, organic | Underground | Insectoid structures |
| `Trork` | Crude, bone | Swamp, Forest | Tribal camps |
| `Yeti` | Ice, stone | Winter | Mountain dwellings |
| `Feran` | Refined, stone | Varied | Wolf-like humanoids |
| `Outlander` | Human, varied | Varied | Human settlements |

### Building Types

| Type | Description |
|------|-------------|
| `House` | Residential dwellings |
| `Shop` | Merchant buildings |
| `Guard_Tower` | Defensive structures |
| `Well` | Water source |
| `Storage` | Warehouses, barns |
| `Workshop` | Crafting buildings |
| `Temple` | Religious structures |
| `Inn` | Rest buildings |

### Size Variants

| Size | Interior Space | NPCs |
|------|----------------|------|
| `Small` | 1-2 rooms | 1-2 |
| `Medium` | 3-4 rooms | 2-4 |
| `Large` | 5+ rooms | 4+ |

### Example: Kweebec House

**File:** `Prefabs/Npc/Kweebec/Houses/Kweebec_House_Medium_01.prefab.json.lpf`

Contains:
- Wood and leaf block walls
- Interior furniture blocks
- Container blocks with faction-appropriate loot
- Door blocks with proper rotations
- Entity spawners for NPC inhabitants

---

## Monuments

**Location:** `Prefabs/Monuments/`
**Count:** ~231 files

Monuments are unique or semi-unique structures including towers, temples, encounter areas, and points of interest.

### Directory Structure

```
Prefabs/Monuments/
├── Mage_Towers/
│   ├── Mage_Tower_Fire.prefab.json.lpf
│   ├── Mage_Tower_Ice.prefab.json.lpf
│   └── Mage_Tower_Lightning.prefab.json.lpf
├── Temples/
├── Challenge/
├── Encounter/
├── Incidental/
│   ├── Camps/
│   ├── Farms/
│   ├── Ruins/
│   └── Shipwrecks/
└── Unique/
```

### Categories

| Category | Count | Description |
|----------|-------|-------------|
| `Mage_Towers/` | ~12 | Elemental wizard towers |
| `Temples/` | ~20 | Religious/ancient structures |
| `Challenge/` | ~40 | Combat encounter arenas |
| `Encounter/` | ~60 | Multi-zone progressions |
| `Incidental/` | ~80 | Camps, farms, ruins |
| `Unique/` | ~20 | One-of-a-kind structures |

### Encounter Types

| Type | Description |
|------|-------------|
| `Challenge` | Single-area combat encounters |
| `Encounter_Tier1` | Easy multi-zone progression |
| `Encounter_Tier2` | Medium difficulty |
| `Encounter_Tier3` | Hard difficulty |
| `Boss` | Boss encounter areas |

### Incidental Structures

| Type | Description |
|------|-------------|
| `Camp` | Abandoned or hostile camps |
| `Farm` | Ruined or active farms |
| `Ruins` | Ancient collapsed structures |
| `Shipwreck` | Wrecked ships on coast |
| `Graveyard` | Cemetery areas |
| `Shrine` | Small religious sites |

### Example: Mage Tower

**File:** `Prefabs/Monuments/Mage_Towers/Mage_Tower_Fire.prefab.json.lpf`

Contains:
- Multi-story tower structure
- Elemental-themed block materials
- Interior rooms with furniture
- Loot containers with rare drops
- Potential boss spawn areas

---

## Mineshafts

**Location:** `Prefabs/Mineshaft/`
**Count:** ~117 files

Mineshafts use a modular system where different components connect to form complete mine networks.

### Directory Structure

```
Prefabs/Mineshaft/
├── Shaft/
│   ├── Shaft_Straight_01.prefab.json.lpf
│   ├── Shaft_Corner_01.prefab.json.lpf
│   ├── Shaft_T_Junction_01.prefab.json.lpf
│   └── Shaft_Crossroad_01.prefab.json.lpf
├── Slope/
│   ├── Slope_Down_01.prefab.json.lpf
│   └── Slope_Up_01.prefab.json.lpf
├── Surface/
│   ├── Surface_Entrance_01.prefab.json.lpf
│   └── Surface_Building_01.prefab.json.lpf
└── Features/
    ├── Feature_Cart_01.prefab.json.lpf
    ├── Feature_Ore_Vein_01.prefab.json.lpf
    └── Feature_Support_01.prefab.json.lpf
```

### Component Types

| Component | Description |
|-----------|-------------|
| `Shaft_Straight` | Linear tunnel section |
| `Shaft_Corner` | 90-degree turn |
| `Shaft_T_Junction` | Three-way intersection |
| `Shaft_Crossroad` | Four-way intersection |
| `Shaft_Cap` | Dead end |
| `Slope_Down` | Descending section |
| `Slope_Up` | Ascending section |
| `Surface_Entrance` | Above-ground entrance |

### Connection System

Mineshaft components use standardized connection points:
- Opening at each end of tunnels
- Matching dimensions for seamless connection
- Anchor points at center of floor

### Example: T-Junction

**File:** `Prefabs/Mineshaft/Shaft/Shaft_T_Junction_01.prefab.json.lpf`

Contains:
- Tunnel walls and ceiling
- Support beam blocks
- Rail track blocks
- Torch/light source blocks
- Openings on three sides for connections

---

## Dungeons

**Location:** `Prefabs/Dungeon/`
**Count:** ~84 files

Dungeons use modular room systems similar to mineshafts, creating varied dungeon layouts.

### Directory Structure

```
Prefabs/Dungeon/
├── Goblin_Lair/
│   ├── Room_Straight_01.prefab.json.lpf
│   ├── Room_Corner_01.prefab.json.lpf
│   ├── Room_T_Junction_01.prefab.json.lpf
│   ├── Room_Large_01.prefab.json.lpf
│   └── Room_Boss_01.prefab.json.lpf
├── Cursed_Crypt/
├── Stone/
└── Ancient/
```

### Dungeon Types

| Type | Theme | Enemies |
|------|-------|---------|
| `Goblin_Lair` | Cave, crude construction | Goblins |
| `Cursed_Crypt` | Stone, dark | Undead |
| `Stone` | Ancient stone | Varied |
| `Ancient` | Mysterious, magical | Magical creatures |

### Room Types

| Room | Description |
|------|-------------|
| `Cap` | Dead end room |
| `Corner` | 90-degree turn |
| `Straight` | Corridor section |
| `T_Junction` | Three-way split |
| `Crossroad` | Four-way intersection |
| `Large` | Expanded encounter room |
| `Boss` | Boss encounter chamber |
| `Treasure` | Loot room |
| `Trap` | Hazard room |

### Example: Goblin Lair Boss Room

**File:** `Prefabs/Dungeon/Goblin_Lair/Room_Boss_01.prefab.json.lpf`

Contains:
- Large room with high ceiling
- Goblin-themed decorations
- Multiple loot containers with boss drops
- Entity spawn points for boss and minions
- Entrance and potential exit openings

---

## Caves

**Location:** `Prefabs/Cave/`
**Count:** ~180 files

Cave prefabs include geological formations, creature nests, and resource nodes found underground.

### Directory Structure

```
Prefabs/Cave/
├── Formations/
│   ├── Stalactite_01.prefab.json.lpf
│   ├── Stalagmite_01.prefab.json.lpf
│   └── Column_01.prefab.json.lpf
├── Klops_Nests/
├── Nodes/
│   ├── Node_Iron_01.prefab.json.lpf
│   ├── Node_Copper_01.prefab.json.lpf
│   └── Node_Crystal_01.prefab.json.lpf
└── Features/
    ├── Pool_01.prefab.json.lpf
    └── Mushroom_Cluster_01.prefab.json.lpf
```

### Categories

| Category | Description |
|----------|-------------|
| `Formations/` | Stalactites, stalagmites, columns |
| `Klops_Nests/` | Creature nest structures |
| `Nodes/` | Ore and crystal deposits |
| `Features/` | Pools, mushrooms, unique features |

### Formation Types

| Formation | Description |
|-----------|-------------|
| `Stalactite` | Hanging from ceiling |
| `Stalagmite` | Rising from floor |
| `Column` | Floor to ceiling |
| `Flowstone` | Wall formations |
| `Crystal_Cluster` | Gem formations |

### Resource Nodes

| Node Type | Resources |
|-----------|-----------|
| `Node_Iron` | Iron ore blocks |
| `Node_Copper` | Copper ore blocks |
| `Node_Gold` | Gold ore blocks |
| `Node_Crystal` | Crystal blocks |
| `Node_Gem` | Gem-bearing rock |

---

## Plants

**Location:** `Prefabs/Plants/`
**Count:** ~205 files

Plant prefabs include bushes, cacti, coral, and other vegetation beyond trees.

### Directory Structure

```
Prefabs/Plants/
├── Bush/
│   ├── Bush_Berry_01.prefab.json.lpf
│   ├── Bush_Flower_01.prefab.json.lpf
│   └── Bush_Thorns_01.prefab.json.lpf
├── Cacti/
│   ├── Cactus_Small_01.prefab.json.lpf
│   ├── Cactus_Large_01.prefab.json.lpf
│   └── Cactus_Flowering_01.prefab.json.lpf
├── Seaweed/
├── Driftwood/
├── Coral/
└── Twisted_Wood/
```

### Categories

| Category | Biomes | Description |
|----------|--------|-------------|
| `Bush/` | Varied | Berry, flower, decorative bushes |
| `Cacti/` | Desert | Desert plants |
| `Seaweed/` | Underwater | Aquatic vegetation |
| `Driftwood/` | Coastal | Washed-up wood |
| `Coral/` | Underwater | Coral formations |
| `Twisted_Wood/` | Corrupted | Dark, twisted plants |

### Bush Variants

| Variant | Description |
|---------|-------------|
| `Berry` | Harvestable berries |
| `Flower` | Decorative flowers |
| `Thorns` | Damaging thorns |
| `Fern` | Fern plants |
| `Tall_Grass` | Grass clumps |

### Coral Types

| Type | Appearance |
|------|------------|
| `Brain` | Rounded, brain-like |
| `Branch` | Branching structure |
| `Fan` | Flat, fan-shaped |
| `Tube` | Tubular formations |

---

## Spawn

**Location:** `Prefabs/Spawn/`
**Count:** ~18 files

Spawn prefabs define player spawn point layouts and initial pathway structures.

### Directory Structure

```
Prefabs/Spawn/
├── Spawn_Point_01.prefab.json.lpf
├── Spawn_Point_02.prefab.json.lpf
├── Spawn_Area_Small.prefab.json.lpf
├── Spawn_Area_Large.prefab.json.lpf
└── Spawn_Path_01.prefab.json.lpf
```

### Types

| Type | Description |
|------|-------------|
| `Spawn_Point` | Single spawn location |
| `Spawn_Area` | Spawn zone with safe area |
| `Spawn_Path` | Initial player pathway |
| `Spawn_Shelter` | Starting shelter structure |

### Example: Spawn Area

**File:** `Prefabs/Spawn/Spawn_Area_Small.prefab.json.lpf`

Contains:
- Flattened terrain blocks
- Torch/light placement
- Basic shelter blocks
- Chest with starter items
- Clear path markers

---

## PrefabList Integration

World generation references prefabs through PrefabList files that group related prefabs by category and biome.

### Example: Forest Trees PrefabList

**File:** `Server/PrefabList/Trees_Forest.json`

```json
{
  "Prefabs": [
    {
      "RootDirectory": "Asset",
      "Path": "Trees/Oak/",
      "Recursive": true
    },
    {
      "RootDirectory": "Asset",
      "Path": "Trees/Birch/",
      "Recursive": true
    },
    {
      "RootDirectory": "Asset",
      "Path": "Trees/Maple/",
      "Recursive": true
    }
  ]
}
```

### Example: Desert Rocks PrefabList

**File:** `Server/PrefabList/Rocks_Desert.json`

```json
{
  "Prefabs": [
    {
      "RootDirectory": "Asset",
      "Path": "Rock_Formations/Rocks/Sandstone/",
      "Recursive": true
    },
    {
      "RootDirectory": "Asset",
      "Path": "Rock_Formations/Arches/Sandstone/",
      "Recursive": true
    }
  ]
}
```

---

## Related Documentation

- [Prefabs API](prefabs.md) - Java API and file format
- [Drop System](drops.md) - Loot tables for containers
- [Block System](blocks.md) - Block types and properties
- [NPC Roles](npc-roles.md) - NPC configuration
