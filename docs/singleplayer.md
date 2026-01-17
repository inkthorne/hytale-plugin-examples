# Singleplayer API

Events for singleplayer-specific functionality.

---

## SingleplayerRequestAccessEvent

**Package:** `com.hypixel.hytale.server.core.modules.singleplayer`

Implements `IEvent<Void>`. Fired when singleplayer requests a specific access level.

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getAccess()` | `Access` | The requested access level |

---

## Usage Example

```java
import com.hypixel.hytale.server.core.modules.singleplayer.SingleplayerRequestAccessEvent;

@Override
protected void setup() {
    // Listen for singleplayer access requests
    getEventRegistry().register(SingleplayerRequestAccessEvent.class, event -> {
        System.out.println("Singleplayer requesting access: " + event.getAccess());
    });
}
```

---

## Notes

- This event is specific to singleplayer/local server mode
- Use this to customize behavior based on access level in singleplayer scenarios
