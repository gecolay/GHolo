package dev.geco.gholo.manager;

import java.io.*;
import java.util.*;

import org.bukkit.*;
import org.bukkit.configuration.file.*;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.objects.*;

public class HoloImportManager {

    private final GHoloMain GPM;

    public HoloImportManager(GHoloMain GPluginMain) { GPM = GPluginMain; }

    public List<String> AVAILABLE_PLUGIN_IMPORTS = new ArrayList<>(); {
        AVAILABLE_PLUGIN_IMPORTS.add("holographic_displays");
        AVAILABLE_PLUGIN_IMPORTS.add("decent_holograms");
    }

    public int importFromPlugin(String Plugin) {
        try {
            switch(Plugin.toLowerCase()) {
                case "holographic_displays": return importHolographicDisplays();
                case "decent_holograms": return importDecentHolograms();
                default: return -1;
            }
        } catch (Throwable e) { e.printStackTrace(); }
        return -1;
    }

    private int importHolographicDisplays() {

        int imported = 0;

        File contentFile = new File("plugins/HolographicDisplays/database.yml");
        if(!contentFile.exists()) return imported;

        FileConfiguration fileContent = YamlConfiguration.loadConfiguration(contentFile);
        for(String line : Objects.requireNonNull(fileContent.getConfigurationSection("")).getKeys(false)) {

            if(GPM.getHoloManager().getHolo(line) != null) continue;

            String[] args;

            if(fileContent.contains(line + ".location")) {

                args = fileContent.getString(line + ".location", "").split(",");
            } else {

                String basePath = line + ".position.";
                args = new String[4];
                args[0] = fileContent.getString(basePath + "world");
                args[1] = fileContent.getString(basePath + "x");
                args[2] = fileContent.getString(basePath + "y");
                args[3] = fileContent.getString(basePath + "z");
            }

            World world = Bukkit.getWorld(args[0]);
            if(world == null) continue;

            List<String> rows = fileContent.getStringList(line + ".lines");
            List<String> removeContent = fileContent.getStringList(line + ".lines");
            for(String removeContentLine : removeContent) if(removeContentLine.equalsIgnoreCase("null")) rows.remove("null");

            GHolo holo = GPM.getHoloManager().createHolo(line, new Location(world, Double.parseDouble(args[1]), Double.parseDouble(args[2]) - 0.51, Double.parseDouble(args[3])));

            for(String row : rows) GPM.getHoloManager().createHoloRow(holo, row);

            imported++;
        }

        return imported;
    }

    private int importDecentHolograms() {

        int imported = 0;

        File hologramsDir = new File("plugins/DecentHolograms/holograms");
        if(!hologramsDir.exists()) return imported;

        for(File file : Objects.requireNonNull(hologramsDir.listFiles())) {

            String name = file.getName().replace(".yml", "");
            if(GPM.getHoloManager().getHolo(name) != null) continue;

            FileConfiguration fileContent = YamlConfiguration.loadConfiguration(file);
            if(!fileContent.getBoolean("enabled", false)) continue;

            int range = fileContent.getInt("display-range", 128);

            String[] args = fileContent.getString("location", "").split(":");

            World world = Bukkit.getWorld(args[0]);
            if(world == null) continue;

            Location location = new Location(world, Double.parseDouble(args[1].replace(",", ".")), Double.parseDouble(args[2].replace(",", ".")) - 0.41, Double.parseDouble(args[3].replace(",", ".")));

            GHolo holo = GPM.getHoloManager().createHolo(name, location);
            GPM.getHoloManager().updateRange(holo, range);

            for(Object section : Objects.requireNonNull(fileContent.getList("pages"))) {

                if(!(section instanceof LinkedHashMap)) continue;

                Object lines = ((LinkedHashMap<?, ?>) section).get("lines");
                if(!(lines instanceof ArrayList)) continue;

                for(Object contentMap : (ArrayList<?>) lines) {

                    if(!(contentMap instanceof LinkedHashMap)) continue;

                    String content = (String) ((LinkedHashMap<?, ?>) contentMap).get("content");
                    GPM.getHoloManager().createHoloRow(holo, content);
                }
            }

            imported++;
        }

        return imported;
    }

}
