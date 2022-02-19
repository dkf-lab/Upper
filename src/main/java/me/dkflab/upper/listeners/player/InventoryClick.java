package me.dkflab.upper.listeners.player;

import me.dkflab.upper.Upper;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import static me.dkflab.upper.Utils.*;

public class InventoryClick implements Listener {

    private Upper main;
    public InventoryClick(Upper main) {
        this.main = main;
        main.getServer().getPluginManager().registerEvents(this,main);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (ChatColor.stripColor(e.getView().getTitle()).equals(ChatColor.stripColor(color(main.getConfig().getString("villager-trade-name"))))) {
            main.getNpcManager().villageTradeInvListener(e);
            e.setCancelled(true);
        }
        if (e.getView().getTitle().equals(color(main.getConfig().getString("base-trade-name")))) {
            main.getNpcManager().baseTradeInvListener(e);
            e.setCancelled(true);
        }
    }
}
