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

        GPM.getTManager().runDelayed(() -> GPM.getHoloManager().spawnHolosToPlayer(Event.getPlayer()), 1);
    }

    @EventHandler
    public void PChaWE(PlayerChangedWorldEvent Event) {
        GPM.getTManager().runDelayed(() -> GPM.getHoloManager().spawnHolosToPlayer(Event.getPlayer()), 1);
    }

}