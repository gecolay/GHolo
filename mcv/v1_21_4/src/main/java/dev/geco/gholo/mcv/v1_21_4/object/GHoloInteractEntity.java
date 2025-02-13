package dev.geco.gholo.mcv.v1_21_4.object;

import dev.geco.gholo.object.IGHoloInteractionEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Interaction;
import net.minecraft.world.level.Level;

public class GHoloInteractEntity extends Interaction implements IGHoloInteractionEntity {

    public GHoloInteractEntity(Level level) {
        super(EntityType.INTERACTION, level);
    }

}