package xyz.synse.economy.manager.auction;

import com.google.gson.annotations.Expose;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public class SellableItem {
    @Expose
    private UUID seller;
    @Expose
    private String username;
    @Expose
    private String itemStack;
    private ItemStack cachedItemStack;
    @Expose
    private double price;

    public SellableItem(Player seller, ItemStack itemStack, double price) {
        this.seller = seller.getUniqueId();
        this.username = seller.getName();
        this.itemStack = itemStackToBase64(itemStack);
        this.cachedItemStack = itemStack;
        this.price = price;
    }

    public String getUsername() {
        return username;
    }

    public UUID getSeller() {
        return seller;
    }

    public ItemStack getItemStack() {
        try {
            return cachedItemStack == null ? cachedItemStack = itemStackFromBase64(itemStack) : cachedItemStack;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public double getPrice() {
        return price;
    }

    public static String itemStackToBase64(ItemStack item) throws IllegalStateException {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            // Save every element in the list
            dataOutput.writeObject(item);

            // Serialize that array
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save item stacks.", e);
        }
    }

    public static ItemStack itemStackFromBase64(String data) throws IOException {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack item = (ItemStack) dataInput.readObject();

            dataInput.close();
            return item;
        } catch (ClassNotFoundException e) {
            throw new IOException("Unable to decode class type.", e);
        }
    }
}
