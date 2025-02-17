package io.github.tootertutor.eventhorizons;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import io.github.tootertutor.eventhorizons.commands.EHCommand;
import io.github.tootertutor.eventhorizons.items.ItemRegistry;
import io.github.tootertutor.eventhorizons.items.tools.Paxel;

public final class EventHorizons extends JavaPlugin {
    private static EventHorizons instance;
    private ItemRegistry itemRegistry;

    public void onEnable() {
        instance = this;

        getLogger().info("Initializing registries...");
        itemRegistry = new ItemRegistry(this);
        registerToolItems();

        getLogger().info("Registered items: " + itemRegistry.getItems().size());

        // Load Commands
        getLogger().info("Attempting to register commands...");
        EHCommand ehCommand = new EHCommand(this);
        PluginCommand eh = getCommand("eh");

        getLogger().info("Command lookup result: " + ((eh == null) ? "null" : "found"));
        if (eh == null) {
            getLogger().severe("Command 'eh' is not registered in the plugin.yml!");
            return;
        }
        eh.setExecutor(ehCommand);
        eh.setTabCompleter(ehCommand);

        getLogger().info("EventHorizons has been enabled!");
    }

    public void onDisable() {
        getLogger().info("EventHorizons has been disabled!");
    }

    public static EventHorizons getInstance() {
        return instance;
    }

    public ItemRegistry getItemRegistry() {
        return itemRegistry; // Return the ItemRegistry instance
    }

    public void registerToolItems() {
        Paxel paxel = new Paxel(this);
        itemRegistry.registerItem(paxel.getId(), paxel);
        Bukkit.getPluginManager().registerEvents(paxel, instance);
    }

}
