package dev.geco.gholo.object.interaction;

import dev.geco.gholo.object.interaction.action.GInteractionActionType;
import org.jetbrains.annotations.NotNull;

public class GInteractionAction {

    private final GInteraction interaction;
    private GInteractionActionType interactionActionType;
    private String parameter;

    public GInteractionAction(@NotNull GInteraction interaction, @NotNull GInteractionActionType interactionActionType, @NotNull String parameter) {
        this.interaction = interaction;
        this.interactionActionType = interactionActionType;
        this.parameter = parameter;
    }

    public @NotNull GInteraction getInteraction() { return interaction; }

    public @NotNull GInteractionActionType getInteractionActionType() { return interactionActionType; }

    public @NotNull GInteractionAction setInteractionActionType(GInteractionActionType interactionActionType) {
        this.interactionActionType = interactionActionType;
        return this;
    }

    public @NotNull String getParameter() { return parameter; }

    public @NotNull GInteractionAction setParameter(String parameter) {
        this.parameter = parameter;
        return this;
    }

    public int getPosition() { return interaction.getActions().indexOf(this); }

}