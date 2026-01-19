# Consumable Items

> Part of the [Items API](items.md). For common item properties, see [Items Reference](items.md#common-properties).

## Quick Navigation

| Category | Template | Count | Description |
|----------|----------|-------|-------------|
| [Food](#food-system) | Template_Food, Template_Fruit | 30+ | Healing and stat buffs |
| [Potions](#potion-system) | Potion_Template | 30+ | Instant effects and transformations |

---

## Food System

Food items restore health and provide temporary buffs when consumed. The system uses timed consumption with charging mechanics.

### Template_Food

**Location:** `Server/Item/Items/Food/Template_Food.json`

Base template for all prepared food items (bread, pies, kebabs, salads).

#### Base Properties

| Property | Value |
|----------|-------|
| `Quality` | Template |
| `MaxStack` | 25 |
| `Consumable` | true |
| `Categories` | Items.Foods |

#### Interactions

| Slot | Root Interaction | Description |
|------|------------------|-------------|
| `Secondary` | Root_Consumable_Eat | Timed consumption |

Food uses the `Secondary` (right-click) slot for consumption, leaving `Primary` available for other actions.

#### Tags

```json
{
  "Tags": {
    "Type": ["Food"]
  }
}
```

#### InteractionVars

Child food items customize these variables:

| Variable | Purpose |
|----------|---------|
| `Consume_Charge` | Charging configuration (duration, movement speed) |
| `Effect` | ApplyEffect interaction for healing/buffs |
| `ConsumeSFX` | Sound during consumption |
| `ConsumedSFX` | Sound when consumption completes |

#### Template InteractionVars

```json
{
  "InteractionVars": {
    "Consume_Charge": {
      "Interactions": [{
        "Parent": "Consumable_Eat_Charge",
        "ChargeTime": 1.5,
        "FailOnDamage": true,
        "HorizontalSpeedMultiplier": 0.5,
        "Effects": {
          "LocalSoundEventId": "SFX_Eating_Local"
        }
      }]
    },
    "Effect": {
      "Interactions": [{
        "Type": "ApplyEffect",
        "EffectId": "Food_Instant_Heal_T1"
      }]
    },
    "ConsumeSFX": {
      "Interactions": [{
        "Type": "PlaySound",
        "LocalSoundEventId": "SFX_Eating_Local"
      }]
    },
    "ConsumedSFX": {
      "Interactions": [{
        "Type": "PlaySound",
        "LocalSoundEventId": "SFX_Eating_Finish_Local"
      }]
    }
  }
}
```

---

### Template_Fruit

**Location:** `Server/Item/Items/Plant/Fruit/Template_Fruit.json`

Base template for fruit items. Inherits food consumption behavior with fruit-specific properties.

#### Base Properties

| Property | Value |
|----------|-------|
| `Quality` | Template |
| `MaxStack` | 25 |
| `Consumable` | true |
| `Categories` | Items.Foods |

#### Tags

```json
{
  "Tags": {
    "Type": ["Food"],
    "Family": ["Fruit"]
  }
}
```

#### ResourceTypes

Fruits register with resource types for recipe flexibility:

```json
{
  "ResourceTypes": [
    { "Id": "Foods" },
    { "Id": "Fruits" }
  ]
}
```

---

### Template_Crop_Item

**Location:** `Server/Item/Items/Plant/Crop/_Template/Template_Crop_Item.json`

Base template for harvested vegetables and crops.

#### Base Properties

| Property | Value |
|----------|-------|
| `Quality` | Template |
| `MaxStack` | 25 |
| `Consumable` | true |
| `Categories` | Items.Foods |

#### Tags

```json
{
  "Tags": {
    "Type": ["Food"],
    "Family": ["Vegetable"]
  }
}
```

#### ResourceTypes

```json
{
  "ResourceTypes": [
    { "Id": "Foods" },
    { "Id": "Vegetables" }
  ]
}
```

---

### Food Tiers

Food items follow a tiered progression affecting healing power and buff strength:

| Tier | Quality | Consume Time | Instant Heal | Regen Buff |
|------|---------|--------------|--------------|------------|
| T1 | Common | 1.5s | 5% | HealthRegen_Buff_T1 |
| T2 | Uncommon | 2.0s | 10% | HealthRegen_Buff_T2 |
| T3 | Rare | 2.5s | 15% | HealthRegen_Buff_T3 |

---

### Food Buff System

Food items can apply various buffs in addition to instant healing:

#### Health Regeneration Buffs

| Buff | Duration | Effect |
|------|----------|--------|
| `HealthRegen_Buff_T1` | 30s | +1 HP/s |
| `HealthRegen_Buff_T2` | 45s | +2 HP/s |
| `HealthRegen_Buff_T3` | 60s | +3 HP/s |

#### Meat Buffs

Cooked meats provide maximum health increases:

| Buff | Duration | Effect |
|------|----------|--------|
| `Meat_Buff_T1` | 120s | +5% Max Health |
| `Meat_Buff_T2` | 180s | +10% Max Health |
| `Meat_Buff_T3` | 240s | +15% Max Health |

#### Fruit/Vegetable Buffs

Plant-based foods boost maximum stamina:

| Buff | Duration | Effect |
|------|----------|--------|
| `FruitVeggie_Buff_T1` | 120s | +10% Max Stamina |
| `FruitVeggie_Buff_T2` | 180s | +15% Max Stamina |
| `FruitVeggie_Buff_T3` | 240s | +20% Max Stamina |

---

### Example Child: Food_Bread

```json
{
  "Parent": "Template_Food",
  "TranslationProperties": {
    "Name": "server.items.Food_Bread.name"
  },
  "Model": "Items/Food/Bread.blockymodel",
  "Texture": "Items/Food/Bread_Texture.png",
  "Icon": "Icons/ItemsGenerated/Food_Bread.png",
  "Quality": "Common",
  "Recipe": {
    "TimeSeconds": 5.0,
    "Input": [
      { "ItemId": "Ingredient_Flour", "Quantity": 3 }
    ],
    "BenchRequirement": [{
      "Type": "Crafting",
      "Categories": ["Food"],
      "Id": "Cooking_Campfire"
    }]
  },
  "InteractionVars": {
    "Consume_Charge": {
      "Interactions": [{
        "Parent": "Consumable_Eat_Charge",
        "ChargeTime": 1.5,
        "FailOnDamage": true,
        "HorizontalSpeedMultiplier": 0.5
      }]
    },
    "Effect": {
      "Interactions": [{
        "Type": "ApplyEffect",
        "EffectId": "Food_Instant_Heal_T1"
      }]
    }
  }
}
```

---

### Food Categories

#### Raw Foods

Unprocessed foods with minimal healing:

| Item | Quality | Healing | Source |
|------|---------|---------|--------|
| `Food_Meat_Raw` | Common | T1 | Animal drops |
| `Plant_Fruit_Apple` | Common | T1 | Apple trees |
| `Plant_Fruit_Berry` | Common | T1 | Berry bushes |
| `Plant_Crop_Carrot` | Common | T1 | Farming |
| `Plant_Crop_Potato` | Common | T1 | Farming |

#### Cooked Foods

Processed at campfire with improved healing:

| Item | Quality | Healing | Buff |
|------|---------|---------|------|
| `Food_Meat_Cooked` | Uncommon | T2 | Meat_Buff_T1 |
| `Food_Meat_Cooked_Prime` | Rare | T3 | Meat_Buff_T2 |
| `Food_Fish_Cooked` | Uncommon | T2 | - |

#### Prepared Foods

Crafted at cooking bench with best effects:

| Item | Quality | Healing | Buff |
|------|---------|---------|------|
| `Food_Bread` | Common | T1 | - |
| `Food_Pie_Apple` | Uncommon | T2 | FruitVeggie_Buff_T1 |
| `Food_Pie_Berry` | Uncommon | T2 | FruitVeggie_Buff_T1 |
| `Food_Salad_Garden` | Uncommon | T2 | FruitVeggie_Buff_T2 |
| `Food_Kebab_Meat` | Rare | T3 | Meat_Buff_T2 |
| `Food_Stew_Hearty` | Rare | T3 | HealthRegen_Buff_T3 |

---

### All Food Variants

Food_Bread, Food_Bread_Baguette, Food_Kebab_Fish, Food_Kebab_Meat, Food_Kebab_Veggie, Food_Meat_Cooked, Food_Meat_Cooked_Prime, Food_Meat_Raw, Food_Pie_Apple, Food_Pie_Berry, Food_Pie_Meat, Food_Salad_Fruit, Food_Salad_Garden, Food_Stew_Fish, Food_Stew_Hearty, Food_Stew_Veggie, Plant_Crop_Beetroot, Plant_Crop_Cabbage, Plant_Crop_Carrot, Plant_Crop_Corn, Plant_Crop_Onion, Plant_Crop_Potato, Plant_Crop_Pumpkin, Plant_Crop_Tomato, Plant_Crop_Wheat, Plant_Fruit_Apple, Plant_Fruit_Berry, Plant_Fruit_Orange, Plant_Fruit_Pear

---

## Potion System

Potions provide instant effects and transformations. Unlike food, potions typically have conditional consumption based on stat checks.

### Potion_Template

**Location:** `Server/Item/Items/Potion/Potion_Template.json`

Base template for all potion items.

#### Base Properties

| Property | Value |
|----------|-------|
| `Quality` | Template |
| `MaxStack` | 10 |
| `Consumable` | true |
| `Categories` | Items.Potions |

#### Interactions

| Slot | Root Interaction | Description |
|------|------------------|-------------|
| `Secondary` | Root_Potion_Drink | Instant consumption with stat check |

#### Tags

```json
{
  "Tags": {
    "Type": ["Potion"]
  }
}
```

#### BlockType (Placeable)

Potions can be placed as decorative light sources:

```json
{
  "BlockType": {
    "BlockPlaceProperties": {
      "BlockId": "Potion_Placed",
      "LightEmission": 8
    }
  }
}
```

| Property | Type | Description |
|----------|------|-------------|
| `BlockId` | string | Block to place when using on ground |
| `LightEmission` | int | Light level (0-15) |

#### InteractionVars

| Variable | Purpose |
|----------|---------|
| `Effect` | ApplyEffect or ChangeStat interaction |
| `Stat_Check` | Condition for consumption (e.g., health not full) |
| `RemoveEffect` | Effect to clear on consumption (e.g., poison) |
| `DrinkSFX` | Sound when drinking |

---

### Stat Check Condition

Potions use stat checks to prevent wasting potions when unnecessary:

```json
{
  "Stat_Check": {
    "Interactions": [{
      "Parent": "Stat_Check",
      "Costs": { "Health": 100 },
      "ValueType": "Percent",
      "LessThan": true
    }]
  }
}
```

| Property | Type | Description |
|----------|------|-------------|
| `Costs` | object | Stat to check and threshold |
| `ValueType` | string | `"Percent"` or `"Absolute"` |
| `LessThan` | boolean | If true, stat must be below threshold |

Example: `Health: 100` with `LessThan: true` means "only drink if health < 100%".

---

### Health Potions

Restore health instantly with stat check to prevent waste.

| Potion | Quality | Restore | Stat_Check |
|--------|---------|---------|------------|
| `Potion_Health` | Common | +30% HP | Health < 100% |
| `Potion_Health_Greater` | Uncommon | +50% HP | Health < 100% |
| `Potion_Health_Supreme` | Rare | +75% HP | Health < 100% |

#### Example: Potion_Health

```json
{
  "Parent": "Potion_Template",
  "TranslationProperties": {
    "Name": "server.items.Potion_Health.name"
  },
  "Model": "Items/Potions/Potion_Health.blockymodel",
  "Texture": "Items/Potions/Potion_Health_Texture.png",
  "Icon": "Icons/ItemsGenerated/Potion_Health.png",
  "Quality": "Common",
  "Recipe": {
    "TimeSeconds": 3.0,
    "Input": [
      { "ItemId": "Ingredient_Bottle_Empty", "Quantity": 1 },
      { "ItemId": "Plant_Herb_Healing", "Quantity": 2 }
    ],
    "BenchRequirement": [{
      "Type": "Crafting",
      "Categories": ["Potion"],
      "Id": "Alchemy_Bench"
    }]
  },
  "InteractionVars": {
    "Stat_Check": {
      "Interactions": [{
        "Parent": "Stat_Check",
        "Costs": { "Health": 100 },
        "ValueType": "Percent",
        "LessThan": true
      }]
    },
    "Effect": {
      "Interactions": [{
        "Type": "ChangeStat",
        "StatModifiers": { "Health": 0.30 },
        "ValueType": "Percent"
      }]
    }
  }
}
```

---

### Stamina Potions

Restore stamina instantly with stat check.

| Potion | Quality | Restore | Stat_Check |
|--------|---------|---------|------------|
| `Potion_Stamina` | Common | +40% Stamina | Stamina < 100% |
| `Potion_Stamina_Greater` | Uncommon | +60% Stamina | Stamina < 100% |
| `Potion_Stamina_Supreme` | Rare | +100% Stamina | Stamina < 100% |

---

### Regeneration Potions

Apply over-time healing effects.

| Potion | Quality | Effect | Duration |
|--------|---------|--------|----------|
| `Potion_Regen_Health` | Uncommon | +2 HP/s | 30s |
| `Potion_Regen_Health_Greater` | Rare | +4 HP/s | 45s |
| `Potion_Regen_Stamina` | Uncommon | +3 Stamina/s | 30s |
| `Potion_Regen_Stamina_Greater` | Rare | +5 Stamina/s | 45s |

#### Example: Potion_Regen_Health

```json
{
  "Parent": "Potion_Template",
  "TranslationProperties": {
    "Name": "server.items.Potion_Regen_Health.name"
  },
  "Quality": "Uncommon",
  "InteractionVars": {
    "Effect": {
      "Interactions": [{
        "Type": "ApplyEffect",
        "EffectId": "Potion_HealthRegen_T2"
      }]
    }
  }
}
```

---

### Morph Potions

Transform the player into creatures for exploration or stealth.

| Potion | Quality | Form | Duration |
|--------|---------|------|----------|
| `Potion_Morph_Dog` | Rare | Dog | 120s |
| `Potion_Morph_Frog` | Rare | Frog | 120s |
| `Potion_Morph_Mouse` | Rare | Mouse | 120s |
| `Potion_Morph_Pigeon` | Rare | Pigeon | 120s |

#### Morph Effect Structure

```json
{
  "Type": "ApplyEffect",
  "EffectId": "Potion_Morph_Dog",
  "EffectProperties": {
    "ModelChange": {
      "EntityTypeId": "Creature_Dog",
      "Duration": 120.0
    }
  }
}
```

| Property | Type | Description |
|----------|------|-------------|
| `EntityTypeId` | string | Entity to transform into |
| `Duration` | float | Morph duration in seconds |

#### Morph Abilities

Different morphs grant unique movement abilities:

| Form | Special Ability |
|------|-----------------|
| Frog | High jump, swim speed |
| Mouse | Small hitbox, fit through gaps |
| Pigeon | Flight, perch on ledges |
| Dog | Fast run speed, tracking scent |

---

### Utility Potions

Special-purpose potions for specific situations.

| Potion | Quality | Effect |
|--------|---------|--------|
| `Potion_Antidote` | Common | Clears Poison effect |
| `Potion_Antidote_Greater` | Uncommon | Clears all negative effects |
| `Potion_Invisibility` | Rare | Invisibility for 30s |
| `Potion_NightVision` | Uncommon | See in dark for 120s |
| `Potion_Featherfall` | Uncommon | Slow falling for 60s |
| `Potion_WaterBreathing` | Uncommon | Breathe underwater for 180s |

#### Example: Potion_Antidote

```json
{
  "Parent": "Potion_Template",
  "TranslationProperties": {
    "Name": "server.items.Potion_Antidote.name"
  },
  "Quality": "Common",
  "InteractionVars": {
    "RemoveEffect": {
      "Interactions": [{
        "Type": "RemoveEffect",
        "EffectId": "Debuff_Poison"
      }]
    },
    "Effect": {
      "Interactions": [{
        "Type": "ApplyEffect",
        "EffectId": "Potion_Antidote_Immunity",
        "Duration": 30.0
      }]
    }
  }
}
```

---

### Signature Potions

Weapon-specific potions that boost signature ability damage.

| Potion | Quality | Effect | Duration |
|--------|---------|--------|----------|
| `Potion_Signature_Damage` | Rare | +25% Signature Damage | 60s |
| `Potion_Signature_Energy` | Rare | +50% Signature Energy Gain | 60s |
| `Potion_Signature_Cooldown` | Rare | -30% Signature Cooldown | 60s |

---

### All Potion Variants

Potion_Antidote, Potion_Antidote_Greater, Potion_Featherfall, Potion_Health, Potion_Health_Greater, Potion_Health_Supreme, Potion_Invisibility, Potion_Morph_Dog, Potion_Morph_Frog, Potion_Morph_Mouse, Potion_Morph_Pigeon, Potion_NightVision, Potion_Regen_Health, Potion_Regen_Health_Greater, Potion_Regen_Stamina, Potion_Regen_Stamina_Greater, Potion_Signature_Cooldown, Potion_Signature_Damage, Potion_Signature_Energy, Potion_Stamina, Potion_Stamina_Greater, Potion_Stamina_Supreme, Potion_WaterBreathing

---

## Common Consumable Patterns

### Consumable Property

All consumable items share these core properties:

```json
{
  "Consumable": true,
  "MaxStack": 25
}
```

The `Consumable: true` flag tells the engine to remove one item from the stack when consumed.

### ApplyEffect Interaction

The standard way to grant buffs from consumables:

```json
{
  "Type": "ApplyEffect",
  "EffectId": "Food_Instant_Heal_T1"
}
```

For effects with custom duration:

```json
{
  "Type": "ApplyEffect",
  "EffectId": "Buff_HealthRegen",
  "Duration": 60.0
}
```

### ChangeStat Interaction

For instant stat modifications without buff effects:

```json
{
  "Type": "ChangeStat",
  "StatModifiers": { "Health": 0.30 },
  "ValueType": "Percent"
}
```

| Property | Type | Description |
|----------|------|-------------|
| `StatModifiers` | object | Stat ID to value mapping |
| `ValueType` | string | `"Percent"` (0.30 = 30%) or `"Absolute"` |

### ChargingInteraction (Food)

Food items use charging for timed consumption:

```json
{
  "Type": "ChargingInteraction",
  "ChargeTime": 1.5,
  "FailOnDamage": true,
  "HorizontalSpeedMultiplier": 0.5,
  "CompleteInteraction": {
    "Type": "Sequence",
    "Interactions": [
      { "Type": "Replace", "Var": "Effect" },
      {
        "Type": "ModifyInventory",
        "ItemToRemove": { "Self": true, "Quantity": 1 }
      }
    ]
  }
}
```

| Property | Type | Description |
|----------|------|-------------|
| `ChargeTime` | float | Seconds to complete consumption |
| `FailOnDamage` | boolean | Cancel on taking damage |
| `HorizontalSpeedMultiplier` | float | Movement speed while consuming (0.5 = 50%) |
| `CompleteInteraction` | object | Interaction to run when charge completes |

### ModifyInventory Interaction

Removes consumed item from inventory:

```json
{
  "Type": "ModifyInventory",
  "ItemToRemove": {
    "Self": true,
    "Quantity": 1
  }
}
```

### RemoveEffect Interaction

Clears debuffs (used by antidotes):

```json
{
  "Type": "RemoveEffect",
  "EffectId": "Debuff_Poison"
}
```

For clearing multiple effects:

```json
{
  "Type": "RemoveEffect",
  "EffectTags": ["Debuff"]
}
```

---

## Creating Custom Consumables

### Custom Food Item

```json
{
  "Parent": "Template_Food",
  "TranslationProperties": {
    "Name": "server.items.My_Custom_Food.name"
  },
  "Icon": "Icons/ItemsGenerated/My_Food.png",
  "Quality": "Uncommon",
  "MaxStack": 25,
  "InteractionVars": {
    "Consume_Charge": {
      "Interactions": [{
        "Parent": "Consumable_Eat_Charge",
        "ChargeTime": 2.0,
        "FailOnDamage": true,
        "HorizontalSpeedMultiplier": 0.4
      }]
    },
    "Effect": {
      "Interactions": [
        {
          "Type": "ApplyEffect",
          "EffectId": "Food_Instant_Heal_T2"
        },
        {
          "Type": "ApplyEffect",
          "EffectId": "Meat_Buff_T1"
        }
      ]
    }
  }
}
```

### Custom Potion Item

```json
{
  "Parent": "Potion_Template",
  "TranslationProperties": {
    "Name": "server.items.My_Custom_Potion.name"
  },
  "Icon": "Icons/ItemsGenerated/My_Potion.png",
  "Quality": "Rare",
  "MaxStack": 10,
  "InteractionVars": {
    "Stat_Check": {
      "Interactions": [{
        "Parent": "Stat_Check",
        "Costs": { "Health": 50 },
        "ValueType": "Percent",
        "LessThan": true
      }]
    },
    "Effect": {
      "Interactions": [{
        "Type": "ApplyEffect",
        "EffectId": "My_Custom_Buff",
        "Duration": 90.0
      }]
    }
  }
}
```

---

## Related Documentation

- [Items Reference](items.md) - Common properties and systems
- [Effects & Stats](effects-stats.md) - Status effects and stat modifiers
- [Interactions API](interactions.md) - ApplyEffect, ChangeStat, ModifyInventory
- [Weapons Reference](items-weapons.md) - Combat items
- [Tools Reference](items-tools.md) - Gathering tools
