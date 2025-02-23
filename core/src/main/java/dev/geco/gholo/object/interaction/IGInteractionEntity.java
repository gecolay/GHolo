package dev.geco.gholo.object.interaction;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface IGInteractionEntity {

    int getId();

    void loadInteraction();

    void loadInteraction(@NotNull Player player);

    void publishUpdate(@NotNull GInteractionUpdateType updateType);

    void unloadInteraction();

    void unloadInteraction(@NotNull Player player);

}