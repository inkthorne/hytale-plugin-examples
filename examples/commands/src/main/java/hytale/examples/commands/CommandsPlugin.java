package hytale.examples.commands;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

public class CommandsPlugin extends JavaPlugin {

    public CommandsPlugin(JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        getCommandRegistry().registerCommand(new HelloCommand());
        getCommandRegistry().registerCommand(new TeleportCommand());

        getLogger().atInfo().log("CommandsExample plugin loaded!");
    }
}
