# UI Templates, Variables & Localization

Advanced DSL features for reusable UI components and localization.

---

## Quick Navigation

| Topic | Description |
|-------|-------------|
| [File Imports](#file-imports) | Include external .ui files |
| [Variables](#variables) | Define and use variables |
| [Spread Operator](#spread-operator) | Compose styles and properties |
| [Localization](#localization) | Translatable text strings |
| [Element References](#element-references) | Reference elements by ID |
| [Patterns](#patterns--best-practices) | Reusable component patterns |

**Related:** [Elements](ui-elements.md) | [Styling](ui-styling.md) | [Java API](ui-api.md) | [UI Overview](ui.md)

---

## File Imports

Import external `.ui` files as variables for reuse across your UI.

### Syntax

```
$VarName = "path/to/file.ui";
```

### Path Resolution

Paths are relative to the current `.ui` file location:

```
$Common = "../Common.ui";           // Parent directory
$Shared = "Shared/Components.ui";   // Subdirectory
$Styles = "./Styles.ui";            // Same directory (explicit)
```

### Example

**Components/Button.ui:**
```
TextButton {
    Anchor: (Width: 120, Height: 40);
    Style: (
        Default: (
            Background: (Color: #3498db),
            LabelStyle: (FontSize: 16, TextColor: #ffffff, HorizontalAlignment: Center)
        ),
        Hovered: (
            Background: (Color: #5dade2),
            LabelStyle: (FontSize: 16, TextColor: #ffffff, HorizontalAlignment: Center)
        )
    );
}
```

**Pages/MyPage.ui:**
```
$PrimaryButton = "Components/Button.ui";

Group {
    LayoutMode: CenterMiddle;

    Group #Panel {
        Anchor: (Width: 400, Height: 200);
        LayoutMode: Center;

        // Use imported button component
        $PrimaryButton #SaveButton {
            Text: "Save";
        }

        $PrimaryButton #CancelButton {
            Text: "Cancel";
        }
    }
}
```

### Notes

- Imported files become element templates
- You can override properties when instantiating
- Import paths are resolved at parse time

---

## Variables

Define reusable values for consistency across your UI.

### Syntax

```
@VarName = value;
```

### Variable Types

**Numeric:**
```
@ButtonHeight = 44;
@PanelWidth = 400;
@Padding = 20;
```

**Color:**
```
@PrimaryColor = #3498db;
@DangerColor = #e74c3c;
@TextColor = #ffffff;
@BackgroundAlpha = 0.95;
```

**Style Objects:**
```
@TitleStyle = (FontSize: 28, TextColor: #ffffff, RenderBold: true);
@SubtitleStyle = (FontSize: 16, TextColor: #aaaaaa, HorizontalAlignment: Center);
```

**Background Objects:**
```
@PanelBackground = (Color: #1a1a2e(0.95));
@ButtonBackground = (TexturePath: "Common/UI/Shared/Button.png", Border: 10);
```

**Complete State Styles:**
```
@PrimaryButtonStyle = (
    Default: (
        Background: (Color: #3498db),
        LabelStyle: (FontSize: 16, TextColor: #ffffff, HorizontalAlignment: Center)
    ),
    Hovered: (
        Background: (Color: #5dade2),
        LabelStyle: (FontSize: 16, TextColor: #ffffff, HorizontalAlignment: Center)
    )
);
```

### Using Variables

Reference variables with `@` prefix:

```
@ButtonHeight = 44;
@PrimaryColor = #3498db;
@TitleStyle = (FontSize: 24, TextColor: #ffffff);

Group #Panel {
    Anchor: (Width: 400, Height: 300);
    Padding: (Full: @Padding);
    Background: (Color: @PrimaryColor);

    Label #Title {
        Style: @TitleStyle;
        Text: "Hello";
    }

    TextButton #Submit {
        Anchor: (Width: 150, Height: @ButtonHeight);
        Style: @PrimaryButtonStyle;
        Text: "Submit";
    }
}
```

### Variable Scope

Variables defined at the top of a `.ui` file are available throughout that file:

```
// Variables defined here are file-scoped
@HeaderHeight = 60;
@FooterHeight = 40;
@ContentPadding = 20;

Group {
    LayoutMode: Top;

    Group #Header {
        Anchor: (Height: @HeaderHeight);
    }

    Group #Content {
        FlexWeight: 1;
        Padding: (Full: @ContentPadding);
    }

    Group #Footer {
        Anchor: (Height: @FooterHeight);
    }
}
```

---

## Spread Operator

Spread properties from a variable into an element.

### Syntax

```
...@VariableName
```

### Example

```
@CommonPanelProps = (
    Padding: (Full: 20),
    Background: (Color: #1a1a2e(0.95)),
    LayoutMode: Top
);

Group #Panel1 {
    Anchor: (Width: 300, Height: 200);
    ...@CommonPanelProps   // Spreads Padding, Background, LayoutMode
}

Group #Panel2 {
    Anchor: (Width: 400, Height: 300);
    ...@CommonPanelProps   // Same properties applied
}
```

### Composing Styles

Combine multiple variable spreads:

```
@BaseStyle = (FontSize: 16, TextColor: #ffffff);
@CenteredStyle = (HorizontalAlignment: Center);
@BoldStyle = (RenderBold: true);

Label #CenteredTitle {
    Style: (
        ...@BaseStyle,
        ...@CenteredStyle,
        ...@BoldStyle,
        FontSize: 24  // Override specific property
    );
    Text: "Title";
}
```

---

## Localization

Use localization keys for translatable text.

### Syntax

```
Text: %namespace.key;
```

### Example

```
Label #WelcomeMessage {
    Style: (FontSize: 18, TextColor: #ffffff);
    Text: %server.customUI.welcomeMessage;
}

Label #ButtonLabel {
    Text: %server.customUI.submitButton;
}
```

### Server-Side Registration

Localization keys are registered server-side. See [Internationalization](i18n.md) for details.

```java
// In your plugin setup
LocalizationManager localization = server.getLocalizationManager();
localization.register("server.customUI.welcomeMessage", "Welcome to the server!");
localization.register("server.customUI.submitButton", "Submit");
```

### Dynamic Text with Placeholders

For dynamic content, use server-side string formatting:

```java
UICommandBuilder cmd = new UICommandBuilder();
cmd.set("#playerName.Text", "Welcome, " + playerName + "!");
```

### Best Practices

1. Use descriptive namespace paths: `server.customUI.pageName.elementPurpose`
2. Keep keys consistent across your plugin
3. Provide fallback text in the registration

---

## Element References

Reference elements by their ID for event binding and dynamic updates.

### Syntax

```
#ElementId
```

### In Event Bindings

```java
// Server-side event registration
eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "MyButton");
eventBuilder.addEventBinding(CustomUIEventBindingType.ValueChanged, "SearchInput");
```

### In UICommandBuilder

```java
UICommandBuilder cmd = new UICommandBuilder();

// Set properties by element ID
cmd.set("#PlayerName.Text", playerName);
cmd.set("#Score.Text", String.valueOf(score));

// Append to specific element
cmd.append("#Container", "Components/ListItem.ui");

// Clear element contents
cmd.clear("#ItemList");

// Remove element
cmd.remove("#OldElement");
```

### In .ui Files

Elements with IDs can be targeted for dynamic updates:

```
Group #StatusPanel {
    Label #StatusText {
        Style: (FontSize: 14, TextColor: #ffffff);
        Text: "Loading...";  // Will be updated server-side
    }

    ProgressBar #LoadingBar {
        Anchor: (Width: 200, Height: 10);
        Value: 0;  // Will be updated server-side
    }
}
```

```java
// Update dynamically
cmd.set("#StatusText.Text", "Connected!");
cmd.set("#LoadingBar.Value", 1.0f);
```

---

## Patterns & Best Practices

### Reusable Component Files

Create a library of reusable components:

**Components/Buttons.ui:**
```
@PrimaryStyle = (
    Default: (Background: (Color: #3498db), LabelStyle: (FontSize: 16, TextColor: #ffffff, HorizontalAlignment: Center)),
    Hovered: (Background: (Color: #5dade2), LabelStyle: (FontSize: 16, TextColor: #ffffff, HorizontalAlignment: Center))
);

@SecondaryStyle = (
    Default: (Background: (Color: #95a5a6), LabelStyle: (FontSize: 16, TextColor: #ffffff, HorizontalAlignment: Center)),
    Hovered: (Background: (Color: #bdc3c7), LabelStyle: (FontSize: 16, TextColor: #ffffff, HorizontalAlignment: Center))
);

@DangerStyle = (
    Default: (Background: (Color: #e74c3c), LabelStyle: (FontSize: 16, TextColor: #ffffff, HorizontalAlignment: Center)),
    Hovered: (Background: (Color: #ec7063), LabelStyle: (FontSize: 16, TextColor: #ffffff, HorizontalAlignment: Center))
);
```

### Theme Variables

Create a theme file for consistent styling:

**Theme.ui:**
```
// Colors
@ColorPrimary = #3498db;
@ColorSecondary = #2ecc71;
@ColorDanger = #e74c3c;
@ColorWarning = #f39c12;
@ColorBackground = #1a1a2e;
@ColorText = #ffffff;
@ColorTextMuted = #aaaaaa;

// Typography
@FontSizeSmall = 12;
@FontSizeMedium = 16;
@FontSizeLarge = 24;
@FontSizeTitle = 32;

// Spacing
@SpacingSmall = 8;
@SpacingMedium = 16;
@SpacingLarge = 24;

// Common Styles
@TextStyle = (FontSize: @FontSizeMedium, TextColor: @ColorText);
@MutedTextStyle = (FontSize: @FontSizeSmall, TextColor: @ColorTextMuted);
@TitleStyle = (FontSize: @FontSizeTitle, TextColor: @ColorText, RenderBold: true);
```

**Using the theme:**
```
$Theme = "../Theme.ui";

Group {
    Background: (Color: @ColorBackground(0.95));
    Padding: (Full: @SpacingLarge);

    Label #Title {
        Style: @TitleStyle;
        Text: "Settings";
    }

    Label #Description {
        Style: @MutedTextStyle;
        Text: "Configure your preferences";
    }
}
```

### Common Mistakes

**Wrong: Using direct style on TextButton**
```
// This won't work!
TextButton {
    Style: (FontSize: 16, TextColor: #ffffff);  // WRONG
    Text: "Click";
}
```

**Right: Using state-based style with LabelStyle**
```
TextButton {
    Style: (
        Default: (LabelStyle: (FontSize: 16, TextColor: #ffffff, HorizontalAlignment: Center))
    );
    Text: "Click";
}
```

**Wrong: Adding ID to root Group**
```
// This won't work!
Group #RootPanel {  // WRONG - root cannot have ID
    // content
}
```

**Right: Anonymous root, named children**
```
Group {  // No ID on root
    Group #Panel {  // ID on children is fine
        // content
    }
}
```

**Wrong: Event handlers in .ui files**
```
// This syntax is NOT supported!
Button #MyButton {
    OnActivating: (SendData: "clicked");  // WRONG
}
```

**Right: Register events server-side**
```java
// In your CustomUIPage build method
eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "MyButton");
```

---

## Related Documentation

- [UI Overview](ui.md) - System architecture and quick start
- [Elements](ui-elements.md) - All element types
- [Styling](ui-styling.md) - Layout and visual styling
- [Java API](ui-api.md) - Server-side API reference
- [Internationalization](i18n.md) - Localization system
