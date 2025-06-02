package io.github.tootertutor.eventhorizons.commands;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import io.github.tootertutor.eventhorizons.utils.ColorGradientUtil;
import io.github.tootertutor.eventhorizons.utils.ColorGradientUtil.GradientInfo;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

/**
 * Command to set the display name of the item in the player's main hand.
 * Supports optional color or color gradient.
 */
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

        // Parse name and optional color or gradient
        String[] parsed = parseNameAndColor(args);
        String name = parsed[0];
        String colorOrGradient = parsed[1];

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            sender.sendMessage(Component.text("This item cannot have a custom name.", NamedTextColor.RED));
            return true;
        }

        GradientInfo gradientInfo = ColorGradientUtil.parseGradientString(colorOrGradient);
        if (gradientInfo != null) {
            // Apply gradient
            List<Component> gradientComponents = ColorGradientUtil.applyGradient(name, gradientInfo);
            // Updated to use non-deprecated join method with JoinConfiguration and separator
            meta.displayName(Component.join(JoinConfiguration.noSeparators(), gradientComponents));
        } else {
            // Apply single color
            String normalizedColor = normalizeColorCode(colorOrGradient);
            meta.displayName(Component.text(name).color(TextColor.fromHexString(normalizedColor)));
        }

        item.setItemMeta(meta);

        sender.sendMessage(Component.text("Updated item name.", NamedTextColor.GREEN));
        return true;
    }

    private String[] parseNameAndColor(String[] args) {
        String[] result = new String[2];
        StringBuilder nameBuilder = new StringBuilder();
        String potentialColor = null;

        // Check if last argument is a color code or gradient syntax
        if (args.length >= 2) {
            String lastArg = args[args.length - 1];
            // Fix: trim lastArg to remove trailing spaces that may affect parsing
            lastArg = lastArg.trim();
            if (isValidColorCode(lastArg) || ColorGradientUtil.parseGradientString(lastArg) != null) {
                potentialColor = lastArg;
                // Process all arguments except the last one as name
                for (int i = 0; i < args.length - 1; i++) {
                    nameBuilder.append(args[i]).append(" ");
                }
            }
        }

        // If no color or gradient found, process all arguments as name
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
        result[1] = potentialColor;
        return result;
    }

    private boolean isValidColorCode(String input) {
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
        sender.sendMessage(Component.text("Usage: /eh name \"<name>\" [#color|#start-#end [>,<,<>,><]]", NamedTextColor.RED));
    }
}
