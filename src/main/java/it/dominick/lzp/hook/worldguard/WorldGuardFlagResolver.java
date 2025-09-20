package it.dominick.lzp.hook.worldguard;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Location;

public final class WorldGuardFlagResolver {

    private WorldGuardFlagResolver() {}

    private static ApplicableRegionSet getApplicable(Location loc) {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager manager = container.get(BukkitAdapter.adapt(loc.getWorld()));
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
}
