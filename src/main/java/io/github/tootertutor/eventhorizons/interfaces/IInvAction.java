package io.github.tootertutor.eventhorizons.interfaces;

import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

import io.papermc.paper.event.player.PlayerPickItemEvent;

public interface IInvAction {
    public void onDropItem(PlayerDropItemEvent event);
    public void onPickItem(PlayerPickItemEvent event);
    public void onSwapHands(PlayerSwapHandItemsEvent event);
}