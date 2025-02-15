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

public class DumpCommand implements CommandExecutor {
    private final Plugin plugin;

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
                sender.sendMessage(Component.text("Display Name: " + meta.displayName(), NamedTextColor.YELLOW));
            }

            if (meta.hasLore()) {
                sender.sendMessage(Component.text("Lore:", NamedTextColor.YELLOW));
                meta.lore().forEach(line -> sender.sendMessage(Component.text("- " + line, NamedTextColor.GRAY)));
            }

            // Additional meta data can be dumped here if needed
        }

        return true;
    }
}
