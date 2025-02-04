package dev.geco.gholo.object;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class GHolo {

    private String id;
    private Location location;
    private final List<GHoloRow> rows = new ArrayList<>();
    private GHoloData defaultData = new GHoloData();

    public GHolo(String id, Location location) {
        this.id = id;
        setLocation(location);
    }

    public String getId() { return id; }

    public GHolo setId(String id) {
        this.id = id;
        return this;
    }

    public Location getLocation() { return location.clone(); }

    public Location getRawLocation() { return location; }

    public GHolo setLocation(Location location) {
        this.location = location.clone();
        this.location.setYaw(0);
        this.location.setPitch(0);
        return this;
    }

    public List<GHoloRow> getRows() { return rows; }

    public GHoloRow getRow(int rowId) { return rows.get(rowId); }

    public GHolo addRow(GHoloRow holoRow) {
        rows.add(holoRow);
        return this;
    }

    public GHolo insertRow(GHoloRow holoRow, int rowId) {
        rows.add(rowId, holoRow);
        return this;
    }

    public GHolo removeRow(int rowId) {
        rows.remove(rowId);
        return this;
    }

    public GHoloData getDefaultData() { return defaultData.clone(); }

    public GHoloData getRawDefaultData() { return defaultData; }

    public GHolo setDefaultData(GHoloData defaultData) {
        this.defaultData = defaultData.clone();
        return this;
    }

}