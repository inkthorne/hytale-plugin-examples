# Tool Items

> Part of the [Items API](items.md). For common item properties, see [Items Reference](items.md#common-properties).

## Quick Navigation

| Tool Type | Children | Primary Use | Description |
|-----------|----------|-------------|-------------|
| [Pickaxe](#pickaxe) | 10 | Rocks/Ores | Mining stone and ore blocks |
| [Hatchet](#hatchet) | 10 | Woods | Chopping wood and trees |
| [Shovel](#shovel) | 5 | Soils | Digging soil and dirt |
| [Hoe](#hoe) | 3 | Tilling | Converting soil for farming |
| [Hammer](#hammer) | 2 | Block Cycling | Rotating block variants |
| [Shears](#shears) | 1 | Shearing | Collecting wool from animals |
| [Watering Can](#watering-can) | 1 | Watering | Irrigating crops |
| [Sickle](#sickle) | 2 | Harvesting | Cutting plants and crops |
| [Repair Kit](#repair-kit) | 3 | Repair | Restoring item durability |
| [Capture Crate](#capture-crate) | 1 | Capture | Capturing animals |
| [Feedbag](#feedbag) | 1 | Feeding | Feeding animals |
| [Fertilizer](#fertilizer) | 1 | Growing | Accelerating plant growth |

---

## Tool Property

Unlike weapons which use formal Templates with signature abilities, tools inherit from a "Crude" base item (e.g., `Tool_Pickaxe_Crude`) and use the `Tool` property to configure block-breaking behavior.

### Tool.Specs

Defines power and efficiency for different block types:

```json
{
  "Tool": {
    "Specs": [
      { "Power": 3.0, "GatherType": "Rocks" },
      { "Power": 1.5, "GatherType": "VolcanicRocks" },
      { "Power": 1.0, "GatherType": "SoftBlocks" },
      { "Power": 1.0, "GatherType": "Benches", "HitSoundLayer": "Bench_Wood" }
    ]
  }
}
```

| Property | Type | Description |
|----------|------|-------------|
| `Power` | float | Breaking speed multiplier (higher = faster) |
| `GatherType` | string | Block category this spec applies to |
| `HitSoundLayer` | string | Optional sound override for this gather type |

### GatherTypes

| GatherType | Primary Tool | Description |
|------------|--------------|-------------|
| `SoftBlocks` | All tools (1.0) | Soft blocks like grass, leaves |
| `Soils` | Shovel | Dirt, sand, gravel |
| `Woods` | Hatchet | Wood blocks, tree trunks |
| `Rocks` | Pickaxe | Stone, rock formations |
| `VolcanicRocks` | Pickaxe (low power) | Volcanic stone, obsidian |
| `Ores` | Pickaxe | Metal ore deposits |
| `Benches` | Most tools | Crafting stations, furniture |

### Tool.DurabilityLossBlockTypes

Configures durability loss per block type:

```json
{
  "Tool": {
    "DurabilityLossBlockTypes": [
      { "BlockSetIds": ["Stone", "Rock", "Brick"], "DurabilityLoss": 1.0 },
      { "BlockSetIds": ["Ores"], "DurabilityLoss": 1.5 },
      { "BlockSetIds": ["SandStone", "Limestone"], "DurabilityLoss": 0.5 }
    ]
  }
}
```

| Property | Type | Description |
|----------|------|-------------|
| `BlockSetIds` | array | Block sets this rule applies to |
| `DurabilityLoss` | float | Durability points lost per block |

### Tool.Speed

Optional speed multiplier for tool animations:

```json
{
  "Tool": {
    "Speed": 1.2
  }
}
```

---

## Material Tiers

Tools follow a consistent material progression:

| Tier | Quality | ItemLevel | Durability | Power Range |
|------|---------|-----------|------------|-------------|
| Crude/Wood | Common | 3-10 | 100-150 | 1.0-1.5 |
| Copper | Common | 10 | ~150 | 2.0-2.5 |
| Iron | Uncommon | 20 | 250 | 3.0-3.5 |
| Cobalt | Rare | 30 | 300 | 4.0-4.5 |
| Mithril | Rare | 35 | 325 | 4.5-5.0 |
| Adamantite | Rare | 40 | 400 | 5.0-6.0 |

---

## Pickaxe

**Location:** `Server/Item/Items/Tool/Pickaxe/`

Mining tool optimized for rocks, stone, and ore extraction.

### Base Properties (Tool_Pickaxe_Crude)

| Property | Value |
|----------|-------|
| `Quality` | Common |
| `ItemLevel` | 3 |
| `PlayerAnimationsId` | Pickaxe |
| `MaxDurability` | 100 |
| `Categories` | Items.Tools |

### Tool.Specs

| GatherType | Power | Description |
|------------|-------|-------------|
| `Rocks` | 1.5 | Primary use - stone blocks |
| `VolcanicRocks` | 0.5 | Volcanic/obsidian (reduced) |
| `SoftBlocks` | 1.0 | General soft blocks |
| `Benches` | 1.0 | Crafting stations |

### Interactions

| Slot | Root Interaction | Description |
|------|------------------|-------------|
| `Primary` | Root_Tool_Pickaxe_Primary | Block breaking swing |

Uses `BreakBlock` interaction with `Tool: "Pickaxe"` specification.

### Tags

```json
{
  "Tags": {
    "Type": ["Tool"],
    "Family": ["Pickaxe"]
  }
}
```

### Example Child: Iron Pickaxe

```json
{
  "Parent": "Tool_Pickaxe_Crude",
  "TranslationProperties": {
    "Name": "server.items.Tool_Pickaxe_Iron.name"
  },
  "Model": "Items/Tools/Pickaxe/Iron.blockymodel",
  "Texture": "Items/Tools/Pickaxe/Iron_Texture.png",
  "Icon": "Icons/ItemsGenerated/Tool_Pickaxe_Iron.png",
  "Quality": "Uncommon",
  "ItemLevel": 20,
  "MaxDurability": 250,
  "Tool": {
    "Specs": [
      { "Power": 3.0, "GatherType": "Rocks" },
      { "Power": 1.5, "GatherType": "VolcanicRocks" },
      { "Power": 1.0, "GatherType": "SoftBlocks" },
      { "Power": 1.0, "GatherType": "Benches", "HitSoundLayer": "Bench_Wood" }
    ],
    "DurabilityLossBlockTypes": [
      { "BlockSetIds": ["Stone", "Rock", "Brick"], "DurabilityLoss": 1.0 },
      { "BlockSetIds": ["Ite_Ore_Copper", "Ite_Ore_Coal"], "DurabilityLoss": 1.0 }
    ]
  },
  "Recipe": {
    "TimeSeconds": 2.5,
    "Input": [
      { "ItemId": "Ingredient_Bar_Iron", "Quantity": 4 },
      { "ResourceTypeId": "Wood_Trunk", "Quantity": 2 }
    ],
    "BenchRequirement": [{
      "Type": "Crafting",
      "Categories": ["Tool"],
      "Id": "Tool_Bench"
    }]
  }
}
```

### Power Scaling by Tier

| Pickaxe | Quality | ItemLevel | Rocks | VolcanicRocks | Durability |
|---------|---------|-----------|-------|---------------|------------|
| Crude | Common | 3 | 1.5 | 0.5 | 100 |
| Copper | Common | 10 | 2.0 | 1.0 | 150 |
| Iron | Uncommon | 20 | 3.0 | 1.5 | 250 |
| Cobalt | Rare | 30 | 4.0 | 2.0 | 300 |
| Mithril | Rare | 35 | 4.5 | 2.5 | 325 |
| Adamantite | Rare | 40 | 5.0 | 3.0 | 400 |

### All Pickaxe Variants

Tool_Pickaxe_Crude, Tool_Pickaxe_Copper, Tool_Pickaxe_Iron, Tool_Pickaxe_Cobalt, Tool_Pickaxe_Mithril, Tool_Pickaxe_Adamantite, Tool_Pickaxe_Bone, Tool_Pickaxe_Stone, Tool_Pickaxe_Bronze, Tool_Pickaxe_Steel

---

## Hatchet

**Location:** `Server/Item/Items/Tool/Hatchet/`

Woodcutting tool optimized for trees and wood blocks.

### Base Properties (Tool_Hatchet_Crude)

| Property | Value |
|----------|-------|
| `Quality` | Common |
| `ItemLevel` | 3 |
| `PlayerAnimationsId` | Hatchet |
| `MaxDurability` | 100 |
| `Categories` | Items.Tools |

### Tool.Specs

| GatherType | Power | Description |
|------------|-------|-------------|
| `Woods` | 1.5 | Primary use - wood blocks |
| `SoftBlocks` | 1.0 | General soft blocks |
| `Benches` | 1.0 | Crafting stations |

### Interactions

| Slot | Root Interaction | Description |
|------|------------------|-------------|
| `Primary` | Root_Tool_Hatchet_Primary | Block breaking swing |

Uses `BreakBlock` interaction with `Tool: "Hatchet"` specification.

### Tags

```json
{
  "Tags": {
    "Type": ["Tool"],
    "Family": ["Hatchet"]
  }
}
```

### Example Child: Iron Hatchet

```json
{
  "Parent": "Tool_Hatchet_Crude",
  "TranslationProperties": {
    "Name": "server.items.Tool_Hatchet_Iron.name"
  },
  "Model": "Items/Tools/Hatchet/Iron.blockymodel",
  "Texture": "Items/Tools/Hatchet/Iron_Texture.png",
  "Icon": "Icons/ItemsGenerated/Tool_Hatchet_Iron.png",
  "Quality": "Uncommon",
  "ItemLevel": 20,
  "MaxDurability": 250,
  "Tool": {
    "Specs": [
      { "Power": 3.0, "GatherType": "Woods" },
      { "Power": 1.0, "GatherType": "SoftBlocks" },
      { "Power": 1.0, "GatherType": "Benches", "HitSoundLayer": "Bench_Wood" }
    ]
  }
}
```

### Power Scaling by Tier

| Hatchet | Quality | ItemLevel | Woods | Durability |
|---------|---------|-----------|-------|------------|
| Crude | Common | 3 | 1.5 | 100 |
| Copper | Common | 10 | 2.0 | 150 |
| Iron | Uncommon | 20 | 3.0 | 250 |
| Cobalt | Rare | 30 | 4.0 | 300 |
| Mithril | Rare | 35 | 4.5 | 325 |
| Adamantite | Rare | 40 | 5.0 | 400 |

### All Hatchet Variants

Tool_Hatchet_Crude, Tool_Hatchet_Copper, Tool_Hatchet_Iron, Tool_Hatchet_Cobalt, Tool_Hatchet_Mithril, Tool_Hatchet_Adamantite, Tool_Hatchet_Bone, Tool_Hatchet_Stone, Tool_Hatchet_Bronze, Tool_Hatchet_Steel

---

## Shovel

**Location:** `Server/Item/Items/Tool/Shovel/`

Digging tool optimized for soil, sand, and dirt blocks.

### Base Properties (Tool_Shovel_Crude)

| Property | Value |
|----------|-------|
| `Quality` | Common |
| `ItemLevel` | 3 |
| `PlayerAnimationsId` | Shovel |
| `MaxDurability` | 100 |
| `Categories` | Items.Tools |

### Tool.Specs

| GatherType | Power | Description |
|------------|-------|-------------|
| `Soils` | 1.5 | Primary use - dirt, sand |
| `SoftBlocks` | 1.0 | General soft blocks |
| `Benches` | 1.0 | Crafting stations |

### Interactions

| Slot | Root Interaction | Description |
|------|------------------|-------------|
| `Primary` | Root_Tool_Shovel_Primary | Block breaking dig |

Uses `BreakBlock` interaction with `Tool: "Shovel"` specification.

### Tags

```json
{
  "Tags": {
    "Type": ["Tool"],
    "Family": ["Shovel"]
  }
}
```

### Power Scaling by Tier

| Shovel | Quality | ItemLevel | Soils | Durability |
|--------|---------|-----------|-------|------------|
| Crude | Common | 3 | 1.5 | 100 |
| Copper | Common | 10 | 2.0 | 150 |
| Iron | Uncommon | 20 | 3.0 | 250 |
| Steel | Rare | 30 | 4.0 | 300 |
| Adamantite | Rare | 40 | 5.0 | 400 |

### All Shovel Variants

Tool_Shovel_Crude, Tool_Shovel_Copper, Tool_Shovel_Iron, Tool_Shovel_Steel, Tool_Shovel_Adamantite

---

## Hoe

**Location:** `Server/Item/Items/Tool/Hoe/`

Farming tool that converts soil blocks for planting.

### Base Properties (Tool_Hoe_Crude)

| Property | Value |
|----------|-------|
| `Quality` | Common |
| `ItemLevel` | 3 |
| `PlayerAnimationsId` | Hoe |
| `MaxDurability` | 150 |
| `Categories` | Items.Tools |

### Interactions

| Slot | Root Interaction | Description |
|------|------------------|-------------|
| `Primary` | Root_Tool_Hoe_Primary | Till soil block |

Uses `ChangeBlock` interaction to convert blocks:

```json
{
  "Type": "ChangeBlock",
  "Changes": [
    { "From": "Soil_Grass", "To": "Soil_Dirt_Tilled" },
    { "From": "Soil_Dirt", "To": "Soil_Dirt_Tilled" },
    { "From": "Soil_Dirt_Tilled", "To": "Soil_Dirt" }
  ]
}
```

### Tags

```json
{
  "Tags": {
    "Type": ["Tool"],
    "Family": ["Hoe"]
  }
}
```

### All Hoe Variants

Tool_Hoe_Crude, Tool_Hoe_Iron, Tool_Hoe_Adamantite

---

## Hammer

**Location:** `Server/Item/Items/Tool/Hammer/`

Utility tool for cycling through block variants and rotations.

### Base Properties (Tool_Hammer_Crude)

| Property | Value |
|----------|-------|
| `Quality` | Common |
| `ItemLevel` | 10 |
| `PlayerAnimationsId` | Hammer |
| `MaxDurability` | 200 |
| `Categories` | Items.Tools |

### Interactions

| Slot | Root Interaction | Description |
|------|------------------|-------------|
| `Primary` | Root_Tool_Hammer_Primary | Cycle block variant |

Uses `CycleBlockGroup` interaction:

```json
{
  "Type": "CycleBlockGroup",
  "BlockSelectorTool": true,
  "DurabilityLossOnUse": 0.5
}
```

The `BlockSelectorTool` property enables special block selection UI.

### Tags

```json
{
  "Tags": {
    "Type": ["Tool"],
    "Family": ["Hammer"]
  }
}
```

### All Hammer Variants

Tool_Hammer_Crude, Tool_Hammer_Iron

---

## Shears

**Location:** `Server/Item/Items/Tool/Shears/`

Tool for shearing wool from animals.

### Base Properties (Tool_Shears)

| Property | Value |
|----------|-------|
| `Quality` | Common |
| `ItemLevel` | 10 |
| `PlayerAnimationsId` | Shears |
| `MaxDurability` | 100 |
| `Categories` | Items.Tools |

### Interactions

| Slot | Root Interaction | Description |
|------|------------------|-------------|
| `Primary` | Root_Tool_Shears_Primary | Shear animal |

Uses `ContextualUseNPC` interaction with "Shear" context:

```json
{
  "Type": "ContextualUseNPC",
  "Context": "Shear",
  "Range": 3.0,
  "DurabilityLossOnUse": 1.0
}
```

### Tags

```json
{
  "Tags": {
    "Type": ["Tool"],
    "Family": ["Shears"]
  }
}
```

### All Shears Variants

Tool_Shears

---

## Watering Can

**Location:** `Server/Item/Items/Tool/WateringCan/`

Farming tool for irrigating crops. Uses a state system with empty/filled variants.

### Base Properties (Tool_WateringCan_Empty)

| Property | Value |
|----------|-------|
| `Quality` | Common |
| `ItemLevel` | 5 |
| `PlayerAnimationsId` | WateringCan |
| `MaxDurability` | - |
| `Categories` | Items.Tools |

### State System

The watering can uses item states to track water level:

| Item | State | Primary Action |
|------|-------|----------------|
| `Tool_WateringCan_Empty` | Empty | Fill from water source |
| `Tool_WateringCan_Filled` | Filled | Water crops |

### Interactions (Empty)

| Slot | Root Interaction | Description |
|------|------------------|-------------|
| `Primary` | Root_Tool_WateringCan_Fill | Fill from water block |

```json
{
  "Type": "FillFromBlock",
  "BlockTypes": ["Water"],
  "ResultItem": "Tool_WateringCan_Filled"
}
```

### Interactions (Filled)

| Slot | Root Interaction | Description |
|------|------------------|-------------|
| `Primary` | Root_Tool_WateringCan_Water | Water crop block |

```json
{
  "Type": "WaterBlock",
  "Range": 4.0,
  "UsesPerFill": 8,
  "EmptyItem": "Tool_WateringCan_Empty"
}
```

### Tags

```json
{
  "Tags": {
    "Type": ["Tool"],
    "Family": ["WateringCan"]
  }
}
```

### All Watering Can Variants

Tool_WateringCan_Empty, Tool_WateringCan_Filled

---

## Sickle

**Location:** `Server/Item/Items/Tool/Sickle/`

Harvesting tool for cutting plants and crops efficiently.

### Base Properties (Tool_Sickle_Crude)

| Property | Value |
|----------|-------|
| `Quality` | Common |
| `ItemLevel` | 5 |
| `PlayerAnimationsId` | Sickle |
| `MaxDurability` | 100 |
| `Categories` | Items.Tools |

### Interactions

| Slot | Root Interaction | Description |
|------|------------------|-------------|
| `Primary` | Root_Tool_Sickle_Primary | Harvest plants |

Optimized for harvesting plant blocks with area effect.

### Tags

```json
{
  "Tags": {
    "Type": ["Tool"],
    "Family": ["Sickle"]
  }
}
```

### All Sickle Variants

Tool_Sickle_Crude, Tool_Sickle_Iron

---

## Repair Kit

**Location:** `Server/Item/Items/Tool/RepairKit/`

Utility item that opens a repair UI to restore item durability.

### Base Properties (Tool_RepairKit_Basic)

| Property | Value |
|----------|-------|
| `Quality` | Common |
| `ItemLevel` | 10 |
| `MaxStack` | 1 |
| `Consumable` | false |
| `Categories` | Items.Tools |

### Interactions

| Slot | Root Interaction | Description |
|------|------------------|-------------|
| `Primary` | Root_Tool_RepairKit_Open | Open repair UI |

Uses `OpenCustomUI` interaction:

```json
{
  "Type": "OpenCustomUI",
  "PageId": "ItemRepair",
  "RepairPenalty": 0.1
}
```

| Property | Type | Description |
|----------|------|-------------|
| `PageId` | string | UI page to open |
| `RepairPenalty` | float | Max durability reduction per repair (10%) |

### Repair Kit Tiers

| Repair Kit | Quality | RepairPenalty | Description |
|------------|---------|---------------|-------------|
| Basic | Common | 0.10 | 10% max durability loss |
| Advanced | Uncommon | 0.05 | 5% max durability loss |
| Master | Rare | 0.02 | 2% max durability loss |

### Tags

```json
{
  "Tags": {
    "Type": ["Tool"],
    "Family": ["RepairKit"]
  }
}
```

### All Repair Kit Variants

Tool_RepairKit_Basic, Tool_RepairKit_Advanced, Tool_RepairKit_Master

---

## Capture Crate

**Location:** `Server/Item/Items/Tool/CaptureCrate/`

Utility item for capturing and transporting animals.

### Base Properties (Tool_CaptureCrate)

| Property | Value |
|----------|-------|
| `Quality` | Common |
| `ItemLevel` | 15 |
| `MaxStack` | 1 |
| `Consumable` | true |
| `Categories` | Items.Tools |

### Interactions

| Slot | Root Interaction | Description |
|------|------------------|-------------|
| `Primary` | Root_Tool_CaptureCrate_Use | Capture animal |

Uses `UseCaptureCrate` interaction:

```json
{
  "Type": "UseCaptureCrate",
  "Range": 3.0,
  "AcceptedNpcGroups": ["Livestock", "Pets", "SmallAnimals"],
  "CapturedItem": "Tool_CaptureCrate_Filled"
}
```

| Property | Type | Description |
|----------|------|-------------|
| `AcceptedNpcGroups` | array | NPC groups that can be captured |
| `CapturedItem` | string | Item created with captured entity |
| `Range` | float | Maximum capture distance |

### Tags

```json
{
  "Tags": {
    "Type": ["Tool"],
    "Family": ["CaptureCrate"]
  }
}
```

### All Capture Crate Variants

Tool_CaptureCrate, Tool_CaptureCrate_Filled

---

## Feedbag

**Location:** `Server/Item/Items/Tool/Feedbag/`

Farming tool for feeding animals.

### Base Properties (Tool_Feedbag)

| Property | Value |
|----------|-------|
| `Quality` | Common |
| `ItemLevel` | 5 |
| `MaxStack` | 1 |
| `Categories` | Items.Tools |

### Interactions

| Slot | Root Interaction | Description |
|------|------------------|-------------|
| `Primary` | Root_Tool_Feedbag_Use | Feed animal |

Uses `ContextualUseNPC` interaction with "Feed" context.

### Tags

```json
{
  "Tags": {
    "Type": ["Tool"],
    "Family": ["Feedbag"]
  }
}
```

### All Feedbag Variants

Tool_Feedbag

---

## Fertilizer

**Location:** `Server/Item/Items/Tool/Fertilizer/`

Farming consumable that accelerates plant growth.

### Base Properties (Tool_Fertilizer)

| Property | Value |
|----------|-------|
| `Quality` | Common |
| `ItemLevel` | 5 |
| `MaxStack` | 25 |
| `Consumable` | true |
| `Categories` | Items.Tools |

### Interactions

| Slot | Root Interaction | Description |
|------|------------------|-------------|
| `Primary` | Root_Tool_Fertilizer_Use | Apply to crop |

Uses block interaction to advance plant growth stage.

### Tags

```json
{
  "Tags": {
    "Type": ["Tool"],
    "Family": ["Fertilizer"]
  }
}
```

### All Fertilizer Variants

Tool_Fertilizer

---

## Common Tool Patterns

### BreakBlock Interaction

Most gathering tools use `BreakBlock` with a tool type specification:

```json
{
  "Type": "BreakBlock",
  "Tool": "Pickaxe",
  "Range": 4.5,
  "Effects": {
    "WorldSoundEventId": "SFX_Pickaxe_Impact",
    "Particles": [{ "SystemId": "Block_Break" }]
  }
}
```

| Property | Type | Description |
|----------|------|-------------|
| `Tool` | string | Tool type required (Pickaxe, Hatchet, Shovel) |
| `Range` | float | Maximum block interaction distance |

### ChangeBlock Interaction

Used by hoes and similar tools to transform blocks:

```json
{
  "Type": "ChangeBlock",
  "Changes": [
    { "From": "Soil_Grass", "To": "Soil_Dirt_Tilled" }
  ],
  "DurabilityLossOnUse": 1.0
}
```

### Tool Durability

Tools lose durability based on block type:

```json
{
  "Tool": {
    "DurabilityLossBlockTypes": [
      { "BlockSetIds": ["Stone"], "DurabilityLoss": 1.0 },
      { "BlockSetIds": ["Ores"], "DurabilityLoss": 1.5 }
    ]
  }
}
```

Default durability loss (if not specified in DurabilityLossBlockTypes) is defined by `DurabilityLossOnHit` in base properties.

---

## Sound Sets

| Tool Family | ItemSoundSetId |
|-------------|----------------|
| Pickaxe | ISS_Tool_Pickaxe |
| Hatchet | ISS_Tool_Hatchet |
| Shovel | ISS_Tool_Shovel |
| Hoe | ISS_Tool_Hoe |
| Hammer | ISS_Tool_Hammer |
| Shears | ISS_Tool_Shears |

---

## Related Documentation

- [Items Reference](items.md) - Common properties and systems
- [Interactions API](interactions.md) - Tool interactions
- [Block Interactions](interactions-block.md) - BreakBlock, ChangeBlock, PlaceBlock
- [Weapons Reference](items-weapons.md) - Combat items
