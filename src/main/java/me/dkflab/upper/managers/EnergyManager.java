package me.dkflab.upper.managers;

import me.dkflab.upper.Upper;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.logging.Level;

public class EnergyManager {

    private Upper main;
    public EnergyManager(Upper main) {
        this.main = main;
        saveDefaultConfig();
    }

    HashMap<Player, Float> energy = new HashMap<>();

    public void tick(Player p) {
        // Calculate load and then take energy
        subtractEnergy(p,(calculateLoad(p)/4)); // event occurs about 3-4 times when moving from one block to next
    }

    public float calculateLoad(Player p) {
        float load = 5;
        if (p.getInventory().getContents() != null) {
            for (ItemStack item : p.getInventory().getContents()) {
                if (item != null) {
                    load += getLoad(item.getType());
                }
            }
        }
        return load;
    }

    public float getLoad(Material m) {
        float load = 0;
        ConfigurationSection materials = getConfig().getConfigurationSection("materials");
        if (materials != null) {
            return materials.getLong(m.name());
        }
        return load;
    }

    public void setLoad(Material m, long load) {
        getConfig().set("materials." + m.name(), load);
        saveConfig();
        reloadConfig();
    }

    public void regen() {
        for (Player all : Bukkit.getOnlinePlayers()) {
            addEnergy(all, 30);
        }
    }

    public float getEnergy(Player p) {
        return energy.get(p);
    }

    public void setEnergy(Player p, float amount) {
        energy.put(p, amount);
    }

    public void addEnergy(Player p, float amount) {
        setEnergy(p, getEnergy(p)+amount);
    }

    public void subtractEnergy(Player p, float amount) {
        setEnergy(p,getEnergy(p)-amount);
    }

    // Data Management
    private FileConfiguration dataConfig = null;
    private File configFile = null;
    public void reloadConfig() {
        // creates config, checks for yml issues
        if (this.configFile == null) {
            configFile = new File(this.main.getDataFolder(), "energy.yml");
        }
        dataConfig = YamlConfiguration.loadConfiguration(configFile);
        InputStream defaultStream = this.main.getResource("energy.yml");
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
            configFile = new File(this.main.getDataFolder(), "energy.yml");
        }
        if (!configFile.exists()) {
            main.saveResource("energy.yml", false);
        }
    }
}
