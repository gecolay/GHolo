package dev.geco.gholo.object.holo.importer.impl;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.object.holo.GHolo;
import dev.geco.gholo.object.holo.GHoloData;
import dev.geco.gholo.object.holo.GHoloRow;
import dev.geco.gholo.object.holo.importer.GHoloImporter;
import dev.geco.gholo.object.holo.importer.GHoloImporterResult;
import dev.geco.gholo.object.simple.SimpleLocation;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.UUID;

public class DecentHologramsImporter extends GHoloImporter {

    @Override
    public @NotNull String getType() { return "decent_holograms"; }

    @Override
    public @NotNull GHoloImporterResult importHolos(@NotNull GHoloMain gHoloMain, boolean override) {
        int imported = 0;

        File hologramsDir = new File("plugins/DecentHolograms/holograms");
        if(!hologramsDir.exists()) return new GHoloImporterResult(false, 0);

        for(File file : hologramsDir.listFiles()) {
            try {
                String id = file.getName().replace(" ", "").replace(".yml", "");
                if(!override && gHoloMain.getHoloService().getHolo(id) != null) continue;

                FileConfiguration fileContent = YamlConfiguration.loadConfiguration(file);

                String[] args = fileContent.getString("location", "").split(":");
                World world = Bukkit.getWorld(args[0]);
                if(world == null) throw new RuntimeException("Can not import holo with id '" + id + "', because the world is invalid!");
                SimpleLocation location = new SimpleLocation(world, Double.parseDouble(args[1].replace(",", ".")), Double.parseDouble(args[2].replace(",", ".")) - 0.41, Double.parseDouble(args[3].replace(",", ".")));

                GHolo holo = new GHolo(UUID.randomUUID(), id, location);
                GHoloData data = holo.getRawData();

                double range = fileContent.getDouble("display-range", GHoloData.DEFAULT_RANGE);
                if(GHoloData.DEFAULT_RANGE != range) data.setRange(range);

                for(Object section : fileContent.getList("pages")) {
                    if(!(section instanceof LinkedHashMap<?,?>)) continue;

                    Object lines = ((LinkedHashMap<?, ?>) section).get("lines");
                    if(!(lines instanceof ArrayList)) continue;

                    for(Object contentMap : (ArrayList<?>) lines) {
                        if(!(contentMap instanceof LinkedHashMap)) continue;

                        String content = (String) ((LinkedHashMap<?, ?>) contentMap).get("content");
                        holo.addRow(new GHoloRow(holo, content));
                    }
                }

                gHoloMain.getHoloService().writeHolo(holo, override);
                for(GHoloRow row : holo.getRows()) gHoloMain.getHoloService().writeHoloRow(row, row.getPosition());

                imported++;
            } catch(Throwable e) { e.printStackTrace(); }
        }

        return new GHoloImporterResult(true, imported);
    }

}