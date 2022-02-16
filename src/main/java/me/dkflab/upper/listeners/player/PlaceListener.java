package me.dkflab.upper.listeners.player;

import me.dkflab.upper.Upper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class PlaceListener implements Listener {

    private Upper main;
    public PlaceListener (Upper main) {
        this.main = main;
        main.getServer().getPluginManager().registerEvents(this,main);
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        if (main.getBuildingManager().isPlayerInBuilders(e.getPlayer())) {
            main.getBuildingManager().placeListener(e);
            return;
        }
        e.setCancelled(true);
    }
}
