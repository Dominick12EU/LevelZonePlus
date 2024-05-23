package it.dominick.lzp.hook;

import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

public interface EntryHook {
    boolean canEnter(Player player, int minLevel);
    boolean isValid(PluginManager pluginManager);
}
