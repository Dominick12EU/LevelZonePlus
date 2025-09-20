package it.dominick.lzp.hook.worldguard;

import it.dominick.lzp.LevelZonePlus;
import it.dominick.lzp.hook.EntryHook;
import it.dominick.lzp.hook.EntryHookFactory;
import it.dominick.lzp.hook.HookType;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.Vector;

@RequiredArgsConstructor
public class WorldGuardMovementListener implements Listener {

    private final LevelZonePlus plugin;

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onMove(PlayerMoveEvent e) {
        if (sameBlock(e.getFrom(), e.getTo())) return;
        Player p = e.getPlayer();
        Location from = e.getFrom();
        Location to   = e.getTo();

        KnockbackDecision kb = checkAndBuildKb(p, to, from);
        if (!kb.allowed) {
            p.setVelocity(kb.vector);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent e) {
        Player p = e.getPlayer();
        Location from = e.getFrom();
        Location to   = e.getTo();

        KnockbackDecision kb = checkAndBuildKb(p, to, from);
        if (!kb.allowed) {
            e.setCancelled(true);
        }
    }

    private KnockbackDecision checkAndBuildKb(Player player, Location dest, Location from) {
        Integer reqDefault = WorldGuardFlagResolver.resolveDefaultMinLevel(dest);
        Integer reqAlonso  = WorldGuardFlagResolver.resolveAlonsoMinLevel(dest);

        boolean okDefault = true;
        boolean okAlonso  = true;

        if (reqDefault != null) {
            EntryHook defaultHook = EntryHookFactory.createHook(HookType.DEFAULT);
            okDefault = defaultHook.isValid(plugin.getServer().getPluginManager())
                    && defaultHook.canEnter(player, reqDefault);
            if (!okDefault) sendDeny(player, "DEFAULT", reqDefault);
        }

        if (reqAlonso != null) {
            EntryHook alonsoHook = EntryHookFactory.createHook(HookType.ALONSOLEVELS);
            okAlonso = alonsoHook.isValid(plugin.getServer().getPluginManager())
                    && alonsoHook.canEnter(player, reqAlonso);
            if (!okAlonso) sendDeny(player, "ALONSOLEVELS", reqAlonso);
        }

        boolean allowed = okDefault && okAlonso;
        Vector kb = allowed ? new Vector() : calculateKnockbackVector(from, dest);
        return new KnockbackDecision(allowed, kb);
    }

    private Vector calculateKnockbackVector(Location from, Location to) {
        int changeX = to.getBlockX() - from.getBlockX();
        int changeY = to.getBlockY() - from.getBlockY();
        int changeZ = to.getBlockZ() - from.getBlockZ();

        int maxDelta = Math.max(Math.abs(changeX), Math.max(Math.abs(changeY), Math.abs(changeZ)));
        if (maxDelta > 1) {
            Vector v = to.toVector().subtract(from.toVector()).normalize().multiply(-1.25);
            if (v.getY() > -0.4) v.setY(-0.4);
            return v;
        }

        double knockbackX = Math.signum(changeX);
        double knockbackY = -0.5;
        double knockbackZ = Math.signum(changeZ);

        Vector v = new Vector(knockbackX, knockbackY, knockbackZ);
        if (v.lengthSquared() == 0) {
            v = to.toVector().subtract(from.toVector());
        }

        return v.normalize().multiply(-1.0);
    }

    private void sendDeny(Player player, String type, int req) {
        player.sendMessage("WorldGuardBlock"); //TODO Change with real message
    }

    private boolean sameBlock(Location a, Location b) {
        return a.getWorld() == b.getWorld()
                && a.getBlockX() == b.getBlockX()
                && a.getBlockY() == b.getBlockY()
                && a.getBlockZ() == b.getBlockZ();
    }

    private record KnockbackDecision(boolean allowed, Vector vector) {}
}