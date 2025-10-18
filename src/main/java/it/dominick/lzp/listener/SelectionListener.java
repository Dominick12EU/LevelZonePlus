package it.dominick.lzp.listener;

import it.dominick.lzp.config.ConfigManager;
import it.dominick.lzp.region.manager.RegionManager;
import it.dominick.lzp.utils.ChatUtils;
import lombok.RequiredArgsConstructor;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.EnumSet;
import java.util.Set;

@RequiredArgsConstructor
public final class SelectionListener implements Listener {

    private static final int SELECTION_RANGE = 20;
    private static final Set<Action> SUPPORTED_ACTIONS = EnumSet.of(
            Action.LEFT_CLICK_BLOCK, Action.RIGHT_CLICK_BLOCK,
            Action.LEFT_CLICK_AIR, Action.RIGHT_CLICK_AIR
    );

    private final RegionManager regionManager;
    private final ConfigManager config;
    private final NamespacedKey wandKey;

    private boolean isWand(final ItemStack item) {
        if (item == null) return false;
        final ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;
        final Byte flag = meta.getPersistentDataContainer().get(wandKey, PersistentDataType.BYTE);
        return Byte.valueOf((byte) 1).equals(flag);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInteract(final PlayerInteractEvent event) {
        if (event.getHand() == EquipmentSlot.OFF_HAND) return;

        final Player player = event.getPlayer();
        final ItemStack inHand = player.getInventory().getItemInMainHand();
        if (!isWand(inHand)) return;

        final Action action = event.getAction();
        if (!SUPPORTED_ACTIONS.contains(action)) return;

        final Block target = player.getTargetBlockExact(SELECTION_RANGE);
        if (target == null) {
            ChatUtils.send(player, config.getString("cmd.wand.tooFar"));
            event.setCancelled(true);
            return;
        }

        switch (action) {
            case LEFT_CLICK_BLOCK:
            case LEFT_CLICK_AIR:
                regionManager.setPlayerPos1(player, target.getLocation());
                ChatUtils.send(player, config.getString("cmd.pos"), "%pos%", "1");
                event.setCancelled(true);
                break;

            case RIGHT_CLICK_BLOCK:
            case RIGHT_CLICK_AIR:
                regionManager.setPlayerPos2(player, target.getLocation());
                ChatUtils.send(player, config.getString("cmd.pos"), "%pos%", "2");
                event.setCancelled(true);
                break;

            default:
                break;
        }
    }
}