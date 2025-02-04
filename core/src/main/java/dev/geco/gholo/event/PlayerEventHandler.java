package dev.geco.gholo.event;

import dev.geco.gholo.GHoloMain;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerEventHandler implements Listener {

    private final GHoloMain gHoloMain;

    public PlayerEventHandler(GHoloMain gHoloMain) {
        this.gHoloMain = gHoloMain;
    }

    @EventHandler
    public void playerJoinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        gHoloMain.getUpdateService().checkForUpdates(player);

        gHoloMain.getTaskService().runDelayed(() -> gHoloMain.getHoloService().loadHolosForPlayer(event.getPlayer()), 1);
    }

    @EventHandler
    public void playerChangedWorldEvent(PlayerChangedWorldEvent event) {
        gHoloMain.getTaskService().runDelayed(() -> gHoloMain.getHoloService().loadHolosForPlayer(event.getPlayer()), 1);
    }

}