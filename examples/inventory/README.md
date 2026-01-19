# Inventory Example Plugin

Demonstrates the Hytale inventory API with commands for managing player items.

## Commands

### `/give <item> <quantity>`
Add items to your inventory. Tries hotbar first, then storage.

**Examples:**
- `/give hytale:wooden_sword` - Give 1 wooden sword
- `/give hytale:apple 10` - Give 10 apples

**API demonstrated:** `ItemStack`, `ItemContainer.addItemStack()`, transaction handling

### `/inv-clear <section>`
Clear inventory sections.

**Examples:**
- `/inv-clear all` - Clear entire inventory
- `/inv-clear hotbar` - Clear only hotbar
- `/inv-clear storage` - Clear only storage
- `/inv-clear armor` - Clear equipped armor

**Valid sections:** hotbar, storage, armor, utility, tools, backpack

**API demonstrated:** `Inventory.clear()`, section access via `getHotbar()`, etc.

### `/inspect`
Show current inventory contents and statistics.

**Output includes:**
- Currently held item
- Active hotbar slot
- Hotbar/storage slot usage and item counts
- Armor slots equipped
- Total item count

**API demonstrated:** `getItemInHand()`, container iteration with `forEach()`

### `/sort <type>`
Sort storage inventory.

**Examples:**
- `/sort name` - Sort alphabetically by item name
- `/sort type` - Sort by item category
- `/sort rarity` - Sort by item rarity

**API demonstrated:** `Inventory.sortStorage(SortType)`

## Building

```batch
build.bat
```

Or:

```batch
gradlew build
```

## Installation

Copy `build/libs/example-inventory.jar` to:
```
%APPDATA%\Hytale\UserData\Mods\
```

Or use:
```batch
deploy.bat
```

## Code Structure

- `InventoryPlugin.java` - Main plugin class, registers all commands
- `GiveCommand.java` - Adding items with transaction handling
- `ClearCommand.java` - Clearing inventory sections
- `InspectCommand.java` - Reading inventory state
- `SortCommand.java` - Sorting storage with SortType enum

## Key API Patterns

### Getting Player Inventory
```java
Player player = store.getComponent(ref, Player.getComponentType());
Inventory inventory = player.getInventory();
```

### Adding Items with Combined Container
```java
ItemStack item = new ItemStack("hytale:apple", 10);
CombinedItemContainer combined = inventory.getCombinedHotbarFirst();
ItemStackTransaction result = combined.addItemStack(item);
if (result.getRemainder() == null) {
    // All items added
} else {
    ItemStack remainder = result.getRemainder();
    // Handle overflow
}
```

### Iterating Container Contents
```java
container.forEach((slot, itemStack) -> {
    if (!itemStack.isEmpty()) {
        // Process item
    }
});
```

### Checking Held Item
```java
ItemStack heldItem = inventory.getItemInHand();
if (!heldItem.isEmpty()) {
    String itemId = heldItem.getItemId();
    int quantity = heldItem.getQuantity();
}
```
