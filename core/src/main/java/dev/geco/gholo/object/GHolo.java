package dev.geco.gholo.object;

import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class GHolo {

    private String id;
    private Location location;
    private final List<GHoloRow> rows = new ArrayList<>();
    private GHoloData defaultData = new GHoloData();

    public GHolo(@NotNull String id, @NotNull Location location) {
        this.id = id;
        setLocation(location);
    }

    public @NotNull String getId() { return id; }

    public @NotNull GHolo setId(@NotNull String id) {
        this.id = id;
        return this;
    }

    public @NotNull Location getLocation() { return location.clone(); }

    public @NotNull Location getRawLocation() { return location; }

    public @NotNull GHolo setLocation(@NotNull Location location) {
        World world = location.getWorld();
        if(this.location == null || !this.location.getWorld().equals(world)) {
            for(GHoloRow holoRow : rows) holoRow.getRawPosition().setWorld(world);
        }
        this.location = location.clone();
        this.location.setYaw(0);
        this.location.setPitch(0);
        return this;
    }

    public @NotNull List<GHoloRow> getRows() { return rows; }

    public @Nullable GHoloRow getRow(int rowId) { return rows.get(rowId); }

    public @NotNull GHolo addRow(@NotNull GHoloRow holoRow) {
        rows.add(holoRow);
        return this;
    }

    public @NotNull GHolo insertRow(@NotNull GHoloRow holoRow, int rowId) {
        rows.add(rowId, holoRow);
        return this;
    }

    public @NotNull GHolo removeRow(int rowId) {
        rows.remove(rowId);
        return this;
    }

    public @NotNull GHoloData getDefaultData() { return defaultData.clone(); }

    public @NotNull GHoloData getRawDefaultData() { return defaultData; }

    public @NotNull GHolo setDefaultData(@NotNull GHoloData defaultData) {
        this.defaultData = defaultData.clone();
        return this;
    }

}