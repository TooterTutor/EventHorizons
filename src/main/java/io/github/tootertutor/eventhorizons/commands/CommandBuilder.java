package io.github.tootertutor.eventhorizons.commands;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public class CommandBuilder {
    private final Map<String, CommandExecutor> commands = new HashMap<>();
    private final Plugin plugin;

    public CommandBuilder(Plugin plugin) {
        this.plugin = plugin;
    }

    public void registerCommand(String name, CommandExecutor executor) {
        commands.put(name.toLowerCase(), executor);
    }

    public boolean executeCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            return false; // No subcommand provided
        }

        String subCommand = args[0].toLowerCase();
        CommandExecutor executor = commands.get(subCommand);
        if (executor != null) {
            return executor.onCommand(sender, command, label, args);
        }
        return false; // Command not found
    }
}
