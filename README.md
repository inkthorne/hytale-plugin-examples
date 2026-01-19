# Hytale Plugin Examples

A reference for Hytale server plugin development with documentation and working examples.

## Documentation

The [docs/](./docs/) directory contains comprehensive API documentation:

### Getting Started
- [Overview](./docs/00-overview.md) - Quick reference and navigation
- [Plugin Lifecycle](./docs/plugin-lifecycle.md) - Setup, lifecycle, and server events
- [Permissions](./docs/permissions.md) - Permission system

### Core Systems
- [Components](./docs/components.md) - Entity Component System (ECS)
- [Codecs](./docs/codecs.md) - Serialization and data persistence
- [Networking](./docs/networking.md) - Network protocols
- [Math](./docs/math.md) - Vectors, matrices, shapes

### Commands & UI
- [Commands](./docs/commands.md) - Command system and arguments
- [UI](./docs/ui.md) - Custom UI pages and HUD
- [i18n](./docs/i18n.md) - Localization

### World & Environment
- [World](./docs/world.md) - World management
- [Blocks](./docs/blocks.md) - Block states and custom blocks
- [Fluids](./docs/fluids.md) - Fluid systems
- [Prefabs](./docs/prefabs.md) - Structure templates
- [Prefab Categories](./docs/prefabs-categories.md) - Category system for prefabs
- [Collision](./docs/collision.md) - Collision queries

### Entities
- [Entities](./docs/entities.md) - Entity hierarchy
- [Player](./docs/player.md) - Player API
- [NPC](./docs/npc.md) - NPC and AI systems
- [NPC Roles](./docs/npc-roles.md) - NPC role templates and behaviors
- [Drops](./docs/drops.md) - Drop system and loot tables
- [Projectiles](./docs/projectiles.md) - Projectile configuration

### Combat & Effects
- [Combat](./docs/combat.md) - Damage and kill feed
- [Effects & Stats](./docs/effects-stats.md) - Status effects and stats

### Items
- [Items](./docs/items.md) - Item definitions overview
- [Block Items](./docs/items-blocks.md) - Block items
- [Consumables](./docs/items-consumables.md) - Consumable items
- [Crafting](./docs/items-crafting.md) - Crafting system
- [Tools](./docs/items-tools.md) - Tool items
- [Weapons](./docs/items-weapons.md) - Weapon templates

### World Generation
- [World Generation](./docs/worldgen.md) - World generation overview
- [Zones](./docs/worldgen-zones.md) - Zone system
- [Biomes](./docs/worldgen-biomes.md) - Biome definitions
- [Terrain](./docs/worldgen-terrain.md) - Terrain layers
- [Caves](./docs/worldgen-caves.md) - Cave system
- [Prefabs](./docs/worldgen-prefabs.md) - Structure placement

### Interactions
- [Interactions](./docs/interactions.md) - Interactions API overview
- [Flow Control](./docs/interactions-flow.md) - Serial, Parallel, Condition
- [Combo System](./docs/interactions-combo.md) - Chaining and charging
- [World Interactions](./docs/interactions-world.md) - Spawning and positioning
- [Combat Interactions](./docs/interactions-combat.md) - Damage and forces

### Assets & Events
- [Assets](./docs/assets.md) - Asset registry
- [Asset Editor](./docs/asset-editor.md) - Editor events
- [Audio](./docs/audio.md) - Audio system
- [Events](./docs/events.md) - Event system overview
- [Adventure](./docs/adventure.md) - Adventure gameplay events
- [Singleplayer](./docs/singleplayer.md) - Singleplayer events

### Utilities
- [Inventory](./docs/inventory.md) - Inventory management
- [Tasks](./docs/tasks.md) - Async task scheduling

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

### [examples/inventory/](./examples/inventory/)
Demonstrates the inventory API with:
- Adding items with transaction handling
- Clearing inventory sections
- Inspecting inventory contents
- Sorting storage by name/type/rarity

## Requirements

- Hytale installed (uses `HytaleServer.jar` from `%APPDATA%\Hytale`)
- Java 25+
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

MIT License - see [LICENSE](./LICENSE) for details.
