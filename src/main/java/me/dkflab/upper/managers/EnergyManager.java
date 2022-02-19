package me.dkflab.upper.managers;

import me.dkflab.upper.Upper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class EnergyManager {

    private Upper main;
    public EnergyManager(Upper main) {
        this.main = main;
    }

    HashMap<Player, Integer> energy = new HashMap<>();

    public void tick(Player p) {
        // Energy regen

    }

    public void regen() {
        for (Player all : Bukkit.getOnlinePlayers()) {
            addEnergy(all, 30);
        }
    }

    public int getEnergy(Player p) {
        return energy.get(p);
    }

    public void setEnergy(Player p, int amount) {
        energy.put(p, amount);
    }

    public void addEnergy(Player p, int amount) {
        setEnergy(p, getEnergy(p)+amount);
    }
}
