package dev.geco.gholo.api.event;

import dev.geco.gholo.object.interaction.GInteractType;
import dev.geco.gholo.object.interaction.GInteraction;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class GPlayerInteractionEvent extends PlayerEvent implements Cancellable {

    private final GInteraction interaction;
    private final GInteractType interactType;
    private boolean cancel = false;
    private static final HandlerList handlers = new HandlerList();

    public GPlayerInteractionEvent(@NotNull GInteraction interaction, Player player, GInteractType interactType) {
        super(player);
        this.interaction = interaction;
        this.interactType = interactType;
    }

    public GInteraction getInteraction() { return interaction; }

    public GInteractType getInteractType() { return interactType; }

    @Override
    public boolean isCancelled() { return cancel; }

    @Override
    public void setCancelled(boolean cancelled) { cancel = cancelled; }

    @Override
    public @NotNull HandlerList getHandlers() { return handlers; }

    public static @NotNull HandlerList getHandlerList() { return handlers; }

}