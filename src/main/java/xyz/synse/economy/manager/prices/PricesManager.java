package xyz.synse.economy.manager.prices;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import org.bukkit.Material;
import xyz.synse.economy.utils.Maker;

import java.io.File;

public class PricesManager {
    private CommentedFileConfig config;

    public PricesManager(File config, File allPrices) {
        this.config = CommentedFileConfig.builder(config).defaultResource("/Prices.toml").autosave().build();
        this.config.load();

        CommentedFileConfig allPricesConfig = CommentedFileConfig.builder(Maker.mkFile(allPrices)).build();
        for(Material mat : Material.values()){
            allPricesConfig.set(mat.name(), 0);
        }
        allPricesConfig.save();
    }

    public boolean isSellable(Material material){
        return this.config.get(material.name()) != null;
    }

    public double getPrice(Material material){
        return this.config.get(material.name());
    }

    public void setPrice(Material material, double value){
        this.config.set(material.name(), value);
    }

    public void removePrice(Material material){
        if(this.config.contains(material.name()))
            this.config.remove(material.name());
    }

    public CommentedFileConfig getConfig() {
        return config;
    }
}
