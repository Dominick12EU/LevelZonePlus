package it.dominick.lzp.hook.module;

import it.dominick.lzp.hook.EntryHook;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

public class DefaultHook implements EntryHook {
    @Override
    public boolean canEnter(Player player, int minLevel) {
        return player.getLevel() >= minLevel;
    }

    @Override
    public boolean isValid(PluginManager pluginManager) {
        return true;
    }
}
