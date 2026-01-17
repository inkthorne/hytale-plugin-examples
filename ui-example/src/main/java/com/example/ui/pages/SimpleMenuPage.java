package com.example.ui.pages;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.player.pages.BasicCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

/**
 * A simple custom UI page loaded from a .ui file.
 */
public class SimpleMenuPage extends BasicCustomUIPage {

    public SimpleMenuPage(PlayerRef playerRef) {
        super(playerRef, CustomPageLifetime.CanDismiss);
    }

    @Override
    public void build(UICommandBuilder cmd) {
        // Load UI from .ui file (path relative to Common/UI/Custom/)
        cmd.append("SimpleMenuPage.ui");
    }

    @Override
    public void handleDataEvent(Ref<EntityStore> ref, Store<EntityStore> store, String data) {
        PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());

        if ("close".equals(data)) {
            // Page will be dismissed automatically
        } else if (data != null && data.startsWith("option:")) {
            String option = data.substring(7);
            playerRef.sendMessage(Message.raw("You selected option: " + option));
        }
    }

    @Override
    public void onDismiss(Ref<EntityStore> ref, Store<EntityStore> store) {
        PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
        playerRef.sendMessage(Message.raw("Menu closed"));
    }
}
