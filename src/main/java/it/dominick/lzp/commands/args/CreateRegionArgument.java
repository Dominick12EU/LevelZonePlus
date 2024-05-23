package it.dominick.lzp.commands.args;

import it.dominick.lzp.config.ConfigManager;
import it.dominick.lzp.hook.HookType;
import it.dominick.lzp.region.manager.RegionManager;
import it.dominick.lzp.utils.ChatUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class CreateRegionArgument extends Argument {

    private final RegionManager regionManager;

    public CreateRegionArgument(ConfigManager config, RegionManager regionManager) {
        super(config, "/lzp create", "levelzone.admin");
        this.regionManager = regionManager;
    }

    @Override
    public void execute(Player player, String[] args) {
        String regionName = args[1];
        if (regionManager.regionExists(regionName)) {
            ChatUtils.send(player, config.getString("cmd.create.already-exist"),
                    "%region%", regionName.toLowerCase());
            return;
        }

        Location pos1 = regionManager.getPlayerPos1(player);
        Location pos2 = regionManager.getPlayerPos2(player);

        if (pos1 == null || pos2 == null) {
            ChatUtils.send(player, config.getString("cmd.create.set-positions"));
            return;
        }
        if (!pos1.getWorld().equals(pos2.getWorld())) {
            ChatUtils.send(player, config.getString("cmd.create.same-world"));
            return;
        }

        regionManager.addRegion(regionName, pos1, pos2, HookType.DEFAULT, 0);
    }

    @Override
    public int minimumArgs() {
        return 1;
    }
}
