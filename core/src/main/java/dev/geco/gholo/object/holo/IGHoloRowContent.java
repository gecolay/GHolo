package dev.geco.gholo.object.holo;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

public interface IGHoloRowContent {

    void loadHoloRow();

    void loadHoloRow(@NotNull Player player);

    void publishUpdate(@NotNull GHoloUpdateType updateType);

    void unloadHoloRow();

    void unloadHoloRow(@NotNull Player player);

    HashMap<UUID, IGHoloRowContentType> getCurrentContentTypes();

}