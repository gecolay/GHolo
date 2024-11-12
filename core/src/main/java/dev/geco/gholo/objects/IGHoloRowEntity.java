package dev.geco.gholo.objects;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface IGHoloRowEntity {

    void spawnHoloRow(Player Player);

    void rerender();

    void updateHoloRowContent(String Content);

    void updateHoloRowLocation(Location Location);

    void removeHoloRow(Player Player);

}