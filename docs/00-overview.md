# Hytale API Documentation Overview

Quick reference for finding the right documentation file.

## Quick Reference Table

| File | Description | Key Classes |
|------|-------------|-------------|
| plugin-lifecycle.md | Plugin setup, lifecycle, and server events | JavaPlugin, PluginBase, PluginState, HytaleLogger |
| commands.md | Command creation and arguments | AbstractPlayerCommand, CommandContext |
| entities.md | Players, entities, stats, velocity, and entity lifecycle events | Player, PlayerRef, Velocity, EntityStatMap |
| player.md | Player events and messaging | PlayerConnectEvent, PlayerInteractEvent, Message |
| world.md | World access, world events, chunk events | World, WorldChunk, GameplayConfig, ClientFeature |
| events.md | Core event system and patterns | EventRegistry, EventPriority, EntityEventSystem |
| components.md | ECS architecture | Store, Ref, EntityStore, ChunkStore, TransformComponent |
| permissions.md | Permission checks and events | PermissionHolder, PlayerGroupEvent |
| inventory.md | Items, inventory, crafting, and inventory events | Inventory, ItemStack, CraftingRecipe, CraftRecipeEvent, DropItemEvent |
| tasks.md | Async task scheduling | TaskRegistry, TaskRegistration |
| ui.md | Player UI management | UICommandBuilder, WindowManager, HudComponent, Page |
| blocks.md | Block states and block events | BlockStateRegistry, BlockType, PlaceBlockEvent, BreakBlockEvent |
| assets.md | Asset registration and models | AssetRegistry, Model |
| codecs.md | Serialization | Codec, RecordCodecBuilder |
| interactions.md | Interaction system | Interaction, SimpleInteraction, SimpleInstantInteraction |
| networking.md | Network serialization and protocol | NetworkSerializable, Direction, WaitForDataFrom |
| prefabs.md | Prefab loading, placement, and events | PrefabStore, BlockSelection, PrefabRotation |
| math.md | Math library | Vector3d, Vector3l, Vector4d, Matrix4d, Quatf, Box |
| projectiles.md | Projectile spawning and physics | ProjectileModule, ProjectileConfig, StandardPhysicsProvider |
| collision.md | Collision detection and queries | CollisionModule, CollisionResult, CollisionDataArray |
| fluids.md | Fluid types and properties | Fluid |
| combat.md | Damage system and kill feed | Damage, DamageEventSystem, KillFeedEvent |
| npc.md | NPC loading and AI sensors | AllNPCsLoadedEvent, BuilderInfo, SensorEvent |
| adventure.md | Adventure features | DiscoverInstanceEvent, TreasureChestOpeningEvent |
| asset-editor.md | Asset editor events | EditorClientEvent, AssetEditorSelectAssetEvent |
| i18n.md | Localization events | GenerateDefaultLanguageEvent |
| singleplayer.md | Singleplayer events | SingleplayerRequestAccessEvent |

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
