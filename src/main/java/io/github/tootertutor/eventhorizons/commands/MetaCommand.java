package io.github.tootertutor.eventhorizons.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import io.github.tootertutor.eventhorizons.EventHorizons;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class MetaCommand implements CommandExecutor, TabCompleter {
    private final Plugin plugin;

    public MetaCommand(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player))
            return false;
        if (args.length < 1)
            return sendUsage(sender);

        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType().isAir()) {
            sender.sendMessage(Component.text("Hold an item first", NamedTextColor.RED));
            return true;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null)
            return true;

        String action = args[0].toLowerCase();
        switch (action) {
            case "set" -> handleSet(sender, meta, args);
            case "remove" -> handleRemove(sender, meta, args);
            case "list" -> handleList(sender, meta);
            default -> sendUsage(sender);
        }

        item.setItemMeta(meta);
        return true;
    }

    private void handleSet(CommandSender sender, ItemMeta meta, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(Component.text("Usage: /eh meta set <key> <value>", NamedTextColor.RED));
            return;
        }

        String key = args[1];
        String value = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
        Object parsed = parseValue(value);

        if (parsed == null) {
            sender.sendMessage(Component.text("Invalid value: " + value, NamedTextColor.RED));
            return;
        }

        NamespacedKey nskey = new NamespacedKey(EventHorizons.getInstance(), key);
        PersistentDataContainer container = meta.getPersistentDataContainer();

        if (parsed instanceof Boolean bool) {
            container.set(nskey, PersistentDataType.BYTE, (byte) (bool ? 1 : 0));
        } else if (parsed instanceof Byte b) {
            container.set(nskey, PersistentDataType.BYTE, b);
        } else if (parsed instanceof Integer i) {
            container.set(nskey, PersistentDataType.INTEGER, i);
        } else if (parsed instanceof Float f) {
            container.set(nskey, PersistentDataType.FLOAT, f);
        } else if (parsed instanceof Double d) {
            container.set(nskey, PersistentDataType.DOUBLE, d);
        } else if (parsed instanceof String s) {
            container.set(nskey, PersistentDataType.STRING, s);
        }

        sender.sendMessage(Component.text("Set " + key + " = " + value, NamedTextColor.GREEN));
    }

    private void handleRemove(CommandSender sender, ItemMeta meta, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(Component.text("Usage: /eh meta remove <key>", NamedTextColor.RED));
            return;
        }

        NamespacedKey key = new NamespacedKey(EventHorizons.getInstance(), args[1]);
        PersistentDataContainer container = meta.getPersistentDataContainer();
        boolean hadKey = container.has(key);
        container.remove(key);

        if (hadKey) {
            sender.sendMessage(Component.text("Removed " + key.getKey(), NamedTextColor.GREEN));
        } else {
            sender.sendMessage(Component.text("Key not found", NamedTextColor.RED));
        }
    }

    private void handleList(CommandSender sender, ItemMeta meta) {
        PersistentDataContainer container = meta.getPersistentDataContainer();
        if (container.getKeys().isEmpty()) {
            sender.sendMessage(Component.text("No metadata", NamedTextColor.YELLOW));
            return;
        }

        Component msg = Component.text("Metadata:", NamedTextColor.GOLD);
        container.getKeys().forEach(key -> msg.append(Component.text("\n- " + key.getKey(), NamedTextColor.WHITE)));
        sender.sendMessage(msg);
    }

    private Object parseValue(String value) {
        try {
            if (value.equalsIgnoreCase("true"))
                return true;
            if (value.equalsIgnoreCase("false"))
                return false;
            if (value.endsWith("b"))
                return Byte.parseByte(value.replace("b", ""));
            if (value.endsWith("i"))
                return Integer.parseInt(value.replace("i", ""));
            if (value.endsWith("f"))
                return Float.parseFloat(value.replace("f", ""));
            if (value.endsWith("d"))
                return Double.parseDouble(value.replace("d", ""));
            if (value.matches("'.*'"))
                return value.substring(1, value.length() - 1);
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            return Arrays.asList("set", "remove", "list");
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("remove") && sender instanceof Player player) {
            return player.getInventory().getItemInMainHand().getItemMeta()
                    .getPersistentDataContainer().getKeys().stream()
                    .map(NamespacedKey::getKey)
                    .collect(Collectors.toList());
        }

        return completions;
    }

    private boolean sendUsage(CommandSender sender) {
        sender.sendMessage(Component.text("Usage:", NamedTextColor.RED)
                .append(Component.text("\n/eh meta set <key> <value>", NamedTextColor.GRAY))
                .append(Component.text("\n/eh meta remove <key>", NamedTextColor.GRAY))
                .append(Component.text("\n/eh meta list", NamedTextColor.GRAY)));
        return true;
    }
}
