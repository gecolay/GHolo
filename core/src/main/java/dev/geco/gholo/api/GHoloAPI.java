package dev.geco.gholo.api;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.object.GHolo;
import dev.geco.gholo.object.GHoloData;
import dev.geco.gholo.object.GHoloRow;
import dev.geco.gholo.object.location.SimpleLocation;
import dev.geco.gholo.object.location.SimpleOffset;
import dev.geco.gholo.object.location.SimpleRotation;
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
     * @since 1.0.0
     * @return Plugin instance
     */
    public static @NotNull GHoloMain getInstance() {
        return GHoloMain.getInstance();
    }

    /**
     * Gets all holos
     * @author Gecolay
     * @since 1.0.0
     * @return List of all holos
     */
    public static @NotNull Collection<GHolo> getHolos() {
        return getInstance().getHoloService().getHolos();
    }

    /**
     * Gets all near holos
     * @author Gecolay
     * @since 1.1.1
     * @param location Location
     * @param range Range
     * @return List of holos
     */
    public static @NotNull List<GHolo> getNearHolos(@NotNull Location location, double range) {
        return getInstance().getHoloService().getNearHolos(location, range);
    }

    /**
     * Checks if a holo exists
     * @author Gecolay
     * @since 1.0.0
     * @param holoId Holo id
     * @return <code>true</code> if the holo exists, <code>false</code> if not
     */
    public static boolean checkHolo(@NotNull String holoId) {
        return getInstance().getHoloService().getHolo(holoId) != null;
    }

    /**
     * Gets a holo by id
     * @author Gecolay
     * @since 1.0.0
     * @param holoId Holo id
     * @return Holo or <code>null</code> if there was no holo
     */
    public static @Nullable GHolo getHolo(@NotNull String holoId) {
        return getInstance().getHoloService().getHolo(holoId);
    }

    /**
     * Creates a new holo
     * @author Gecolay
     * @since 1.0.0
     * @param holoId Holo id
     * @param location Location
     * @return Holo or <code>null</code> if the creation failed
     */
    public static @Nullable GHolo createHolo(@NotNull String holoId, @NotNull SimpleLocation location) {
        return getInstance().getHoloService().createHolo(holoId, location);
    }

    /**
     * Creates a new holo row for a holo
     * @author Gecolay
     * @since 1.0.0
     * @param holo Holo
     * @param content Content
     * @return Holo row or <code>null</code> if the creation failed
     */
    public static @Nullable GHoloRow createHoloRow(@NotNull GHolo holo, @NotNull String content) {
        return getInstance().getHoloService().createHoloRow(holo, content);
    }

    /**
     * Inserts a new holo row into a holo
     * @author Gecolay
     * @since 1.0.0
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
     * @since 1.0.0
     * @param holoRow HoloRow
     * @param content Content
     */
    public static void updateHoloRowContent(@NotNull GHoloRow holoRow, @NotNull String content) {
        getInstance().getHoloService().updateHoloRowContent(holoRow, content);
    }

    /**
     * Updates the position of a holo row
     * <p>
     * (This is not the direct location of a holo row, but the offset to the holo location)
     * @author Gecolay
     * @since 1.0.0
     * @param holoRow HoloRow
     * @param offset Offset
     */
    public static void updateHoloRowPosition(@NotNull GHoloRow holoRow, @NotNull SimpleOffset offset) {
        getInstance().getHoloService().updateHoloRowOffset(holoRow, offset);
    }

    /**
     * Updates the data of a holo row
     * @author Gecolay
     * @since 1.0.0
     * @param holoRow HoloRow
     * @param data Data
     */
    public static void updateHoloRowData(@NotNull GHoloRow holoRow, @NotNull GHoloData data) {
        getInstance().getHoloService().updateHoloRowData(holoRow, data);
    }

    /**
     * Removes a holo row from a holo
     * @author Gecolay
     * @since 1.0.0
     * @param holoRow HoloRow
     * @param updateOffset Should the offset of all rows with a higher row id be updated
     */
    public static void removeHoloRow(@NotNull GHoloRow holoRow, boolean updateOffset) {
        getInstance().getHoloService().removeHoloRow(holoRow, updateOffset);
    }

    /**
     * Updates the id of a holo
     * @author Gecolay
     * @since 1.0.0
     * @param holo Holo
     * @param holoId Holo id
     */
    public static void updateHoloId(@NotNull GHolo holo, @NotNull String holoId) {
        getInstance().getHoloService().updateHoloId(holo, holoId);
    }

    /**
     * Updates the location of a holo
     * @author Gecolay
     * @since 1.0.0
     * @param holo Holo
     * @param location Location
     */
    public static void updateHoloLocation(@NotNull GHolo holo, @NotNull SimpleLocation location) {
        getInstance().getHoloService().updateHoloLocation(holo, location);
    }

    /**
     * Updates the default data of a holo
     * @author Gecolay
     * @since 1.0.0
     * @param holo Holo
     * @param data Default data
     */
    public static void updateHoloData(@NotNull GHolo holo, @NotNull GHoloData data) {
        getInstance().getHoloService().updateHoloData(holo, data);
    }

    /**
     * Sets all new rows of text for a holo
     * @author Gecolay
     * @since 1.0.0
     * @param holo Holo
     * @param rows Text-Rows
     */
    public static void setHoloRows(@NotNull GHolo holo, @NotNull List<String> rows) {
        getInstance().getHoloService().setAllHoloRowContent(holo, rows);
    }

    /**
     * Copies all rows of a holo to another holo
     * @author Gecolay
     * @since 1.0.0
     * @param holo Holo
     * @param copyToHolo CopyToHolo
     */
    public static void copyHoloRows(@NotNull GHolo holo, @NotNull GHolo copyToHolo) {
        getInstance().getHoloService().copyAllHoloRowContent(holo, copyToHolo);
    }

    /**
     * Removes a holo
     * <p>
     * (Removing a holo will automatically unload it)
     * @author Gecolay
     * @since 1.0.0
     * @param holo Holo
     */
    public static void removeHolo(@NotNull GHolo holo) {
        getInstance().getHoloService().removeHolo(holo);
    }

    /**
     * Manually loads all holos in the world for a player
     * <p>
     * (Only use this if you have previously unloaded all holos for the player, as all holos are already loaded automatically on creation ({@link #createHolo(String, SimpleLocation)}) or on server join for a player.)
     * @author Gecolay
     * @since 1.0.0
     * @param player Player
     */
    public static void loadHolosForPlayer(@NotNull Player player) {
        getInstance().getHoloService().loadHolosForPlayer(player);
    }

    /**
     * Manually loads a holo on the server for all players
     * <p>
     * (Only use this if you have previously unloaded the holo for all players, as the holo is already loaded automatically on creation ({@link #createHolo(String, SimpleLocation)}) or on server join for all players.)
     * @author Gecolay
     * @since 1.0.0
     * @param holo Holo
     */
    public static void loadHolo(@NotNull GHolo holo) {
        getInstance().getHoloService().loadHolo(holo);
    }

    /**
     * Manually loads a holo in the world for a player
     * <p>
     * (Only use this if you have previously unloaded the holo for the player, as the holo is already loaded automatically on creation ({@link #createHolo(String, SimpleLocation)}) or on server join for a player.)
     * @author Gecolay
     * @since 1.0.0
     * @param holo Holo
     * @param player Player
     */
    public static void loadHoloForPlayer(@NotNull GHolo holo, @NotNull Player player) {
        getInstance().getHoloService().unloadHoloForPlayer(holo, player);
    }

    /**
     * Manually unloads a holo on the server for all players
     * @author Gecolay
     * @since 1.0.0
     * @param holo Holo
     */
    public static void unloadHolo(@NotNull GHolo holo) {
        getInstance().getHoloService().unloadHolo(holo);
    }

    /**
     * Manually unloads a holo in the world for a player
     * @author Gecolay
     * @since 1.0.0
     * @param holo Holo
     * @param player Player
     */
    public static void unloadHoloForPlayer(@NotNull GHolo holo, @NotNull Player player) {
        getInstance().getHoloService().unloadHoloForPlayer(holo, player);
    }

}