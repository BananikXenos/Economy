package xyz.synse.economy.manager.auction;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.synse.economy.Economy;
import xyz.synse.economy.manager.users.User;
import xyz.synse.economy.utils.StringUtils;

import java.io.FileNotFoundException;

public class AuctionGUI {
    public static void openAuctionGUI(Player player){
        ChestGui gui = new ChestGui(6, "Auction");

        PaginatedPane pages = new PaginatedPane(0, 0, 9, 5);
        pages.populateWithItemStacks(
                Economy.getInstance().getAuctionManager().getSellableItemStacksFormatted()
        );
        pages.setOnClick(event -> {
            event.setCancelled(true);

            if(event.getCurrentItem() == null)
                return;

            try {
                //buy item
                User user = Economy.getInstance().getUserManager().getUser(player);
                SellableItem sellableItem = Economy.getInstance().getAuctionManager().getFromItemStack(event.getCurrentItem());
                User seller = Economy.getInstance().getUserManager().getUser(sellableItem.getSeller());

                if (!user.getWallet().hasAtLeast(sellableItem.getPrice())) {
                    Economy.getInstance().printChat("Not " + ChatColor.RED + "enough" + ChatColor.GRAY + " funds. You need "
                            + ChatColor.RED + StringUtils.toPrettyString(sellableItem.getPrice() - user.getWallet().getBalance()) +
                            Economy.getInstance().getConfiguration().get("currency-icon") + ChatColor.GRAY + " more.", player);
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 10, 1);
                    return;
                }

                seller.getWallet().deposit(sellableItem.getPrice());
                user.getWallet().withdraw(sellableItem.getPrice());

                Economy.getInstance().printChat("Bought " + ChatColor.YELLOW + sellableItem.getItemStack().getAmount() + ChatColor.WHITE + "x " +
                        ChatColor.BLUE + StringUtils.fixMinecraftName(sellableItem.getItemStack().getType()) + ChatColor.GRAY + " for " +
                        ChatColor.WHITE + StringUtils.toPrettyString(sellableItem.getPrice()) + ChatColor.GREEN + Economy.getInstance().getConfiguration().get("currency-icon"), player);

                if (seller.isOnline()) {
                    Player sellerPlayer = seller.getPlayer();

                    Economy.getInstance().printChat(player.getName() + " bought " + ChatColor.YELLOW + sellableItem.getItemStack().getAmount() + ChatColor.WHITE + "x " +
                            ChatColor.BLUE + StringUtils.fixMinecraftName(sellableItem.getItemStack().getType()) + ChatColor.GRAY + " for " +
                            ChatColor.WHITE + StringUtils.toPrettyString(sellableItem.getPrice()) + ChatColor.GREEN + Economy.getInstance().getConfiguration().get("currency-icon"), sellerPlayer);
                }

                player.getInventory().addItem(sellableItem.getItemStack());
                Economy.getInstance().getAuctionManager().getSellableItems().remove(sellableItem);
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10, 1);
                pages.clear();
                pages.populateWithItemStacks(
                        Economy.getInstance().getAuctionManager().getSellableItemStacksFormatted()
                );
                gui.update();
            }catch (Exception ex){
                Economy.getInstance().printChat("An " + ChatColor.RED + "error" + ChatColor.GRAY + " occurred while using auction", player);
            }
        });

        gui.addPane(pages);

        OutlinePane background = new OutlinePane(0, 5, 9, 1);
        background.addItem(new GuiItem(new ItemStack(Material.BLACK_STAINED_GLASS_PANE)));
        background.setRepeat(true);
        background.setPriority(Pane.Priority.LOWEST);
        background.setOnClick(event -> event.setCancelled(true));

        gui.addPane(background);

        StaticPane navigation = new StaticPane(0, 5, 9, 1);
        ItemStack backItem = new ItemStack(Material.RED_WOOL);
        ItemMeta backMeta = backItem.getItemMeta();
        backMeta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Back");
        backItem.setItemMeta(backMeta);
        navigation.addItem(new GuiItem(backItem, event -> {
            if (pages.getPage() > 0) {
                pages.setPage(pages.getPage() - 1);

                gui.update();
            }
            event.setCancelled(true);
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10, 1);
        }), 0, 0);

        ItemStack nextItem = new ItemStack(Material.GREEN_WOOL);
        ItemMeta nextMeta = nextItem.getItemMeta();
        nextMeta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Next");
        nextItem.setItemMeta(nextMeta);
        navigation.addItem(new GuiItem(nextItem, event -> {
            if (pages.getPage() < pages.getPages() - 1) {
                pages.setPage(pages.getPage() + 1);

                gui.update();
            }
            event.setCancelled(true);
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10, 1);
        }), 8, 0);

        ItemStack closeItem = new ItemStack(Material.BARRIER);
        ItemMeta closeMeta = closeItem.getItemMeta();
        closeMeta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "CLOSE");
        closeItem.setItemMeta(closeMeta);
        navigation.addItem(new GuiItem(closeItem, event ->
                event.getWhoClicked().closeInventory()), 4, 0);

        gui.addPane(navigation);
        gui.show(player);
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10, 1);
    }
}
