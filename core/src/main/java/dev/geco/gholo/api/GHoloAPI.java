package dev.geco.gholo.api;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.object.holo.GHolo;
import dev.geco.gholo.object.holo.GHoloData;
import dev.geco.gholo.object.holo.GHoloRow;
import dev.geco.gholo.object.interaction.GInteraction;
import dev.geco.gholo.object.interaction.GInteractionAction;
import dev.geco.gholo.object.interaction.GInteractionData;
import dev.geco.gholo.object.interaction.action.GInteractionActionType;
import dev.geco.gholo.object.simple.SimpleLocation;
import dev.geco.gholo.object.simple.SimpleOffset;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public class GHoloAPI {

    /**
     * Returns the plugin instance for GHolo
     * @author Gecolay
     * @since 2.0.0
     * @return Plugin instance
     */
    public static @NotNull GHoloMain getInstance() {
        return GHoloMain.getInstance();
    }

    /**
     * Gets all holos
     * @author Gecolay
     * @since 2.0.0
     * @return List of all holos
     */
    public static @NotNull Collection<GHolo> getHolos() {
        return getInstance().getHoloService().getHolos();
    }

    /**
     * Gets all near holos
     * @author Gecolay
     * @since 2.0.0
     * @param location Location
     * @param range Range
     * @return List of holos
     */
    public static @NotNull List<GHolo> getNearHolos(@NotNull Location location, double range) {
        return getInstance().getHoloService().getNearHolos(location, range);
    }

    /**
     * Gets a holo by id
     * @author Gecolay
     * @since 2.0.0
     * @param holoId Holo id
     * @return Holo or <code>null</code> if there was no holo
     */
    public static @Nullable GHolo getHolo(@NotNull String holoId) {
        return getInstance().getHoloService().getHolo(holoId);
    }

    /**
     * Creates a new holo
     * @author Gecolay
     * @since 2.0.0
     * @param holoId Holo id
     * @param location Location
     * @return Holo or <code>null</code> if the creation failed
     */
    public static @Nullable GHolo createHolo(@NotNull String holoId, @NotNull SimpleLocation location) {
        return getInstance().getHoloService().createHolo(holoId, location);
    }

    /**
     * Adds a new holo row for a holo
     * @author Gecolay
     * @since 2.0.0
     * @param holo Holo
     * @param content Content
     * @return Holo row or <code>null</code> if the creation failed
     */
    public static @Nullable GHoloRow addHoloRow(@NotNull GHolo holo, @NotNull String content) {
        return getInstance().getHoloService().addHoloRow(holo, content);
    }

    /**
     * Inserts a new holo row into a holo
     * @author Gecolay
     * @since 2.0.0
     * @param holo Holo
     * @param position Position (0-indexed)
     * @param content Content
     * @param updateOffset Should the offset of all rows with a higher position be updated
     * @return Holo row or <code>null</code> if the creation failed
     */
    public static @Nullable GHoloRow insertHoloRow(@NotNull GHolo holo, int position, @NotNull String content, boolean updateOffset) {
        return getInstance().getHoloService().insertHoloRow(holo, position, content, updateOffset);
    }

    /**
     * Updates the content of a holo row
     * @author Gecolay
     * @since 2.0.0
     * @param holoRow Holo row
     * @param content Content
     */
    public static void updateHoloRowContent(@NotNull GHoloRow holoRow, @NotNull String content) {
        getInstance().getHoloService().updateHoloRowContent(holoRow, content);
    }

    /**
     * Updates the offset of a holo row
     * @author Gecolay
     * @since 2.0.0
     * @param holoRow Holo row
     * @param offset Offset
     */
    public static void updateHoloRowOffset(@NotNull GHoloRow holoRow, @NotNull SimpleOffset offset) {
        getInstance().getHoloService().updateHoloRowOffset(holoRow, offset);
    }

    /**
     * Updates the data of a holo row
     * @author Gecolay
     * @since 2.0.0
     * @param holoRow Holo row
     * @param data Data
     */
    public static void updateHoloRowData(@NotNull GHoloRow holoRow, @NotNull GHoloData data) {
        getInstance().getHoloService().updateHoloRowData(holoRow, data);
    }

    /**
     * Removes a holo row from a holo
     * @author Gecolay
     * @since 2.0.0
     * @param holoRow Holo row
     * @param updateOffset Should the offset of all rows with a higher position be updated
     */
    public static void removeHoloRow(@NotNull GHoloRow holoRow, boolean updateOffset) {
        getInstance().getHoloService().removeHoloRow(holoRow, updateOffset);
    }

    /**
     * Updates the id of a holo
     * @author Gecolay
     * @since 2.0.0
     * @param holo Holo
     * @param holoId Holo id
     */
    public static void updateHoloId(@NotNull GHolo holo, @NotNull String holoId) {
        getInstance().getHoloService().updateHoloId(holo, holoId);
    }

    /**
     * Updates the location of a holo
     * @author Gecolay
     * @since 2.0.0
     * @param holo Holo
     * @param location Location
     */
    public static void updateHoloLocation(@NotNull GHolo holo, @NotNull SimpleLocation location) {
        getInstance().getHoloService().updateHoloLocation(holo, location);
    }

    /**
     * Updates the data of a holo
     * @author Gecolay
     * @since 2.0.0
     * @param holo Holo
     * @param data Data
     */
    public static void updateHoloData(@NotNull GHolo holo, @NotNull GHoloData data) {
        getInstance().getHoloService().updateHoloData(holo, data);
    }

    /**
     * Sets the content for all holo rows of a holo
     * @author Gecolay
     * @since 2.0.0
     * @param holo Holo
     * @param rows Rows
     */
    public static void setAllHoloRowContent(@NotNull GHolo holo, @NotNull List<String> rows) {
        getInstance().getHoloService().setAllHoloRowContent(holo, rows);
    }

    /**
     * Copies all rows of a holo to another holo
     * @author Gecolay
     * @since 2.0.0
     * @param holo Holo
     * @param holoId Holo id
     */
    public static void copyHolo(@NotNull GHolo holo, @NotNull String holoId) {
        getInstance().getHoloService().copyHolo(holo, holoId);
    }

    /**
     * Removes a holo
     * @author Gecolay
     * @since 2.0.0
     * @param holo Holo
     */
    public static void removeHolo(@NotNull GHolo holo) {
        getInstance().getHoloService().removeHolo(holo);
    }

    /**
     * Manually loads all holos in the world for a player
     * <p>
     * (Only use this if you have previously manually unloaded all holos for the player)
     * @author Gecolay
     * @since 2.0.0
     * @param player Player
     */
    public static void loadHolosForPlayer(@NotNull Player player) {
        getInstance().getHoloService().loadHolosForPlayer(player);
    }

    /**
     * Manually loads a holo on the server for all players
     * <p>
     * (Only use this if you have previously manually unloaded the holo for all players)
     * @author Gecolay
     * @since 2.0.0
     * @param holo Holo
     */
    public static void loadHolo(@NotNull GHolo holo) {
        getInstance().getHoloService().loadHolo(holo);
    }

    /**
     * Manually loads a holo in the world for a player
     * <p>
     * (Only use this if you have previously manually unloaded the holo for the player)
     * @author Gecolay
     * @since 2.0.0
     * @param holo Holo
     * @param player Player
     */
    public static void loadHoloForPlayer(@NotNull GHolo holo, @NotNull Player player) {
        getInstance().getHoloService().unloadHoloForPlayer(holo, player);
    }

    /**
     * Manually unloads all holos in the world for a player
     * @author Gecolay
     * @since 2.0.0
     * @param player Player
     */
    public static void unloadHolosForPlayer(@NotNull Player player) {
        getInstance().getHoloService().unloadHolosForPlayer(player);
    }

    /**
     * Manually unloads a holo on the server for all players
     * @author Gecolay
     * @since 2.0.0
     * @param holo Holo
     */
    public static void unloadHolo(@NotNull GHolo holo) {
        getInstance().getHoloService().unloadHolo(holo);
    }

    /**
     * Manually unloads a holo in the world for a player
     * @author Gecolay
     * @since 2.0.0
     * @param holo Holo
     * @param player Player
     */
    public static void unloadHoloForPlayer(@NotNull GHolo holo, @NotNull Player player) {
        getInstance().getHoloService().unloadHoloForPlayer(holo, player);
    }

    /**
     * Gets all interactions
     * @author Gecolay
     * @since 2.0.0
     * @return List of all interactions
     */
    public static @NotNull Collection<GInteraction> getInteractions() {
        return getInstance().getInteractionService().getInteractions();
    }

    /**
     * Gets all near interactions
     * @author Gecolay
     * @since 2.0.0
     * @param location Location
     * @param range Range
     * @return List of interactions
     */
    public static @NotNull List<GInteraction> getNearInteractions(@NotNull Location location, double range) {
        return getInstance().getInteractionService().getNearInteractions(location, range);
    }

    /**
     * Gets an interaction by id
     * @author Gecolay
     * @since 2.0.0
     * @param interactionId Interaction id
     * @return Interaction or <code>null</code> if there was no interaction
     */
    public static @Nullable GInteraction getInteraction(@NotNull String interactionId) {
        return getInstance().getInteractionService().getInteraction(interactionId);
    }

    /**
     * Creates a new interaction
     * @author Gecolay
     * @since 2.0.0
     * @param interactionId Interaction id
     * @param location Location
     * @return Interaction or <code>null</code> if the creation failed
     */
    public static @Nullable GInteraction createInteraction(@NotNull String interactionId, @NotNull SimpleLocation location) {
        return getInstance().getInteractionService().createInteraction(interactionId, location);
    }

    /**
     * Adds a new interaction action for an interaction
     * @author Gecolay
     * @since 2.0.0
     * @param interaction Interaction
     * @param interactionActionType Action type
     * @param parameter Action parameter
     * @return Interaction action or <code>null</code> if the creation failed
     */
    public static @Nullable GInteractionAction addInteractionAction(@NotNull GInteraction interaction, @NotNull GInteractionActionType interactionActionType, @NotNull String parameter) {
        return getInstance().getInteractionService().addInteractionAction(interaction, interactionActionType, parameter);
    }

    /**
     * Inserts a new interaction action into an interaction
     * @author Gecolay
     * @since 2.0.0
     * @param interaction Interaction
     * @param position Position (0-indexed)
     * @param interactionActionType Action type
     * @param parameter Action parameter
     * @return Interaction action or <code>null</code> if the creation failed
     */
    public static @Nullable GInteractionAction insertInteractionAction(@NotNull GInteraction interaction, int position, @NotNull GInteractionActionType interactionActionType, @NotNull String parameter) {
        return getInstance().getInteractionService().insertInteractionAction(interaction, position, interactionActionType, parameter);
    }

    /**
     * Updates the type and parameter of an interaction action
     * @author Gecolay
     * @since 2.0.0
     * @param interactionAction Interaction action
     * @param interactionActionType Action type
     * @param parameter Action parameter
     */
    public static void updateInteractionAction(@NotNull GInteractionAction interactionAction, @NotNull GInteractionActionType interactionActionType, @NotNull String parameter) {
        getInstance().getInteractionService().updateInteractionAction(interactionAction, interactionActionType, parameter);
    }

    /**
     * Removes an interaction action from an interaction
     * @author Gecolay
     * @since 2.0.0
     * @param interactionAction Interaction action
     */
    public static void removeInteractionAction(@NotNull GInteractionAction interactionAction) {
        getInstance().getInteractionService().removeInteractionAction(interactionAction);
    }

    /**
     * Updates the id of an interaction
     * @author Gecolay
     * @since 2.0.0
     * @param interaction Interaction
     * @param interactionId Interaction id
     */
    public static void updateInteractionId(@NotNull GInteraction interaction, @NotNull String interactionId) {
        getInstance().getInteractionService().updateInteractionId(interaction, interactionId);
    }

    /**
     * Updates the location of an interaction
     * @author Gecolay
     * @since 2.0.0
     * @param interaction Interaction
     * @param location Location
     */
    public static void updateInteractionLocation(@NotNull GInteraction interaction, @NotNull SimpleLocation location) {
        getInstance().getInteractionService().updateInteractionLocation(interaction, location);
    }

    /**
     * Updates the data of an interaction
     * @author Gecolay
     * @since 2.0.0
     * @param interaction Interaction
     * @param data Data
     */
    public static void updateInteractionData(@NotNull GInteraction interaction, @NotNull GInteractionData data) {
        getInstance().getInteractionService().updateInteractionData(interaction, data);
    }

    /**
     * Copies all rows of an interaction to another interaction
     * @author Gecolay
     * @since 2.0.0
     * @param interaction Interaction
     * @param interactionId Interaction id
     */
    public static void copyInteraction(@NotNull GInteraction interaction, @NotNull String interactionId) {
        getInstance().getInteractionService().copyInteraction(interaction, interactionId);
    }

    /**
     * Removes an interaction
     * @author Gecolay
     * @since 2.0.0
     * @param interaction Interaction
     */
    public static void removeInteraction(@NotNull GInteraction interaction) {
        getInstance().getInteractionService().removeInteraction(interaction);
    }

    /**
     * Manually loads all interactions in the world for a player
     * <p>
     * (Only use this if you have previously manually unloaded all interactions for the player)
     * @author Gecolay
     * @since 2.0.0
     * @param player Player
     */
    public static void loadInteractionsForPlayer(@NotNull Player player) {
        getInstance().getInteractionService().loadInteractionsForPlayer(player);
    }

    /**
     * Manually loads an interaction on the server for all players
     * <p>
     * (Only use this if you have previously manually unloaded the interaction for all players)
     * @author Gecolay
     * @since 2.0.0
     * @param interaction Interaction
     */
    public static void loadInteraction(@NotNull GInteraction interaction) {
        getInstance().getInteractionService().loadInteraction(interaction);
    }

    /**
     * Manually loads an interaction in the world for a player
     * <p>
     * (Only use this if you have previously manually unloaded the interaction for the player)
     * @author Gecolay
     * @since 2.0.0
     * @param interaction Interaction
     * @param player Player
     */
    public static void loadInteractionForPlayer(@NotNull GInteraction interaction, @NotNull Player player) {
        getInstance().getInteractionService().unloadInteractionForPlayer(interaction, player);
    }

    /**
     * Manually unloads all interactions in the world for a player
     * @author Gecolay
     * @since 2.0.0
     * @param player Player
     */
    public static void unloadInteractionsForPlayer(@NotNull Player player) {
        getInstance().getInteractionService().unloadInteractionsForPlayer(player);
    }

    /**
     * Manually unloads an interaction on the server for all players
     * @author Gecolay
     * @since 2.0.0
     * @param interaction Interaction
     */
    public static void unloadInteraction(@NotNull GInteraction interaction) {
        getInstance().getInteractionService().unloadInteraction(interaction);
    }

    /**
     * Manually unloads an interaction in the world for a player
     * @author Gecolay
     * @since 2.0.0
     * @param interaction Interaction
     * @param player Player
     */
    public static void unloadInteractionForPlayer(@NotNull GInteraction interaction, @NotNull Player player) {
        getInstance().getInteractionService().unloadInteractionForPlayer(interaction, player);
    }

}