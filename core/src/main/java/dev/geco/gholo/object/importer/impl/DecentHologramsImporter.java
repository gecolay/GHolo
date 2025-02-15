package dev.geco.gholo.object.importer.impl;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.object.GHolo;
import dev.geco.gholo.object.GHoloData;
import dev.geco.gholo.object.importer.GHoloImporter;
import dev.geco.gholo.object.importer.GHoloImporterResult;
import dev.geco.gholo.object.location.SimpleLocation;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class DecentHologramsImporter extends GHoloImporter {

    @Override
    public @NotNull String getType() { return "decent_holograms"; }

    @Override
    public @NotNull GHoloImporterResult importHolos(@NotNull GHoloMain gHoloMain, boolean override) {
        int imported = 0;

        File hologramsDir = new File("plugins/DecentHolograms/holograms");
        if(!hologramsDir.exists()) return new GHoloImporterResult(false, 0);

        for(File file : hologramsDir.listFiles()) {
            String name = file.getName().replace(".yml", "");
            if(gHoloMain.getHoloService().getHolo(name) != null) continue;

            FileConfiguration fileContent = YamlConfiguration.loadConfiguration(file);
            if(!fileContent.getBoolean("enabled", false)) continue;

            String[] args = fileContent.getString("location", "").split(":");
            World world = Bukkit.getWorld(args[0]);
            if(world == null) continue;
            SimpleLocation location = new SimpleLocation(world, Double.parseDouble(args[1].replace(",", ".")), Double.parseDouble(args[2].replace(",", ".")) - 0.41, Double.parseDouble(args[3].replace(",", ".")));

            GHolo holo = gHoloMain.getHoloService().createHolo(name, location);
            GHoloData data = holo.getData();

            double range = fileContent.getDouble("display-range", GHoloData.DEFAULT_RANGE);
            if(GHoloData.DEFAULT_RANGE != range) data.setRange(range);

            gHoloMain.getHoloService().updateHoloData(holo, data);

            for(Object section : fileContent.getList("pages")) {
                if(!(section instanceof LinkedHashMap<?,?>)) continue;

                Object lines = ((LinkedHashMap<?, ?>) section).get("lines");
                if(!(lines instanceof ArrayList)) continue;

                for(Object contentMap : (ArrayList<?>) lines) {
                    if(!(contentMap instanceof LinkedHashMap)) continue;

                    String content = (String) ((LinkedHashMap<?, ?>) contentMap).get("content");
                    gHoloMain.getHoloService().createHoloRow(holo, content);
                }
            }

            imported++;
        }

        return new GHoloImporterResult(true, imported);
    }

}