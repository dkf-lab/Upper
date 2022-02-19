package me.dkflab.upper.commands;

import me.dkflab.upper.Upper;
import me.dkflab.upper.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static me.dkflab.upper.Utils.*;

public class EnergyCommand implements CommandExecutor, TabExecutor {

    private Upper main;
    public EnergyCommand(Upper main) {
        this.main = main;
        main.getCommand("energy").setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("energy")) {
            if (!(sender instanceof Player)) {
                notPlayer(sender);
                return true;
            }
            Player p = (Player) sender;
            if (args.length == 0) {
                info(p, "Energy: &e" + main.getEnergyManager().getEnergy(p));
                info(p, "Load: &e" + main.getEnergyManager().calculateLoad(p));
                return true;
            }
            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("set")) {
                    if (parseLong(sender, args[1])) {
                        Material mat = p.getInventory().getItemInMainHand().getType();
                        if (mat.equals(Material.AIR)) {
                            error(sender,"You need to be holding an item!");
                            return true;
                        }
                        main.getEnergyManager().setLoad(mat,Long.parseLong(args[1]));
                        success(p, "Set load successfully.");
                    }
                    return true;
                }
            }
            help(sender);
        }
        return true;
    }

    private void help(CommandSender s) {
        info(s, "Energy Help");
        sendMessage(s, "&e/energy &7- Display energy stats");
        if (s.hasPermission("upper.admin")) {
            sendMessage(s, "&8/energy &eset [load] &7- Sets load of item in hand");
        }
    }

    List<String> arguments = new ArrayList<>();
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (arguments.isEmpty()) {
            arguments.add("help");
            arguments.add("set");
        }
        List<String> result = new ArrayList<String>();
        if (args.length == 1) {
            for (String a : arguments) {
                if (a.toLowerCase().startsWith(args[0].toLowerCase())) {
                    result.add(a);
                }
            }
            return result;
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("set")) {
                result.add("[load]");
            }
        }
        return result;
    }
}
