package io.github.tootertutor.eventhorizons.commands;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public class CommandBuilder {
    private final Map<String, CommandExecutor> commands = new HashMap<>();
    public CommandBuilder(Plugin plugin) {
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
            // Remove the subcommand from the args array
            String[] newArgs = Arrays.copyOfRange(args, 1, args.length);
            return executor.onCommand(sender, command, label, newArgs);
        }
        return false; // Command not found
    }
}
