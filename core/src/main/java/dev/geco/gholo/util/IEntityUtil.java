package dev.geco.gholo.util;

import dev.geco.gholo.objects.*;

public interface IEntityUtil {

    void startHoloTicking(GHolo Holo);

    void stopHoloTicking(GHolo Holo);

    IGHoloRowEntity createHoloRowEntity(GHoloRow HoloRow);

}