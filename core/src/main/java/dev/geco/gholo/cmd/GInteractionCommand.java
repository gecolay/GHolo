package dev.geco.gholo.cmd;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.object.interaction.GInteraction;
import dev.geco.gholo.object.interaction.GInteractionAction;
import dev.geco.gholo.object.interaction.GInteractionData;
import dev.geco.gholo.object.interaction.GInteractionSize;
import dev.geco.gholo.object.interaction.GInteractionUpdateType;
import dev.geco.gholo.object.interaction.action.GInteractionActionType;
import dev.geco.gholo.object.interaction.exporter.GInteractionExporter;
import dev.geco.gholo.object.interaction.exporter.GInteractionExporterResult;
import dev.geco.gholo.object.interaction.importer.GInteractionImporter;
import dev.geco.gholo.object.interaction.importer.GInteractionImporterResult;
import dev.geco.gholo.object.location.SimpleLocation;
import dev.geco.gholo.object.location.SimpleRotation;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class GInteractionCommand implements CommandExecutor {

    public static List<String> COMMAND_LIST = List.of("help", "list", "near", "create", "info", "remove", "rename", "move", "tphere", "tp", "align", "size", "addaction", "insertaction", "setaction", "removeaction", "copy", "option", "rotate", "import", "export");

    private final GHoloMain gHoloMain;

    public GInteractionCommand(GHoloMain gHoloMain) {
        this.gHoloMain = gHoloMain;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if(!gHoloMain.getPermissionService().hasPermission(sender, "Interaction")) {
            gHoloMain.getMessageService().sendMessage(sender, "Messages.command-permission-error");
            return true;
        }

        if(args.length == 0) {
            gHoloMain.getMessageService().sendMessage(sender, "Messages.command-ginteraction-use-error");
            return true;
        }

        GInteraction interaction;
        switch(args[0].toLowerCase()) {
            case "help":
                gHoloMain.getMessageService().sendMessage(sender, "InteractionHelpCommand.header");
                for(String helpRow : COMMAND_LIST) {
                    gHoloMain.getMessageService().sendMessage(sender, "InteractionHelpCommand." + helpRow.toLowerCase());
                }
                gHoloMain.getMessageService().sendMessage(sender, "InteractionHelpCommand.footer");
                break;
            case "list":
                List<GInteraction> interactionList = gHoloMain.getInteractionService().getInteractions();
                if(interactionList.isEmpty()) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-ginteraction-none");
                    break;
                }
                try {
                    int pageSize = gHoloMain.getConfigService().LIST_PAGE_SIZE;
                    int page = args.length > 1 ? Integer.parseInt(args[1]) : 1;
                    int totalInteractionCount = interactionList.size();
                    int maxPage = (int) Math.ceil((double) totalInteractionCount / pageSize);
                    page = Math.max(Math.min(page, maxPage), 1);
                    gHoloMain.getMessageService().sendMessage(sender, "InteractionListCommand.header", "%Page%", page, "%MaxPage%", maxPage);
                    int startIndex = (page - 1) * pageSize;
                    int endIndex = Math.min(startIndex + pageSize, totalInteractionCount);
                    for(int i = startIndex; i < endIndex; i++) {
                        GInteraction listInteraction = interactionList.get(i);
                        SimpleLocation interactionLocation = listInteraction.getRawLocation();
                        BigDecimal x = BigDecimal.valueOf(interactionLocation.getX()).setScale(2, RoundingMode.HALF_UP);
                        BigDecimal y = BigDecimal.valueOf(interactionLocation.getY()).setScale(2, RoundingMode.HALF_UP);
                        BigDecimal z = BigDecimal.valueOf(interactionLocation.getZ()).setScale(2, RoundingMode.HALF_UP);
                        gHoloMain.getMessageService().sendMessage(sender, "InteractionListCommand.interaction", "%Interaction%", listInteraction.getId(), "%X%", x.stripTrailingZeros().toPlainString(), "%Y%", y.stripTrailingZeros().toPlainString(), "%Z%", z.stripTrailingZeros().toPlainString(), "%World%", interactionLocation.getWorld().getName());
                    }
                    gHoloMain.getMessageService().sendMessage(sender, "InteractionListCommand.footer", "%Page%", page, "%MaxPage%", maxPage);
                } catch(NumberFormatException e) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-ginteraction-list-page-error", "%Page%", args[1]);
                    break;
                }
                break;
            case "near":
                if(!(sender instanceof Player player)) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-sender-error");
                    break;
                }
                if(gHoloMain.getInteractionService().getInteractions().isEmpty()) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-ginteraction-none");
                    break;
                }
                try {
                    double range = args.length > 1 ? Double.parseDouble(args[1]) : gHoloMain.getConfigService().NEAR_RANGE;
                    List<GInteraction> nearInteractionList = gHoloMain.getInteractionService().getNearInteractions(player.getLocation(), range);
                    if(nearInteractionList.isEmpty()) {
                        gHoloMain.getMessageService().sendMessage(sender, "Messages.command-ginteraction-near-none");
                        break;
                    }
                    gHoloMain.getMessageService().sendMessage(sender, "InteractionNearCommand.header", "%Range%", range);
                    for(GInteraction nearInteraction : nearInteractionList) {
                        SimpleLocation interactionLocation = nearInteraction.getRawLocation();
                        BigDecimal x = BigDecimal.valueOf(interactionLocation.getX()).setScale(2, RoundingMode.HALF_UP);
                        BigDecimal y = BigDecimal.valueOf(interactionLocation.getY()).setScale(2, RoundingMode.HALF_UP);
                        BigDecimal z = BigDecimal.valueOf(interactionLocation.getZ()).setScale(2, RoundingMode.HALF_UP);
                        gHoloMain.getMessageService().sendMessage(sender, "InteractionNearCommand.interaction", "%Interaction%", nearInteraction.getId(), "%X%", x.stripTrailingZeros().toPlainString(), "%Y%", y.stripTrailingZeros().toPlainString(), "%Z%", z.stripTrailingZeros().toPlainString(), "%World%", interactionLocation.getWorld().getName());
                    }
                    gHoloMain.getMessageService().sendMessage(sender, "InteractionNearCommand.footer", "%Range%", range);
                } catch(NumberFormatException e) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-ginteraction-near-range-error", "%Range%", args[1]);
                    break;
                }
                break;
            case "create":
                if(!(sender instanceof Player player)) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-sender-error");
                    break;
                }
                if(args.length == 1) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-ginteraction-create-use-error");
                    break;
                }
                interaction = gHoloMain.getInteractionService().getInteraction(args[1]);
                if(interaction != null) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-ginteraction-create-exist-error", "%Interaction%", interaction.getId());
                    break;
                }
                gHoloMain.getInteractionService().createInteraction(args[1], SimpleLocation.fromBukkitLocation(player.getLocation()));
                gHoloMain.getMessageService().sendMessage(sender, "Messages.command-ginteraction-create", "%Interaction%", args[1]);
                break;
            case "info":
                if(args.length == 1) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-ginteraction-info-use-error");
                    break;
                }
                interaction = gHoloMain.getInteractionService().getInteraction(args[1]);
                if(interaction == null) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-ginteraction-exist-error", "%Interaction%", args[1]);
                    break;
                }
                gHoloMain.getMessageService().sendMessage(sender, "InteractionInfoCommand.header", "%Interaction%", interaction.getId());
                SimpleLocation interactionInfoLocation = interaction.getRawLocation();
                BigDecimal x = BigDecimal.valueOf(interactionInfoLocation.getX()).setScale(2, RoundingMode.HALF_UP);
                BigDecimal y = BigDecimal.valueOf(interactionInfoLocation.getY()).setScale(2, RoundingMode.HALF_UP);
                BigDecimal z = BigDecimal.valueOf(interactionInfoLocation.getZ()).setScale(2, RoundingMode.HALF_UP);
                gHoloMain.getMessageService().sendMessage(sender, "InteractionInfoCommand.location", "%X%", x.stripTrailingZeros().toPlainString(), "%Y%", y.stripTrailingZeros().toPlainString(), "%Z%", z.stripTrailingZeros().toPlainString(), "%World%", interactionInfoLocation.getWorld().getName());
                int position = 1;
                for(GInteractionAction interactionAction : interaction.getActions()) {
                    gHoloMain.getMessageService().sendMessage(sender, "InteractionInfoCommand.action", "%Position%", position, "%Type%", interactionAction.getInteractionActionType().getType(), "%Parameter%", interactionAction.getParameter());
                    position++;
                }
                gHoloMain.getMessageService().sendMessage(sender, "InteractionInfoCommand.footer", "%Interaction%", interaction.getId());
                break;
            case "remove":
                if(args.length == 1) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-ginteraction-remove-use-error");
                    break;
                }
                interaction = gHoloMain.getInteractionService().getInteraction(args[1]);
                if(interaction == null) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-ginteraction-exist-error", "%Interaction%", args[1]);
                    break;
                }
                gHoloMain.getInteractionService().removeInteraction(interaction);
                gHoloMain.getMessageService().sendMessage(sender, "Messages.command-ginteraction-remove", "%Interaction%", interaction.getId());
                break;
            case "rename":
                if(args.length <= 2) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-ginteraction-rename-use-error");
                    break;
                }
                interaction = gHoloMain.getInteractionService().getInteraction(args[1]);
                if(interaction == null) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-ginteraction-exist-error", "%Interaction%", args[1]);
                    break;
                }
                GInteraction newIdInteraction = gHoloMain.getInteractionService().getInteraction(args[2]);
                if(newIdInteraction != null) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-ginteraction-rename-exist-error", "%Interaction%", newIdInteraction.getId());
                    break;
                }
                String oldId = interaction.getId();
                gHoloMain.getInteractionService().updateInteractionId(interaction, args[2]);
                gHoloMain.getMessageService().sendMessage(sender, "Messages.command-ginteraction-rename", "%Interaction%", interaction.getId(), "%OldInteraction%", oldId);
                break;
            case "move":
                if(args.length <= 4) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-ginteraction-move-use-error");
                    break;
                }
                interaction = gHoloMain.getInteractionService().getInteraction(args[1]);
                if(interaction == null) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-ginteraction-exist-error", "%Interaction%", args[1]);
                    break;
                }
                try {
                    SimpleLocation location = interaction.getLocation();
                    location.set(Double.parseDouble(args[2]), Double.parseDouble(args[3]), Double.parseDouble(args[4]));
                    gHoloMain.getInteractionService().updateInteractionLocation(interaction, location);
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-ginteraction-move", "%Interaction%", interaction.getId());
                } catch(NumberFormatException e) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-ginteraction-move-location-error");
                }
                break;
            case "tphere":
                if(!(sender instanceof Player player)) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-sender-error");
                    break;
                }
                if(args.length == 1) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-ginteraction-tphere-use-error");
                    break;
                }
                interaction = gHoloMain.getInteractionService().getInteraction(args[1]);
                if(interaction == null) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-ginteraction-exist-error", "%Interaction%", args[1]);
                    break;
                }
                gHoloMain.getInteractionService().updateInteractionLocation(interaction, SimpleLocation.fromBukkitLocation(player.getLocation()));
                gHoloMain.getMessageService().sendMessage(sender, "Messages.command-ginteraction-tphere", "%Interaction%", interaction.getId());
                break;
            case "tp":
                if(!(sender instanceof Player player)) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-sender-error");
                    break;
                }
                if(args.length == 1) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-ginteraction-tp-use-error");
                    break;
                }
                interaction = gHoloMain.getInteractionService().getInteraction(args[1]);
                if(interaction == null) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-ginteraction-exist-error", "%Interaction%", args[1]);
                    break;
                }
                Location location = interaction.getLocation();
                location.setYaw(player.getLocation().getYaw());
                location.setPitch(player.getLocation().getPitch());
                player.teleport(location, PlayerTeleportEvent.TeleportCause.COMMAND);
                gHoloMain.getMessageService().sendMessage(sender, "Messages.command-ginteraction-tp", "%Interaction%", interaction.getId());
                break;
            case "align":
                if(args.length <= 3) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-ginteraction-align-use-error");
                    break;
                }
                interaction = gHoloMain.getInteractionService().getInteraction(args[1]);
                if(interaction == null) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-ginteraction-exist-error", "%Interaction%", args[1]);
                    break;
                }
                GInteraction alignOnInteraction = gHoloMain.getInteractionService().getInteraction(args[2]);
                if(alignOnInteraction == null) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-ginteraction-exist-error", "%Interaction%", args[2].toLowerCase());
                    break;
                }
                SimpleLocation interactionLocation = interaction.getLocation();
                SimpleLocation alignOnInteractionLocation = alignOnInteraction.getRawLocation();
                String appliedAxis = "";
                String axis = args[3].toLowerCase();
                if(axis.contains("x")) {
                    interactionLocation.setX(alignOnInteractionLocation.getX());
                    appliedAxis += "x";
                }
                if(axis.contains("y")) {
                    interactionLocation.setY(alignOnInteractionLocation.getY());
                    appliedAxis += "y";
                }
                if(axis.contains("z")) {
                    interactionLocation.setZ(alignOnInteractionLocation.getZ());
                    appliedAxis += "z";
                }
                if(appliedAxis.isEmpty()) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-ginteraction-align-axis-error", "%Axis%", axis);
                    break;
                }
                gHoloMain.getInteractionService().updateInteractionLocation(interaction, interactionLocation);
                gHoloMain.getMessageService().sendMessage(sender, "Messages.command-ginteraction-align", "%Interaction%", interaction.getId(), "%Axis%", appliedAxis, "%AlignOnInteraction%", alignOnInteraction.getId());
                break;
            case "size":
                if(args.length <= 3) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-ginteraction-size-use-error");
                    break;
                }
                interaction = gHoloMain.getInteractionService().getInteraction(args[1]);
                if(interaction == null) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-ginteraction-exist-error", "%Interaction%", args[1]);
                    break;
                }
                String sizeType = args[2].toLowerCase();
                GInteractionSize size = interaction.getSize();
                switch (sizeType) {
                    case "width":
                        try {
                            if(args[3].equalsIgnoreCase("*")) size.setWidth(1f);
                            else size.setWidth(Float.parseFloat(args[3]));
                        } catch(NumberFormatException e) {
                            gHoloMain.getMessageService().sendMessage(sender, "Messages.command-ginteraction-size-value-error", "%Size%", args[2], "%Value%", args[3]);
                            return true;
                        }
                        break;
                    case "height":
                        try {
                            if(args[3].equalsIgnoreCase("*")) size.setHeight(1f);
                            else size.setHeight(Float.parseFloat(args[3]));
                        } catch(NumberFormatException e) {
                            gHoloMain.getMessageService().sendMessage(sender, "Messages.command-ginteraction-size-value-error", "%Size%", args[2], "%Value%", args[3]);
                            return true;
                        }
                        break;
                    default:
                        gHoloMain.getMessageService().sendMessage(sender, "Messages.command-ginteraction-size-use-error");
                        return true;
                }
                gHoloMain.getInteractionService().updateInteractionSize(interaction, size);
                gHoloMain.getMessageService().sendMessage(sender, "Messages.command-ginteraction-size", "%Interaction%", interaction.getId(), "%Size%", args[2], "%Value%", args[3]);
            case "addaction":
                if(args.length <= 3) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-ginteraction-addaction-use-error");
                    break;
                }
                interaction = gHoloMain.getInteractionService().getInteraction(args[1]);
                if(interaction == null) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-ginteraction-exist-error", "%Interaction%", args[1]);
                    break;
                }
                String addActionType = args[2].toLowerCase();
                GInteractionActionType addInteractionActionType = gHoloMain.getInteractionActionService().getInteractionAction(addActionType);
                if(addInteractionActionType == null) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-ginteraction-action-type-error", "%Type%", args[2]);
                    break;
                }
                StringBuilder addActionParameter = new StringBuilder();
                for(int arg = 3; arg <= args.length - 1; arg++) addActionParameter.append(args[arg]).append(" ");
                addActionParameter.deleteCharAt(addActionParameter.length() - 1);
                if(!addInteractionActionType.validateParameter(addActionParameter.toString())) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-ginteraction-action-parameter-error", "%Type%", args[2], "%Parameter%", addActionParameter.toString());
                    break;
                }
                gHoloMain.getInteractionService().addInteractionAction(interaction, addInteractionActionType, addActionParameter.toString());
                gHoloMain.getMessageService().sendMessage(sender, "Messages.command-ginteraction-addaction", "%Interaction%", interaction.getId(), "%Type%", addActionType.toLowerCase(), "%Parameter%", addActionParameter.toString());
                break;
            case "insertaction":
                if(args.length <= 4) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-ginteraction-insertaction-use-error");
                    break;
                }
                interaction = gHoloMain.getInteractionService().getInteraction(args[1]);
                if(interaction == null) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-ginteraction-exist-error", "%Interaction%", args[1]);
                    break;
                }
                try {
                    GInteractionAction interactionAction = interaction.getAction(Integer.parseInt(args[2]) - 1);
                    if(interactionAction == null) {
                        gHoloMain.getMessageService().sendMessage(sender, "Messages.command-ginteraction-action-error", "%Position%", args[2]);
                        break;
                    }
                    String insertActionType = args[3].toLowerCase();
                    GInteractionActionType insertInteractionActionType = gHoloMain.getInteractionActionService().getInteractionAction(insertActionType);
                    if(insertInteractionActionType == null) {
                        gHoloMain.getMessageService().sendMessage(sender, "Messages.command-ginteraction-action-type-error", "%Type%", args[2]);
                        break;
                    }
                    StringBuilder insertActionParameter = new StringBuilder();
                    for(int arg = 4; arg <= args.length - 1; arg++) insertActionParameter.append(args[arg]).append(" ");
                    insertActionParameter.deleteCharAt(insertActionParameter.length() - 1);
                    if(!insertInteractionActionType.validateParameter(insertActionParameter.toString())) {
                        gHoloMain.getMessageService().sendMessage(sender, "Messages.command-ginteraction-action-parameter-error", "%Type%", args[3], "%Parameter%", insertActionParameter.toString());
                        break;
                    }
                    gHoloMain.getInteractionService().insertInteractionAction(interaction, interactionAction.getPosition(), insertInteractionActionType, insertActionParameter.toString());
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-ginteraction-insertaction", "%Interaction%", interaction.getId(), "%Position%", Integer.parseInt(args[2]), "%Type%", insertActionType.toLowerCase(), "%Parameter%", insertActionParameter.toString());
                } catch(NumberFormatException e) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-ginteraction-action-error", "%Position%", args[2]);
                }
                break;
            case "setaction":
                if(args.length <= 4) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-ginteraction-setaction-use-error");
                    break;
                }
                interaction = gHoloMain.getInteractionService().getInteraction(args[1]);
                if(interaction == null) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-ginteraction-exist-error", "%Interaction%", args[1]);
                    break;
                }
                try {
                    GInteractionAction interactionAction = interaction.getAction(Integer.parseInt(args[2]) - 1);
                    if(interactionAction == null) {
                        gHoloMain.getMessageService().sendMessage(sender, "Messages.command-ginteraction-action-error", "%Position%", args[2]);
                        break;
                    }
                    String setActionType = args[3].toLowerCase();
                    GInteractionActionType setInteractionActionType = gHoloMain.getInteractionActionService().getInteractionAction(setActionType);
                    if(setInteractionActionType == null) {
                        gHoloMain.getMessageService().sendMessage(sender, "Messages.command-ginteraction-action-type-error", "%Type%", args[2]);
                        break;
                    }
                    StringBuilder setActionParameter = new StringBuilder();
                    for(int arg = 4; arg <= args.length - 1; arg++) setActionParameter.append(args[arg]).append(" ");
                    setActionParameter.deleteCharAt(setActionParameter.length() - 1);
                    if(!setInteractionActionType.validateParameter(setActionParameter.toString())) {
                        gHoloMain.getMessageService().sendMessage(sender, "Messages.command-ginteraction-action-parameter-error", "%Type%", args[3], "%Parameter%", setActionParameter.toString());
                        break;
                    }
                    gHoloMain.getInteractionService().updateInteractionAction(interactionAction, setInteractionActionType, setActionParameter.toString());
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-ginteraction-setaction", "%Interaction%", interaction.getId(), "%Position%", Integer.parseInt(args[2]), "%Type%", setActionType.toLowerCase(), "%Parameter%", setActionParameter.toString());
                } catch(NumberFormatException e) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-ginteraction-action-error", "%Position%", args[2]);
                }
                break;
            case "removeaction":
                if(args.length <= 2) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-ginteraction-removeaction-use-error");
                    break;
                }
                interaction = gHoloMain.getInteractionService().getInteraction(args[1]);
                if(interaction == null) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-ginteraction-exist-error", "%Interaction%", args[1]);
                    break;
                }
                try {
                    GInteractionAction interactionAction = interaction.getAction(Integer.parseInt(args[2]) - 1);
                    if(interactionAction == null) {
                        gHoloMain.getMessageService().sendMessage(sender, "Messages.command-ginteraction-action-error", "%Position%", args[2]);
                        break;
                    }
                    gHoloMain.getInteractionService().removeInteractionAction(interactionAction);
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-ginteraction-removeaction", "%Interaction%", interaction.getId(), "%Position%", Integer.parseInt(args[2]));
                } catch(NumberFormatException e) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-ginteraction-action-error", "%Position%", args[2]);
                }
                break;
            case "copy":
                if(args.length <= 2) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-ginteraction-copy-use-error");
                    break;
                }
                interaction = gHoloMain.getInteractionService().getInteraction(args[1]);
                if(interaction == null) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-ginteraction-exist-error", "%Interaction%", args[1]);
                    break;
                }
                if(gHoloMain.getInteractionService().getInteraction(args[2]) != null) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-ginteraction-copy-exist-error", "%Interaction%", args[2].toLowerCase());
                    break;
                }
                gHoloMain.getInteractionService().copyInteraction(interaction, args[2]);
                gHoloMain.getMessageService().sendMessage(sender, "Messages.command-ginteraction-copy", "%Interaction%", interaction.getId(), "%NewInteraction%", args[2]);
                break;
            case "option":
                if(args.length <= 3) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-ginteraction-option-use-error");
                    break;
                }
                interaction = gHoloMain.getInteractionService().getInteraction(args[1]);
                if(interaction == null) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-ginteraction-exist-error", "%Interaction%", args[1]);
                    break;
                }
                GInteractionData data = interaction.getData();
                int optionArg = 2;
                GInteractionUpdateType optionUpdateType = null;
                switch(args[optionArg].toLowerCase()) {
                    case "permission":
                        if(args[optionArg + 1].equalsIgnoreCase("*")) data.setPermission(GInteractionData.DEFAULT_PERMISSION);
                        else data.setPermission(args[optionArg + 1]);
                        optionUpdateType = GInteractionUpdateType.PERMISSION;
                        break;
                    default:
                        gHoloMain.getMessageService().sendMessage(sender, "Messages.command-ginteraction-option-use-error");
                        return true;
                }
                gHoloMain.getInteractionService().updateInteractionData(interaction, data);
                interaction.getInteractionEntity().publishUpdate(optionUpdateType);
                gHoloMain.getMessageService().sendMessage(sender, "Messages.command-ginteraction-option", "%Option%", args[optionArg], "%Value%", args[optionArg + 1]);
                break;
            case "rotate":
                if(args.length <= 3) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-ginteraction-rotate-use-error");
                    break;
                }
                interaction = gHoloMain.getInteractionService().getInteraction(args[1]);
                if(interaction == null) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-ginteraction-exist-error", "%Interaction%", args[1]);
                    break;
                }
                SimpleRotation rotation = interaction.getRotation();
                switch(args[2].toLowerCase()) {
                    case "yaw":
                        try {
                            if(args[3].equalsIgnoreCase("*")) rotation.setYaw(null);
                            else rotation.setYaw(Float.parseFloat(args[3]));
                        } catch(NumberFormatException e) {
                            gHoloMain.getMessageService().sendMessage(sender, "Messages.command-ginteraction-rotate-value-error", "%Rotation%", args[2], "%Value%", args[3]);
                            return true;
                        }
                        break;
                    case "pitch":
                        try {
                            if(args[3].equalsIgnoreCase("*")) rotation.setPitch(null);
                            else rotation.setPitch(Float.parseFloat(args[3]));
                        } catch(NumberFormatException e) {
                            gHoloMain.getMessageService().sendMessage(sender, "Messages.command-ginteraction-rotate-value-error", "%Rotation%", args[2], "%Value%", args[3]);
                            return true;
                        }
                        break;
                    default:
                        gHoloMain.getMessageService().sendMessage(sender, "Messages.command-ginteraction-rotate-use-error");
                        return true;
                }
                gHoloMain.getInteractionService().updateInteractionRotation(interaction, rotation);
                gHoloMain.getMessageService().sendMessage(sender, "Messages.command-ginteraction-rotate", "%Rotation%", args[2], "%Value%", args[3]);
                break;
            case "import":
                if(args.length == 1) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-ginteraction-import-use-error");
                    break;
                }
                GInteractionImporter interactionImporter = gHoloMain.getInteractionImporterService().getInteractionImporter(args[1]);
                if(interactionImporter == null) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-ginteraction-import-exist-error", "%Type%", args[1]);
                    break;
                }
                GInteractionImporterResult importerResult = interactionImporter.importInteractions(gHoloMain, true);
                gHoloMain.getInteractionService().unloadInteractions();
                gHoloMain.getInteractionService().loadInteractions();
                if(!importerResult.hasSucceeded()) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-ginteraction-import-import-error", "%Type%", args[1]);
                    break;
                }
                gHoloMain.getMessageService().sendMessage(sender, "Messages.command-ginteraction-import", "%Type%", args[1], "%Count%", importerResult.getCount());
                break;
            case "export":
                if(args.length == 1) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-ginteraction-export-use-error");
                    break;
                }
                GInteractionExporter interactionExporter = gHoloMain.getInteractionExporterService().getInteractionExporter(args[1]);
                if(interactionExporter == null) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-ginteraction-export-exist-error", "%Type%", args[1]);
                    break;
                }
                GInteractionExporterResult exporterResult = interactionExporter.exportInteractions(gHoloMain, true);
                if(!exporterResult.hasSucceeded()) {
                    gHoloMain.getMessageService().sendMessage(sender, "Messages.command-ginteraction-export-export-error", "%Type%", args[1]);
                    break;
                }
                gHoloMain.getMessageService().sendMessage(sender, "Messages.command-ginteraction-export", "%Type%", args[1], "%Count%", exporterResult.getCount());
                break;
            default:
                gHoloMain.getMessageService().sendMessage(sender, "Messages.command-ginteraction-use-error");
        }

        return true;
    }

}