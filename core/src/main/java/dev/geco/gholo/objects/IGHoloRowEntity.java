package dev.geco.gholo.objects;

import org.bukkit.entity.*;

public interface IGHoloRowEntity {

    void spawnHoloRow(Player Player);

    void startTicking();

    void stopTicking();

    void rerender();

    void updateHoloRowRange(double Range);

    void updateHoloRowContent(String Content);

    void adjustLocationToHolo();

    void removeHoloRow(Player Player);

}