package xyz.synse.economy.manager.auction;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.synse.economy.Economy;
import xyz.synse.economy.utils.StringUtils;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class AuctionManager {
    private ArrayList<SellableItem> sellableItems = new ArrayList<>();
    private final File config;
    private Gson gson = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create();

    public AuctionManager(File cfg) {
        this.config = cfg;
        if (!config.exists()) {
            try {
                save();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            sellableItems = load();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void addItemToAuction(Player player, ItemStack itemStack, double cost) {
        this.sellableItems.add(new SellableItem(player, itemStack.clone(), cost));
    }

    public ArrayList<SellableItem> getSellableItems() {
        return sellableItems;
    }

    public SellableItem getFromItemStack(ItemStack itemStack) {
        for (SellableItem item : sellableItems) {
            if (item.getItemStack().getType() == itemStack.getType()
                    && item.getItemStack().getAmount() == itemStack.getAmount()
            && item.getItemStack().getItemMeta().getEnchants().equals(itemStack.getItemMeta().getEnchants()))
                return item;
        }

        return null;
    }

    public ArrayList<ItemStack> getSellableItemStacks() {
        ArrayList<ItemStack> items = new ArrayList<>();
        for (SellableItem item : sellableItems) {
            items.add(item.getItemStack());
        }
        return items;
    }

    public ArrayList<ItemStack> getSellableItemStacksFormatted() {
        ArrayList<ItemStack> items = new ArrayList<>();
        for (SellableItem item : sellableItems) {
            ItemStack itemStack = item.getItemStack().clone();
            ItemMeta itemMeta = itemStack.getItemMeta();

            List<String> lore = itemMeta.getLore() == null ? new ArrayList<>() : itemMeta.getLore();
            lore.add(ChatColor.GRAY + "Price " + ChatColor.WHITE + StringUtils.toPrettyString(item.getPrice()) + ChatColor.GREEN + Economy.getInstance().getConfiguration().get("currency-icon"));
            lore.add(ChatColor.GRAY + "Seller " + ChatColor.WHITE + item.getUsername());
            itemMeta.setLore(lore);

            itemStack.setItemMeta(itemMeta);

            items.add(itemStack);
        }
        return items;
    }

    public void save() throws IOException {
        if (!config.exists()) {
            config.createNewFile();
        }

        Type listType = new TypeToken<ArrayList<SellableItem>>(){}.getType();
        FileWriter fw = new FileWriter(config, false);
        fw.write(gson.toJson(sellableItems, listType));
        fw.flush();
        fw.close();
    }

    public ArrayList<SellableItem> load() throws FileNotFoundException {
        if(!config.exists())
            return new ArrayList<>();

        Type listType = new TypeToken<ArrayList<SellableItem>>(){}.getType();
        return gson.fromJson(new FileReader(config), listType);
    }
}
