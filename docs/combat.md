# Combat API

This document covers damage events, combat systems, and kill feed customization.

## Damage Events (DamageEventSystem)

Handle damage events when entities receive damage. Extend `DamageEventSystem` (not raw `EntityEventSystem`).

**Package:** `com.hypixel.hytale.server.core.modules.entity.damage`

### Key Classes

| Class | Description |
|-------|-------------|
| `Damage` | ECS event fired when damage occurs. Extends `CancellableEcsEvent` |
| `DamageEventSystem` | Abstract base class for handling Damage events |
| `Damage.Source` | Interface for damage sources |
| `Damage.EntitySource` | Source when damage comes from an entity (player/mob) |
| `Damage.EnvironmentSource` | Source for environmental damage (fall, drowning) |
| `Damage.ProjectileSource` | Source for projectile damage (arrows) |
| `Damage.CommandSource` | Source for damage from commands |
| `DamageCause` | Asset type for damage cause (FALL, DROWNING, PHYSICAL, etc.) |
| `DamageDataComponent` | Component on entities that can receive damage |

---

## Damage Class

The main ECS event fired when an entity takes damage.

### Methods

```java
// Get who/what caused the damage
Damage.Source getSource()

// Get damage amount
float getAmount()
float getInitialAmount()

// Get damage cause
DamageCause getCause()
int getDamageCauseIndex()

// Cancellable
boolean isCancelled()
void setCancelled(boolean)
```

### Important Notes

1. **Event fires on VICTIM**: The Damage event is invoked on the entity receiving damage, not the attacker
2. **Getting the attacker**: Use `Damage.EntitySource.getRef()` to get the attacker's entity reference
3. **getQuery() required**: Must return a valid query (not null). Use `DamageDataComponent.getComponentType()`
4. **Extend DamageEventSystem**: Use the provided base class, not raw `EntityEventSystem<EntityStore, Damage>`

---

## Damage Source Types

### Damage.Source (Interface)

Base interface for all damage sources.

### Damage.EntitySource

Source when damage comes from another entity (player or mob).

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getRef()` | `Ref<EntityStore>` | Reference to the attacking entity |

### Damage.EnvironmentSource

Source for environmental damage (fall damage, drowning, lava, etc.).

### Damage.ProjectileSource

Source for projectile damage (arrows, thrown items).

### Damage.CommandSource

Source for damage inflicted via commands.

---

## DamageCause

**Package:** `com.hypixel.hytale.server.core.modules.entity.damage`

Asset type representing the cause/type of damage. Returned by `Damage.getCause()`.

### Predefined Constants
```java
static DamageCause PHYSICAL       // Melee/physical attacks
static DamageCause PROJECTILE     // Arrow/thrown item damage
static DamageCause COMMAND        // Damage from commands
static DamageCause DROWNING       // Underwater suffocation
static DamageCause ENVIRONMENT    // Environmental hazards (lava, etc.)
static DamageCause FALL           // Fall damage
static DamageCause OUT_OF_WORLD   // Void damage
static DamageCause SUFFOCATION    // Block suffocation
```

### Methods
```java
String getId()
String getInherits()              // Parent cause for inheritance
String getAnimationId()           // Animation to play on damage
String getDeathAnimationId()      // Animation to play on death
boolean isDurabilityLoss()        // Does this cause item durability loss?
boolean isStaminaLoss()           // Does this cause stamina loss?
boolean doesBypassResistances()   // Does this ignore damage resistances?
```

### Usage Example
```java
@Override
public void handle(int index, ArchetypeChunk<EntityStore> chunk,
                   Store<EntityStore> store, CommandBuffer<EntityStore> buffer,
                   Damage event) {
    DamageCause cause = event.getCause();

    if (cause == DamageCause.FALL) {
        // Handle fall damage specially
        event.setCancelled(true);  // No fall damage
    } else if (cause == DamageCause.DROWNING) {
        // Reduce drowning damage
        System.out.println("Drowning damage: " + event.getAmount());
    }
}
```

---

## Creating a Damage Handler

```java
import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.damage.DamageDataComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageEventSystem;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public class MyDamageSystem extends DamageEventSystem {

    public MyDamageSystem() {
        super();
    }

    @Override
    public void handle(int index, ArchetypeChunk<EntityStore> chunk,
                       Store<EntityStore> store, CommandBuffer<EntityStore> buffer,
                       Damage event) {
        // NOTE: This fires on the VICTIM entity (receiving damage)

        Damage.Source source = event.getSource();
        if (source instanceof Damage.EntitySource entitySource) {
            // Get the attacker's entity reference
            Ref<EntityStore> attackerRef = entitySource.getRef();

            // Check if attacker is a player
            Player attacker = store.getComponent(attackerRef, Player.getComponentType());
            if (attacker != null) {
                attacker.sendMessage(Message.raw("You hit something for " + event.getAmount() + " damage!"));
            }
        }
    }

    @Override
    public Query<EntityStore> getQuery() {
        // Match entities that can receive damage
        return DamageDataComponent.getComponentType();
    }
}
```

### Registering the Damage System

```java
@Override
protected void setup() {
    getEntityStoreRegistry().registerSystem(new MyDamageSystem());
}
```

---

## DamageDataComponent

**Package:** `com.hypixel.hytale.server.core.entity.damage`

Component attached to entities that can receive damage. Use this in your query to match damageable entities.

```java
@Override
public Query<EntityStore> getQuery() {
    return DamageDataComponent.getComponentType();
}
```

---

## KillFeedEvent

**Package:** `com.hypixel.hytale.server.core.modules.entity.damage.event`

Container class for kill feed related events. Contains three nested event classes that fire when an entity is killed.

### KillFeedEvent.Display

ECS event fired to display the kill in the kill feed UI. Extends `CancellableEcsEvent`.

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getDamage()` | `Damage` | The damage that caused the kill |
| `getBroadcastTargets()` | `List<PlayerRef>` | Players who will see this kill feed entry |
| `getIcon()` | `String` | Icon to display in kill feed |
| `setIcon(String)` | `void` | Change the display icon |
| `isCancelled()` | `boolean` | Whether display is cancelled |
| `setCancelled(boolean)` | `void` | Cancel or uncancel the display |

### KillFeedEvent.KillerMessage

ECS event fired to send a message to the killer. Extends `CancellableEcsEvent`.

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getDamage()` | `Damage` | The damage that caused the kill |
| `getTargetRef()` | `Ref<EntityStore>` | Reference to the killed entity |
| `getMessage()` | `Message` | Message to show the killer |
| `setMessage(Message)` | `void` | Change the message |
| `isCancelled()` | `boolean` | Whether message is cancelled |
| `setCancelled(boolean)` | `void` | Cancel or uncancel the message |

### KillFeedEvent.DecedentMessage

ECS event fired to send a message to the deceased (victim). Extends `CancellableEcsEvent`.

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getDamage()` | `Damage` | The damage that caused death |
| `getMessage()` | `Message` | Message to show the deceased |
| `setMessage(Message)` | `void` | Change the message |
| `isCancelled()` | `boolean` | Whether message is cancelled |
| `setCancelled(boolean)` | `void` | Cancel or uncancel the message |

---

## KillFeedEvent Usage

Handle kill feed events using `EntityEventSystem`:

```java
import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.damage.event.KillFeedEvent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

// Customize message shown to killer
public class KillerMessageSystem extends EntityEventSystem<EntityStore, KillFeedEvent.KillerMessage> {

    public KillerMessageSystem() {
        super(KillFeedEvent.KillerMessage.class);
    }

    @Override
    public void handle(int index, ArchetypeChunk<EntityStore> chunk,
                       Store<EntityStore> store, CommandBuffer<EntityStore> buffer,
                       KillFeedEvent.KillerMessage event) {
        // Customize the kill message
        event.setMessage(Message.raw("You eliminated a target!"));
    }

    @Override
    public Query<EntityStore> getQuery() {
        return Player.getComponentType();
    }
}

// Customize or suppress kill feed display
public class KillFeedDisplaySystem extends EntityEventSystem<EntityStore, KillFeedEvent.Display> {

    public KillFeedDisplaySystem() {
        super(KillFeedEvent.Display.class);
    }

    @Override
    public void handle(int index, ArchetypeChunk<EntityStore> chunk,
                       Store<EntityStore> store, CommandBuffer<EntityStore> buffer,
                       KillFeedEvent.Display event) {
        // Change the icon or cancel the display
        event.setIcon("custom_kill_icon");

        // Or suppress kill feed entirely
        // event.setCancelled(true);
    }

    @Override
    public Query<EntityStore> getQuery() {
        return null;
    }
}
```

> **Note:** Unlike `DamageEventSystem`, `KillFeedEvent` handlers can return `null` from `getQuery()` to handle all entities.

### Registration

```java
@Override
protected void setup() {
    getEntityStoreRegistry().registerSystem(new KillerMessageSystem());
    getEntityStoreRegistry().registerSystem(new KillFeedDisplaySystem());
}
```

### Kill Feed Event Flow

When an entity is killed, the events fire in this order:
1. `KillFeedEvent.KillerMessage` - Allows customizing/cancelling the killer's notification
2. `KillFeedEvent.DecedentMessage` - Allows customizing/cancelling the victim's death message
3. `KillFeedEvent.Display` - Allows customizing/cancelling the kill feed UI broadcast

---

## Damage Handling Examples

### Cancel Damage

```java
public class NoDamageSystem extends DamageEventSystem {
    @Override
    public void handle(int index, ArchetypeChunk<EntityStore> chunk,
                       Store<EntityStore> store, CommandBuffer<EntityStore> buffer,
                       Damage event) {
        // Cancel all damage
        event.setCancelled(true);
    }

    @Override
    public Query<EntityStore> getQuery() {
        return DamageDataComponent.getComponentType();
    }
}
```

### Modify Damage Based on Source

```java
public class DamageModifierSystem extends DamageEventSystem {
    @Override
    public void handle(int index, ArchetypeChunk<EntityStore> chunk,
                       Store<EntityStore> store, CommandBuffer<EntityStore> buffer,
                       Damage event) {
        Damage.Source source = event.getSource();

        if (source instanceof Damage.EnvironmentSource) {
            // Could log environmental damage
            System.out.println("Environmental damage: " + event.getAmount());
        } else if (source instanceof Damage.ProjectileSource) {
            // Could modify projectile damage
            System.out.println("Projectile damage: " + event.getAmount());
        } else if (source instanceof Damage.EntitySource entitySource) {
            // Player or mob attack
            System.out.println("Entity damage: " + event.getAmount());
        }
    }

    @Override
    public Query<EntityStore> getQuery() {
        return DamageDataComponent.getComponentType();
    }
}
```

### Notify Attacker on Hit

```java
public class HitNotificationSystem extends DamageEventSystem {
    @Override
    public void handle(int index, ArchetypeChunk<EntityStore> chunk,
                       Store<EntityStore> store, CommandBuffer<EntityStore> buffer,
                       Damage event) {
        if (event.getSource() instanceof Damage.EntitySource entitySource) {
            Ref<EntityStore> attackerRef = entitySource.getRef();
            Player attacker = store.getComponent(attackerRef, Player.getComponentType());

            if (attacker != null) {
                float damage = event.getAmount();
                attacker.sendMessage(Message.raw("Dealt " + damage + " damage!").color("#FF6600"));
            }
        }
    }

    @Override
    public Query<EntityStore> getQuery() {
        return DamageDataComponent.getComponentType();
    }
}
```
