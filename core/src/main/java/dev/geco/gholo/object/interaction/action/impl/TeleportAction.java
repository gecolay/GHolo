package dev.geco.gholo.object.interaction.action.impl;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.object.interaction.GInteractType;
import dev.geco.gholo.object.interaction.action.GInteractionActionType;
import dev.geco.gholo.object.interaction.action.GInteractionActionTypeResult;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TeleportAction extends GInteractionActionType {

    public @NotNull String getType() { return "teleport"; }

    @Override
    public boolean validateParameter(@NotNull String parameter) { return parseLocation(parameter, null) != null; }

    @Override
    public @NotNull GInteractionActionTypeResult execute(@NotNull GHoloMain gHoloMain, @NotNull Player player, @NotNull GInteractType interactType, @NotNull String parameter) {

        Location location = parseLocation(parameter, player);
        if(location == null) return new GInteractionActionTypeResult(false);

        boolean success = player.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);

        return new GInteractionActionTypeResult(success);
    }

    private @Nullable Location parseLocation(@NotNull String parameter, @Nullable Player player) {
        String[] split = parameter.split(":");
        if(split.length < 4) return null;
        try {
            Location location = player != null ? player.getLocation() : null;
            World world = split[0].equalsIgnoreCase("~") ? (location != null ? location.getWorld() : null) : Bukkit.getWorld(split[0]);
            double x = split[1].equalsIgnoreCase("~") ? (location != null ? location.getX() : 0) : Double.parseDouble(split[1]);
            double y = split[2].equalsIgnoreCase("~") ? (location != null ? location.getY() : 0) : Double.parseDouble(split[2]);
            double z = split[3].equalsIgnoreCase("~") ? (location != null ? location.getZ() : 0) : Double.parseDouble(split[3]);
            float yaw = split.length > 4 ? (split[4].equalsIgnoreCase("~") ? (location != null ? location.getYaw() : 0) : Float.parseFloat(split[4])) : (location != null ? location.getYaw() : 0);
            float pitch = split.length > 5 ? (split[5].equalsIgnoreCase("~") ? (location != null ? location.getPitch() : 0) : Float.parseFloat(split[5])) : (location != null ? location.getPitch() : 0);
            return new Location(world, x, y, z, yaw, pitch);
        } catch(NumberFormatException e) { }
        return null;
    }

}