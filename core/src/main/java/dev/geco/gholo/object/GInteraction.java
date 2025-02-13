package dev.geco.gholo.object;

import dev.geco.gholo.object.action.GInteractionAction;
import org.jetbrains.annotations.NotNull;

public class GInteraction {

    private final Type type;
    private final GInteractionAction action;

    public GInteraction(@NotNull Type type, @NotNull GInteractionAction action) {
        this.type = type;
        this.action = action;
    }

    public @NotNull Type getType() { return type; }

    public @NotNull GInteractionAction getAction() { return action; }

    public enum Type {
        LEFT_CLICK,
        RIGHT_CLICK,
        SHIFT_LEFT_CLICK,
        SHIFT_RIGHT_CLICK
    }

}