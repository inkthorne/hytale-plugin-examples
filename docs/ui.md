# UI API

## Overview
**Package:** `com.hypixel.hytale.server.core.ui`

Hytale provides UI building APIs for creating custom interfaces. UI is managed through XML-based definitions sent to clients via command builders.

---

## Positioning Classes

### Anchor
**Package:** `com.hypixel.hytale.server.core.ui`

UI element anchoring for positioning.

```java
// Setters (fluent API, return Anchor)
Anchor left(Value<Integer> value)
Anchor right(Value<Integer> value)
Anchor top(Value<Integer> value)
Anchor bottom(Value<Integer> value)
Anchor height(Value<Integer> value)
Anchor width(Value<Integer> value)
Anchor minWidth(Value<Integer> value)
Anchor maxWidth(Value<Integer> value)

// Presets
Anchor full()        // Fill parent completely
Anchor horizontal()  // Fill horizontally
Anchor vertical()    // Fill vertically

// Codec for serialization
static final Codec<Anchor> CODEC
```

### Area
**Package:** `com.hypixel.hytale.server.core.ui`

UI area/bounds definition.

```java
Area setX(int x)
Area setY(int y)
Area setWidth(int width)
Area setHeight(int height)

int getX()
int getY()
int getWidth()
int getHeight()
```

### Value<T>
**Package:** `com.hypixel.hytale.server.core.ui`

Generic value container supporting literal values or document references for dynamic binding.

```java
// Get value
T getValue()
String getDocumentPath()
String getValueName()

// Static factories
static <T> Value<T> ref(String documentPath, String valueName)  // Reference to UI document value
static <T> Value<T> of(T literal)                                // Literal value
```

---

## UI Command Builder

### UICommandBuilder
**Package:** `com.hypixel.hytale.server.core.ui`

Build UI commands for updating client UI elements.

#### Element Operations
```java
UICommandBuilder clear(String elementId)            // Clear element contents
UICommandBuilder remove(String elementId)           // Remove element from DOM
```

#### Append Operations
```java
UICommandBuilder append(String uiFilePath)                            // Append .ui file to root
UICommandBuilder append(String elementSelector, String uiFilePath)    // Append .ui file to element
UICommandBuilder appendInline(String elementSelector, String dslMarkup)  // Append inline DSL markup
```

**File paths** are relative to `resources/Common/UI/Custom/`. Examples:
- `append("MyUI.ui")` loads `Common/UI/Custom/MyUI.ui`
- `append("Pages/MyPage.ui")` loads `Common/UI/Custom/Pages/MyPage.ui`
- `append("#container", "Components/Button.ui")` appends to element with id "container"

**Inline DSL markup** uses Hytale's curly-brace format (NOT XML):
```java
appendInline("#container", "Label { Text: \"Hello\"; Style: (Alignment: Center); }");
```

**Note:** The selector for `appendInline()` must reference an existing element in the UI. Load a base `.ui` file first with `append()`, then use `appendInline()` to add content to named containers within it.

#### Insert Operations
```java
UICommandBuilder insertBefore(String elementSelector, String uiFilePath)
UICommandBuilder insertBeforeInline(String elementSelector, String dslMarkup)
```

#### Set Property Operations
```java
// By Value<T>
<T> UICommandBuilder set(String path, Value<T> value)

// By type
UICommandBuilder set(String path, String value)
UICommandBuilder set(String path, Message message)
UICommandBuilder set(String path, boolean value)
UICommandBuilder set(String path, float value)
UICommandBuilder set(String path, int value)
UICommandBuilder set(String path, double value)

// Object and collections
UICommandBuilder setObject(String path, Object value)
<T> UICommandBuilder set(String path, T[] array)
<T> UICommandBuilder set(String path, List<T> list)
UICommandBuilder setNull(String path)
```

#### Get Commands
```java
CustomUICommand[] getCommands()
```

---

## UI Event Builder

### UIEventBuilder
**Package:** `com.hypixel.hytale.server.core.ui`

Build UI event handlers for client interaction.

```java
// Add event bindings
UIEventBuilder addEventBinding(CustomUIEventBindingType type, String elementId)
UIEventBuilder addEventBinding(CustomUIEventBindingType type, String elementId, boolean capture)
UIEventBuilder addEventBinding(CustomUIEventBindingType type, String elementId, EventData data)
UIEventBuilder addEventBinding(CustomUIEventBindingType type, String elementId, EventData data, boolean capture)

// Get events
CustomUIEventBinding[] getEvents()
```

### EventData
**Package:** `com.hypixel.hytale.server.core.ui`

Container for UI event data passed to handlers.

---

## CustomUIEventBindingType
**Package:** `com.hypixel.hytale.protocol.packets.interface_`

Enum defining UI event types for event bindings.

### Values

| Value | Description |
|-------|-------------|
| `Activating` | Element activated (clicked/pressed) |
| `RightClicking` | Right mouse button clicked |
| `DoubleClicking` | Double click |
| `MouseEntered` | Mouse entered element bounds |
| `MouseExited` | Mouse exited element bounds |
| `ValueChanged` | Input value changed |
| `ElementReordered` | Element order changed (drag/drop) |
| `Validating` | Input validation triggered |
| `Dismissing` | Element being dismissed |
| `FocusGained` | Element gained focus |
| `FocusLost` | Element lost focus |
| `KeyDown` | Key pressed while focused |
| `MouseButtonReleased` | Mouse button released |
| `SlotClicking` | Inventory slot clicked |
| `SlotDoubleClicking` | Inventory slot double-clicked |
| `SlotMouseEntered` | Mouse entered slot |
| `SlotMouseExited` | Mouse exited slot |
| `DragCancelled` | Drag operation cancelled |
| `Dropped` | Item dropped |
| `SlotMouseDragCompleted` | Slot drag completed |
| `SlotMouseDragExited` | Mouse exited during slot drag |
| `SlotClickReleaseWhileDragging` | Click released while dragging |
| `SlotClickPressWhileDragging` | Click pressed while dragging |
| `SelectedTabChanged` | Tab selection changed |

### Methods
```java
static CustomUIEventBindingType[] values()
static CustomUIEventBindingType valueOf(String name)
int getValue()
static CustomUIEventBindingType fromValue(int value)
```

### Usage Example
```java
UIEventBuilder events = new UIEventBuilder();
events.addEventBinding(CustomUIEventBindingType.Activating, "submit-button");
events.addEventBinding(CustomUIEventBindingType.ValueChanged, "search-input");
events.addEventBinding(CustomUIEventBindingType.SlotClicking, "inventory-slot");
```

---

## Player UI Managers

Access via `Player`:

```java
Player player = store.getComponent(ref, Player.getComponentType());
WindowManager windows = player.getWindowManager();
PageManager pages = player.getPageManager();
HudManager hud = player.getHudManager();
HotbarManager hotbar = player.getHotbarManager();
```

---

### WindowManager
**Package:** `com.hypixel.hytale.server.core.entity.entities.player.windows`

Manage player windows (inventory, crafting, etc.).

```java
// Open windows
OpenWindow openWindow(Window window)
List<OpenWindow> openWindows(Window... windows)

// Set/Get by slot
void setWindow(int slotId, Window window)
Window getWindow(int slotId)
List<Window> getWindows()

// Update
void updateWindow(Window window)
void updateWindows()
void markWindowChanged(int slotId)

// Close
Window closeWindow(int slotId)
void closeAllWindows()

// Validation
void validateWindows()
```

---

### Window
**Package:** `com.hypixel.hytale.server.core.entity.entities.player.windows`

Abstract base class for player windows (inventory, crafting, container UIs).

```java
// Constructor
Window(WindowType type)

// Initialization (called by framework)
void init(PlayerRef playerRef, WindowManager windowManager)

// Abstract - must implement
abstract JsonObject getData()  // Window data for client

// Event handling
void handleAction(Ref<EntityStore> ref, Store<EntityStore> store, WindowAction action)

// Properties
WindowType getType()
int getId()
void setId(int id)
PlayerRef getPlayerRef()

// Lifecycle
void close()

// Close event registration
EventRegistration registerCloseEvent(Consumer<Window.WindowCloseEvent> handler)
EventRegistration registerCloseEvent(short priority, Consumer<Window.WindowCloseEvent> handler)
EventRegistration registerCloseEvent(EventPriority priority, Consumer<Window.WindowCloseEvent> handler)
```

#### Subclasses
| Class | Description |
|-------|-------------|
| `ContainerWindow` | Container-based windows |
| `ItemContainerWindow` | Item container windows |
| `BlockWindow` | Block interaction windows |
| `ValidatedWindow` | Windows with validation |

---

### WindowType
**Package:** `com.hypixel.hytale.protocol.packets.window`

Enum defining window types.

| Value | Description |
|-------|-------------|
| `Container` | Generic container window |
| `PocketCrafting` | Quick/pocket crafting |
| `BasicCrafting` | Standard crafting table |
| `DiagramCrafting` | Blueprint-based crafting |
| `StructuralCrafting` | Building/structural crafting |
| `Processing` | Processing bench (smelting, etc.) |
| `Memories` | Memories/journal window |

#### Methods
```java
static WindowType[] values()
static WindowType valueOf(String name)
int getValue()
static WindowType fromValue(int value)
```

---

### OpenWindow
**Package:** `com.hypixel.hytale.protocol.packets.window`

Protocol packet representing an opened window. Returned by `WindowManager.openWindow()`.

```java
// Fields
int id                           // Window slot ID
WindowType windowType            // Type of window
String windowData                // JSON data for client
InventorySection inventory       // Associated inventory
ExtraResources extraResources    // Additional resources

// Methods
int getId()
```

#### Usage Example
```java
WindowManager windows = player.getWindowManager();

// Open a window and get reference
Window myWindow = new MyCustomWindow(WindowType.Container);
OpenWindow opened = windows.openWindow(myWindow);

int slotId = opened.getId();

// Later, close by slot ID
windows.closeWindow(slotId);
```

---

### PageManager
**Package:** `com.hypixel.hytale.server.core.entity.entities.player.pages`

Manage player pages (full-screen UIs).

```java
// Initialization
void init(PlayerRef playerRef, WindowManager windowManager)

// Custom page access
CustomUIPage getCustomPage()

// Set pages
void setPage(Ref<EntityStore> ref, Store<EntityStore> store, Page page)
void setPage(Ref<EntityStore> ref, Store<EntityStore> store, Page page, boolean animate)

// Open custom pages
void openCustomPage(Ref<EntityStore> ref, Store<EntityStore> store, CustomUIPage page)

// With windows
boolean setPageWithWindows(Ref<EntityStore> ref, Store<EntityStore> store, Page page, boolean animate, Window... windows)
boolean openCustomPageWithWindows(Ref<EntityStore> ref, Store<EntityStore> store, CustomUIPage page, Window... windows)

// Update
void updateCustomPage(CustomPage page)

// Event handling
void handleEvent(Ref<EntityStore> ref, Store<EntityStore> store, CustomPageEvent event)
```

---

### Page
**Package:** `com.hypixel.hytale.protocol.packets.interface_`

Enum defining built-in page types.

| Value | Description |
|-------|-------------|
| `None` | No page (close current page) |
| `Bench` | Crafting bench page |
| `Inventory` | Player inventory page |
| `ToolsSettings` | Tools/settings page |
| `Map` | World map page |
| `MachinimaEditor` | Machinima editor page |
| `ContentCreation` | Content creation tools |
| `Custom` | Custom plugin-defined page |

#### Methods
```java
static Page[] values()
static Page valueOf(String name)
int getValue()
static Page fromValue(int value)
```

#### Usage Example
```java
PageManager pages = player.getPageManager();

// Open inventory page
pages.setPage(ref, store, Page.Inventory);

// Open with animation
pages.setPage(ref, store, Page.Map, true);

// Close current page
pages.setPage(ref, store, Page.None);
```

---

### CustomUIPage
**Package:** `com.hypixel.hytale.server.core.entity.entities.player.pages`

Abstract base class for custom plugin-defined pages.

```java
// Constructor
CustomUIPage(PlayerRef playerRef, CustomPageLifetime lifetime)

// Lifetime management
void setLifetime(CustomPageLifetime lifetime)
CustomPageLifetime getLifetime()

// Abstract - must implement
abstract void build(Ref<EntityStore> ref, UICommandBuilder cmdBuilder,
                    UIEventBuilder eventBuilder, Store<EntityStore> store)

// Event handling
void handleDataEvent(Ref<EntityStore> ref, Store<EntityStore> store, String data)

// Lifecycle
void onDismiss(Ref<EntityStore> ref, Store<EntityStore> store)
```

#### Subclasses
| Class | Description |
|-------|-------------|
| `BasicCustomUIPage` | Basic custom page - use this for display-only pages |
| `InteractiveCustomUIPage<T>` | Interactive page with typed event handling |

**Important:** Use `BasicCustomUIPage` (not `CustomUIPage` directly) for most custom pages.

#### Usage Example
```java
public class MyCustomPage extends BasicCustomUIPage {
    public MyCustomPage(PlayerRef playerRef) {
        super(playerRef, CustomPageLifetime.CanDismiss);
    }

    @Override
    public void build(UICommandBuilder cmd) {
        // Load UI from .ui file (path relative to Common/UI/Custom/)
        cmd.append("MyCustomPage.ui");
    }

    @Override
    public void onDismiss(Ref<EntityStore> ref, Store<EntityStore> store) {
        // Cleanup when page closes
    }
}

// Open the custom page
Player player = store.getComponent(ref, Player.getComponentType());
PageManager pages = player.getPageManager();
pages.openCustomPage(ref, store, new MyCustomPage(playerRef));
```

#### .ui File Requirements

1. **Location:** `resources/Common/UI/Custom/MyCustomPage.ui`
2. **Manifest:** Must include `"IncludesAssetPack": true`
3. **Structure:** Must have an anonymous root Group:

```
Group {
    LayoutMode: Center;

    Group #MyPanel {
        Anchor: (Width: 300, Height: 100);
        Background: #000000(0.8);

        Label #MyLabel {
            Style: (FontSize: 24, TextColor: #00ff00, Alignment: Center);
            Text: "Hello World";
        }
    }
}
```

**Note:** The root `Group` must NOT have an ID. Named elements go inside it.

#### LayoutMode Values

| Value | Description |
|-------|-------------|
| `Top` | Stack children from top downward |
| `Left` | Stack children from left to right |
| `Middle` | Vertically center children |
| `Center` | Horizontally center children |
| `Bottom` | Stack children from bottom upward |
| `Right` | Stack children from right to left |
| `TopScrolling` | Top layout with vertical scrolling |
| `Full` | Fill entire parent area |
| `CenterMiddle` | Center both horizontally and vertically |
| `MiddleCenter` | Center both horizontally and vertically (alternate) |
| `LeftCenterWrap` | Left-aligned with center wrapping |

**Note:** `Top` and `Left` are the most commonly used values for stacking layouts.

---

### HudManager
**Package:** `com.hypixel.hytale.server.core.entity.entities.player.hud`

Manage HUD visibility and custom HUD elements.

```java
// Custom HUD
CustomUIHud getCustomHud()

// Visibility control
Set<HudComponent> getVisibleHudComponents()
void setVisibleHudComponents(PlayerRef playerRef, HudComponent... components)
void setVisibleHudComponents(PlayerRef playerRef, Set<HudComponent> components)

// Show/Hide specific components
void showHudComponents(PlayerRef playerRef, HudComponent... components)
void showHudComponents(PlayerRef playerRef, Set<HudComponent> components)
void hideHudComponents(PlayerRef playerRef, HudComponent... components)

// Custom HUD
void setCustomHud(PlayerRef playerRef, CustomUIHud hud)

// Reset
void resetHud(PlayerRef playerRef)
void resetUserInterface(PlayerRef playerRef)

// Network
void sendVisibleHudComponents(PacketHandler handler)
```

---

### HudComponent
**Package:** `com.hypixel.hytale.protocol.packets.interface_`

Enum defining individual HUD components that can be shown or hidden.

#### Values

| Value | Description |
|-------|-------------|
| `Hotbar` | Item hotbar at bottom of screen |
| `StatusIcons` | Status effect icons |
| `Reticle` | Crosshair/targeting reticle |
| `Chat` | Chat window |
| `Requests` | Friend/party requests |
| `Notifications` | System notifications |
| `KillFeed` | Combat kill feed |
| `InputBindings` | Key binding hints |
| `PlayerList` | Online player list (Tab) |
| `EventTitle` | Event title display |
| `Compass` | Navigation compass |
| `ObjectivePanel` | Quest/objective panel |
| `PortalPanel` | Portal information |
| `BuilderToolsLegend` | Builder mode tools legend |
| `Speedometer` | Vehicle speed display |
| `UtilitySlotSelector` | Utility item selector |
| `BlockVariantSelector` | Block variant picker |
| `BuilderToolsMaterialSlotSelector` | Builder material selector |
| `Stamina` | Stamina bar |
| `AmmoIndicator` | Ammunition counter |
| `Health` | Health bar |
| `Mana` | Mana bar |
| `Oxygen` | Oxygen/breath bar |
| `Sleep` | Sleep indicator |

#### Methods
```java
static HudComponent[] values()
static HudComponent valueOf(String name)
int getValue()
static HudComponent fromValue(int value)
```

#### Usage Example
```java
HudManager hud = player.getHudManager();

// Show only essential HUD elements
hud.setVisibleHudComponents(playerRef,
    HudComponent.Hotbar,
    HudComponent.Health,
    HudComponent.Reticle);

// Hide chat temporarily
hud.hideHudComponents(playerRef, HudComponent.Chat);

// Show stamina bar
hud.showHudComponents(playerRef, HudComponent.Stamina);
```

---

### HotbarManager
**Package:** `com.hypixel.hytale.server.core.entity.entities.player`

Manage player hotbar configurations.

```java
// Constants
static final int HOTBARS_MAX

// Save/Load hotbar configurations
void saveHotbar(Ref<EntityStore> ref, short index, ComponentAccessor<EntityStore> accessor)
void loadHotbar(Ref<EntityStore> ref, short index, ComponentAccessor<EntityStore> accessor)

// State
int getCurrentHotbarIndex()
boolean getIsCurrentlyLoadingHotbar()
```

---

## File Browser System

### FileBrowserConfig
**Package:** `com.hypixel.hytale.server.core.ui`

Configuration record for file browser UI.

```java
// Builder pattern
static FileBrowserConfig.Builder builder()

// Accessor methods
String listElementId()
String rootSelectorId()
String searchInputId()
String currentPathId()
List<RootEntry> roots()
Set<String> allowedExtensions()
boolean enableRootSelector()
boolean enableSearch()
boolean enableDirectoryNav()
boolean enableMultiSelect()
int maxResults()
FileListProvider customProvider()
```

### FileBrowserConfig.Builder

```java
Builder listElementId(String id)
Builder rootSelectorId(String id)
Builder searchInputId(String id)
Builder currentPathId(String id)
Builder roots(List<RootEntry> roots)
Builder allowedExtensions(Set<String> extensions)
Builder enableRootSelector(boolean enable)
Builder enableSearch(boolean enable)
Builder enableDirectoryNav(boolean enable)
Builder enableMultiSelect(boolean enable)
Builder maxResults(int max)
Builder customProvider(FileListProvider provider)
FileBrowserConfig build()
```

---

### ServerFileBrowser
**Package:** `com.hypixel.hytale.server.core.ui`

Server-side file browser implementation.

#### Constructors
```java
ServerFileBrowser(FileBrowserConfig config)
ServerFileBrowser(FileBrowserConfig config, Path rootPath, Path currentPath)
```

#### Build UI
```java
void buildRootSelector(UICommandBuilder cmdBuilder, UIEventBuilder eventBuilder)
void buildSearchInput(UICommandBuilder cmdBuilder, UIEventBuilder eventBuilder)
void buildCurrentPath(UICommandBuilder cmdBuilder)
void buildFileList(UICommandBuilder cmdBuilder, UIEventBuilder eventBuilder)
void buildUI(UICommandBuilder cmdBuilder, UIEventBuilder eventBuilder)
```

#### Event Handling
```java
boolean handleEvent(FileBrowserEventData eventData)
```

#### Navigation
```java
Path getRoot()
void setRoot(Path path)
Path getCurrentDir()
void setCurrentDir(Path path)
String getSearchQuery()
void setSearchQuery(String query)
void navigateUp()
void navigateTo(Path path)
```

#### Selection
```java
Set<String> getSelectedItems()
void addSelection(String item)
void clearSelection()
```

#### Utility
```java
FileBrowserConfig getConfig()
Path resolveSecure(String path)      // Secure path resolution
Path resolveFromCurrent(String path)
```

---

## Supporting Classes

### LocalizableString
Translatable UI strings with parameter support.

### DropdownEntryInfo
Dropdown menu entry configuration.

### ItemGridSlot
Inventory grid slot definition for UI display.

### PatchStyle
UI styling/theming support.

---

## Usage Examples

### Create and Update Custom UI
```java
@Override
protected void execute(CommandContext ctx, Store<EntityStore> store,
                      Ref<EntityStore> ref, PlayerRef playerRef, World world) {
    Player player = store.getComponent(ref, Player.getComponentType());

    // Build UI commands
    UICommandBuilder builder = new UICommandBuilder();
    builder.set("score.value", 100)
           .set("player.name", playerRef.getUsername())
           .append("MyUI.ui");  // Load .ui file from Common/UI/Custom/

    // Build event handlers
    UIEventBuilder events = new UIEventBuilder();
    events.addEventBinding(CustomUIEventBindingType.Activating, "button");

    // Send to client via managers
    // Implementation depends on specific UI type (Page, Window, HUD)
}
```

### Show/Hide HUD Components
```java
Player player = store.getComponent(ref, Player.getComponentType());
HudManager hud = player.getHudManager();

// Hide all HUD
hud.setVisibleHudComponents(playerRef);

// Show specific components
hud.showHudComponents(playerRef, HudComponent.HEALTH, HudComponent.HOTBAR);
```

### Open Custom Page
```java
Player player = store.getComponent(ref, Player.getComponentType());
PageManager pages = player.getPageManager();

CustomUIPage customPage = ...; // Create custom page
pages.openCustomPage(ref, store, customPage);
```

### Window Management
```java
Player player = store.getComponent(ref, Player.getComponentType());
WindowManager windows = player.getWindowManager();

Window inventoryWindow = ...; // Create window
OpenWindow opened = windows.openWindow(inventoryWindow);

// Later, close it
windows.closeWindow(opened.getSlotId());
```

### File Browser Setup
```java
FileBrowserConfig config = FileBrowserConfig.builder()
    .listElementId("file-list")
    .enableSearch(true)
    .enableDirectoryNav(true)
    .allowedExtensions(Set.of("json", "txt"))
    .maxResults(50)
    .build();

ServerFileBrowser browser = new ServerFileBrowser(config);

UICommandBuilder cmd = new UICommandBuilder();
UIEventBuilder events = new UIEventBuilder();
browser.buildUI(cmd, events);
```

---

## UI Events

### WindowCloseEvent

**Package:** `com.hypixel.hytale.server.core.entity.entities.player.windows`

Event fired when a player window is closed. Implements `IEvent<Void>`.

This is a minimal event that signals window closure. It does not provide additional methods beyond the basic event interface.

### Usage Example

```java
import com.hypixel.hytale.server.core.entity.entities.player.windows.WindowCloseEvent;

@Override
protected void setup() {
    getEventRegistry().register(WindowCloseEvent.class, event -> {
        System.out.println("A window was closed");
    });
}
```

---

## Notes
- UI systems are XML-based; understand the client protocol for full usage
- Window/Page/HUD managers handle network synchronization automatically
- Use `Value.ref()` for dynamic bindings that update when document values change
- File browser provides secure path resolution to prevent directory traversal
