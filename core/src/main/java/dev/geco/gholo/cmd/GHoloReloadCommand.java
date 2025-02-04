package dev.geco.gholo.cmd;

import dev.geco.gholo.GHoloMain;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GHoloReloadCommand implements CommandExecutor {

    private final GHoloMain gHoloMain;

    public GHoloReloadCommand(GHoloMain gHoloMain) {
        this.gHoloMain = gHoloMain;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if(!(sender instanceof Player || sender instanceof ConsoleCommandSender || sender instanceof RemoteConsoleCommandSender)) {
            gHoloMain.getMessageService().sendMessage(sender, "Messages.command-sender-error");
            return true;
        }

        if(!gHoloMain.getPermissionService().hasPermission(sender, "Reload")) {
            gHoloMain.getMessageService().sendMessage(sender, "Messages.command-permission-error");
            return true;
        }

        gHoloMain.reload(sender);

        gHoloMain.getMessageService().sendMessage(sender, "Plugin.plugin-reload");
        return true;
    }

}