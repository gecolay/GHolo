package dev.geco.gholo.object.exporter.impl;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.object.GHolo;
import dev.geco.gholo.object.exporter.GHoloExporter;
import dev.geco.gholo.object.exporter.GHoloExporterResult;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

public class FilesExporter extends GHoloExporter {

    private final String FILE_FORMAT = ".yml";

    @Override
    public @NotNull String getType() { return "files"; }

    @Override
    public @NotNull GHoloExporterResult exportHolos(@NotNull GHoloMain gHoloMain, boolean override) {
        int exported = 0;

        List<GHolo> holos = gHoloMain.getHoloService().getHolos();
        if(holos.isEmpty()) return new GHoloExporterResult(true, exported);

        File holoDir = new File(gHoloMain.getDataFolder(), "holos");
        if(holoDir.exists()) holoDir.mkdir();

        for(GHolo holo : holos) {
            try {
                File holoFile = new File(holoDir.getPath(), holo.getId() + FILE_FORMAT);
                if(!override && holoFile.exists()) continue;
                holoFile.delete();
                getHoloFileStructure(holo).save(holoFile);
            } catch (Throwable e) { e.printStackTrace(); }
        }

        return new GHoloExporterResult(true, exported);
    }

    private static FileConfiguration getHoloFileStructure(GHolo holo) {
        FileConfiguration structure = new YamlConfiguration();
        structure.set("Holo.id", holo.getId());
        Location location = holo.getRawLocation();
        structure.set("Holo.location.x", location.getX());
        structure.set("Holo.location.y", location.getY());
        structure.set("Holo.location.z", location.getZ());
        structure.set("Holo.location.yaw", location.getYaw());
        structure.set("Holo.location.pitch", location.getPitch());

        return structure;
    }

}