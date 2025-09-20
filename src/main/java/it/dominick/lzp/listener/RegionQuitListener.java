package it.dominick.lzp.listener;

import it.dominick.lzp.event.RegionQuitEvent;
import it.dominick.lzp.region.CustomRegion;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class RegionQuitListener implements Listener {
    @EventHandler
    public void onRegionJoin(RegionQuitEvent event) {
        Player player = event.getPlayer();
        CustomRegion region = event.getRegion();

        if (!region.canEnter(player)) return;

        region.onExit();
        region.handlePlayerExit(player);
    }
}
