package dev.geco.gholo.util;

import dev.geco.gholo.objects.*;

public interface IEntityUtil {

    void spawnHolo(GHolo Holo);

    void removeHolo(GHolo Holo);

    IGHoloRowEntity createHoloRowEntity(GHoloRow HoloRow);

}