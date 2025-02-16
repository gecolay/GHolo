package dev.geco.gholo.api.event;

import dev.geco.gholo.GHoloMain;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.server.PluginEvent;
import org.jetbrains.annotations.NotNull;

public class GHoloReloadEvent extends PluginEvent implements Cancellable {

    private final GHoloMain gHoloMain;
    private boolean cancel = false;
    private static final HandlerList handlers = new HandlerList();

    public GHoloReloadEvent(@NotNull GHoloMain gHoloMain) {
        super(gHoloMain);
        this.gHoloMain = gHoloMain;
    }

    @Override
    public @NotNull GHoloMain getPlugin() { return gHoloMain; }

    @Override
    public boolean isCancelled() { return cancel; }

    @Override
    public void setCancelled(boolean cancelled) { cancel = cancelled; }

    @Override
    public @NotNull HandlerList getHandlers() { return handlers; }

    public static @NotNull HandlerList getHandlerList() { return handlers; }

}