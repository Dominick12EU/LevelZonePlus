package it.dominick.lzp.gui;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import it.dominick.lzp.region.manager.RegionManager;
import it.dominick.lzp.utils.ChatUtils;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class ConfirmGUI {
    private final RegionManager regionManager;
    private final String regionName;

    public void open(Player player) {
        Gui regionsGui = Gui.gui()
                .title(Component.text("Are you sure?"))
                .rows(3)
                .disableAllInteractions()
                .create();

        regionsGui.setItem(2, 3, ItemBuilder.from(Material.GREEN_WOOL)
            .setName(ChatUtils.color("Im Sure"))
            .asGuiItem(confirm -> {
                regionManager.removeRegion(regionName);
                if (!regionManager.getRegions().isEmpty()) {
                    new RegionsGUI(regionManager).open(player);
                }
            })
        );

        regionsGui.setItem(2, 7, ItemBuilder.from(Material.RED_WOOL)
                .setName(ChatUtils.color("Undo"))
                .asGuiItem(confirm -> {
                    new RegionSettingsGUI(regionManager, regionName).open(player);
                })
        );

        regionsGui.open(player);
    }
}
