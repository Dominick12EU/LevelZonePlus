package it.dominick.lzp.event;

import it.dominick.lzp.region.CustomRegion;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RegionQuitEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;
    private final CustomRegion region;

    public RegionQuitEvent(Player player, CustomRegion region) {
        this.player = player;
        this.region = region;
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
}