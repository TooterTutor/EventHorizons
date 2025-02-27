package io.github.tootertutor.eventhorizons.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

public class NameCommand implements CommandExecutor {
    private static final TextColor DEFAULT_COLOR = NamedTextColor.WHITE;
    public NameCommand(Plugin plugin) {
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Component.text("This command can only be used by players.", NamedTextColor.RED));
            return true;
        }

        if (args.length < 1) {
            sendUsage(sender);
            return true;
        }

        Player player = (Player) sender;
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item.getType().isAir()) {
            sender.sendMessage(Component.text("You must be holding an item.", NamedTextColor.RED));
            return true;
        }

        // Join all arguments except potential color code
        String[] parsed = parseNameAndColor(args);
        String name = parsed[0];
        String color = parsed[1];

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            sender.sendMessage(Component.text("This item cannot have a custom name.", NamedTextColor.RED));
            return true;
        }

        meta.displayName(Component.text(name).color(TextColor.fromHexString(color)));
        item.setItemMeta(meta);

        sender.sendMessage(Component.text("Updated item name.", NamedTextColor.GREEN));
        return true;
    }

    private String[] parseNameAndColor(String[] args) {
        String[] result = new String[2];
        StringBuilder nameBuilder = new StringBuilder();
        String potentialColor = null;

        // Check if last argument is a color code
        if (args.length >= 2) {
            String lastArg = args[args.length - 1];
            if (isValidColorCode(lastArg)) {
                potentialColor = lastArg;
                // Process all arguments except the last one as name
                for (int i = 0; i < args.length - 1; i++) {
                    nameBuilder.append(args[i]).append(" ");
                }
            }
        }

        // If no color found, process all arguments as name
        if (potentialColor == null) {
            potentialColor = DEFAULT_COLOR.asHexString();
            for (String arg : args) {
                nameBuilder.append(arg).append(" ");
            }
        }

        String rawName = nameBuilder.toString().trim();

        // Remove surrounding quotes if present
        if (rawName.startsWith("\"") && rawName.endsWith("\"")) {
            rawName = rawName.substring(1, rawName.length() - 1);
        }

        result[0] = rawName;
        result[1] = normalizeColorCode(potentialColor);
        return result;
    }

    private boolean isValidColorCode(String input) {
        // return input.matches("^#[0-9a-fA-F]{3}([0-9a-fA-F]{3})?$");
        return input.matches("^#([\\da-fA-F]{3}){1,2}?$");
    }

    private String normalizeColorCode(String color) {
        if (color.length() == 4) { // #RGB format
            return "#" + color.charAt(1) + color.charAt(1)
                    + color.charAt(2) + color.charAt(2)
                    + color.charAt(3) + color.charAt(3);
        }
        return color;
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage(Component.text("Usage: /eh name \"<name>\" [#color]", NamedTextColor.RED));
    }
}