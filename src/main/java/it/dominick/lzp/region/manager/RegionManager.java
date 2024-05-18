package it.dominick.lzp.region.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.dominick.lzp.LevelZonePlus;
import it.dominick.lzp.region.CustomRegion;
import it.dominick.lzp.region.RegionData;
import org.bukkit.Location;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class RegionManager {

    private final Map<String, CustomRegion> regions = new HashMap<>();
    private final File regionsFolder;
    private final Gson gson;

    public RegionManager(LevelZonePlus plugin) {
        this.regionsFolder = new File(plugin.getDataFolder(), "regions");
        if (!regionsFolder.exists()) {
            regionsFolder.mkdirs();
        }
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        loadRegions();
    }

    public void addRegion(String name, Location point1, Location point2) {
        CustomRegion region = new CustomRegion(point1, point2);
        regions.put(name, region);
        saveRegion(new RegionData(name, point1, point2));
    }

    public void removeRegion(String name) {
        regions.remove(name);
        File regionFile = new File(regionsFolder, name + ".json");
        if (regionFile.exists()) {
            regionFile.delete();
        }
    }

    public CustomRegion getRegion(Location location) {
        return regions.values().stream()
                .filter(region -> region.contains(location.getBlockX(), location.getBlockY(), location.getBlockZ()))
                .findFirst()
                .orElse(null);
    }

    private void saveRegion(RegionData regionData) {
        File regionFile = new File(regionsFolder, regionData.getName() + ".json");
        try (Writer writer = new FileWriter(regionFile)) {
            gson.toJson(regionData, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadRegions() {
        File[] files = regionsFolder.listFiles((dir, name) -> name.endsWith(".json"));
        if (files != null) {
            for (File file : files) {
                try (Reader reader = new FileReader(file)) {
                    RegionData regionData = gson.fromJson(reader, RegionData.class);
                    CustomRegion region = new CustomRegion(regionData.getPoint1(), regionData.getPoint2());
                    regions.put(regionData.getName(), region);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Map<String, CustomRegion> getRegions() {
        return regions;
    }
}
