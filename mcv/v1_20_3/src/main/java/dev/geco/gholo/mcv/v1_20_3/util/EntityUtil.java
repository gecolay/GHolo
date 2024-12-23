package dev.geco.gholo.mcv.v1_20_3.util;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.mcv.v1_20_3.objects.*;
import dev.geco.gholo.objects.*;
import dev.geco.gholo.util.*;

public class EntityUtil implements IEntityUtil {

    private final GHoloMain GPM;

    public EntityUtil(GHoloMain GPluginMain) { GPM = GPluginMain; }

    @Override
    public void loadHoloRowEntity(GHoloRow HoloRow) {
        GHoloRowEntity holoRowEntity = new GHoloRowEntity(HoloRow);
        HoloRow.setHoloRowEntity(holoRowEntity);
        holoRowEntity.loadHoloRow();
    }

}