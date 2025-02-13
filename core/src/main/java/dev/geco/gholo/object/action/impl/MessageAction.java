package dev.geco.gholo.object.action.impl;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.object.action.GInteractionAction;
import dev.geco.gholo.object.action.GInteractionActionResult;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MessageAction extends GInteractionAction {

    public @NotNull String getType() { return "message"; }

    @Override
    public @NotNull GInteractionActionResult execute(@NotNull GHoloMain gHoloMain, @NotNull Player player, @NotNull String arguments) {

        String message = arguments.replace("%player%", player.getName());

        gHoloMain.getMessageService().sendMessage(player, message);

        return new GInteractionActionResult(true);
    }

}