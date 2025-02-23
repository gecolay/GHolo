package dev.geco.gholo.mcv.v1_20_5.object.holo.type;

import dev.geco.gholo.object.holo.GHoloUpdateType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface IGHoloRowContentType {

    void load(Player player, String content, boolean create);

    void publishUpdate(@NotNull GHoloUpdateType updateType);

    void unload(Player player);

}