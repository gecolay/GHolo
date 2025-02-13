package dev.geco.gholo.object.action;

import dev.geco.gholo.GHoloMain;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class GInteractionAction {

    public abstract @NotNull String getType();

    public abstract @NotNull GInteractionActionResult execute(@NotNull GHoloMain gHoloMain, @NotNull Player player, @NotNull String arguments);

}