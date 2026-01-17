# Assets API

## AssetRegistry
**Package:** `com.hypixel.hytale.server.core.plugin.registry`

Register custom assets. Access via `getAssetRegistry()` in your plugin.

### Methods
```java
// Register an asset store
<K, T extends JsonAssetWithMap<K, M>, M extends AssetMap<K, T>, S extends AssetStore<K, T, M>>
AssetRegistry register(S store)

// Shutdown (called automatically)
void shutdown()
```

---

## Related Registries

From `PluginBase`, you also have access to codec registries:

```java
// Asset registry
AssetRegistry getAssetRegistry()

// String-keyed codec registry
<T, C extends Codec<? extends T>> CodecMapRegistry<T, C>
    getCodecRegistry(StringCodecMapCodec<T, C> codec)

// Asset-keyed codec registry
<K, T extends JsonAsset<K>> CodecMapRegistry.Assets<T, ?>
    getCodecRegistry(AssetCodecMapCodec<K, T> codec)

// Map-keyed codec registry
<V> MapKeyMapRegistry<V> getCodecRegistry(MapKeyMapCodec<V> codec)
```

---

## Asset Store
**Package:** `com.hypixel.hytale.server.core.asset`

`HytaleAssetStore` - Central asset storage for the server.

```java
// Access registered assets
<K, T extends JsonAssetWithMap<K, M>, M extends AssetMap<K, T>>
T getAsset(Class<T> assetClass, K key)
```

---

## Prefab Store
**Package:** `com.hypixel.hytale.server.core.prefab`

`PrefabStore` - Store and manage entity prefabs.

See [Prefabs Documentation](prefabs.md) for detailed usage.

---

## Asset Types
**Package:** `com.hypixel.hytale.server.core.asset.type`

Common asset type configurations:

| Subpackage | Description |
|------------|-------------|
| `item/` | Item definitions and properties |
| `blocktype/` | Block type configurations |
| `model/` | 3D model definitions |
| `particle/` | Particle effect configurations |
| `gameplay/` | Gameplay configuration assets |

---

## Model
**Package:** `com.hypixel.hytale.server.core.asset.type.model.config`

Represents a 3D model configuration for entities, items, and projectiles.

**Implements:** `NetworkSerializable<Model>`

### Constants
```java
static final String UNKNOWN_TEXTURE;  // Fallback texture ID
```

### Key Methods
```java
// Identity
String getModelAssetId()        // Asset ID reference
String getModel()               // Model file path
String getTexture()             // Texture file path

// Scale and transforms
float getScale()

// Bounding boxes
Box getBoundingBox()
Box getBoundingBox(MovementStates states)
Box getCrouchBoundingBox()

// Eye height
float getEyeHeight()
float getEyeHeight(Ref<EntityStore> ref, ComponentAccessor<EntityStore> accessor)
float getCrouchOffset()

// Gradients (color variations)
String getGradientSet()
String getGradientId()

// Attachments (items, accessories)
ModelAttachment[] getAttachments()
Map<String, String> getRandomAttachmentIds()

// Animations
Map<String, ModelAsset.AnimationSet> getAnimationSetMap()
String getFirstBoundAnimationId(String... animationNames)
String getFirstBoundAnimationId(String set, String name)

// Visual effects
ColorLight getLight()           // Emissive lighting
ModelParticle[] getParticles()  // Particle effects
ModelTrail[] getTrails()        // Trail effects
CameraSettings getCamera()      // Camera configuration

// Physics
PhysicsValues getPhysicsValues()

// Detail boxes (hitboxes, selection boxes)
Map<String, DetailBox[]> getDetailBoxes()

// Phobia settings (accessibility)
Phobia getPhobia()
String getPhobiaModelAssetId()

// Network
Model toPacket()
Model.ModelReference toReference()
```

### Static Factory Methods
```java
// Create models from ModelAsset with different scaling
static Model createRandomScaleModel(ModelAsset asset)
static Model createStaticScaledModel(ModelAsset asset, float scale)
static Model createUnitScaleModel(ModelAsset asset)
static Model createUnitScaleModel(ModelAsset asset, Box boundingBox)
static Model createScaledModel(ModelAsset asset, float scale)
static Model createScaledModel(ModelAsset asset, float scale, Map<String, String> attachments)
static Model createScaledModel(ModelAsset asset, float scale, Map<String, String> attachments, Box boundingBox)
static Model createScaledModel(ModelAsset asset, float scale, Map<String, String> attachments, Box boundingBox, boolean flag)
```

### Usage Example
```java
// Get model from a projectile config
ProjectileConfig config = ProjectileConfig.getAssetMap().get("arrow");
Model model = config.getModel();

// Access model properties
float scale = model.getScale();
Box bounds = model.getBoundingBox();
String texture = model.getTexture();

// Get particle effects
ModelParticle[] particles = model.getParticles();

// Check animations
Map<String, ModelAsset.AnimationSet> animations = model.getAnimationSetMap();
String idleAnim = model.getFirstBoundAnimationId("idle", "default");
```

---

## JSON Asset Pattern

Assets in Hytale typically follow a JSON-based pattern with codec serialization:

```java
public class MyAsset implements JsonAsset<String> {
    private String id;
    private String name;
    private int value;

    // Codec for serialization
    public static final Codec<MyAsset> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Codec.STRING.fieldOf("id").forGetter(MyAsset::getId),
            Codec.STRING.fieldOf("name").forGetter(MyAsset::getName),
            Codec.INT.fieldOf("value").forGetter(MyAsset::getValue)
        ).apply(instance, MyAsset::new)
    );

    public MyAsset(String id, String name, int value) {
        this.id = id;
        this.name = name;
        this.value = value;
    }

    @Override
    public String getKey() {
        return id;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public int getValue() { return value; }
}
```

---

## Asset Store Pattern

Create a custom asset store:

```java
public class MyAssetStore extends AssetStore<String, MyAsset, MyAssetMap> {
    public MyAssetStore() {
        super(MyAsset.class, MyAssetMap.class, MyAsset.CODEC);
    }
}

public class MyAssetMap extends AssetMap<String, MyAsset> {
    // Map implementation
}
```

---

## Usage Examples

### Register Custom Assets
```java
@Override
protected void setup() {
    // Register your asset store
    MyAssetStore assetStore = new MyAssetStore();
    getAssetRegistry().register(assetStore);
}
```

### Access Registered Assets
```java
// Get asset by key
MyAsset asset = assetStore.get("my_asset_id");

// Check if asset exists
if (assetStore.contains("my_asset_id")) {
    // Use asset
}
```

### Using Codec Registries
```java
@Override
protected void setup() {
    // Register a string-keyed codec
    CodecMapRegistry<MyConfig, Codec<MyConfig>> registry =
        getCodecRegistry(MyConfig.STRING_CODEC_MAP);

    // Register configurations
    registry.register("my_config", myConfigInstance);
}
```

---

## Built-in Asset Access

Access built-in Hytale assets through the server's asset store:

```java
// Get item definition
ItemDefinition itemDef = HytaleAssetStore.getItemDefinition("hytale:wooden_sword");

// Get block type
BlockType blockType = HytaleAssetStore.getBlockType("hytale:stone");
```

---

## Asset Loading

Assets are loaded during server startup:
1. Built-in assets are loaded first
2. Plugin assets are loaded during plugin `setup()` phase
3. Assets can be accessed after all plugins are set up

---

## Notes
- Assets are typically JSON-based configurations
- Register custom assets during plugin `setup()`
- Asset loading happens through codec serialization
- Use the appropriate codec type for your asset structure
- Assets persist across server restarts (stored in data files)
- Explore specific asset type packages for detailed APIs

---

## Asset Events

Events related to asset pack lifecycle, loading, and file monitoring.

### Event Summary

| Class | Package | Key Type | Description |
|-------|---------|----------|-------------|
| `AssetPackRegisterEvent` | `...core.asset` | `Void` | Asset pack registered |
| `AssetPackUnregisterEvent` | `...core.asset` | `Void` | Asset pack unregistered |
| `LoadAssetEvent` | `...core.asset` | `Void` | Assets loaded (has priority constants) |
| `GenerateSchemaEvent` | `...core.asset` | `Void` | Schema generation |
| `CommonAssetMonitorEvent` | `...core.asset.common.events` | `Void` | Common asset file monitoring |
| `SendCommonAssetsEvent` | `...core.asset.common.events` | `Void` | Async - sending assets to client |
| `PathEvent` | `...core.asset.monitor` | N/A | File path change monitoring |

---

### AssetPackRegisterEvent

**Package:** `com.hypixel.hytale.server.core.asset`

Fired when an asset pack is registered with the server.

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getAssetPack()` | `AssetPack` | The registered asset pack |

---

### AssetPackUnregisterEvent

**Package:** `com.hypixel.hytale.server.core.asset`

Fired when an asset pack is unregistered from the server.

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getAssetPack()` | `AssetPack` | The unregistered asset pack |

---

### LoadAssetEvent

**Package:** `com.hypixel.hytale.server.core.asset`

Fired during asset loading phase. Supports priority-based loading order.

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getBootStart()` | `long` | Boot start timestamp |
| `isShouldShutdown()` | `boolean` | Whether shutdown was requested |
| `getReasons()` | `List<String>` | Failure reasons |
| `failed(boolean, String)` | `void` | Mark asset loading as failed |

**Priority Constants:**
| Constant | Description |
|----------|-------------|
| `PRIORITY_LOAD_COMMON` | Load common assets first |
| `PRIORITY_LOAD_REGISTRY` | Load registry assets |
| `PRIORITY_LOAD_LATE` | Load late-stage assets |

---

### GenerateSchemaEvent

**Package:** `com.hypixel.hytale.server.core.asset`

Fired during schema generation for assets.

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getContext()` | `SchemaContext` | Schema context |
| `getVsCodeConfig()` | `BsonDocument` | VSCode config document |
| `addSchemaLink(String, List<String>, String)` | `void` | Add schema link |
| `addSchema(String, Schema)` | `void` | Add schema |

---

### CommonAssetMonitorEvent

**Package:** `com.hypixel.hytale.server.core.asset.common.events`

Extends `AssetMonitorEvent<Void>`. Fired when common asset files are changed. Constructor takes lists of created, modified, deleted, and moved paths.

---

### SendCommonAssetsEvent

**Package:** `com.hypixel.hytale.server.core.asset.common.events`

Async event fired when sending assets to clients.

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getPacketHandler()` | `PacketHandler` | Network handler |
| `getRequestedAssets()` | `Asset[]` | Assets being sent |

---

### PathEvent

**Package:** `com.hypixel.hytale.server.core.asset.monitor`

Represents a file path change event for asset monitoring.

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getEventKind()` | `EventKind` | Type of path event |
| `getTimestamp()` | `long` | Event timestamp |

---

### Asset Events Registration Example

```java
import com.hypixel.hytale.server.core.asset.*;

@Override
protected void setup() {
    // Listen for asset pack registration
    getEventRegistry().register(AssetPackRegisterEvent.class, event -> {
        System.out.println("Asset pack registered: " + event.getAssetPack());
    });

    // Listen for asset loading with priority
    getEventRegistry().register(LoadAssetEvent.PRIORITY_LOAD_LATE,
        LoadAssetEvent.class, event -> {
        if (event.isShouldShutdown()) {
            System.out.println("Asset loading aborted: " + event.getReasons());
        }
    });
}
```
