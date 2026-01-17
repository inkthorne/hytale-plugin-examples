package com.example.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractTargetPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

/**
 * Admin command demonstrating permissions and player targeting.
 * Usage: /giveitem <player> <item>
 * Requires admin permission.
 */
public class GiveItemCommand extends AbstractTargetPlayerCommand {

    private final RequiredArg<Item> itemArg;

    public GiveItemCommand() {
        super("giveitem", "Give an item to a player");
        requirePermission("admin");
        itemArg = withRequiredArg("item", "Item to give", ArgTypes.ITEM_ASSET);
    }

    @Override
    protected void execute(CommandContext ctx, Ref<EntityStore> senderRef,
                          Ref<EntityStore> targetRef, PlayerRef targetPlayer,
                          World world, Store<EntityStore> store) {
        Item item = ctx.get(itemArg);

        // Note: Actual inventory manipulation would require getting the Player component
        // and using the inventory API. This is a simplified example.
        targetPlayer.sendMessage(Message.raw("You received: " + item.getId()));

        ctx.sendMessage(Message.raw("Gave " + item.getId() + " to " + targetPlayer.getUsername()));
    }
}
