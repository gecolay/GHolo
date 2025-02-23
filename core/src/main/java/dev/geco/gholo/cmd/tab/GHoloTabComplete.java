package dev.geco.gholo.cmd.tab;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.cmd.GHoloCommand;
import dev.geco.gholo.object.holo.GHolo;
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
import java.util.Set;

public class GHoloTabComplete implements TabCompleter {

    private final GHoloMain gHoloMain;
    private final Set<String> REQUIRE_ID_SUBCOMMANDS = Set.of("info", "remove", "rename", "move", "tphere", "tp", "align", "addrow", "insertrow", "setrow", "removerow", "offsetrow", "copy", "option", "image");
    private final Set<String> OPTIONS = Set.of("range", "background_color", "text_opacity", "text_shadow", "text_alignment", "billboard", "see_through", "scale", "rotation", "brightness", "permission", "size");

    public GHoloTabComplete(GHoloMain gHoloMain) {
        this.gHoloMain = gHoloMain;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        List<String> complete = new ArrayList<>(), completeStarted = new ArrayList<>();

        List<String> holoIdArg = new ArrayList<>(REQUIRE_ID_SUBCOMMANDS);
        if(!(sender instanceof Player)) holoIdArg.removeAll(List.of("tp", "tphere"));

        if(args.length == 1) {
            if(gHoloMain.getPermissionService().hasPermission(sender, "Holo")) {
                complete.addAll(GHoloCommand.SUBCOMMANDS);
                if(!(sender instanceof Player)) complete.removeAll(List.of("create", "tp", "tphere"));
            }
            if(!args[args.length - 1].isEmpty()) {
                for(String entry : complete) if(entry.toLowerCase().startsWith(args[args.length - 1].toLowerCase())) completeStarted.add(entry);
                complete.clear();
            }
        } else if(args.length == 2) {
            if(holoIdArg.contains(args[0].toLowerCase())) {
                complete.addAll(gHoloMain.getHoloService().getHolos().stream().map(GHolo::getId).toList());
            } else if(args[0].equalsIgnoreCase("import")) {
                complete.addAll(gHoloMain.getHoloImporterService().getHoloImporters().keySet());
            } else if(args[0].equalsIgnoreCase("export")) {
                complete.addAll(gHoloMain.getHoloExporterService().getHoloExporters().keySet());
            }
            if(!args[args.length - 1].isEmpty()) {
                for(String entry : complete) if(entry.toLowerCase().startsWith(args[args.length - 1].toLowerCase())) completeStarted.add(entry);
                complete.clear();
            }
        } else if(args.length == 3) {
            if(args[0].equalsIgnoreCase("move")) {
                GHolo holo = gHoloMain.getHoloService().getHolo(args[1]);
                if(holo != null) complete.addAll(List.of("" + holo.getRawLocation().getX(), "~"));
            } else if(args[0].equalsIgnoreCase("align")) {
                complete.addAll(gHoloMain.getHoloService().getHolos().stream().map(GHolo::getId).filter(holoId -> !holoId.equalsIgnoreCase(args[1])).toList());
            } else if(args[0].equalsIgnoreCase("insertrow") || args[0].equalsIgnoreCase("setrow") || args[0].equalsIgnoreCase("removerow") || args[0].equalsIgnoreCase("offsetrow")) {
                GHolo holo = gHoloMain.getHoloService().getHolo(args[1]);
                if(holo != null) for(int position = 1; position <= holo.getRows().size(); position++) complete.add("" + position);
            } else if(args[0].equalsIgnoreCase("option")) {
                complete.addAll(List.of("holo", "row"));
            } else if(args[0].equalsIgnoreCase("image")) {
                complete.addAll(ImageUtil.IMAGE_TYPES);
            } else if(args[0].equalsIgnoreCase("import") || args[0].equalsIgnoreCase("export")) {
                complete.addAll(List.of("true", "false"));
            }
            if(!args[args.length - 1].isEmpty()) {
                for(String entry : complete) if(entry.toLowerCase().startsWith(args[args.length - 1].toLowerCase())) completeStarted.add(entry);
                complete.clear();
            }
        } else if(args.length == 4) {
            if(args[0].equalsIgnoreCase("move")) {
                GHolo holo = gHoloMain.getHoloService().getHolo(args[1]);
                if(holo != null) complete.addAll(List.of("" + holo.getRawLocation().getY(), "~"));
            } else if(args[0].equalsIgnoreCase("align")) {
                complete.addAll(List.of("x", "y", "z", "xy", "xz", "yz", "xyz"));
            } else if(args[0].equalsIgnoreCase("setrow")) {
                GHolo holo = gHoloMain.getHoloService().getHolo(args[1]);
                if(holo != null) {
                    try {
                        complete.add(holo.getRow(Integer.parseInt(args[2]) - 1).getContent());
                    } catch(Throwable ignored) { }
                }
            } else if(args[0].equalsIgnoreCase("removerow")) {
                complete.addAll(List.of("true", "false"));
            } else if(args[0].equalsIgnoreCase("offsetrow")) {
                GHolo holo = gHoloMain.getHoloService().getHolo(args[1]);
                if(holo != null) {
                    try {
                        complete.addAll(List.of("" + holo.getRow(Integer.parseInt(args[2]) - 1).getRawOffset().getX(), "~", "*"));
                    } catch(Throwable ignored) { }
                }
            } else if(args[0].equalsIgnoreCase("option")) {
                if(args[2].equalsIgnoreCase("holo")) {
                    complete.addAll(OPTIONS);
                } else if(args[2].equalsIgnoreCase("row")) {
                    GHolo holo = gHoloMain.getHoloService().getHolo(args[1]);
                    if(holo != null) for(int row = 1; row <= holo.getRows().size(); row++) complete.add("" + row);
                }
            } else if(args[0].equalsIgnoreCase("image")) {
                if(args[2].equalsIgnoreCase("file")) {
                    File[] files = ImageUtil.IMAGE_FOLDER.listFiles();
                    if(files != null) complete.addAll(Arrays.stream(files).map(File::getName).toList());
                } else if(args[2].equalsIgnoreCase("avatar") || args[2].equalsIgnoreCase("helm")) {
                    complete.addAll(Arrays.stream(Bukkit.getOfflinePlayers()).map(OfflinePlayer::getName).toList());
                }
            }
            if(!args[args.length - 1].isEmpty()) {
                for(String entry : complete) if(entry.toLowerCase().startsWith(args[args.length - 1].toLowerCase())) completeStarted.add(entry);
                complete.clear();
            }
        } else if(args.length == 5) {
            if(args[0].equalsIgnoreCase("move")) {
                GHolo holo = gHoloMain.getHoloService().getHolo(args[1]);
                if(holo != null) complete.addAll(List.of("" + holo.getRawLocation().getZ(), "~"));
            } else if(args[0].equalsIgnoreCase("offsetrow")) {
                GHolo holo = gHoloMain.getHoloService().getHolo(args[1]);
                if(holo != null) {
                    try {
                        complete.addAll(List.of("" + holo.getRow(Integer.parseInt(args[2]) - 1).getRawOffset().getY(), "~", "*"));
                    } catch(Throwable ignored) { }
                }
            } else if(args[0].equalsIgnoreCase("option")) {
                if(args[2].equalsIgnoreCase("holo")) {
                    if(args[3].equalsIgnoreCase("background_color")) {
                        complete.addAll(List.of("transparent", "#000000", "#00000000", "000000", "00000000", "#ffffff", "#ffffffff", "ffffff", "ffffffff"));
                    } else if(args[3].equalsIgnoreCase("text_shadow") || args[3].equalsIgnoreCase("see_through")) {
                        complete.addAll(List.of("true", "false"));
                    } else if(args[3].equalsIgnoreCase("text_alignment")) {
                        complete.addAll(Arrays.stream(TextDisplay.TextAlignment.values()).map(ta -> ta.name().toLowerCase()).toList());
                    } else if(args[3].equalsIgnoreCase("billboard")) {
                        complete.addAll(Arrays.stream(Display.Billboard.values()).map(b -> b.name().toLowerCase()).toList());
                    } else if(args[3].equalsIgnoreCase("rotation")) {
                        complete.addAll(List.of("yaw", "pitch"));
                    } else if(args[3].equalsIgnoreCase("size")) {
                        complete.addAll(List.of("width", "height"));
                    } else complete.add("*");
                } else if(args[2].equalsIgnoreCase("row")) {
                    complete.addAll(OPTIONS);
                }
            }
            if(!args[args.length - 1].isEmpty()) {
                for(String entry : complete) if(entry.toLowerCase().startsWith(args[args.length - 1].toLowerCase())) completeStarted.add(entry);
                complete.clear();
            }
        } else if(args.length == 6) {
            if(args[0].equalsIgnoreCase("offsetrow")) {
                GHolo holo = gHoloMain.getHoloService().getHolo(args[1]);
                if(holo != null) {
                    try {
                        complete.addAll(List.of("" + holo.getRow(Integer.parseInt(args[2]) - 1).getRawOffset().getZ(), "~", "*"));
                    } catch(Throwable ignored) { }
                }
            } else if(args[0].equalsIgnoreCase("option")) {
                if(args[2].equalsIgnoreCase("holo")) {
                    if(args[4].equalsIgnoreCase("rotation") || args[4].equalsIgnoreCase("size")) {
                        complete.add("*");
                    }
                } else if(args[2].equalsIgnoreCase("row")) {
                    if(args[3].equalsIgnoreCase("background_color")) {
                        complete.addAll(List.of("transparent", "#000000", "#00000000", "000000", "00000000", "#ffffff", "#ffffffff", "ffffff", "ffffffff"));
                    } else if(args[4].equalsIgnoreCase("text_shadow") || args[4].equalsIgnoreCase("see_through")) {
                        complete.addAll(List.of("true", "false"));
                    } else if(args[4].equalsIgnoreCase("text_alignment")) {
                        complete.addAll(Arrays.stream(TextDisplay.TextAlignment.values()).map(ta -> ta.name().toLowerCase()).toList());
                    } else if(args[4].equalsIgnoreCase("billboard")) {
                        complete.addAll(Arrays.stream(Display.Billboard.values()).map(b -> b.name().toLowerCase()).toList());
                    } else if(args[4].equalsIgnoreCase("rotation")) {
                        complete.addAll(List.of("yaw", "pitch"));
                    } else if(args[4].equalsIgnoreCase("size")) {
                        complete.addAll(List.of("width", "height"));
                    } else complete.add("*");
                }
            }
            if(!args[args.length - 1].isEmpty()) {
                for(String entry : complete) if(entry.toLowerCase().startsWith(args[args.length - 1].toLowerCase())) completeStarted.add(entry);
                complete.clear();
            }
        } else if(args.length == 7) {
            if(args[0].equalsIgnoreCase("option")) {
                if(args[2].equalsIgnoreCase("row")) {
                    if(args[4].equalsIgnoreCase("rotation") || args[4].equalsIgnoreCase("size")) {
                        complete.add("*");
                    }
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