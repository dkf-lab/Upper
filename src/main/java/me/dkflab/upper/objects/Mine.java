package me.dkflab.upper.objects;

import me.dkflab.upper.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class Mine {

    private Location middle, playerMiddle;
    private int radius;
    private Material ore;

    public Mine(Location middle,int radius, Material ore,Location playerMiddle) {
        this.middle = middle;
        this.radius = radius;
        this.ore = ore;
        this.playerMiddle = playerMiddle;
    }

    public Location getMiddle() {
        return this.middle;
    }

    public Location getPlayerMiddle() {
        return this.playerMiddle;
    }

    public int getRadius() {
        return this.radius;
    }

    public boolean isWithinMine(Location loc) {
        for (int i = 1; i < 255; i++) {
            middle.setY(i);
            for (Block b: Utils.getBlocks(middle.getBlock(), getRadius())) {
                if (b.getLocation().equals(loc)) {
                    return true;
                }
            }
        }
        return false;
    }

    public Material getMaterial() {
        return this.ore;
    }
}
