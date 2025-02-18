package io.github.tootertutor.eventhorizons.builders;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

public class ItemDataBuilder {
    private final Plugin plugin;
    private final Map<NamespacedKey, Object> data = new HashMap<>();

    public ItemDataBuilder(Plugin plugin) {
        this.plugin = plugin;
    }

    public ItemDataBuilder fromMeta(ItemMeta meta) {
        if (meta != null) {
            meta.getPersistentDataContainer().getKeys().forEach(key -> {
                data.put(key, meta.getPersistentDataContainer().get(key, getType(key)));
            });
        }
        return this;
    }

    public ItemDataBuilder set(String key, Object value) {
        NamespacedKey nskey = new NamespacedKey(plugin, key);
        if (value == null) {
            data.remove(nskey);
        } else {
            data.put(nskey, value);
        }
        return this;
    }

    public void applyTo(ItemMeta meta) {
        if (meta == null)
            return;

        data.forEach((key, value) -> {
            if (value instanceof Boolean) {
                byte byteValue = (Boolean) value ? (byte) 1 : (byte) 0;
                meta.getPersistentDataContainer().set(key, PersistentDataType.BYTE, byteValue);
            } else if (value instanceof Byte) {
                meta.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (Byte) value);
            } else if (value instanceof Integer) {
                meta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, (Integer) value);
            } else if (value instanceof Float) {
                meta.getPersistentDataContainer().set(key, PersistentDataType.FLOAT, (Float) value);
            } else if (value instanceof Double) {
                meta.getPersistentDataContainer().set(key, PersistentDataType.DOUBLE, (Double) value);
            } else if (value instanceof String) {
                meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, (String) value);
            }
        });
    }

    public boolean hasBoolean(String key) {
        NamespacedKey nskey = new NamespacedKey(plugin, key);
        Object value = data.get(nskey);
        return value instanceof Boolean || value instanceof Byte;
    }

    // Add general has() method for any key
    public boolean has(String key) {
        return data.containsKey(new NamespacedKey(plugin, key));
    }

    // Update getType to handle boolean-as-byte
    private PersistentDataType<?, ?> getType(NamespacedKey key) {
        Object value = data.get(key);
        if (value instanceof Boolean || (value instanceof Byte && (Byte) value <= 1)) {
            return PersistentDataType.BYTE;
        } else if (value instanceof String) {
            return PersistentDataType.STRING;
        } else if (value instanceof Integer) {
            return PersistentDataType.INTEGER;
        } else if (value instanceof Float) {
            return PersistentDataType.FLOAT;
        } else if (value instanceof Double) {
            return PersistentDataType.DOUBLE;
        }
        return null; // Fallback type
    }

}
