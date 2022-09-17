package xyz.synse.economy.manager.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.LocationArgument;
import me.liuli.path.Cell;
import me.liuli.path.MinecraftWorldProvider;
import me.liuli.path.Pathfinder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import xyz.synse.economy.Economy;

import java.util.ArrayList;
import java.util.HashMap;

public class PathFindCommand {
    public void register() {
        new CommandAPICommand("pathfind")
                .withArguments(new LocationArgument("to"))
                .executesPlayer((player, args) -> {
                    new Thread(() -> {
                        Location location = (Location) args[0];
                        Pathfinder pathfinder = new Pathfinder(new Cell((int) player.getLocation().getX(), (int) player.getLocation().getY(), (int) player.getLocation().getZ()),
                                new Cell((int) location.getX(), (int) location.getY(), (int) location.getZ()), Pathfinder.DIAGONAL_NEIGHBORS, new MinecraftWorldProvider(player.getWorld()));

                        ArrayList<Cell> path = pathfinder.findPath(50000);
                        HashMap<Location, BlockCache> blocks = new HashMap<>();

                        Bukkit.getScheduler().scheduleSyncDelayedTask(Economy.getInstance(), new Runnable() {

                            @Override
                            public void run() {
                                for(Cell cell : path){
                                    Location loc = new Location(location.getWorld(), cell.x, cell.y, cell.z);
                                    Block block = loc.getBlock();

                                    blocks.put(loc, new BlockCache(block.getBlockData(), block.getType(), block.getState()));
                                    loc.getBlock().setType(Material.RED_CONCRETE);
                                }
                                Economy.getInstance().printChat("Location found. Length " + path.size(), player);
                            }

                        }, 20);

                        Bukkit.getScheduler().scheduleSyncDelayedTask(Economy.getInstance(), new Runnable() {

                            @Override
                            public void run() {
                                for(Cell cell : path){
                                    Location loc = new Location(location.getWorld(), cell.x, cell.y, cell.z);
                                    Block block = loc.getBlock();
                                    BlockCache cache = blocks.get(loc);

                                    block.setBlockData(cache.data);
                                    block.setType(cache.material);
                                    cache.state.update(true);
                                }
                                Economy.getInstance().printChat("Restored. Length " + path.size(), player);
                            }
                        }, 200);
                    }).start();
                }).register();
    }

    class BlockCache{
        private final BlockData data;
        private final Material material;
        private final BlockState state;

        BlockCache(BlockData data, Material material, BlockState state) {
            this.data = data;
            this.material = material;
            this.state = state;
        }
    }
}
