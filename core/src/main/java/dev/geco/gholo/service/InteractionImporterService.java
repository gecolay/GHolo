package dev.geco.gholo.service;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.object.interaction.importer.GInteractionImporter;
import dev.geco.gholo.object.interaction.importer.impl.FilesImporter;

import java.io.File;
import java.util.HashMap;

public class InteractionImporterService {

    private final GHoloMain gHoloMain;
    private final HashMap<String, GInteractionImporter> interactionImporters = new HashMap<>();

    public InteractionImporterService(GHoloMain gHoloMain) {
        this.gHoloMain = gHoloMain;
    }

    public HashMap<String, GInteractionImporter> getInteractionImporters() { return interactionImporters; }

    public GInteractionImporter getInteractionImporter(String type) { return interactionImporters.get(type.toLowerCase()); }

    public void registerInteractionImporter(GInteractionImporter interactionImporter) { this.interactionImporters.put(interactionImporter.getType().toLowerCase(), interactionImporter); }

    public void registerDefaultInteractionImporters() {
        File interactionFileDir = new File(gHoloMain.getDataFolder(), "interactions");
        if(!interactionFileDir.exists()) interactionFileDir.mkdir();

        registerInteractionImporter(new FilesImporter());
    }

    public void unregisterInteractionImporters() { interactionImporters.clear(); }

}