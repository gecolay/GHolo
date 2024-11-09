package dev.geco.gholo.cmd;

import org.jetbrains.annotations.*;

import org.bukkit.command.*;
import org.bukkit.entity.*;

import dev.geco.gholo.GHoloMain;

public class GHoloCommand implements CommandExecutor {

    private final GHoloMain GPM;

    public GHoloCommand(GHoloMain GPluginMain) { GPM = GPluginMain; }

    @Override
    public boolean onCommand(@NotNull CommandSender Sender, @NotNull Command Command, @NotNull String Label, String[] Args) {

        if(!(Sender instanceof Player)) {

            GPM.getMManager().sendMessage(Sender, "Messages.command-sender-error");
            return true;
        }

        if(!GPM.getPManager().hasPermission(Sender, "Holo")) {

            GPM.getMManager().sendMessage(Sender, "Messages.command-permission-error");
            return true;
        }

        Player player = (Player) Sender;

        if(Args.length == 0) {

        }

        switch(Args[0]) {


        }

        return true;
    }

}