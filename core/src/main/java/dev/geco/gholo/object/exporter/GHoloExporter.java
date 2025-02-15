package dev.geco.gholo.object.exporter;

import dev.geco.gholo.GHoloMain;
import org.jetbrains.annotations.NotNull;

public abstract class GHoloExporter {

    abstract public @NotNull String getType();

    abstract public @NotNull GHoloExporterResult exportHolos(@NotNull GHoloMain gHoloMain, boolean override);

}