package dev.geco.gholo.api.event;

import dev.geco.gholo.GHoloMain;
import org.bukkit.event.HandlerList;
import org.bukkit.event.server.PluginEvent;
import org.jetbrains.annotations.NotNull;

public class GHoloLoadedEvent extends PluginEvent {

    private final GHoloMain gHoloMain;
    private static final HandlerList handlers = new HandlerList();

    public GHoloLoadedEvent(@NotNull GHoloMain gHoloMain) {
        super(gHoloMain);
        this.gHoloMain = gHoloMain;
    }

    public @NotNull GHoloMain getPlugin() { return gHoloMain; }

    public @NotNull HandlerList getHandlers() { return handlers; }

    public static @NotNull HandlerList getHandlerList() { return handlers; }

}