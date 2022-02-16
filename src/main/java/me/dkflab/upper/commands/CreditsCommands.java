package me.dkflab.upper.commands;

import me.dkflab.upper.Upper;
import me.dkflab.upper.Utils;
import org.bukkit.Bukkit;
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

public class CreditsCommands implements CommandExecutor, TabExecutor {

    private Upper main;
    public CreditsCommands(Upper main) {
        this.main = main;
        main.getCommand("credits").setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("credits")) {
            if (args.length == 0) {
                if (sender instanceof Player) {
                    balance((Player)sender);
                } else {
                    notPlayer(sender);
                }
                return true;
            }
            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("balance")) {
                    Player target = null;
                    for (Player all : Bukkit.getOnlinePlayers()) {
                        if (all.getName().equalsIgnoreCase(args[1])) {
                            target = all;
                        }
                    }
                    if (target == null) {
                        error(sender, args[1] + " is not an online player. Check spelling and try again.");
                        return true;
                    }
                    info(sender, "The balance of &e" + target.getName() + "&7 is  &e" + main.getCreditManager().getCredits(target.getUniqueId()) + "&7 credits.");
                    return true;
                }
            }
            if (args.length == 3) {
                if (args[0].equalsIgnoreCase("give")) {
                    Player target = null;
                    for (Player all : Bukkit.getOnlinePlayers()) {
                        if (all.getName().equalsIgnoreCase(args[1])) {
                            target = all;
                        }
                    }
                    if (target == null) {
                        error(sender, args[1] + " is not an online player. Check spelling and try again.");
                        return true;
                    }
                    if (parseInt(sender,args[2])) {
                        main.getCreditManager().addCredits(target.getUniqueId(), Integer.parseInt(args[2]));
                        success(sender, "Added &e" + args[2] + "&7 credits to player &e" + target.getName() + "&7.");
                    }
                    return true;
                }
                if (args[0].equalsIgnoreCase("set")) {
                    Player target = null;
                    for (Player all : Bukkit.getOnlinePlayers()) {
                        if (all.getName().equalsIgnoreCase(args[1])) {
                            target = all;
                        }
                    }
                    if (target == null) {
                        error(sender, args[1] + " is not an online player. Check spelling and try again.");
                        return true;
                    }
                    if (parseInt(sender,args[2])) {
                        main.getCreditManager().setCredits(target.getUniqueId(), Integer.parseInt(args[2]));
                        success(sender, "Set &e" + target.getName() + "&7's credits to &e" + args[2] + "&7.");
                    }
                    return true;
                }
                if (args[0].equalsIgnoreCase("remove")) {
                    Player target = null;
                    for (Player all : Bukkit.getOnlinePlayers()) {
                        if (all.getName().equalsIgnoreCase(args[1])) {
                            target = all;
                        }
                    }
                    if (target == null) {
                        error(sender, args[1] + " is not an online player. Check spelling and try again.");
                        return true;
                    }
                    if (parseInt(sender,args[2])) {
                        main.getCreditManager().removeCredits(target.getUniqueId(), Integer.parseInt(args[2]));
                        success(sender, "Removed &e" + args[2] + "&7 credits from player &e" + target.getName() + "&7.");
                    }
                    return true;
                }
            }
        }
        help(sender);
        return true;
    }

    private void help (CommandSender s) {
        info(s, "Credits Help");
        sendMessage(s, "&8/credits &7- View credit balance");
        sendMessage(s, "&8/credits &egive [player] [amount] &7- Give player a certain amount of credits");
        sendMessage(s, "&8/credits &eremove [player] [amount] &7- Remove certain amount of player's credits");
        sendMessage(s, "&8/credits &eset [player] [amount] &7- Set player credit amount");
        sendMessage(s, "&8/credits &ebalance [player] &7- View another players balance");
    }

    private void balance(Player p) {
        info(p, "You have &a" + main.getCreditManager().getCredits(p.getUniqueId()) + "&7 credits.");
    }

    List<String> arguments = new ArrayList<>();
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (arguments.isEmpty()) {
            arguments.add("help");
            arguments.add("give");
            arguments.add("remove");
            arguments.add("set");
            arguments.add("balance");
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
            result.add("[player]");
            for (Player all : Bukkit.getOnlinePlayers()) {
                result.add(all.getName());
            }
        }

        if (args.length == 3) {
            if (!args[0].equalsIgnoreCase("balance")) {
                result.add("[amount]");
            }
        }
        return result;
    }
}
