# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a complete reference for Hytale server plugin development containing:
- **docs/**: Comprehensive API documentation (28 markdown files)
- **examples/**: Standalone Gradle projects demonstrating different aspects of the plugin API

## Build Commands

Build any example (from its directory):
```batch
build.bat
```
Or with Gradle:
```batch
gradlew build
```

Deploy to Hytale mods folder:
```batch
deploy.bat
```

**Note:** When running from bash, use `./build.bat` and `./deploy.bat` (with the `./` prefix).

Mods directory: `%APPDATA%\Hytale\UserData\Mods\`

## Requirements

- Java 21+
- Hytale installed (provides `HytaleServer.jar` from `%APPDATA%\Hytale\install\release\package\game\latest\Server\`)

## Architecture

### Plugin Structure
- Plugins extend `JavaPlugin` and override `setup()` to register commands
- Each plugin requires a `manifest.json` in `src/main/resources/` with `Group`, `Name`, and `Main` fields (PascalCase)
- Plugins with UI assets need `"IncludesAssetPack": true` in manifest

### Command System
- Simple commands extend `AbstractPlayerCommand`
- Commands targeting other players extend `AbstractTargetPlayerCommand`
- Arguments registered via `withRequiredArg()` with types like `ArgTypes.RELATIVE_POSITION`, `ArgTypes.ITEM_ASSET`
- Execute method receives `CommandContext`, entity store, player ref, and world

### UI System
- Custom pages extend `BasicCustomUIPage`
- UI layouts defined in `.ui` files using Hytale's curly-brace DSL (placed in `resources/Common/UI/Custom/`)
- Button clicks send data events via `OnActivating: (SendData: "event_name");` and handled in `handleDataEvent()`
- Root `Group` in `.ui` files must NOT have an ID; named elements go inside it
- HUD controlled via `player.getHudManager().setVisibleHudComponents()`

## Examples

- **examples/commands/**: Command system (no-arg, position args, permissions)
- **examples/ui/**: Custom UI pages and HUD management
