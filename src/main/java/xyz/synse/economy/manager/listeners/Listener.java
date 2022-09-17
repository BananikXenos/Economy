package xyz.synse.economy.manager.listeners;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import xyz.synse.economy.Economy;
import xyz.synse.economy.manager.users.User;
import xyz.synse.economy.utils.StringUtils;

import java.io.FileNotFoundException;
import java.io.IOException;

public class Listener implements org.bukkit.event.Listener {
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event){
        Economy.getInstance().getUserManager().getUser(event.getPlayer()).onPlayerMove(event.getTo());
    }

    @EventHandler
    public void onItemUse(PlayerInteractEvent event){
        if(event.hasItem() && event.getItem().getType() == Material.PAPER){
            NBTItem nbtItem = new NBTItem(event.getItem());

            NBTCompound compound = nbtItem.getCompound("Economy");

            if(compound == null)
                return;

            User user = Economy.getInstance().getUserManager().getUser(event.getPlayer());
            user.getWallet().deposit(compound.getDouble("amount"));

            Economy.getInstance().printChat("Deposited " + ChatColor.WHITE + StringUtils.toPrettyString(compound.getDouble("amount")) + ChatColor.GREEN +
                    Economy.getInstance().getConfiguration().get("currency-icon") + ChatColor.GRAY + " from " + ChatColor.YELLOW + compound.getString("user"), event.getPlayer());
            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10, 1);

            event.getItem().setAmount(event.getItem().getAmount() - 1);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event){
        Economy.getInstance().getUserManager().getUser(event.getEntity()).onPlayerDeath();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event){
        try {
            Economy.getInstance().getUserManager().loadUser(event.getPlayer());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event){
        try {
            Economy.getInstance().getUserManager().unloadUser(event.getPlayer());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
