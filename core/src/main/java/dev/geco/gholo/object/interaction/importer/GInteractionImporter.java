package dev.geco.gholo.object.interaction.importer;

import dev.geco.gholo.GHoloMain;
import org.jetbrains.annotations.NotNull;

public abstract class GInteractionImporter {

    abstract public @NotNull String getType();

    abstract public @NotNull GInteractionImporterResult importInteractions(@NotNull GHoloMain gHoloMain, boolean override);

}