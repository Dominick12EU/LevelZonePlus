package it.dominick.lzp.listener;

import it.dominick.lzp.event.RegionJoinEvent;
import it.dominick.lzp.region.CustomRegion;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class RegionJoinListener implements Listener {

    @EventHandler
    public void onRegionJoin(RegionJoinEvent event) {
        Player player = event.getPlayer();
        CustomRegion region = event.getRegion();

        if (!region.canEnter(player)) {
            event.setCancelled(true);
            region.denyEntry(player);
            return;
        }

        region.onEnter();
        region.handlePlayerEntry(player);
    }
}
