package dev.geco.gholo.events;

import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;

import dev.geco.gholo.GHoloMain;

public class PlayerEvents implements Listener {

    private final GHoloMain GPM;

    public PlayerEvents(GHoloMain GPluginMain) { GPM = GPluginMain; }

    @EventHandler
    public void PJoiE(PlayerJoinEvent Event) {

        Player player = Event.getPlayer();

        GPM.getUManager().loginCheckForUpdates(player);
    }

    /*@EventHandler
    public void PQuiE(PlayerQuitEvent Event) { GPM.getHoloSpawnManager().removeForPlayer(Event.getPlayer()); }

    @EventHandler
    public void PChaWE(PlayerChangedWorldEvent Event) {

        GPM.getHoloSpawnManager().removeForPlayer(Event.getPlayer());

        GPM.getTManager().runDelayed(() -> {
            GPM.getHoloSpawnManager().updateStaticHolosForPlayer(Event.getPlayer());
        }, 1);
    }*/

}