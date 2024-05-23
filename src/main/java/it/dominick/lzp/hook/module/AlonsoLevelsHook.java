package it.dominick.lzp.hook.module;

import com.alonsoaliaga.alonsolevels.api.AlonsoLevelsAPI;
import it.dominick.lzp.hook.EntryHook;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

public class AlonsoLevelsHook implements EntryHook {
    @Override
    public boolean canEnter(Player player, int minLevel) {
        int level = AlonsoLevelsAPI.getLevel(player.getUniqueId());
        return level >= minLevel;
    }

    @Override
    public boolean isValid(PluginManager pluginManager) {
        return pluginManager.getPlugin("AlonsoLevels") != null;
    }
}
