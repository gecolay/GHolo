package dev.geco.gholo.mcv.v1_20_3.util;

import dev.geco.gholo.mcv.v1_20_3.object.GHoloRowEntity;
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