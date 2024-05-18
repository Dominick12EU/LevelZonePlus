package it.dominick.lzp.region.manager;

import it.dominick.lzp.region.Cuboid;
import org.bukkit.Location;
import java.util.ArrayList;
import java.util.List;

public class RegionManager {
    private List<Cuboid> regions;

    public RegionManager() {
        this.regions = new ArrayList<>();
    }

    public void addRegion(Cuboid region) {
        regions.add(region);
    }

    public void removeRegion(Cuboid region) {
        regions.remove(region);
    }

    public Cuboid getRegion(Location location) {
        for (Cuboid region : regions) {
            if (region.contains(location)) {
                return region;
            }
        }
        return null;
    }

    public List<Cuboid> getRegions() {
        return new ArrayList<>(regions);
    }
}
