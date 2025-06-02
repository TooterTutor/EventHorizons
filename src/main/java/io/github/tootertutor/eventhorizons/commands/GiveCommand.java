package io.github.tootertutor.eventhorizons.commands;

import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import io.github.tootertutor.eventhorizons.EventHorizons;
import io.github.tootertutor.eventhorizons.items.Item;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

/**
 * Command to give a specified item to a player.
 * Usage: /eh give <player> <item> <amount>
 */
public class GiveCommand implements CommandExecutor {
    private final Plugin plugin;

    /**
     * Constructs the GiveCommand with the plugin instance.
     * @param plugin The plugin instance.
     */
    public GiveCommand(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // /eh give <player> <item> <amount>
        if (args.length < 3) {
            sender.sendMessage(Component.text("Usage: /eh give <player> <item> <amount>", NamedTextColor.RED));
            return true;
        }

        Player target = plugin.getServer().getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(Component.text("Player not found: " + args[0], NamedTextColor.RED));
            return true;
        }

        Item item = EventHorizons.getInstance().getItemRegistry().getItem(new NamespacedKey(plugin, args[1].toLowerCase()));

        if (item == null) {
            sender.sendMessage(Component.text("Item not found: " + args[1], NamedTextColor.RED));
            return true;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[2]);
            if (amount < 1)
                throw new NumberFormatException();
        } catch (NumberFormatException e) {
            sender.sendMessage(Component.text("Invalid amount: " + args[2], NamedTextColor.RED));
            return true;
        }

        // Get the item with proper text and PDC already set
        ItemStack itemStack = item.getItemStack();
        itemStack.setAmount(amount);

        // Give the item to the player
        target.getInventory().addItem(itemStack);
        sender.sendMessage(Component.text("Gave " + amount + "x " + item.getKey() + " to " + target.getName(),
                NamedTextColor.GREEN));
        return true;
    }
}
