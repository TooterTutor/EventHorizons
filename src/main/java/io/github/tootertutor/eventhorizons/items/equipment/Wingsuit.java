package io.github.tootertutor.eventhorizons.items.equipment;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.inventory.ItemStack;

import io.github.tootertutor.eventhorizons.EventHorizons;
import io.github.tootertutor.eventhorizons.interfaces.AutoRegisterItem;
import io.github.tootertutor.eventhorizons.items.Item;

public class Elytra extends Item implements Listener, AutoRegisterItem {
    private final Set<UUID> gliding = new HashSet<>();

    public Elytra(EventHorizons plugin) {
        super(plugin, new NamespacedKey(plugin, "wingsuit"));
        super.displayName = "Wingsuit";
        super.nameColor = "#43F0C0";
        super.lore = Arrays.asList("Grants Immunity to Crash Damage");
        super.loreColor = Arrays.asList("#00BBCC");
        super.material = Material.ELYTRA;
        super.itemStack = new ItemStack(material);
        
        applyMetadata();
    }

    @EventHandler
    public void onGlideToggle(EntityToggleGlideEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player player) {
            gliding.add(player.getUniqueId());
        }
    }

    @EventHandler
    public void onPlayerCrash(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player player)) {
            return;
        }

        if (!isFallOrWallDamage(e.getCause()) || !isPlayerGliding(player)) {
            return;
        }

        ItemStack chestplate = player.getInventory().getChestplate();
        if (isItem(chestplate)) {
            handleDamageCancellation(e, player, true);
            return;
        }

        if (isWearingStandardElytra(player)) {
            handleDamageCancellation(e, player, false);
        }
    }

    private boolean isFallOrWallDamage(EntityDamageEvent.DamageCause cause) {
        return cause == EntityDamageEvent.DamageCause.FALL || cause == EntityDamageEvent.DamageCause.FLY_INTO_WALL;
    }

    private boolean isPlayerGliding(Player player) {
        return player.isGliding() || gliding.contains(player.getUniqueId());
    }

    private boolean isWearingStandardElytra(Player player) {
        ItemStack chestplate = player.getInventory().getChestplate();
        return chestplate != null && chestplate.getType() == Material.ELYTRA;
    }

    private void handleDamageCancellation(EntityDamageEvent e, Player player, boolean cancel) {
        e.setCancelled(cancel);
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_BIG_FALL, 1.0F, 1.0F);
    }
}
