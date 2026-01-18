package hytale.examples.inventory;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

/**
 * Clear inventory sections.
 * Usage: /inv-clear <section>
 * Examples:
 *   /inv-clear all - Clear entire inventory
 *   /inv-clear hotbar - Clear only hotbar
 *   /inv-clear storage - Clear only storage
 */
public class ClearCommand extends AbstractPlayerCommand {

    private final RequiredArg<String> sectionArg;

    public ClearCommand() {
        super("inv-clear", "Clear inventory sections");
        sectionArg = withRequiredArg("section", "Section to clear (all/hotbar/storage/armor/utility/tools/backpack)", ArgTypes.STRING);
    }

    @Override
    protected void execute(CommandContext ctx, Store<EntityStore> store,
                          Ref<EntityStore> ref, PlayerRef playerRef, World world) {
        Player player = store.getComponent(ref, Player.getComponentType());
        Inventory inventory = player.getInventory();

        String section = ctx.get(sectionArg).toLowerCase();

        if (section.equals("all")) {
            inventory.clear();
            playerRef.sendMessage(Message.raw("Cleared entire inventory"));
            return;
        }

        ItemContainer container = getSectionContainer(inventory, section);

        if (container == null) {
            playerRef.sendMessage(Message.raw("Unknown section: " + section
                + ". Valid sections: all, hotbar, storage, armor, utility, tools, backpack"));
            return;
        }

        container.clear();
        playerRef.sendMessage(Message.raw("Cleared " + section));
    }

    private ItemContainer getSectionContainer(Inventory inventory, String section) {
        return switch (section) {
            case "hotbar" -> inventory.getHotbar();
            case "storage" -> inventory.getStorage();
            case "armor" -> inventory.getArmor();
            case "utility" -> inventory.getUtility();
            case "tools" -> inventory.getTools();
            case "backpack" -> inventory.getBackpack();
            default -> null;
        };
    }
}
