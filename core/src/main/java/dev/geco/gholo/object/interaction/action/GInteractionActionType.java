package dev.geco.gholo.object.interaction.action;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.object.interaction.GInteractType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class GInteractionActionType {

    public abstract @NotNull String getType();

    public abstract boolean validateParameter(@NotNull String parameter);

    public abstract @NotNull GInteractionActionTypeResult execute(@NotNull GHoloMain gHoloMain, @NotNull Player player, @NotNull GInteractType interactType, @NotNull String parameter);

}