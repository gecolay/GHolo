package dev.geco.gholo.object.interaction;

import org.jetbrains.annotations.NotNull;

public class GInteractionAction {

    private final GInteraction interaction;
    private String type;
    private String parameter;

    public GInteractionAction(@NotNull GInteraction interaction, @NotNull String type, @NotNull String parameter) {
        this.interaction = interaction;
        this.type = type;
        this.parameter = parameter;
    }

    public @NotNull GInteraction getInteraction() { return interaction; }

    public @NotNull String getType() { return type; }

    public @NotNull GInteractionAction setType(String type) {
        this.type = type;
        return this;
    }

    public @NotNull String getParameter() { return parameter; }

    public @NotNull GInteractionAction setParameter(String parameter) {
        this.parameter = parameter;
        return this;
    }

    public int getPosition() { return interaction.getActions().indexOf(this); }

}