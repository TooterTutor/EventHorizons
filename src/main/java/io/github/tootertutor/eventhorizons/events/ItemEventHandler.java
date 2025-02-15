package io.github.tootertutor.eventhorizons.events;

import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

import io.papermc.paper.event.player.PlayerPickItemEvent;

public interface ItemEventHandler extends Listener {
    void onInteract(PlayerInteractEvent event);

    void onDropItem(PlayerDropItemEvent event);

    void onPickupItem(PlayerPickItemEvent event);

    void onSwapHands(PlayerSwapHandItemsEvent event);

    void onBlockBreak(BlockDamageEvent event);
}
