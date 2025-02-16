package dev.geco.gholo.object.action.impl;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.object.action.GInteractionActionType;
import dev.geco.gholo.object.action.GInteractionActionTypeResult;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MessageAction extends GInteractionActionType {

    public @NotNull String getType() { return "message"; }

    @Override
    public @NotNull GInteractionActionTypeResult execute(@NotNull GHoloMain gHoloMain, @NotNull Player player, @NotNull String arguments) {

        String message = arguments.replace("%player%", player.getName());

        gHoloMain.getMessageService().sendMessage(player, message);

        return new GInteractionActionTypeResult(true);
    }

}