package dev.geco.gholo.objects;

import java.util.*;

import org.bukkit.*;

public class GHolo {

    private String id;
    private Location location;
    private final List<GHoloRow> rows = new ArrayList<>();
    private GHoloData defaultData = new GHoloData();

    public GHolo(String Id, Location Location) {
        id = Id;
        setLocation(Location);
    }

    public String getId() { return id; }

    public void setId(String Id) { id = Id; }

    public Location getLocation() { return location.clone(); }

    public Location getRawLocation() { return location; }

    public void setLocation(Location Location) {
        location = Location.clone();
        location.setYaw(0);
        location.setPitch(0);
    }

    public List<GHoloRow> getRows() { return rows; }

    public GHoloRow getRow(int Row) { return rows.get(Row); }

    public void addRow(GHoloRow HoloRow) { rows.add(HoloRow); }

    public void insertRow(GHoloRow HoloRow, int Index) { rows.add(Index, HoloRow); }

    public void removeRow(int Row) { rows.remove(Row); }

    public GHoloData getDefaultData() { return defaultData; }

    public void setDefaultData(GHoloData DefaultData) { defaultData = DefaultData; }

}