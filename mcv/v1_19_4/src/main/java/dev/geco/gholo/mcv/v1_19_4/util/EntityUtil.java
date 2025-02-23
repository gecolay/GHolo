package dev.geco.gholo.mcv.v1_19_4.util;

import dev.geco.gholo.mcv.v1_19_4.object.holo.GHoloRowContent;
import dev.geco.gholo.mcv.v1_19_4.object.interaction.GInteractionEntity;
import dev.geco.gholo.object.holo.GHoloRow;
import dev.geco.gholo.object.holo.IGHoloRowContent;
import dev.geco.gholo.object.interaction.GInteraction;
import dev.geco.gholo.object.interaction.IGInteractionEntity;
import dev.geco.gholo.util.IEntityUtil;

public class EntityUtil implements IEntityUtil {

    @Override
    public void createHoloRowEntity(GHoloRow holoRow) {
        IGHoloRowContent holoRowContent = new GHoloRowContent(holoRow);
        holoRow.setHoloRowContent(holoRowContent);
        holoRowContent.loadHoloRow();
    }

    @Override
    public void createInteractionEntity(GInteraction interaction) {
        IGInteractionEntity interactEntity = new GInteractionEntity(interaction);
        interaction.setInteractionEntity(interactEntity);
        interactEntity.loadInteraction();
    }

}