package dev.geco.gholo.object.importer.impl;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.object.GHolo;
import dev.geco.gholo.object.importer.GHoloImporter;
import dev.geco.gholo.object.importer.GHoloImporterResult;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

public class HolographicDisplaysImporter extends GHoloImporter {

    @Override
    public @NotNull String getType() { return "holographic_displays"; }

    @Override
    public @NotNull GHoloImporterResult importHolos(@NotNull GHoloMain gHoloMain) {
        int imported = 0;

        File contentFile = new File("plugins/HolographicDisplays/database.yml");
        if(!contentFile.exists()) return new GHoloImporterResult(false, 0);

        FileConfiguration fileContent = YamlConfiguration.loadConfiguration(contentFile);
        for(String line : fileContent.getConfigurationSection("").getKeys(false)) {
            if(gHoloMain.getHoloService().getHolo(line) != null) continue;

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

            GHolo holo = gHoloMain.getHoloService().createHolo(line, new Location(world, Double.parseDouble(args[1]), Double.parseDouble(args[2]) - 0.51, Double.parseDouble(args[3])));

            for(String row : rows) gHoloMain.getHoloService().createHoloRow(holo, row);

            imported++;
        }

        return new GHoloImporterResult(true, imported);
    }

}