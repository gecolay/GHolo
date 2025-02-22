package dev.geco.gholo.object.interaction.importer.impl;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.object.interaction.importer.GInteractionImporter;
import dev.geco.gholo.object.interaction.importer.GInteractionImporterResult;
import org.jetbrains.annotations.NotNull;

public class FilesImporter extends GInteractionImporter {

    @Override
    public @NotNull String getType() { return "files"; }

    @Override
    public @NotNull GInteractionImporterResult importInteractions(@NotNull GHoloMain gHoloMain, boolean override) {
        int imported = 0;

        return new GInteractionImporterResult(true, imported);
    }

}