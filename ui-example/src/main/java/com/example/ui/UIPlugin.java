package com.example.ui;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

public class UIPlugin extends JavaPlugin {

    public UIPlugin(JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        getCommandRegistry().registerCommand(new MenuCommand());
        getCommandRegistry().registerCommand(new HudCommand());

        getLogger().atInfo().log("UIExample plugin loaded!");
    }
}
