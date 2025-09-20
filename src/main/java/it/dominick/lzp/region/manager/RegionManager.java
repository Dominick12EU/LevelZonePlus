package it.dominick.lzp.region.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.dominick.lzp.LevelZonePlus;
import it.dominick.lzp.config.ConfigFile;
import it.dominick.lzp.config.ConfigManager;
import it.dominick.lzp.hook.EntryHook;
import it.dominick.lzp.hook.EntryHookFactory;
import it.dominick.lzp.hook.HookType;
import it.dominick.lzp.region.CustomRegion;
import it.dominick.lzp.region.RegionData;
import it.dominick.lzp.utils.LocationAdapter;
import it.dominick.lzp.utils.RNG;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class RegionManager {

    @Getter
    private final Map<String, CustomRegion> regions = new HashMap<>();
    private static final Map<Player, Location> playerPos1Map = new HashMap<>();
    private static final Map<Player, Location> playerPos2Map = new HashMap<>();
    private final File regionsFolder;
    private final ConfigManager configManager;
    private final Gson gson;
    private final PluginManager pluginManager;

    public RegionManager(LevelZonePlus plugin) {
        this.pluginManager = plugin.getServer().getPluginManager();
        this.configManager = new ConfigManager(plugin);
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

    public void addRegion(String name, Location point1, Location point2, HookType hookType, int minLevel) {
        EntryHook entryHook = EntryHookFactory.createHook(hookType);
        if (!entryHook.isValid(pluginManager)) {
            System.err.println("Hook type " + hookType + " is not valid. Region not added.");
            return;
        }

        CustomRegion region = new CustomRegion(point1, point2, hookType, minLevel);
        regions.put(name.toLowerCase(), region);
        saveRegion(new RegionData(name, hookType, minLevel, point1, point2));
    }

    public void removeRegion(String name) {
        regions.remove(name.toLowerCase());
        configManager.deleteConfig(new ConfigFile(name + ".yml", "regions"));
    }

    public CustomRegion getRegion(Location location) {
        return regions.values().stream()
                .filter(region -> region.contains(location.getBlockX(), location.getBlockY(), location.getBlockZ()))
                .findFirst()
                .orElse(null);
    }

    public CustomRegion getRegion(String name) {
        return regions.get(name.toLowerCase());
    }

    public RegionData getRegionData(String name) {
        ConfigFile configFile = new ConfigFile(name.toLowerCase() + ".yml", "regions");
        FileConfiguration config = configManager.getConfig(configFile);

        if (config == null) {
            return null;
        }

        String regionName = config.getString("name");
        HookType hookType = HookType.valueOf(config.getString("hook.type"));
        int minLevel = config.getInt("hook.min-level");
        Location pos1 = config.getLocation("pos1");
        Location pos2 = config.getLocation("pos2");

        return new RegionData(regionName, hookType, minLevel, pos1, pos2);
    }

    public boolean regionExists(String name) {
        return regions.containsKey(name.toLowerCase());
    }

    private void saveRegion(RegionData regionData) {
        ConfigFile configFile = new ConfigFile(regionData.getName().toLowerCase() + ".yml", "regions");
        FileConfiguration config = configManager.getConfig(configFile);
        config.set("name", regionData.getName());
        config.set("hook.type", regionData.getHookType().name());
        config.set("hook.min-level", regionData.getMinLevel());
        config.set("pos1", regionData.getPoint1());
        config.set("pos2", regionData.getPoint2());
        configManager.saveConfig(configFile, config);
    }

    private void loadRegions() {
        File[] files = regionsFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files != null) {
            for (File file : files) {
                ConfigFile configFile = new ConfigFile(file.getName(), "regions");
                FileConfiguration config = configManager.getConfig(configFile);
                if (config == null) {
                    continue;
                }

                String name = config.getString("name");
                HookType hookType = HookType.valueOf(config.getString("hook.type"));
                int minLevel = config.getInt("hook.min-level");
                Location pos1 = config.getLocation("pos1");
                Location pos2 = config.getLocation("pos2");

                EntryHook entryHook = EntryHookFactory.createHook(hookType);
                if (!entryHook.isValid(pluginManager)) {
                    System.err.println("Hook type " + hookType + " is not valid. Region " + name + " not loaded.");
                    continue;
                }

                CustomRegion region = new CustomRegion(pos1, pos2, hookType, minLevel);
                regions.put(name.toLowerCase(), region);
                System.out.println("Loaded region: " + name + " (Hook: " + hookType + ")");
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
