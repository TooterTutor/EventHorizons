package io.github.tootertutor.eventhorizons.handlers;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public class ItemTextHandler {

    private final ItemStack item;

    public ItemTextHandler(ItemStack item) {
        this.item = item;
    }

    // Display Name Methods
    public Component getDisplayName() {
        ItemMeta meta = item.getItemMeta();
        return (meta != null) ? meta.displayName() : null;
    }

    public void setDisplayName(Component displayName) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(displayName);
            item.setItemMeta(meta);
        }
    }

    public void setDisplayName(String displayName) {
        setDisplayName(Component.text(displayName));
    }

    public void setDisplayName(String displayName, String hexColor) {
        setDisplayName(Component.text(displayName).color(TextColor.fromHexString(hexColor)));
    }

    public void updateDisplayName(Component newDisplayName) {
        setDisplayName(newDisplayName);
    }

    public void replaceInDisplayName(Component placeholder, Component replacement) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null && meta.displayName() != null) {
            String updatedName = meta.displayName().toString().replace(placeholder.toString(), replacement.toString());
            meta.displayName(Component.text(updatedName));
            item.setItemMeta(meta);
        }
    }

    // Lore Methods
    public List<Component> getLore() {
        ItemMeta meta = item.getItemMeta();
        return (meta != null) ? meta.lore() : null;
    }

    public void setLoreFromStrings(List<String> lore) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            List<Component> loreComponents = lore.stream()
                    .map(Component::text)
                    .collect(Collectors.toList());
            meta.lore(loreComponents);
            item.setItemMeta(meta);
        }
    }

    public void setLore(List<Component> lore) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.lore(lore);
            item.setItemMeta(meta);
        }
    }

    public void updateLoreLine(int lineIndex, Component newText) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            List<Component> lore = meta.lore();
            if (lore != null && lineIndex >= 0 && lineIndex < lore.size()) {
                lore.set(lineIndex, newText);
                meta.lore(lore);
                item.setItemMeta(meta);
            }
        }
    }

    public void replaceLoreLine(int lineIndex, Component placeholder, Component replacement) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            List<Component> lore = meta.lore();
            if (lore != null && lineIndex >= 0 && lineIndex < lore.size()) {
                String updatedLine = lore.get(lineIndex).toString().replace(placeholder.toString(),
                        replacement.toString());
                lore.set(lineIndex, Component.text(updatedLine));
                meta.lore(lore);
                item.setItemMeta(meta);
            }
        }
    }

    public void addLoreLine(Component line) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            List<Component> lore = meta.lore();
            if (lore != null) {
                lore.add(line);
                meta.lore(lore);
                item.setItemMeta(meta);
            }
        }
    }

    public void removeLoreLine(int lineIndex) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            List<Component> lore = meta.lore();
            if (lore != null && lineIndex >= 0 && lineIndex < lore.size()) {
                lore.remove(lineIndex);
                meta.lore(lore);
                item.setItemMeta(meta);
            }
        }
    }

    public void clearLore() {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.lore(null);
            item.setItemMeta(meta);
        }
    }

    // Utility Methods
    public void setColoredText(Component text, String hexColor) {
        if (hexColor != null && !hexColor.isEmpty()) {
            TextColor color = TextColor.fromHexString(hexColor);
            text = text.color(color);
        }
    }

    public boolean hasDisplayName() {
        ItemMeta meta = item.getItemMeta();
        return meta != null && meta.hasDisplayName();
    }

    public boolean hasLore() {
        ItemMeta meta = item.getItemMeta();
        return meta != null && meta.hasLore();
    }
}
