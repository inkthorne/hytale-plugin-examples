# Hytale Plugin & Asset Reference

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

## Asset Type Index

JSON asset types used in Hytale's data-driven systems.

**Interactions - Combo Systems**
[ChainingInteraction](interactions-combo.md#chaininginteraction), [FirstClickInteraction](interactions-combo.md#firstclickinteraction), [ChargingInteraction](interactions-combo.md#charginginteraction), [ChainFlagInteraction](interactions-combo.md#chainflaginteraction), [CancelChainInteraction](interactions-combo.md#cancelchaininteraction)

**Interactions - Combat & Effects**
[SimpleInteraction](interactions-combat.md#simpleinteraction), [DamageEntity](interactions-combat.md#damageentity), [ApplyForce](interactions-combat.md#applyforce), [ApplyEffect](interactions-combat.md#applyeffect), [ClearEntityEffect](interactions-combat.md#clearentityeffect), [ChangeStat](interactions-combat.md#changestat), [InterruptInteraction](interactions-combat.md#interruptinteraction)

**Interactions - Control Flow**
[Serial](interactions-flow.md#serial), [Parallel](interactions-flow.md#parallel), [Condition](interactions-flow.md#condition), [StatsCondition](interactions-flow.md#statscondition), [EffectCondition](interactions-flow.md#effectcondition), [BlockCondition](interactions-flow.md#blockcondition), [CooldownCondition](interactions-flow.md#cooldowncondition), [MovementCondition](interactions-flow.md#movementcondition), [DestroyCondition](interactions-flow.md#destroycondition), [PlacementCountCondition](interactions-flow.md#placementcountcondition), [Repeat](interactions-flow.md#repeat), [Select](interactions-flow.md#select), [Replace](interactions-flow.md#replace)

**Interactions - Target Selectors**
[AOECircle](interactions-flow.md#aoecircleselector), [AOECylinder](interactions-flow.md#aoecylinderselector), [Raycast](interactions-flow.md#raycastselector), [Stab](interactions-flow.md#stabselector), [Horizontal](interactions-flow.md#horizontalselector)

**Interactions - Entity & World**
[SpawnPrefab](interactions-world.md#spawnprefab), [RemoveEntity](interactions-world.md#removeentity), [LaunchProjectile](interactions-world.md#launchprojectile), [SendMessage](interactions-world.md#sendmessage), [RunRootInteraction](interactions-world.md#runrootinteraction), [OpenPage](interactions-world.md#ui-interactions), [OpenCustomUI](interactions-world.md#ui-interactions), [EquipItem](interactions-world.md#inventory-interactions), [ModifyInventory](interactions-world.md#inventory-interactions), [BreakBlock](interactions-world.md#block-interactions), [PlaceBlock](interactions-world.md#block-interactions), [ChangeState](interactions-world.md#changestate), [LaunchPadInteraction](interactions-world.md#launchpadinteraction), [WieldingInteraction](interactions-world.md#wieldinginteraction)

---

## Java Class Index

**Core Plugin**
[JavaPlugin](plugin-lifecycle.md), [PluginBase](plugin-lifecycle.md), [BootEvent](plugin-lifecycle.md), [ShutdownEvent](plugin-lifecycle.md), [PrepareUniverseEvent](plugin-lifecycle.md), [PluginSetupEvent](plugin-lifecycle.md), [PluginState](plugin-lifecycle.md), [PluginIdentifier](plugin-lifecycle.md), [PluginManifest](plugin-lifecycle.md), [HytaleLogger](plugin-lifecycle.md)

**Commands**
[AbstractPlayerCommand](commands.md), [AbstractWorldCommand](commands.md), [AbstractTargetPlayerCommand](commands.md), [CommandContext](commands.md), [CommandSender](commands.md), [CommandOwner](commands.md), [CommandRegistration](commands.md), [ArgumentType](commands.md), [SingleArgumentType](commands.md), [ArgTypes](commands.md)

**Entities**
[PlayerRef](entities.md), [Player](entities.md), [Velocity](entities.md), [EntityStatMap](entities.md), [StatModifiersManager](entities.md), [EntityEvent](entities.md), [EntityRemoveEvent](entities.md), [InteractionManager](entities.md)

**Player Events & Messaging**
[PlayerConnectEvent](player.md), [PlayerDisconnectEvent](player.md), [PlayerInteractEvent](player.md), [InteractionType](player.md), [Message](player.md), [FormattedMessage](player.md), [GameMode](player.md), [ChangeGameModeEvent](player.md), [HiddenPlayersManager](player.md)

**World & Chunks**
[World](world.md), [WorldChunk](world.md), [ChunkTracker](world.md), [ChunkVisibility](world.md), [ChunkFlag](world.md), [WorldEvent](world.md), [AddWorldEvent](world.md), [ChunkEvent](world.md), [ChunkSaveEvent](world.md), [ChunkPreLoadProcessEvent](world.md), [MoonPhaseChangeEvent](world.md), [GameplayConfig](world.md), [WorldConfig](world.md), [DeathConfig](world.md), [ClientFeature](world.md)

**Event System**
[EventRegistry](events.md), [EventRegistration](events.md), [EventPriority](events.md), [EntityEventSystem](events.md), [IBaseEvent](events.md), [IEvent](events.md), [IAsyncEvent](events.md), [ICancellable](events.md), [EcsEvent](events.md), [CancellableEcsEvent](events.md), [ICancellableEcsEvent](events.md)

**ECS**
[Store](components.md), [Ref](components.md), [Component](components.md), [Query](components.md), [CommandBuffer](components.md), [EntityStore](components.md), [ChunkStore](components.md), [ComponentRegistry](components.md), [TransformComponent](components.md), [Holder](components.md), [Archetype](components.md), [EntityTickingSystem](components.md)

**Permissions**
[PermissionHolder](permissions.md), [PlayerGroupEvent](permissions.md), [PlayerPermissionChangeEvent](permissions.md)

**Inventory & Crafting**
[Inventory](inventory.md), [ItemStack](inventory.md), [Item](inventory.md), [ItemContainer](inventory.md), [CombinedItemContainer](inventory.md), [SmartMoveType](inventory.md), [SortType](inventory.md), [FilterType](inventory.md), [FilterActionType](inventory.md), [SlotFilter](inventory.md), [ActionType](inventory.md), [ItemStackTransaction](inventory.md), [CraftingRecipe](inventory.md), [MaterialQuantity](inventory.md), [ResourceQuantity](inventory.md), [BenchRequirement](inventory.md), [BenchType](inventory.md), [DropItemEvent](inventory.md), [SwitchActiveSlotEvent](inventory.md), [InteractivelyPickupItemEvent](inventory.md), [LivingEntityInventoryChangeEvent](inventory.md), [CraftRecipeEvent](inventory.md)

**Tasks**
[TaskRegistry](tasks.md), [TaskRegistration](tasks.md)

**UI**
[UICommandBuilder](ui.md), [WindowManager](ui.md), [PageManager](ui.md), [HudManager](ui.md), [HotbarManager](ui.md), [Window](ui.md), [WindowType](ui.md), [OpenWindow](ui.md), [Page](ui.md), [CustomUIPage](ui.md), [HudComponent](ui.md), [CustomUIEventBindingType](ui.md)

**Blocks**
[BlockStateRegistry](blocks.md), [BlockStateRegistration](blocks.md), [BlockType](blocks.md), [BlockMaterial](blocks.md), [Rotation](blocks.md), [RotationTuple](blocks.md), [PlaceBlockEvent](blocks.md), [BreakBlockEvent](blocks.md), [DamageBlockEvent](blocks.md), [UseBlockEvent](blocks.md)

**Assets**
[AssetRegistry](assets.md), [Model](assets.md), [ModelAsset](assets.md), [AssetPackRegisterEvent](assets.md), [AssetPackUnregisterEvent](assets.md), [LoadAssetEvent](assets.md), [GenerateSchemaEvent](assets.md), [CommonAssetMonitorEvent](assets.md), [SendCommonAssetsEvent](assets.md), [PathEvent](assets.md)

**Interactions**
[Interaction](interactions.md), [SimpleInteraction](interactions.md), [SimpleInstantInteraction](interactions.md)

**Networking**
[NetworkSerializable](networking.md), [Direction](networking.md), [WaitForDataFrom](networking.md)

**Prefabs**
[PrefabStore](prefabs.md), [BlockSelection](prefabs.md), [PrefabRotation](prefabs.md), [PrefabEntry](prefabs.md), [PrefabWeights](prefabs.md), [PrefabPasteEvent](prefabs.md), [PrefabPlaceEntityEvent](prefabs.md)

**Math**
[Vector3d](math.md), [Vector3f](math.md), [Vector3i](math.md), [Vector3l](math.md), [Vector2d](math.md), [Vector2i](math.md), [Vector4d](math.md), [Matrix4d](math.md), [Mat4f](math.md), [Quatf](math.md), [Transform](math.md), [Box](math.md), [Axis](math.md), [MathUtil](math.md)

**Codecs**
[Codec](codecs.md), [RecordCodecBuilder](codecs.md)

**Projectiles**
[ProjectileModule](projectiles.md), [ProjectileConfig](projectiles.md), [PhysicsConfig](projectiles.md), [StandardPhysicsConfig](projectiles.md), [ImpactConsumer](projectiles.md), [BounceConsumer](projectiles.md), [StandardPhysicsProvider](projectiles.md), [PredictedProjectile](projectiles.md), [BallisticData](projectiles.md), [BallisticDataProvider](projectiles.md), [ProjectileInteraction](projectiles.md)

**Collision**
[CollisionModule](collision.md), [CollisionResult](collision.md), [BlockCollisionData](collision.md), [CollisionConfig](collision.md), [CollisionFilter](collision.md), [CollisionMaterial](collision.md), [BasicCollisionData](collision.md), [BoxCollisionData](collision.md), [IBlockCollisionEvaluator](collision.md), [BoxBlockIntersectionEvaluator](collision.md), [CollisionModuleConfig](collision.md), [CollisionDataArray](collision.md), [CollisionResultComponent](collision.md)

**Fluids**
[Fluid](fluids.md)

**Combat**
[Damage](combat.md), [DamageEventSystem](combat.md), [Damage.Source](combat.md), [Damage.EntitySource](combat.md), [DamageDataComponent](combat.md), [DamageCause](combat.md), [KillFeedEvent](combat.md)

**NPCs**
[AllNPCsLoadedEvent](npc.md), [LoadedNPCEvent](npc.md), [BuilderInfo](npc.md), [SensorEvent](npc.md), [SensorEntityEvent](npc.md), [EventSearchType](npc.md)

**Adventure**
[DiscoverInstanceEvent](adventure.md), [DiscoverZoneEvent](adventure.md), [TreasureChestOpeningEvent](adventure.md), [InstanceDiscoveryConfig](adventure.md), [WorldMapTracker](adventure.md), [ZoneDiscoveryInfo](adventure.md)

**Asset Editor**
[EditorClientEvent](asset-editor.md), [AssetEditorActivateButtonEvent](asset-editor.md), [AssetEditorAssetCreatedEvent](asset-editor.md), [AssetEditorClientDisconnectEvent](asset-editor.md), [AssetEditorSelectAssetEvent](asset-editor.md), [AssetEditorFetchAutoCompleteDataEvent](asset-editor.md), [AssetEditorRequestDataSetEvent](asset-editor.md)

**Localization**
[GenerateDefaultLanguageEvent](i18n.md)

**Singleplayer**
[SingleplayerRequestAccessEvent](singleplayer.md)

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
