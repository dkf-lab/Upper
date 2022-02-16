package me.dkflab.upper;

import me.dkflab.upper.commands.CreditsCommands;
import me.dkflab.upper.commands.MainCommand;
import me.dkflab.upper.listeners.player.*;
import me.dkflab.upper.managers.BuildingManager;
import me.dkflab.upper.managers.CreditManager;
import me.dkflab.upper.managers.EnergyManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Upper extends JavaPlugin {

    @Override
    public void onEnable() {
        initializeCommands();
        initializeListeners();
    }

    @Override
    public void onDisable() {
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
}
