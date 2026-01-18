package hytale.examples.inventory;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

public class InventoryPlugin extends JavaPlugin {

    public InventoryPlugin(JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        getCommandRegistry().registerCommand(new GiveCommand());
        getCommandRegistry().registerCommand(new ClearCommand());
        getCommandRegistry().registerCommand(new InspectCommand());
        getCommandRegistry().registerCommand(new SortCommand());

        getLogger().atInfo().log("InventoryExample plugin loaded!");
    }
}
