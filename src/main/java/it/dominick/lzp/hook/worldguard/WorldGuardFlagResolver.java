package it.dominick.lzp.hook.worldguard;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public final class WorldGuardFlagResolver {

    private WorldGuardFlagResolver() {}

    private static RegionContainer container() {
        return WorldGuard.getInstance().getPlatform().getRegionContainer();
    }

    public static RegionManager getManager(World bukkitWorld) {
        return container().get(BukkitAdapter.adapt(bukkitWorld));
    }

    public static ApplicableRegionSet getApplicable(Location loc) {
        RegionManager manager = getManager(loc.getWorld());
        if (manager == null) return null;
        return manager.getApplicableRegions(BukkitAdapter.asBlockVector(loc));
    }

    public static Integer resolveDefaultMinLevel(Location loc) {
        var set = getApplicable(loc);
        if (set == null) return null;
        Integer max = null;
        for (var region : set) {
            Integer v = region.getFlag(WorldGuardHookLoader.DEFAULT_MIN_LVL);
            if (v != null) max = (max == null) ? v : Math.max(max, v);
        }
        return max;
    }

    public static Integer resolveAlonsoMinLevel(Location loc) {
        var set = getApplicable(loc);
        if (set == null) return null;
        Integer max = null;
        for (var region : set) {
            Integer v = region.getFlag(WorldGuardHookLoader.ALONSO_MIN_LVL);
            if (v != null) max = (max == null) ? v : Math.max(max, v);
        }
        return max;
    }

    public static boolean hasLzpSupport(ProtectedRegion region) {
        if (region == null) return false;
        Integer def = region.getFlag(WorldGuardHookLoader.DEFAULT_MIN_LVL);
        Integer alo = region.getFlag(WorldGuardHookLoader.ALONSO_MIN_LVL);
        return def != null || alo != null;
    }

    public static Map<String, ProtectedRegion> listLzpRegions(World world) {
        RegionManager rm = getManager(world);
        if (rm == null) return Collections.emptyMap();
        return rm.getRegions().entrySet().stream()
                .filter(e -> hasLzpSupport(e.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public static Integer getDefaultMin(ProtectedRegion region) {
        return region.getFlag(WorldGuardHookLoader.DEFAULT_MIN_LVL);
    }

    public static Integer getAlonsoMin(ProtectedRegion region) {
        return region.getFlag(WorldGuardHookLoader.ALONSO_MIN_LVL);
    }

    public static boolean removeLZPSupport(ProtectedRegion region) {
        boolean changed = false;
        if (region.getFlag(WorldGuardHookLoader.DEFAULT_MIN_LVL) != null) {
            region.setFlag(WorldGuardHookLoader.DEFAULT_MIN_LVL, null);
            changed = true;
        }
        if (region.getFlag(WorldGuardHookLoader.ALONSO_MIN_LVL) != null) {
            region.setFlag(WorldGuardHookLoader.ALONSO_MIN_LVL, null);
            changed = true;
        }
        return changed;
    }

    public static boolean removeLZPSupport(World world, String regionId, boolean save) {
        RegionManager rm = getManager(world);
        if (rm == null) return false;
        ProtectedRegion r = rm.getRegion(regionId);
        if (r == null) return false;

        boolean changed = removeLZPSupport(r);
        if (changed && save) {
            try {
                rm.save();
            } catch (StorageException e) {
                e.printStackTrace();
            }
        }
        return changed;
    }
}
