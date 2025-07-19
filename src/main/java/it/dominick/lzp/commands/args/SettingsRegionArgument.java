package it.dominick.lzp.commands.args;

import it.dominick.lzp.config.ConfigManager;
import it.dominick.lzp.gui.RegionsGUI;
import it.dominick.lzp.region.manager.RegionManager;
import org.bukkit.entity.Player;

public class SettingsRegionArgument extends Argument {

    private final RegionManager regionManager;

    public SettingsRegionArgument(ConfigManager config, RegionManager regionManager) {
        super(config, "/lzp settings", "levelzone.admin");
        this.regionManager = regionManager;
    }

    @Override
    public void execute(Player player, String[] args) {
        new RegionsGUI(regionManager).open(player);
    }

    @Override
    public int minimumArgs() {
        return 0;
    }
}
