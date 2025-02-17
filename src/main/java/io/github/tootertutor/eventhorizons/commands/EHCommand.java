package io.github.tootertutor.eventhorizons.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import io.github.tootertutor.eventhorizons.EventHorizons;
import io.github.tootertutor.eventhorizons.items.ItemRegistry;

public class EHCommand implements CommandExecutor, TabCompleter {
    private final CommandBuilder commandBuilder;
    private final EventHorizons plugin;

    public EHCommand(EventHorizons plugin) {
        this.plugin = plugin;
        commandBuilder = new CommandBuilder(plugin);
        registerCommands();
    }

    private void registerCommands() {
        commandBuilder.registerCommand("give", new GiveCommand(plugin));
        commandBuilder.registerCommand("lore", new LoreCommand(plugin));
        commandBuilder.registerCommand("name", new NameCommand(plugin));
        commandBuilder.registerCommand("meta", new MetaCommand(plugin));
        commandBuilder.registerCommand("dump", new DumpCommand(plugin));
        // commandBuilder.registerCommand("world", new WorldCommand(plugin));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        ItemRegistry itemRegistry = EventHorizons.getInstance().getItemRegistry(); // Correctly call on EventHorizons
        itemRegistry.getItems();
        return commandBuilder.executeCommand(sender, command, label, args);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            // String[] subCommands = { "give", "lore", "name", "meta", "dump", "world" };
            String[] subCommands = { "give", "lore", "name", "meta", "dump"};
            return filterCompletions(subCommands, args[0]);
        }

        if (args.length == 2) {
            switch (args[0].toLowerCase()) {
                case "give":
                    return null; // Return null to show online players
                case "meta":
                    return filterCompletions(new String[] { "set", "remove" }, args[1]);
                // case "world":
                //     return filterCompletions(new String[] { "create", "delete", "teleport" }, args[1]);
            }
        }

        if (args.length == 3) {
            switch (args[0].toLowerCase()) {
                case "give":
                    return EventHorizons.getInstance().getItemRegistry().getItems().stream()
                            .map(item -> item.getId().getKey()) // Use getId() to get item identifiers as String
                            .filter(id -> id.toLowerCase().startsWith(args[2].toLowerCase()))
                            .collect(Collectors.toList());
                // case "world":
                //     if (args[1].equalsIgnoreCase("create")) {
                //         return filterCompletions(new String[] { "flat", "normal", "void" }, args[2]);
                //     }
            }
        }
        return completions;
    }

    private List<String> filterCompletions(String[] options, String input) {
        return Arrays.stream(options)
                .filter(option -> option.toLowerCase().startsWith(input.toLowerCase()))
                .collect(Collectors.toList());
    }
}
