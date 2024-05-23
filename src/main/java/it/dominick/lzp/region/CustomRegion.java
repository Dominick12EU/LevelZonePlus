package it.dominick.lzp.region;

import it.dominick.lzp.hook.EntryHook;
import it.dominick.lzp.hook.EntryHookFactory;
import it.dominick.lzp.hook.HookType;
import it.dominick.lzp.region.manager.RegionManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class CustomRegion extends Cuboid {

    private final EntryHook entryHook;
    private final int requiredLevel;

    public CustomRegion(Location point1, Location point2, HookType hookType, int requiredLevel) {
        super(point1, point2);
        this.entryHook = EntryHookFactory.createHook(hookType);
        this.requiredLevel = requiredLevel;
    }

    @Override
    public void onEnter() {
        System.out.println("Un giocatore è entrato nella regione.");
    }

    @Override
    public void onExit() {
        System.out.println("Un giocatore è uscito dalla regione.");
    }

    public void handlePlayerEntry(Player player) {
        player.sendMessage("Sei entrato nella regione personalizzata!");
    }

    public void handlePlayerExit(Player player) {
        player.sendMessage("Sei uscito dalla regione personalizzata!");
    }

    public boolean canEnter(Player player) {
        return entryHook.canEnter(player, requiredLevel);
    }

    public void denyEntry(Player player) {
        player.sendMessage("Non hai abbastanza esperienza per entrare in questa regione!");
    }

    public Vector calculateKnockbackVector(Location from, Location to, RegionManager regionSet) {
        int changeX = to.getBlockX() - from.getBlockX();
        int changeY = to.getBlockY() - from.getBlockY();
        int changeZ = to.getBlockZ() - from.getBlockZ();

        if (Math.max(changeX, Math.max(changeY, changeZ)) > 1) {
            return null;
        }

        boolean restrictedX = regionSet.getRegions().values().stream().allMatch(region -> region.contains(from.getBlockX() + changeX, from.getBlockY(), from.getBlockZ()));
        boolean restrictedZ = regionSet.getRegions().values().stream().allMatch(region -> region.contains(from.getBlockX(), from.getBlockY(), from.getBlockZ() + changeZ));

        double knockbackX = restrictedX ? Math.signum(changeX) : 0;
        double knockbackY = -0.5;
        double knockbackZ = restrictedZ ? Math.signum(changeZ) : 0;

        return new Vector(knockbackX, knockbackY, knockbackZ).normalize().multiply(-1);
    }
}
