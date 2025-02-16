package dev.geco.gholo.object.interaction.action.impl;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.object.interaction.action.GInteractionActionType;
import dev.geco.gholo.object.interaction.action.GInteractionActionTypeResult;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ServerCommandAction extends GInteractionActionType {

    public @NotNull String getType() { return "server_command"; }

    @Override
    public @NotNull GInteractionActionTypeResult execute(@NotNull GHoloMain gHoloMain, @NotNull Player player, @NotNull String arguments) {

        String command = arguments.replace("%player%", player.getName());

        boolean success = Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);

        return new GInteractionActionTypeResult(success);
    }

}