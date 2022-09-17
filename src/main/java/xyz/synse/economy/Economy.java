package xyz.synse.economy;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIConfig;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.synse.economy.manager.auction.AuctionManager;
import xyz.synse.economy.manager.commands.*;
import xyz.synse.economy.manager.listeners.Listener;
import xyz.synse.economy.manager.prices.PricesManager;
import xyz.synse.economy.manager.users.UserManager;
import xyz.synse.economy.utils.Maker;

import java.io.File;
import java.io.IOException;

public final class Economy extends JavaPlugin {
    private static Economy Instance;
    private CommentedFileConfig config;
    private UserManager userManager;
    private PricesManager pricesManager;
    private AuctionManager auctionManager;

    @Override
    public void onLoad() {
        CommandAPI.onLoad(new CommandAPIConfig().silentLogs(true));
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        Instance = this;

        getLogger().info("Loading config");
        Maker.mkDir(new File(getDataFolder(), "Users"));
        this.config = CommentedFileConfig.builder(new File(getDataFolder(), "Config.toml")).defaultResource("/Config.toml").autosave().build();
        this.config.load();
        getLogger().info("Initializing User Manager");
        this.userManager = new UserManager(new File(getDataFolder(), "Users"));
        getLogger().info("Initializing Prices Manager");
        this.pricesManager = new PricesManager(new File(getDataFolder(), "Prices.toml"), new File(getDataFolder(), "ItemsSellable.toml"));
        getLogger().info("Initializing Auction Manager");
        this.auctionManager = new AuctionManager(new File(getDataFolder(), "Auction.json"));
        getLogger().info("Registering Events");
        this.getServer().getPluginManager().registerEvents(new Listener(), this);
        getLogger().info("Initializing discord bot");

        getLogger().info("Initializing commands");
        new BalanceCommand().register();
        new WithdrawCommand().register();
        new SellCommand().register();
        new PriceCommand().register();
        new EconomyCommand().register();
        new PathFindCommand().register();
        new AuctionCommand().register();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("Saving all users");
        this.userManager.unloadAllUsers();
        getLogger().info("Saving config");
        this.config.save();
        getLogger().info("Saving Auction Items");
        try {
            this.auctionManager.save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Economy getInstance() {
        return Instance;
    }

    public PricesManager getPricesManager() {
        return pricesManager;
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public AuctionManager getAuctionManager() {
        return auctionManager;
    }

    public CommentedFileConfig getConfiguration() {
        return config;
    }

    public void printChat(String message, CommandSender sender) {
        sender.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + ChatColor.BOLD + "Economy" + ChatColor.RESET + ChatColor.WHITE + "] " + ChatColor.GRAY + message);
    }
}
