package me.dkflab.upper.managers;

import me.dkflab.upper.Upper;
import me.dkflab.upper.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
import java.util.Collections;
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
                purchaseitem((Player) e.getWhoClicked(),Material.EMERALD,c.getInt("villager-emeraldBuy"),main.getConfig().getString("villager-trade-name"));
                return;
            }
            if (i.getItemMeta().getDisplayName().equals(color("&e&lBread"))) {
                purchaseitem((Player) e.getWhoClicked(), Material.BREAD, c.getInt("villager-breadBuy"),main.getConfig().getString("villager-trade-name"));
                return;
            }
            if (i.getItemMeta().getDisplayName().contains(Utils.color("&e&lPurchase"))) {
                if (i.getItemMeta().getLore() != null) {
                    int price = 0;
                    int amount = 0;
                    for (String l : i.getItemMeta().getLore()) {
                        l = ChatColor.stripColor(l);
                        if (l.startsWith("Amount")) {
                            amount = Integer.parseInt(l.replaceFirst(".*?(\\d+).*", "$1"));
                        }
                        if (l.startsWith("Price")) {
                            price = Integer.parseInt(l.replaceFirst(".*?(\\d+).*", "$1"));
                        }
                    }
                    if (price==0||amount==0) {
                        return;
                    }
                    if (main.getCreditManager().purchase(e.getWhoClicked().getUniqueId(),price)) {
                        e.getWhoClicked().getInventory().addItem(new ItemStack(i.getType(), amount));
                        success(e.getWhoClicked(), "Purchase successful.");
                    } else {
                        error(e.getWhoClicked(),"Insufficient funds.");
                    }
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

    public void purchaseitem(Player target, Material type, int price, String title) {
        Inventory i = Bukkit.createInventory(null, 9,Utils.color(title));
        i.setItem(0,Utils.blankPane());
        i.setItem(8,Utils.blankPane());
        i.setItem(4,Utils.createItem(Material.PAPER,1,"&aPurchase", Collections.singletonList("&7Select amount"),null,null,false));
        List<String> lore = new ArrayList<>();
        lore.add("&7Amount: &b1");
        lore.add("&7Price: &a" + price);
        i.setItem(1,Utils.createItem(type,1,"&e&lPurchase One", lore, null, null, false));
        lore.clear();
        lore.add("&7Amount: &b5");
        lore.add("&7Price: &a" + price*5);
        i.setItem(2,Utils.createItem(type,1,"&e&lPurchase Five", lore, null, null, false));
        lore.clear();
        lore.add("&7Amount: &b10");
        lore.add("&7Price: &a" + price*10);
        i.setItem(3,Utils.createItem(type,1,"&e&lPurchase Ten", lore, null, null, false));
        lore.clear();
        lore.add("&7Amount: &b64");
        lore.add("&7Price: &a" + price*64);
        i.setItem(5,Utils.createItem(type,1,"&e&lPurchase 1 Stack", lore, null, null, false));
        lore.clear();
        lore.add("&7Amount: &b128");
        lore.add("&7Price: &a" + price*128);
        i.setItem(6,Utils.createItem(type,1,"&e&lPurchase 2 Stacks", lore, null, null, false));
        lore.clear();
        lore.add("&7Amount: &b192");
        lore.add("&7Price: &a" + price*192);
        i.setItem(7,Utils.createItem(type,1,"&e&lPurchase 3 Stacks", lore, null, null, false));
        target.openInventory(i);
    }

    public void baseTradeInvListener(InventoryClickEvent e) {
        ItemStack i = e.getCurrentItem();
        FileConfiguration c = main.getConfig();
        if (i == null) {
            return;
        }
        if (i.hasItemMeta()) {
            if (i.getItemMeta().getDisplayName().equals(color("&a&lEmerald"))) {
                purchaseitem((Player) e.getWhoClicked(),Material.EMERALD,c.getInt("base-emeraldBuy"),main.getConfig().getString("base-trade-name"));
                return;
            }
            if (i.getItemMeta().getDisplayName().equals(color("&e&lBread"))) {
                purchaseitem((Player) e.getWhoClicked(), Material.BREAD, c.getInt("base-breadBuy"),main.getConfig().getString("base-trade-name"));
                return;
            }
            if (i.getItemMeta().getDisplayName().contains(Utils.color("&e&lPurchase"))) {
                if (i.getItemMeta().getLore() != null) {
                    int price = 0;
                    int amount = 0;
                    for (String l : i.getItemMeta().getLore()) {
                        l = ChatColor.stripColor(l);
                        if (l.startsWith("Amount")) {
                            amount = Integer.parseInt(l.replaceFirst(".*?(\\d+).*", "$1"));
                        }
                        if (l.startsWith("Price")) {
                            price = Integer.parseInt(l.replaceFirst(".*?(\\d+).*", "$1"));
                        }
                    }
                    if (price==0||amount==0) {
                        return;
                    }
                    if (main.getCreditManager().purchase(e.getWhoClicked().getUniqueId(),price)) {
                        e.getWhoClicked().getInventory().addItem(new ItemStack(i.getType(), amount));
                        success(e.getWhoClicked(), "Purchase successful.");
                    } else {
                        error(e.getWhoClicked(),"Insufficient funds.");
                    }
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
