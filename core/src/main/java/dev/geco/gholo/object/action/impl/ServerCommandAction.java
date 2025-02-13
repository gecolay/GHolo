package dev.geco.gholo.object.action.impl;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.object.action.GInteractionAction;
import dev.geco.gholo.object.action.GInteractionActionResult;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ServerCommandAction extends GInteractionAction {

    public @NotNull String getType() { return "server_command"; }

    @Override
    public @NotNull GInteractionActionResult execute(@NotNull GHoloMain gHoloMain, @NotNull Player player, @NotNull String arguments) {

        String command = arguments.replace("%player%", player.getName());

        boolean success = Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);

        return new GInteractionActionResult(success);
    }

}