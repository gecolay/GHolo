package dev.geco.gholo.cmd.tab;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.cmd.GHoloCommand;
import dev.geco.gholo.object.GHolo;
import dev.geco.gholo.util.ImageUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GHoloTabComplete implements TabCompleter {

    private final GHoloMain gHoloMain;

    public GHoloTabComplete(GHoloMain gHoloMain) {
        this.gHoloMain = gHoloMain;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        List<String> complete = new ArrayList<>(), completeStarted = new ArrayList<>();

        List<String> holoIdArg = new ArrayList<>(List.of("info", "remove", "rename", "relocate", "tphere", "tpto", "align", "addrow", "insertrow", "setrow", "removerow", "positionrow", "copyrows", "data", "setimage"));
        if(!(sender instanceof Player)) holoIdArg.removeAll(List.of("tpto", "tphere"));

        if(args.length == 1) {
            if(gHoloMain.getPermissionService().hasPermission(sender, "Holo")) {
                complete.addAll(GHoloCommand.COMMAND_LIST);
                if(!(sender instanceof Player)) complete.removeAll(List.of("create", "tpto", "tphere"));
            }
            if(!args[args.length - 1].isEmpty()) {
                for(String entry : complete) if(entry.toLowerCase().startsWith(args[args.length - 1].toLowerCase())) completeStarted.add(entry);
                complete.clear();
            }
        } else if(args.length == 2) {
            if(holoIdArg.contains(args[0].toLowerCase())) {
                complete.addAll(gHoloMain.getHoloService().getHolos().stream().map(GHolo::getId).toList());
            }
            if(args[0].equalsIgnoreCase("import")) {
                complete.addAll(gHoloMain.getHoloImportService().AVAILABLE_PLUGIN_IMPORTS);
            }
            if(!args[args.length - 1].isEmpty()) {
                for(String entry : complete) if(entry.toLowerCase().startsWith(args[args.length - 1].toLowerCase())) completeStarted.add(entry);
                complete.clear();
            }
        } else if(args.length == 3) {
            if(args[0].equalsIgnoreCase("align") || args[0].equalsIgnoreCase("copyrows")) {
                complete.addAll(gHoloMain.getHoloService().getHolos().stream().map(GHolo::getId).filter(holoId -> !holoId.equalsIgnoreCase(args[1])).toList());
            }
            if(args[0].equalsIgnoreCase("insertrow") || args[0].equalsIgnoreCase("setrow") || args[0].equalsIgnoreCase("removerow") || args[0].equalsIgnoreCase("positionrow")) {
                GHolo holo = gHoloMain.getHoloService().getHolo(args[1]);
                if(holo != null) for(int row = 1; row <= holo.getRows().size(); row++) complete.add("" + row);
            }
            if(args[0].equalsIgnoreCase("data")) {
                complete.addAll(List.of("default", "row"));
            }
            if(args[0].equalsIgnoreCase("setimage")) {
                complete.addAll(ImageUtil.IMAGE_TYPES);
            }
            if(!args[args.length - 1].isEmpty()) {
                for(String entry : complete) if(entry.toLowerCase().startsWith(args[args.length - 1].toLowerCase())) completeStarted.add(entry);
                complete.clear();
            }
        } else if(args.length == 4) {
            if(args[0].equalsIgnoreCase("align")) {
                complete.addAll(List.of("x", "y", "z", "xy", "xz", "yz", "xyz"));
            }
            if(args[0].equalsIgnoreCase("positionrow")) {
                complete.addAll(List.of("xoffset", "yoffset", "zoffset", "yaw", "pitch"));
            }
            if(args[0].equalsIgnoreCase("data")) {
                if(args[2].equalsIgnoreCase("default")) {
                    complete.addAll(List.of("range", "background_color", "text_opacity", "text_shadow", "text_alignment", "billboard", "see_through", "scale", "brightness", "permission"));
                }
                if(args[2].equalsIgnoreCase("row")) {
                    GHolo holo = gHoloMain.getHoloService().getHolo(args[1]);
                    if(holo != null) for(int row = 1; row <= holo.getRows().size(); row++) complete.add("" + row);
                }
            }
            if(args[0].equalsIgnoreCase("setimage")) {
                if(args[2].equalsIgnoreCase("file")) {
                    File[] files = ImageUtil.IMAGE_FOLDER.listFiles();
                    if(files != null) complete.addAll(Arrays.stream(files).map(File::getName).toList());
                }
                if(args[2].equalsIgnoreCase("avatar") || args[2].equalsIgnoreCase("helm")) {
                    complete.addAll(Arrays.stream(Bukkit.getOfflinePlayers()).map(OfflinePlayer::getName).toList());
                }
            }
            if(!args[args.length - 1].isEmpty()) {
                for(String entry : complete) if(entry.toLowerCase().startsWith(args[args.length - 1].toLowerCase())) completeStarted.add(entry);
                complete.clear();
            }
        } else if(args.length == 5) {
            if(args[0].equalsIgnoreCase("data")) {
                if(args[2].equalsIgnoreCase("default")) {
                    if(args[3].equalsIgnoreCase("text_shadow") || args[3].equalsIgnoreCase("see_through")) {
                        complete.addAll(List.of("true", "false"));
                    }
                    if(args[3].equalsIgnoreCase("text_alignment")) {
                        complete.addAll(Arrays.stream(TextDisplay.TextAlignment.values()).map(ta -> ta.name().toLowerCase()).toList());
                    }
                    if(args[3].equalsIgnoreCase("billboard")) {
                        complete.addAll(Arrays.stream(Display.Billboard.values()).map(b -> b.name().toLowerCase()).toList());
                    }
                    complete.add("*");
                }
                if(args[2].equalsIgnoreCase("row")) {
                    complete.addAll(List.of("range", "background_color", "text_opacity", "text_shadow", "text_alignment", "billboard", "see_through", "scale", "brightness", "permission"));
                }
            }
            if(!args[args.length - 1].isEmpty()) {
                for(String entry : complete) if(entry.toLowerCase().startsWith(args[args.length - 1].toLowerCase())) completeStarted.add(entry);
                complete.clear();
            }
        } else if(args.length == 6) {
            if(args[0].equalsIgnoreCase("data")) {
                if(args[2].equalsIgnoreCase("row")) {
                    if(args[4].equalsIgnoreCase("text_shadow") || args[4].equalsIgnoreCase("see_through")) {
                        complete.addAll(List.of("true", "false"));
                    }
                    if(args[4].equalsIgnoreCase("text_alignment")) {
                        complete.addAll(Arrays.stream(TextDisplay.TextAlignment.values()).map(ta -> ta.name().toLowerCase()).toList());
                    }
                    if(args[4].equalsIgnoreCase("billboard")) {
                        complete.addAll(Arrays.stream(Display.Billboard.values()).map(b -> b.name().toLowerCase()).toList());
                    }
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