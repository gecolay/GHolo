package dev.geco.gholo.object.action.impl;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.object.action.GInteractionActionType;
import dev.geco.gholo.object.action.GInteractionActionTypeResult;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;

public class TeleportAction extends GInteractionActionType {

    public @NotNull String getType() { return "teleport"; }

    @Override
    public @NotNull GInteractionActionTypeResult execute(@NotNull GHoloMain gHoloMain, @NotNull Player player, @NotNull String arguments) {

        //TODO: parse location
        Location location = player.getLocation();

        boolean success = player.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);

        return new GInteractionActionTypeResult(success);
    }

}