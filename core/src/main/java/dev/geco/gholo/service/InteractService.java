package dev.geco.gholo.service;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.object.GHoloRow;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class InteractService {

    public static final double INTERACTION_COOLDOWN_SECONDS = 0.5;
    private static final long INTERACTION_COOLDOWN_MILLIS = (long) (INTERACTION_COOLDOWN_SECONDS * 1000);

    private final GHoloMain gHoloMain;
    private final HashMap<Integer, GHoloRow> interactHoloRowMap = new HashMap<>();
    private final HashMap<Integer, HashMap<UUID, Long>> lastInteractionMap = new HashMap<>();

    public InteractService(GHoloMain gHoloMain) {
        this.gHoloMain = gHoloMain;
    }

    public void addInteractionEntry(int entityId, GHoloRow holoRow) { interactHoloRowMap.put(entityId, holoRow); }

    public void removeInteraction(int entityId) {
        interactHoloRowMap.remove(entityId);
        lastInteractionMap.remove(entityId);
    }

    public void clearPlayerInteractions(Player player) {
        UUID playerId = player.getUniqueId();
        for(HashMap<UUID, Long> lastHoloRowInteractions : lastInteractionMap.values()) lastHoloRowInteractions.remove(playerId);
    }

    public void clearInteractions() {
        interactHoloRowMap.clear();
        lastInteractionMap.clear();
    }

    public boolean callHoloHowInteraction(int entityId, Player player, boolean hand) {
        GHoloRow holoRow = interactHoloRowMap.get(entityId);
        if(holoRow == null) return false;
        HashMap<UUID, Long> lastHoloRowInteractions = lastInteractionMap.computeIfAbsent(entityId, k -> new HashMap<>());
        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();
        Long lastInteractionTime = lastHoloRowInteractions.get(playerId);
        if(lastInteractionTime != null && (currentTime - lastInteractionTime) < INTERACTION_COOLDOWN_MILLIS) return true;
        lastHoloRowInteractions.put(playerId, currentTime);
        player.sendMessage(player + " : " + holoRow);
        // call GHoloRowInteractEvent
        return true;
    }

}