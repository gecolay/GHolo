package dev.geco.gholo.object.interaction.exporter;

import dev.geco.gholo.GHoloMain;
import org.jetbrains.annotations.NotNull;

public abstract class GInteractionExporter {

    abstract public @NotNull String getType();

    abstract public @NotNull GInteractionExporterResult exportInteractions(@NotNull GHoloMain gHoloMain, boolean override);

}