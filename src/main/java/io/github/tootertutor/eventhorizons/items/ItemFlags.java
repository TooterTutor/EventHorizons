package io.github.tootertutor.eventhorizons.items;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;

import io.github.tootertutor.eventhorizons.EventHorizons;

/**
 * Abstract class extending Item to support setting various item meta flags and properties such as unbreakable,
 * fireproof, custom model data, enchantments, and item flags.
 */
public abstract class ItemFlags extends Item {

    protected Set<ItemFlag> itemFlags = EnumSet.noneOf(ItemFlag.class);
    protected boolean unbreakable = false;
    protected boolean fireproof = false;

    protected Integer customModelData = null;
    protected Map<Enchantment, Integer> enchantments = new HashMap<>();

    protected ItemFlags(EventHorizons plugin, NamespacedKey key) {
        super(plugin, key);
    }

    /**
     * Set the item flags to be applied to the item, replacing any existing flags.
     * @param flags ItemFlags to set
     * @return this instance for chaining
     */
    public ItemFlags setItemFlags(Set<ItemFlag> flags) {
        this.itemFlags = EnumSet.copyOf(flags);
        applyMetadata();
        return this;
    }

    /**
     * Add an item flag to the current set.
     * @param flag ItemFlag to add
     * @return this instance for chaining
     */
    public ItemFlags addItemFlag(ItemFlag flag) {
        this.itemFlags.add(flag);
        applyMetadata();
        return this;
    }

    /**
     * Remove an item flag from the current set.
     * @param flag ItemFlag to remove
     * @return this instance for chaining
     */
    public ItemFlags removeItemFlag(ItemFlag flag) {
        this.itemFlags.remove(flag);
        applyMetadata();
        return this;
    }

    /**
     * Check if the item has a specific flag.
     * @param flag ItemFlag to check
     * @return true if present, false otherwise
     */
    public boolean hasItemFlag(ItemFlag flag) {
        return this.itemFlags.contains(flag);
    }

    /**
     * Clear all item flags.
     * @return this instance for chaining
     */
    public ItemFlags clearItemFlags() {
        this.itemFlags.clear();
        applyMetadata();
        return this;
    }

    /**
     * Set whether the item is unbreakable.
     * @param unbreakable true to make unbreakable, false otherwise
     * @return this instance for chaining
     */
    public ItemFlags setUnbreakable(boolean unbreakable) {
        this.unbreakable = unbreakable;
        applyMetadata();
        return this;
    }

    /**
     * Set whether the item is fire resistant.
     * Note: Bukkit API does not have direct fireproof flag, so this is stored for custom use.
     * @param fireproof true to make fire resistant, false otherwise
     * @return this instance for chaining
     */
    public ItemFlags setFireproof(boolean fireproof) {
        this.fireproof = fireproof;
        applyMetadata();
        return this;
    }

    /**
     * Set the custom model data of the item.
     * @param customModelData the custom model data to set, or null to clear
     * @return this instance for chaining
     */
    public ItemFlags setCustomModelData(Integer customModelData) {
        this.customModelData = customModelData;
        applyMetadata();
        return this;
    }

    /**
     * Add an enchantment to the item.
     * @param enchantment the enchantment to add
     * @param level the level of the enchantment
     * @return this instance for chaining
     */
    public ItemFlags addEnchantment(Enchantment enchantment, int level) {
        this.enchantments.put(enchantment, level);
        applyMetadata();
        return this;
    }

    /**
     * Remove an enchantment from the item.
     * @param enchantment the enchantment to remove
     * @return this instance for chaining
     */
    public ItemFlags removeEnchantment(Enchantment enchantment) {
        this.enchantments.remove(enchantment);
        applyMetadata();
        return this;
    }

    /**
     * Clear all enchantments from the item.
     * @return this instance for chaining
     */
    public ItemFlags clearEnchantments() {
        this.enchantments.clear();
        applyMetadata();
        return this;
    }

    @Override
    protected void applyMetadata() {
        super.applyMetadata();
        if (itemStack == null) return;
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return;

        meta.setUnbreakable(unbreakable);

        if (!itemFlags.isEmpty()) {
            meta.addItemFlags(itemFlags.toArray(new ItemFlag[0]));
        } else {
            // Remove all flags if empty
            for (ItemFlag flag : ItemFlag.values()) {
                meta.removeItemFlags(flag);
            }
        }

        if (customModelData != null) {
            meta.setCustomModelData(customModelData);
        } else {
            meta.setCustomModelData(null);
        }

        // Clear existing enchantments and add current ones
        meta.getEnchants().keySet().forEach(meta::removeEnchant);
        for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
            meta.addEnchant(entry.getKey(), entry.getValue(), true);
        }

        itemStack.setItemMeta(meta);
    }
}
