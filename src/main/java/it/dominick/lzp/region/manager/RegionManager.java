package it.dominick.lzp.region.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.dominick.lzp.LevelZonePlus;
import it.dominick.lzp.region.CustomRegion;
import it.dominick.lzp.region.RegionData;
import it.dominick.lzp.utils.LocationAdapter;
import it.dominick.lzp.utils.RNG;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class RegionManager {

    @Getter
    private final Map<String, CustomRegion> regions = new HashMap<>();
    private static final Map<Player, Location> playerPos1Map = new HashMap<>();
    private static final Map<Player, Location> playerPos2Map = new HashMap<>();
    private final File regionsFolder;
    private final Gson gson;

    public RegionManager(LevelZonePlus plugin) {
        this.regionsFolder = new File(plugin.getDataFolder(), "regions");
        if (!regionsFolder.exists()) {
            regionsFolder.mkdirs();
        }
        this.gson = new GsonBuilder().setPrettyPrinting()
                .registerTypeAdapter(Location.class, new LocationAdapter())
                .excludeFieldsWithoutExposeAnnotation()
                .create();
        loadRegions();
    }

    public void addRegion(String name, Location point1, Location point2) {
        CustomRegion region = new CustomRegion(point1, point2);
        regions.put(name.toLowerCase(), region);
        saveRegion(new RegionData(name, point1, point2));
    }

    public void removeRegion(String name) {
        regions.remove(name.toLowerCase());
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

    public boolean regionExists(String name) {
        return regions.containsKey(name.toLowerCase());
    }

    private void saveRegion(RegionData regionData) {
        File regionFile = new File(regionsFolder, regionData.getName().toLowerCase() + ".json");
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("name", regionData.getName());
        jsonObject.add("pos1", gson.toJsonTree(regionData.getPoint1()));
        jsonObject.add("pos2", gson.toJsonTree(regionData.getPoint2()));
        try (Writer writer = new FileWriter(regionFile)) {
            gson.toJson(jsonObject, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadRegions() {
        File[] files = regionsFolder.listFiles((dir, name) -> name.endsWith(".json"));
        if (files != null) {
            for (File file : files) {
                try (Reader reader = new FileReader(file)) {
                    JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
                    String name = jsonObject.get("name").getAsString();
                    JsonObject pos1Obj = jsonObject.getAsJsonObject("pos1");
                    JsonObject pos2Obj = jsonObject.getAsJsonObject("pos2");

                    Location pos1 = gson.fromJson(pos1Obj, Location.class);
                    Location pos2 = gson.fromJson(pos2Obj, Location.class);

                    RegionData regionData = new RegionData(name, pos1, pos2);

                    CustomRegion region = new CustomRegion(regionData.getPoint1(), regionData.getPoint2());
                    regions.put(regionData.getName().toLowerCase(), region);
                    System.out.println("Loaded region: " + regionData.getName());
                } catch (FileNotFoundException e) {
                    System.err.println("File not found: " + file.getName());
                    e.printStackTrace();
                } catch (IOException e) {
                    System.err.println("IOException while reading file: " + file.getName());
                    e.printStackTrace();
                } catch (Exception e) {
                    System.err.println("Unexpected error while loading region from file: " + file.getName());
                    e.printStackTrace();
                }
            }
        } else {
            System.err.println("No region files found in the regions folder.");
        }
    }

    public void setPlayerPos1(Player player, Location pos1) {
        playerPos1Map.put(player, pos1);
        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 0.25f, RNG.r.f(0.125f, 1.95f));
    }

    public Location getPlayerPos1(Player player) {
        return playerPos1Map.get(player);
    }

    public void setPlayerPos2(Player player, Location pos2) {
        playerPos2Map.put(player, pos2);
        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 0.25f, RNG.r.f(0.125f, 1.95f));
    }

    public Location getPlayerPos2(Player player) {
        return playerPos2Map.get(player);
    }
}
