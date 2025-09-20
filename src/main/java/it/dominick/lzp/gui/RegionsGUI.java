package it.dominick.lzp.gui;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import it.dominick.lzp.hook.worldguard.WorldGuardFlagResolver;
import it.dominick.lzp.region.CustomRegion;
import it.dominick.lzp.region.manager.RegionManager;
import it.dominick.lzp.utils.ChatUtils;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
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

        boolean hasWG = Bukkit.getPluginManager().getPlugin("WorldGuard") != null;
        Map<String, CustomRegion> regions = regionManager.getRegions();
        Map<String, ProtectedRegion> wgLzpRegions = hasWG
                ? WorldGuardFlagResolver.listLzpRegions(player.getWorld())
                : Map.of();

        if (regions.isEmpty() && wgLzpRegions.isEmpty()) {
            player.closeInventory();
            ChatUtils.send(player, "&7No regions found!, if you want create one do /lz create");
            return;
        }

        regionsGui.getFiller().fillBorder(ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).asGuiItem());

        GuiItem guiItemClose = ItemBuilder.from(Material.EMERALD).setName(ChatUtils.color("&4&lClose")).setLore(ChatUtils.color("&7Close the gui with a click")).asGuiItem(event -> {
            regionsGui.close(player);
        });
        regionsGui.setItem(4, guiItemClose);

        GuiItem guiItemNextPage = ItemBuilder.from(Material.SPECTRAL_ARROW).setName(ChatUtils.color("&a&lNext Page")).setLore(ChatUtils.color("&7Switch to the next page")).asGuiItem(event -> {
            regionsGui.next();
            regionsGui.update();
        });

        GuiItem guiItemPrevPage = ItemBuilder.from(Material.SPECTRAL_ARROW).setName(ChatUtils.color("&c&lPrevious Page")).setLore(ChatUtils.color("&7Switch to the previous page")).asGuiItem(event -> {
            regionsGui.previous();
            regionsGui.update();
        });

        regionsGui.setItem(48, guiItemPrevPage);


        if (!regions.isEmpty()) {
            for (Map.Entry<String, CustomRegion> entry : regions.entrySet()) {
                String regionName = entry.getKey();
                CustomRegion region = entry.getValue();

                ItemStack itemStack = new ItemStack(Material.PAPER);
                ItemMeta itemMeta = itemStack.getItemMeta();
                if (itemMeta != null) {
                    itemMeta.setDisplayName(regionName);
                    itemMeta.setLore(ChatUtils.color(List.of(
                            "&7First Point: &f" + formatLocation(region.getMin()),
                            "&7Second Point: &f" + formatLocation(region.getMax())
                    )));
                    itemStack.setItemMeta(itemMeta);
                }

                regionsGui.addItem(ItemBuilder.from(itemStack).asGuiItem(event -> {
                    if (event.isLeftClick()) {
                        new RegionSettingsGUI(regionManager, regionName).open(player);
                    }
                }));
            }
        }

        if (!wgLzpRegions.isEmpty()) {
            for (Map.Entry<String, ProtectedRegion> e : wgLzpRegions.entrySet()) {
                String id = e.getKey();
                ProtectedRegion pr = e.getValue();

                ItemStack mapItem = new ItemStack(Material.MAP);
                ItemMeta meta = mapItem.getItemMeta();
                if (meta != null) {
                    Integer defMin = WorldGuardFlagResolver.getDefaultMin(pr);
                    Integer aloMin = WorldGuardFlagResolver.getAlonsoMin(pr);

                    String name = ChatUtils.color("&e[WG]&f " + id);
                    meta.setDisplayName(name);

                    List<String> lore = new ArrayList<>();
                    lore.addAll(ChatUtils.color(List.of(
                            "&7World: &f" + player.getWorld().getName(),
                            "&7Min: &f" + bvToString(pr.getMinimumPoint()),
                            "&7Max: &f" + bvToString(pr.getMaximumPoint())
                    )));
                    if (defMin != null) lore.add(ChatUtils.color("&7lzp-default-min-lvl: &a" + defMin));
                    if (aloMin != null) lore.add(ChatUtils.color("&7lzp-alonsolevels-min-lvl: &a" + aloMin));
                    lore.add(ChatUtils.color("&8Shift+Right-Click: remove LZP support"));

                    meta.setLore(lore);
                    mapItem.setItemMeta(meta);
                }

                regionsGui.addItem(ItemBuilder.from(mapItem).asGuiItem(event -> {
                    if (event.isLeftClick()) {
                        ChatUtils.send(player, "{prefix} &7[WG] Region &f" + id + " &7— edit via WorldGuard.");
                    }
                    if (event.isRightClick() && event.isShiftClick()) {
                        boolean removed = WorldGuardFlagResolver.removeLZPSupport(player.getWorld(), id, true);
                        if (removed) {
                            ChatUtils.send(player, "{prefix} &aRemoved LZP support from &f[WG] " + id);
                            player.closeInventory();
                            Bukkit.getScheduler().runTaskLater(
                                    Bukkit.getPluginManager().getPlugin("LevelZonePlus"),
                                    () -> open(player),
                                    1L
                            );
                        }
                    }
                }));
            }
        }

        regionsGui.setItem(50, guiItemNextPage);

        regionsGui.open(player);
    }

    private String formatLocation(Location location) {
        return String.format("(%d, %d, %d)",
                location.getBlockX(),
                location.getBlockY(),
                location.getBlockZ());
    }

    private String bvToString(com.sk89q.worldedit.math.BlockVector3 v) {
        return "(" + v.getBlockX() + ", " + v.getBlockY() + ", " + v.getBlockZ() + ")";
    }
}