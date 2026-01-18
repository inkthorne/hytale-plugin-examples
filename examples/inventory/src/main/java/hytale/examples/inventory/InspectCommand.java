package hytale.examples.inventory;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

/**
 * Inspect current inventory contents.
 * Usage: /inspect
 * Shows: held item, hotbar summary, total items
 */
public class InspectCommand extends AbstractPlayerCommand {

    public InspectCommand() {
        super("inspect", "Show inventory contents");
    }

    @Override
    protected void execute(CommandContext ctx, Store<EntityStore> store,
                          Ref<EntityStore> ref, PlayerRef playerRef, World world) {
        Player player = store.getComponent(ref, Player.getComponentType());
        Inventory inventory = player.getInventory();

        // Show held item
        ItemStack heldItem = inventory.getItemInHand();
        if (heldItem.isEmpty()) {
            playerRef.sendMessage(Message.raw("Held item: (empty)"));
        } else {
            playerRef.sendMessage(Message.raw("Held item: " + heldItem.getItemId()
                + " x" + heldItem.getQuantity()));
        }

        // Show active hotbar slot
        byte activeSlot = inventory.getActiveHotbarSlot();
        playerRef.sendMessage(Message.raw("Active hotbar slot: " + activeSlot));

        // Count hotbar items
        ItemContainer hotbar = inventory.getHotbar();
        int hotbarCount = countItems(hotbar);
        int hotbarSlots = countNonEmptySlots(hotbar);
        playerRef.sendMessage(Message.raw("Hotbar: " + hotbarSlots + "/" + hotbar.getCapacity()
            + " slots, " + hotbarCount + " total items"));

        // Count storage items
        ItemContainer storage = inventory.getStorage();
        int storageCount = countItems(storage);
        int storageSlots = countNonEmptySlots(storage);
        playerRef.sendMessage(Message.raw("Storage: " + storageSlots + "/" + storage.getCapacity()
            + " slots, " + storageCount + " total items"));

        // Count armor items
        ItemContainer armor = inventory.getArmor();
        int armorSlots = countNonEmptySlots(armor);
        playerRef.sendMessage(Message.raw("Armor: " + armorSlots + "/" + armor.getCapacity() + " slots equipped"));

        // Total summary
        int totalItems = hotbarCount + storageCount;
        playerRef.sendMessage(Message.raw("Total items (hotbar + storage): " + totalItems));
    }

    private int countItems(ItemContainer container) {
        int[] count = {0};
        container.forEach((slot, itemStack) -> {
            if (!itemStack.isEmpty()) {
                count[0] += itemStack.getQuantity();
            }
        });
        return count[0];
    }

    private int countNonEmptySlots(ItemContainer container) {
        int[] count = {0};
        container.forEach((slot, itemStack) -> {
            if (!itemStack.isEmpty()) {
                count[0]++;
            }
        });
        return count[0];
    }
}
