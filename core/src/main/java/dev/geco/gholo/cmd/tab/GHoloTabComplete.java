package dev.geco.gholo.cmd.tab;

import java.io.*;
import java.util.*;

import org.jetbrains.annotations.*;

import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.cmd.*;
import dev.geco.gholo.objects.*;
import dev.geco.gholo.util.*;

public class GHoloTabComplete implements TabCompleter {

    private final GHoloMain GPM;

    public GHoloTabComplete(GHoloMain GPluginMain) { GPM = GPluginMain; }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender Sender, @NotNull Command Command, @NotNull String Label, String[] Args) {

        List<String> complete = new ArrayList<>(), completeStarted = new ArrayList<>();

        List<String> holoIdArg = new ArrayList<>(List.of("info", "remove", "rename", "relocate", "tphere", "tpto", "align", "setrange", "addrow", "insertrow", "setrow", "removerow", "copyrows", "setimage"));

        if(!(Sender instanceof Player)) holoIdArg.removeAll(List.of("tpto", "tphere"));

        if(Args.length == 1) {

            if(GPM.getPManager().hasPermission(Sender, "Holo")) {
                complete.addAll(GHoloCommand.COMMAND_LIST);
                if(!(Sender instanceof Player)) complete.removeAll(List.of("create", "tpto", "tphere"));
            }

            if(!Args[Args.length - 1].isEmpty()) {

                for(String entry : complete) if(entry.toLowerCase().startsWith(Args[Args.length - 1].toLowerCase())) completeStarted.add(entry);

                complete.clear();
            }
        } else if(Args.length == 2) {

            if(holoIdArg.contains(Args[0].toLowerCase())) {
                complete.addAll(GPM.getHoloManager().getHolos().stream().map(GHolo::getId).toList());
            }

            if(Args[0].equalsIgnoreCase("importdata")) {
                complete.addAll(GPM.getHoloImportManager().AVAILABLE_PLUGIN_IMPORTS);
            }

            if(!Args[Args.length - 1].isEmpty()) {

                for(String entry : complete) if(entry.toLowerCase().startsWith(Args[Args.length - 1].toLowerCase())) completeStarted.add(entry);

                complete.clear();
            }
        } else if(Args.length == 3) {

            if(Args[0].equalsIgnoreCase("align") || Args[0].equalsIgnoreCase("copyrows")) {
                complete.addAll(GPM.getHoloManager().getHolos().stream().map(GHolo::getId).filter(holoId -> !holoId.equalsIgnoreCase(Args[1])).toList());
            }

            if(Args[0].equalsIgnoreCase("insertrow") || Args[0].equalsIgnoreCase("setrow") || Args[0].equalsIgnoreCase("removerow")) {
                GHolo holo = GPM.getHoloManager().getHolo(Args[1]);
                if(holo != null) for(int row = 1; row <= holo.getRows().size(); row++) complete.add("" + row);
            }

            if(Args[0].equalsIgnoreCase("setimage")) {
                complete.addAll(ImageUtil.IMAGE_TYPES);
            }

            if(!Args[Args.length - 1].isEmpty()) {

                for(String entry : complete) if(entry.toLowerCase().startsWith(Args[Args.length - 1].toLowerCase())) completeStarted.add(entry);

                complete.clear();
            }
        } else if(Args.length == 4) {

            if(Args[0].equalsIgnoreCase("align")) {
                complete.addAll(List.of("x", "y", "z", "xy", "xz", "yz", "xyz"));
            }

            if(Args[0].equalsIgnoreCase("setimage")) {

                if(Args[2].equalsIgnoreCase("file")) {
                    File[] files = ImageUtil.IMAGE_FOLDER.listFiles();
                    if(files != null) complete.addAll(Arrays.stream(files).map(File::getName).toList());
                }

                if(Args[2].equalsIgnoreCase("avatar") || Args[2].equalsIgnoreCase("helm")) {
                    complete.addAll(Arrays.stream(Bukkit.getOfflinePlayers()).map(OfflinePlayer::getName).toList());
                }
            }

            if(!Args[Args.length - 1].isEmpty()) {

                for(String entry : complete) if(entry.toLowerCase().startsWith(Args[Args.length - 1].toLowerCase())) completeStarted.add(entry);

                complete.clear();
            }
        }

        return complete.isEmpty() ? completeStarted : complete;
    }

}