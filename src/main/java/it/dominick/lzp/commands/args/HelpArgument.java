package it.dominick.lzp.commands.args;

import it.dominick.lzp.config.ConfigManager;
import org.bukkit.entity.Player;

public class HelpArgument extends Argument {

    public HelpArgument(ConfigManager config) {
        super(config, "/lzp help", "levelzone.admin");
    }

    @Override
    public void execute(Player player, String[] args) {
        config.printHelp(player);
    }

    @Override
    public int minimumArgs() {
        return 0;
    }
}
