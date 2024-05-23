package it.dominick.lzp.region;

import it.dominick.lzp.hook.HookType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;

@RequiredArgsConstructor
@Getter
public class RegionData {
    private final String name;
    private final HookType hookType;
    private final int minLevel;
    private final Location point1;
    private final Location point2;
}
