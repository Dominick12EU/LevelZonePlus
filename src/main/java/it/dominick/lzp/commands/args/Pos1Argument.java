package it.dominick.lzp.commands.args;

import it.dominick.lzp.config.ConfigManager;
import it.dominick.lzp.region.manager.RegionManager;
import it.dominick.lzp.utils.ChatUtils;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class Pos1Argument extends Argument {

    private static final int MAX_DISTANCE = 20;
    private final RegionManager regionManager;

    public Pos1Argument(ConfigManager config, RegionManager regionManager) {
        super(config, "/lzp pos1", "levelzone.admin");
        this.regionManager = regionManager;
    }

    @Override
    public void execute(Player player, String[] args) {
        Block target = player.getTargetBlockExact(MAX_DISTANCE);
        if (target == null) return;

        Location pos1 = target.getLocation();
        regionManager.setPlayerPos1(player, pos1);
        ChatUtils.send(player, config.getString("cmd.pos"), "%pos%", "1");
    }

    @Override
    public int minimumArgs() {
        return 0;
    }
}
