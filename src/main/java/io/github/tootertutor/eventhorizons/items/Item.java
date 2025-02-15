package io.github.tootertutor.eventhorizons.items;

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
import org.bukkit.plugin.Plugin;

import io.github.tootertutor.eventhorizons.builders.ItemDataBuilder;
import io.github.tootertutor.eventhorizons.handlers.ItemTextHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public abstract class Item implements Keyed {
    protected final Plugin plugin;
    // private String id;
    protected String displayName;
    protected Material material;
    protected List<String> lore;
    protected String nameColor; // Ensure this field is defined only once
    protected List<String> loreColor;
    protected ItemDataBuilder itemDataBuilder;
    protected Map<String, Recipe> recipes;
    protected ItemStack itemStack;
    protected ItemTextHandler textHandler;
    protected NamespacedKey key; // Add NamespacedKey field

    protected Item(Plugin plugin, NamespacedKey key) {
        this.plugin = plugin;
        this.key = key;
        // this.displayName = displayName;
        // this.material = material;
        // this.lore = lore;
        // this.nameColor = nameColor;
        // this.loreColor = loreColor;
        // this.itemDataBuilder = new ItemDataBuilder(itemMeta, plugin);
        // this.recipes = recipes != null ? recipes : new HashMap<>();
        // this.itemStack = new ItemStack(material);
        // this.textHandler = new ItemTextHandler(this.itemStack);
        updateItemText();
    }

    protected Item(Plugin plugin, ItemStack itemStack) {
        this.plugin = plugin;
        this.itemStack = itemStack;
        ItemMeta meta = itemStack.getItemMeta();
        this.key = new NamespacedKey(plugin, itemStack.getType().name().toLowerCase()); // Initialize NamespacedKey
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

    // Implement getKey() method from Keyed interface
    @Override
    public NamespacedKey getKey() {
        return key;
    }

    // New method to get the ID of the item
    public NamespacedKey getId() {
        return key;
    }

    public String getName() {
        return displayName;
    }

    // Item management methods
    public Material getMaterial() {
        return material;
    }

    public ItemStack getItemStack() {
        updateItemText();
        return itemStack.clone();
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

    public void updateItemText() {
        if (textHandler != null) {
            textHandler.setDisplayName(displayName, nameColor);
            List<Component> loreComponents = new ArrayList<>();
            for (int i = 0; i < lore.size(); i++) {
                String loreLine = lore.get(i);
                String color = i < loreColor.size() ? loreColor.get(i) : "#FFFFFF";
                Component component = Component.text(loreLine).color(TextColor.fromHexString(color));
                loreComponents.add(component);
            }
            setLore(loreComponents);
        }
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
                    Component.text(text).color(net.kyori.adventure.text.format.TextColor.fromHexString(color)));
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
                    Component.text(text).color(net.kyori.adventure.text.format.TextColor.fromHexString(color)));
        }
    }

    // Recipe management methods
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

    // PersistentDataContainer methods
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
