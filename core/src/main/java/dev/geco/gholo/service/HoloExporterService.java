package dev.geco.gholo.service;

import dev.geco.gholo.object.holo.exporter.GHoloExporter;
import dev.geco.gholo.object.holo.exporter.impl.FilesExporter;

import java.util.HashMap;

public class HoloExporterService {

    private final HashMap<String, GHoloExporter> holoExporters = new HashMap<>();

    public HashMap<String, GHoloExporter> getHoloExporters() { return holoExporters; }

    public GHoloExporter getHoloExporter(String type) { return holoExporters.get(type.toLowerCase()); }

    public void registerHoloExporter(GHoloExporter holoExporter) { this.holoExporters.put(holoExporter.getType().toLowerCase(), holoExporter); }

    public void registerDefaultHoloExporters() {
        registerHoloExporter(new FilesExporter());
    }

    public void unregisterHoloExporters() { holoExporters.clear(); }

}