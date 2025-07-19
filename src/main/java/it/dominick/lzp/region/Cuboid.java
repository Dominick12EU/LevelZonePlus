package it.dominick.lzp.region;

import lombok.Getter;
import org.bukkit.Location;

public abstract class Cuboid {
    @Getter
    protected Location min;
    @Getter
    protected Location max;
    protected int minX;
    protected int minY;
    protected int minZ;
    protected int maxX;
    protected int maxY;
    protected int maxZ;

    public Cuboid(Location point1, Location point2) {
        this.min = new Location(point1.getWorld(),
                Math.min(point1.getX(), point2.getX()),
                Math.min(point1.getY(), point2.getY()),
                Math.min(point1.getZ(), point2.getZ()));
        this.max = new Location(point1.getWorld(),
                Math.max(point1.getX(), point2.getX()),
                Math.max(point1.getY(), point2.getY()),
                Math.max(point1.getZ(), point2.getZ()));
        minX = Math.min(point1.getBlockX(), point2.getBlockX());
        minY = Math.min(point1.getBlockY(), point2.getBlockY());
        minZ = Math.min(point1.getBlockZ(), point2.getBlockZ());
        maxX = Math.max(point1.getBlockX(), point2.getBlockX());
        maxY = Math.max(point1.getBlockY(), point2.getBlockY());
        maxZ = Math.max(point1.getBlockZ(), point2.getBlockZ());
    }

    public Location getCenter() {
        return new Location(min.getWorld(),
                (min.getX() + max.getX()) / 2,
                (min.getY() + max.getY()) / 2,
                (min.getZ() + max.getZ()) / 2);
    }

    public boolean contains(int x, int y, int z) {
        return x >= minX && x <= maxX && y >= minY && y <= maxY && z >= minZ && z <= maxZ;
    }

    public boolean contains(Location location) {
        if (!location.getWorld().equals(min.getWorld())) {
            return false;
        }

        return location.getX() >= min.getX() && location.getX() <= max.getX()
                && location.getY() >= min.getY() && location.getY() <= max.getY()
                && location.getZ() >= min.getZ() && location.getZ() <= max.getZ();
    }

    public abstract void onEnter();
    public abstract void onExit();
}
