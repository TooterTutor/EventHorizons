package io.github.tootertutor.eventhorizons.commands;

import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import io.github.tootertutor.eventhorizons.EventHorizons;
import io.github.tootertutor.eventhorizons.builders.ItemDataBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class MetaCommand implements CommandExecutor {
    private final Plugin plugin;

    public MetaCommand(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // /eh meta <set|remove> <meta> <value>
        if (!(sender instanceof Player)) {
            sender.sendMessage(Component.text("This command can only be used by players.", NamedTextColor.RED));
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage(Component.text("Usage: /eh meta <set|remove> <meta> [value]", NamedTextColor.RED));
            return true;
        }

        Player player = (Player) sender;
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item == null || item.getType().isAir()) {
            sender.sendMessage(Component.text("You must be holding an item.", NamedTextColor.RED));
            return true;
        }

        String action = args[1].toLowerCase();
        String meta = args[2];
        String value = args.length > 3 ? args[3] : null;

        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta == null) {
            sender.sendMessage(Component.text("This item cannot have meta data.", NamedTextColor.RED));
            return true;
        }

        ItemDataBuilder itemDataBuilder = new ItemDataBuilder(itemMeta, plugin);
        NamespacedKey key = new NamespacedKey(EventHorizons.getInstance(), meta);

        switch (action) {
            case "set":
                if (value == null) {
                    sender.sendMessage(Component.text("You must provide a value to set.", NamedTextColor.RED));
                    return true;
                }
                itemDataBuilder.setString(meta, value);
                sender.sendMessage(Component.text("Set meta " + meta + " to " + value, NamedTextColor.GREEN));
                break;
            case "remove":
                itemDataBuilder.setString(meta, null); // Assuming null removes the key
                sender.sendMessage(Component.text("Removed meta " + meta, NamedTextColor.GREEN));
                break;
            default:
                sender.sendMessage(Component.text("Invalid action: " + action, NamedTextColor.RED));
                return true;
        }

        item.setItemMeta(itemMeta);
        return true;
    }
}
