package dev.geco.gholo.mcv.v1_21_4.object;

import dev.geco.gholo.object.GInteraction;
import dev.geco.gholo.object.IGInteractionEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Interaction;
import org.bukkit.craftbukkit.CraftWorld;

public class GInteractionEntity extends Interaction implements IGInteractionEntity {

    public GInteractionEntity(GInteraction interaction) {
        super(EntityType.INTERACTION, ((CraftWorld) interaction.getRawLocation().getWorld()).getHandle());
    }

    public void loadInteraction() { }

}