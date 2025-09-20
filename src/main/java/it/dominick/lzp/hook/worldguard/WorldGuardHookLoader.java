package it.dominick.lzp.hook.worldguard;

import com.sk89q.worldguard.protection.flags.IntegerFlag;
import it.dominick.lzp.LevelZonePlus;
import org.bukkit.Bukkit;

public class WorldGuardHookLoader {

    public static final String DEFAULT_FLAG_ID = "lzp-default-min-lvl";
    public static final String ALONSO_FLAG_ID  = "lzp-alonsolevels-min-lvl";

    static IntegerFlag DEFAULT_MIN_LVL;
    static IntegerFlag ALONSO_MIN_LVL;

    private final LevelZonePlus plugin;

    public WorldGuardHookLoader(LevelZonePlus plugin) {
        this.plugin = plugin;
    }

    public void initMovementListeners() {
        Bukkit.getPluginManager().registerEvents(new WorldGuardMovementListener(plugin), plugin);
    }
}
