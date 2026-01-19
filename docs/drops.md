# Drop System

Drop files define loot tables for blocks, NPCs, containers, and world prefabs. They use a hierarchical container system that supports guaranteed drops, weighted random selection, and modular composition through references.

> **See also:** [Item Definitions](items.md) for item IDs and properties, [NPC Roles](npc-roles.md) for NPC drop configuration, [Block Items](items-blocks.md) for container block drops

---

## Quick Navigation

| Category | Directory | Files | Description |
|----------|-----------|-------|-------------|
| [Crops](#crop-drops) | `Crop/` | 231 | Growth stage and harvest drops |
| [NPCs](#npc-drops) | `NPCs/` | 270 | Creature loot tables |
| [Mining](#mining-drops) | `Rock/` | 63 | Ore and crystal drops |
| [Plants](#plant-drops) | `Plant/` | 21 | Wild plant harvesting |
| [Wood](#wood-drops) | `Wood/` | 6 | Tree and branch drops |
| [Containers](#container-drops) | `Items/` | 12 | Destructible container drops |
| [Prefabs](#prefab-drops) | `Prefabs/` | 49 | Chest loot by zone/tier |
| [Objectives](#objective-drops) | `Objectives/` | 4 | Quest rewards |
| [Traps](#trap-drops) | `Traps/` | 2 | Fishing trap catches |

**Location:** `Assets.zip > Server/Drops/` (669 total files)

---

## Quick Start

### Simple Guaranteed Drop

A single item that always drops:

```json
{
  "Type": "Single",
  "Item": {
    "ItemId": "Plant_Crop_Carrot",
    "QuantityMin": 1,
    "QuantityMax": 1
  }
}
```

### Random Quantity Drop

A single item with variable quantity:

```json
{
  "Type": "Single",
  "Item": {
    "ItemId": "Ingredient_Wool",
    "QuantityMin": 1,
    "QuantityMax": 3
  }
}
```

### Weighted Random Selection

One item chosen randomly from a pool:

```json
{
  "Type": "Choice",
  "Containers": [
    {
      "Type": "Single",
      "Weight": 70,
      "Item": { "ItemId": "Fish_Common_Carp", "QuantityMin": 1, "QuantityMax": 1 }
    },
    {
      "Type": "Single",
      "Weight": 25,
      "Item": { "ItemId": "Fish_Uncommon_Bass", "QuantityMin": 1, "QuantityMax": 1 }
    },
    {
      "Type": "Single",
      "Weight": 5,
      "Item": { "ItemId": "Fish_Rare_Goldfish", "QuantityMin": 1, "QuantityMax": 1 }
    }
  ]
}
```

### Multiple Guaranteed Drops

All items drop together:

```json
{
  "Type": "Multiple",
  "Containers": [
    {
      "Type": "Single",
      "Item": { "ItemId": "Ingredient_Leather_Light", "QuantityMin": 1, "QuantityMax": 2 }
    },
    {
      "Type": "Single",
      "Item": { "ItemId": "Ingredient_Meat_Raw", "QuantityMin": 1, "QuantityMax": 1 }
    }
  ]
}
```

### Empty Drop File

Use for drops that should yield nothing:

```json
{
  "Type": "Empty"
}
```

---

## Reference

### Container Types

| Type | Behavior | Use Case |
|------|----------|----------|
| `Multiple` | All nested containers evaluated | Guaranteed multi-item drops |
| `Choice` | One container selected by weight | Random loot tables |
| `Single` | One item stack | Leaf node for actual items |
| `Empty` | No drop | Chance for nothing in Choice |
| `Droplist` | Reference another drop file | Modular composition |

### Container Properties

| Property | Type | Description |
|----------|------|-------------|
| `Type` | string | Container type: `Multiple`, `Choice`, `Single`, `Empty`, `Droplist` |
| `Containers` | array | Nested containers (for Multiple/Choice) |
| `Item` | object | Item definition (for Single) |
| `Weight` | int | Selection weight (for Choice children) |
| `RollsMin` | int | Minimum number of selections (for Choice) |
| `RollsMax` | int | Maximum number of selections (for Choice) |
| `DroplistId` | string | Referenced drop file path (for Droplist) |
| `$Comment` | string | Documentation comment (ignored by game) |

### Item Properties

| Property | Type | Description |
|----------|------|-------------|
| `ItemId` | string | Item identifier from item definitions |
| `QuantityMin` | int | Minimum quantity to drop |
| `QuantityMax` | int | Maximum quantity to drop |

---

## Container Type Details

### Multiple Container

Evaluates all nested containers. Every child container produces drops.

```json
{
  "Type": "Multiple",
  "Containers": [
    {
      "Type": "Single",
      "Item": { "ItemId": "Ingredient_Bone", "QuantityMin": 1, "QuantityMax": 3 }
    },
    {
      "Type": "Single",
      "Item": { "ItemId": "Ingredient_Meat_Raw", "QuantityMin": 1, "QuantityMax": 2 }
    }
  ]
}
```

**Weight as Percentage Chance:**

Within a Multiple container, child `Weight` values function as percentage chances (0-100):

```json
{
  "Type": "Multiple",
  "Containers": [
    {
      "Type": "Single",
      "Weight": 100,
      "$Comment": "Always drops",
      "Item": { "ItemId": "Ingredient_Leather_Light", "QuantityMin": 1, "QuantityMax": 2 }
    },
    {
      "Type": "Single",
      "Weight": 25,
      "$Comment": "25% chance to drop",
      "Item": { "ItemId": "Ingredient_Horn", "QuantityMin": 1, "QuantityMax": 1 }
    }
  ]
}
```

### Choice Container

Selects one nested container based on weight. Higher weight = higher chance.

```json
{
  "Type": "Choice",
  "Containers": [
    {
      "Type": "Single",
      "Weight": 60,
      "Item": { "ItemId": "Ingredient_Feather_Common", "QuantityMin": 1, "QuantityMax": 2 }
    },
    {
      "Type": "Single",
      "Weight": 30,
      "Item": { "ItemId": "Ingredient_Feather_Uncommon", "QuantityMin": 1, "QuantityMax": 1 }
    },
    {
      "Type": "Single",
      "Weight": 10,
      "Item": { "ItemId": "Ingredient_Feather_Rare", "QuantityMin": 1, "QuantityMax": 1 }
    }
  ]
}
```

**Weight Calculation:**

Total weight = sum of all child weights. Probability = child weight / total weight.

In the example above:
- Common: 60/100 = 60%
- Uncommon: 30/100 = 30%
- Rare: 10/100 = 10%

**Multiple Rolls with RollsMin/RollsMax:**

```json
{
  "Type": "Choice",
  "RollsMin": 2,
  "RollsMax": 4,
  "Containers": [
    {
      "Type": "Single",
      "Weight": 50,
      "Item": { "ItemId": "Ingredient_Gem_Rough_Amethyst", "QuantityMin": 1, "QuantityMax": 1 }
    },
    {
      "Type": "Single",
      "Weight": 30,
      "Item": { "ItemId": "Ingredient_Gem_Rough_Ruby", "QuantityMin": 1, "QuantityMax": 1 }
    },
    {
      "Type": "Single",
      "Weight": 20,
      "Item": { "ItemId": "Ingredient_Gem_Rough_Sapphire", "QuantityMin": 1, "QuantityMax": 1 }
    }
  ]
}
```

This selects 2-4 items from the pool (with replacement).

### Single Container

Leaf node that defines an actual item drop.

```json
{
  "Type": "Single",
  "Item": {
    "ItemId": "Plant_Crop_Corn",
    "QuantityMin": 1,
    "QuantityMax": 3
  }
}
```

**Quantity Range:**

When `QuantityMin` differs from `QuantityMax`, a random value is chosen uniformly within the range.

### Empty Container

Produces no drops. Used within Choice containers to add a "nothing drops" outcome.

```json
{
  "Type": "Choice",
  "Containers": [
    {
      "Type": "Single",
      "Weight": 80,
      "Item": { "ItemId": "Ingredient_Seed_Wheat", "QuantityMin": 1, "QuantityMax": 2 }
    },
    {
      "Type": "Empty",
      "Weight": 20,
      "$Comment": "20% chance for no seed drop"
    }
  ]
}
```

### Droplist Container

References another drop file for modular composition.

```json
{
  "Type": "Droplist",
  "DroplistId": "NPCs/Creature/Shared_Meat_Drop"
}
```

**Use Cases:**
- Share common drops across multiple NPCs
- Create tiered loot table hierarchies
- Separate base drops from bonus drops

**Example: NPC using shared droplist:**

```json
{
  "Type": "Multiple",
  "Containers": [
    {
      "Type": "Droplist",
      "DroplistId": "NPCs/Creature/Livestock/Shared_Livestock_Leather"
    },
    {
      "Type": "Single",
      "Item": { "ItemId": "Ingredient_Meat_Raw_Beef", "QuantityMin": 2, "QuantityMax": 4 }
    }
  ]
}
```

---

## Drop Categories

### Crop Drops

**Location:** `Server/Drops/Crop/`

Defines what crops yield when harvested at different growth stages.

**Naming Convention:** `{CropName}.json`, `{CropName}_{BiomeVariant}.json`

**Example: Carrot Crop**

```json
{
  "$Comment": "Carrot harvest drop",
  "Type": "Multiple",
  "Containers": [
    {
      "Type": "Single",
      "Item": { "ItemId": "Plant_Crop_Carrot", "QuantityMin": 1, "QuantityMax": 3 }
    },
    {
      "Type": "Single",
      "Weight": 50,
      "Item": { "ItemId": "Plant_Crop_Carrot_Seed", "QuantityMin": 0, "QuantityMax": 2 }
    }
  ]
}
```

**Biome Variants:**
- `Carrot_Eternal.json` - Eternal biome variant
- `Carrot_Wet.json` - Swamp/wetland variant
- `Carrot_Winter.json` - Winter biome variant

### NPC Drops

**Location:** `Server/Drops/NPCs/`

Organized by creature category:

```
Server/Drops/NPCs/
├── Creature/
│   ├── Aquatic/
│   ├── Avian/
│   ├── Critter/
│   ├── Livestock/
│   ├── Mammal/
│   └── Vermin/
├── Intelligent/
│   ├── Feran/
│   ├── Goblin/
│   ├── Kweebec/
│   ├── Outlander/
│   ├── Scarak/
│   └── Trork/
└── Undead/
```

**Example: Cow Drop**

```json
{
  "Type": "Multiple",
  "Containers": [
    {
      "Type": "Single",
      "Item": { "ItemId": "Ingredient_Leather_Light", "QuantityMin": 1, "QuantityMax": 3 }
    },
    {
      "Type": "Single",
      "Item": { "ItemId": "Ingredient_Meat_Raw_Beef", "QuantityMin": 2, "QuantityMax": 4 }
    }
  ]
}
```

**Example: Goblin Scrapper Drop**

```json
{
  "Type": "Multiple",
  "Containers": [
    {
      "Type": "Single",
      "Weight": 100,
      "Item": { "ItemId": "Ingredient_Salvage_Goblin", "QuantityMin": 1, "QuantityMax": 3 }
    },
    {
      "Type": "Choice",
      "Weight": 30,
      "Containers": [
        {
          "Type": "Single",
          "Weight": 70,
          "Item": { "ItemId": "Weapon_Club_Crude", "QuantityMin": 1, "QuantityMax": 1 }
        },
        {
          "Type": "Empty",
          "Weight": 30
        }
      ]
    }
  ]
}
```

### Mining Drops

**Location:** `Server/Drops/Rock/`

Defines drops for ore blocks and crystals when mined.

**Naming Convention:** `Rock_{Material}.json`, `Crystal_{Type}.json`

**Example: Iron Ore Drop**

```json
{
  "Type": "Single",
  "Item": {
    "ItemId": "Ingredient_Ore_Iron",
    "QuantityMin": 1,
    "QuantityMax": 2
  }
}
```

**Example: Gem Crystal with Rarity**

```json
{
  "Type": "Choice",
  "Containers": [
    {
      "Type": "Single",
      "Weight": 60,
      "Item": { "ItemId": "Ingredient_Gem_Rough_Amethyst", "QuantityMin": 1, "QuantityMax": 2 }
    },
    {
      "Type": "Single",
      "Weight": 30,
      "Item": { "ItemId": "Ingredient_Gem_Rough_Ruby", "QuantityMin": 1, "QuantityMax": 1 }
    },
    {
      "Type": "Single",
      "Weight": 10,
      "Item": { "ItemId": "Ingredient_Gem_Rough_Diamond", "QuantityMin": 1, "QuantityMax": 1 }
    }
  ]
}
```

### Plant Drops

**Location:** `Server/Drops/Plant/`

Wild plants and foraged items.

**Example: Wild Berry Bush**

```json
{
  "Type": "Single",
  "Item": {
    "ItemId": "Plant_Berry_Wild",
    "QuantityMin": 2,
    "QuantityMax": 5
  }
}
```

### Wood Drops

**Location:** `Server/Drops/Wood/`

Tree and branch drops when cut.

**Example: Oak Tree Drop**

```json
{
  "Type": "Multiple",
  "Containers": [
    {
      "Type": "Single",
      "Item": { "ItemId": "Wood_Log_Oak", "QuantityMin": 3, "QuantityMax": 6 }
    },
    {
      "Type": "Single",
      "Weight": 40,
      "Item": { "ItemId": "Plant_Acorn", "QuantityMin": 1, "QuantityMax": 2 }
    }
  ]
}
```

### Container Drops

**Location:** `Server/Drops/Items/`

Defines what destructible containers (barrels, crates, pots) drop when broken.

**Example: Wooden Crate Drop**

```json
{
  "Type": "Choice",
  "RollsMin": 1,
  "RollsMax": 3,
  "Containers": [
    {
      "Type": "Single",
      "Weight": 40,
      "Item": { "ItemId": "Ingredient_Cloth", "QuantityMin": 1, "QuantityMax": 2 }
    },
    {
      "Type": "Single",
      "Weight": 30,
      "Item": { "ItemId": "Ingredient_Rope", "QuantityMin": 1, "QuantityMax": 1 }
    },
    {
      "Type": "Single",
      "Weight": 20,
      "Item": { "ItemId": "Ingredient_Bar_Iron", "QuantityMin": 1, "QuantityMax": 1 }
    },
    {
      "Type": "Empty",
      "Weight": 10
    }
  ]
}
```

### Prefab Drops

**Location:** `Server/Drops/Prefabs/`

Loot tables for pre-placed chests in dungeons, camps, and structures.

**Organization by Zone and Tier:**

```
Server/Drops/Prefabs/
├── Zone1/
│   ├── Chest_Common.json
│   ├── Chest_Uncommon.json
│   └── Chest_Rare.json
├── Zone2/
├── Zone3/
└── Zone4/
```

**Example: Zone 1 Common Chest**

```json
{
  "Type": "Choice",
  "RollsMin": 3,
  "RollsMax": 5,
  "Containers": [
    {
      "Type": "Single",
      "Weight": 30,
      "Item": { "ItemId": "Food_Bread", "QuantityMin": 1, "QuantityMax": 3 }
    },
    {
      "Type": "Single",
      "Weight": 25,
      "Item": { "ItemId": "Ingredient_Bar_Copper", "QuantityMin": 1, "QuantityMax": 2 }
    },
    {
      "Type": "Single",
      "Weight": 20,
      "Item": { "ItemId": "Potion_Health_Minor", "QuantityMin": 1, "QuantityMax": 1 }
    },
    {
      "Type": "Single",
      "Weight": 15,
      "Item": { "ItemId": "Ingredient_Gem_Rough_Amethyst", "QuantityMin": 1, "QuantityMax": 1 }
    },
    {
      "Type": "Single",
      "Weight": 10,
      "Item": { "ItemId": "Weapon_Sword_Copper", "QuantityMin": 1, "QuantityMax": 1 }
    }
  ]
}
```

### Trap Drops

**Location:** `Server/Drops/Traps/`

Fishing traps and other trap-based loot.

**Example: Fishing Trap**

```json
{
  "Type": "Choice",
  "Containers": [
    {
      "Type": "Single",
      "Weight": 50,
      "Item": { "ItemId": "Fish_Common_Carp", "QuantityMin": 1, "QuantityMax": 2 }
    },
    {
      "Type": "Single",
      "Weight": 25,
      "Item": { "ItemId": "Fish_Common_Trout", "QuantityMin": 1, "QuantityMax": 1 }
    },
    {
      "Type": "Single",
      "Weight": 15,
      "Item": { "ItemId": "Fish_Uncommon_Bass", "QuantityMin": 1, "QuantityMax": 1 }
    },
    {
      "Type": "Single",
      "Weight": 8,
      "Item": { "ItemId": "Fish_Rare_Goldfish", "QuantityMin": 1, "QuantityMax": 1 }
    },
    {
      "Type": "Single",
      "Weight": 2,
      "Item": { "ItemId": "Fish_Legendary_Koi", "QuantityMin": 1, "QuantityMax": 1 }
    }
  ]
}
```

### Objective Drops

**Location:** `Server/Drops/Objectives/`

Quest completion rewards.

---

## File Organization

### Directory Structure

```
Server/Drops/
├── Crop/
│   ├── Carrot.json
│   ├── Carrot_Eternal.json
│   ├── Corn.json
│   └── ...
├── Items/
│   ├── Barrel.json
│   ├── Crate_Wood.json
│   └── ...
├── NPCs/
│   ├── Creature/
│   │   ├── Aquatic/
│   │   ├── Avian/
│   │   ├── Critter/
│   │   ├── Livestock/
│   │   │   ├── Cow.json
│   │   │   ├── Sheep.json
│   │   │   └── ...
│   │   ├── Mammal/
│   │   └── Vermin/
│   ├── Intelligent/
│   │   ├── Goblin/
│   │   ├── Trork/
│   │   └── ...
│   └── Undead/
├── Objectives/
├── Plant/
├── Prefabs/
│   ├── Zone1/
│   ├── Zone2/
│   ├── Zone3/
│   └── Zone4/
├── Rock/
├── Traps/
└── Wood/
```

### Naming Conventions

| Pattern | Description | Example |
|---------|-------------|---------|
| `{Entity}.json` | Base drop file | `Cow.json` |
| `{Entity}_{Variant}.json` | Biome/type variant | `Cow_Winter.json` |
| `Shared_{Type}.json` | Reusable shared drops | `Shared_Livestock_Leather.json` |
| `Chest_{Rarity}.json` | Prefab chest tiers | `Chest_Rare.json` |

### Biome Variant Suffixes

| Suffix | Description |
|--------|-------------|
| `_Eternal` | Eternal/magical biome |
| `_Wet` | Swamp/wetland biome |
| `_Winter` | Cold/snow biome |
| `_Desert` | Arid/desert biome |
| `_Cave` | Underground variant |

---

## Integration Points

### Block Drops

Blocks reference drop files in their `BlockType.Components.container` configuration:

```json
{
  "BlockType": {
    "Components": {
      "container": {
        "Droplist": "Items/Crate_Wood"
      }
    }
  }
}
```

See [Block Items - Container Configuration](items-blocks.md#container-configuration) for details.

### NPC Drops

NPC roles reference drop files via the `DropList` property:

```json
{
  "Reference": "Creature/Livestock/_Core/Template_Livestock",
  "Modify": {
    "DropList": "NPCs/Creature/Livestock/Cow"
  }
}
```

See [NPC Roles](npc-roles.md#key-properties) for details.

### Farming Crops

Crop blocks define harvest drops in their `BlockType.Farming.HarvestDrops` property:

```json
{
  "BlockType": {
    "Farming": {
      "HarvestDrops": [
        { "ItemId": "Plant_Crop_Carrot", "Quantity": [1, 3] },
        { "ItemId": "Plant_Crop_Carrot_Seed", "Quantity": [0, 2], "Chance": 0.5 }
      ]
    }
  }
}
```

Note: `HarvestDrops` uses a simplified inline format rather than referencing drop files.

See [Block Items - Farming Configuration](items-blocks.md#farming-configuration) for details.

### Prefab Containers

Prefab-placed chests reference drop files for their loot tables:

```json
{
  "Type": "Prefab",
  "Containers": [
    {
      "BlockId": "Furniture_Ancient_Chest_Small",
      "DroplistId": "Prefabs/Zone1/Chest_Common"
    }
  ]
}
```

See [Prefabs](prefabs.md) for details.

---

## Complete Examples

### Tiered Rarity Loot Table

A fishing trap with common to legendary fish:

```json
{
  "$Comment": "Fishing Trap - Freshwater",
  "Type": "Choice",
  "Containers": [
    {
      "Type": "Single",
      "Weight": 45,
      "$Comment": "Common (45%)",
      "Item": { "ItemId": "Fish_Common_Carp", "QuantityMin": 1, "QuantityMax": 2 }
    },
    {
      "Type": "Single",
      "Weight": 25,
      "$Comment": "Common (25%)",
      "Item": { "ItemId": "Fish_Common_Trout", "QuantityMin": 1, "QuantityMax": 1 }
    },
    {
      "Type": "Single",
      "Weight": 15,
      "$Comment": "Uncommon (15%)",
      "Item": { "ItemId": "Fish_Uncommon_Bass", "QuantityMin": 1, "QuantityMax": 1 }
    },
    {
      "Type": "Single",
      "Weight": 10,
      "$Comment": "Rare (10%)",
      "Item": { "ItemId": "Fish_Rare_Goldfish", "QuantityMin": 1, "QuantityMax": 1 }
    },
    {
      "Type": "Single",
      "Weight": 4,
      "$Comment": "Epic (4%)",
      "Item": { "ItemId": "Fish_Epic_Sturgeon", "QuantityMin": 1, "QuantityMax": 1 }
    },
    {
      "Type": "Single",
      "Weight": 1,
      "$Comment": "Legendary (1%)",
      "Item": { "ItemId": "Fish_Legendary_Koi", "QuantityMin": 1, "QuantityMax": 1 }
    }
  ]
}
```

### Multi-Roll Encounter Chest

A dungeon chest with multiple random items:

```json
{
  "$Comment": "Zone 2 Dungeon Boss Chest",
  "Type": "Multiple",
  "Containers": [
    {
      "Type": "Choice",
      "RollsMin": 4,
      "RollsMax": 6,
      "$Comment": "Random loot rolls",
      "Containers": [
        {
          "Type": "Single",
          "Weight": 25,
          "Item": { "ItemId": "Ingredient_Bar_Iron", "QuantityMin": 2, "QuantityMax": 4 }
        },
        {
          "Type": "Single",
          "Weight": 20,
          "Item": { "ItemId": "Potion_Health_Medium", "QuantityMin": 1, "QuantityMax": 2 }
        },
        {
          "Type": "Single",
          "Weight": 20,
          "Item": { "ItemId": "Food_Cooked_Steak", "QuantityMin": 2, "QuantityMax": 4 }
        },
        {
          "Type": "Single",
          "Weight": 15,
          "Item": { "ItemId": "Ingredient_Gem_Rough_Ruby", "QuantityMin": 1, "QuantityMax": 2 }
        },
        {
          "Type": "Single",
          "Weight": 10,
          "Item": { "ItemId": "Ingredient_Gem_Cut_Amethyst", "QuantityMin": 1, "QuantityMax": 1 }
        },
        {
          "Type": "Single",
          "Weight": 10,
          "Item": { "ItemId": "Recipe_Weapon_Sword_Iron", "QuantityMin": 1, "QuantityMax": 1 }
        }
      ]
    },
    {
      "Type": "Choice",
      "$Comment": "Guaranteed equipment piece",
      "Containers": [
        {
          "Type": "Single",
          "Weight": 40,
          "Item": { "ItemId": "Weapon_Sword_Iron", "QuantityMin": 1, "QuantityMax": 1 }
        },
        {
          "Type": "Single",
          "Weight": 30,
          "Item": { "ItemId": "Armor_Chest_Iron", "QuantityMin": 1, "QuantityMax": 1 }
        },
        {
          "Type": "Single",
          "Weight": 20,
          "Item": { "ItemId": "Tool_Pickaxe_Iron", "QuantityMin": 1, "QuantityMax": 1 }
        },
        {
          "Type": "Single",
          "Weight": 10,
          "Item": { "ItemId": "Weapon_Bow_Iron", "QuantityMin": 1, "QuantityMax": 1 }
        }
      ]
    }
  ]
}
```

### Creature with Guaranteed and Chance Drops

A predator with guaranteed meat and chance for rare drops:

```json
{
  "$Comment": "Wolf Drop Table",
  "Type": "Multiple",
  "Containers": [
    {
      "Type": "Single",
      "Weight": 100,
      "$Comment": "Always drops meat",
      "Item": { "ItemId": "Ingredient_Meat_Raw_Wolf", "QuantityMin": 1, "QuantityMax": 2 }
    },
    {
      "Type": "Single",
      "Weight": 100,
      "$Comment": "Always drops fur",
      "Item": { "ItemId": "Ingredient_Fur_Wolf", "QuantityMin": 1, "QuantityMax": 2 }
    },
    {
      "Type": "Single",
      "Weight": 75,
      "$Comment": "75% chance for bone",
      "Item": { "ItemId": "Ingredient_Bone", "QuantityMin": 1, "QuantityMax": 2 }
    },
    {
      "Type": "Single",
      "Weight": 25,
      "$Comment": "25% chance for fang",
      "Item": { "ItemId": "Ingredient_Fang_Wolf", "QuantityMin": 1, "QuantityMax": 1 }
    },
    {
      "Type": "Choice",
      "Weight": 5,
      "$Comment": "5% chance for rare drop",
      "Containers": [
        {
          "Type": "Single",
          "Weight": 80,
          "Item": { "ItemId": "Trophy_Wolf_Pelt", "QuantityMin": 1, "QuantityMax": 1 }
        },
        {
          "Type": "Single",
          "Weight": 20,
          "Item": { "ItemId": "Trophy_Wolf_Alpha_Pelt", "QuantityMin": 1, "QuantityMax": 1 }
        }
      ]
    }
  ]
}
```

### Modular Drop with Shared References

Using Droplist for shared components:

**Shared_Livestock_Meat.json:**
```json
{
  "Type": "Single",
  "Item": { "ItemId": "Ingredient_Meat_Raw", "QuantityMin": 1, "QuantityMax": 3 }
}
```

**Shared_Livestock_Leather.json:**
```json
{
  "Type": "Single",
  "Weight": 80,
  "Item": { "ItemId": "Ingredient_Leather_Light", "QuantityMin": 1, "QuantityMax": 2 }
}
```

**Cow.json (using shared drops):**
```json
{
  "Type": "Multiple",
  "Containers": [
    {
      "Type": "Droplist",
      "DroplistId": "NPCs/Creature/Livestock/Shared_Livestock_Meat"
    },
    {
      "Type": "Droplist",
      "DroplistId": "NPCs/Creature/Livestock/Shared_Livestock_Leather"
    },
    {
      "Type": "Single",
      "$Comment": "Cow-specific drop",
      "Item": { "ItemId": "Ingredient_Meat_Raw_Beef", "QuantityMin": 1, "QuantityMax": 2 }
    }
  ]
}
```

---

## Related Documentation

- [Item Definitions](items.md) - Item IDs, properties, and inheritance
- [NPC Roles](npc-roles.md) - NPC configuration and DropList property
- [Block Items](items-blocks.md) - Container blocks and Droplist property
- [Prefabs](prefabs.md) - Prefab chest loot configuration
- [Inventory API](inventory.md) - Programmatic inventory and item management
