package dev.geco.gholo.cmd.tab;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.cmd.GInteractionCommand;
import dev.geco.gholo.object.interaction.GInteraction;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class GInteractionTabComplete implements TabCompleter {

    private final GHoloMain gHoloMain;

    public GInteractionTabComplete(GHoloMain gHoloMain) {
        this.gHoloMain = gHoloMain;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        List<String> complete = new ArrayList<>(), completeStarted = new ArrayList<>();

        List<String> interactionIdArg = new ArrayList<>(List.of("info", "remove", "rename", "move", "tphere", "tp", "align", "addaction", "insertaction", "setaction", "removeaction", "copy", "option", "rotate"));
        if(!(sender instanceof Player)) interactionIdArg.removeAll(List.of("tp", "tphere"));

        if(args.length == 1) {
            if(gHoloMain.getPermissionService().hasPermission(sender, "Holo")) {
                complete.addAll(GInteractionCommand.COMMAND_LIST);
                if(!(sender instanceof Player)) complete.removeAll(List.of("create", "tp", "tphere"));
            }
            if(!args[args.length - 1].isEmpty()) {
                for(String entry : complete) if(entry.toLowerCase().startsWith(args[args.length - 1].toLowerCase())) completeStarted.add(entry);
                complete.clear();
            }
        } else if(args.length == 2) {
            if(interactionIdArg.contains(args[0].toLowerCase())) {
                complete.addAll(gHoloMain.getInteractionService().getInteractions().stream().map(GInteraction::getId).toList());
            }
            if(args[0].equalsIgnoreCase("import")) {
                complete.addAll(gHoloMain.getInteractionImporterService().getInteractionImporters().keySet());
            }
            if(args[0].equalsIgnoreCase("export")) {
                complete.addAll(gHoloMain.getInteractionExporterService().getInteractionExporters().keySet());
            }
            if(!args[args.length - 1].isEmpty()) {
                for(String entry : complete) if(entry.toLowerCase().startsWith(args[args.length - 1].toLowerCase())) completeStarted.add(entry);
                complete.clear();
            }
        } else if(args.length == 3) {
            if(args[0].equalsIgnoreCase("align")) {
                complete.addAll(gHoloMain.getInteractionService().getInteractions().stream().map(GInteraction::getId).filter(interactionId -> !interactionId.equalsIgnoreCase(args[1])).toList());
            }
            if(args[0].equalsIgnoreCase("insertaction") || args[0].equalsIgnoreCase("setaction") || args[0].equalsIgnoreCase("removeaction")) {
                GInteraction interaction = gHoloMain.getInteractionService().getInteraction(args[1]);
                if(interaction != null) for(int row = 1; row <= interaction.getActions().size(); row++) complete.add("" + row);
            }
            if(args[0].equalsIgnoreCase("option")) {
                complete.addAll(List.of("range", "permission"));
            }
            if(args[0].equalsIgnoreCase("rotate")) {
                complete.addAll(List.of("yaw", "pitch"));
            }
            if(!args[args.length - 1].isEmpty()) {
                for(String entry : complete) if(entry.toLowerCase().startsWith(args[args.length - 1].toLowerCase())) completeStarted.add(entry);
                complete.clear();
            }
        } else if(args.length == 4) {
            if(args[0].equalsIgnoreCase("align")) {
                complete.addAll(List.of("x", "y", "z", "xy", "xz", "yz", "xyz"));
            }
            if(args[0].equalsIgnoreCase("option") || args[0].equalsIgnoreCase("rotate")) {
                complete.add("*");
            }
            if(!args[args.length - 1].isEmpty()) {
                for(String entry : complete) if(entry.toLowerCase().startsWith(args[args.length - 1].toLowerCase())) completeStarted.add(entry);
                complete.clear();
            }
        }
        return complete.isEmpty() ? completeStarted : complete;
    }

}