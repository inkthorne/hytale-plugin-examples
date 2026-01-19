# UI Elements Reference

Comprehensive reference for all UI element types in Hytale's DSL.

---

## Quick Navigation

| Category | Elements |
|----------|----------|
| [Container](#container-elements) | Group |
| [Text](#text-elements) | Label, TimerLabel |
| [Button](#button-elements) | Button, TextButton, BackButton |
| [Input](#input-elements) | TextField, CompactTextField, NumberField, CheckBox, Slider, DropdownBox |
| [Display](#display-elements) | Sprite, AssetImage, ItemSlot, ProgressBar, CircularProgressBar |

**Related:** [Styling & Layout](ui-styling.md) | [Templates & Variables](ui-templates.md) | [Java API](ui-api.md) | [UI Overview](ui.md)

---

## Container Elements

### Group

Basic container for grouping and laying out child elements.

| Property | Type | Description |
|----------|------|-------------|
| `Anchor` | Anchor | Position and size |
| `LayoutMode` | Enum | Child layout mode |
| `Padding` | Padding | Internal spacing |
| `Background` | Background | Background color/texture |
| `Visible` | Boolean | Visibility state |
| `FlexWeight` | Number | Flex layout weight |
| `Opacity` | Number | Opacity (0.0 to 1.0) |

**Example:**
```
Group #MyContainer {
    Anchor: (Width: 400, Height: 300);
    LayoutMode: Top;
    Padding: (Full: 20);
    Background: (Color: #1a1a2e(0.95));
}
```

**Important:** The root `Group` in a `.ui` file must NOT have an ID. Named elements go inside it:

```
Group {
    LayoutMode: CenterMiddle;

    Group #Panel {
        // Named elements go here
    }
}
```

---

## Text Elements

### Label

Static or dynamic text display.

| Property | Type | Description |
|----------|------|-------------|
| `Text` | String | Text content |
| `Style` | Style | Text styling |
| `Anchor` | Anchor | Position and size |
| `Visible` | Boolean | Visibility state |
| `Opacity` | Number | Opacity (0.0 to 1.0) |

**Example:**
```
Label #Title {
    Style: (FontSize: 28, TextColor: #e94560, HorizontalAlignment: Center, RenderBold: true);
    Text: "Welcome";
}
```

**With Localization:**
```
Label #WelcomeText {
    Style: (FontSize: 16, TextColor: #ffffff);
    Text: %server.customUI.welcomeMessage;
}
```

**Note:** Label uses direct styling, not state-based styling. See [Styling](ui-styling.md#style-properties) for all style properties.

---

### TimerLabel

Label that displays countdown/timer values.

| Property | Type | Description |
|----------|------|-------------|
| `Text` | String | Timer format string |
| `Style` | Style | Text styling |
| `Anchor` | Anchor | Position and size |
| `Duration` | Number | Timer duration in seconds |

**Example:**
```
TimerLabel #Countdown {
    Style: (FontSize: 32, TextColor: #ff0000, HorizontalAlignment: Center);
    Duration: 60;
}
```

---

## Button Elements

### Button

Basic icon-based clickable button.

| Property | Type | Description |
|----------|------|-------------|
| `Anchor` | Anchor | Position and size |
| `Style` | StateStyle | State-based styling |
| `Icon` | String | Icon asset path |
| `Visible` | Boolean | Visibility state |

**Example:**
```
Button #CloseBtn {
    Anchor: (Width: 32, Height: 32);
    Style: (
        Default: (Background: (Color: #333333));
        Hovered: (Background: (Color: #444444));
    );
    Icon: "Common/UI/Icons/Close.png";
}
```

**Note:** Button click events must be registered server-side via `UIEventBuilder.addEventBinding()`. See [Java API](ui-api.md#uieventbuilder).

---

### TextButton

Button with text content. Requires state-based styling with `LabelStyle`.

| Property | Type | Description |
|----------|------|-------------|
| `Text` | String | Button text |
| `Anchor` | Anchor | Position and size |
| `Style` | StateStyle | State-based styling (requires `LabelStyle`) |
| `Visible` | Boolean | Visibility state |

**Example:**
```
TextButton #ActionButton {
    Anchor: (Width: 150, Height: 44);
    Style: (
        Default: (
            Background: (Color: #0f3460),
            LabelStyle: (FontSize: 18, TextColor: #ffffff, HorizontalAlignment: Center)
        ),
        Hovered: (
            Background: (Color: #1a5a90),
            LabelStyle: (FontSize: 18, TextColor: #ffffff, HorizontalAlignment: Center)
        )
    );
    Text: "Click Me";
}
```

**Important:** TextButton requires `LabelStyle` inside each state for text styling. Direct `Style` properties like `FontSize` will not work - they must be nested within `LabelStyle`.

---

### BackButton

Navigation back button with built-in styling.

| Property | Type | Description |
|----------|------|-------------|
| `Anchor` | Anchor | Position and size |
| `Style` | StateStyle | State-based styling |
| `Visible` | Boolean | Visibility state |

**Example:**
```
BackButton #Back {
    Anchor: (Width: 100, Height: 40);
}
```

---

## Input Elements

### TextField

Single-line text input field.

| Property | Type | Description |
|----------|------|-------------|
| `Anchor` | Anchor | Position and size |
| `Placeholder` | String | Placeholder text |
| `Value` | String | Current value |
| `MaxLength` | Number | Maximum character length |
| `Style` | Style | Input styling |

**Example:**
```
TextField #NameInput {
    Anchor: (Width: 200, Height: 36);
    Placeholder: "Enter your name...";
    MaxLength: 32;
}
```

**Note:** Use `CustomUIEventBindingType.ValueChanged` to receive input changes server-side.

---

### CompactTextField

Smaller text input variant.

| Property | Type | Description |
|----------|------|-------------|
| `Anchor` | Anchor | Position and size |
| `Placeholder` | String | Placeholder text |
| `Value` | String | Current value |
| `MaxLength` | Number | Maximum character length |

**Example:**
```
CompactTextField #SearchInput {
    Anchor: (Width: 150, Height: 28);
    Placeholder: "Search...";
}
```

---

### NumberField

Numeric input field.

| Property | Type | Description |
|----------|------|-------------|
| `Anchor` | Anchor | Position and size |
| `Value` | Number | Current numeric value |
| `Min` | Number | Minimum allowed value |
| `Max` | Number | Maximum allowed value |
| `Step` | Number | Increment/decrement step |

**Example:**
```
NumberField #QuantityInput {
    Anchor: (Width: 100, Height: 36);
    Value: 1;
    Min: 1;
    Max: 64;
    Step: 1;
}
```

---

### CheckBox

Boolean toggle checkbox.

| Property | Type | Description |
|----------|------|-------------|
| `Anchor` | Anchor | Position and size |
| `Checked` | Boolean | Current checked state |
| `Label` | String | Associated label text |

**Example:**
```
CheckBox #EnableSound {
    Anchor: (Width: 200, Height: 24);
    Checked: true;
    Label: "Enable Sound Effects";
}
```

---

### Slider

Range slider control.

| Property | Type | Description |
|----------|------|-------------|
| `Anchor` | Anchor | Position and size |
| `Value` | Number | Current value |
| `Min` | Number | Minimum value |
| `Max` | Number | Maximum value |
| `Step` | Number | Value increment |

**Example:**
```
Slider #VolumeSlider {
    Anchor: (Width: 200, Height: 24);
    Value: 50;
    Min: 0;
    Max: 100;
    Step: 1;
}
```

---

### DropdownBox

Dropdown selection menu.

| Property | Type | Description |
|----------|------|-------------|
| `Anchor` | Anchor | Position and size |
| `Options` | Array | List of selectable options |
| `SelectedIndex` | Number | Currently selected index |
| `Style` | Style | Dropdown styling |

**Example:**
```
DropdownBox #DifficultySelect {
    Anchor: (Width: 180, Height: 36);
    SelectedIndex: 0;
}
```

**Note:** Options are typically populated server-side via `UICommandBuilder.set()`.

---

## Display Elements

### Sprite

Image/sprite display.

| Property | Type | Description |
|----------|------|-------------|
| `Anchor` | Anchor | Position and size |
| `Texture` | String | Texture asset path |
| `Color` | Color | Tint color |
| `Visible` | Boolean | Visibility state |

**Example:**
```
Sprite #Logo {
    Anchor: (Width: 128, Height: 128);
    Texture: "Common/UI/Images/Logo.png";
}
```

---

### AssetImage

Asset-based image display for dynamic content.

| Property | Type | Description |
|----------|------|-------------|
| `Anchor` | Anchor | Position and size |
| `AssetPath` | String | Asset path reference |
| `Visible` | Boolean | Visibility state |

**Example:**
```
AssetImage #ItemIcon {
    Anchor: (Width: 64, Height: 64);
    AssetPath: "Items/Sword.png";
}
```

---

### ItemSlot

Inventory item slot display.

| Property | Type | Description |
|----------|------|-------------|
| `Anchor` | Anchor | Position and size |
| `SlotIndex` | Number | Inventory slot index |
| `ShowQuantity` | Boolean | Display item count |
| `Interactive` | Boolean | Allow interaction |

**Example:**
```
ItemSlot #InventorySlot0 {
    Anchor: (Width: 48, Height: 48);
    SlotIndex: 0;
    ShowQuantity: true;
    Interactive: true;
}
```

**Note:** Use `CustomUIEventBindingType.SlotClicking` for slot click events.

---

### ProgressBar

Horizontal progress bar display.

| Property | Type | Description |
|----------|------|-------------|
| `Anchor` | Anchor | Position and size |
| `Value` | Number | Current value (0.0 to 1.0) |
| `FillColor` | Color | Fill color |
| `BackgroundColor` | Color | Background color |

**Example:**
```
ProgressBar #HealthBar {
    Anchor: (Width: 200, Height: 20);
    Value: 0.75;
    FillColor: #00ff00;
    BackgroundColor: #333333;
}
```

---

### CircularProgressBar

Circular/radial progress indicator.

| Property | Type | Description |
|----------|------|-------------|
| `Anchor` | Anchor | Position and size |
| `Value` | Number | Current value (0.0 to 1.0) |
| `FillColor` | Color | Fill color |
| `Thickness` | Number | Ring thickness |

**Example:**
```
CircularProgressBar #CooldownIndicator {
    Anchor: (Width: 64, Height: 64);
    Value: 0.5;
    FillColor: #3498db;
    Thickness: 8;
}
```

---

## Related Documentation

- [UI Overview](ui.md) - System architecture and quick start
- [Styling & Layout](ui-styling.md) - Anchor, padding, colors, state-based styling
- [Templates & Variables](ui-templates.md) - Imports, variables, localization
- [Java API](ui-api.md) - Server-side API reference
