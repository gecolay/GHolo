package dev.geco.gholo.object.interaction.action.impl;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.object.interaction.GInteractType;
import dev.geco.gholo.object.interaction.action.GInteractionActionType;
import dev.geco.gholo.object.interaction.action.GInteractionActionTypeResult;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CommandAction extends GInteractionActionType {

    public @NotNull String getType() { return "command"; }

    @Override
    public boolean validateParameter(@NotNull GHoloMain gHoloMain, @NotNull String parameter) { return true; }

    @Override
    public @NotNull GInteractionActionTypeResult execute(@NotNull GHoloMain gHoloMain, @NotNull Player player, @NotNull GInteractType interactType, @NotNull String parameter) {

        String command = parameter.replace("%player%", player.getName());

        boolean success = Bukkit.dispatchCommand(player, command);

        return new GInteractionActionTypeResult(success);
    }

}