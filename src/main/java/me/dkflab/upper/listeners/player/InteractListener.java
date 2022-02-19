package me.dkflab.upper.listeners.player;

import me.dkflab.upper.Upper;
import me.dkflab.upper.Utils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class InteractListener implements Listener {

    private Upper main;
    public InteractListener(Upper main) {
        this.main = main;
        main.getServer().getPluginManager().registerEvents(this,main);
    }

    @EventHandler
    public void onInter (PlayerInteractAtEntityEvent e) {
        if (e.getRightClicked().getCustomName().equals(Utils.color(main.getConfig().getString("base-trade-name")))) {
            main.getNpcManager().baseTradeListener(e);
            e.setCancelled(true);
        }
        if (e.getRightClicked().getCustomName().equals(Utils.color(main.getConfig().getString("villager-trade-name")))) {
            main.getNpcManager().villageTradeListener(e);
            e.setCancelled(true);
        }
    }
}
