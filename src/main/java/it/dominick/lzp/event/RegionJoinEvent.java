package it.dominick.lzp.event;

import it.dominick.lzp.region.CustomRegion;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Cancellable;

public class RegionJoinEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;
    private final CustomRegion region;
    private boolean cancelled;

    public RegionJoinEvent(Player player, CustomRegion region) {
        this.player = player;
        this.region = region;
        this.cancelled = false;
    }

    public Player getPlayer() {
        return player;
    }

    public CustomRegion getRegion() {
        return region;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
