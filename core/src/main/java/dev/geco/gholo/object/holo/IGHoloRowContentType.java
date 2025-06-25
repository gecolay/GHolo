package dev.geco.gholo.object.holo;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface IGHoloRowContentType {

    void load(Player player, String content, boolean create);

    void publishUpdate(@NotNull GHoloUpdateType updateType);

    void unload(Player player);

}