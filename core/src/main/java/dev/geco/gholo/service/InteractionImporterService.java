package dev.geco.gholo.service;

import dev.geco.gholo.object.interaction.importer.GInteractionImporter;
import dev.geco.gholo.object.interaction.importer.impl.FilesImporter;

import java.util.HashMap;

public class InteractionImporterService {

    private final HashMap<String, GInteractionImporter> interactionImporters = new HashMap<>();

    public HashMap<String, GInteractionImporter> getInteractionImporters() { return interactionImporters; }

    public GInteractionImporter getInteractionImporter(String type) { return interactionImporters.get(type.toLowerCase()); }

    public void registerInteractionImporter(GInteractionImporter interactionImporter) { this.interactionImporters.put(interactionImporter.getType().toLowerCase(), interactionImporter); }

    public void registerDefaultInteractionImporters() {
        registerInteractionImporter(new FilesImporter());
    }

    public void unregisterInteractionImporters() { interactionImporters.clear(); }

}