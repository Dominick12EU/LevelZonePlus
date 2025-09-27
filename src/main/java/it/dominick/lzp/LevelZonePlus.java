package it.dominick.lzp;

import it.dominick.lzp.commands.CmdLevelZone;
import it.dominick.lzp.config.ConfigManager;
import it.dominick.lzp.hook.worldguard.WorldGuardFlagRegistrar;
import it.dominick.lzp.hook.worldguard.WorldGuardHookLoader;
import it.dominick.lzp.listener.PlayerMoveListener;
import it.dominick.lzp.listener.RegionJoinListener;
import it.dominick.lzp.listener.RegionQuitListener;
import it.dominick.lzp.listener.SelectionListener;
import it.dominick.lzp.region.manager.RegionManager;
import lombok.Getter;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

public final class LevelZonePlus extends JavaPlugin {

    @Getter
    public static LevelZonePlus instance;

    @Override
    public void onLoad() {
        if (getServer().getPluginManager().getPlugin("WorldGuard") != null) {
            getLogger().info("[LZP] WorldGuard found");
            try {
                WorldGuardFlagRegistrar.registerFlagsIfPossible();
            } catch (Exception ex) {
                getLogger().severe("[LZP] Error registry flag WG: " + ex.getMessage());
            }
        } else {
            getLogger().info("[LZP] WorldGuard not found");
        }
    }

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        ConfigManager configManager = new ConfigManager(this);
        configManager.createAndCopyResource("messages.yml", "messages.yml");

        RegionManager regionManager = new RegionManager(this);

        getServer().getPluginManager().registerEvents(new PlayerMoveListener(regionManager), this);
        getServer().getPluginManager().registerEvents(new RegionJoinListener(), this);
        getServer().getPluginManager().registerEvents(new RegionQuitListener(), this);

        getServer().getPluginManager().registerEvents(new SelectionListener(regionManager,
                configManager, new NamespacedKey(this, "lzp_wand")), this);

        getCommand("lzp").setExecutor(new CmdLevelZone(configManager, regionManager));

        if (getServer().getPluginManager().getPlugin("WorldGuard") != null) {
            new WorldGuardHookLoader(this).initMovementListeners();
            getLogger().info("[LZP] Hook WG enabled.");
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
