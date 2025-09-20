package it.dominick.lzp.hook.worldguard;

import it.dominick.lzp.LevelZonePlus;
import it.dominick.lzp.hook.EntryHook;
import it.dominick.lzp.hook.EntryHookFactory;
import it.dominick.lzp.hook.HookType;
import it.dominick.lzp.utils.ChatUtils;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@RequiredArgsConstructor
public class WorldGuardMovementListener implements Listener {

    private final LevelZonePlus plugin;

    private static final Map<HookType, Function<Location, Integer>> REQ_RESOLVERS = Map.of(
            HookType.DEFAULT,      WorldGuardFlagResolver::resolveDefaultMinLevel,
            HookType.ALONSOLEVELS, WorldGuardFlagResolver::resolveAlonsoMinLevel
    );

    private record Requirement(HookType type, int min) {}
    private record KnockbackDecision(boolean allowed, Vector vector) {}

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
        List<Requirement> reqs = resolveRequirements(dest);
        if (reqs.isEmpty()) {
            return new KnockbackDecision(true, new Vector());
        }

        var pm = plugin.getServer().getPluginManager();
        List<Requirement> failed = new ArrayList<>();

        for (Requirement r : reqs) {
            EntryHook hook = EntryHookFactory.createHook(r.type());
            boolean ok = hook.isValid(pm) && hook.canEnter(player, r.min());
            if (!ok) failed.add(r);
        }

        boolean allowed = failed.isEmpty();
        if (!allowed) {
            sendDenyAggregated(player, failed);
        }

        Vector kb = allowed ? new Vector() : calculateKnockbackVector(from, dest);
        return new KnockbackDecision(allowed, kb);
    }

    private List<Requirement> resolveRequirements(Location dest) {
        List<Requirement> out = new ArrayList<>();
        for (var e : REQ_RESOLVERS.entrySet()) {
            Integer min = e.getValue().apply(dest);
            if (min != null) out.add(new Requirement(e.getKey(), min));
        }
        return out;
    }

    private void sendDenyAggregated(Player player, List<Requirement> failed) {
        StringBuilder sb = new StringBuilder("{prefix} &cYou do not have the required level &8(");
        for (int i = 0; i < failed.size(); i++) {
            Requirement r = failed.get(i);
            sb.append(displayName(r.type())).append(": &e").append(r.min()).append("&8");
            if (i < failed.size() - 1) sb.append(", ");
        }
        sb.append(")&c.");
        player.sendMessage(ChatUtils.color(ChatUtils.placeholder(sb.toString())));
    }

    private String displayName(HookType type) {
        return switch (type) {
            case DEFAULT      -> "DEFAULT";
            case ALONSOLEVELS -> "ALONSOLEVELS";
        };
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

    private boolean sameBlock(Location a, Location b) {
        return a.getWorld() == b.getWorld()
                && a.getBlockX() == b.getBlockX()
                && a.getBlockY() == b.getBlockY()
                && a.getBlockZ() == b.getBlockZ();
    }
}