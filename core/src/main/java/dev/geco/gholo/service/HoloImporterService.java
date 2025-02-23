package dev.geco.gholo.service;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.object.holo.importer.GHoloImporter;
import dev.geco.gholo.object.holo.importer.impl.DecentHologramsImporter;
import dev.geco.gholo.object.holo.importer.impl.FancyHologramsImporter;
import dev.geco.gholo.object.holo.importer.impl.FilesImporter;
import dev.geco.gholo.object.holo.importer.impl.HolographicDisplaysImporter;
import dev.geco.gholo.object.holo.importer.impl.DatabaseV1Importer;

import java.io.File;
import java.util.HashMap;

public class HoloImporterService {

    private final GHoloMain gHoloMain;
    private final HashMap<String, GHoloImporter> holoImporters = new HashMap<>();

    public HoloImporterService(GHoloMain gHoloMain) {
        this.gHoloMain = gHoloMain;
    }

    public HashMap<String, GHoloImporter> getHoloImporters() { return holoImporters; }

    public GHoloImporter getHoloImporter(String type) { return holoImporters.get(type.toLowerCase()); }

    public void registerHoloImporter(GHoloImporter holoImporter) { this.holoImporters.put(holoImporter.getType().toLowerCase(), holoImporter); }

    public void registerDefaultHoloImporters() {
        File holoFileDir = new File(gHoloMain.getDataFolder(), "holos");
        if(!holoFileDir.exists()) holoFileDir.mkdir();

        registerHoloImporter(new FilesImporter());
        registerHoloImporter(new DecentHologramsImporter());
        registerHoloImporter(new HolographicDisplaysImporter());
        registerHoloImporter(new FancyHologramsImporter());
        registerHoloImporter(new DatabaseV1Importer());
    }

    public void unregisterHoloImporters() { holoImporters.clear(); }

}