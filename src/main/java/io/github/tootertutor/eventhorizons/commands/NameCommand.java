package io.github.tootertutor.eventhorizons.commands;

import java.util.Arrays;

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
    private final Plugin plugin;

    public NameCommand(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // /eh name <name> [#color]
        if (!(sender instanceof Player)) {
            sender.sendMessage(Component.text("This command can only be used by players.", NamedTextColor.RED));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(Component.text("Usage: /eh name <name> [#color]", NamedTextColor.RED));
            return true;
        }

        Player player = (Player) sender;
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item == null || item.getType().isAir()) {
            sender.sendMessage(Component.text("You must be holding an item.", NamedTextColor.RED));
            return true;
        }

        // Join the name arguments and parse color
        String input = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        String[] parsed = parseColoredText(input);
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

    private String[] parseColoredText(String text) {
        String[] result = new String[2]; // [text, color]

        // Match both #RRGGBB and #RGB formats at the end of the text
        java.util.regex.Pattern pattern = java.util.regex.Pattern
                .compile("(.*?)\\s+(#[0-9A-Fa-f]{6}|#[0-9A-Fa-f]{3})\\s*$");
        java.util.regex.Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            result[0] = matcher.group(1).trim();
            String colorCode = matcher.group(2);

            // Convert 3-digit hex to 6-digit hex if needed
            if (colorCode.length() == 4) {
                String r = colorCode.substring(1, 2);
                String g = colorCode.substring(2, 3);
                String b = colorCode.substring(3, 4);
                colorCode = "#" + r + r + g + g + b + b;
            }
            result[1] = colorCode;
        } else {
            result[0] = text;
            result[1] = "#FFFFFF"; // Default white color
        }

        // Remove quotes if present
        if (result[0].startsWith("\"") && result[0].endsWith("\"")) {
            result[0] = result[0].substring(1, result[0].length() - 1);
        }

        return result;
    }
}
