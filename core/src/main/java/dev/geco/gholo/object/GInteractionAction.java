package dev.geco.gholo.object;

import org.jetbrains.annotations.NotNull;

public class GInteractionAction {

    private final GInteraction interaction;
    private final String type;
    private final String parameter;

    public GInteractionAction(@NotNull GInteraction interaction, @NotNull String type, @NotNull String parameter) {
        this.interaction = interaction;
        this.type = type;
        this.parameter = parameter;
    }

    public @NotNull GInteraction getInteraction() { return interaction; }

    public @NotNull String getType() { return type; }

    public @NotNull String getParameter() { return parameter; }

    public int getPosition() { return interaction.getActions().indexOf(this); }

}