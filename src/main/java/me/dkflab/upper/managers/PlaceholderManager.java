package me.dkflab.upper.managers;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.dkflab.upper.Upper;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlaceholderManager extends PlaceholderExpansion {

    private Upper main;
    public PlaceholderManager(Upper main) {
        this.main = main;
    }


    @Override
    public @NotNull String getIdentifier() {
        return "upper";
    }

    @Override
    public @NotNull String getAuthor() {
        return "dkflab";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player == null) {
            return "";
        }
        if (params.equalsIgnoreCase("credits")) {
            return String.valueOf(main.getCreditManager().getCredits(player.getUniqueId()));
        }
        if (params.equalsIgnoreCase("load")) {
            return String.valueOf(main.getEnergyManager().calculateLoad(player));
        }
        if (params.equalsIgnoreCase("energy")) {
            return String.valueOf(main.getEnergyManager().getEnergy(player));
        }
        return null;
    }
}
