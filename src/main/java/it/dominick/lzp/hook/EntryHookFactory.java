package it.dominick.lzp.hook;

import it.dominick.lzp.hook.module.AlonsoLevelsHook;
import it.dominick.lzp.hook.module.DefaultHook;

public class EntryHookFactory {
    public static EntryHook createHook(HookType type) {
        return switch (type) {
            case DEFAULT -> new DefaultHook();
            case ALONSOLEVELS -> new AlonsoLevelsHook();
            default -> throw new IllegalArgumentException("Unknown hook type: " + type);
        };
    }
}
