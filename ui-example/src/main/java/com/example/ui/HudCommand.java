package com.example.ui;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.HudComponent;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

/**
 * Toggles HUD components on/off.
 * Usage: /hud <show|hide>
 */
public class HudCommand extends AbstractPlayerCommand {

    private enum Mode { show, hide }

    private final RequiredArg<Mode> modeArg;

    public HudCommand() {
        super("hud", "Toggle HUD visibility");
        modeArg = withRequiredArg("mode", "show or hide", ArgTypes.forEnum("mode", Mode.class));
    }

    @Override
    protected void execute(CommandContext ctx, Store<EntityStore> store,
                          Ref<EntityStore> ref, PlayerRef playerRef, World world) {
        Mode mode = ctx.get(modeArg);
        Player player = store.getComponent(ref, Player.getComponentType());

        if (mode == Mode.show) {
            player.getHudManager().setVisibleHudComponents(playerRef,
                HudComponent.Hotbar,
                HudComponent.Health,
                HudComponent.Reticle);
            playerRef.sendMessage(Message.raw("HUD components shown"));
        } else {
            player.getHudManager().setVisibleHudComponents(playerRef);
            playerRef.sendMessage(Message.raw("HUD components hidden"));
        }
    }
}
