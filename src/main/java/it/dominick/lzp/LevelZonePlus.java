package it.dominick.lzp;

import it.dominick.lzp.listener.PlayerMoveListener;
import it.dominick.lzp.listener.RegionJoinListener;
import it.dominick.lzp.listener.RegionQuitListener;
import it.dominick.lzp.region.CustomRegion;
import it.dominick.lzp.region.manager.RegionManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class LevelZonePlus extends JavaPlugin {

    @Override
    public void onEnable() {
        RegionManager regionManager = new RegionManager(this);

        getServer().getPluginManager().registerEvents(new PlayerMoveListener(regionManager), this);
        getServer().getPluginManager().registerEvents(new RegionJoinListener(), this);
        getServer().getPluginManager().registerEvents(new RegionQuitListener(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
