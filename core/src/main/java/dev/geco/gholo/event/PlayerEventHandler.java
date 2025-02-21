package dev.geco.gholo.event;

import dev.geco.gholo.GHoloMain;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/*
 * This class contains code adapted from FancyHolograms
 * https://github.com/FancyMcPlugins/FancyHolograms
 * Copyright (c) 2022-2023 Oliver
 * Licensed under the MIT License
 */

public class PlayerEventHandler implements Listener {

    private final GHoloMain gHoloMain;
    private final Map<UUID, List<UUID>> loadingResourcePacks;

    public PlayerEventHandler(GHoloMain gHoloMain) {
        this.gHoloMain = gHoloMain;
        this.loadingResourcePacks = new HashMap<>();
    }

    @EventHandler
    public void playerJoinEvent(PlayerJoinEvent event) {
        gHoloMain.getTaskService().runDelayed(() -> gHoloMain.getHoloService().loadHolosForPlayer(event.getPlayer()), 1);
    }

    @EventHandler
    public void playerChangedWorldEvent(PlayerChangedWorldEvent event) {
        gHoloMain.getTaskService().runDelayed(() -> gHoloMain.getHoloService().loadHolosForPlayer(event.getPlayer()), 1);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onResourcePackStatus(PlayerResourcePackStatusEvent event) {
        if (!event.getPlayer().isOnline()) return;
        UUID playerUniqueId = event.getPlayer().getUniqueId();
        UUID packUniqueId = getResourcePackID(event);

        if (event.getStatus() == PlayerResourcePackStatusEvent.Status.ACCEPTED)
            loadingResourcePacks.computeIfAbsent(playerUniqueId, (___) -> new ArrayList<>()).add(packUniqueId);
        else if (event.getStatus() == PlayerResourcePackStatusEvent.Status.SUCCESSFULLY_LOADED || event.getStatus() == PlayerResourcePackStatusEvent.Status.FAILED_DOWNLOAD) {
            loadingResourcePacks.computeIfAbsent(playerUniqueId, (___) -> new ArrayList<>()).removeIf(uuid -> uuid.equals(packUniqueId));
            if (loadingResourcePacks.get(playerUniqueId) != null && loadingResourcePacks.get(playerUniqueId).isEmpty()) {
                loadingResourcePacks.remove(playerUniqueId);
                gHoloMain.getTaskService().runDelayed(() -> gHoloMain.getHoloService().loadHolosForPlayer(event.getPlayer()), 1);
            }
        }
    }

    // For 1.20.2 and higher this method returns actual pack identifier, while for older versions, the identifier is a dummy UUID full of zeroes.
    // Versions prior 1.20.2 supports sending and receiving only one resource-pack and a dummy, constant identifier can be used as a key.
    private static @NotNull UUID getResourcePackID(PlayerResourcePackStatusEvent event) {
        try {
            event.getClass().getMethod("getID");
            return event.getID();
        } catch (final @NotNull NoSuchMethodException e) {
            return new UUID(0,0);
        }
    }
}