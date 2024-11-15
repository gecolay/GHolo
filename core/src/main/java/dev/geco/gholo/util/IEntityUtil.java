package dev.geco.gholo.util;

import org.bukkit.entity.*;

import dev.geco.gholo.objects.*;

public interface IEntityUtil {

    void spawnHolo(GHolo Holo);

    void spawnHolo(GHolo Holo, Player Player);

    void removeHolo(GHolo Holo);

    void removeHolo(GHolo Holo, Player Player);

    IGHoloRowEntity createHoloRowEntity(GHoloRow HoloRow);

}