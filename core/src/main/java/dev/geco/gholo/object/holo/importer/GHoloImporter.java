package dev.geco.gholo.object.holo.importer;

import dev.geco.gholo.GHoloMain;
import org.jetbrains.annotations.NotNull;

public abstract class GHoloImporter {

    abstract public @NotNull String getType();

    abstract public @NotNull GHoloImporterResult importHolos(@NotNull GHoloMain gHoloMain, boolean override);

}