package me.dkflab.upper.listeners.player;

import me.dkflab.upper.Upper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    private Upper main;
    public JoinListener(Upper main) {
        this.main = main;

        main.getServer().getPluginManager().registerEvents(this,main);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if (!e.getPlayer().hasPlayedBefore()) {
            if (main.getConfig().getLocation("first-join") != null) {
                e.getPlayer().teleportAsync(main.getConfig().getLocation("first-point"));
            }
        }
        main.getEnergyManager().setEnergy(e.getPlayer(),6000);
    }
}
