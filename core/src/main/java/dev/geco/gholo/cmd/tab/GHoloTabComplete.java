package dev.geco.gholo.cmd.tab;

import java.util.*;

import org.jetbrains.annotations.*;

import org.bukkit.command.*;
import org.bukkit.entity.*;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.objects.*;

public class GHoloTabComplete implements TabCompleter {

    private final GHoloMain GPM;

    public GHoloTabComplete(GHoloMain GPluginMain) { GPM = GPluginMain; }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender Sender, @NotNull Command Command, @NotNull String Label, String[] Args) {

        List<String> complete = new ArrayList<>(), completeStarted = new ArrayList<>();

        List<String> holoIdArg = List.of("info", "remove", "rename", "relocate", "tphere", "tpto", "setrange", "addrow", "setrow", "removerow");

        if(Sender instanceof Player) {

            if(Args.length == 1) {

                if(GPM.getPManager().hasPermission(Sender, "Holo")) {
                    complete.add("help");
                    complete.add("list");
                    complete.add("create");
                    complete.add("info");
                    complete.add("remove");
                    complete.add("rename");
                    complete.add("relocate");
                    complete.add("tphere");
                    complete.add("tpto");
                    complete.add("setrange");
                    complete.add("addrow");
                    complete.add("setrow");
                    complete.add("removerow");
                }

                if(!Args[Args.length - 1].isEmpty()) {

                    for(String entry : complete) if(entry.toLowerCase().startsWith(Args[Args.length - 1].toLowerCase())) completeStarted.add(entry);

                    complete.clear();
                }
            } else if(Args.length == 2) {

                if(holoIdArg.contains(Args[0].toLowerCase())) {
                    complete.addAll(GPM.getHoloManager().getHolos().stream().map(GHolo::getId).toList());
                }

                if(!Args[Args.length - 1].isEmpty()) {

                    for(String entry : complete) if(entry.toLowerCase().startsWith(Args[Args.length - 1].toLowerCase())) completeStarted.add(entry);

                    complete.clear();
                }
            } else if(Args.length == 3) {

                if(Args[0].equalsIgnoreCase("setrow") || Args[0].equalsIgnoreCase("removerow")) {
                    GHolo holo = GPM.getHoloManager().getHolo(Args[1]);
                    if(holo != null) for(int row = 1; row <= holo.getRows().size(); row++) complete.add("" + row);
                }

                if(!Args[Args.length - 1].isEmpty()) {

                    for(String entry : complete) if(entry.toLowerCase().startsWith(Args[Args.length - 1].toLowerCase())) completeStarted.add(entry);

                    complete.clear();
                }
            }
        }

        return complete.isEmpty() ? completeStarted : complete;
    }

}