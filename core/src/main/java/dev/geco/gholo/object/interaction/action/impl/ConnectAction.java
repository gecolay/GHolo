package dev.geco.gholo.object.interaction.action.impl;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.object.interaction.action.GInteractionActionType;
import dev.geco.gholo.object.interaction.action.GInteractionActionTypeResult;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ConnectAction extends GInteractionActionType {

    public @NotNull String getType() { return "connect"; }

    @Override
    public @NotNull GInteractionActionTypeResult execute(@NotNull GHoloMain gHoloMain, @NotNull Player player, @NotNull String arguments) {

        boolean success = gHoloMain.getServerUtil().connectPlayerToServer(player, arguments);

        return new GInteractionActionTypeResult(success);
    }

}