package it.dominick.lzp.gui;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.PaginatedGui;
import it.dominick.lzp.region.CustomRegion;
import it.dominick.lzp.region.manager.RegionManager;
import it.dominick.lzp.utils.ChatUtils;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class RegionsGUI {

    private final RegionManager regionManager;

    public void open(Player player) {
        PaginatedGui regionsGui = Gui.paginated()
                .title(Component.text("Regions"))
                .rows(6)
                .pageSize(28)
                .disableAllInteractions()
                .create();

        Map<String, CustomRegion> regions = regionManager.getRegions();

        if (regions.isEmpty()) {
            player.closeInventory();
            ChatUtils.send(player, "&7No regions found!, if you want create one do /lz create");
            return;
        }

        for (Map.Entry<String, CustomRegion> entry : regions.entrySet()) {
            String regionName = entry.getKey();
            CustomRegion region = entry.getValue();

            ItemStack itemStack = new ItemStack(Material.PAPER);
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta != null) {
                itemMeta.setDisplayName(regionName);
                itemMeta.setLore(ChatUtils.color(List.of(
                        "First Point: " + formatLocation(region.getMin()),
                        "Second Point: " + formatLocation(region.getMax())
                )));
                itemStack.setItemMeta(itemMeta);
            }

            regionsGui.addItem(ItemBuilder.from(itemStack).asGuiItem(event -> {
                if (event.isLeftClick()) {
                    new RegionSettingsGUI(regionManager, regionName).open(player);
                }
            }));
        }

        regionsGui.open(player);
    }

    private String formatLocation(Location location) {
        return String.format("(%d, %d, %d)",
                location.getBlockX(),
                location.getBlockY(),
                location.getBlockZ());
    }
}