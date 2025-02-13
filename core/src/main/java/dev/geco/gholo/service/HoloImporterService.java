package dev.geco.gholo.service;

import dev.geco.gholo.object.importer.GHoloImporter;
import dev.geco.gholo.object.importer.impl.DecentHologramsImporter;
import dev.geco.gholo.object.importer.impl.FancyHologramsImporter;
import dev.geco.gholo.object.importer.impl.FilesImporter;
import dev.geco.gholo.object.importer.impl.HolographicDisplaysImporter;

import java.util.HashMap;

public class HoloImporterService {

    private final HashMap<String, GHoloImporter> holoImporters = new HashMap<>();

    public HashMap<String, GHoloImporter> getHoloImporters() { return holoImporters; }

    public void clearHoloImporters() { holoImporters.clear(); }

    public void registerDefaultHoloImports() {
        registerHoloImporter(new FilesImporter());
        registerHoloImporter(new DecentHologramsImporter());
        registerHoloImporter(new HolographicDisplaysImporter());
        registerHoloImporter(new FancyHologramsImporter());
    }

    public void registerHoloImporter(GHoloImporter holoImporter) { this.holoImporters.put(holoImporter.getType().toLowerCase(), holoImporter); }

}
