package dev.geco.gholo.cmd;

import java.util.*;

import org.jetbrains.annotations.*;

import org.bukkit.*;
import org.bukkit.event.player.*;

import org.bukkit.command.*;
import org.bukkit.entity.*;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.objects.*;

public class GHoloCommand implements CommandExecutor {

    private final GHoloMain GPM;

    public GHoloCommand(GHoloMain GPluginMain) { GPM = GPluginMain; }

    @Override
    public boolean onCommand(@NotNull CommandSender Sender, @NotNull Command Command, @NotNull String Label, String[] Args) {

        if(!GPM.getPManager().hasPermission(Sender, "Holo")) {

            GPM.getMManager().sendMessage(Sender, "Messages.command-permission-error");
            return true;
        }

        if(Args.length == 0) {
            GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-use-error");
            return true;
        }

        GHolo holo;

        switch(Args[0].toLowerCase()) {
            case "help":
                GPM.getMManager().sendMessage(Sender, "HoloHelpCommand.header");
                for(String helpRow : List.of("help", "list", "create", "info", "remove", "rename", "relocate", "tphere", "setrange", "tpto", "addrow", "setrow", "removerow")) {
                    GPM.getMManager().sendMessage(Sender, "HoloHelpCommand." + helpRow.toLowerCase());
                }
                GPM.getMManager().sendMessage(Sender, "HoloHelpCommand.footer");
                break;
            case "list":
                break;
            case "create":
                if(!(Sender instanceof Player player)) {
                    GPM.getMManager().sendMessage(Sender, "Messages.command-sender-error");
                    break;
                }
                if(Args.length == 1) {
                    GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-create-use-error");
                    break;
                }
                holo = GPM.getHoloManager().getHolo(Args[1]);
                if(holo != null) {
                    GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-create-exist-error", "%Holo%", holo.getId());
                    break;
                }
                GPM.getHoloManager().createHolo(Args[1], player.getLocation());
                GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-create", "%Holo%", Args[1]);
                break;
            case "info":
                if(Args.length == 1) {
                    GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-info-use-error");
                    break;
                }
                holo = GPM.getHoloManager().getHolo(Args[1]);
                if(holo == null) {
                    GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-exist-error", "%Holo%", Args[1].toLowerCase());
                    break;
                }
                GPM.getMManager().sendMessage(Sender, "HoloInfoCommand.header", "%Holo%", holo.getId());
                for(GHoloRow holoRow : holo.getRows()) {
                    GPM.getMManager().sendMessage(Sender, "HoloInfoCommand.row", "%Row%", holoRow.getRow() + 1, "%Content%", holoRow.getContent());
                }
                GPM.getMManager().sendMessage(Sender, "HoloInfoCommand.footer", "%Holo%", holo.getId());
                break;
            case "remove":
                if(Args.length == 1) {
                    GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-remove-use-error");
                    break;
                }
                holo = GPM.getHoloManager().getHolo(Args[1]);
                if(holo == null) {
                    GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-exist-error", "%Holo%", Args[1].toLowerCase());
                    break;
                }
                GPM.getHoloManager().deleteHolo(holo);
                GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-remove", "%Holo%", holo.getId());
                break;
            case "rename":
                if(Args.length <= 2) {
                    GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-rename-use-error");
                    break;
                }
                holo = GPM.getHoloManager().getHolo(Args[1]);
                if(holo == null) {
                    GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-exist-error", "%Holo%", Args[1].toLowerCase());
                    break;
                }
                GHolo newIdHolo = GPM.getHoloManager().getHolo(Args[2]);
                if(newIdHolo != null) {
                    GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-rename-exist-error", "%Holo%", newIdHolo.getId());
                    break;
                }
                String oldId = holo.getId();
                GPM.getHoloManager().updateId(holo, Args[2]);
                GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-rename", "%Holo%", holo.getId(), "%OldHolo%", oldId);
                break;
            case "relocate":
                if(Args.length <= 4) {
                    GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-relocate-use-error");
                    break;
                }
                holo = GPM.getHoloManager().getHolo(Args[1]);
                if(holo == null) {
                    GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-exist-error", "%Holo%", Args[1].toLowerCase());
                    break;
                }
                try {
                    Location location = holo.getLocation();
                    location.set(Double.parseDouble(Args[2]), Double.parseDouble(Args[3]), Double.parseDouble(Args[4]));
                    GPM.getHoloManager().updateLocation(holo, location);
                    GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-relocate", "%Holo%", holo.getId());
                } catch (NumberFormatException e) {
                    GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-relocate-location-error");
                }
                break;
            case "tphere":
                if(!(Sender instanceof Player player)) {
                    GPM.getMManager().sendMessage(Sender, "Messages.command-sender-error");
                    break;
                }
                if(Args.length == 1) {
                    GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-tphere-use-error");
                    break;
                }
                holo = GPM.getHoloManager().getHolo(Args[1]);
                if(holo == null) {
                    GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-exist-error", "%Holo%", Args[1].toLowerCase());
                    break;
                }
                GPM.getHoloManager().updateLocation(holo, player.getLocation());
                GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-tphere", "%Holo%", holo.getId());
                break;
            case "tpto":
                if(!(Sender instanceof Player player)) {
                    GPM.getMManager().sendMessage(Sender, "Messages.command-sender-error");
                    break;
                }
                if(Args.length == 1) {
                    GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-tpto-use-error");
                    break;
                }
                holo = GPM.getHoloManager().getHolo(Args[1]);
                if(holo == null) {
                    GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-exist-error", "%Holo%", Args[1].toLowerCase());
                    break;
                }
                player.teleport(holo.getLocation(), PlayerTeleportEvent.TeleportCause.COMMAND);
                GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-tpto", "%Holo%", holo.getId());
                break;
            case "setrange":
                if(Args.length <= 2) {
                    GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-setrange-use-error");
                    break;
                }
                holo = GPM.getHoloManager().getHolo(Args[1]);
                if(holo == null) {
                    GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-exist-error", "%Holo%", Args[1].toLowerCase());
                    break;
                }
                double range;
                try {
                    range = Double.parseDouble(Args[2]);
                    GPM.getHoloManager().updateRange(holo, range);
                    GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-setrange", "%Holo%", holo.getId(), "%Range%", range);
                } catch (NumberFormatException e) {
                    GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-setrange-range-error", "%Range%", Args[2]);
                }
                break;
            case "addrow":
                if(Args.length <= 2) {
                    GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-addrow-use-error");
                    break;
                }
                holo = GPM.getHoloManager().getHolo(Args[1]);
                if(holo == null) {
                    GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-exist-error", "%Holo%", Args[1].toLowerCase());
                    break;
                }
                StringBuilder addIdStringBuilder = new StringBuilder();
                for (int arg = 2; arg <= Args.length - 1; arg++) addIdStringBuilder.append(Args[arg]).append(" ");
                addIdStringBuilder.deleteCharAt(addIdStringBuilder.length() - 1);
                GPM.getHoloManager().createHoloRow(holo, addIdStringBuilder.toString());
                GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-addrow", "%Holo%", holo.getId(), "%Content%", addIdStringBuilder.toString());
                break;
            case "setrow":
                if(Args.length <= 3) {
                    GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-setrow-use-error");
                    break;
                }
                holo = GPM.getHoloManager().getHolo(Args[1]);
                if(holo == null) {
                    GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-exist-error", "%Holo%", Args[1].toLowerCase());
                    break;
                }
                try {
                    GHoloRow holoRow = holo.getRow(Integer.parseInt(Args[2]));
                    if(holoRow == null) {
                        GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-setrow-row-error", "%Row%", Args[2]);
                        break;
                    }
                    StringBuilder setIdStringBuilder = new StringBuilder();
                    for (int arg = 3; arg <= Args.length - 1; arg++) setIdStringBuilder.append(Args[arg]).append(" ");
                    setIdStringBuilder.deleteCharAt(setIdStringBuilder.length() - 1);
                    GPM.getHoloManager().updateHoloRowContent(holoRow, setIdStringBuilder.toString());
                    GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-setrow", "%Holo%", holo.getId(), "%Row%", holoRow.getRow(), "%Content%", setIdStringBuilder.toString());
                } catch (NumberFormatException e) {
                    GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-setrow-row-error", "%Row%", Args[2]);
                }
                break;
            case "removerow":
                if(Args.length <= 2) {
                    GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-removerow-use-error");
                    break;
                }
                holo = GPM.getHoloManager().getHolo(Args[1]);
                if(holo == null) {
                    GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-exist-error", "%Holo%", Args[1].toLowerCase());
                    break;
                }
                try {
                    GHoloRow holoRow = holo.getRow(Integer.parseInt(Args[2]));
                    if(holoRow == null) {
                        GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-removerow-row-error", "%Row%", Args[2]);
                        break;
                    }
                    GPM.getHoloManager().removeHoloRow(holoRow, true);
                    GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-removerow", "%Holo%", holo.getId(), "%Row%", holoRow.getRow());
                } catch (NumberFormatException e) {
                    GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-removerow-row-error", "%Row%", Args[2]);
                }
                break;
            default:
                GPM.getMManager().sendMessage(Sender, "Messages.command-gholo-use-error");
        }

        return true;
    }

}