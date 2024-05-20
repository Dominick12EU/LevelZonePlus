package it.dominick.lzp.commands.args;

import it.dominick.lzp.config.ConfigManager;
import it.dominick.lzp.region.manager.RegionManager;
import it.dominick.lzp.utils.ChatUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Pos2Argument extends Argument {

    private final RegionManager regionManager;

    public Pos2Argument(ConfigManager config, RegionManager regionManager) {
        super(config, "/lzp pos2", "levelzone.admin");
        this.regionManager = regionManager;
    }

    @Override
    public void execute(Player player, String[] args) {
        Location pos2 = player.getLocation();
        regionManager.setPlayerPos2(player, pos2);
        ChatUtils.send(player, config.getString("cmd.pos"), "%pos%", "2");
    }

    @Override
    public int minimumArgs() {
        return 0;
    }
}
