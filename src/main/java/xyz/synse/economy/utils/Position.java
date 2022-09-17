package xyz.synse.economy.utils;

import org.bukkit.World;

import java.util.UUID;

public class Position {
    private UUID worldUniqueID;
    private String worldName;
    private double x;
    private double y;
    private double z;
    private float pitch;
    private float yaw;

    public Position(World world, double x, double y, double z, float yaw, float pitch) {
        this.worldUniqueID = world.getUID();
        this.worldName = world.getName();
        this.x = x;
        this.y = y;
        this.z = z;
        this.pitch = pitch;
        this.yaw = yaw;
    }

    public UUID getWorldUniqueID() {
        return worldUniqueID;
    }

    public String getWorldName() {
        return worldName;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public String asCoords() {
        return "x=" + x +
                ", y=" + y +
                ", z=" + z;
    }
}
