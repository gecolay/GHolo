package dev.geco.gholo.objects;

import org.bukkit.entity.*;

public interface IGHoloRowEntity {

    void loadHoloRow();

    void loadHoloRow(Player Player);

    void publishUpdate(GHoloRowUpdateType UpdateType);

    void unloadHoloRow();

    void unloadHoloRow(Player Player);

}