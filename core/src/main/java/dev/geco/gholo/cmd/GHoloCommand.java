package dev.geco.gholo.cmd;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.object.GHolo;
import dev.geco.gholo.object.GHoloData;
import dev.geco.gholo.object.GHoloRow;
import dev.geco.gholo.object.GHoloRowUpdateType;
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

    public static List<String> COMMAND_LIST = List.of("help", "list", "near", "create", "info", "remove", "rename", "relocate", "tphere", "tpto", "align", "addrow", "insertrow", "setrow", "removerow", "positionrow", "copyrows", "data", "setimage", "import");

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

        GHolo holo;
        switch(args[0].toLowerCase()) {
            case "help":
                gHoloMain.getMessageService().sendMessage(sender, "HoloHelpCommand.header");
                for(String helpRow : COMMAND_LIST) {
                    gHoloMain.getMessageService().sendMessage(sender, "HoloHelpCommand." + helpRow.toLowerCase());
                }
                gHoloMain.getMessageService().sendMessage(sender, "HoloHelpCommand.footer");
                break;
            case "list":
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
                        Location holoLocation = listHolo.getRawLocation();
                        BigDecimal x = BigDecimal.valueOf(holoLocation.getX()).setScale(2, RoundingMode.HALF_UP);
                        BigDecimal y = BigDecimal.valueOf(holoLocation.getY()).setScale(2, RoundingMode.HALF_UP);
                        BigDecimal z = BigDecimal.valueOf(holoLocation.getZ()).setScale(2, RoundingMode.HALF_UP);
                        gHoloMain.getMessageService().sendMessage(sender, "HoloListCommand.holo", "%Holo%", listHolo.getId(), "%X%", x.stripTrailingZeros().toPlainString(), "%Y%", y.stripTrailingZeros().toPlainString(), "%Z%", z.stripTrailingZeros().toPlainString(), "%World%", holoLocation.getWorld().getName());
                    }
                    gHoloMain.getMessageService().sendMessage(sender, "HoloListCommand.footer", "%Page%", page, "%MaxPage%", maxPage);
                } catch(NumberFormatException e) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-list-page-error", "%Page%", args[1]);
                    break;
                }
                break;
            case "near":
                if(!(sender instanceof Player player)) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-sender-error");
                    break;
                }
                List<GHolo> allHoloList = gHoloMain.getHoloService().getHolos();
                if(allHoloList.isEmpty()) {
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
                        Location holoLocation = nearHolo.getRawLocation();
                        BigDecimal x = BigDecimal.valueOf(holoLocation.getX()).setScale(2, RoundingMode.HALF_UP);
                        BigDecimal y = BigDecimal.valueOf(holoLocation.getY()).setScale(2, RoundingMode.HALF_UP);
                        BigDecimal z = BigDecimal.valueOf(holoLocation.getZ()).setScale(2, RoundingMode.HALF_UP);
                        gHoloMain.getMessageService().sendMessage(sender, "HoloNearCommand.holo", "%Holo%", nearHolo.getId(), "%X%", x.stripTrailingZeros().toPlainString(), "%Y%", y.stripTrailingZeros().toPlainString(), "%Z%", z.stripTrailingZeros().toPlainString(), "%World%", holoLocation.getWorld().getName());
                    }
                    gHoloMain.getMessageService().sendMessage(sender, "HoloNearCommand.footer", "%Range%", range);
                } catch(NumberFormatException e) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-near-range-error", "%Range%", args[1]);
                    break;
                }
                break;
            case "create":
                if(!(sender instanceof Player player)) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-sender-error");
                    break;
                }
                if(args.length == 1) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-create-use-error");
                    break;
                }
                holo = gHoloMain.getHoloService().getHolo(args[1]);
                if(holo != null) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-create-exist-error", "%Holo%", holo.getId());
                    break;
                }
                gHoloMain.getHoloService().createHolo(args[1], player.getLocation());
                gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-create", "%Holo%", args[1]);
                break;
            case "info":
                if(args.length == 1) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-info-use-error");
                    break;
                }
                holo = gHoloMain.getHoloService().getHolo(args[1]);
                if(holo == null) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-exist-error", "%Holo%", args[1].toLowerCase());
                    break;
                }
                gHoloMain.getMessageService().sendMessage(sender, "HoloInfoCommand.header", "%Holo%", holo.getId());
                Location holoInfoLocation = holo.getRawLocation();
                BigDecimal x = BigDecimal.valueOf(holoInfoLocation.getX()).setScale(2, RoundingMode.HALF_UP);
                BigDecimal y = BigDecimal.valueOf(holoInfoLocation.getY()).setScale(2, RoundingMode.HALF_UP);
                BigDecimal z = BigDecimal.valueOf(holoInfoLocation.getZ()).setScale(2, RoundingMode.HALF_UP);
                gHoloMain.getMessageService().sendMessage(sender, "HoloInfoCommand.location", "%X%", x.stripTrailingZeros().toPlainString(), "%Y%", y.stripTrailingZeros().toPlainString(), "%Z%", z.stripTrailingZeros().toPlainString(), "%World%", holoInfoLocation.getWorld().getName());
                int row = 1;
                for(GHoloRow holoRow : holo.getRows()) {
                    gHoloMain.getMessageService().sendMessage(sender, "HoloInfoCommand.row", "%Row%", row, "%Content%", holoRow.getContent());
                    row++;
                }
                gHoloMain.getMessageService().sendMessage(sender, "HoloInfoCommand.footer", "%Holo%", holo.getId());
                break;
            case "remove":
                if(args.length == 1) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-remove-use-error");
                    break;
                }
                holo = gHoloMain.getHoloService().getHolo(args[1]);
                if(holo == null) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-exist-error", "%Holo%", args[1].toLowerCase());
                    break;
                }
                gHoloMain.getHoloService().removeHolo(holo);
                gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-remove", "%Holo%", holo.getId());
                break;
            case "rename":
                if(args.length <= 2) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-rename-use-error");
                    break;
                }
                holo = gHoloMain.getHoloService().getHolo(args[1]);
                if(holo == null) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-exist-error", "%Holo%", args[1].toLowerCase());
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
                break;
            case "relocate":
                if(args.length <= 4) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-relocate-use-error");
                    break;
                }
                holo = gHoloMain.getHoloService().getHolo(args[1]);
                if(holo == null) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-exist-error", "%Holo%", args[1].toLowerCase());
                    break;
                }
                try {
                    Location location = holo.getLocation();
                    location.set(Double.parseDouble(args[2]), Double.parseDouble(args[3]), Double.parseDouble(args[4]));
                    gHoloMain.getHoloService().updateHoloLocation(holo, location);
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-relocate", "%Holo%", holo.getId());
                } catch(NumberFormatException e) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-relocate-location-error");
                }
                break;
            case "tphere":
                if(!(sender instanceof Player player)) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-sender-error");
                    break;
                }
                if(args.length == 1) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-tphere-use-error");
                    break;
                }
                holo = gHoloMain.getHoloService().getHolo(args[1]);
                if(holo == null) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-exist-error", "%Holo%", args[1].toLowerCase());
                    break;
                }
                gHoloMain.getHoloService().updateHoloLocation(holo, player.getLocation());
                gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-tphere", "%Holo%", holo.getId());
                break;
            case "tpto":
                if(!(sender instanceof Player player)) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-sender-error");
                    break;
                }
                if(args.length == 1) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-tpto-use-error");
                    break;
                }
                holo = gHoloMain.getHoloService().getHolo(args[1]);
                if(holo == null) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-exist-error", "%Holo%", args[1].toLowerCase());
                    break;
                }
                player.teleport(holo.getRawLocation(), PlayerTeleportEvent.TeleportCause.COMMAND);
                gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-tpto", "%Holo%", holo.getId());
                break;
            case "align":
                if(args.length <= 3) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-align-use-error");
                    break;
                }
                holo = gHoloMain.getHoloService().getHolo(args[1]);
                if(holo == null) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-exist-error", "%Holo%", args[1].toLowerCase());
                    break;
                }
                GHolo alignOnHolo = gHoloMain.getHoloService().getHolo(args[2]);
                if(alignOnHolo == null) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-exist-error", "%Holo%", args[2].toLowerCase());
                    break;
                }
                String axis = args[3].toLowerCase();
                Location holoLocation = holo.getLocation();
                Location alignOnHoloLocation = alignOnHolo.getRawLocation();
                String appliedAxis = "";
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
                break;
            case "addrow":
                if(args.length == 1) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-addrow-use-error");
                    break;
                }
                holo = gHoloMain.getHoloService().getHolo(args[1]);
                if(holo == null) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-exist-error", "%Holo%", args[1].toLowerCase());
                    break;
                }
                StringBuilder addIdStringBuilder = new StringBuilder();
                if(args.length > 2) {
                    for(int arg = 2; arg <= args.length - 1; arg++) addIdStringBuilder.append(args[arg]).append(" ");
                    addIdStringBuilder.deleteCharAt(addIdStringBuilder.length() - 1);
                }
                gHoloMain.getHoloService().createHoloRow(holo, addIdStringBuilder.toString());
                gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-addrow", "%Holo%", holo.getId(), "%Content%", addIdStringBuilder.toString());
                break;
            case "insertrow":
                if(args.length <= 2) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-insertrow-use-error");
                    break;
                }
                holo = gHoloMain.getHoloService().getHolo(args[1]);
                if(holo == null) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-exist-error", "%Holo%", args[1].toLowerCase());
                    break;
                }
                try {
                    GHoloRow holoRow = holo.getRow(Integer.parseInt(args[2]) - 1);
                    if(holoRow == null) {
                        gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-row-error", "%Row%", args[2]);
                        break;
                    }
                    StringBuilder insertIdStringBuilder = new StringBuilder();
                    if(args.length > 3) {
                        for(int arg = 3; arg <= args.length - 1; arg++) insertIdStringBuilder.append(args[arg]).append(" ");
                        insertIdStringBuilder.deleteCharAt(insertIdStringBuilder.length() - 1);
                    }
                    gHoloMain.getHoloService().insertHoloRow(holo, holoRow.getRowId(), insertIdStringBuilder.toString(), true);
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-insertrow", "%Holo%", holo.getId(), "%Row%", Integer.parseInt(args[2]), "%Content%", insertIdStringBuilder.toString());
                } catch(NumberFormatException e) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-row-error", "%Row%", args[2]);
                }
                break;
            case "setrow":
                if(args.length <= 2) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-setrow-use-error");
                    break;
                }
                holo = gHoloMain.getHoloService().getHolo(args[1]);
                if(holo == null) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-exist-error", "%Holo%", args[1].toLowerCase());
                    break;
                }
                try {
                    GHoloRow holoRow = holo.getRow(Integer.parseInt(args[2]) - 1);
                    if(holoRow == null) {
                        gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-row-error", "%Row%", args[2]);
                        break;
                    }
                    StringBuilder setIdStringBuilder = new StringBuilder();
                    if(args.length > 3) {
                        for(int arg = 3; arg <= args.length - 1; arg++) setIdStringBuilder.append(args[arg]).append(" ");
                        setIdStringBuilder.deleteCharAt(setIdStringBuilder.length() - 1);
                    }
                    gHoloMain.getHoloService().updateHoloRowContent(holoRow, setIdStringBuilder.toString());
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-setrow", "%Holo%", holo.getId(), "%Row%", Integer.parseInt(args[2]), "%Content%", setIdStringBuilder.toString());
                } catch(NumberFormatException e) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-row-error", "%Row%", args[2]);
                }
                break;
            case "removerow":
                if(args.length <= 2) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-removerow-use-error");
                    break;
                }
                holo = gHoloMain.getHoloService().getHolo(args[1]);
                if(holo == null) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-exist-error", "%Holo%", args[1].toLowerCase());
                    break;
                }
                try {
                    GHoloRow holoRow = holo.getRow(Integer.parseInt(args[2]) - 1);
                    if(holoRow == null) {
                        gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-row-error", "%Row%", args[2]);
                        break;
                    }
                    gHoloMain.getHoloService().removeHoloRow(holoRow, true);
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-removerow", "%Holo%", holo.getId(), "%Row%", Integer.parseInt(args[2]));
                } catch(NumberFormatException e) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-row-error", "%Row%", args[2]);
                }
                break;
            case "positionrow":
                if(args.length <= 4) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-positionrow-use-error");
                    break;
                }
                holo = gHoloMain.getHoloService().getHolo(args[1]);
                if(holo == null) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-exist-error", "%Holo%", args[1].toLowerCase());
                    break;
                }
                try {
                    GHoloRow holoRow = holo.getRow(Integer.parseInt(args[2]) - 1);
                    if(holoRow == null) {
                        gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-row-error", "%Row%", args[2]);
                        break;
                    }
                    try {
                        Location position = holoRow.getPosition();
                        switch(args[3].toLowerCase()) {
                            case "xoffset":
                                position.setX(Double.parseDouble(args[4]));
                                break;
                            case "yoffset":
                                position.setY(Double.parseDouble(args[4]));
                                break;
                            case "zoffset":
                                position.setZ(Double.parseDouble(args[4]));
                                break;
                            case "yaw":
                                position.setYaw(Float.parseFloat(args[4]));
                                break;
                            case "pitch":
                                position.setPitch(Float.parseFloat(args[4]));
                                break;
                            default:
                                gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-positionrow-use-error");
                                return true;
                        }
                        gHoloMain.getHoloService().updateHoloRowPosition(holoRow, position);
                        gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-positionrow");
                    } catch(NumberFormatException e) {
                        gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-positionrow-value-error", "%Value%", args[4]);
                    }
                } catch(NumberFormatException e) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-row-error", "%Row%", args[2]);
                }
                break;
            case "copyrows":
                if(args.length <= 2) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-copyrows-use-error");
                    break;
                }
                holo = gHoloMain.getHoloService().getHolo(args[1]);
                if(holo == null) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-exist-error", "%Holo%", args[1].toLowerCase());
                    break;
                }
                GHolo copyToHolo = gHoloMain.getHoloService().getHolo(args[2]);
                if(copyToHolo == null) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-exist-error", "%Holo%", args[1].toLowerCase());
                    break;
                }
                gHoloMain.getHoloService().copyHoloRows(holo, copyToHolo);
                gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-copyrows", "%Holo%", holo.getId(), "%CopyToHolo%", copyToHolo.getId());
                break;
            case "data":
                if(args.length <= 4) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-data-use-error");
                    break;
                }
                holo = gHoloMain.getHoloService().getHolo(args[1]);
                if(holo == null) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-exist-error", "%Holo%", args[1].toLowerCase());
                    break;
                }
                GHoloData data = null;
                int arg = 3;
                GHoloRow holoRow = null;
                switch (args[2].toLowerCase()) {
                    case "default":
                        data = holo.getDefaultData();
                        break;
                    case "row":
                        try {
                            holoRow = holo.getRow(Integer.parseInt(args[3]) - 1);
                            if(holoRow == null) {
                                gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-row-error", "%Row%", args[3]);
                                return true;
                            }
                            data = holoRow.getData();
                            arg = 4;
                        } catch(NumberFormatException e) {
                            gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-row-error", "%Row%", args[3]);
                            return true;
                        }
                        break;
                }
                if(data == null) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-data-use-error");
                    break;
                }
                GHoloRowUpdateType updateType = null;
                String option = args[arg].toLowerCase();
                switch (option) {
                    case "range":
                        try {
                            if(args[arg + 1].equalsIgnoreCase("*")) data.setRange(GHoloData.DEFAULT_RANGE);
                            else data.setRange(Double.parseDouble(args[arg + 1]));
                            updateType = GHoloRowUpdateType.RANGE;
                        } catch(NumberFormatException e) {
                            gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-data-value-error", "%Data%", option, "%Value%", args[arg + 1]);
                            return true;
                        }
                        break;
                    case "background_color":
                        if(args[arg + 1].equalsIgnoreCase("*")) data.setBackgroundColor(GHoloData.DEFAULT_BACKGROUND_COLOR);
                        else {
                            String backgroundColor = args[arg + 1];
                            if(!backgroundColor.matches("^[0-9A-Fa-f]{6}|[0-9A-Fa-f]{8}$")) {
                                gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-data-value-error", "%Data%", option, "%Value%", backgroundColor);
                                return true;
                            } else data.setBackgroundColor(backgroundColor);
                        }
                        updateType = GHoloRowUpdateType.BACKGROUND_COLOR;
                        break;
                    case "text_opacity":
                        try {
                            if(args[arg + 1].equalsIgnoreCase("*")) data.setTextOpacity(GHoloData.DEFAULT_TEXT_OPACITY);
                            else data.setTextOpacity(Byte.parseByte(args[arg + 1]));
                            updateType = GHoloRowUpdateType.TEXT_OPACITY;
                        } catch(NumberFormatException e) {
                            gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-data-value-error", "%Data%", option, "%Value%", args[arg + 1]);
                            return true;
                        }
                        break;
                    case "text_shadow":
                        try {
                            if(args[arg + 1].equalsIgnoreCase("*")) data.setTextShadow(GHoloData.DEFAULT_HAS_TEXT_SHADOW);
                            else data.setTextShadow(Boolean.parseBoolean(args[arg + 1]));
                            updateType = GHoloRowUpdateType.TEXT_SHADOW;
                        } catch(NumberFormatException e) {
                            gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-data-value-error", "%Data%", option, "%Value%", args[arg + 1]);
                            return true;
                        }
                        break;
                    case "text_alignment":
                        try {
                            if(args[arg + 1].equalsIgnoreCase("*")) data.setTextAlignment(GHoloData.DEFAULT_TEXT_ALIGNMENT);
                            else data.setTextAlignment(TextDisplay.TextAlignment.valueOf(args[arg + 1].toUpperCase()).name().toLowerCase());
                            updateType = GHoloRowUpdateType.TEXT_ALIGNMENT;
                        } catch(IllegalArgumentException e) {
                            gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-data-value-error", "%Data%", option, "%Value%", args[arg + 1].toUpperCase());
                            return true;
                        }
                        break;
                    case "billboard":
                        try {
                            if(args[arg + 1].equalsIgnoreCase("*")) data.setBillboard(GHoloData.DEFAULT_BILLBOARD);
                            else data.setBillboard(Display.Billboard.valueOf(args[arg + 1].toUpperCase()).name().toLowerCase());
                            updateType = GHoloRowUpdateType.BILLBOARD;
                        } catch(IllegalArgumentException e) {
                            gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-data-value-error", "%Data%", option, "%Value%", args[arg + 1].toUpperCase());
                            return true;
                        }
                        break;
                    case "see_through":
                        try {
                            if(args[arg + 1].equalsIgnoreCase("*")) data.setSeeThrough(GHoloData.DEFAULT_CAN_SEE_THROUGH);
                            else data.setSeeThrough(Boolean.parseBoolean(args[arg + 1]));
                            updateType = GHoloRowUpdateType.SEE_THROUGH;
                        } catch(NumberFormatException e) {
                            gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-data-value-error", "%Data%", option, "%Value%", args[arg + 1]);
                            return true;
                        }
                        break;
                    case "scale":
                        try {
                            if(args[arg + 1].equalsIgnoreCase("*")) data.setScale(GHoloData.DEFAULT_SCALE);
                            else data.setScale(new org.joml.Vector3f(Float.parseFloat(args[arg + 1]), Float.parseFloat(args[arg + 1]), Float.parseFloat(args[arg + 1])));
                            updateType = GHoloRowUpdateType.SCALE;
                        } catch(NumberFormatException e) {
                            gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-data-value-error", "%Data%", option, "%Value%", args[arg + 1]);
                            return true;
                        }
                        break;
                    case "brightness":
                        try {
                            if(args[arg + 1].equalsIgnoreCase("*")) data.setBrightness(GHoloData.DEFAULT_TEXT_OPACITY);
                            else data.setBrightness(Byte.parseByte(args[arg + 1]));
                            updateType = GHoloRowUpdateType.BRIGHTNESS;
                        } catch(NumberFormatException e) {
                            gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-data-value-error", "%Data%", option, "%Value%", args[arg + 1]);
                            return true;
                        }
                        break;
                    case "permission":
                        if(args[arg + 1].equalsIgnoreCase("*")) data.setPermission(GHoloData.DEFAULT_PERMISSION);
                        else data.setPermission(args[arg + 1]);
                        updateType = GHoloRowUpdateType.PERMISSION;
                        break;
                }
                if(holoRow == null) {
                    gHoloMain.getHoloService().updateHoloData(holo, data);
                    for(GHoloRow updateHoloRow : holo.getRows()) updateHoloRow.getHoloRowEntity().publishUpdate(updateType);
                } else {
                    gHoloMain.getHoloService().updateHoloRowData(holoRow, data);
                    holoRow.getHoloRowEntity().publishUpdate(updateType);
                }
                gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-data", "%Data%", option, "%Value%", args[arg + 1].toLowerCase());
                break;
            case "setimage":
                if(args.length <= 3) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-setimage-use-error");
                    break;
                }
                holo = gHoloMain.getHoloService().getHolo(args[1]);
                if(holo == null) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-exist-error", "%Holo%", args[1].toLowerCase());
                    break;
                }
                BufferedImage bufferedImage = null;
                switch (args[2].toLowerCase()) {
                    case "file":
                        File imageFile = new File(ImageUtil.IMAGE_FOLDER, args[3]);
                        if(!imageFile.exists()) break;
                        bufferedImage = ImageUtil.getBufferedImage(imageFile);
                        break;
                    case "url":
                        bufferedImage = ImageUtil.getBufferedImage(args[3]);
                        break;
                    case "avatar":
                    case "helm":
                        OfflinePlayer target;
                        try {
                            target = Bukkit.getOfflinePlayer(UUID.fromString(args[3]));
                        } catch(Throwable e) {
                            target = Bukkit.getOfflinePlayer(args[3]);
                        }
                        bufferedImage = ImageUtil.getBufferedImage(target, args[2].equalsIgnoreCase("helm"));
                        break;
                    default:
                        gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-setimage-use-error");
                        return true;
                }
                if(bufferedImage == null) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-setimage-image-error", "%Type%", args[2].toLowerCase(), "%Source%", args[3]);
                    break;
                }
                List<String> rows;
                if(args.length > 4) {
                    try {
                        if(args[4].contains(":")) {
                            String[] sizes = args[4].split(":");
                            rows = new ImageUtil(bufferedImage, Integer.parseInt(sizes[0]), Integer.parseInt(sizes[1])).getLines();
                        } else rows = new ImageUtil(bufferedImage, Integer.parseInt(args[4])).getLines();
                    } catch(Throwable e) {
                        gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-setimage-size-error");
                        return true;
                    }
                }  else rows = new ImageUtil(bufferedImage).getLines();
                gHoloMain.getHoloService().setHoloRows(holo, rows);
                gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-setimage", "%Holo%", holo.getId(), "%Type%", args[2].toLowerCase(), "%Source%", args[3]);
                break;
            case "import":
                if(args.length == 1) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-import-use-error");
                    break;
                }
                String plugin = args[1].toLowerCase();
                if(!gHoloMain.getHoloImportService().AVAILABLE_PLUGIN_IMPORTS.contains(plugin)) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-import-exist-error", "%Plugin%", args[1]);
                    break;
                }
                int imported = gHoloMain.getHoloImportService().importFromPlugin(plugin);
                if(imported < 0) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-import-import-error", "%Plugin%", plugin);
                    break;
                }
                gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-import", "%Plugin%", plugin, "%Imported%", imported);
                break;
            default:
                gHoloMain.getMessageService().sendMessage(sender, "Messages.command-gholo-use-error");
        }

        return true;
    }

}