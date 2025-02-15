package dev.geco.gholo.object;

import dev.geco.gholo.object.action.GInteractionAction;
import dev.geco.gholo.object.location.SimpleLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GInteraction {

    private final Type type;
    private final GInteractionAction action;
    private SimpleLocation location;
    private IGInteractionEntity interactionEntity = null;

    public GInteraction(@NotNull Type type, @NotNull GInteractionAction action) {
        this.type = type;
        this.action = action;
    }

    public @NotNull Type getType() { return type; }

    public @NotNull GInteractionAction getAction() { return action; }

    public @NotNull SimpleLocation getRawLocation() { return location.clone(); }

    public @NotNull SimpleLocation getLocation() { return location; }

    public @Nullable IGInteractionEntity getInteractionEntity() { return interactionEntity; }

    public @NotNull GInteraction setInteractionEntity(@Nullable IGInteractionEntity interactionEntity) {
        this.interactionEntity = interactionEntity;
        return this;
    }

    public enum Type {
        LEFT_CLICK,
        RIGHT_CLICK,
        SHIFT_LEFT_CLICK,
        SHIFT_RIGHT_CLICK
    }

}