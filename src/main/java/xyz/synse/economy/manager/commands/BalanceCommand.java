package xyz.synse.economy.manager.commands;

import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import xyz.synse.economy.Economy;
import xyz.synse.economy.manager.users.User;
import xyz.synse.economy.utils.StringUtils;

import java.io.FileNotFoundException;

public class BalanceCommand {
    public void register(){
        new CommandTree("balance")
                .withAliases("bal")
                .executesPlayer((player, args) -> {
                    User user = Economy.getInstance().getUserManager().getUser(player);
                    Economy.getInstance().printChat("Your balance is " + ChatColor.WHITE + StringUtils.toPrettyString(user.getWallet().getBalance()) + ChatColor.GREEN + Economy.getInstance().getConfiguration().get("currency-icon"), player);
                    player.playSound(player.getLocation(), Sound.ENTITY_WANDERING_TRADER_YES, 10, 1);
                }).then(new GreedyStringArgument("target")
                        .executes((sender, args) -> {
                            OfflinePlayer target = Bukkit.getOfflinePlayer((String) args[0]);

                            User user = null;
                            try {
                                user = Economy.getInstance().getUserManager().getUser(target.getUniqueId());
                            } catch (FileNotFoundException e) {}

                            if (user == null) {
                                Economy.getInstance().printChat("No player by name " + ChatColor.RED + args[0], sender);
                                if (sender instanceof Player) {
                                    Player player = (Player) sender;
                                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 10, 1);
                                }
                                return;
                            }

                            Economy.getInstance().printChat(ChatColor.YELLOW + target.getName() + ChatColor.GRAY + "'s balance is " + ChatColor.WHITE + StringUtils.toPrettyString(user.getWallet().getBalance()) + ChatColor.GREEN + Economy.getInstance().getConfiguration().get("currency-icon"), sender);
                            if (sender instanceof Player) {
                                Player player = (Player) sender;
                                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_YES, 10, 1);
                            }
                        }))
                .then(new PlayerArgument("player")
                        .executes((sender, args) -> {
                            Player target = (Player) args[0];
                            if (target == null) {
                                Economy.getInstance().printChat("No player by name " + ChatColor.RED + args[0], sender);
                                if (sender instanceof Player) {
                                    Player player = (Player) sender;
                                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 10, 1);
                                }
                                return;
                            }

                            User user = Economy.getInstance().getUserManager().getUser(target);
                            Economy.getInstance().printChat(ChatColor.YELLOW + target.getName() + ChatColor.GRAY + "'s balance is " + ChatColor.WHITE + StringUtils.toPrettyString(user.getWallet().getBalance()) + ChatColor.GREEN + Economy.getInstance().getConfiguration().get("currency-icon"), sender);
                            if (sender instanceof Player) {
                                Player player = (Player) sender;
                                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_YES, 10, 1);
                            }
                        }))
                .register();
    }
}
