package dev.geco.gholo.object.action.impl;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.object.action.GInteractionActionType;
import dev.geco.gholo.object.action.GInteractionActionTypeResult;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CommandAction extends GInteractionActionType {

    public @NotNull String getType() { return "command"; }

    @Override
    public @NotNull GInteractionActionTypeResult execute(@NotNull GHoloMain gHoloMain, @NotNull Player player, @NotNull String arguments) {

        String command = arguments.replace("%player%", player.getName());

        boolean success = Bukkit.dispatchCommand(player, command);

        return new GInteractionActionTypeResult(success);
    }

}