package me.dkflab.upper.managers;

import me.dkflab.upper.Upper;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.UUID;
import java.util.logging.Level;

public class CreditManager {

    private Upper main;
    public CreditManager (Upper main) {
        this.main = main;
        saveDefaultConfig();
    }

    public void addCredits(UUID uuid, int amount) {
        amount += getConfig().getInt(uuid.toString());
        setCredits(uuid, amount);
    }

    public void setCredits(UUID uuid, int amount) {
        getConfig().set(uuid.toString(),amount);
        saveConfig();
    }

    public boolean purchase(UUID uuid, int amount) {
        if (getCredits(uuid) - amount < 0) {
            return false;
        }
        setCredits(uuid, getCredits(uuid)-amount);
        return true;
    }

    public void removeCredits(UUID uuid, int amount) {
        if (getCredits(uuid) - amount < 0) {
            setCredits(uuid, 0);
            return;
        }
        setCredits(uuid, getCredits(uuid)-amount);
    }

    public int getCredits(UUID uuid) {
        return getConfig().getInt(uuid.toString());
    }

    // Data Management
    private FileConfiguration dataConfig = null;
    private File configFile = null;
    public void reloadConfig() {
        // creates config, checks for yml issues
        if (this.configFile == null) {
            configFile = new File(this.main.getDataFolder(), "credits.yml");
        }
        dataConfig = YamlConfiguration.loadConfiguration(configFile);
        InputStream defaultStream = this.main.getResource("credits.yml");
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
            configFile = new File(this.main.getDataFolder(), "credits.yml");
        }
        if (!configFile.exists()) {
            main.saveResource("credits.yml", false);
        }
    }
}
