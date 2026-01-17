# UI Example Plugin

Demonstrates the Hytale UI system with custom pages and HUD management.

## Commands

### `/menu`
Opens a custom UI page with clickable options.

The page demonstrates:
- Loading a `.ui` definition file
- Handling button click events via `SendData`
- Page dismiss handling

### `/hud <show|hide>`
Toggles HUD components visibility.

**Examples:**
- `/hud show` - Shows hotbar, health, and reticle
- `/hud hide` - Hides all HUD components

## Building

```batch
build.bat
```

Or:

```batch
gradlew build
```

## Installation

Copy `build/libs/ui-example-1.0.0.jar` to:
```
%APPDATA%\Hytale\UserData\Mods\
```

**Important:** The manifest.json must have `"IncludesAssetPack": true` for the `.ui` file to load.

## Code Structure

- `UIPlugin.java` - Main plugin class, registers commands
- `MenuCommand.java` - Opens the custom menu page
- `HudCommand.java` - Toggles HUD visibility
- `pages/SimpleMenuPage.java` - Custom page implementation
- `Common/UI/Custom/SimpleMenuPage.ui` - UI definition file (DSL format)

## UI File Format

The `SimpleMenuPage.ui` uses Hytale's curly-brace DSL format:

```
Group {
    LayoutMode: Center;

    Group #MenuContainer {
        Anchor: (Width: 400, Height: 300);

        Button #MyButton {
            Text: "Click Me";
            OnActivating: { SendData: "button_clicked"; }
        }
    }
}
```

**Note:** The root `Group` must NOT have an ID. Named elements go inside it.

## Key API Patterns

### Custom Page
```java
public class SimpleMenuPage extends BasicCustomUIPage {
    public SimpleMenuPage(PlayerRef playerRef) {
        super(playerRef, CustomPageLifetime.CanDismiss);
    }

    @Override
    public void build(UICommandBuilder cmd) {
        cmd.append("SimpleMenuPage.ui");
    }

    @Override
    public void handleDataEvent(Ref<EntityStore> ref, Store<EntityStore> store, String data) {
        // Handle events from UI
    }
}
```

### Opening a Page
```java
Player player = store.getComponent(ref, Player.getComponentType());
player.getPageManager().openCustomPage(ref, store, new MyPage(playerRef));
```

### HUD Control
```java
Player player = store.getComponent(ref, Player.getComponentType());
player.getHudManager().setVisibleHudComponents(playerRef,
    HudComponent.Hotbar,
    HudComponent.Health);
```
