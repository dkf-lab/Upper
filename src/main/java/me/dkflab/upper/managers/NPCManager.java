package me.dkflab.upper.managers;

import me.dkflab.upper.Upper;
import me.dkflab.upper.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import static me.dkflab.upper.Utils.*;

public class NPCManager {

    private Upper main;
    public NPCManager (Upper main) {
        this.main = main;
        saveDefaultConfig();
        spawnNPCs();
        villager = Bukkit.createInventory(null, 9, color(main.getConfig().getString("villager-trade-name")));
        base = Bukkit.createInventory(null,9, color(main.getConfig().getString("base-trade-name")));
        FileConfiguration c = main.getConfig();
        createInventory(villager,c.getInt("villager-emeraldBuy"),c.getInt("villager-emeraldSell"),c.getInt("villager-breadBuy"),c.getInt("villager-breadSell"));
        createInventory(base,c.getInt("base-emeraldBuy"),c.getInt("base-emeraldSell"),c.getInt("base-breadBuy"),c.getInt("base-breadSell"));
    }

    HashMap<Player, String> shopSelected = new HashMap<>();
    List<Entity> live = new ArrayList<>();

    public Inventory villager;
    public Inventory base;
    private void createInventory(Inventory inv, int emeraldBuy, int emeraldSell, int breadBuy, int breadSell) {
        for (int i = 0; i < 9; i++) {
            inv.setItem(i, blankPane());
        }
        List<String> lore = new ArrayList<>();
        lore.add("&7Click on an item in your inventory");
        lore.add("&7to sell it.");
        inv.setItem(4, createItem(Material.PAPER, 1, "&b&lSelling Items", lore,null,null,false));
        lore.clear();
        lore.add("&eBuy: &7" + emeraldBuy);
        lore.add("&eSell: &7" + emeraldSell);
        lore.add("&7Click to Buy");
        inv.setItem(3, createItem(Material.EMERALD,1,"&a&lEmerald", lore, null, null, false));
        lore.clear();
        lore.add("&eBuy: &7" + breadBuy);
        lore.add("&eSell: &7" + breadSell);
        lore.add("&7Click to Buy");
        inv.setItem(5, createItem(Material.BREAD,1,"&e&lBread", lore, null, null, false));
    }

    public void villageTradeInvListener(InventoryClickEvent e) {
        ItemStack i = e.getCurrentItem();
        FileConfiguration c = main.getConfig();
        if (i == null) {
            return;
        }
        if (i.hasItemMeta()) {
            if (i.getItemMeta().getDisplayName().equals(color("&a&lEmerald"))) {
                if (main.getCreditManager().purchase(e.getWhoClicked().getUniqueId(),c.getInt("villager-emeraldBuy"))) {
                    e.getWhoClicked().getInventory().addItem(new ItemStack(Material.EMERALD, 1));
                    success(e.getWhoClicked(), "Purchase successful.");
                } else {
                    error(e.getWhoClicked(),"Insufficient funds.");
                }
                return;
            }
            if (i.getItemMeta().getDisplayName().equals(color("&e&lBread"))) {
                if (main.getCreditManager().purchase(e.getWhoClicked().getUniqueId(),c.getInt("villager-breadBuy"))) {
                    e.getWhoClicked().getInventory().addItem(new ItemStack(Material.BREAD, 1));
                    success(e.getWhoClicked(), "Purchase successful.");
                } else {
                    error(e.getWhoClicked(),"Insufficient funds.");
                }
                return;
            }
        }
        if (i.getType().equals(Material.EMERALD)) {
            int amount = i.getAmount();
            int price = c.getInt("villager-emeraldSell");
            e.getWhoClicked().getInventory().removeItem(i);
            main.getCreditManager().addCredits(e.getWhoClicked().getUniqueId(),price*amount);
            success(e.getWhoClicked(), "Sold &e" + amount + " &eEmeralds &7for &e" + price*amount + " Credits&7!");
        }
        if (i.getType().equals(Material.BREAD)) {
            int amount = i.getAmount();
            int price = c.getInt("villager-breadSell");
            e.getWhoClicked().getInventory().removeItem(i);
            main.getCreditManager().addCredits(e.getWhoClicked().getUniqueId(),price*amount);
            success(e.getWhoClicked(), "Sold &e" + amount + " &eBread&7 for &e" + price*amount + " Credits&7!");
        }
    }

    public void baseTradeInvListener(InventoryClickEvent e) {
        ItemStack i = e.getCurrentItem();
        FileConfiguration c = main.getConfig();
        if (i == null) {
            return;
        }
        if (i.hasItemMeta()) {
            if (i.getItemMeta().getDisplayName().equals(color("&a&lEmerald"))) {
                if (main.getCreditManager().purchase(e.getWhoClicked().getUniqueId(),c.getInt("base-emeraldBuy"))) {
                    e.getWhoClicked().getInventory().addItem(new ItemStack(Material.EMERALD, 1));
                    success(e.getWhoClicked(), "Purchase successful.");
                } else {
                    error(e.getWhoClicked(),"Insufficient funds.");
                }
                return;
            }
            if (i.getItemMeta().getDisplayName().equals(color("&e&lBread"))) {
                if (main.getCreditManager().purchase(e.getWhoClicked().getUniqueId(),c.getInt("base-breadBuy"))) {
                    e.getWhoClicked().getInventory().addItem(new ItemStack(Material.BREAD, 1));
                    success(e.getWhoClicked(), "Purchase successful.");
                } else {
                    error(e.getWhoClicked(),"Insufficient funds.");
                }
                return;
            }
        }
        if (i.getType().equals(Material.EMERALD)) {
            int amount = i.getAmount();
            int price = c.getInt("base-emeraldSell");
            e.getWhoClicked().getInventory().removeItem(i);
            main.getCreditManager().addCredits(e.getWhoClicked().getUniqueId(),price*amount);
            success(e.getWhoClicked(), "Sold &e" + amount + " &eEmeralds&7 for &e" + price*amount + " Credits&7!");
        }
        if (i.getType().equals(Material.BREAD)) {
            int amount = i.getAmount();
            int price = c.getInt("base-breadSell");
            e.getWhoClicked().getInventory().removeItem(i);
            main.getCreditManager().addCredits(e.getWhoClicked().getUniqueId(),price*amount);
            success(e.getWhoClicked(), "Sold &e" + amount + " &eBread&7 for &e" + price*amount + " Credits&7!");
        }
    }

    public void baseTradeListener(PlayerInteractAtEntityEvent e) {
        Player target = e.getPlayer();
        shopSelected.put(target,"base");
        target.openInventory(base);
    }

    public void villageTradeListener(PlayerInteractAtEntityEvent e) {
        Player target = e.getPlayer();
        shopSelected.put(target,"villager");
        target.openInventory(villager);
    }

    public void addVillageTrade(Location loc) {
        int test = 0;
        if (getConfig().getConfigurationSection("npcs") != null) {
            test = getConfig().getConfigurationSection("npcs").getKeys(false).size() + 1;
        }
        Bukkit.getLogger().info("Test: " + test);
        getConfig().set("npcs." + test, loc);
        saveConfig();
        reloadConfig();
        spawnNPCs();
    }

    public void addBaseTrade(Location loc) {
        int test = 0;
        if (getConfig().getConfigurationSection("base-npcs") != null) {
            test = getConfig().getConfigurationSection("base-npcs").getKeys(false).size() + 1;
        }
        Bukkit.getLogger().info("Test: " + test);
        getConfig().set("base-npcs." + test, loc);
        saveConfig();
        reloadConfig();
        spawnNPCs();
    }

    public void spawnNPCs() {
        despawnNPCs();
        ConfigurationSection s = getConfig().getConfigurationSection("npcs");
        if (s != null) {
            for (String key : s.getKeys(false)) {
                Location loc = s.getLocation(key);
                Villager e = (Villager) loc.getWorld().spawnEntity(loc, EntityType.VILLAGER);
                e.setCustomNameVisible(true);
                e.setCustomName(color(main.getConfig().getString("villager-trade-name")));
                e.setAI(false);
                live.add(e);
            }
        }
        ConfigurationSection base = getConfig().getConfigurationSection("base-npcs");
        if (base != null) {
            for (String key : base.getKeys(false)) {
                Location loc = base.getLocation(key);
                Villager e = (Villager) loc.getWorld().spawnEntity(loc, EntityType.VILLAGER);
                e.setCustomNameVisible(true);
                e.setCustomName(color(main.getConfig().getString("base-trade-name")));
                e.setAI(false);
                live.add(e);
            }
        }
    }

    public void despawnNPCs() {
        for (Entity e : live) {
            e.remove();
        }
    }

    // Data Management
    private FileConfiguration dataConfig = null;
    private File configFile = null;
    public void reloadConfig() {
        // creates config, checks for yml issues
        if (this.configFile == null) {
            configFile = new File(this.main.getDataFolder(), "npcs.yml");
        }
        dataConfig = YamlConfiguration.loadConfiguration(configFile);
        InputStream defaultStream = this.main.getResource("npcs.yml");
        if (defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
            this.dataConfig.setDefaults(defaultConfig);
        }
    }

    public FileConfiguration getConfig() {
        if (dataConfig == null) {
            reloadConfig();
        }
        return dataConfig;
    }

    public void saveConfig() {
        // save config after changing data
        if (dataConfig == null||configFile==null) {
            return;
        }
        try {
            getConfig().save(this.configFile);
        } catch (IOException e) {
            main.getLogger().log(Level.SEVERE, "Could not save config to " + this.configFile, e);
        }
    }

    public void saveDefaultConfig() {
        if (this.configFile == null) {
            configFile = new File(this.main.getDataFolder(), "npcs.yml");
        }
        if (!configFile.exists()) {
            main.saveResource("npcs.yml", false);
        }
    }
}
