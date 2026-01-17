# Codecs API

Hytale uses a codec-based serialization system for data persistence, configuration, and asset loading.

## Overview
**Package:** `com.hypixel.hytale.codec`

Codecs provide type-safe serialization and deserialization of data structures to/from various formats (JSON, BSON, etc.).

---

## Core Codec Types

### Codec<T>
Base interface for all codecs.

```java
public interface Codec<T> {
    // Encode value to output
    <O> DataResult<O> encode(T value, DynamicOps<O> ops, O prefix);

    // Decode value from input
    <I> DataResult<Pair<T, I>> decode(DynamicOps<I> ops, I input);
}
```

---

### BuilderCodec<T>
**Package:** `com.hypixel.hytale.codec`

Codec with default value support for configuration loading.

```java
public interface BuilderCodec<T> extends Codec<T> {
    // Get default instance
    T getDefault();
}
```

#### Usage with Plugin Configuration
```java
public class MyConfig {
    private int maxPlayers;
    private String serverName;

    public static final BuilderCodec<MyConfig> CODEC = new BuilderCodec<>() {
        // Implement codec methods
        @Override
        public MyConfig getDefault() {
            return new MyConfig(20, "My Server");
        }
    };
}

// In plugin setup
@Override
protected void setup() {
    MyConfig config = withConfig(MyConfig.CODEC);
    // config is loaded from file or default if not exists
}
```

---

### KeyedCodec<T>
**Package:** `com.hypixel.hytale.codec`

Codec with a string key for metadata storage.

```java
public interface KeyedCodec<T> extends Codec<T> {
    String getKey();
}
```

#### Usage with ItemStack Metadata
```java
public static final KeyedCodec<MyData> MY_DATA_CODEC = new KeyedCodec<>() {
    @Override
    public String getKey() {
        return "my_data";
    }
    // Implement codec methods
};

// Store in ItemStack
ItemStack item = itemStack.withMetadata(MY_DATA_CODEC, myDataInstance);

// Retrieve from ItemStack
MyData data = itemStack.getFromMetadataOrNull(MY_DATA_CODEC);
```

---

## Codec Map Types

### StringCodecMapCodec<T, C>
Maps string keys to codec-serialized values.

```java
// Define codec map
public static final StringCodecMapCodec<MyConfig, Codec<MyConfig>> CONFIG_MAP =
    new StringCodecMapCodec<>(MyConfig.CODEC);

// Register with plugin
@Override
protected void setup() {
    CodecMapRegistry<MyConfig, Codec<MyConfig>> registry =
        getCodecRegistry(CONFIG_MAP);
    registry.register("my_config", myConfig);
}
```

---

### AssetCodecMapCodec<K, T>
Maps asset keys to asset instances.

```java
// For assets with custom key types
public static final AssetCodecMapCodec<String, MyAsset> ASSET_MAP =
    new AssetCodecMapCodec<>(MyAsset.CODEC);
```

---

### MapKeyMapCodec<V>
Maps arbitrary keys to values.

```java
public static final MapKeyMapCodec<MyValue> VALUE_MAP =
    new MapKeyMapCodec<>(MyValue.CODEC);
```

---

## Built-in Codecs

### Primitive Codecs
```java
Codec.BOOL        // boolean
Codec.BYTE        // byte
Codec.SHORT       // short
Codec.INT         // int
Codec.LONG        // long
Codec.FLOAT       // float
Codec.DOUBLE      // double
Codec.STRING      // String
```

### Collection Codecs
```java
Codec.list(elementCodec)        // List<T>
Codec.unboundedMap(keyCodec, valueCodec)  // Map<K, V>
```

### Optional Codec
```java
codec.optionalFieldOf("name", defaultValue)  // Optional with default
codec.optionalFieldOf("name")                 // Optional, may be absent
```

---

## RecordCodecBuilder

Build codecs for record-like classes:

```java
public class MyRecord {
    private final String name;
    private final int value;
    private final List<String> tags;

    public static final Codec<MyRecord> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Codec.STRING.fieldOf("name").forGetter(MyRecord::getName),
            Codec.INT.fieldOf("value").forGetter(MyRecord::getValue),
            Codec.STRING.listOf().optionalFieldOf("tags", List.of()).forGetter(MyRecord::getTags)
        ).apply(instance, MyRecord::new)
    );

    public MyRecord(String name, int value, List<String> tags) {
        this.name = name;
        this.value = value;
        this.tags = tags;
    }

    public String getName() { return name; }
    public int getValue() { return value; }
    public List<String> getTags() { return tags; }
}
```

---

## Codec Operations

### Mapping
```java
// Transform codec output
Codec<Integer> intCodec = Codec.STRING.xmap(
    Integer::parseInt,    // String -> Integer
    String::valueOf       // Integer -> String
);
```

### Field Of
```java
// Create codec for a named field
Codec<String> fieldCodec = Codec.STRING.fieldOf("my_field");
```

### Either
```java
// Try first codec, then second
Codec<Object> either = Codec.either(firstCodec, secondCodec);
```

### Dispatch
```java
// Polymorphic codec based on type field
Codec<BaseClass> dispatch = type.dispatch(
    BaseClass::getType,
    typeName -> getCodecForType(typeName)
);
```

---

## Usage Examples

### Plugin Configuration
```java
public class PluginConfig {
    private boolean enabled;
    private int maxConnections;
    private String welcomeMessage;

    public static final BuilderCodec<PluginConfig> CODEC = new BuilderCodec<>() {
        private final Codec<PluginConfig> inner = RecordCodecBuilder.create(instance ->
            instance.group(
                Codec.BOOL.optionalFieldOf("enabled", true).forGetter(c -> c.enabled),
                Codec.INT.optionalFieldOf("maxConnections", 100).forGetter(c -> c.maxConnections),
                Codec.STRING.optionalFieldOf("welcomeMessage", "Welcome!").forGetter(c -> c.welcomeMessage)
            ).apply(instance, PluginConfig::new)
        );

        @Override
        public PluginConfig getDefault() {
            return new PluginConfig(true, 100, "Welcome!");
        }

        // Delegate to inner codec
    };
}

// Load in plugin
@Override
protected void setup() {
    PluginConfig config = withConfig(PluginConfig.CODEC);
    if (config.isEnabled()) {
        // Plugin is enabled
    }
}
```

### Custom Asset with Codec
```java
public class SpellDefinition implements JsonAsset<String> {
    private final String id;
    private final String name;
    private final int manaCost;
    private final float cooldown;

    public static final Codec<SpellDefinition> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Codec.STRING.fieldOf("id").forGetter(SpellDefinition::getId),
            Codec.STRING.fieldOf("name").forGetter(SpellDefinition::getName),
            Codec.INT.fieldOf("manaCost").forGetter(SpellDefinition::getManaCost),
            Codec.FLOAT.fieldOf("cooldown").forGetter(SpellDefinition::getCooldown)
        ).apply(instance, SpellDefinition::new)
    );

    // Constructor and getters...

    @Override
    public String getKey() {
        return id;
    }
}
```

### Block State with Codec
```java
public class MyBlockState implements BlockState {
    private final int power;
    private final boolean active;

    public static final Codec<MyBlockState> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Codec.INT.fieldOf("power").forGetter(s -> s.power),
            Codec.BOOL.fieldOf("active").forGetter(s -> s.active)
        ).apply(instance, MyBlockState::new)
    );

    // Register in setup()
    // getBlockStateRegistry().registerBlockState(MyBlockState.class, "my_state", CODEC);
}
```

---

## Notes
- Codecs are immutable and thread-safe
- Use `RecordCodecBuilder` for complex data structures
- `BuilderCodec` provides defaults for missing fields
- `KeyedCodec` is ideal for ItemStack metadata
- Always provide meaningful defaults for optional fields
- Codec errors are returned as `DataResult` for safe handling
