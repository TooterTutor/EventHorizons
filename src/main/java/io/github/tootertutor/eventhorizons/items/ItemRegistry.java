package io.github.tootertutor.eventhorizons.items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemRegistry implements Registry<Keyed> {
    protected final Plugin plugin;
    private final Map<NamespacedKey, Item> itemMap = new HashMap<>(); // Map to store items by NamespacedKey

    public ItemRegistry(Plugin plugin) {
        this.plugin = plugin;
    }

    public void registerItem(NamespacedKey key, Item item) {
        itemMap.put(key, item);
    }

    public Item getItem(NamespacedKey key) {
        return itemMap.get(key);
    }

    public List<Item> getItems() {
        return new ArrayList<>(itemMap.values());
    }

    @Override
    public @Nullable Keyed get(@NotNull NamespacedKey key) {
        return getItem(key);
    }

    @Override
    public @NotNull Keyed getOrThrow(@NotNull NamespacedKey key) {
        Item item = getItem(key);
        if (item == null) {
            throw new IllegalArgumentException("Item not found for key: " + key);
        }
        return item;
    }

    @Override
    public @NotNull Stream<Keyed> stream() {
        return itemMap.values().stream().map(item -> (Keyed) item);
    }

    @Override
    public Iterator<Keyed> iterator() {
        return itemMap.values().stream().map(item -> (Keyed) item).iterator();
    }
}
