package hytale.examples.ui.pages;

import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.server.core.entity.entities.player.pages.BasicCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;

/**
 * A simple custom UI page loaded from a .ui file.
 * This is a display-only page - press ESC to dismiss.
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
}
