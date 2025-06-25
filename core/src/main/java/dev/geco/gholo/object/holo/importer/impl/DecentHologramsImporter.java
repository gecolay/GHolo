package dev.geco.gholo.object.holo.importer.impl;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.object.holo.GHolo;
import dev.geco.gholo.object.holo.GHoloData;
import dev.geco.gholo.object.holo.GHoloRow;
import dev.geco.gholo.object.holo.importer.GHoloImporter;
import dev.geco.gholo.object.holo.importer.GHoloImporterResult;
import dev.geco.gholo.object.simple.SimpleLocation;
import dev.geco.gholo.object.simple.SimpleOffset;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.UUID;
import java.util.logging.Level;

public class DecentHologramsImporter extends GHoloImporter {

    @Override
    public @NotNull String getType() { return "decent_holograms"; }

    @Override
    public @NotNull GHoloImporterResult importHolos(@NotNull GHoloMain gHoloMain, boolean override) {
        int imported = 0;

        File hologramsDir = new File("plugins/DecentHolograms/holograms");
        if(!hologramsDir.exists()) return new GHoloImporterResult(true, 0);

        for(File file : hologramsDir.listFiles()) {
            String id = file.getName().replace(" ", "").replace(".yml", "");
            try {
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

                double offset = 0;

                gHoloMain.getHoloService().writeHolo(holo, override);
                for(GHoloRow row : holo.getRows()) {
                    row.setOffset(new SimpleOffset(0, offset, 0));
                    gHoloMain.getHoloService().writeHoloRow(row, row.getPosition());
                    offset -= gHoloMain.getConfigService().DEFAULT_SIZE_BETWEEN_ROWS;
                }

                imported++;
            } catch(Throwable e) { gHoloMain.getLogger().log(Level.SEVERE, "Could not import holo '" + id + "'!", e); }
        }

        return new GHoloImporterResult(true, imported);
    }

}