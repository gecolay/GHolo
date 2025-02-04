package dev.geco.gholo.mcv.v1_20.util;

import dev.geco.gholo.mcv.v1_20.object.GHoloRowEntity;
import dev.geco.gholo.object.GHoloRow;
import dev.geco.gholo.util.IEntityUtil;

public class EntityUtil implements IEntityUtil {

    @Override
    public void loadHoloRowEntity(GHoloRow holoRow) {
        GHoloRowEntity holoRowEntity = new GHoloRowEntity(holoRow);
        holoRow.setHoloRowEntity(holoRowEntity);
        holoRowEntity.loadHoloRow();
    }

}