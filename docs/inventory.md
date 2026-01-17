# Inventory API

## Inventory
**Package:** `com.hypixel.hytale.server.core.inventory`

Player inventory with multiple sections.

### Section IDs
```java
static final int HOTBAR_SECTION_ID
static final int STORAGE_SECTION_ID
static final int ARMOR_SECTION_ID
static final int UTILITY_SECTION_ID
static final int TOOLS_SECTION_ID
static final int BACKPACK_SECTION_ID
```

### Default Capacities
```java
static final int DEFAULT_HOTBAR_CAPACITY
static final int DEFAULT_UTILITY_CAPACITY
static final int DEFAULT_TOOLS_CAPACITY
static final int DEFAULT_ARMOR_CAPACITY
static final int DEFAULT_STORAGE_ROWS
static final int DEFAULT_STORAGE_COLUMNS
static final int DEFAULT_STORAGE_CAPACITY
static final int INACTIVE_SLOT_INDEX
```

### Get Sections
```java
ItemContainer getHotbar()
ItemContainer getStorage()
ItemContainer getArmor()
ItemContainer getUtility()
ItemContainer getTools()
ItemContainer getBackpack()
ItemContainer getSectionById(int sectionId)
```

### Combined Containers
```java
CombinedItemContainer getCombinedHotbarFirst()
CombinedItemContainer getCombinedStorageFirst()
CombinedItemContainer getCombinedEverything()
CombinedItemContainer getCombinedArmorHotbarStorage()
CombinedItemContainer getCombinedArmorHotbarUtilityStorage()
CombinedItemContainer getCombinedHotbarUtilityConsumableStorage()
CombinedItemContainer getCombinedBackpackStorageHotbar()
```

### Active Slots
```java
// Hotbar
byte getActiveHotbarSlot()
void setActiveHotbarSlot(byte slot)
ItemStack getActiveHotbarItem()

// Tools
byte getActiveToolsSlot()
void setActiveToolsSlot(byte slot)
ItemStack getToolsItem()
ItemStack getActiveToolItem()
boolean usingToolsItem()
void setUsingToolsItem(boolean using)

// Utility
byte getActiveUtilitySlot()
void setActiveUtilitySlot(byte slot)
ItemStack getUtilityItem()

// General
byte getActiveSlot(int sectionId)
void setActiveSlot(int sectionId, byte slot)
ItemStack getItemInHand()
```

### Item Operations
```java
void moveItem(int fromSection, int fromSlot, int toSection, int toSlot, int count)
void smartMoveItem(int section, int slot, int count, SmartMoveType type)
ListTransaction<MoveTransaction<ItemStackTransaction>> takeAll(int section)
ListTransaction<MoveTransaction<ItemStackTransaction>> putAll(int section)
ListTransaction<MoveTransaction<ItemStackTransaction>> quickStack(int section)
List<ItemStack> dropAllItemStacks()
void clear()
```

### Sorting & Management
```java
void sortStorage(SortType sortType)
void setSortType(SortType sortType)
void resizeBackpack(short size, List<ItemStack> overflow)
void markChanged()
boolean consumeIsDirty()
boolean consumeNeedsSaving()
boolean containsBrokenItem()
```

---

## SmartMoveType
**Package:** `com.hypixel.hytale.protocol`

Enum for smart move operations. Used with `Inventory.smartMoveItem()`.

```java
public enum SmartMoveType {
    EquipOrMergeStack,     // Equip item or merge with existing stack
    PutInHotbarOrWindow,   // Move to hotbar or active window
    PutInHotbarOrBackpack  // Move to hotbar or backpack
}
```

---

## ItemStack
**Package:** `com.hypixel.hytale.server.core.inventory`

Represents an item with quantity and metadata. **Immutable** - modification methods return new instances.

### Constants
```java
static final ItemStack EMPTY          // Empty stack constant
static final ItemStack[] EMPTY_ARRAY  // Empty array constant
```

### Constructors
```java
ItemStack(String itemId)
ItemStack(String itemId, int quantity)
ItemStack(String itemId, int quantity, BsonDocument metadata)
ItemStack(String itemId, int quantity, double durability, double maxDurability, BsonDocument metadata)
```

### Getters
```java
String getItemId()
int getQuantity()
BsonDocument getMetadata()
double getDurability()
double getMaxDurability()
boolean isUnbreakable()
boolean isBroken()
boolean isEmpty()
boolean isValid()
Item getItem()
String getBlockKey()
boolean getOverrideDroppedItemAnimation()
```

### Modification Methods (Return New ItemStack)
```java
// Durability
ItemStack withDurability(double durability)
ItemStack withMaxDurability(double maxDurability)
ItemStack withIncreasedDurability(double amount)
ItemStack withRestoredDurability(double amount)

// State & Quantity
ItemStack withState(String state)
ItemStack withQuantity(int quantity)

// Metadata
ItemStack withMetadata(BsonDocument metadata)
<T> ItemStack withMetadata(KeyedCodec<T> codec, T value)
<T> ItemStack withMetadata(String key, Codec<T> codec, T value)
ItemStack withMetadata(String key, BsonValue value)
```

### Metadata Access
```java
<T> T getFromMetadataOrNull(KeyedCodec<T> codec)
<T> T getFromMetadataOrNull(String key, Codec<T> codec)
<T> T getFromMetadataOrDefault(String key, BuilderCodec<T> codec)
```

### Comparison Methods
```java
boolean isStackableWith(ItemStack other)
boolean isEquivalentType(ItemStack other)

// Static versions
static boolean isEmpty(ItemStack stack)
static boolean isStackableWith(ItemStack a, ItemStack b)
static boolean isEquivalentType(ItemStack a, ItemStack b)
static boolean isSameItemType(ItemStack a, ItemStack b)
```

### Static Factory
```java
static ItemStack fromPacket(ItemQuantity packet)
```

---

## Item
**Package:** `com.hypixel.hytale.server.core.asset.type.item.config`

Asset type for items. Get from `ItemStack.getItem()`.

### Constants
```java
static final Item UNKNOWN  // Unknown/invalid item
```

### Static Methods
```java
static AssetStore<String, Item, ...> getAssetStore()
static AssetMap<String, Item> getAssetMap()
```

### Identity
```java
String getId()
String getBlockId()           // Associated block ID (if placeable)
String getTranslationKey()
String getDescriptionTranslationKey()
boolean hasBlockType()        // Whether item places a block
```

### Properties
```java
int getMaxStack()
int getItemLevel()
int getQualityIndex()
double getMaxDurability()
boolean isConsumable()
boolean isVariant()
boolean isState()
boolean dropsOnDeath()
```

### Visual
```java
String getModel()
String getTexture()
String getIcon()
float getScale()
String getReticleId()
AssetIconProperties getIconProperties()
```

### Equipment Types
```java
ItemTool getTool()            // Tool properties (if tool)
ItemArmor getArmor()          // Armor properties (if armor)
ItemWeapon getWeapon()        // Weapon properties (if weapon)
ItemGlider getGlider()        // Glider properties (if glider)
ItemUtility getUtility()      // Utility properties (if utility item)
```

### Interactions
```java
Map<InteractionType, String> getInteractions()
Map<String, String> getInteractionVars()
InteractionConfiguration getInteractionConfig()
```

### State Management
```java
String getItemIdForState(String state)
Item getItemForState(String state)
String getStateForItem(Item item)
String getStateForItem(String itemId)
```

### Usage Example
```java
ItemStack stack = inventory.getItemInHand();
if (!stack.isEmpty()) {
    Item item = stack.getItem();

    // Check item type
    if (item.getWeapon() != null) {
        playerRef.sendMessage(Message.raw("Holding a weapon!"));
    }

    // Get max stack size
    int maxStack = item.getMaxStack();

    // Check if placeable
    if (item.hasBlockType()) {
        String blockId = item.getBlockId();
    }
}
```

---

## ItemContainer
**Package:** `com.hypixel.hytale.server.core.inventory.container`

Abstract base class for inventory containers with filtering support.

### Capacity
```java
abstract short getCapacity()
ItemStack getItemStack(short slotIndex)
```

### Filtering
```java
abstract void setGlobalFilter(FilterType filterType)
abstract void setSlotFilter(FilterActionType actionType, short slotIndex, SlotFilter filter)
```

### Adding Items
```java
boolean canAddItemStack(ItemStack item)
boolean canAddItemStack(ItemStack item, boolean addAllOrNothing, boolean fullStacks)

ItemStackTransaction addItemStack(ItemStack item)
ItemStackTransaction addItemStack(ItemStack item, boolean addAllOrNothing, boolean fullStacks, boolean filter)
ItemStackTransaction addItemStacks(List<ItemStack> items)
ItemStackTransaction addItemStacksOrdered(List<ItemStack> items)
ItemStackTransaction addItemStacksOrdered(List<ItemStack> items, short startSlot)
ItemStackTransaction addItemStacksOrdered(List<ItemStack> items, short startSlot, boolean addAllOrNothing, boolean fullStacks)
```

### Removing Items
```java
// By slot
ItemStack removeItemStackFromSlot(short slotIndex)
ItemStackSlotTransaction removeItemStackFromSlot(short slotIndex, int quantity)
ItemStackSlotTransaction removeItemStackFromSlot(short slotIndex, ItemStack item, int quantity)

// By item
ItemStackTransaction removeItemStack(ItemStack item)
ItemStackTransaction removeItemStacks(List<ItemStack> items)
```

### Removing Materials
```java
MaterialSlotTransaction removeMaterialFromSlot(short slotIndex, MaterialQuantity material)
MaterialTransaction removeMaterial(MaterialQuantity material)
MaterialTransaction removeMaterials(List<MaterialQuantity> materials)
```

### Removing Resources
```java
ResourceSlotTransaction removeResourceFromSlot(short slotIndex, ResourceQuantity resource)
ResourceTransaction removeResource(ResourceQuantity resource)
```

### Moving Items
```java
MoveTransaction<ItemStackTransaction> moveItemStackFromSlot(short slotIndex, ItemContainer destination)
MoveTransaction<SlotTransaction> moveItemStackFromSlotToSlot(short slotIndex, int quantity, ItemContainer destination, short destSlot)
ListTransaction<MoveTransaction<ItemStackTransaction>> moveAllItemStacksTo(ItemContainer... destinations)
ListTransaction<MoveTransaction<ItemStackTransaction>> quickStackTo(ItemContainer... destinations)
ListTransaction<MoveTransaction<SlotTransaction>> combineItemStacksIntoSlot(ItemContainer source, short slotIndex)
ListTransaction<MoveTransaction<SlotTransaction>> swapItems(short slot, ItemContainer container, short containerSlot, short targetSlot)
```

### Utility
```java
ClearTransaction clear()
List<ItemStack> removeAllItemStacks()
List<ItemStack> dropAllItemStacks()
boolean isEmpty()
int countItemStacks(Predicate<ItemStack> filter)
boolean containsItemStacksStackableWith(ItemStack item)
void forEach(ShortObjectConsumer<ItemStack> consumer)
ListTransaction<SlotTransaction> sortItems(SortType sortType)
```

### Events
```java
EventRegistration registerChangeEvent(Consumer<ItemContainerChangeEvent> handler)
EventRegistration registerChangeEvent(EventPriority priority, Consumer<ItemContainerChangeEvent> handler)
EventRegistration registerChangeEvent(short slotIndex, Consumer<ItemContainerChangeEvent> handler)
```

---

## ItemContainerChangeEvent

**Package:** `com.hypixel.hytale.server.core.inventory.container`

Java Record that fires when an item container's contents change. Implements `IEvent<Void>`.

### Record Components

| Method | Return Type | Description |
|--------|-------------|-------------|
| `container()` | `ItemContainer` | The container that changed |
| `transaction()` | `Transaction` | The transaction details |

### Usage Example

Register directly on an ItemContainer:

```java
Player player = store.getComponent(ref, Player.getComponentType());
Inventory inventory = player.getInventory();
ItemContainer hotbar = inventory.getHotbar();

// Listen for changes to this specific container
hotbar.registerChangeEvent(event -> {
    ItemContainer container = event.container();
    Transaction transaction = event.transaction();
    System.out.println("Container changed: " + transaction);
});

// Listen for changes to a specific slot
hotbar.registerChangeEvent((short) 0, event -> {
    System.out.println("First hotbar slot changed!");
});

// With priority
hotbar.registerChangeEvent(EventPriority.EARLY, event -> {
    System.out.println("Early handler for container change");
});
```

### Note

This event is specific to individual `ItemContainer` instances. To listen for general inventory changes across all entities, use `LivingEntityInventoryChangeEvent` instead (see [entities.md](entities.md)).

---

## SimpleItemContainer
**Package:** `com.hypixel.hytale.server.core.inventory.container`

Thread-safe concrete implementation of ItemContainer.

```java
// Constructor
SimpleItemContainer(short capacity)

// Static factory
static ItemContainer getNewContainer(short capacity)

// Utility methods with drop support
static boolean addOrDropItemStack(ComponentAccessor<EntityStore> accessor, Ref<EntityStore> ref, ItemContainer container, ItemStack item)
static boolean addOrDropItemStack(ComponentAccessor<EntityStore> accessor, Ref<EntityStore> ref, ItemContainer container, short startSlot, ItemStack item)
static boolean addOrDropItemStacks(ComponentAccessor<EntityStore> accessor, Ref<EntityStore> ref, ItemContainer container, List<ItemStack> items)
static boolean tryAddOrderedOrDropItemStacks(ComponentAccessor<EntityStore> accessor, Ref<EntityStore> ref, ItemContainer container, List<ItemStack> items)
```

---

## CombinedItemContainer
**Package:** `com.hypixel.hytale.server.core.inventory.container`

Combines multiple ItemContainers into a single logical container.

```java
// Constructor
CombinedItemContainer(ItemContainer... containers)

// Access
ItemContainer getContainer(int index)
int getContainersSize()
ItemContainer getContainerForSlot(short slotIndex)
short getCapacity()
CombinedItemContainer clone()
```

---

## SortType
**Package:** `com.hypixel.hytale.server.core.inventory.container`

Enum for sorting options.

```java
public enum SortType {
    NAME,   // Sort alphabetically by item name
    TYPE,   // Sort by item type/category
    RARITY  // Sort by item rarity

    Comparator<ItemStack> getComparator()
}
```

---

## FilterType
**Package:** `com.hypixel.hytale.server.core.inventory.container.filter`

Enum for container-level filtering.

```java
public enum FilterType {
    ALLOW_INPUT_ONLY,   // Only allow items in
    ALLOW_OUTPUT_ONLY,  // Only allow items out
    ALLOW_ALL,          // Allow input and output
    DENY_ALL            // Block all operations

    boolean allowInput()
    boolean allowOutput()
}
```

---

## FilterActionType
**Package:** `com.hypixel.hytale.server.core.inventory.container.filter`

Enum for filter action contexts. Used with `SlotFilter` to determine what operation is being filtered.

```java
public enum FilterActionType {
    ADD,    // Adding item to slot
    REMOVE, // Removing item from slot
    DROP    // Dropping item
}
```

---

## SlotFilter
**Package:** `com.hypixel.hytale.server.core.inventory.container.filter`

Interface for per-slot filtering. Set via `ItemContainer.setSlotFilter()`.

### Constants
```java
static final SlotFilter ALLOW  // Always allow
static final SlotFilter DENY   // Always deny
```

### Methods
```java
// Test if the operation should be allowed
boolean test(FilterActionType actionType, ItemContainer container, short slot, ItemStack item)
```

### Usage Example
```java
ItemContainer hotbar = inventory.getHotbar();

// Deny all operations on slot 0
hotbar.setSlotFilter(FilterActionType.ADD, (short) 0, SlotFilter.DENY);

// Custom filter - only allow weapons in slot 1
hotbar.setSlotFilter(FilterActionType.ADD, (short) 1, (action, container, slot, item) -> {
    return item.getItem().getWeapon() != null;
});
```

---

## MaterialQuantity
**Package:** `com.hypixel.hytale.server.core.inventory`

Represents a quantity of crafting material.

### Fields
```java
String itemId
String resourceTypeId
String tag
int tagIndex
int quantity
BsonDocument metadata
```

### Methods
```java
ItemStack toItemStack()
ResourceQuantity toResource()
MaterialQuantity clone(int newQuantity)
```

---

## ResourceQuantity
**Package:** `com.hypixel.hytale.server.core.inventory`

Represents a quantity of a resource.

### Fields
```java
String resourceId
int quantity
```

### Methods
```java
String getResourceId()
int getQuantity()
ResourceQuantity clone(int newQuantity)
ItemResourceType getResourceType(Item item)
```

---

## Transaction System

All item operations return Transaction objects indicating success/failure.

### ActionType
**Package:** `com.hypixel.hytale.server.core.inventory.transaction`

Enum for transaction operation types. Get via `ItemStackTransaction.getAction()`.

```java
public enum ActionType {
    SET,     // Set slot to specific item
    ADD,     // Add item to container
    REMOVE,  // Remove item from container
    REPLACE  // Replace existing item

    boolean isAdd()
    boolean isRemove()
    boolean isDestroy()
}
```

### ItemStackTransaction
```java
boolean succeeded()
boolean wasSlotModified(short slotIndex)
ActionType getAction()              // Operation type
ItemStack getQuery()                // Original item query
ItemStack getRemainder()            // Leftover items not processed
boolean isAllOrNothing()
boolean isFilter()                  // Whether filtering blocked operation
List<ItemStackSlotTransaction> getSlotTransactions()
```

### ListTransaction<T extends Transaction>
```java
boolean succeeded()
List<T> getList()
int size()
static <T> ListTransaction<T> getEmptyTransaction(boolean success)
```

### MoveTransaction<T extends Transaction>
Wraps source and destination transactions for move operations.

---

## Usage Examples

### Get Player Inventory
```java
Player player = store.getComponent(ref, Player.getComponentType());
Inventory inventory = player.getInventory();
```

### Check Held Item
```java
ItemStack heldItem = inventory.getItemInHand();
if (!heldItem.isEmpty()) {
    String itemId = heldItem.getItemId();
    int count = heldItem.getQuantity();
    playerRef.sendMessage(Message.raw("Holding: " + itemId + " x" + count));
}
```

### Add Item to Inventory
```java
ItemStack newItem = new ItemStack("my_item", 10);
ItemContainer hotbar = inventory.getHotbar();
ItemStackTransaction result = hotbar.addItemStack(newItem);
if (result.succeeded()) {
    playerRef.sendMessage(Message.raw("Item added!"));
} else {
    ItemStack remainder = result.getRemainder();
    // Handle overflow
}
```

### Move Items Between Containers
```java
ItemContainer hotbar = inventory.getHotbar();
ItemContainer storage = inventory.getStorage();

// Move all from hotbar to storage
ListTransaction<MoveTransaction<ItemStackTransaction>> result =
    hotbar.moveAllItemStacksTo(storage);

if (result.succeeded()) {
    playerRef.sendMessage(Message.raw("Items moved to storage"));
}
```

### Listen for Inventory Changes
```java
ItemContainer hotbar = inventory.getHotbar();
hotbar.registerChangeEvent(event -> {
    playerRef.sendMessage(Message.raw("Hotbar changed!"));
});
```

### Sort Storage
```java
inventory.sortStorage(SortType.TYPE);
```

### Create ItemStack with Metadata
```java
ItemStack item = new ItemStack("magic_sword", 1)
    .withDurability(100.0)
    .withMaxDurability(100.0)
    .withMetadata("enchantment", someCodec, enchantmentData);
```

### Access via LivingEntity
```java
LivingEntity entity = ...;
Inventory inv = entity.getInventory();
entity.setInventory(newInventory);
entity.setInventory(newInventory, true);  // with notification
```

---

## Crafting System

Types related to crafting recipes and requirements.

---

## CraftingRecipe
**Package:** `com.hypixel.hytale.server.core.asset.type.item.config`

Asset type for crafting recipes. Returned by `CraftRecipeEvent.getCraftedRecipe()`.

### Constants
```java
static final String FIELDCRAFT_REQUIREMENT  // Fieldcraft bench requirement ID
```

### Static Methods
```java
static AssetStore<String, CraftingRecipe, ...> getAssetStore()
static AssetMap<String, CraftingRecipe> getAssetMap()
static String generateIdFromItemRecipe(Item item, int index)  // Generate recipe ID from item
```

### Identity
```java
String getId()  // Unique recipe identifier
```

### Inputs & Outputs
```java
MaterialQuantity[] getInput()         // Required materials
MaterialQuantity[] getOutputs()       // All output items
MaterialQuantity getPrimaryOutput()   // Main crafted item
```

### Requirements
```java
BenchRequirement[] getBenchRequirement()           // Required crafting benches
boolean isRestrictedByBenchTierLevel(String benchId, int tierLevel)  // Check tier restriction
float getTimeSeconds()                             // Crafting time
boolean isKnowledgeRequired()                      // Whether recipe must be learned
int getRequiredMemoriesLevel()                     // Required memories level
```

### Network
```java
CraftingRecipe toPacket(String id)  // Convert to network packet format
```

### Usage Example
```java
// In a CraftRecipeEvent handler
public void handle(..., CraftRecipeEvent.Pre event) {
    CraftingRecipe recipe = event.getCraftedRecipe();

    // Get what's being crafted
    MaterialQuantity output = recipe.getPrimaryOutput();
    int quantity = event.getQuantity();

    // Check crafting time
    float seconds = recipe.getTimeSeconds();

    // Check required materials
    MaterialQuantity[] inputs = recipe.getInput();
    for (MaterialQuantity input : inputs) {
        String itemId = input.getItemId();
        int needed = input.getQuantity();
    }

    // Check bench requirements
    BenchRequirement[] benches = recipe.getBenchRequirement();
    for (BenchRequirement bench : benches) {
        if (bench.type == BenchType.Crafting) {
            // Standard crafting bench required
        }
    }
}
```

---

## BenchRequirement
**Package:** `com.hypixel.hytale.protocol`

Specifies crafting bench requirements for a recipe.

### Fields
```java
BenchType type           // Type of bench required
String id                // Specific bench ID (optional)
String[] categories      // Bench categories (optional)
int requiredTierLevel    // Minimum bench tier level
```

### Usage
```java
BenchRequirement[] benches = recipe.getBenchRequirement();
for (BenchRequirement bench : benches) {
    BenchType type = bench.type;
    int tierLevel = bench.requiredTierLevel;

    if (type == BenchType.Processing) {
        // Requires a processing station
    }
}
```

---

## BenchType
**Package:** `com.hypixel.hytale.protocol`

Enum for types of crafting benches.

```java
public enum BenchType {
    Crafting,           // Standard crafting table
    Processing,         // Processing station (smelting, etc.)
    DiagramCrafting,    // Blueprint-based crafting
    StructuralCrafting  // Building/structural crafting

    int getValue()                        // Get numeric value
    static BenchType fromValue(int value) // Get from numeric value
}
```

---

## Crafting Events

Events related to crafting and recipes.

### CraftRecipeEvent

**Package:** `com.hypixel.hytale.server.core.event.events.ecs`

Abstract base ECS event for crafting. Extends `CancellableEcsEvent`. Has two concrete variants for pre/post crafting.

#### Base Methods

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getCraftedRecipe()` | `CraftingRecipe` | The recipe being crafted |
| `getQuantity()` | `int` | Number of items being crafted |

### CraftRecipeEvent.Pre

Fired before crafting completes. Cancellable.

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getCraftedRecipe()` | `CraftingRecipe` | The recipe being crafted |
| `getQuantity()` | `int` | Number of items being crafted |
| `isCancelled()` | `boolean` | Whether the event is cancelled |
| `setCancelled(boolean)` | `void` | Cancel the crafting |

### CraftRecipeEvent.Post

Fired after crafting completes.

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getCraftedRecipe()` | `CraftingRecipe` | The recipe that was crafted |
| `getQuantity()` | `int` | Number of items crafted |

### Crafting Event Example

```java
import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.ecs.CraftRecipeEvent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public class CraftingSystem extends EntityEventSystem<EntityStore, CraftRecipeEvent.Pre> {

    public CraftingSystem() {
        super(CraftRecipeEvent.Pre.class);
    }

    @Override
    public void handle(int index, ArchetypeChunk<EntityStore> chunk,
                       Store<EntityStore> store, CommandBuffer<EntityStore> buffer,
                       CraftRecipeEvent.Pre event) {
        Player player = chunk.getComponent(index, Player.getComponentType());
        if (player != null) {
            var recipe = event.getCraftedRecipe();
            int quantity = event.getQuantity();
            player.sendMessage(Message.raw("Crafting " + quantity + " items..."));

            // Optionally cancel the craft
            // event.setCancelled(true);
        }
    }

    @Override
    public Query<EntityStore> getQuery() {
        return Player.getComponentType();
    }
}
```

### Crafting Event Registration

```java
@Override
protected void setup() {
    // Listen for pre-craft (can cancel)
    getEntityStoreRegistry().registerSystem(new CraftingSystem());

    // Or listen for post-craft (after completion)
    getEntityStoreRegistry().registerSystem(
        new EntityEventSystem<EntityStore, CraftRecipeEvent.Post>(CraftRecipeEvent.Post.class) {
            @Override
            public void handle(int index, ArchetypeChunk<EntityStore> chunk,
                               Store<EntityStore> store, CommandBuffer<EntityStore> buffer,
                               CraftRecipeEvent.Post event) {
                // Handle post-craft
            }

            @Override
            public Query<EntityStore> getQuery() {
                return Player.getComponentType();
            }
        }
    );
}
```

---

## Inventory Events

Events related to inventory operations (dropping items, switching slots, picking up items, inventory changes).

### Event Summary

**Package:** `com.hypixel.hytale.server.core.event.events.entity`

| Class | Description |
|-------|-------------|
| `LivingEntityInventoryChangeEvent` | Living entity inventory changes |

**Package:** `com.hypixel.hytale.server.core.event.events.ecs`

| Class | Description |
|-------|-------------|
| `DropItemEvent` | Item is dropped (has `Drop` and `PlayerRequest` variants) |
| `InteractivelyPickupItemEvent` | Item is picked up interactively |
| `SwitchActiveSlotEvent` | Active inventory slot changes |

---

### LivingEntityInventoryChangeEvent

**Package:** `com.hypixel.hytale.server.core.event.events.entity`

Fired when a living entity's inventory changes.

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getEntity()` | `LivingEntity` | The entity whose inventory changed |
| `getInventory()` | `Inventory` | The updated inventory |

### Usage Example

```java
import com.hypixel.hytale.server.core.event.events.entity.LivingEntityInventoryChangeEvent;

@Override
protected void setup() {
    getEventRegistry().registerGlobal(LivingEntityInventoryChangeEvent.class, event -> {
        var entity = event.getEntity();
        System.out.println("Inventory changed for: " + entity);
    });
}
```

---

### DropItemEvent

**Package:** `com.hypixel.hytale.server.core.event.events.ecs`

ECS event fired when an item is dropped. Has variants:
- `DropItemEvent.Drop` - General item drop
- `DropItemEvent.PlayerRequest` - Player-initiated drop

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getItemStack()` | `ItemStack` | The item being dropped |
| `getPosition()` | `Vector3d` | Drop position |

---

### InteractivelyPickupItemEvent

**Package:** `com.hypixel.hytale.server.core.event.events.ecs`

ECS event fired when an item is picked up interactively (e.g., player collecting items).

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getItemStack()` | `ItemStack` | The item being picked up |

---

### SwitchActiveSlotEvent

**Package:** `com.hypixel.hytale.server.core.event.events.ecs`

ECS event fired when the active inventory slot changes (e.g., player switching hotbar slot).

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getPreviousSlot()` | `int` | The previous active slot |
| `getNewSlot()` | `int` | The new active slot |

### ECS Inventory Event Example

For ECS events, use an `EntityEventSystem`:

```java
import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.ecs.SwitchActiveSlotEvent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public class SlotSwitchSystem extends EntityEventSystem<EntityStore, SwitchActiveSlotEvent> {

    public SlotSwitchSystem() {
        super(SwitchActiveSlotEvent.class);
    }

    @Override
    public void handle(int index, ArchetypeChunk<EntityStore> chunk,
                       Store<EntityStore> store, CommandBuffer<EntityStore> buffer,
                       SwitchActiveSlotEvent event) {
        Player player = chunk.getComponent(index, Player.getComponentType());
        if (player != null) {
            player.sendMessage(Message.raw(
                "Switched from slot " + event.getPreviousSlot() + " to " + event.getNewSlot()
            ));
        }
    }

    @Override
    public Query<EntityStore> getQuery() {
        return Player.getComponentType();
    }
}
```

### Registration

```java
@Override
protected void setup() {
    getEntityStoreRegistry().registerSystem(new SlotSwitchSystem());
}
```
