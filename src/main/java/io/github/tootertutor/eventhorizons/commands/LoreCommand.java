package io.github.tootertutor.eventhorizons.commands;

import java.util.ArrayList;
import java.util.Arrays;
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
 * Command to set or remove lore lines on the item in the player's main hand.
 * Supports optional color or color gradient for lore lines.
 */
public class LoreCommand implements CommandExecutor {
    private static final TextColor DEFAULT_COLOR = NamedTextColor.WHITE;
    private final Plugin plugin;

    public LoreCommand(Plugin plugin) {
        this.plugin = plugin;
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

        // Parse line number
        int lineNumber;
        try {
            lineNumber = Integer.parseInt(args[0]);
            if (lineNumber < 0)
                throw new NumberFormatException();
        } catch (NumberFormatException e) {
            sender.sendMessage(Component.text("Invalid line number: " + args[0], NamedTextColor.RED));
            return true;
        }

        // Handle lore removal
        if (args.length >= 2 && args[1].equalsIgnoreCase("remove")) {
            return handleLoreRemoval(sender, item, lineNumber);
        }

        // Handle lore modification/addition
        String[] loreArgs = Arrays.copyOfRange(args, 1, args.length);
        String[] parsed = parseLoreAndColor(loreArgs);
        String loreText = parsed[0];
        String colorOrGradient = parsed[1];

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            sender.sendMessage(Component.text("This item cannot have lore.", NamedTextColor.RED));
            return true;
        }

        List<Component> lore = meta.hasLore() ? new ArrayList<>(meta.lore()) : new ArrayList<>();
        while (lore.size() <= lineNumber) {
            lore.add(Component.empty());
        }

        GradientInfo gradientInfo = ColorGradientUtil.parseGradientString(colorOrGradient);
        if (gradientInfo != null) {
            List<Component> gradientComponents = ColorGradientUtil.applyGradient(loreText, gradientInfo);
            lore.set(lineNumber, Component.join(JoinConfiguration.noSeparators(), gradientComponents));
        } else {
            lore.set(lineNumber, Component.text(loreText).color(TextColor.fromHexString(normalizeColorCode(colorOrGradient))));
        }

        meta.lore(lore);
        item.setItemMeta(meta);

        sender.sendMessage(Component.text("Updated item lore.", NamedTextColor.GREEN));
        return true;
    }

    private boolean handleLoreRemoval(CommandSender sender, ItemStack item, int lineNumber) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasLore()) {
            sender.sendMessage(Component.text("No lore to remove.", NamedTextColor.RED));
            return true;
        }

        List<Component> lore = new ArrayList<>(meta.lore());
        if (lineNumber >= lore.size()) {
            sender.sendMessage(Component.text("Line number out of bounds.", NamedTextColor.RED));
            return true;
        }

        lore.remove(lineNumber);
        meta.lore(lore);
        item.setItemMeta(meta);
        sender.sendMessage(Component.text("Removed lore line " + lineNumber, NamedTextColor.GREEN));
        return true;
    }

    private String[] parseLoreAndColor(String[] args) {
        String[] result = new String[2];
        StringBuilder loreBuilder = new StringBuilder();
        String potentialColor = null;

        // Check if last argument is a color code or gradient syntax
        if (args.length >= 1) {
            String lastArg = args[args.length - 1];
            if (isValidColorCode(lastArg) || ColorGradientUtil.parseGradientString(lastArg) != null) {
                potentialColor = lastArg;
                // Build lore from all arguments except last
                for (int i = 0; i < args.length - 1; i++) {
                    loreBuilder.append(args[i]).append(" ");
                }
            } else {
                // Use all arguments as lore
                potentialColor = DEFAULT_COLOR.asHexString();
                for (String arg : args) {
                    loreBuilder.append(arg).append(" ");
                }
            }
        }

        // Remove surrounding quotes
        String rawLore = loreBuilder.toString().trim();
        if (rawLore.startsWith("\"") && rawLore.endsWith("\"")) {
            rawLore = rawLore.substring(1, rawLore.length() - 1);
        }

        result[0] = rawLore;
        result[1] = potentialColor;
        return result;
    }

    private boolean isValidColorCode(String input) {
        return input.matches("^#[0-9a-fA-F]{3}([0-9a-fA-F]{3})?$");
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
        sender.sendMessage(Component.text("Usage: /eh lore <line|remove> [\"<text>\"] [#color|#start-#end [>,<,<>,><]]", NamedTextColor.RED));
    }
}
