package dev.geco.gholo.event;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.api.event.GPlayerInteractionEvent;
import dev.geco.gholo.object.interaction.GInteractionAction;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class InteractionEventHandler implements Listener {

    private final GHoloMain gHoloMain;

    public InteractionEventHandler(GHoloMain gHoloMain) {
        this.gHoloMain = gHoloMain;
    }

    @EventHandler
    public void playerJoinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        gHoloMain.getUpdateService().checkForUpdates(player);

        gHoloMain.getTaskService().runDelayed(() -> {
            gHoloMain.getPacketHandler().setupPlayerPacketHandler(player);
            gHoloMain.getInteractionService().loadInteractionsForPlayer(player);
        }, 1);
    }

    @EventHandler
    public void playerQuitEvent(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        gHoloMain.getPacketHandler().removePlayerPacketHandler(player);
        gHoloMain.getInteractionService().clearPlayerInteractions(player);
    }

    @EventHandler
    public void playerChangedWorldEvent(PlayerChangedWorldEvent event) {
        gHoloMain.getTaskService().runDelayed(() -> {
            gHoloMain.getInteractionService().loadInteractionsForPlayer(event.getPlayer());
        }, 1);
    }

    @EventHandler
    public void playerInteractionEvent(GPlayerInteractionEvent event) {
        Player player = event.getPlayer();
        for(GInteractionAction interactionAction : event.getInteraction().getActions()) {
            interactionAction.getInteractionActionType().execute(gHoloMain, player, event.getInteractType(), interactionAction.getParameter());
        }
    }

}