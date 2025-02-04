package dev.geco.gholo.object;

import org.bukkit.entity.Player;

public interface IGHoloRowEntity {

    void loadHoloRow();

    void loadHoloRow(Player player);

    void publishUpdate(GHoloRowUpdateType updateType);

    void unloadHoloRow();

    void unloadHoloRow(Player player);

}