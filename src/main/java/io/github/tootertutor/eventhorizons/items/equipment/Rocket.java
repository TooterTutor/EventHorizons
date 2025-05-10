package io.github.tootertutor.eventhorizons.items.equipment;

import java.util.Arrays;
import java.util.Objects;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

import io.github.tootertutor.eventhorizons.EventHorizons;
import io.github.tootertutor.eventhorizons.interfaces.AutoRegisterItem;
import io.github.tootertutor.eventhorizons.items.Item;

public class Rocket extends Item implements Listener, AutoRegisterItem {

    protected Rocket(EventHorizons plugin) {
        super(plugin, new NamespacedKey(plugin, "infinityrocket"));
        super.displayName = "Infinity Rocket";
        super.nameColor = "#1dd608";
        super.lore = Arrays.asList("An infinite source of propulsion...");
        super.loreColor = Arrays.asList("#2d9137");
        super.material = Material.FIREWORK_ROCKET;
        super.itemStack = new ItemStack(material);

        applyMetadata();
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (isItem(item)) {
            if (player.isGliding() && event.getAction() == Action.RIGHT_CLICK_AIR) {
                player.fireworkBoost(item);
                event.setCancelled(true);
            } else if (isItem(item) && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                Firework firework = player.getWorld().spawn(Objects.requireNonNull(event.getInteractionPoint()),
                        Firework.class);
                FireworkMeta fireworkMeta = firework.getFireworkMeta();

                fireworkMeta.addEffect(FireworkEffect.builder()
                        .withColor(Color.ORANGE)
                        .withColor(Color.BLACK)
                        .withColor(Color.SILVER)
                        .withTrail()
                        .with(FireworkEffect.Type.STAR)
                        .build());

                fireworkMeta.setPower(2);
                firework.setFireworkMeta(fireworkMeta);

                event.setCancelled(true);
            }
        }
    }
}
