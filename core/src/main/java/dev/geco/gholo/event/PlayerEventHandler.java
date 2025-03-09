package dev.geco.gholo.event;

import dev.geco.gholo.GHoloMain;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;

public class PlayerEventHandler implements Listener {

    private final GHoloMain gHoloMain;

    public PlayerEventHandler(GHoloMain gHoloMain) {
        this.gHoloMain = gHoloMain;
    }

    @EventHandler
    public void playerJoinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        gHoloMain.getUpdateService().checkForUpdates(player);

        gHoloMain.getTaskService().runDelayed(() -> {
            gHoloMain.getHoloService().loadHolosForPlayer(player);

            if(gHoloMain.getInteractionService().hasInteractions()) gHoloMain.getPacketHandler().setupPlayerPacketHandler(player);
            gHoloMain.getInteractionService().loadInteractionsForPlayer(player);
        }, false, player, 1);
    }

    @EventHandler
    public void playerQuitEvent(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        gHoloMain.getHoloService().clearHolosCurrentContentForPlayer(player);

        gHoloMain.getInteractionService().clearPlayerInteractions(player);
    }

    @EventHandler
    public void playerChangedWorldEvent(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();

        gHoloMain.getHoloService().clearHolosCurrentContentForPlayer(player);

        gHoloMain.getTaskService().runDelayed(() -> {
            gHoloMain.getHoloService().loadHolosForPlayer(player);

            gHoloMain.getInteractionService().loadInteractionsForPlayer(event.getPlayer());
        }, false, player, 1);
    }

    @EventHandler
    public void playerResourcePackStatusEvent(PlayerResourcePackStatusEvent event) {
        if(event.getStatus() != PlayerResourcePackStatusEvent.Status.SUCCESSFULLY_LOADED) return;

        Player player = event.getPlayer();

        gHoloMain.getTaskService().runDelayed(() -> {
            gHoloMain.getHoloService().unloadHolosForPlayer(player);
            gHoloMain.getHoloService().loadHolosForPlayer(player);
        }, false, player, 1);
    }

}