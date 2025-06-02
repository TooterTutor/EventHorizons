package io.github.tootertutor.eventhorizons.commands;

import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

/**
 * Command to dump information about the item in the player's main hand.
 * Displays material, amount, display name with color, lore with color, and metadata keys and values.
 */
public class DumpCommand implements CommandExecutor {
    private final Plugin plugin;

    /**
     * Constructs the DumpCommand with the plugin instance.
     * @param plugin The plugin instance.
     */
    public DumpCommand(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // /eh dump
        if (!(sender instanceof Player)) {
            sender.sendMessage(Component.text("This command can only be used by players.", NamedTextColor.RED));
            return true;
        }

        Player player = (Player) sender;
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item == null || item.getType().isAir()) {
            sender.sendMessage(Component.text("You must be holding an item.", NamedTextColor.RED));
            return true;
        }

        // Dump item information
        sender.sendMessage(Component.text("Item Information:"));
        sender.sendMessage(Component.text("Material: " + item.getType(), NamedTextColor.YELLOW));
        sender.sendMessage(Component.text("Amount: " + item.getAmount(), NamedTextColor.YELLOW));

        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            if (meta.hasDisplayName()) {
                Component displayName = meta.displayName();
                String color = getComponentColor(displayName);
                Component message = Component.text("Display Name: ", NamedTextColor.YELLOW)
                        .append(Component.text(PlainTextComponentSerializer.plainText().serialize(displayName)));

                if (color != null) {
                    message = message.append(Component.text(", "))
                            .append(createColoredSquare(color))
                            .append(Component.text(" " + color));
                }

                sender.sendMessage(message);
            }

            if (meta.hasLore()) {
                sender.sendMessage(Component.text("Lore:", NamedTextColor.YELLOW));
                meta.lore().forEach(line -> {
                    String color = getComponentColor(line);
                    Component message = Component.text("- ", NamedTextColor.GRAY)
                            .append(Component.text(PlainTextComponentSerializer.plainText().serialize(line)));

                    if (color != null) {
                        message = message.append(Component.text(", "))
                                .append(createColoredSquare(color))
                                .append(Component.text(" " + color));
                    }

                    sender.sendMessage(message);
                });
            }

            // Dump metadata
            PersistentDataContainer container = meta.getPersistentDataContainer();
            if (!container.getKeys().isEmpty()) {
                sender.sendMessage(Component.text("Metadata:", NamedTextColor.YELLOW));
                for (NamespacedKey key : container.getKeys()) {
                    Byte value = container.get(key, PersistentDataType.BYTE);
                    if (value != null) {
                        sender.sendMessage(
                                Component.text("- " + key.getKey() + ": " + value + " (Byte)", NamedTextColor.GRAY));
                    }
                }
            }
        }

        return true;
    }

    private String getComponentColor(Component component) {
        if (component.color() != null) {
            return "#" + String.format("%06X", component.color().value());
        }
        return null;
    }

    private Component createColoredSquare(String hexColor) {
        if (hexColor != null) {
            // Convert 3-digit hex to 6-digit if needed
            if (hexColor.length() == 4) {
                String r = hexColor.substring(1, 2);
                String g = hexColor.substring(2, 3);
                String b = hexColor.substring(3, 4);
                hexColor = "#" + r + r + g + g + b + b;
            }
            return Component.text("■").color(TextColor.fromHexString(hexColor));
        }
        return null;
    }
}
