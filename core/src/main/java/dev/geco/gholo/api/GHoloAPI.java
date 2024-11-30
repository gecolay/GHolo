package dev.geco.gholo.api;

import java.util.*;

import org.jetbrains.annotations.*;

import org.bukkit.*;
import org.bukkit.entity.*;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.objects.*;

public class GHoloAPI {

    /**
     * Returns the Plugin-Instance for GHolo
     * @author Gecolay
     * @since 1.0.0
     * @return Plugin-Instance
     */
    public static GHoloMain getInstance() { return GHoloMain.getInstance(); }

    /**
     * Returns all available holos
     * @author Gecolay
     * @since 1.0.0
     * @return Holos
     */
    public static List<GHolo> getHolos() {
        return getInstance().getHoloManager().getHolos();
    }

    /**
     * Checks if a holo is present
     * @author Gecolay
     * @since 1.0.0
     * @param Id Holo-Id
     * @return <code>true</code> if the holo exists, <code>false</code> if not
     */
    public static boolean checkHolo(String Id) {
        return getInstance().getHoloManager().getHolo(Id) != null;
    }

    /**
     * Gets a holo by id
     * @author Gecolay
     * @since 1.0.0
     * @param Id Holo-Id
     * @return Holo
     */
    public static GHolo getHolo(String Id) {
        return getInstance().getHoloManager().getHolo(Id);
    }

    /**
     * Creates a new holo
     * @author Gecolay
     * @since 1.0.0
     * @param Id Holo-Id
     * @param Location Holo-Location
     * @return New holo
     */
    public static GHolo createHolo(String Id, Location Location) {
        return getInstance().getHoloManager().createHolo(Id, Location);
    }

    /**
     * Creates a new holo row for a holo
     * @author Gecolay
     * @since 1.0.0
     * @param Holo Holo
     * @param Content Content
     * @return New added holo row
     */
    public static GHoloRow createHoloRow(@NotNull GHolo Holo, @NotNull String Content) {
        return getInstance().getHoloManager().createHoloRow(Holo, Content);
    }

    /**
     * Inserts a new holo row into a holo
     * @author Gecolay
     * @since 1.0.0
     * @param Holo Holo
     * @param Row Row-Number (0-indexed)
     * @param Content Content
     * @param UpdateOffset Should the offset of all rows with a higher row number be updated to move them down
     * @return New inserted holo row
     */
    public static GHoloRow insertHoloRow(@NotNull GHolo Holo, int Row, @NotNull String Content, boolean UpdateOffset) {
        return getInstance().getHoloManager().insertHoloRow(Holo, Row, Content, UpdateOffset);
    }

    /**
     * Updates the content of a holo row
     * @author Gecolay
     * @since 1.0.0
     * @param HoloRow HoloRow
     * @param Content HoloRow-Content
     */
    public static void updateHoloRowContent(@NotNull GHoloRow HoloRow, @NotNull String Content) {
        getInstance().getHoloManager().updateHoloRowContent(HoloRow, Content);
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
        getInstance().getHoloManager().updateHoloRowPosition(HoloRow, Position);
    }

    /**
     * Updates the data of a holo row
     * @author Gecolay
     * @since 1.0.0
     * @param HoloRow HoloRow
     * @param Data HoloRow-Data
     */
    public static void updateHoloRowData(@NotNull GHoloRow HoloRow, @NotNull GHoloData Data) {
        getInstance().getHoloManager().updateHoloRowData(HoloRow, Data);
    }

    /**
     * Removes a holo row from a holo
     * @author Gecolay
     * @since 1.0.0
     * @param HoloRow HoloRow
     * @param UpdateOffset Should the offset of all rows with a higher row number be updated to move them down
     */
    public static void removeHoloRow(@NotNull GHoloRow HoloRow, boolean UpdateOffset) {
        getInstance().getHoloManager().removeHoloRow(HoloRow, UpdateOffset);
    }

    /**
     * Updates the id of a holo
     * @author Gecolay
     * @since 1.0.0
     * @param Holo Holo
     * @param Id Holo-Id
     */
    public static void updateHoloId(@NotNull GHolo Holo, @NotNull String Id) {
        getInstance().getHoloManager().updateHoloId(Holo, Id);
    }

    /**
     * Updates the location of a holo
     * @author Gecolay
     * @since 1.0.0
     * @param Holo Holo
     * @param Location Holo-Location
     */
    public static void updateHoloLocation(@NotNull GHolo Holo, @NotNull Location Location) {
        getInstance().getHoloManager().updateHoloLocation(Holo, Location);
    }

    /**
     * Updates the default data of a holo
     * @author Gecolay
     * @since 1.0.0
     * @param Holo Holo
     * @param Data Default Holo-Data
     */
    public static void updateHoloData(@NotNull GHolo Holo, @NotNull GHoloData Data) {
        getInstance().getHoloManager().updateHoloData(Holo, Data);
    }

    /**
     * Sets all new rows of text for a holo
     * @author Gecolay
     * @since 1.0.0
     * @param Holo Holo
     * @param Rows Text-Rows
     */
    public static void setHoloRows(@NotNull GHolo Holo, @NotNull List<String> Rows) {
        getInstance().getHoloManager().setHoloRows(Holo, Rows);
    }

    /**
     * Copies all rows of a holo to another holo
     * @author Gecolay
     * @since 1.0.0
     * @param Holo Holo
     * @param CopyToHolo CopyToHolo
     */
    public static void copyHoloRows(@NotNull GHolo Holo, @NotNull GHolo CopyToHolo) {
        getInstance().getHoloManager().copyHoloRows(Holo, CopyToHolo);
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
        getInstance().getHoloManager().removeHolo(Holo);
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
        getInstance().getHoloManager().loadHolosForPlayer(Player);
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
        getInstance().getHoloManager().loadHolo(Holo);
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
        getInstance().getHoloManager().unloadHoloForPlayer(Holo, Player);
    }

    /**
     * Manually unloads a holo on the server for all players
     * @author Gecolay
     * @since 1.0.0
     * @param Holo Holo
     */
    public static void unloadHolo(@NotNull GHolo Holo) {
        getInstance().getHoloManager().unloadHolo(Holo);
    }

    /**
     * Manually unloads a holo in the world for a player
     * @author Gecolay
     * @since 1.0.0
     * @param Holo Holo
     * @param Player Player
     */
    public static void unloadHoloForPlayer(@NotNull GHolo Holo, @NotNull Player Player) {
        getInstance().getHoloManager().unloadHoloForPlayer(Holo, Player);
    }

}