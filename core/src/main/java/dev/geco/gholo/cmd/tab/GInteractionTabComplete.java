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
import java.util.Set;

public class GInteractionTabComplete implements TabCompleter {

    private final GHoloMain gHoloMain;
    private final Set<String> REQUIRE_ID_SUBCOMMANDS = Set.of("info", "remove", "rename", "move", "tphere", "tp", "align", "addaction", "insertaction", "setaction", "removeaction", "copy", "option");

    public GInteractionTabComplete(GHoloMain gHoloMain) {
        this.gHoloMain = gHoloMain;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        List<String> complete = new ArrayList<>(), completeStarted = new ArrayList<>();

        List<String> interactionIdArg = new ArrayList<>(REQUIRE_ID_SUBCOMMANDS);
        if(!(sender instanceof Player)) interactionIdArg.removeAll(List.of("tp", "tphere"));

        if(args.length == 1) {
            if(gHoloMain.getPermissionService().hasPermission(sender, "Holo")) {
                complete.addAll(GInteractionCommand.SUBCOMMANDS);
                if(!(sender instanceof Player)) complete.removeAll(List.of("create", "tp", "tphere"));
            }
            if(!args[args.length - 1].isEmpty()) {
                for(String entry : complete) if(entry.toLowerCase().startsWith(args[args.length - 1].toLowerCase())) completeStarted.add(entry);
                complete.clear();
            }
        } else if(args.length == 2) {
            if(interactionIdArg.contains(args[0].toLowerCase())) {
                complete.addAll(gHoloMain.getInteractionService().getInteractions().stream().map(GInteraction::getId).toList());
            } else if(args[0].equalsIgnoreCase("import")) {
                complete.addAll(gHoloMain.getInteractionImporterService().getInteractionImporters().keySet());
            } else if(args[0].equalsIgnoreCase("export")) {
                complete.addAll(gHoloMain.getInteractionExporterService().getInteractionExporters().keySet());
            }
            if(!args[args.length - 1].isEmpty()) {
                for(String entry : complete) if(entry.toLowerCase().startsWith(args[args.length - 1].toLowerCase())) completeStarted.add(entry);
                complete.clear();
            }
        } else if(args.length == 3) {
            if(args[0].equalsIgnoreCase("move")) {
                GInteraction interaction = gHoloMain.getInteractionService().getInteraction(args[1]);
                if(interaction != null) complete.addAll(List.of("" + interaction.getRawLocation().getX(), "~"));
            } else if(args[0].equalsIgnoreCase("align")) {
                complete.addAll(gHoloMain.getInteractionService().getInteractions().stream().map(GInteraction::getId).filter(interactionId -> !interactionId.equalsIgnoreCase(args[1])).toList());
            } else if(args[0].equalsIgnoreCase("addaction")) {
                complete.addAll(gHoloMain.getInteractionActionService().getInteractionActions().keySet());
            } else if(args[0].equalsIgnoreCase("insertaction") || args[0].equalsIgnoreCase("setaction") || args[0].equalsIgnoreCase("removeaction")) {
                GInteraction interaction = gHoloMain.getInteractionService().getInteraction(args[1]);
                if(interaction != null) for(int position = 1; position <= interaction.getActions().size(); position++) complete.add("" + position);
            } else if(args[0].equalsIgnoreCase("option")) {
                complete.addAll(List.of("permission", "size"));
            }
            if(!args[args.length - 1].isEmpty()) {
                for(String entry : complete) if(entry.toLowerCase().startsWith(args[args.length - 1].toLowerCase())) completeStarted.add(entry);
                complete.clear();
            }
        } else if(args.length == 4) {
            if(args[0].equalsIgnoreCase("move")) {
                GInteraction interaction = gHoloMain.getInteractionService().getInteraction(args[1]);
                if(interaction != null) complete.addAll(List.of("" + interaction.getRawLocation().getY(), "~"));
            } else if(args[0].equalsIgnoreCase("align")) {
                complete.addAll(List.of("x", "y", "z", "xy", "xz", "yz", "xyz"));
            } else if(args[0].equalsIgnoreCase("insertaction") || args[0].equalsIgnoreCase("setaction")) {
                complete.addAll(gHoloMain.getInteractionActionService().getInteractionActions().keySet());
            } else if(args[0].equalsIgnoreCase("option")) {
                if(args[3].equalsIgnoreCase("size")) {
                    complete.addAll(List.of("width", "height"));
                } else complete.add("*");
            }
            if(!args[args.length - 1].isEmpty()) {
                for(String entry : complete) if(entry.toLowerCase().startsWith(args[args.length - 1].toLowerCase())) completeStarted.add(entry);
                complete.clear();
            }
        } else if(args.length == 5) {
            if(args[0].equalsIgnoreCase("move")) {
                GInteraction interaction = gHoloMain.getInteractionService().getInteraction(args[1]);
                if(interaction != null) complete.addAll(List.of("" + interaction.getRawLocation().getZ(), "~"));
            } else if(args[0].equalsIgnoreCase("setaction")) {
                GInteraction interaction = gHoloMain.getInteractionService().getInteraction(args[1]);
                if(interaction != null) {
                    try {
                        complete.add(interaction.getAction(Integer.parseInt(args[2]) - 1).getParameter());
                    } catch(Throwable ignored) { }
                }
            } else if(args[0].equalsIgnoreCase("insertaction")) {
                if(args[3].equalsIgnoreCase("teleport")) {
                    complete.addAll(List.of("~:~:~:~", "world:x:y:z", "world:x:y:z:yaw:pitch"));
                }
            } else if(args[0].equalsIgnoreCase("option")) {
                if(args[3].equalsIgnoreCase("size")) {
                    complete.add("*");
                }
            }
            if(!args[args.length - 1].isEmpty()) {
                for(String entry : complete) if(entry.toLowerCase().startsWith(args[args.length - 1].toLowerCase())) completeStarted.add(entry);
                complete.clear();
            }
        }
        return complete.isEmpty() ? completeStarted : complete;
    }

}