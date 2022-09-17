package xyz.synse.economy.manager.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.DoubleArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import xyz.synse.economy.Economy;
import xyz.synse.economy.manager.users.User;
import xyz.synse.economy.utils.StringUtils;

public class EconomyCommand {
    public void register(){
        new CommandAPICommand("economy")
                .withAliases("eco")
                .executes((sender, args) -> {
                    Economy.getInstance().printChat("By " + Economy.getInstance().getDescription().getAuthors() + " v" + Economy.getInstance().getDescription().getVersion(), sender);

                    if (sender instanceof Player) {
                        ((Player) sender).playSound(((Player) sender).getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_SET_SPAWN, 10, 1);
                    }
                }).withSubcommand(new CommandAPICommand("reload")
                        .withAliases("rel")
                        .withPermission(CommandPermission.OP)
                        .executes((sender, args) -> {
                            Economy.getInstance().printChat("Reloading main config", sender);
                            Economy.getInstance().getConfiguration().load();
                            Economy.getInstance().printChat("Reloading prices config", sender);
                            Economy.getInstance().getPricesManager().getConfig().load();

                            if (sender instanceof Player) {
                                ((Player) sender).playSound(((Player) sender).getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_AMBIENT, 10, 1);
                            }
                        })
                )
                .withSubcommand(new CommandAPICommand("set")
                        .withArguments(new PlayerArgument("player"), new DoubleArgument("amount"))
                        .withPermission(CommandPermission.OP)
                        .executes(((sender, args) -> {
                            Player target = (Player) args[0];

                            User user = Economy.getInstance().getUserManager().getUser(target);

                            user.getWallet().setBalance((double) args[1]);
                            Economy.getInstance().printChat(ChatColor.YELLOW + target.getName() + ChatColor.GRAY + "'s balance set to " + ChatColor.WHITE + StringUtils.toPrettyString(user.getWallet().getBalance()) + ChatColor.GREEN + Economy.getInstance().getConfiguration().get("currency-icon"), sender);

                            if (sender instanceof Player) {
                                Player player = (Player) sender;
                                player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 10, 1);
                            }
                        }))
                )
                .register();
    }
}
