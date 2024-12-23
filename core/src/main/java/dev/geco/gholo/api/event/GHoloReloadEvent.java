package dev.geco.gholo.api.event;

import org.jetbrains.annotations.*;

import org.bukkit.event.*;
import org.bukkit.event.server.*;

import dev.geco.gholo.GHoloMain;

public class GHoloReloadEvent extends PluginEvent implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private boolean cancel = false;

    private final GHoloMain GPM;

    public GHoloReloadEvent(GHoloMain GPluginMain) {
        super(GPluginMain);
        GPM = GPluginMain;
    }

    public @NotNull GHoloMain getPlugin() { return GPM; }

    public @NotNull HandlerList getHandlers() { return HANDLERS; }

    public static HandlerList getHandlerList() { return HANDLERS; }

    public boolean isCancelled() { return cancel; }

    public void setCancelled(boolean Cancel) { cancel = Cancel; }

}