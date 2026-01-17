# Hytale Plugin Examples

A complete reference for Hytale server plugin development with documentation and working examples.

## Documentation

The [docs/](./docs/) directory contains comprehensive documentation for the Hytale Plugin API:

- [Overview](./docs/overview.md) - Plugin API overview
- [Commands](./docs/commands.md) - Command system and argument types
- [UI](./docs/ui.md) - Custom UI pages and HUD management
- [Events](./docs/events.md) - Event handling and listeners
- And more...

## Examples

### [examples/commands/](./examples/commands/)
Demonstrates the command system with:
- Simple no-argument commands
- Commands with position arguments

### [examples/ui/](./examples/ui/)
Demonstrates the UI system with:
- Opening custom UI pages
- Toggling HUD components
- Loading `.ui` definition files

## Requirements

- Hytale installed (uses `HytaleServer.jar` from `%APPDATA%\Hytale`)
- Java 21+
- Gradle (wrapper included)

## Building

Each example can be built independently:

```batch
cd examples/commands
build.bat
```

Or using Gradle directly:

```batch
gradlew build
```

## Deployment

Copy the built JAR from `build/libs/` to:
```
%APPDATA%\Hytale\UserData\Mods\
```

## License

These examples are provided for educational purposes.
