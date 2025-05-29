package dev.geco.gholo.event;

import dev.geco.gholo.GHoloMain;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

public class WorldEventHandler implements Listener {

    private final GHoloMain gHoloMain;

    public WorldEventHandler(GHoloMain gHoloMain) {
        this.gHoloMain = gHoloMain;
    }

    @EventHandler
    public void worldLoadEvent(WorldLoadEvent event) {
        gHoloMain.getHoloService().loadHolos(event.getWorld());
        gHoloMain.getInteractionService().loadInteractions(event.getWorld());
    }

    @EventHandler
    public void worldUnloadEvent(WorldUnloadEvent event) {
        gHoloMain.getHoloService().unloadHolos(event.getWorld());
        gHoloMain.getInteractionService().unloadInteractions(event.getWorld());
    }

}