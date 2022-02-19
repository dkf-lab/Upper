package me.dkflab.upper.managers;

import me.dkflab.upper.Upper;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

import static me.dkflab.upper.Utils.*;

public class BuildingManager {

    private Upper main;
    public BuildingManager(Upper main) {
        this.main = main;
    }

    private HashMap<Player, ItemStack[]> inventories = new HashMap<>();
    private HashMap<Location, Material> markers = new HashMap<>(); // og material, block placement
    private HashMap<Player, HashMap<Location, Integer>> builders = new HashMap<>();    // player, center pos, radius

    public void moveListener(PlayerMoveEvent e) {
        for (Location middle : builders.get(e.getPlayer()).keySet()) {
            if (e.getPlayer().getLocation().distanceSquared(middle) >= Math.pow(builders.get(e.getPlayer()).get(middle),2)) {
                e.getPlayer().teleportAsync(middle);
                e.setCancelled(true);
                error(e.getPlayer(),"Don't leave the building area!");
            }
        }
    }

    public void placeListener(BlockPlaceEvent e) {
        for (Location middle : builders.get(e.getPlayer()).keySet()) {
            if (e.getBlock().getLocation().distanceSquared(middle) >= Math.pow(builders.get(e.getPlayer()).get(middle),2)) {
                e.setCancelled(true);
                error(e.getPlayer(),"Don't place blocks outside the building area!");
            }
        }
    }

    public void disable() {
        for (Player builders : builders.keySet()) {
            removePlayerFromBuilders(builders);
        }
    }

    public void addPlayerToBuilders(Player p, int radius) {
        if (!isPlayerInBuilders(p)) {
            HashMap<Location, Integer> temp = new HashMap<>();
            temp.put(p.getLocation(),radius);
            builders.put(p, temp);
            inventories.put(p, p.getInventory().getContents());
            p.getInventory().clear();
            p.setGameMode(GameMode.CREATIVE);

            target(p.getLocation().add(radius,0,0));
            target(p.getLocation().add(0,0,radius));
            target(p.getLocation().subtract(radius,0,0));
            target(p.getLocation().subtract(0,0,radius));

            Bukkit.getLogger().info("Adding " + p.getName() + " to builders.");
        }
    }

    private void target(Location loc) {
        markers.put(loc, loc.getBlock().getType());
        loc.getBlock().setType(Material.TARGET);
    }

    public void removePlayerFromBuilders(Player p) {
        if (isPlayerInBuilders(p)) {
            builders.remove(p);
            p.setGameMode(GameMode.SURVIVAL);
            p.getInventory().clear();
            if (inventories.get(p) != null) {
                for (ItemStack item : inventories.get(p)) {
                    if (item != null) {
                        p.getInventory().addItem(item);
                    }
                }
            }
            for (Location loc : markers.keySet()) {
                loc.getBlock().setType(markers.get(loc));
            }
            Bukkit.getLogger().info("Removing " + p.getName() + " from builders.");
        }
    }

    public boolean isPlayerInBuilders(Player p) {
        return builders.containsKey(p);
    }
}
