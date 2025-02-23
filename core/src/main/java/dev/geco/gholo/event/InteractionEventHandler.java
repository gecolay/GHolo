package dev.geco.gholo.event;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.api.event.GInteractionPlayerEvent;
import dev.geco.gholo.object.interaction.GInteractionAction;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class InteractionEventHandler implements Listener {

    private final GHoloMain gHoloMain;

    public InteractionEventHandler(GHoloMain gHoloMain) {
        this.gHoloMain = gHoloMain;
    }

    @EventHandler
    public void interactionPlayerEvent(GInteractionPlayerEvent event) {
        Player player = event.getPlayer();
        for(GInteractionAction interactionAction : event.getInteraction().getActions()) {
            interactionAction.getInteractionActionType().execute(gHoloMain, player, event.getInteractType(), interactionAction.getParameter());
        }
    }

}