package me.dkflab.upper.managers;

import me.dkflab.upper.Upper;
import me.dkflab.upper.Utils;
import me.dkflab.upper.objects.Mine;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

public class MineManager {

    List<Mine> mines = new ArrayList<>();
    HashMap<Block, Material> resetList = new HashMap<>();


    private Upper main;
    public MineManager(Upper main) {
        this.main = main;
        saveDefaultConfig();
        for (String key : getConfig().getKeys(false)) {
            ConfigurationSection sec = getConfig().getConfigurationSection(key);
            if (sec != null) {
                mines.add(new Mine(sec.getLocation("middle"),sec.getInt("radius"),Material.getMaterial(sec.getString("material"))));
            }
        }
    }

    public void createMine(Location middle, int radius, Material ore) {
        Mine e = new Mine(middle,radius,ore);
        mines.add(e);
        int index = getConfig().getKeys(false).size()+1;
        getConfig().set(index + ".middle", middle);
        getConfig().set(index + ".radius", radius);
        getConfig().set(index + ".material",ore.name());
        saveConfig();
        reloadConfig();
    }

    public boolean isBlockInMine(Location loc) {
        for (Mine m : mines) {
            if (m.isWithinMine(loc)) {
                return true;
            }
        }
        return false;
    }

    public void addBlockToReset(Block b, Material type) {
        resetList.put(b,type);
    }

    public void resetMines() {
        if (resetList != null) {
            for (Block b : resetList.keySet()) {
                b.setType(resetList.get(b));
            }
        }
        for (Mine m : mines) {
            Location middle = m.getMiddle();
            for (int i = 1; i < 255; i++) {
                middle.setY(i);
                for (Block b: Utils.getBlocks(middle.getBlock(), m.getRadius())) {
                    if (b.getType().equals(Material.STONE)) {
                       // if (Math.random() < 0.01) {
                            b.setType(m.getMaterial());
                     //   }
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
            configFile = new File(this.main.getDataFolder(), "mines.yml");
        }
        dataConfig = YamlConfiguration.loadConfiguration(configFile);
        InputStream defaultStream = this.main.getResource("mines.yml");
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
            configFile = new File(this.main.getDataFolder(), "mines.yml");
        }
        if (!configFile.exists()) {
            main.saveResource("mines.yml", false);
        }
    }
}
