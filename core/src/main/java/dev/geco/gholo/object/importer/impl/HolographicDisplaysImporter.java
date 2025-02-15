package dev.geco.gholo.object.importer.impl;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.object.GHolo;
import dev.geco.gholo.object.GHoloRow;
import dev.geco.gholo.object.importer.GHoloImporter;
import dev.geco.gholo.object.importer.GHoloImporterResult;
import dev.geco.gholo.object.location.SimpleLocation;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;
import java.util.UUID;

public class HolographicDisplaysImporter extends GHoloImporter {

    @Override
    public @NotNull String getType() { return "holographic_displays"; }

    @Override
    public @NotNull GHoloImporterResult importHolos(@NotNull GHoloMain gHoloMain, boolean override) {
        int imported = 0;

        File contentFile = new File("plugins/HolographicDisplays/database.yml");
        if(!contentFile.exists()) return new GHoloImporterResult(false, 0);

        FileConfiguration fileContent = YamlConfiguration.loadConfiguration(contentFile);
        for(String id : fileContent.getConfigurationSection("").getKeys(false)) {
            try {
                if(!override && gHoloMain.getHoloService().getHolo(id) != null) continue;

                String[] args;
                if(fileContent.contains(id + ".location")) {
                    args = fileContent.getString(id + ".location", "").split(",");
                } else {
                    String basePath = id + ".position.";
                    args = new String[4];
                    args[0] = fileContent.getString(basePath + "world");
                    args[1] = fileContent.getString(basePath + "x");
                    args[2] = fileContent.getString(basePath + "y");
                    args[3] = fileContent.getString(basePath + "z");
                }
                World world = Bukkit.getWorld(args[0]);
                if(world == null) continue;

                List<String> rows = fileContent.getStringList(id + ".lines");
                List<String> removeContent = fileContent.getStringList(id + ".lines");
                for(String removeContentLine : removeContent) if(removeContentLine.equalsIgnoreCase("null")) rows.remove("null");

                SimpleLocation location = new SimpleLocation(world, Double.parseDouble(args[1]), Double.parseDouble(args[2]) - 0.51, Double.parseDouble(args[3]));
                GHolo holo = new GHolo(UUID.randomUUID(), id, location);
                gHoloMain.getHoloService().writeHolo(holo, override);

                for(String rowContent : rows) {
                    GHoloRow row = new GHoloRow(holo, rowContent);
                    gHoloMain.getHoloService().writeHoloRow(row, row.getPosition());
                }

                imported++;
            } catch(Throwable e) { e.printStackTrace(); }
        }

        return new GHoloImporterResult(true, imported);
    }

}