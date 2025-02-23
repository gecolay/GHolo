package dev.geco.gholo.service;

import dev.geco.gholo.object.interaction.exporter.GInteractionExporter;
import dev.geco.gholo.object.interaction.exporter.impl.FilesExporter;

import java.util.HashMap;

public class InteractionExporterService {

    private final HashMap<String, GInteractionExporter> interactionExporters = new HashMap<>();

    public HashMap<String, GInteractionExporter> getInteractionExporters() { return interactionExporters; }

    public GInteractionExporter getInteractionExporter(String type) { return interactionExporters.get(type.toLowerCase()); }

    public void registerInteractionExporter(GInteractionExporter interactionExporter) { this.interactionExporters.put(interactionExporter.getType().toLowerCase(), interactionExporter); }

    public void registerDefaultInteractionExporters() {
        registerInteractionExporter(new FilesExporter());
    }

    public void unregisterInteractionExporters() { interactionExporters.clear(); }

}