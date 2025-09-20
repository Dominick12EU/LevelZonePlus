package it.dominick.lzp.hook.worldguard;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.IntegerFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;

public final class WorldGuardFlagRegistrar {
    private WorldGuardFlagRegistrar() {}

    public static void registerFlagsIfPossible() {
        FlagRegistry reg = WorldGuard.getInstance().getFlagRegistry();

        Flag<?> existingDefault = reg.get(WorldGuardHookLoader.DEFAULT_FLAG_ID);
        if (existingDefault instanceof IntegerFlag f) {
            WorldGuardHookLoader.DEFAULT_MIN_LVL = f;
        } else {
            WorldGuardHookLoader.DEFAULT_MIN_LVL = new IntegerFlag(WorldGuardHookLoader.DEFAULT_FLAG_ID);
            reg.register(WorldGuardHookLoader.DEFAULT_MIN_LVL);
        }


        Flag<?> existingAlonso = reg.get(WorldGuardHookLoader.ALONSO_FLAG_ID);
        if (existingAlonso instanceof IntegerFlag f) {
            WorldGuardHookLoader.ALONSO_MIN_LVL = f;
        } else {
            WorldGuardHookLoader.ALONSO_MIN_LVL = new IntegerFlag(WorldGuardHookLoader.ALONSO_FLAG_ID);
            reg.register(WorldGuardHookLoader.ALONSO_MIN_LVL);
        }
    }
}
