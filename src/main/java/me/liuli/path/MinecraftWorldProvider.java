package me.liuli.path;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

public class MinecraftWorldProvider implements IWorldProvider {
    private World world;

    public MinecraftWorldProvider(World world) {
        this.world = world;
    }

    @Override
    public boolean isBlocked(Cell cell) {
        Block block = world.getBlockAt(cell.x, cell.y, cell.z);
        Block blockUnder = world.getBlockAt(cell.x, cell.y - 1, cell.z);
        Block blockAbove = world.getBlockAt(cell.x, cell.y + 1, cell.z);

        if(blockUnder.getType() == Material.AIR || blockUnder.getType() == Material.CAVE_AIR
                || blockUnder.getType() == Material.VOID_AIR || blockUnder.getType() == Material.GRASS
                || blockUnder.getType() == Material.TALL_GRASS || blockUnder.getType() == Material.WATER)
            return true;
        if(block.getType() != Material.AIR && block.getType() != Material.CAVE_AIR
                && block.getType() != Material.VOID_AIR && block.getType() != Material.GRASS
                && block.getType() != Material.TALL_GRASS)
            return true;
        return blockAbove.getType() != Material.AIR && blockAbove.getType() != Material.CAVE_AIR
                && blockAbove.getType() != Material.VOID_AIR && blockAbove.getType() != Material.GRASS
                && blockAbove.getType() != Material.TALL_GRASS;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public World getWorld() {
        return world;
    }
}
