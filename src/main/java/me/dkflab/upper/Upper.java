package me.dkflab.upper;

import me.dkflab.upper.commands.CreditsCommands;
import me.dkflab.upper.commands.EnergyCommand;
import me.dkflab.upper.commands.MainCommand;
import me.dkflab.upper.listeners.player.*;
import me.dkflab.upper.managers.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Upper extends JavaPlugin {

    public boolean placeholder = false;

    @Override
    public void onEnable() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            placeholder = true;
        }
        saveDefaultConfig();
        initializeCommands();
        initializeListeners();
        getNpcManager(); // make sure our NPCs spawn
        getBuildingManager();
        getEnergyManager();
        getMineManager();
        getAnchorManager();
        new PlaceholderManager(this).register();
        int time = getConfig().getInt("mine-reset-time");
        if (time == 0) {
            time = 3600;
        }
        Runnable mineTick = new Runnable() {
            @Override
            public void run() {
                // Every 1 hr
                getMineManager().resetMines();
            }
        };
        Bukkit.getScheduler().runTaskTimer(this, mineTick, 0, time*20);

        Runnable energyTick = new Runnable() {
            @Override
            public void run() {
                // Every 6 min
                getEnergyManager().regen();
            }
        };
        Bukkit.getScheduler().runTaskTimer(this,energyTick,6*60*20,6*60*20);
    }

    @Override
    public void onDisable() {
        getNpcManager().despawnNPCs();
        getBuildingManager().disable();
        getMineManager().resetMines();
    }

    private void initializeCommands() {
        new MainCommand(this);
        new CreditsCommands(this);
        new EnergyCommand(this);
    }

    private void initializeListeners() {
        new MoveListener(this);
        new PlaceListener(this);
        new JoinListener(this);
        new BreakListener(this);
        //new PickupListener(this);
        new InteractListener(this);
        new InventoryClick(this);
    }

    BuildingManager buildingManager;
    public BuildingManager getBuildingManager() {
        if (buildingManager == null) {
            buildingManager = new BuildingManager(this);
        }
        return buildingManager;
    }

    AnchorManager anchorManager;
    public AnchorManager getAnchorManager() {
        if (anchorManager == null) {
            anchorManager = new AnchorManager(this);
        }
        return anchorManager;
    }

    CreditManager creditManager;
    public CreditManager getCreditManager() {
        if (creditManager == null) {
            creditManager = new CreditManager(this);
        }
        return creditManager;
    }

    EnergyManager energyManager;
    public EnergyManager getEnergyManager() {
        if (energyManager == null) {
            energyManager = new EnergyManager(this);
        }
        return energyManager;
    }

    NPCManager npcManager;
    public NPCManager getNpcManager() {
        if (npcManager == null) {
            npcManager = new NPCManager(this);
        }
        return npcManager;
    }

    MineManager mineManager;
    public MineManager getMineManager() {
        if (mineManager == null) {
            mineManager = new MineManager(this);
        }
        return mineManager;
    }
}
