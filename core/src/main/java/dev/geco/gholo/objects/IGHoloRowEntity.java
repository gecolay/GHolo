package dev.geco.gholo.objects;

import org.bukkit.entity.*;

public interface IGHoloRowEntity {

    void spawnHoloRow();

    void spawnHoloRow(Player Player);

    void rerender();

    void publishUpdate(GHoloRowUpdateType UpdateType);

    void removeHoloRow();

    void removeHoloRow(Player Player);

}