package dev.geco.gholo.object.interaction.exporter.impl;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.object.interaction.exporter.GInteractionExporter;
import dev.geco.gholo.object.interaction.exporter.GInteractionExporterResult;
import org.jetbrains.annotations.NotNull;

public class FilesExporter extends GInteractionExporter {

    private final String FILE_FORMAT = ".yml";

    @Override
    public @NotNull String getType() { return "files"; }

    @Override
    public @NotNull GInteractionExporterResult exportInteractions(@NotNull GHoloMain gHoloMain, boolean override) {
        int exported = 0;

        return new GInteractionExporterResult(true, exported);
    }

}