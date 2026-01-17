# Blocks API

## BlockStateRegistry
**Package:** `com.hypixel.hytale.server.core.universe.world.meta`

Register custom block states. Access via `getBlockStateRegistry()` in your plugin.

### Methods
```java
// Register a simple block state
<T extends BlockState> BlockStateRegistration registerBlockState(
    Class<T> stateClass,
    String name,
    Codec<T> codec
)

// Register block state with associated data
<T extends BlockState, D extends StateData> BlockStateRegistration registerBlockState(
    Class<T> stateClass,
    String name,
    Codec<T> stateCodec,
    Class<D> dataClass,
    Codec<D> dataCodec
)
```

---

## BlockState Interface

Custom block states must implement the `BlockState` interface.

```java
public interface BlockState {
    // Implement your state logic
}
```

---

## StateData Interface

For blocks that need additional persistent data beyond basic state.

```java
public interface StateData {
    // Implement your data storage
}
```

---

## BlockStateRegistration
**Package:** `com.hypixel.hytale.server.core.universe.world.meta`

Registration handle returned by `registerBlockState()`. Extends `Registration`.

```java
public class BlockStateRegistration extends Registration {
    // Get the registered BlockState class
    Class<? extends BlockState> getBlockStateClass()
}
```

---

## BlockType
**Package:** `com.hypixel.hytale.server.core.asset.type.blocktype.config`

Core class representing a block type configuration. Provides access to all block properties including material, textures, sounds, and behavior settings.

### Constants
```java
static final BlockType EMPTY;      // Empty/air block
static final BlockType UNKNOWN;    // Unknown block placeholder
static final BlockType DEBUG_CUBE; // Debug cube block
static final BlockType DEBUG_MODEL;// Debug model block

static final String EMPTY_KEY;     // Key for empty block
static final String UNKNOWN_KEY;   // Key for unknown block
static final int EMPTY_ID;         // ID for empty block
static final int UNKNOWN_ID;       // ID for unknown block
```

### Static Methods
```java
// Get block from string identifier
static BlockType fromString(String id)

// Access the block asset store
static AssetStore<String, BlockType, ...> getAssetStore()
static BlockTypeAssetMap<String, BlockType> getAssetMap()

// Get unknown block for a specific key
static BlockType getUnknownFor(String key)

// Get block ID with fallback to unknown
static int getBlockIdOrUnknown(String key, String context, Object... args)
```

### Core Properties
```java
String getId()                    // Block identifier
String getGroup()                 // Block group/category
boolean isUnknown()               // Check if this is an unknown block
boolean isState()                 // Check if this is a block state
Item getItem()                    // Get associated item (if any)
```

### Material & Rendering
```java
BlockMaterial getMaterial()       // Get block material (Empty/Solid)
DrawType getDrawType()            // How the block is drawn
Opacity getOpacity()              // Block opacity
BlockFlags getFlags()             // Block flags (various properties)
ColorLight getLight()             // Light emission
```

### Textures & Model
```java
BlockTypeTextures[] getTextures() // Block textures
String getCustomModel()           // Custom model path (if any)
float getCustomModelScale()       // Custom model scale
String getCustomModelAnimation()  // Custom model animation
CustomModelTexture[] getCustomModelTexture()
```

### Sounds & Particles
```java
String getBlockSoundSetId()       // Sound set identifier
int getBlockSoundSetIndex()       // Sound set index
ModelParticle[] getParticles()    // Particle effects
String getBlockParticleSetId()    // Particle set identifier
Color getParticleColor()          // Particle color
String getBlockBreakingDecalId()  // Breaking decal texture
```

### Rotation & Placement
```java
Rotation getRotationYawPlacementOffset()    // Rotation offset when placed
RandomRotation getRandomRotation()          // Random rotation settings
VariantRotation getVariantRotation()        // Variant rotation settings
BlockFlipType getFlipType()                 // Flip behavior
BlockPlacementSettings getPlacementSettings()// Placement rules
```

### Collision & Interaction
```java
String getHitboxType()                      // Collision hitbox type
int getHitboxTypeIndex()                    // Collision hitbox index
String getInteractionHitboxType()           // Interaction hitbox type
int getInteractionHitboxTypeIndex()         // Interaction hitbox index
String getInteractionHint()                 // UI interaction hint
boolean isTrigger()                         // Is this a trigger block
int getDamageToEntities()                   // Damage dealt to entities
Map<InteractionType, String> getInteractions()// Interaction mappings
```

### Block States
```java
BlockType getBlockForState(String state)    // Get block for named state
String getBlockKeyForState(String state)    // Get block key for state
String getDefaultStateKey()                 // Default state key
String getStateForBlock(BlockType block)    // Get state name for block
String getStateForBlock(String blockKey)    // Get state name for key
StateData getState()                        // Get state data config
```

### Movement & Support
```java
BlockMovementSettings getMovementSettings() // Movement properties
SupportDropType getSupportDropType()        // Support drop behavior
int getMaxSupportDistance()                 // Max support distance
boolean isFullySupportive()                 // Fully supports neighbors
boolean hasSupport()                        // Has support requirements
Map<BlockFace, RequiredBlockFaceSupport[]> getSupport(int rotation)
Map<BlockFace, BlockFaceSupport[]> getSupporting(int rotation)
```

### Other Properties
```java
ConnectedBlockRuleSet getConnectedBlockRuleSet()
RotatedMountPointsArray getSeats()          // Seat mount points
RotatedMountPointsArray getBeds()           // Bed mount points
TickProcedure getTickProcedure()            // Tick behavior
ShaderType[] getEffect()                    // Shader effects
Bench getBench()                            // Crafting bench data
BlockGathering getGathering()               // Gathering/farming data
FarmingData getFarming()                    // Farming configuration
Holder<ChunkStore> getBlockEntity()         // Block entity template
RailConfig getRailConfig(int rotation)      // Rail configuration
boolean isDoor()                            // Is this a door block
boolean canBePlacedAsDeco()                 // Can be deco placement
void getBlockCenter(int rotation, Vector3d out)// Get block center
```

---

## BlockMaterial
**Package:** `com.hypixel.hytale.protocol`

Simple enum representing the physical material type of a block.

```java
public enum BlockMaterial {
    Empty,  // No collision/air
    Solid   // Solid block with collision
}
```

### Methods
```java
int getValue()                              // Get numeric value
static BlockMaterial fromValue(int value)   // Get from numeric value
static BlockMaterial[] values()             // All values
static BlockMaterial valueOf(String name)   // Get by name
```

---

## Rotation
**Package:** `com.hypixel.hytale.server.core.asset.type.blocktype.config`

Enum representing 90-degree rotation increments around an axis.

```java
public enum Rotation {
    None,       // 0 degrees
    Ninety,     // 90 degrees
    OneEighty,  // 180 degrees
    TwoSeventy  // 270 degrees
}
```

### Constants
```java
static final Rotation[] VALUES; // All rotation values
static final Rotation[] NORMAL; // Normal rotations subset
```

### Methods
```java
int getDegrees()                 // Get rotation in degrees (0, 90, 180, 270)
double getRadians()              // Get rotation in radians
Axis getAxisOfAlignment()        // Get alignment axis
Vector3i getAxisDirection()      // Get axis direction vector

// Rotation operations
Rotation flip()                  // Flip rotation
Rotation flip(Axis axis)         // Flip around axis
Rotation add(Rotation other)     // Add rotations
Rotation subtract(Rotation other)// Subtract rotations

// Vector rotation methods
Vector3i rotateX(Vector3i v, Vector3i out)
Vector3f rotateX(Vector3f v, Vector3f out)
Vector3d rotateX(Vector3d v, Vector3d out)
Vector3i rotateY(Vector3i v, Vector3i out)
Vector3f rotateY(Vector3f v, Vector3f out)
Vector3d rotateY(Vector3d v, Vector3d out)
Vector3i rotateZ(Vector3i v, Vector3i out)
Vector3f rotateZ(Vector3f v, Vector3f out)
Vector3d rotateZ(Vector3d v, Vector3d out)
Vector3i rotateYaw(Vector3i v, Vector3i out)
Vector3f rotateYaw(Vector3f v, Vector3f out)
Vector3i rotatePitch(Vector3i v, Vector3i out)
Vector3f rotatePitch(Vector3f v, Vector3f out)

// Static rotation methods
static Rotation ofDegrees(int degrees)           // Get from degrees
static Rotation closestOfDegrees(float degrees)  // Closest to degrees
static Rotation add(Rotation a, Rotation b)      // Add two rotations
static Vector3i rotate(Vector3i v, Rotation yaw, Rotation pitch)
static Vector3i rotate(Vector3i v, Rotation yaw, Rotation pitch, Rotation roll)
static Vector3f rotate(Vector3f v, Rotation yaw, Rotation pitch, Rotation roll)
static Vector3d rotate(Vector3d v, Rotation yaw, Rotation pitch, Rotation roll)
```

---

## RotationTuple
**Package:** `com.hypixel.hytale.server.core.asset.type.blocktype.config`

Java record combining yaw, pitch, and roll rotations. Used for block placement rotation (see `PlaceBlockEvent.getRotation()`).

```java
public record RotationTuple(int index, Rotation yaw, Rotation pitch, Rotation roll) {
}
```

### Constants
```java
static final RotationTuple NONE;       // No rotation (all None)
static final int NONE_INDEX;           // Index of NONE
static final RotationTuple[] VALUES;   // All possible rotation tuples
```

### Factory Methods
```java
// Create from components
static RotationTuple of(Rotation yaw, Rotation pitch, Rotation roll)
static RotationTuple of(Rotation yaw, Rotation pitch)  // roll = None

// Get by index
static RotationTuple get(int index)

// Compute index from components
static int index(Rotation yaw, Rotation pitch, Rotation roll)
```

### Record Components (Accessors)
```java
int index()        // Pre-computed index
Rotation yaw()     // Yaw rotation
Rotation pitch()   // Pitch rotation
Rotation roll()    // Roll rotation
```

### Methods
```java
// Apply rotation to vector
Vector3d rotate(Vector3d v)

// Get rotation from array
static RotationTuple getRotation(RotationTuple[] rotations,
                                  RotationTuple tuple, Rotation yaw)
```

### Usage Example
```java
// In a PlaceBlockEvent handler
PlaceBlockEvent event = ...;
RotationTuple rotation = event.getRotation();

// Access individual components
Rotation yaw = rotation.yaw();
Rotation pitch = rotation.pitch();
Rotation roll = rotation.roll();

// Modify rotation
RotationTuple newRotation = RotationTuple.of(
    Rotation.Ninety,
    Rotation.None,
    Rotation.None
);
event.setRotation(newRotation);
```

---

## World Block Access

### Via World and Chunks
```java
// Get chunk key from block coordinates
long chunkKey = ...; // Calculate from world position

// Get chunk if loaded (returns null if not loaded)
WorldChunk chunk = world.getChunkIfLoaded(chunkKey);

// Get chunk if in memory (non-ticking)
WorldChunk chunk = world.getChunkIfInMemory(chunkKey);

// Get chunk asynchronously
CompletableFuture<WorldChunk> futureChunk = world.getChunkAsync(chunkKey);
futureChunk.thenAccept(chunk -> {
    // Work with chunk
});
```

### Chunk Access Methods
```java
// Synchronous access
WorldChunk loadChunkIfInMemory(long chunkKey)
WorldChunk getChunkIfInMemory(long chunkKey)
WorldChunk getChunkIfLoaded(long chunkKey)
WorldChunk getChunkIfNonTicking(long chunkKey)

// Asynchronous access
CompletableFuture<WorldChunk> getChunkAsync(long chunkKey)
CompletableFuture<WorldChunk> getNonTickingChunkAsync(long chunkKey)
```

---

## Block Events

Handle block interactions through the event system. All block events are ECS events and should be handled using `EntityEventSystem`.

**Package:** `com.hypixel.hytale.server.core.event.events.ecs`

### Event Summary

| Class | Description | Cancellable |
|-------|-------------|-------------|
| `PlaceBlockEvent` | Block is placed | Yes |
| `BreakBlockEvent` | Block is broken | Yes |
| `DamageBlockEvent` | Block takes damage (mining progress) | Yes |
| `UseBlockEvent.Pre` | Before block is used/interacted with | Yes |
| `UseBlockEvent.Post` | After block is used/interacted with | No |

---

### PlaceBlockEvent

Fired when a block is placed.

```java
public class PlaceBlockEvent extends CancellableEcsEvent {
    ItemStack getItemInHand()
    Vector3i getTargetBlock()
    void setTargetBlock(Vector3i position)
    RotationTuple getRotation()
    void setRotation(RotationTuple rotation)
    boolean isCancelled()
    void setCancelled(boolean)
}
```

---

### BreakBlockEvent

Fired when a block is broken.

```java
public class BreakBlockEvent extends CancellableEcsEvent {
    ItemStack getItemInHand()
    Vector3i getTargetBlock()
    BlockType getBlockType()
    void setTargetBlock(Vector3i position)
    boolean isCancelled()
    void setCancelled(boolean)
}
```

---

### DamageBlockEvent

Fired when a block takes damage (mining progress). This fires during the mining process before the block is actually broken.

```java
public class DamageBlockEvent extends CancellableEcsEvent {
    ItemStack getItemInHand()
    Vector3i getTargetBlock()
    BlockType getBlockType()
    boolean isCancelled()
    void setCancelled(boolean)
}
```

#### DamageBlockEvent Usage

```java
import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.ecs.DamageBlockEvent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public class DamageBlockEventSystem extends EntityEventSystem<EntityStore, DamageBlockEvent> {

    public DamageBlockEventSystem() {
        super(DamageBlockEvent.class);
    }

    @Override
    public void handle(int index, ArchetypeChunk<EntityStore> chunk,
                       Store<EntityStore> store, CommandBuffer<EntityStore> buffer,
                       DamageBlockEvent event) {
        Player player = chunk.getComponent(index, Player.getComponentType());
        if (player != null) {
            // Could log mining progress or modify damage
            var blockType = event.getBlockType();
            var pos = event.getTargetBlock();
            System.out.println("Mining " + blockType + " at " + pos);
        }
    }

    @Override
    public Query<EntityStore> getQuery() {
        return Player.getComponentType();
    }
}
```

---

### UseBlockEvent

Fired when a block is used/interacted with. Has `Pre` and `Post` variants.

#### UseBlockEvent.Pre

Fired before the block interaction is processed. Can be cancelled.

```java
public class UseBlockEvent.Pre extends CancellableEcsEvent {
    Vector3i getTargetBlock()
    BlockType getBlockType()
    ItemStack getItemInHand()
    boolean isCancelled()
    void setCancelled(boolean)
}
```

#### UseBlockEvent.Post

Fired after the block interaction is processed. Cannot be cancelled.

```java
public class UseBlockEvent.Post extends EcsEvent {
    Vector3i getTargetBlock()
    BlockType getBlockType()
    ItemStack getItemInHand()
}
```

#### UseBlockEvent Usage

```java
import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.ecs.UseBlockEvent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public class UseBlockPreSystem extends EntityEventSystem<EntityStore, UseBlockEvent.Pre> {

    public UseBlockPreSystem() {
        super(UseBlockEvent.Pre.class);
    }

    @Override
    public void handle(int index, ArchetypeChunk<EntityStore> chunk,
                       Store<EntityStore> store, CommandBuffer<EntityStore> buffer,
                       UseBlockEvent.Pre event) {
        Player player = chunk.getComponent(index, Player.getComponentType());
        if (player != null) {
            // Prevent using certain block types
            // event.setCancelled(true);
            player.sendMessage(Message.raw("You used a block!"));
        }
    }

    @Override
    public Query<EntityStore> getQuery() {
        return Player.getComponentType();
    }
}

---

## Usage Examples

### Register Block State
```java
@Override
protected void setup() {
    getBlockStateRegistry().registerBlockState(
        MyBlockState.class,
        "my_block_state",
        MyBlockState.CODEC
    );
}
```

### Register Block State with Data
```java
@Override
protected void setup() {
    getBlockStateRegistry().registerBlockState(
        MyBlockState.class,
        "my_block_state",
        MyBlockState.CODEC,
        MyBlockStateData.class,
        MyBlockStateData.CODEC
    );
}
```

### Handle Block Break Event
```java
// Using EntityEventSystem for ECS events
public class BlockBreakSystem extends EntityEventSystem<EntityStore, BreakBlockEvent> {
    public BlockBreakSystem() {
        super(BreakBlockEvent.class);
    }

    @Override
    public void handle(int index, ArchetypeChunk<EntityStore> chunk,
                       Store<EntityStore> store, CommandBuffer<EntityStore> buffer,
                       BreakBlockEvent event) {
        Player player = chunk.getComponent(index, Player.getComponentType());
        if (player != null) {
            Vector3i pos = event.getTargetBlock();
            player.sendMessage(Message.raw("You broke a block at " + pos.x + ", " + pos.y + ", " + pos.z));
        }
    }

    @Override
    public Query<EntityStore> getQuery() {
        return Player.getComponentType();
    }
}

// Register in setup()
@Override
protected void setup() {
    getEntityStoreRegistry().registerSystem(new BlockBreakSystem());
}
```

### Cancel Block Placement
```java
public class BlockPlaceSystem extends EntityEventSystem<EntityStore, PlaceBlockEvent> {
    public BlockPlaceSystem() {
        super(PlaceBlockEvent.class);
    }

    @Override
    public void handle(int index, ArchetypeChunk<EntityStore> chunk,
                       Store<EntityStore> store, CommandBuffer<EntityStore> buffer,
                       PlaceBlockEvent event) {
        // Cancel placement in certain conditions
        Vector3i target = event.getTargetBlock();
        if (target.y > 100) {
            event.setCancelled(true);
            Player player = chunk.getComponent(index, Player.getComponentType());
            if (player != null) {
                player.sendMessage(Message.raw("Cannot place blocks above y=100"));
            }
        }
    }

    @Override
    public Query<EntityStore> getQuery() {
        return Player.getComponentType();
    }
}
```

---

## Notes
- Block manipulation typically goes through chunk accessors
- Block states persist additional data per-block instance
- Always check if chunk is loaded before accessing blocks
- Use async chunk loading for non-critical operations to avoid blocking
- Block events are ECS events; use `EntityEventSystem` to handle them
