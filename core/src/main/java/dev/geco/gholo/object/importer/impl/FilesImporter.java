package dev.geco.gholo.object.importer.impl;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.object.importer.GHoloImporter;
import dev.geco.gholo.object.importer.GHoloImporterResult;
import org.jetbrains.annotations.NotNull;

public class FilesImporter extends GHoloImporter {

    @Override
    public @NotNull String getType() { return "files"; }

    @Override
    public @NotNull GHoloImporterResult importHolos(@NotNull GHoloMain gHoloMain) {
        int imported = 0;

        return new GHoloImporterResult(true, imported);
    }

}