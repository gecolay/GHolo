package dev.geco.gholo.object.interaction.action.impl;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.object.interaction.GInteractType;
import dev.geco.gholo.object.interaction.action.GInteractionActionType;
import dev.geco.gholo.object.interaction.action.GInteractionActionTypeResult;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;

public class TeleportAction extends GInteractionActionType {

    public @NotNull String getType() { return "teleport"; }

    @Override
    public boolean validateParameter(@NotNull GHoloMain gHoloMain, @NotNull String parameter) { return parameter.split(":").length >= 4; }

    @Override
    public @NotNull GInteractionActionTypeResult execute(@NotNull GHoloMain gHoloMain, @NotNull Player player, @NotNull GInteractType interactType, @NotNull String parameter) {
        try {
            Location location = parseLocation(parameter, player, gHoloMain);
            if(location == null) return new GInteractionActionTypeResult(false);

            boolean success = player.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);

            return new GInteractionActionTypeResult(success);
        } catch(NumberFormatException ignored) {  }

        return new GInteractionActionTypeResult(false);
    }

    private Location parseLocation(String parameter, Player player, GHoloMain gHoloMain) throws NumberFormatException {
        String[] split = parameter.split(":");
        if(split.length < 4) return null;
        Location location = player.getLocation();
        World world = gHoloMain.getLocationUtil().parseLocationWorldInput(split[0], location.getWorld());
        if(world == null) return null;
        double x = gHoloMain.getLocationUtil().parseLocationInput(split[1], location.getX());
        double y = gHoloMain.getLocationUtil().parseLocationInput(split[2], location.getX());
        double z = gHoloMain.getLocationUtil().parseLocationInput(split[3], location.getX());
        float yaw = split.length > 4 ? gHoloMain.getLocationUtil().parseLocationInput(split[4], location.getYaw()) : location.getYaw();
        float pitch = split.length > 5 ? gHoloMain.getLocationUtil().parseLocationInput(split[5], location.getPitch()) :location.getPitch();
        return new Location(world, x, y, z, yaw, pitch);
    }

}