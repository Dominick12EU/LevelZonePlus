package it.dominick.lzp.commands.args;

import com.google.common.collect.ImmutableList;
import it.dominick.lzp.LevelZonePlus;
import it.dominick.lzp.config.ConfigManager;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.bukkit.entity.Player;

import java.util.List;

@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public abstract class Argument {

    protected final ConfigManager config;
    private final String command;
    private final String permission;

    public Argument(ConfigManager config, String command) {
        this(config, command, "levelzone.admin");
    }

    public abstract void execute(Player player, String[] args);

    public abstract int minimumArgs();

    public boolean hasPermission(Player player) {
        return player.hasPermission(permission);
    }

    public boolean invalidArgs(String[] args) {
        return args.length < minimumArgs();
    }

    public List<String> completation(Player player, String[] args) {
        return ImmutableList.of();
    }
}

