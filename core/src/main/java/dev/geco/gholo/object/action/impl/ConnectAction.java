package dev.geco.gholo.object.action.impl;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.object.action.GInteractionAction;
import dev.geco.gholo.object.action.GInteractionActionResult;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ConnectAction extends GInteractionAction {

    public @NotNull String getType() { return "connect"; }

    @Override
    public @NotNull GInteractionActionResult execute(@NotNull GHoloMain gHoloMain, @NotNull Player player, @NotNull String arguments) {

        boolean success = gHoloMain.getServerUtil().connectPlayerToServer(player, arguments);

        return new GInteractionActionResult(success);
    }

}