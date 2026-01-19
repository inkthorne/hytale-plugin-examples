# Hytale API Documentation Overview

Quick reference for finding the right documentation file.

## Quick Reference Table

| File | Description |
|------|-------------|
| [interactions.md](interactions.md) | Comprehensive reference for entity interactions including combo systems, combat effects, control flow, and world manipulation. |
| [plugin-lifecycle.md](plugin-lifecycle.md) | Documents the complete plugin lifecycle from initialization through shutdown, including setup phases, event registration, and logging. |
| [commands.md](commands.md) | Details the command system for creating player commands with arguments, permissions, subcommands, and built-in argument types. |
| [entities.md](entities.md) | Covers the entity system including Player and Entity classes, entity stats, velocity management, and interaction chains. |
| [player.md](player.md) | Describes player-specific functionality including messaging, permissions, connect/disconnect events, and interaction events. |
| [world.md](world.md) | Comprehensive world management API covering the World class, chunks, chunk tracking, configuration, and lifecycle events. |
| [events.md](events.md) | Core event system documentation covering registration methods, event priorities, keyed vs non-keyed events, and ECS patterns. |
| [components.md](components.md) | Entity Component System (ECS) documentation including Store, Ref, queries, CommandBuffer, and ticking systems. |
| [permissions.md](permissions.md) | Covers permission checking for players and commands, plus permission-related events like group changes. |
| [inventory.md](inventory.md) | Complete inventory management API including item stacks, containers, transactions, crafting recipes, and related events. |
| [tasks.md](tasks.md) | Documents the task registration system for tracking async futures and scheduled tasks throughout plugin lifecycle. |
| [ui.md](ui.md) | Comprehensive UI building system covering pages, windows, HUD management, UI DSL syntax, and interactive elements. |
| [blocks.md](blocks.md) | Covers the block system including block states, BlockType properties, block events (place/break/use/damage), and world access. |
| [assets.md](assets.md) | Asset registry system for custom assets, asset stores, built-in asset access, and asset lifecycle events. |
| [codecs.md](codecs.md) | Serialization system using codecs for data persistence and configuration loading, including RecordCodecBuilder patterns. |
| [networking.md](networking.md) | Network serialization types including NetworkSerializable interface, Direction rotation class, and sync modes. |
| [prefabs.md](prefabs.md) | Prefab system for placing pre-defined block/entity selections including transformations, placement, and prefab events. |
| [math.md](math.md) | Comprehensive math library with vectors (Vector3d/f/i/l), matrices, transforms, Box AABB, and utility methods. |
| [projectiles.md](projectiles.md) | Projectile spawning and management including ProjectileConfig assets, physics simulation, and projectile components. |
| [collision.md](collision.md) | Collision detection and querying system for block and character collisions, results, and material-based filtering. |
| [fluids.md](fluids.md) | Fluid asset type documentation for water, lava, and other fluids with damage and interaction properties. |
| [combat.md](combat.md) | Combat system covering damage events, damage causes, knockback mechanics, blocking/wielding, and kill feed customization. |
| [npc.md](npc.md) | NPC loading events and metadata including BuilderInfo for loaded NPCs and sensor event systems for AI. |
| [adventure.md](adventure.md) | Adventure gameplay features including instance discovery, zone discovery, treasure chest opening, and world map tracking. |
| [asset-editor.md](asset-editor.md) | Asset editor events for button activation, asset creation, client disconnects, and autocomplete/dataset requests. |
| [i18n.md](i18n.md) | Localization system with GenerateDefaultLanguageEvent for registering plugin translations during server startup. |
| [singleplayer.md](singleplayer.md) | Singleplayer-specific functionality with SingleplayerRequestAccessEvent for handling local server access requests. |

## Class Lookup Index

```
# Core Plugin
JavaPlugin, PluginBase           → plugin-lifecycle.md
BootEvent, ShutdownEvent         → plugin-lifecycle.md
PrepareUniverseEvent             → plugin-lifecycle.md
PluginSetupEvent                 → plugin-lifecycle.md
PluginState                      → plugin-lifecycle.md
PluginIdentifier, PluginManifest → plugin-lifecycle.md
HytaleLogger                     → plugin-lifecycle.md

# Commands
AbstractPlayerCommand            → commands.md
AbstractWorldCommand             → commands.md
AbstractTargetPlayerCommand      → commands.md
CommandContext                   → commands.md
CommandSender                    → commands.md
CommandOwner                     → commands.md
CommandRegistration              → commands.md
ArgumentType, SingleArgumentType → commands.md
ArgTypes                         → commands.md

# Entities
PlayerRef, Player                → entities.md
Velocity, EntityStatMap          → entities.md
StatModifiersManager             → entities.md
EntityEvent, EntityRemoveEvent   → entities.md
DropItemEvent, SwitchActiveSlotEvent → inventory.md
InteractivelyPickupItemEvent     → inventory.md
LivingEntityInventoryChangeEvent → inventory.md
InteractionManager               → entities.md (also referenced in interactions.md)

# Player Events & Messaging
PlayerConnectEvent               → player.md
PlayerDisconnectEvent            → player.md
PlayerInteractEvent              → player.md
InteractionType                  → player.md
Message, FormattedMessage        → player.md
GameMode                         → player.md
ChangeGameModeEvent              → player.md
CraftRecipeEvent                 → inventory.md
HiddenPlayersManager             → player.md

# World & Chunks
World                            → world.md
WorldChunk                       → world.md
ChunkTracker, ChunkVisibility    → world.md
ChunkFlag                        → world.md
WorldEvent, AddWorldEvent        → world.md
ChunkEvent, ChunkSaveEvent       → world.md
MoonPhaseChangeEvent             → world.md
GameplayConfig                   → world.md
WorldConfig, DeathConfig         → world.md
ClientFeature                    → world.md

# Event System
EventRegistry                    → events.md
EventRegistration                → events.md
EventPriority                    → events.md
EntityEventSystem                → events.md
IBaseEvent, IEvent, IAsyncEvent  → events.md
ICancellable                     → events.md
EcsEvent, CancellableEcsEvent    → events.md
ICancellableEcsEvent             → events.md

# ECS
Store, Ref, Component            → components.md
Query, CommandBuffer             → components.md
EntityStore, ChunkStore          → components.md
ComponentRegistry                → components.md
TransformComponent               → components.md
Holder, Archetype                → components.md
EntityTickingSystem              → components.md

# Permissions
PermissionHolder                 → permissions.md
PlayerGroupEvent                 → permissions.md
PlayerPermissionChangeEvent      → permissions.md

# Inventory & Crafting
Inventory, ItemStack, Item       → inventory.md
ItemContainer, CombinedItemContainer → inventory.md
SmartMoveType, SortType          → inventory.md
FilterType, FilterActionType, SlotFilter → inventory.md
ActionType, ItemStackTransaction → inventory.md
CraftingRecipe                   → inventory.md
MaterialQuantity, ResourceQuantity → inventory.md
BenchRequirement, BenchType      → inventory.md

# Tasks
TaskRegistry, TaskRegistration   → tasks.md

# UI
UICommandBuilder                 → ui.md
WindowManager                    → ui.md
PageManager, HudManager          → ui.md
HotbarManager                    → ui.md
Window, WindowType, OpenWindow   → ui.md
Page, CustomUIPage               → ui.md
HudComponent                     → ui.md
CustomUIEventBindingType         → ui.md

# Blocks
BlockStateRegistry               → blocks.md
BlockStateRegistration           → blocks.md
BlockType                        → blocks.md
BlockMaterial                    → blocks.md
Rotation, RotationTuple          → blocks.md
PlaceBlockEvent, BreakBlockEvent → blocks.md
DamageBlockEvent, UseBlockEvent  → blocks.md

# Assets
AssetRegistry                    → assets.md
Model, ModelAsset                → assets.md

# Interactions
Interaction                      → interactions.md
SimpleInteraction                → interactions.md
SimpleInstantInteraction         → interactions.md

# Networking
NetworkSerializable              → networking.md
Direction                        → networking.md
WaitForDataFrom                  → networking.md

# Prefabs
PrefabStore, BlockSelection      → prefabs.md
PrefabRotation, PrefabEntry      → prefabs.md
PrefabWeights                    → prefabs.md
PrefabPasteEvent                 → prefabs.md
PrefabPlaceEntityEvent           → prefabs.md

# Math
Vector3d, Vector3f, Vector3i, Vector3l → math.md
Vector2d, Vector2i, Vector4d     → math.md
Matrix4d, Mat4f, Quatf           → math.md
Transform, Box, Axis, MathUtil   → math.md

# Codecs
Codec, RecordCodecBuilder        → codecs.md

# Projectiles
ProjectileModule, ProjectileConfig → projectiles.md
PhysicsConfig, StandardPhysicsConfig → projectiles.md
ImpactConsumer, BounceConsumer   → projectiles.md
StandardPhysicsProvider          → projectiles.md
PredictedProjectile              → projectiles.md
BallisticData, BallisticDataProvider → projectiles.md
ProjectileInteraction            → projectiles.md

# Collision
CollisionModule, CollisionResult → collision.md
BlockCollisionData, CollisionConfig → collision.md
CollisionFilter, CollisionMaterial → collision.md
BasicCollisionData, BoxCollisionData → collision.md
IBlockCollisionEvaluator         → collision.md
BoxBlockIntersectionEvaluator    → collision.md
CollisionModuleConfig            → collision.md
CollisionDataArray               → collision.md
CollisionResultComponent         → collision.md

# Fluids
Fluid                            → fluids.md

# Combat
Damage, DamageEventSystem        → combat.md
Damage.Source, Damage.EntitySource → combat.md
DamageDataComponent              → combat.md
DamageCause                      → combat.md
KillFeedEvent                    → combat.md

# NPCs
AllNPCsLoadedEvent               → npc.md
LoadedNPCEvent, BuilderInfo      → npc.md
SensorEvent, SensorEntityEvent   → npc.md
EventSearchType                  → npc.md

# Adventure
DiscoverInstanceEvent            → adventure.md
DiscoverZoneEvent                → adventure.md
TreasureChestOpeningEvent        → adventure.md
InstanceDiscoveryConfig          → adventure.md
WorldMapTracker                  → adventure.md
ZoneDiscoveryInfo                → adventure.md

# Asset Events
AssetPackRegisterEvent           → assets.md
AssetPackUnregisterEvent         → assets.md
LoadAssetEvent                   → assets.md
GenerateSchemaEvent              → assets.md
CommonAssetMonitorEvent          → assets.md
SendCommonAssetsEvent            → assets.md
PathEvent                        → assets.md

# Asset Editor
EditorClientEvent                → asset-editor.md
AssetEditorActivateButtonEvent   → asset-editor.md
AssetEditorAssetCreatedEvent     → asset-editor.md
AssetEditorClientDisconnectEvent → asset-editor.md
AssetEditorSelectAssetEvent      → asset-editor.md
AssetEditorFetchAutoCompleteDataEvent → asset-editor.md
AssetEditorRequestDataSetEvent   → asset-editor.md

# Localization
GenerateDefaultLanguageEvent     → i18n.md

# Singleplayer
SingleplayerRequestAccessEvent   → singleplayer.md

# Chunk Events
ChunkPreLoadProcessEvent         → world.md
```

## Topic Groups

**Core** - Essential plugin development
- plugin-lifecycle.md - Plugin entry point, setup, and server lifecycle events
- commands.md - Slash commands
- entities.md - Players, entities, stats, velocity, and entity events
- player.md - Player events and messaging
- world.md - World access, world events, and chunk events
- events.md - Core event system patterns
- components.md - ECS system

**Systems** - Game systems integration
- permissions.md - Permission checks and permission events
- inventory.md - Items, inventory, and crafting
- tasks.md - Async scheduling
- ui.md - Player UI
- blocks.md - Block manipulation and block events
- assets.md - Asset registry, models, and asset events
- interactions.md - Interaction system (attacks, abilities)
- projectiles.md - Projectile spawning and physics
- collision.md - Collision detection and queries
- fluids.md - Fluid types and properties
- i18n.md - Localization events
- singleplayer.md - Singleplayer events

**Networking** - Network communication
- networking.md - Serialization, protocol types

**Editor** - Development tools
- asset-editor.md - Asset editor events

**Combat & NPCs** - Combat and AI systems
- combat.md - Damage system, damage events, kill feed
- npc.md - NPC loading, AI sensors

**Adventure** - Adventure gameplay features
- adventure.md - Instance discovery, treasure chests

**Data** - Serialization
- codecs.md - Data encoding/decoding

**Utilities** - Helper systems
- prefabs.md - Prefab loading, placement, and events
- math.md - Vectors, matrices, quaternions, shapes
