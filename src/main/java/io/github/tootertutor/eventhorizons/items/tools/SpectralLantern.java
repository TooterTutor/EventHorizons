package io.github.tootertutor.eventhorizons.items.tools;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import io.github.tootertutor.eventhorizons.EventHorizons;
import io.github.tootertutor.eventhorizons.interfaces.AutoRegisterItem;
import io.github.tootertutor.eventhorizons.items.Item;

public class SpectralLantern extends Item implements Listener, AutoRegisterItem {

    private static final double RADIUS = 7.5;
    private static final int GLOW_DURATION = 10;

    protected SpectralLantern(EventHorizons plugin) {
        super(plugin, new NamespacedKey(plugin, "lantern"));
        super.displayName = "Spectral Lantern";
        super.nameColor = "#54bf90";
        super.lore = Arrays.asList("A lantern that guides lost souls and wards off darkness.");
        super.loreColor = Arrays.asList("#54b8bf");
        super.material = Material.SOUL_LANTERN;
        super.itemStack = new ItemStack(material);

        applyMetadata();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        Action action = event.getAction();
        if (isItem(item) && (action == Action.RIGHT_CLICK_AIR)) {
            revealEntities(player);
        } else if (isItem(item) && (action == Action.RIGHT_CLICK_BLOCK)) {
            event.setCancelled(true);
        }
    }

    private void revealEntities(Player player) {
        Set<Entity> entitiesToHighlight = new HashSet<>();

        for (Entity entity : player.getNearbyEntities(RADIUS, RADIUS, RADIUS)) {
            if (entity.getLocation().distanceSquared(player.getLocation()) <= RADIUS * RADIUS) {
                if (entity instanceof LivingEntity
                        && ((LivingEntity) entity).hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                    entitiesToHighlight.add(entity);
                }
            }
        }

        // Apply glow immediately
        for (Entity entity : entitiesToHighlight) {
            entity.setGlowing(true);
        }

        // Schedule removal of glow after duration
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Entity entity : entitiesToHighlight) {
                    entity.setGlowing(false);
                }
            }
        }.runTaskLater(plugin, GLOW_DURATION * 20L);
    }
}
