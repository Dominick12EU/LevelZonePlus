package it.dominick.lzp.region;

import org.bukkit.Location;

public class RegionData {
    private String name;
    private Location point1;
    private Location point2;

    public RegionData(String name, Location point1, Location point2) {
        this.name = name;
        this.point1 = point1;
        this.point2 = point2;
    }

    public String getName() {
        return name;
    }

    public Location getPoint1() {
        return point1;
    }

    public Location getPoint2() {
        return point2;
    }
}
