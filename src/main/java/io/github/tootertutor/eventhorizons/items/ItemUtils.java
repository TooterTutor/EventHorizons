package io.github.tootertutor.eventhorizons.items;

import java.lang.reflect.Constructor;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import io.github.tootertutor.eventhorizons.EventHorizons;

public final class ItemUtils {
    private ItemUtils() {
    } // Prevent instantiation

    /**
     * Checks if an ItemStack matches a registered custom item
     * 
     * @param itemStack The item to check
     * @param itemKey   The NamespacedKey of the custom item to verify
     * @return true if the item matches the specified custom item
     */
    public static boolean isItem(ItemStack item, Class<? extends Item> itemClass) {
        try {
            if (item == null || item.getType().isAir())
                return false;

            // Get key from class
            Constructor<?> constructor = itemClass.getConstructor(EventHorizons.class);
            Item instance = (Item) constructor.newInstance(EventHorizons.getInstance());
            NamespacedKey key = instance.getKey();

            // Check PDC
            ItemMeta meta = item.getItemMeta();
            return meta != null &&
                    meta.getPersistentDataContainer().has(key, PersistentDataType.BYTE);

        } catch (Exception e) {
            EventHorizons.getInstance().getLogger().warning(
                    "Failed to check item type: " + itemClass.getSimpleName());
            return false;
        }
    }
}
