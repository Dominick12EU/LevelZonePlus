package it.dominick.lzp.listener;

import it.dominick.lzp.event.RegionJoinEvent;
import it.dominick.lzp.event.RegionQuitEvent;
import it.dominick.lzp.region.CustomRegion;
import it.dominick.lzp.region.manager.RegionManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener {

    private final RegionManager regionManager;

    public PlayerMoveListener(RegionManager regionManager) {
        this.regionManager = regionManager;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Location to = event.getTo();
        Location from = event.getFrom();
        Player player = event.getPlayer();

        if (to == null || from == null || to.equals(from)) {
            return;
        }

        CustomRegion fromRegion = (CustomRegion) regionManager.getRegion(from);
        CustomRegion toRegion = (CustomRegion) regionManager.getRegion(to);

        if (toRegion != null && toRegion != fromRegion) {
            if (!toRegion.canEnter(player)) {
                toRegion.denyEntry(player);
                player.setVelocity(toRegion.calculateKnockbackVector(from, to, regionManager));
                return;
            }
        }

        if (fromRegion != null && !fromRegion.equals(toRegion)) {
            fromRegion.onExit();
            fromRegion.handlePlayerExit(player);
            Bukkit.getPluginManager().callEvent(new RegionQuitEvent(event.getPlayer(), fromRegion));
        }

        if (toRegion != null && !toRegion.equals(fromRegion)) {
            toRegion.onEnter();
            toRegion.handlePlayerEntry(player);
            Bukkit.getPluginManager().callEvent(new RegionJoinEvent(event.getPlayer(), toRegion));
        }
    }
}
