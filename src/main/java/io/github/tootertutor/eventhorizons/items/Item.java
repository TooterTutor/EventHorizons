package io.github.tootertutor.eventhorizons.items;

import java.net.http.WebSocket.Listener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import io.github.tootertutor.eventhorizons.builders.ItemDataBuilder;
import io.github.tootertutor.eventhorizons.handlers.ItemTextHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public abstract class Item implements Listener, Keyed {
    protected final Plugin plugin;
    protected String displayName;
    protected Material material;
    protected List<String> lore;
    protected String nameColor;
    protected List<String> loreColor;
    protected ItemDataBuilder itemDataBuilder;
    protected Map<String, Recipe> recipes;
    protected ItemStack itemStack;
    protected ItemTextHandler textHandler;
    protected NamespacedKey key;

    protected Item(Plugin plugin, NamespacedKey key) {
        this.plugin = plugin;
        this.key = key;
        updateItemText();
    }

    protected Item(Plugin plugin, ItemStack itemStack) {
        this.plugin = plugin;
        this.itemStack = itemStack;
        ItemMeta meta = itemStack.getItemMeta();
        this.key = new NamespacedKey(plugin, itemStack.getType().name().toLowerCase());
        this.material = itemStack.getType();

        if (meta != null) {
            Component name = meta.displayName();
            this.displayName = name != null ? PlainTextComponentSerializer.plainText().serialize(name) : "Unknown Item";

            List<Component> loreComponents = meta.lore();
            if (loreComponents != null) {
                this.lore = new ArrayList<>();
                for (Component comp : loreComponents) {
                    this.lore.add(PlainTextComponentSerializer.plainText().serialize(comp));
                }
            } else {
                this.lore = new ArrayList<>();
            }

            this.itemDataBuilder = new ItemDataBuilder(meta, plugin);
        } else {
            this.displayName = "Unknown Item";
            this.lore = new ArrayList<>();
            this.itemDataBuilder = null;
        }

        this.nameColor = "#FFF";
        this.loreColor = Collections.emptyList();
        this.recipes = new HashMap<>();
        this.textHandler = new ItemTextHandler(this.itemStack);
        updateItemText();
    }

    @Override
    public NamespacedKey getKey() {
        return key;
    }

    public NamespacedKey getId() {
        return key;
    }

    public String getName() {
        return displayName;
    }

    public Material getMaterial() {
        return material;
    }

    public ItemStack getItemStack() {
        applyMetadata();
        return itemStack;
    }

    /**
     * Applies the item's metadata (display name, lore, and persistent data) to the
     * ItemStack
     */
    protected void applyMetadata() {
        if (itemStack == null) {
            return;
        }

        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            return;
        }

        // Apply display name
        if (displayName != null && nameColor != null) {
            meta.displayName(Component.text(displayName)
                    .color(TextColor.fromHexString(nameColor)));
        }

        // Apply lore
        if (lore != null && !lore.isEmpty()) {
            List<Component> loreComponents = new ArrayList<>();
            for (int i = 0; i < lore.size(); i++) {
                String color = (loreColor != null && i < loreColor.size()) ? loreColor.get(i) : "#FFFFFF";
                loreComponents.add(Component.text(lore.get(i))
                        .color(TextColor.fromHexString(color)));
            }
            meta.lore(loreComponents);
        }

        // Apply persistent data
        if (itemDataBuilder != null) {
            for (NamespacedKey key : itemDataBuilder.build().getKeys()) {
                byte value = itemDataBuilder.build().get(key, PersistentDataType.BYTE);
                meta.getPersistentDataContainer().set(key, PersistentDataType.BYTE, value);
            }

        }

        itemStack.setItemMeta(meta);
    }

    public List<String> getLore() {
        return Collections.unmodifiableList(lore);
    }

    public String getNameColor() {
        return nameColor;
    }

    public List<String> getLoreColor() {
        return Collections.unmodifiableList(loreColor);
    }

    public Map<String, Recipe> getRecipes() {
        return Collections.unmodifiableMap(recipes);
    }

    public ItemTextHandler getTextHandler() {
        return textHandler;
    }

    @Deprecated
    public void updateItemText() {
        applyMetadata();
    }

    public void setDisplayName(String name) {
        this.displayName = name;
        textHandler.setDisplayName(name, nameColor);
    }

    public void setDisplayName(String name, String color) {
        this.displayName = name;
        this.nameColor = color;
        textHandler.setDisplayName(name, color);
    }

    public void setLore(List<Component> lore) {
        this.lore = new ArrayList<>(lore.stream().map(Component::toString).collect(Collectors.toList()));
        updateItemText();
    }

    public void setLore(List<String> lore, List<String> colors) {
        this.lore = new ArrayList<>(lore);
        this.loreColor = new ArrayList<>(colors);
        updateItemText();
    }

    public void updateLoreLine(int index, String text) {
        if (index >= 0 && index < lore.size()) {
            lore.set(index, text);
            String color = index < loreColor.size() ? loreColor.get(index) : "#FFFFFF";
            textHandler.updateLoreLine(index,
                    Component.text(text).color(TextColor.fromHexString(color)));
        }
    }

    public void updateLoreLine(int index, String text, String color) {
        if (index >= 0 && index < lore.size()) {
            lore.set(index, text);
            if (index < loreColor.size()) {
                loreColor.set(index, color);
            } else {
                loreColor.add(color);
            }
            textHandler.updateLoreLine(index,
                    Component.text(text).color(TextColor.fromHexString(color)));
        }
    }

    public void addRecipe(String key, Recipe recipe) {
        recipes.put(key, recipe);
        if (plugin.isEnabled()) {
            plugin.getServer().addRecipe(recipe);
        }
    }

    public Recipe getRecipe(String key) {
        return recipes.get(key);
    }

    public void removeRecipe(String key) {
        Recipe recipe = recipes.remove(key);
        if (recipe != null && recipe instanceof ShapedRecipe && plugin.isEnabled()) {
            plugin.getServer().removeRecipe(((ShapedRecipe) recipe).getKey());
        }
    }

    public List<Recipe> listRecipes() {
        return new ArrayList<>(recipes.values());
    }

    public boolean has(String key) {
        return itemDataBuilder.hasByte(key);
    }

    public void add(String key) {
        itemDataBuilder.setByte(key, (byte) 1);
    }

    @Override
    public String toString() {
        return "Item{id='" + key + "', displayName='" + displayName + "', lore=" + lore +
                ", nameColor='" + nameColor + "', loreColor=" + loreColor + '}';
    }
}
