# Permissions API

## PermissionHolder
**Package:** `com.hypixel.hytale.server.core.permissions`

Interface for entities that can have permissions. `Player` implements this.

### Methods
```java
boolean hasPermission(String permission)
boolean hasPermission(String permission, boolean defaultValue)
```

## Usage

### Check Permission in Command
```java
@Override
protected void execute(CommandContext ctx, Store<EntityStore> store,
                      Ref<EntityStore> ref, PlayerRef playerRef, World world) {
    Player player = store.getComponent(ref, Player.getComponentType());

    if (player.hasPermission("myplugin.admin")) {
        // Admin-only action
        playerRef.sendMessage(Message.raw("Admin access granted"));
    } else {
        playerRef.sendMessage(Message.raw("Permission denied"));
    }
}
```

### With Default Value
```java
// Returns true if permission not explicitly set
boolean canUse = player.hasPermission("myplugin.feature", true);

// Returns false if permission not explicitly set
boolean isAdmin = player.hasPermission("myplugin.admin", false);
```

## Command Permissions

Commands can require permissions using `AbstractCommand`:

```java
public class AdminCommand extends AbstractPlayerCommand {
    public AdminCommand() {
        super("admin", "Admin-only command");
        requirePermission("myplugin.admin");  // Require this permission
    }
}
```

Players without the required permission won't be able to execute the command.

---

## Permission Events

**Package:** `com.hypixel.hytale.server.core.event.events.permissions`

Events related to permission changes for players and groups.

### Event Summary

| Class | Description |
|-------|-------------|
| `PlayerGroupEvent` | Player group changes (has `Added` and `Removed` variants) |
| `PlayerPermissionChangeEvent` | Player permissions change |
| `GroupPermissionChangeEvent` | Group permissions change |

---

### PlayerGroupEvent

Fired when a player's group membership changes.

**Variants:**
- `PlayerGroupEvent.Added` - Player added to a group
- `PlayerGroupEvent.Removed` - Player removed from a group

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getPlayerRef()` | `PlayerRef` | The player whose group changed |
| `getGroup()` | `String` | The group being added/removed |

---

### PlayerPermissionChangeEvent

Fired when a player's individual permissions change.

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getPlayerRef()` | `PlayerRef` | The player whose permissions changed |
| `getPermission()` | `String` | The permission that changed |
| `getValue()` | `Boolean` | The new permission value (true/false/null) |

---

### GroupPermissionChangeEvent

Fired when a permission group's permissions change.

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getGroup()` | `String` | The group whose permissions changed |
| `getPermission()` | `String` | The permission that changed |
| `getValue()` | `Boolean` | The new permission value (true/false/null) |

---

### Permission Events Usage Example

```java
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.event.events.permissions.*;

@Override
protected void setup() {
    // Listen for player group additions
    getEventRegistry().register(PlayerGroupEvent.Added.class, event -> {
        var playerRef = event.getPlayerRef();
        var group = event.getGroup();
        playerRef.sendMessage(Message.raw("You were added to group: " + group));
    });

    // Listen for player group removals
    getEventRegistry().register(PlayerGroupEvent.Removed.class, event -> {
        var playerRef = event.getPlayerRef();
        var group = event.getGroup();
        playerRef.sendMessage(Message.raw("You were removed from group: " + group));
    });

    // Listen for permission changes
    getEventRegistry().register(PlayerPermissionChangeEvent.class, event -> {
        var playerRef = event.getPlayerRef();
        var permission = event.getPermission();
        var value = event.getValue();
        System.out.println("Player " + playerRef.getUsername() +
                          " permission " + permission + " changed to " + value);
    });

    // Listen for group permission changes
    getEventRegistry().register(GroupPermissionChangeEvent.class, event -> {
        var group = event.getGroup();
        var permission = event.getPermission();
        var value = event.getValue();
        System.out.println("Group " + group +
                          " permission " + permission + " changed to " + value);
    });
}
