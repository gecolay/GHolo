package dev.geco.gholo.api.event;

import dev.geco.gholo.object.GInteraction;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class GInteractionEvent extends PlayerEvent implements Cancellable {

    private final GInteraction interaction;
    private final boolean mainHand;
    private boolean cancel = false;
    private static final HandlerList handlers = new HandlerList();

    public GInteractionEvent(@NotNull GInteraction interaction, Player player, boolean mainHand) {
        super(player);
        this.interaction = interaction;
        this.mainHand = mainHand;
    }

    public GInteraction getInteraction() { return interaction; }

    public boolean isMainHand() { return mainHand; }

    @Override
    public boolean isCancelled() { return cancel; }

    @Override
    public void setCancelled(boolean cancelled) { cancel = cancelled; }

    @Override
    public @NotNull HandlerList getHandlers() { return handlers; }

    public static @NotNull HandlerList getHandlerList() { return handlers; }

}