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

import javax.swing.*;

@RequiredArgsConstructor
public class SelectionListener implements Listener {

    private static final int MAX_DISTANCE = 20;

    private final RegionManager regionManager;
    private final ConfigManager config;
    private final NamespacedKey wandKey;

    private boolean isWand(ItemStack item) {
        if (item == null) return false;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;
        Byte flag = meta.getPersistentDataContainer().get(wandKey, PersistentDataType.BYTE);
        return flag != null && flag == (byte) 1;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent e) {
        if (e.getHand() == EquipmentSlot.OFF_HAND) return;

        Player p = e.getPlayer();
        ItemStack item = p.getInventory().getItemInMainHand();
        if (!isWand(item)) return;

        Action action = e.getAction();
        if (action != Action.LEFT_CLICK_BLOCK && action != Action.RIGHT_CLICK_BLOCK
                && action != Action.LEFT_CLICK_AIR && action != Action.RIGHT_CLICK_AIR) return;

        Block target = p.getTargetBlockExact(MAX_DISTANCE);
        if (target == null) {
            ChatUtils.send(p, config.getString("cmd.wand.tooFar"));
        }

        switch (action) {
            case LEFT_CLICK_BLOCK:
            case LEFT_CLICK_AIR:
                regionManager.setPlayerPos1(p, target.getLocation());
                ChatUtils.send(p, config.getString("cmd.pos"), "%pos%", "1");
                e.setCancelled(true);
                break;

            case RIGHT_CLICK_BLOCK:
            case RIGHT_CLICK_AIR:
                regionManager.setPlayerPos2(p, target.getLocation());
                ChatUtils.send(p, config.getString("cmd.pos"), "%pos%", "2");
                e.setCancelled(true);
                break;

            default:
                break;
        }
    }
}
