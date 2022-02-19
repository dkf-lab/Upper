package me.dkflab.upper;

import me.dkflab.upper.commands.CreditsCommands;
import me.dkflab.upper.commands.MainCommand;
import me.dkflab.upper.listeners.player.*;
import me.dkflab.upper.managers.BuildingManager;
import me.dkflab.upper.managers.CreditManager;
import me.dkflab.upper.managers.EnergyManager;
import me.dkflab.upper.managers.NPCManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Upper extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        initializeCommands();
        initializeListeners();
        getNpcManager(); // make sure our NPCs spawn
        getBuildingManager();
        Runnable energyTick = new Runnable() {
            @Override
            public void run() {
                getEnergyManager().regen();
            }
        };
        Bukkit.getScheduler().runTaskTimer(this,energyTick,6*60*20,6*60*20);
    }

    @Override
    public void onDisable() {
        getNpcManager().despawnNPCs();
        getBuildingManager().disable();
    }

    private void initializeCommands() {
        new MainCommand(this);
        new CreditsCommands(this);
    }

    private void initializeListeners() {
        new MoveListener(this);
        new PlaceListener(this);
        new JoinListener(this);
        new BreakListener(this);
        new PickupListener(this);
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
}
