package dev.geco.gholo.mcv.v1_20_5.util;

import dev.geco.gholo.mcv.v1_20_5.object.GHoloRowEntity;
import dev.geco.gholo.mcv.v1_20_5.object.GInteractionEntity;
import dev.geco.gholo.object.holo.GHoloRow;
import dev.geco.gholo.object.interaction.GInteraction;
import dev.geco.gholo.util.IEntityUtil;

public class EntityUtil implements IEntityUtil {

    @Override
    public void createHoloRowEntity(GHoloRow holoRow) {
        GHoloRowEntity holoRowEntity = new GHoloRowEntity(holoRow);
        holoRow.setHoloRowEntity(holoRowEntity);
        holoRowEntity.loadHoloRow();
    }

    @Override
    public void createInteractionEntity(GInteraction interaction) {
        GInteractionEntity interactEntity = new GInteractionEntity(interaction);
        interaction.setInteractionEntity(interactEntity);
        interactEntity.loadInteraction();
    }

}