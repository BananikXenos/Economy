package xyz.synse.economy.manager.users;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import xyz.synse.economy.Economy;
import xyz.synse.economy.utils.Position;

import java.util.UUID;

public class User {
    private UUID uniqueID;
    private Position position;
    private Wallet wallet = new Wallet();
    private long deaths;

    public User(UUID uniqueID) {
        this.uniqueID = uniqueID;
    }

    public Position getPosition() {
        return position;
    }

    public Wallet getWallet() {
        if (wallet == null)
            wallet = new Wallet();
        return wallet;
    }

    public void onPlayerMove(Location location) {
        this.position = new Position(location.getWorld(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    public void onPlayerDeath() {
        this.deaths++;
        Economy.getInstance().printChat("You " + ChatColor.RED + "died" + ChatColor.GRAY + " at " + this.position.asCoords(), getPlayer());

        Economy.getInstance().printChat("You have lost " + ChatColor.RED + (100 - (double) Economy.getInstance().getConfiguration().get("percentage-on-death")) + "% "
                + ChatColor.GRAY + "of your money. " + ChatColor.RED + "(" + (getWallet().getBalance() - ((getWallet().getBalance() / 100D) * (double) Economy.getInstance().getConfiguration().get("percentage-on-death"))) + Economy.getInstance().getConfiguration().get("currency-icon") + ")", getPlayer());
        getWallet().setBalance((getWallet().getBalance() / 100D) * (double) Economy.getInstance().getConfiguration().get("percentage-on-death"));
    }

    public long getDeaths() {
        return deaths;
    }

    public UUID getUniqueID() {
        return uniqueID;
    }

    public boolean isOnline() {
        return Bukkit.getPlayer(uniqueID) != null;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uniqueID);
    }

    public OfflinePlayer getOfflinePlayer() {
        return Bukkit.getOfflinePlayer(uniqueID);
    }
}
