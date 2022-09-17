package xyz.synse.economy.manager.commands;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.DoubleArgument;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.synse.economy.Economy;
import xyz.synse.economy.manager.auction.AuctionGUI;
import xyz.synse.economy.manager.auction.SellableItem;
import xyz.synse.economy.manager.users.User;
import xyz.synse.economy.utils.StringUtils;

import java.io.FileNotFoundException;
import java.util.Arrays;

public class AuctionCommand {
    public void register(){
        new CommandAPICommand("auction")
                .withAliases("ah", "shop")
                .executesPlayer((player, args) -> {
                    AuctionGUI.openAuctionGUI(player);
                }).withSubcommand(new CommandAPICommand("sell")
                        .withArguments(new DoubleArgument("cost"))
                        .executesPlayer((player, args) -> {
                            ItemStack item = player.getInventory().getItemInMainHand();

                            if(item.getType() == Material.AIR)
                            {
                                Economy.getInstance().printChat("You are not holding anything", player);
                                return;
                            }

                            Economy.getInstance().getAuctionManager().addItemToAuction(player, item, (double) args[0]);
                            Economy.getInstance().printChat(ChatColor.BLUE + StringUtils.fixMinecraftName(item.getType()) + ChatColor.GRAY +  " is being sold for " + ChatColor.WHITE + StringUtils.toPrettyString((double) args[0]) + ChatColor.GREEN + Economy.getInstance().getConfiguration().get("currency-icon"), player);
                            item.setAmount(0);
                            player.playSound(player.getLocation(), Sound.BLOCK_SCULK_STEP, 10, 1);
                        })
                ).register();
    }
}
