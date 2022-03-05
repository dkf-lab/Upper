package me.dkflab.upper.listeners.player;

import me.dkflab.upper.Upper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class PickupListener implements Listener {

    private Upper main;
    public PickupListener(Upper main) {
        this.main = main;
        main.getServer().getPluginManager().registerEvents(this,main);
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent e) {
        // depreciated
        if (!(main.getMineManager().isBlockInMine(e.getPlayer().getLocation()))) {
            e.setCancelled(true);
        }
    }
}
