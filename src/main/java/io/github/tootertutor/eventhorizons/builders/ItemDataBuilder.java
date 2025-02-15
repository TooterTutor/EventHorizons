package io.github.tootertutor.eventhorizons.builders;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

public class ItemDataBuilder {
    private PersistentDataContainer container;
    private Plugin plugin;

    public ItemDataBuilder(Plugin plugin) {
        this.plugin = plugin;
    }

    public ItemDataBuilder(ItemMeta itemMeta, Plugin plugin) {
        this.container = itemMeta.getPersistentDataContainer();
        this.plugin = plugin;
    }

    // Setters
    public ItemDataBuilder setByte(String key, byte value) {
        NamespacedKey namespacedKey = new NamespacedKey(plugin, key);
        container.set(namespacedKey, PersistentDataType.BYTE, value);
        return this;
    }

    public ItemDataBuilder setString(String key, String value) {
        NamespacedKey namespacedKey = new NamespacedKey(plugin, key);
        container.set(namespacedKey, PersistentDataType.STRING, value);
        return this;
    }

    public ItemDataBuilder setInt(String key, int value) {
        NamespacedKey namespacedKey = new NamespacedKey(plugin, key);
        container.set(namespacedKey, PersistentDataType.INTEGER, value);
        return this;
    }

    public ItemDataBuilder setBoolean(String key, boolean value) {
        NamespacedKey namespacedKey = new NamespacedKey(plugin, key);
        container.set(namespacedKey, PersistentDataType.BYTE, (byte) (value ? 1 : 0));
        return this;
    }

    // Getters
    public boolean hasByte(String key) {
        NamespacedKey namespacedKey = new NamespacedKey(plugin, key);
        return container.has(namespacedKey, PersistentDataType.BYTE);
    }

    public boolean hasString(String key) {
        NamespacedKey namespacedKey = new NamespacedKey(plugin, key);
        return container.has(namespacedKey, PersistentDataType.STRING);
    }

    public boolean hasInt(String key) {
        NamespacedKey namespacedKey = new NamespacedKey(plugin, key);
        return container.has(namespacedKey, PersistentDataType.INTEGER);
    }

    public boolean hasBoolean(String key) {
        NamespacedKey namespacedKey = new NamespacedKey(plugin, key);
        return container.has(namespacedKey, PersistentDataType.BYTE);
    }

    // Builder
    public PersistentDataContainer build() {
        return container;
    }

    public void register() {
        // Implementation of the register method
        // Add logic to register the item
    }
}
