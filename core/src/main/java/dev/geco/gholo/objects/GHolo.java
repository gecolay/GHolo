package dev.geco.gholo.objects;

import java.util.*;

import org.bukkit.*;
import org.bukkit.entity.*;

public class GHolo {

    private String id;
    private Location location;
    private final Map<Integer, GHoloRow> rows = new TreeMap<>();
    private double maxRange = 64;
    private final List<Player> players = new ArrayList<>();
    private final List<UUID> tasks = new ArrayList<>();

    public GHolo(String Id, Location Location) {
        id = Id;
        location = Location.clone();
        location.setYaw(0);
        location.setPitch(0);
    }

    public String getId() { return id; }

    public void setId(String Id) { id = Id; }

    public Location getLocation() { return location.clone(); }

    public void setLocation(Location Location) {
        location = Location.clone();
        location.setYaw(0);
        location.setPitch(0);
    }

    public List<GHoloRow> getRows() { return new ArrayList<>(rows.values()); }

    public GHoloRow getRow(int Row) { return rows.get(Row); }

    public void addRow(GHoloRow HoloRow) { rows.put(HoloRow.getRow(), HoloRow); }

    public void removeRow(int Row) { rows.remove(Row); }

    public void reorderRows() {
        List<GHoloRow> sortedRows = getRows();
        rows.clear();
        for(GHoloRow holoRow : sortedRows) rows.put(holoRow.getRow(), holoRow);
    }

    public void clearRows() { rows.clear(); }

    public double getMaxRange() { return maxRange; }

    public void setMaxRange(double MaxRange) { maxRange = MaxRange; }

    public List<Player> getPlayers() { return players; }

    public List<UUID> getTasks() { return tasks; }

    public void addTask(UUID Task) { tasks.add(Task); }

}