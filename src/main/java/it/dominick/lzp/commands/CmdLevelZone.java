package it.dominick.lzp.commands;

import com.google.common.collect.ImmutableList;
import it.dominick.lzp.LevelZonePlus;
import it.dominick.lzp.commands.args.*;
import it.dominick.lzp.config.ConfigManager;
import it.dominick.lzp.region.manager.RegionManager;
import it.dominick.lzp.utils.ChatUtils;
import it.dominick.lzp.utils.RNG;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CmdLevelZone implements TabExecutor {

    private final ConfigManager config;
    private final RegionManager region;
    private final Argument helpArgument;
    private final Map<String, Argument> argumentMap;

    public CmdLevelZone(ConfigManager config, RegionManager region) {
        this.config = config;
        this.region = region;
        argumentMap = new HashMap<>();

        argumentMap.put("create", new CreateRegionArgument(config, region));
        argumentMap.put("delete", new DeleteRegionArgument(config, region));
        argumentMap.put("pos1", new Pos1Argument(config, region));
        argumentMap.put("pos2", new Pos2Argument(config, region));
        argumentMap.put("help", new HelpArgument(config));

        helpArgument = new HelpArgument(config);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            ChatUtils.send(sender, config.getString("global.no-console"));
            return true;
        }
        if (args.length == 0) {
            config.printHelp(player);
            return true;
        }

        Argument argument = getArgument(args[0].toLowerCase());

        if (!argument.hasPermission(player)) {
            ChatUtils.send(player, config.getString("global.insufficient-permission"));
            return true;
        }

        if (argument.invalidArgs(args)) {
            ChatUtils.send(player, config.getString("global.wrong-command-syntax"), "%command%", argument.command());
            return true;
        }

        argument.execute(player, args);
        return true;
    }

    private Argument getArgument(String name) {
        return argumentMap.getOrDefault(name, helpArgument);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player player) || args.length == 0) return ImmutableList.of();

        player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_CHIME, 0.25f, RNG.r.f(0.125f, 1.95f));

        if (args.length == 1) {
            return argumentMap.keySet().parallelStream().filter(arg -> arg.startsWith(args[0])).toList();
        } else {
            Argument argument = argumentMap.get(args[0]);
            if(argument == null) return argumentMap.keySet().parallelStream().filter(arg -> arg.startsWith(args[0])).toList();
            return argument.completation(player, args);
        }

    }
}


