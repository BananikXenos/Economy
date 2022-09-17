package xyz.synse.economy.manager.commands;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTItem;
import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.DoubleArgument;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.synse.economy.Economy;
import xyz.synse.economy.manager.users.User;
import xyz.synse.economy.utils.ItemUtils;
import xyz.synse.economy.utils.StringUtils;

import java.util.ArrayList;

public class WithdrawCommand {
    public void register(){
        new CommandTree("withdraw")
                .executesPlayer((player, args) -> {
                    User user = Economy.getInstance().getUserManager().getUser(player);

                    if (!user.getWallet().hasAtLeast(Economy.getInstance().getConfiguration().get("min-to-withdraw"))) {
                        Economy.getInstance().printChat("You can't withdraw less than " + StringUtils.toPrettyString(Economy.getInstance().getConfiguration().get("min-to-withdraw")) + ". You need "
                                + ChatColor.RED + StringUtils.toPrettyString((double) Economy.getInstance().getConfiguration().get("min-to-withdraw") - user.getWallet().getBalance()) +
                                Economy.getInstance().getConfiguration().get("currency-icon") + ChatColor.GRAY + " more.", player);
                        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 10, 1);
                        return;
                    }

                    ItemStack item = new ItemStack(Material.PAPER, 1);
                    ItemMeta itemMeta = item.getItemMeta();
                    itemMeta.setDisplayName(ChatColor.WHITE + "" + StringUtils.toPrettyString(user.getWallet().getBalance()) + ChatColor.GREEN + Economy.getInstance().getConfiguration().get("currency-icon"));
                    itemMeta.setLore(new ArrayList<String>() {
                        {
                            add(ChatColor.GRAY + "From " + ChatColor.YELLOW + player.getName());
                        }
                    });
                    item.setItemMeta(itemMeta);
                    ItemUtils.setGlow(item);

                    NBTItem nbtItem = new NBTItem(item);

                    NBTCompound economy = nbtItem.addCompound("Economy");
                    economy.setDouble("amount", user.getWallet().getBalance());
                    economy.setString("user", player.getName());

                    Economy.getInstance().printChat("You withdrew " + ChatColor.WHITE + StringUtils.toPrettyString(user.getWallet().getBalance()) + ChatColor.GREEN + Economy.getInstance().getConfiguration().get("currency-icon"), player);
                    user.getWallet().setBalance(0);

                    player.playSound(player.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_DEPLETE, 10, 1);
                    player.getInventory().addItem(nbtItem.getItem());
                }).then(new DoubleArgument("amount")
                        .executesPlayer((player, args) -> {
                            User user = Economy.getInstance().getUserManager().getUser(player);
                            double value = (double) args[0];

                            if (!user.getWallet().hasAtLeast(Economy.getInstance().getConfiguration().get("min-to-withdraw"))) {
                                Economy.getInstance().printChat("You can't withdraw less than " + StringUtils.toPrettyString(Economy.getInstance().getConfiguration().get("min-to-withdraw")) + ". You need "
                                        + ChatColor.RED + StringUtils.toPrettyString((double) Economy.getInstance().getConfiguration().get("min-to-withdraw") - user.getWallet().getBalance()) +
                                        Economy.getInstance().getConfiguration().get("currency-icon") + ChatColor.GRAY + " more.", player);
                                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 10, 1);
                                return;
                            }

                            if (!user.getWallet().hasAtLeast(value)) {
                                Economy.getInstance().printChat("Not " + ChatColor.RED + "enough" + ChatColor.GRAY + " funds. You need "
                                        + ChatColor.RED + StringUtils.toPrettyString(value - user.getWallet().getBalance()) +
                                        Economy.getInstance().getConfiguration().get("currency-icon") + ChatColor.GRAY + " more.", player);
                                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 10, 1);
                                return;
                            }

                            ItemStack item = new ItemStack(Material.PAPER, 1);
                            ItemMeta itemMeta = item.getItemMeta();
                            itemMeta.setDisplayName(ChatColor.WHITE + StringUtils.toPrettyString(value) + ChatColor.GREEN + Economy.getInstance().getConfiguration().get("currency-icon"));
                            itemMeta.setLore(new ArrayList<String>() {
                                {
                                    add(ChatColor.GRAY + "From " + ChatColor.YELLOW + player.getName());
                                }
                            });
                            item.setItemMeta(itemMeta);
                            ItemUtils.setGlow(item);

                            NBTItem nbtItem = new NBTItem(item);

                            NBTCompound economy = nbtItem.addCompound("Economy");
                            economy.setDouble("amount", value);
                            economy.setString("user", player.getName());

                            Economy.getInstance().printChat("You withdrew " + ChatColor.WHITE + StringUtils.toPrettyString(value) + ChatColor.GREEN + Economy.getInstance().getConfiguration().get("currency-icon"), player);
                            user.getWallet().withdraw(value);

                            player.playSound(player.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_CHARGE, 10, 1);
                            player.getInventory().addItem(nbtItem.getItem());
                        }))
                .register();
    }
}
