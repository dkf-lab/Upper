package me.dkflab.upper.listeners.player;

import me.dkflab.upper.Upper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BreakListener implements Listener {

    private Upper main;
    public BreakListener(Upper main) {
        this.main = main;
        main.getServer().getPluginManager().registerEvents(this,main);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        if (!main.getBuildingManager().isPlayerInBuilders(e.getPlayer())) {
            if (!main.getMineManager().isBlockInMine(e.getBlock().getLocation())) {
                e.setCancelled(true);
            } else {
                main.getEnergyManager().subtractEnergy(e.getPlayer(), main.getConfig().getInt("energy-cost-blockBreak"));
            }
        }
    }
}
