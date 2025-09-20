package it.dominick.lzp.commands.args;

import it.dominick.lzp.config.ConfigManager;
import it.dominick.lzp.region.manager.RegionManager;
import it.dominick.lzp.utils.ChatUtils;
import org.bukkit.entity.Player;

public class DeleteRegionArgument extends Argument {

    private final RegionManager regionManager;

    public DeleteRegionArgument(ConfigManager config, RegionManager regionManager) {
        super(config, "/lzp delete", "levelzone.admin");
        this.regionManager = regionManager;
    }

    @Override
    public void execute(Player player, String[] args) {
        String regionName = args[1];
        if (regionManager.regionExists(regionName)) {
            regionManager.removeRegion(regionName);
            ChatUtils.send(player, config.getString("cnd.delete"), "%region%", regionName.toLowerCase());
        } else {
            ChatUtils.send(player, config.getString("global.no-region-found"), "%region%", regionName.toLowerCase());
        }
    }

    @Override
    public int minimumArgs() {
        return 1;
    }
}
