package me.dkflab.upper.listeners.player;

import me.dkflab.upper.Upper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class MoveListener implements Listener {

    private Upper main;
    public MoveListener(Upper main) {
        this.main = main;
        main.getServer().getPluginManager().registerEvents(this,main);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();

        // Building
        if (main.getBuildingManager().isPlayerInBuilders(p)) {
            main.getBuildingManager().moveListener(e);
        }
        main.getEnergyManager().tick(p);
        if (main.getEnergyManager().getEnergy(p) <= 0) {
            e.setCancelled(true);
        }
    }
}
