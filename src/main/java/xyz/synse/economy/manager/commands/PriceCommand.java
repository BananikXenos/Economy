package xyz.synse.economy.manager.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.DoubleArgument;
import dev.jorel.commandapi.arguments.ItemStackArgument;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import xyz.synse.economy.Economy;
import xyz.synse.economy.utils.StringUtils;

public class PriceCommand {
    public void register(){
        new CommandAPICommand("price").executesPlayer((player, args) -> {
                    ItemStack item = player.getInventory().getItemInMainHand();
                    if (!Economy.getInstance().getPricesManager().isSellable(item.getType())) {
                        Economy.getInstance().printChat(ChatColor.BLUE + StringUtils.fixMinecraftName(item.getType()) + ChatColor.GRAY + " is " + ChatColor.RED + "not" + ChatColor.GRAY + " sellable.", player);
                        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 10, 1);
                        return;
                    }

                    double price = Economy.getInstance().getPricesManager().getPrice(item.getType()) * item.getAmount();

                    Economy.getInstance().printChat("" + ChatColor.YELLOW + item.getAmount() + ChatColor.WHITE + "x " + ChatColor.BLUE + StringUtils.fixMinecraftName(item.getType()) + ChatColor.GRAY +
                            " costs " + ChatColor.WHITE + StringUtils.toPrettyString(price) + ChatColor.GREEN + Economy.getInstance().getConfiguration().get("currency-icon"), player);
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_YES, 10, 1);
                }).withSubcommand(
                        new CommandAPICommand("set")
                                .withArguments(new DoubleArgument("value"))
                                .withPermission(CommandPermission.OP)
                                .executesPlayer((player, args) -> {
                                    ItemStack item = player.getInventory().getItemInMainHand();
                                    Economy.getInstance().getPricesManager().setPrice(item.getType(), (double) args[0]);
                                    Economy.getInstance().printChat(ChatColor.BLUE + StringUtils.fixMinecraftName(item.getType()) + ChatColor.GRAY +
                                            "'s cost set to " + ChatColor.WHITE + StringUtils.toPrettyString((double) args[0]) + ChatColor.GREEN + Economy.getInstance().getConfiguration().get("currency-icon"), player);
                                }))
                .withSubcommand(
                        new CommandAPICommand("set")
                                .withArguments(new ItemStackArgument("item"), new DoubleArgument("value"))
                                .withPermission(CommandPermission.OP)
                                .executesPlayer((player, args) -> {
                                    ItemStack item = (ItemStack) args[0];
                                    Economy.getInstance().getPricesManager().setPrice(item.getType(), (double) args[1]);
                                    Economy.getInstance().printChat(ChatColor.BLUE + StringUtils.fixMinecraftName(item.getType()) + ChatColor.GRAY +
                                            "'s cost set to " + ChatColor.WHITE + StringUtils.toPrettyString((double) args[1]) + ChatColor.GREEN + Economy.getInstance().getConfiguration().get("currency-icon"), player);
                                }))
                .withSubcommand(
                        new CommandAPICommand("remove")
                                .withPermission(CommandPermission.OP)
                                .executesPlayer((player, args) -> {
                                    ItemStack item = player.getInventory().getItemInMainHand();
                                    Economy.getInstance().getPricesManager().removePrice(item.getType());
                                    Economy.getInstance().printChat(ChatColor.BLUE + StringUtils.fixMinecraftName(item.getType()) + ChatColor.GRAY +
                                            "'s cost has been " + ChatColor.RED + "removed", player);
                                }))
                .withSubcommand(
                        new CommandAPICommand("remove")
                                .withArguments(new ItemStackArgument("item"))
                                .withPermission(CommandPermission.OP)
                                .executesPlayer((player, args) -> {
                                    ItemStack item = (ItemStack) args[0];
                                    Economy.getInstance().getPricesManager().removePrice(item.getType());
                                    Economy.getInstance().printChat(ChatColor.BLUE + StringUtils.fixMinecraftName(item.getType()) + ChatColor.GRAY +
                                            "'s cost has been " + ChatColor.RED + "removed", player);
                                }))
                .register();
    }
}
