package me.dkflab.upper.managers;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.dkflab.upper.Upper;
import me.dkflab.upper.objects.Mine;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class AnchorManager {

    private Upper main;
    public AnchorManager(Upper main) {
        this.main = main;
        saveDefaultConfig();
        init();
    }

    private List<Location> anchors = new ArrayList<>();
    private void init() {
        for (String keys : getConfig().getKeys(false)) {
            Location l = getConfig().getLocation(keys);
            if (l != null) {
                anchors.add(l);
            }
        }
    }

    public void createMineAnchor(Mine m) {
        m.getMiddle().getBlock().setType(Material.TARGET);
        anchors.add(m.getPlayerMiddle());
        int size = getConfig().getKeys(false).size()+1;
        getConfig().set(String.valueOf(size),m.getMiddle());
        saveConfig();
        main.getServer().getScheduler().runTaskLater(main, new Runnable() {
            @Override
            public void run() {
                Bukkit.getLogger().info("Setting target at " + m.getPlayerMiddle().getBlock());
                m.getMiddle().getBlock().setType(Material.TARGET);
            }
        }, 20L);
    }

    public void checkForAnchor(Block b) {
        Location loc = b.getLocation();
        loc.setYaw(0);
        loc.setPitch(0);
        for (Location l : anchors) {
            if (l.getBlockX() == b.getX()) {
                if (l.getBlockZ() == b.getZ()) {
                    if (l.getBlockY() == b.getY()) {
                        Bukkit.getLogger().info("Passing mine " +  main.getMineManager().getMineOfBlock(l) + " to.");
                        main.getMineManager().removeMine(main.getMineManager().getMineOfBlock(l));
                    }
                }
            }
        }
    }

    // Data Management
    private FileConfiguration dataConfig = null;
    private File configFile = null;
    public void reloadConfig() {
        // creates config, checks for yml issues
        if (this.configFile == null) {
            configFile = new File(this.main.getDataFolder(), "anchors.yml");
        }
        dataConfig = YamlConfiguration.loadConfiguration(configFile);
        InputStream defaultStream = this.main.getResource("anchors.yml");
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
            configFile = new File(this.main.getDataFolder(), "anchors.yml");
        }
        if (!configFile.exists()) {
            main.saveResource("anchors.yml", false);
        }
    }
}
