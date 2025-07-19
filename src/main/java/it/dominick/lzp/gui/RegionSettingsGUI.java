package it.dominick.lzp.gui;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import it.dominick.lzp.region.RegionData;
import it.dominick.lzp.region.manager.RegionManager;
import it.dominick.lzp.utils.ChatUtils;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;


@RequiredArgsConstructor
public class RegionSettingsGUI {

    private final RegionManager regionManager;
    private final String regionName;

    public void open(Player player) {
        Gui regionSettings = Gui.gui()
                .title(Component.text(regionName + " Settings"))
                .rows(3)
                .disableAllInteractions()
                .create();

        RegionData regionData = regionManager.getRegionData(regionName);
        List<String> regionInfo = Arrays.asList(
                "&r",
                "&7&lRegion Information",
                "&fName: &a" + regionData.getName(),
                "&fHook Type: &b" + regionData.getHookType().toString(),
                "&fMin Level: &e" + regionData.getMinLevel(),
                "&fPoint 1: &6" + formatLocation(regionData.getPoint1()),
                "&fPoint 2: &6" + formatLocation(regionData.getPoint2())
        );

        regionSettings.setItem(1, 5, ItemBuilder.from(Material.EMERALD_BLOCK)
                .setName(ChatUtils.color("Region Information"))
                .setLore(ChatUtils.color(regionInfo))
                .asGuiItem());

        regionSettings.setItem(2, 3, ItemBuilder.from(Material.BARRIER)
                .setName(ChatUtils.color("&cDelete Region"))
                .setLore(ChatUtils.color(List.of("&7This function will delete the region permanently")))
                .asGuiItem(deleteEvent -> {
                    new ConfirmGUI(regionManager, regionName).open(player);
                }));

        regionSettings.open(player);
    }

    private String formatLocation(Location location) {
        return String.format("(%d, %d, %d)",
                location.getBlockX(),
                location.getBlockY(),
                location.getBlockZ());
    }
}