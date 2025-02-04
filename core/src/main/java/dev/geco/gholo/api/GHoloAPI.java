package dev.geco.gholo.api;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.object.GHolo;
import dev.geco.gholo.object.GHoloData;
import dev.geco.gholo.object.GHoloRow;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    public static @NotNull List<GHolo> getHolos() {
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
    public static @Nullable GHolo createHolo(@NotNull String holoId, @NotNull Location location) {
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
     * @param rowId Row id (0-indexed)
     * @param content Content
     * @param updateOffset Should the offset of all rows with a higher row id be updated
     * @return Holo row or <code>null</code> if the creation failed
     */
    public static GHoloRow insertHoloRow(@NotNull GHolo holo, int rowId, @NotNull String content, boolean updateOffset) {
        return getInstance().getHoloService().insertHoloRow(holo, rowId, content, updateOffset);
    }

    //TODO

    /**
     * Updates the content of a holo row
     * @author Gecolay
     * @since 1.0.0
     * @param HoloRow HoloRow
     * @param Content HoloRow-Content
     */
    public static void updateHoloRowContent(@NotNull GHoloRow HoloRow, @NotNull String Content) {
        getInstance().getHoloService().updateHoloRowContent(HoloRow, Content);
    }

    /**
     * Updates the position of a holo row
     * <p>
     * (This is not the direct location of a holo row, but the offset to the holo location)
     * @author Gecolay
     * @since 1.0.0
     * @param HoloRow HoloRow
     * @param Position HoloRow-Position
     */
    public static void updateHoloRowPosition(@NotNull GHoloRow HoloRow, @NotNull Location Position) {
        getInstance().getHoloService().updateHoloRowPosition(HoloRow, Position);
    }

    /**
     * Updates the data of a holo row
     * @author Gecolay
     * @since 1.0.0
     * @param HoloRow HoloRow
     * @param Data HoloRow-Data
     */
    public static void updateHoloRowData(@NotNull GHoloRow HoloRow, @NotNull GHoloData Data) {
        getInstance().getHoloService().updateHoloRowData(HoloRow, Data);
    }

    /**
     * Removes a holo row from a holo
     * @author Gecolay
     * @since 1.0.0
     * @param HoloRow HoloRow
     * @param UpdateOffset Should the offset of all rows with a higher row number be updated to move them down
     */
    public static void removeHoloRow(@NotNull GHoloRow HoloRow, boolean UpdateOffset) {
        getInstance().getHoloService().removeHoloRow(HoloRow, UpdateOffset);
    }

    /**
     * Updates the id of a holo
     * @author Gecolay
     * @since 1.0.0
     * @param Holo Holo
     * @param Id Holo-Id
     */
    public static void updateHoloId(@NotNull GHolo Holo, @NotNull String Id) {
        getInstance().getHoloService().updateHoloId(Holo, Id);
    }

    /**
     * Updates the location of a holo
     * @author Gecolay
     * @since 1.0.0
     * @param Holo Holo
     * @param Location Holo-Location
     */
    public static void updateHoloLocation(@NotNull GHolo Holo, @NotNull Location Location) {
        getInstance().getHoloService().updateHoloLocation(Holo, Location);
    }

    /**
     * Updates the default data of a holo
     * @author Gecolay
     * @since 1.0.0
     * @param Holo Holo
     * @param Data Default Holo-Data
     */
    public static void updateHoloData(@NotNull GHolo Holo, @NotNull GHoloData Data) {
        getInstance().getHoloService().updateHoloData(Holo, Data);
    }

    /**
     * Sets all new rows of text for a holo
     * @author Gecolay
     * @since 1.0.0
     * @param Holo Holo
     * @param Rows Text-Rows
     */
    public static void setHoloRows(@NotNull GHolo Holo, @NotNull List<String> Rows) {
        getInstance().getHoloService().setHoloRows(Holo, Rows);
    }

    /**
     * Copies all rows of a holo to another holo
     * @author Gecolay
     * @since 1.0.0
     * @param Holo Holo
     * @param CopyToHolo CopyToHolo
     */
    public static void copyHoloRows(@NotNull GHolo Holo, @NotNull GHolo CopyToHolo) {
        getInstance().getHoloService().copyHoloRows(Holo, CopyToHolo);
    }

    /**
     * Removes a holo
     * <p>
     * (Removing a holo will automatically unload it)
     * @author Gecolay
     * @since 1.0.0
     * @param Holo Holo
     */
    public static void removeHolo(@NotNull GHolo Holo) {
        getInstance().getHoloService().removeHolo(Holo);
    }

    /**
     * Manually loads all holos in the world for a player
     * <p>
     * (Only use this if you have previously unloaded all holos for the player, as all holos are already loaded automatically on creation ({@link #createHolo(String, Location)}) or on server join for a player.)
     * @author Gecolay
     * @since 1.0.0
     * @param Player Player
     */
    public static void loadHolosForPlayer(@NotNull Player Player) {
        getInstance().getHoloService().loadHolosForPlayer(Player);
    }

    /**
     * Manually loads a holo on the server for all players
     * <p>
     * (Only use this if you have previously unloaded the holo for all players, as the holo is already loaded automatically on creation ({@link #createHolo(String, Location)}) or on server join for all players.)
     * @author Gecolay
     * @since 1.0.0
     * @param Holo Holo
     */
    public static void loadHolo(@NotNull GHolo Holo) {
        getInstance().getHoloService().loadHolo(Holo);
    }

    /**
     * Manually loads a holo in the world for a player
     * <p>
     * (Only use this if you have previously unloaded the holo for the player, as the holo is already loaded automatically on creation ({@link #createHolo(String, Location)}) or on server join for a player.)
     * @author Gecolay
     * @since 1.0.0
     * @param Holo Holo
     * @param Player Player
     */
    public static void loadHoloForPlayer(@NotNull GHolo Holo, @NotNull Player Player) {
        getInstance().getHoloService().unloadHoloForPlayer(Holo, Player);
    }

    /**
     * Manually unloads a holo on the server for all players
     * @author Gecolay
     * @since 1.0.0
     * @param Holo Holo
     */
    public static void unloadHolo(@NotNull GHolo Holo) {
        getInstance().getHoloService().unloadHolo(Holo);
    }

    /**
     * Manually unloads a holo in the world for a player
     * @author Gecolay
     * @since 1.0.0
     * @param Holo Holo
     * @param Player Player
     */
    public static void unloadHoloForPlayer(@NotNull GHolo Holo, @NotNull Player Player) {
        getInstance().getHoloService().unloadHoloForPlayer(Holo, Player);
    }

}