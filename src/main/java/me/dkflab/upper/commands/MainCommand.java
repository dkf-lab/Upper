package me.dkflab.upper.commands;

import me.dkflab.upper.Upper;
import me.dkflab.upper.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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

public class MainCommand implements CommandExecutor, TabExecutor {

    private Upper main;
    public MainCommand(Upper main) {
        this.main = main;
        main.getCommand("upper").setExecutor(this);
        main.getCommand("upper").setTabCompleter(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("upper")) {
            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("set")) {
                    if (args[1].equalsIgnoreCase("first-point")) {
                        if (sender instanceof Player) {
                            main.getConfig().set("first-point",((Player)sender).getLocation());
                            success(sender, "Set first-point to your location.");
                        } else {
                            notPlayer(sender);
                        }
                        return true;
                    }
                }
                if (args[0].equalsIgnoreCase("create")) {
                    if (args[1].equalsIgnoreCase("village-trade")) {
                        if (sender instanceof Player) {
                            main.getNpcManager().addVillageTrade(((Player)sender).getLocation());
                            success(sender, "Added village-trade to your location.");
                        } else {
                            notPlayer(sender);
                        }
                        return true;
                    }
                    if (args[1].equalsIgnoreCase("base-trade")) {
                        if (sender instanceof Player) {
                            main.getNpcManager().addBaseTrade(((Player)sender).getLocation());
                            success(sender, "Added base-trade to your location.");
                        } else {
                            notPlayer(sender);
                        }
                        return true;
                    }
                }
                return true;
            }
            if (args.length == 3) {
                if (args[0].equalsIgnoreCase("set")) {
                    if (args[1].equalsIgnoreCase("player")) {
                        Player target = null;
                        for (Player all : Bukkit.getOnlinePlayers()) {
                            if (all.getName().equalsIgnoreCase(args[2])) {
                                target = all;
                            }
                        }
                        if (target == null) {
                            error(sender, args[2] + "is not a player.");
                            return true;
                        }
                        main.getBuildingManager().removePlayerFromBuilders(target);
                        success(sender, "Removed player &e" + target.getName() + "&7 from the builders list!");
                    }
                }
                if (args[0].equalsIgnoreCase("create")) {
                    if (args[1].equalsIgnoreCase("protection-point")) {
                        if (parseInt(sender,args[2])) {
                            int radius = Integer.parseInt(args[2]);
                            if (!(sender instanceof Player)) {
                                notPlayer(sender);
                                return true;
                            }
                            success(sender, "Created protection point at your location.");
                        }
                    }

                }
                return true;
            }
            if (args.length == 4) {
                if (args[0].equalsIgnoreCase("set")) {
                    if (args[1].equalsIgnoreCase("first-point")) {
                        if (!sender.hasPermission("upper.admin")) {
                            noPerms(sender);
                            return true;
                        }
                        if (!(sender instanceof Player)) {
                            notPlayer(sender);
                            return true;
                        }
                        main.getConfig().set("first-point",((Player)sender).getLocation());
                        main.saveConfig();
                        main.reloadConfig();
                        success(sender, "Set first point to your location.");
                    }
                    if (args[1].equalsIgnoreCase("builder")) {
                        Player target = null;
                        for (Player all : Bukkit.getOnlinePlayers()) {
                            if (all.getName().equalsIgnoreCase(args[2])) {
                                target = all;
                            }
                        }
                        if (target == null) {
                            error(sender, args[2] + "is not a player.");
                            return true;
                        }
                        if (parseInt(sender, args[3])) {
                            int radius = Integer.parseInt(args[3]);
                            main.getBuildingManager().addPlayerToBuilders(target,radius);
                            success(sender, "Added player &e" + target.getName() + "&7 to the builders list!");
                        }
                    }
                }
                if (args[0].equalsIgnoreCase("create")) {
                    if (args[1].equalsIgnoreCase("mine")) {
                        // create mine [ore] [radius]
                        Material ore = null;
                        for (Material m : Material.values()) {
                            if (m.name().equalsIgnoreCase(args[2])) {
                                ore = m;
                            }
                        }
                        if (ore == null) {
                            error(sender, args[2] + " is not a block.");
                            return true;
                        }
                        if (!(sender instanceof Player)) {
                            notPlayer(sender);
                            return true;
                        }
                        if (parseInt(sender,args[3])) {
                            Location l = ((Player)sender).getLocation();
                            main.getMineManager().createMine(l,Integer.parseInt(args[3]),ore,l);
                            success(sender, "Successfully created mine at your location.");
                        }
                    }
                }
                return true;
            }
        }
        help(sender);
        return true;
    }

    private void help (CommandSender sender) {
        info(sender, "Help");
        info(sender, "Set Commands");
        sendMessage(sender, "&8/u &7set &ebuilder [player] [radius] &7- Set player as builder in a certain radius"); // 4 args
        sendMessage(sender, "&8/u &7set &eplayer [player] &7- Reset player back to normal"); // 3 args
        sendMessage(sender, "&8/u &7set &efirst-point &7 - Set location for first join"); // 2 args
        info(sender, "Creation Commands");
        sendMessage(sender, "&8/u &7create &eprotection-point [radius] &7 - Protect area surrounding from PVP."); // 3 args
        sendMessage(sender, "&8/u &7create &evillage-trade &7 - Create NPC villager for trading."); // 2 args
        sendMessage(sender, "&8/u &7create &ebase-trade &7 - Create NPC villager for trading."); // 2 args
        sendMessage(sender, "&8/u &7create &emine [ore] [radius] &7 - Create mine with ore."); // 3 args
    }

    List<String> arguments = new ArrayList<>();
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (arguments.isEmpty()) {
            arguments.add("help");
            arguments.add("create");
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
                result.add("builder");
                result.add("player");
                result.add("first-point");
            }
            if (args[0].equalsIgnoreCase("create")) {
                result.add("protection-point");
                result.add("village-trade");
                result.add("base-trade");
                result.add("mine");
            }
        }

        if (args.length == 3) {
            if (args[0].equalsIgnoreCase("set")) {
                if (args[1].equalsIgnoreCase("builder")) {
                    for (Player all : Bukkit.getOnlinePlayers()) {
                        result.add(all.getName());
                    }
                    result.add("[player]");
                }
                if (args[1].equalsIgnoreCase("player")) {
                    for (Player all : Bukkit.getOnlinePlayers()) {
                        result.add(all.getName());
                    }
                    result.add("[player]");
                }
            }
            if (args[0].equalsIgnoreCase("create")) {
                if (args[1].equalsIgnoreCase("protection-point")) {
                    result.add("[radius]");
                }
                if (args[1].equalsIgnoreCase("mine")) {
                    result.add("[ore]");
                    for (Material m : Material.values()) {
                        result.add(m.name());
                    }
                }
            }
        }

        if (args.length == 4) {
            if (args[0].equalsIgnoreCase("set")) {
                if (args[1].equalsIgnoreCase("builder")) {
                    result.add("[radius]");
                }
                if (args[1].equalsIgnoreCase("mine")) {
                    result.add("[radius]");
                }
            }
        }
        return result;
    }
}
