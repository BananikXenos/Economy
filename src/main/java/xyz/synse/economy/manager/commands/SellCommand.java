package xyz.synse.economy.manager.commands;

import dev.jorel.commandapi.CommandAPICommand;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import xyz.synse.economy.Economy;
import xyz.synse.economy.manager.users.User;
import xyz.synse.economy.utils.StringUtils;

public class SellCommand {
    public void register(){
        new CommandAPICommand("sell").executesPlayer((player, args) -> {
            ItemStack item = player.getInventory().getItemInMainHand();
            if (!Economy.getInstance().getPricesManager().isSellable(item.getType())) {
                Economy.getInstance().printChat(ChatColor.BLUE + StringUtils.fixMinecraftName(item.getType()) + ChatColor.GRAY + " is " + ChatColor.RED + "not" + ChatColor.GRAY + " sellable.", player);
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 10, 1);
                return;
            }

            double price = Economy.getInstance().getPricesManager().getPrice(item.getType()) * item.getAmount();
            User user = Economy.getInstance().getUserManager().getUser(player);

            user.getWallet().deposit(price);
            Economy.getInstance().printChat("Sold " + ChatColor.YELLOW + item.getAmount() + ChatColor.WHITE + "x " + ChatColor.BLUE + StringUtils.fixMinecraftName(item.getType()) + ChatColor.GRAY +
                    " for " + ChatColor.WHITE + StringUtils.toPrettyString(price) + ChatColor.GREEN + Economy.getInstance().getConfiguration().get("currency-icon"), player);
            item.setAmount(0);
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10, 1);
        }).register();
    }
}
