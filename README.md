# Hytale Plugin Examples

A collection of minimal, self-contained example plugins demonstrating Hytale plugin development.

## Examples

### [commands-example](./commands-example/)
Demonstrates the command system with:
- Simple no-argument commands
- Commands with position arguments
- Admin commands with permission requirements

### [ui-example](./ui-example/)
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
cd commands-example
build.bat
```

Or using Gradle directly:

```batch
gradlew shadowJar
```

## Deployment

Copy the built JAR from `build/libs/` to:
```
%APPDATA%\Hytale\UserData\Mods\
```

## License

These examples are provided for educational purposes.
