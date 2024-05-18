package it.dominick.lzp;

import it.dominick.lzp.listener.PlayerMoveListener;
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
        RegionManager regionManager = new RegionManager();

        getServer().getPluginManager().registerEvents(new PlayerMoveListener(regionManager), this);

        Location loc1 = new Location(getServer().getWorld("world"), 100, 64, 100);
        Location loc2 = new Location(getServer().getWorld("world"), 200, 80, 200);
        CustomRegion customRegion = new CustomRegion(loc1, loc2);
        regionManager.addRegion(customRegion);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
