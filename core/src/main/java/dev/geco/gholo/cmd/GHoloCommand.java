package dev.geco.gholo.cmd;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.object.holo.GHolo;
import dev.geco.gholo.object.holo.GHoloData;
import dev.geco.gholo.object.holo.GHoloRow;
import dev.geco.gholo.object.holo.GHoloUpdateType;
import dev.geco.gholo.object.holo.exporter.GHoloExporter;
import dev.geco.gholo.object.holo.exporter.GHoloExporterResult;
import dev.geco.gholo.object.holo.importer.GHoloImporter;
import dev.geco.gholo.object.holo.importer.GHoloImporterResult;
import dev.geco.gholo.object.simple.SimpleLocation;
import dev.geco.gholo.object.simple.SimpleVector;
import dev.geco.gholo.util.ImageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;
import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

public class GHoloCommand implements CommandExecutor {

    public static List<String> SUBCOMMANDS = List.of("help", "list", "near", "create", "info", "remove", "rename", "move", "tphere", "tp", "align", "addrow", "insertrow", "setrow", "removerow", "offsetrow", "copy", "option", "image", "import", "export");

    private final GHoloMain gHoloMain;

    public GHoloCommand(GHoloMain gHoloMain) {
        this.gHoloMain = gHoloMain;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if(!gHoloMain.getPermissionService().hasPermission(sender, "Holo")) {
            gHoloMain.getMessageService().sendMessage(sender, "Messages.command-permission-error");
            return true;
        }

        if(args.length == 0) {
            gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-use-error");
            return true;
        }

        switch(args[0].toLowerCase()) {
            case "help" -> {
                gHoloMain.getMessageService().sendMessage(sender, "HoloHelpCommand.header");
                for(String helpRow : SUBCOMMANDS) {
                    gHoloMain.getMessageService().sendMessage(sender, "HoloHelpCommand." + helpRow.toLowerCase());
                }
                gHoloMain.getMessageService().sendMessage(sender, "HoloHelpCommand.footer");
            }
            case "list" -> {
                List<GHolo> holoList = gHoloMain.getHoloService().getHolos();
                if(holoList.isEmpty()) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-none");
                    break;
                }
                try {
                    int pageSize = gHoloMain.getConfigService().LIST_PAGE_SIZE;
                    int page = args.length > 1 ? Integer.parseInt(args[1]) : 1;
                    int totalHoloCount = holoList.size();
                    int maxPage = (int) Math.ceil((double) totalHoloCount / pageSize);
                    page = Math.max(Math.min(page, maxPage), 1);
                    gHoloMain.getMessageService().sendMessage(sender, "HoloListCommand.header", "%Page%", page, "%MaxPage%", maxPage);
                    int startIndex = (page - 1) * pageSize;
                    int endIndex = Math.min(startIndex + pageSize, totalHoloCount);
                    for(int i = startIndex; i < endIndex; i++) {
                        GHolo listHolo = holoList.get(i);
                        SimpleLocation holoLocation = listHolo.getRawLocation();
                        BigDecimal x = BigDecimal.valueOf(holoLocation.getX()).setScale(2, RoundingMode.HALF_UP);
                        BigDecimal y = BigDecimal.valueOf(holoLocation.getY()).setScale(2, RoundingMode.HALF_UP);
                        BigDecimal z = BigDecimal.valueOf(holoLocation.getZ()).setScale(2, RoundingMode.HALF_UP);
                        gHoloMain.getMessageService().sendMessage(sender, "HoloListCommand.holo", "%Holo%", listHolo.getId(), "%X%", x.stripTrailingZeros().toPlainString(), "%Y%", y.stripTrailingZeros().toPlainString(), "%Z%", z.stripTrailingZeros().toPlainString(), "%World%", holoLocation.getWorld().getName());
                    }
                    gHoloMain.getMessageService().sendMessage(sender, "HoloListCommand.footer", "%Page%", page, "%MaxPage%", maxPage);
                } catch(NumberFormatException e) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-list-page-error", "%Page%", args[1]);
                }
            }
            case "near" -> {
                if(!(sender instanceof Player player)) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-sender-error");
                    break;
                }
                if(gHoloMain.getHoloService().getHolos().isEmpty()) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-none");
                    break;
                }
                try {
                    double range = args.length > 1 ? Double.parseDouble(args[1]) : gHoloMain.getConfigService().NEAR_RANGE;
                    List<GHolo> nearHoloList = gHoloMain.getHoloService().getNearHolos(player.getLocation(), range);
                    if(nearHoloList.isEmpty()) {
                        gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-near-none");
                        break;
                    }
                    gHoloMain.getMessageService().sendMessage(sender, "HoloNearCommand.header", "%Range%", range);
                    for(GHolo nearHolo : nearHoloList) {
                        SimpleLocation holoLocation = nearHolo.getRawLocation();
                        BigDecimal x = BigDecimal.valueOf(holoLocation.getX()).setScale(2, RoundingMode.HALF_UP);
                        BigDecimal y = BigDecimal.valueOf(holoLocation.getY()).setScale(2, RoundingMode.HALF_UP);
                        BigDecimal z = BigDecimal.valueOf(holoLocation.getZ()).setScale(2, RoundingMode.HALF_UP);
                        gHoloMain.getMessageService().sendMessage(sender, "HoloNearCommand.holo", "%Holo%", nearHolo.getId(), "%X%", x.stripTrailingZeros().toPlainString(), "%Y%", y.stripTrailingZeros().toPlainString(), "%Z%", z.stripTrailingZeros().toPlainString(), "%World%", holoLocation.getWorld().getName());
                    }
                    gHoloMain.getMessageService().sendMessage(sender, "HoloNearCommand.footer", "%Range%", range);
                } catch(NumberFormatException e) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-near-range-error", "%Range%", args[1]);

                }
            }
            case "create" -> {
                if(!(sender instanceof Player player)) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-sender-error");
                    break;
                }
                if(args.length == 1) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-create-use-error");
                    break;
                }
                GHolo holo = gHoloMain.getHoloService().getHolo(args[1]);
                if(holo != null) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-create-exist-error", "%Holo%", holo.getId());
                    break;
                }
                gHoloMain.getHoloService().createHolo(args[1], SimpleLocation.fromBukkitLocation(player.getLocation()));
                gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-create", "%Holo%", args[1]);
            }
            case "info" -> {
                if(args.length == 1) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-info-use-error");
                    break;
                }
                GHolo holo = gHoloMain.getHoloService().getHolo(args[1]);
                if(holo == null) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-exist-error", "%Holo%", args[1]);
                    break;
                }
                gHoloMain.getMessageService().sendMessage(sender, "HoloInfoCommand.header", "%Holo%", holo.getId());
                SimpleLocation holoInfoLocation = holo.getRawLocation();
                BigDecimal x = BigDecimal.valueOf(holoInfoLocation.getX()).setScale(2, RoundingMode.HALF_UP);
                BigDecimal y = BigDecimal.valueOf(holoInfoLocation.getY()).setScale(2, RoundingMode.HALF_UP);
                BigDecimal z = BigDecimal.valueOf(holoInfoLocation.getZ()).setScale(2, RoundingMode.HALF_UP);
                gHoloMain.getMessageService().sendMessage(sender, "HoloInfoCommand.location", "%X%", x.stripTrailingZeros().toPlainString(), "%Y%", y.stripTrailingZeros().toPlainString(), "%Z%", z.stripTrailingZeros().toPlainString(), "%World%", holoInfoLocation.getWorld().getName());
                int position = 1;
                for(GHoloRow holoRow : holo.getRows()) {
                    gHoloMain.getMessageService().sendMessage(sender, "HoloInfoCommand.row", "%Position%", position, "%Content%", holoRow.getContent());
                    position++;
                }
                gHoloMain.getMessageService().sendMessage(sender, "HoloInfoCommand.footer", "%Holo%", holo.getId());
            }
            case "remove" -> {
                if(args.length == 1) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-remove-use-error");
                    break;
                }
                GHolo holo = gHoloMain.getHoloService().getHolo(args[1]);
                if(holo == null) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-exist-error", "%Holo%", args[1]);
                    break;
                }
                gHoloMain.getHoloService().removeHolo(holo);
                gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-remove", "%Holo%", holo.getId());
            }
            case "rename" -> {
                if(args.length <= 2) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-rename-use-error");
                    break;
                }
                GHolo holo = gHoloMain.getHoloService().getHolo(args[1]);
                if(holo == null) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-exist-error", "%Holo%", args[1]);
                    break;
                }
                GHolo newIdHolo = gHoloMain.getHoloService().getHolo(args[2]);
                if(newIdHolo != null) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-rename-exist-error", "%Holo%", newIdHolo.getId());
                    break;
                }
                String oldId = holo.getId();
                gHoloMain.getHoloService().updateHoloId(holo, args[2]);
                gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-rename", "%Holo%", holo.getId(), "%OldHolo%", oldId);
            }
            case "move" -> {
                if(args.length <= 4) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-move-use-error");
                    break;
                }
                GHolo holo = gHoloMain.getHoloService().getHolo(args[1]);
                if(holo == null) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-exist-error", "%Holo%", args[1]);
                    break;
                }
                try {
                    SimpleLocation location = holo.getLocation();
                    double x = gHoloMain.getLocationUtil().parseLocationInput(args[2], location.getX());
                    double y = gHoloMain.getLocationUtil().parseLocationInput(args[3], location.getY());
                    double z = gHoloMain.getLocationUtil().parseLocationInput(args[4], location.getZ());
                    location.setX(x);
                    location.setY(y);
                    location.setZ(z);
                    gHoloMain.getHoloService().updateHoloLocation(holo, location);
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-move", "%Holo%", holo.getId());
                } catch(NumberFormatException e) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-move-location-error");
                }
            }
            case "tphere" -> {
                if(!(sender instanceof Player player)) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-sender-error");
                    break;
                }
                if(args.length == 1) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-tphere-use-error");
                    break;
                }
                GHolo holo = gHoloMain.getHoloService().getHolo(args[1]);
                if(holo == null) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-exist-error", "%Holo%", args[1]);
                    break;
                }
                gHoloMain.getHoloService().updateHoloLocation(holo, SimpleLocation.fromBukkitLocation(player.getLocation()));
                gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-tphere", "%Holo%", holo.getId());
            }
            case "tp" -> {
                if(!(sender instanceof Player player)) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-sender-error");
                    break;
                }
                if(args.length == 1) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-tp-use-error");
                    break;
                }
                GHolo holo = gHoloMain.getHoloService().getHolo(args[1]);
                if(holo == null) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-exist-error", "%Holo%", args[1]);
                    break;
                }
                Location location = holo.getLocation();
                location.setYaw(player.getLocation().getYaw());
                location.setPitch(player.getLocation().getPitch());
                player.teleport(location, PlayerTeleportEvent.TeleportCause.COMMAND);
                gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-tp", "%Holo%", holo.getId());
            }
            case "align" -> {
                if(args.length <= 3) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-align-use-error");
                    break;
                }
                GHolo holo = gHoloMain.getHoloService().getHolo(args[1]);
                if(holo == null) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-exist-error", "%Holo%", args[1]);
                    break;
                }
                GHolo alignOnHolo = gHoloMain.getHoloService().getHolo(args[2]);
                if(alignOnHolo == null) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-exist-error", "%Holo%", args[2].toLowerCase());
                    break;
                }
                SimpleLocation holoLocation = holo.getLocation();
                SimpleLocation alignOnHoloLocation = alignOnHolo.getRawLocation();
                String appliedAxis = "";
                String axis = args[3].toLowerCase();
                if(axis.contains("x")) {
                    holoLocation.setX(alignOnHoloLocation.getX());
                    appliedAxis += "x";
                }
                if(axis.contains("y")) {
                    holoLocation.setY(alignOnHoloLocation.getY());
                    appliedAxis += "y";
                }
                if(axis.contains("z")) {
                    holoLocation.setZ(alignOnHoloLocation.getZ());
                    appliedAxis += "z";
                }
                if(appliedAxis.isEmpty()) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-align-axis-error", "%Axis%", axis);
                    break;
                }
                gHoloMain.getHoloService().updateHoloLocation(holo, holoLocation);
                gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-align", "%Holo%", holo.getId(), "%Axis%", appliedAxis, "%AlignOnHolo%", alignOnHolo.getId());
            }
            case "addrow" -> {
                if(args.length == 1) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-addrow-use-error");
                    break;
                }
                GHolo holo = gHoloMain.getHoloService().getHolo(args[1]);
                if(holo == null) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-exist-error", "%Holo%", args[1]);
                    break;
                }
                StringBuilder addIdStringBuilder = new StringBuilder();
                if(args.length > 2) {
                    for(int arg = 2; arg <= args.length - 1; arg++) addIdStringBuilder.append(args[arg]).append(" ");
                    addIdStringBuilder.deleteCharAt(addIdStringBuilder.length() - 1);
                }
                gHoloMain.getHoloService().addHoloRow(holo, addIdStringBuilder.toString());
                gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-addrow", "%Holo%", holo.getId(), "%Content%", addIdStringBuilder.toString());
            }
            case "insertrow" -> {
                if(args.length <= 2) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-insertrow-use-error");
                    break;
                }
                GHolo holo = gHoloMain.getHoloService().getHolo(args[1]);
                if(holo == null) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-exist-error", "%Holo%", args[1]);
                    break;
                }
                try {
                    GHoloRow holoRow = holo.getRow(Integer.parseInt(args[2]) - 1);
                    if(holoRow == null) {
                        gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-row-error", "%Position%", args[2]);
                        break;
                    }
                    StringBuilder insertIdStringBuilder = new StringBuilder();
                    if(args.length > 3) {
                        for(int arg = 3; arg <= args.length - 1; arg++) insertIdStringBuilder.append(args[arg]).append(" ");
                        insertIdStringBuilder.deleteCharAt(insertIdStringBuilder.length() - 1);
                    }
                    gHoloMain.getHoloService().insertHoloRow(holo, holoRow.getPosition(), insertIdStringBuilder.toString(), true);
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-insertrow", "%Holo%", holo.getId(), "%Position%", Integer.parseInt(args[2]), "%Content%", insertIdStringBuilder.toString());
                } catch(NumberFormatException e) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-row-error", "%Position%", args[2]);
                }
            }
            case "setrow" -> {
                if(args.length <= 2) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-setrow-use-error");
                    break;
                }
                GHolo holo = gHoloMain.getHoloService().getHolo(args[1]);
                if(holo == null) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-exist-error", "%Holo%", args[1]);
                    break;
                }
                try {
                    GHoloRow holoRow = holo.getRow(Integer.parseInt(args[2]) - 1);
                    if(holoRow == null) {
                        gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-row-error", "%Position%", args[2]);
                        break;
                    }
                    StringBuilder setIdStringBuilder = new StringBuilder();
                    if(args.length > 3) {
                        for(int arg = 3; arg <= args.length - 1; arg++) setIdStringBuilder.append(args[arg]).append(" ");
                        setIdStringBuilder.deleteCharAt(setIdStringBuilder.length() - 1);
                    }
                    gHoloMain.getHoloService().updateHoloRowContent(holoRow, setIdStringBuilder.toString());
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-setrow", "%Holo%", holo.getId(), "%Position%", Integer.parseInt(args[2]), "%Content%", setIdStringBuilder.toString());
                } catch(NumberFormatException e) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-row-error", "%Position%", args[2]);
                }
            }
            case "removerow" -> {
                if(args.length <= 2) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-removerow-use-error");
                    break;
                }
                GHolo holo = gHoloMain.getHoloService().getHolo(args[1]);
                if(holo == null) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-exist-error", "%Holo%", args[1]);
                    break;
                }
                try {
                    GHoloRow holoRow = holo.getRow(Integer.parseInt(args[2]) - 1);
                    if(holoRow == null) {
                        gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-row-error", "%Position%", args[2]);
                        break;
                    }
                    boolean updateOffsets = true;
                    if(args.length > 3) updateOffsets = Boolean.parseBoolean(args[3]);
                    gHoloMain.getHoloService().removeHoloRow(holoRow, updateOffsets);
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-removerow", "%Holo%", holo.getId(), "%Position%", Integer.parseInt(args[2]));
                } catch(NumberFormatException e) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-row-error", "%Position%", args[2]);
                }
            }
            case "offsetrow" -> {
                if(args.length <= 5) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-offsetrow-use-error");
                    break;
                }
                GHolo holo = gHoloMain.getHoloService().getHolo(args[1]);
                if(holo == null) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-exist-error", "%Holo%", args[1]);
                    break;
                }
                try {
                    GHoloRow holoRow = holo.getRow(Integer.parseInt(args[2]) - 1);
                    if(holoRow == null) {
                        gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-row-error", "%Position%", args[2]);
                        break;
                    }
                    try {
                        SimpleVector offset = holoRow.getOffset();
                        offset.setX(args[3].equalsIgnoreCase("*") ? 0 : gHoloMain.getLocationUtil().parseLocationInput(args[3], offset.getX()));
                        if(args[4].equalsIgnoreCase("*")) {
                            double sizeBetweenRows = gHoloMain.getConfigService().DEFAULT_SIZE_BETWEEN_ROWS;
                            double rowOffset = sizeBetweenRows * holoRow.getPosition();
                            offset.setY(-rowOffset);
                        } else offset.setY(gHoloMain.getLocationUtil().parseLocationInput(args[4], offset.getY()));
                        offset.setZ(args[5].equalsIgnoreCase("*") ? 0 : gHoloMain.getLocationUtil().parseLocationInput(args[5], offset.getZ()));
                        gHoloMain.getHoloService().updateHoloRowOffset(holoRow, offset);
                        gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-offsetrow", "%Holo%", holo.getId());
                    } catch(NumberFormatException e) {
                        gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-offsetrow-offset-error");
                    }
                } catch(NumberFormatException e) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-row-error", "%Position%", args[2]);
                }
            }
            case "copy" -> {
                if(args.length <= 2) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-copy-use-error");
                    break;
                }
                GHolo holo = gHoloMain.getHoloService().getHolo(args[1]);
                if(holo == null) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-exist-error", "%Holo%", args[1]);
                    break;
                }
                if(gHoloMain.getHoloService().getHolo(args[2]) != null) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-copy-exist-error", "%Holo%", args[2].toLowerCase());
                    break;
                }
                gHoloMain.getHoloService().copyHolo(holo, args[2]);
                gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-copy", "%Holo%", holo.getId(), "%NewHolo%", args[2]);
            }
            case "option" -> {
                if(args.length <= 4) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-option-use-error");
                    break;
                }
                GHolo holo = gHoloMain.getHoloService().getHolo(args[1]);
                if(holo == null) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-exist-error", "%Holo%", args[1]);
                    break;
                }
                GHoloData data = null;
                int optionArg = 3;
                GHoloRow optionHoloRow = null;
                switch(args[2].toLowerCase()) {
                    case "holo" -> {
                        data = holo.getData();
                    }
                    case "row" -> {
                        try {
                            optionHoloRow = holo.getRow(Integer.parseInt(args[3]) - 1);
                            if(optionHoloRow == null) {
                                gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-row-error", "%Position%", args[3]);
                                return true;
                            }
                            data = optionHoloRow.getData();
                            optionArg = 4;
                        } catch(NumberFormatException e) {
                            gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-row-error", "%Position%", args[3]);
                            return true;
                        }
                    }
                }
                if(data == null) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-option-use-error");
                    break;
                }
                GHoloUpdateType optionUpdateType;
                switch(args[optionArg].toLowerCase()) {
                    case "range" -> {
                        try {
                            if(args[optionArg + 1].equalsIgnoreCase("*")) data.setRange(GHoloData.DEFAULT_RANGE);
                            else data.setRange(Double.parseDouble(args[optionArg + 1]));
                            optionUpdateType = GHoloUpdateType.RANGE;
                        } catch(NumberFormatException e) {
                            gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-option-value-error", "%Option%", args[optionArg], "%Value%", args[optionArg + 1]);
                            return true;
                        }
                    }
                    case "background_color" -> {
                        if(args[optionArg + 1].equalsIgnoreCase("*")) data.setBackgroundColor(GHoloData.DEFAULT_BACKGROUND_COLOR);
                        else {
                            String backgroundColor = args[optionArg + 1].toLowerCase();
                            if(backgroundColor.matches("^#?[0-9A-Fa-f]{6}([0-9A-Fa-f]{2})?$") || backgroundColor.equalsIgnoreCase("transparent")) {
                                data.setBackgroundColor(backgroundColor);
                            } else {
                                gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-option-value-error", "%Option%", args[optionArg], "%Value%", backgroundColor);
                                return true;
                            }
                        }
                        optionUpdateType = GHoloUpdateType.BACKGROUND_COLOR;
                    }
                    case "text_opacity" -> {
                        try {
                            if(args[optionArg + 1].equalsIgnoreCase("*")) data.setTextOpacity(GHoloData.DEFAULT_TEXT_OPACITY);
                            else data.setTextOpacity(Byte.parseByte(args[optionArg + 1]));
                            optionUpdateType = GHoloUpdateType.TEXT_OPACITY;
                        } catch(NumberFormatException e) {
                            gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-option-value-error", "%Option%", args[optionArg], "%Value%", args[optionArg + 1]);
                            return true;
                        }
                    }
                    case "text_shadow" -> {
                        try {
                            if(args[optionArg + 1].equalsIgnoreCase("*")) data.setTextShadow(GHoloData.DEFAULT_HAS_TEXT_SHADOW);
                            else data.setTextShadow(Boolean.parseBoolean(args[optionArg + 1]));
                            optionUpdateType = GHoloUpdateType.TEXT_SHADOW;
                        } catch(NumberFormatException e) {
                            gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-option-value-error", "%Option%", args[optionArg], "%Value%", args[optionArg + 1]);
                            return true;
                        }
                    }
                    case "text_alignment" -> {
                        try {
                            if(args[optionArg + 1].equalsIgnoreCase("*")) data.setTextAlignment(GHoloData.DEFAULT_TEXT_ALIGNMENT);
                            else data.setTextAlignment(TextDisplay.TextAlignment.valueOf(args[optionArg + 1].toUpperCase()).name().toLowerCase());
                            optionUpdateType = GHoloUpdateType.TEXT_ALIGNMENT;
                        } catch(IllegalArgumentException e) {
                            gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-option-value-error", "%Option%", args[optionArg], "%Value%", args[optionArg + 1].toUpperCase());
                            return true;
                        }
                    }
                    case "billboard" -> {
                        try {
                            if(args[optionArg + 1].equalsIgnoreCase("*")) data.setBillboard(GHoloData.DEFAULT_BILLBOARD);
                            else data.setBillboard(Display.Billboard.valueOf(args[optionArg + 1].toUpperCase()).name().toLowerCase());
                            optionUpdateType = GHoloUpdateType.BILLBOARD;
                        } catch(IllegalArgumentException e) {
                            gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-option-value-error", "%Option%", args[optionArg], "%Value%", args[optionArg + 1].toUpperCase());
                            return true;
                        }
                    }
                    case "see_through" -> {
                        try {
                            if(args[optionArg + 1].equalsIgnoreCase("*")) data.setSeeThrough(GHoloData.DEFAULT_CAN_SEE_THROUGH);
                            else data.setSeeThrough(Boolean.parseBoolean(args[optionArg + 1]));
                            optionUpdateType = GHoloUpdateType.SEE_THROUGH;
                        } catch(NumberFormatException e) {
                            gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-option-value-error", "%Option%", args[optionArg], "%Value%", args[optionArg + 1]);
                            return true;
                        }
                    }
                    case "scale" -> {
                        try {
                            if(args[optionArg + 1].equalsIgnoreCase("*")) data.setScale(GHoloData.DEFAULT_SCALE);
                            else data.setScale(new SimpleVector(Float.parseFloat(args[optionArg + 1]), Float.parseFloat(args[optionArg + 1]), Float.parseFloat(args[optionArg + 1])));
                            optionUpdateType = GHoloUpdateType.SCALE;
                        } catch(NumberFormatException e) {
                            gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-option-value-error", "%Option%", args[optionArg], "%Value%", args[optionArg + 1]);
                            return true;
                        }
                    }
                    case "rotation" -> {
                        optionArg++;
                        if(args.length == optionArg + 1) {
                            gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-option-use-error");
                            return true;
                        }
                        switch(args[optionArg].toLowerCase()) {
                            case "yaw" -> {
                                try {
                                    if(args[optionArg + 1].equalsIgnoreCase("*")) data.getRawRotation().setYaw(null);
                                    else data.getRawRotation().setYaw(Float.parseFloat(args[optionArg + 1]));
                                    optionUpdateType = GHoloUpdateType.LOCATION;
                                } catch(NumberFormatException e) {
                                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-option-value-error", "%Option%", args[optionArg], "%Value%", args[optionArg + 1]);
                                    return true;
                                }
                            }
                            case "pitch" -> {
                                try {
                                    if(args[optionArg + 1].equalsIgnoreCase("*")) data.getRawRotation().setPitch(null);
                                    else data.getRawRotation().setPitch(Float.parseFloat(args[optionArg + 1]));
                                    optionUpdateType = GHoloUpdateType.LOCATION;
                                } catch(NumberFormatException e) {
                                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-option-value-error", "%Option%", args[optionArg], "%Value%", args[optionArg + 1]);
                                    return true;
                                }
                            }
                            default -> {
                                gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-option-use-error");
                                return true;
                            }
                        }
                    }
                    case "brightness" -> {
                        try {
                            if(args[optionArg + 1].equalsIgnoreCase("*")) data.setBrightness(GHoloData.DEFAULT_TEXT_OPACITY);
                            else data.setBrightness(Byte.parseByte(args[optionArg + 1]));
                            optionUpdateType = GHoloUpdateType.BRIGHTNESS;
                        } catch(NumberFormatException e) {
                            gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-option-value-error", "%Option%", args[optionArg], "%Value%", args[optionArg + 1]);
                            return true;
                        }
                    }
                    case "permission" -> {
                        if(args[optionArg + 1].equalsIgnoreCase("*")) data.setPermission(GHoloData.DEFAULT_PERMISSION);
                        else data.setPermission(args[optionArg + 1]);
                        optionUpdateType = GHoloUpdateType.PERMISSION;
                    }
                    case "size" -> {
                        optionArg++;
                        if(args.length == optionArg + 1) {
                            gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-option-use-error");
                            return true;
                        }
                        switch(args[optionArg].toLowerCase()) {
                            case "width" -> {
                                try {
                                    if(args[optionArg + 1].equalsIgnoreCase("*")) data.getRawSize().setWidth(1f);
                                    else data.getRawSize().setWidth(Float.parseFloat(args[optionArg + 1]));
                                    optionUpdateType = GHoloUpdateType.SIZE;
                                } catch(NumberFormatException e) {
                                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-option-value-error", "%Option%", args[optionArg], "%Value%", args[optionArg + 1]);
                                    return true;
                                }
                            }
                            case "height" -> {
                                try {
                                    if(args[optionArg + 1].equalsIgnoreCase("*")) data.getRawSize().setHeight(1f);
                                    else data.getRawSize().setHeight(Float.parseFloat(args[optionArg + 1]));
                                    optionUpdateType = GHoloUpdateType.SIZE;
                                } catch(NumberFormatException e) {
                                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-option-value-error", "%Option%", args[optionArg], "%Value%", args[optionArg + 1]);
                                    return true;
                                }
                            }
                            default -> {
                                gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-option-use-error");
                                return true;
                            }
                        }
                    }
                    default -> {
                        gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-option-use-error");
                        return true;
                    }
                }
                if(optionHoloRow == null) {
                    gHoloMain.getHoloService().updateHoloData(holo, data);
                    for(GHoloRow updateHoloRow : holo.getRows()) updateHoloRow.getHoloRowContent().publishUpdate(optionUpdateType);
                } else {
                    gHoloMain.getHoloService().updateHoloRowData(optionHoloRow, data);
                    optionHoloRow.getHoloRowContent().publishUpdate(optionUpdateType);
                }
                gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-option", "%Option%", args[optionArg], "%Value%", args[optionArg + 1]);
            }
            case "image" -> {
                if(args.length <= 3) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-image-use-error");
                    break;
                }
                GHolo holo = gHoloMain.getHoloService().getHolo(args[1]);
                if(holo == null) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-exist-error", "%Holo%", args[1]);
                    break;
                }
                gHoloMain.getTaskService().run(() -> {
                    BufferedImage bufferedImage = null;
                    switch(args[2].toLowerCase()) {
                        case "file" -> {
                            File imageFile = new File(ImageUtil.IMAGE_FOLDER, args[3]);
                            if(!imageFile.exists()) break;
                            bufferedImage = ImageUtil.getBufferedImage(imageFile);
                        }
                        case "url" -> bufferedImage = ImageUtil.getBufferedImage(args[3]);
                        case "avatar", "helm" -> {
                            OfflinePlayer target;
                            try {
                                target = Bukkit.getOfflinePlayer(UUID.fromString(args[3]));
                            } catch(Throwable e) {
                                target = Bukkit.getOfflinePlayer(args[3]);
                            }
                            bufferedImage = ImageUtil.getBufferedImage(target, args[2].equalsIgnoreCase("helm"));
                        }
                        default -> {
                            gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-image-use-error");
                            return;
                        }
                    }
                    if(bufferedImage == null) {
                        gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-image-image-error", "%Type%", args[2].toLowerCase(), "%Source%", args[3]);
                        return;
                    }
                    List<String> rows;
                    if(args.length > 4) {
                        try {
                            if(args[4].contains(":")) {
                                String[] sizes = args[4].split(":");
                                rows = new ImageUtil(bufferedImage, Integer.parseInt(sizes[0]), Integer.parseInt(sizes[1])).getLines();
                            } else rows = new ImageUtil(bufferedImage, Integer.parseInt(args[4])).getLines();
                        } catch(Throwable e) {
                            gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-image-size-error");
                            return;
                        }
                    }  else rows = new ImageUtil(bufferedImage).getLines();
                    gHoloMain.getHoloService().setAllHoloRowContent(holo, rows);
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-image", "%Holo%", holo.getId(), "%Type%", args[2].toLowerCase(), "%Source%", args[3]);
                }, false);
            }
            case "import" -> {
                if(args.length == 1) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-import-use-error");
                    break;
                }
                GHoloImporter holoImporter = gHoloMain.getHoloImporterService().getHoloImporter(args[1]);
                if(holoImporter == null) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-import-exist-error", "%Type%", args[1]);
                    break;
                }
                boolean override = true;
                if(args.length > 2) override = Boolean.parseBoolean(args[2]);
                GHoloImporterResult importerResult = holoImporter.importHolos(gHoloMain, override);
                gHoloMain.getHoloService().unloadHolos(null);
                gHoloMain.getHoloService().loadHolos(null);
                if(!importerResult.hasSucceeded()) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-import-import-error", "%Type%", args[1]);
                    break;
                }
                gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-import", "%Type%", args[1], "%Count%", importerResult.getCount());
            }
            case "export" -> {
                if(args.length == 1) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-export-use-error");
                    break;
                }
                GHoloExporter holoExporter = gHoloMain.getHoloExporterService().getHoloExporter(args[1]);
                if(holoExporter == null) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-export-exist-error", "%Type%", args[1]);
                    break;
                }
                boolean override = true;
                if(args.length > 2) override = Boolean.parseBoolean(args[2]);
                GHoloExporterResult exporterResult = holoExporter.exportHolos(gHoloMain, override);
                if(!exporterResult.hasSucceeded()) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-export-export-error", "%Type%", args[1]);
                    break;
                }
                gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-export", "%Type%", args[1], "%Count%", exporterResult.getCount());
            }
            default -> {
                gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-use-error");
            }
        }

        return true;
    }

}