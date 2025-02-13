package dev.geco.gholo.object.action.impl;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.object.action.GInteractionAction;
import dev.geco.gholo.object.action.GInteractionActionResult;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;

public class TeleportAction extends GInteractionAction {

    public @NotNull String getType() { return "teleport"; }

    @Override
    public @NotNull GInteractionActionResult execute(@NotNull GHoloMain gHoloMain, @NotNull Player player, @NotNull String arguments) {

        //TODO: parse location
        Location location = player.getLocation();

        boolean success = player.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);

        return new GInteractionActionResult(success);
    }

}