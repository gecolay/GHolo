package dev.geco.gholo.manager;

import java.sql.*;
import java.util.*;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;

import dev.geco.gholo.*;
import dev.geco.gholo.objects.*;

public class HoloManager {

    private final GHoloMain GPM;

    public HoloManager(GHoloMain GPluginMain) { GPM = GPluginMain; }

    public void createTables() {
        try {
            GPM.getDManager().execute("CREATE TABLE IF NOT EXISTS holo (id TEXT, l_world TEXT, l_x REAL, l_y REAL, l_z REAL, max_range REAL);");
            GPM.getDManager().execute("CREATE TABLE IF NOT EXISTS holo_row (row_number INTEGER, holo_id TEXT, content TEXT, o_x REAL, o_y REAL, o_z REAL, l_yaw REAL, l_pitch REAL);");
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private final List<GHolo> holos = new ArrayList<>();

    public List<GHolo> getHolos() { return holos; }

    public GHolo getHolo(String Id) { return holos.stream().filter(holo -> holo.getId().equalsIgnoreCase(Id)).findFirst().orElse(null); }

    public int getHoloCount() { return holos.size(); }

    public int getHoloRowCount() { return holos.stream().mapToInt(holo -> holo.getRows().size()).sum(); }

    public void loadHolos() {
        clearHolos();
        try {
            try(ResultSet resultSet = GPM.getDManager().executeAndGet("SELECT * FROM holo")) {
                while(resultSet.next()) {
                    String id = resultSet.getString("id");

                    UUID worldUuid = UUID.fromString(resultSet.getString("l_world"));
                    World world = Bukkit.getWorld(worldUuid);
                    if(world == null) continue;
                    double locationX = resultSet.getDouble("l_x");
                    double locationY = resultSet.getDouble("l_y");
                    double locationZ = resultSet.getDouble("l_z");
                    Location location = new Location(world, locationX, locationY, locationZ);
                    GHolo holo = new GHolo(id, location);

                    int maxRange = resultSet.getInt("max_range");
                    holo.setMaxRange(maxRange);

                    try(ResultSet rowResultSet = GPM.getDManager().executeAndGet("SELECT * FROM holo_row where holo_id = ?", holo.getId())) {

                        TreeMap<Integer, GHoloRow> holoRowMap = new TreeMap<>();

                        while(rowResultSet.next()) {
                            int row = rowResultSet.getInt("row_number");
                            String content = GPM.getFormatUtil().formatBase(rowResultSet.getString("content"));
                            GHoloRow holoRow = new GHoloRow(holo, content);

                            double offsetX = rowResultSet.getDouble("o_x");
                            double offsetY = rowResultSet.getDouble("o_y");
                            double offsetZ = rowResultSet.getDouble("o_z");
                            holoRow.setOffsets(new Vector(offsetX, offsetY, offsetZ));

                            float locationYaw = rowResultSet.getFloat("l_yaw");
                            holoRow.setLocationYaw(locationYaw);

                            float locationPitch = rowResultSet.getFloat("l_pitch");
                            holoRow.setLocationPitch(locationPitch);

                            IGHoloRowEntity holoRowEntity = GPM.getEntityUtil().createHoloRowEntity(holoRow);
                            holoRow.setHoloRowEntity(holoRowEntity);
                            holoRowEntity.startTicking();
                            holoRowEntity.rerender();

                            holoRowMap.put(row, holoRow);
                        }

                        for(GHoloRow holoRow : holoRowMap.values()) holo.addRow(holoRow);
                    }

                    GPM.getEntityUtil().startHoloTicking(holo);

                    holos.add(holo);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    public GHolo createHolo(String Id, Location Location) {
        try {
            GPM.getDManager().execute("INSERT INTO holo (id, l_world, l_x, l_y, l_z, max_range) VALUES (?, ?, ?, ?, ?, ?)",
                    Id,
                    Location.getWorld().getUID().toString(),
                    Location.getX(),
                    Location.getY(),
                    Location.getZ(),
                    GPM.getCManager().DEFAULT_RANGE
            );

            GHolo holo = new GHolo(Id, Location);
            holo.setMaxRange(GPM.getCManager().DEFAULT_RANGE);
            holos.add(holo);

            GPM.getEntityUtil().startHoloTicking(holo);

            return holo;
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    public GHoloRow createHoloRow(GHolo Holo, String Content) {
        try {
            int row = Holo.getRows().size();
            double offset = GPM.getCManager().DEFAULT_SIZE_BETWEEN_ROWS;
            double rowOffset = offset * row;
            Vector offsets = new Vector(0, -rowOffset, 0);
            GPM.getDManager().execute("INSERT INTO holo_row (row_number, holo_id, content, o_x, o_y, o_z, l_yaw, l_pitch) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                    row,
                    Holo.getId(),
                    Content,
                    offsets.getX(),
                    offsets.getY(),
                    offsets.getZ(),
                    0,
                    0
            );

            GHoloRow holoRow = new GHoloRow(Holo, GPM.getFormatUtil().formatBase(Content));
            holoRow.setOffsets(offsets);
            Holo.addRow(holoRow);

            IGHoloRowEntity holoRowEntity = GPM.getEntityUtil().createHoloRowEntity(holoRow);
            holoRow.setHoloRowEntity(holoRowEntity);
            holoRowEntity.startTicking();
            holoRowEntity.rerender();

            return holoRow;
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    public GHoloRow insertHoloRow(GHolo Holo, int Row, String Content, boolean updateOffset) {
        try {
            double offset = GPM.getCManager().DEFAULT_SIZE_BETWEEN_ROWS;
            double rowOffset = offset * Row;
            Vector offsets = new Vector(0, -rowOffset, 0);

            if(updateOffset) {
                GPM.getDManager().execute("UPDATE holo_row SET o_y = o_y - ? WHERE holo_id = ? AND row_number >= ?", offset, Holo.getId(), Row);
                for(GHoloRow holoRow : Holo.getRows().subList(Row, Holo.getRows().size())) {
                    Vector rowOffsets = holoRow.getOffsets();
                    rowOffsets.setY(rowOffsets.getY() - offset);
                    holoRow.setOffsets(rowOffsets);
                    holoRow.getHoloRowEntity().adjustLocationToHolo();
                }
            }

            GPM.getDManager().execute("UPDATE holo_row SET row_number = row_number + 1 WHERE holo_id = ? AND row_number >= ?", Holo.getId(), Row);

            GPM.getDManager().execute("INSERT INTO holo_row (row_number, holo_id, content, o_x, o_y, o_z, l_yaw, l_pitch) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                    Row,
                    Holo.getId(),
                    Content,
                    offsets.getX(),
                    offsets.getY(),
                    offsets.getZ(),
                    0,
                    0
            );

            GHoloRow holoRow = new GHoloRow(Holo, GPM.getFormatUtil().formatBase(Content));
            holoRow.setOffsets(offsets);
            Holo.getRows().add(Row, holoRow);

            IGHoloRowEntity holoRowEntity = GPM.getEntityUtil().createHoloRowEntity(holoRow);
            holoRow.setHoloRowEntity(holoRowEntity);
            holoRowEntity.startTicking();
            holoRowEntity.rerender();

            return holoRow;
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    public void updateHoloRowContent(GHoloRow HoloRow, String Content) {
        try {
            GPM.getDManager().execute("UPDATE holo_row SET content = ? WHERE row_number = ? AND holo_id = ?", Content, HoloRow.getRow(), HoloRow.getHolo().getId());
            String content = GPM.getFormatUtil().formatBase(Content);
            HoloRow.setContent(content);
            HoloRow.getHoloRowEntity().updateHoloRowContent(content);
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void removeHoloRow(GHoloRow HoloRow, boolean updateOffset) {
        try {
            GHolo holo = HoloRow.getHolo();
            int row = HoloRow.getRow();
            double offset = GPM.getCManager().DEFAULT_SIZE_BETWEEN_ROWS;
            GPM.getDManager().execute("DELETE FROM holo_row where holo_id = ? AND row_number = ?", holo.getId(), row);
            if(updateOffset) {
                GPM.getDManager().execute("UPDATE holo_row SET o_y = o_y + ? WHERE holo_id = ? AND row_number > ?", offset, holo.getId(), row);
                for(GHoloRow holoRow : holo.getRows().subList(row + 1, holo.getRows().size())) {
                    Vector rowOffsets = holoRow.getOffsets();
                    rowOffsets.setY(rowOffsets.getY() + offset);
                    holoRow.setOffsets(rowOffsets);
                    holoRow.getHoloRowEntity().adjustLocationToHolo();
                }
            }
            GPM.getDManager().execute("UPDATE holo_row SET row_number = row_number - 1 WHERE holo_id = ? AND row_number > ?", holo.getId(), row);
            holo.removeRow(row);
            HoloRow.getHoloRowEntity().stopTicking();
            for(Player player : holo.getPlayers()) {
                HoloRow.getHoloRowEntity().removeHoloRow(player);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void updateId(GHolo Holo, String Id) {
        try {
            GPM.getDManager().execute("UPDATE holo SET id = ? WHERE id = ?", Id, Holo.getId());
            GPM.getDManager().execute("UPDATE holo_row SET holo_id = ? WHERE holo_id = ?", Id, Holo.getId());
            Holo.setId(Id);
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void updateLocation(GHolo Holo, Location Location) {
        try {
            GPM.getDManager().execute("UPDATE holo SET l_world = ?, l_x = ?, l_y = ?, l_z = ? WHERE id = ?",
                    Location.getWorld().getUID().toString(),
                    Location.getX(),
                    Location.getY(),
                    Location.getZ(),
                    Holo.getId()
            );
            Holo.setLocation(Location);
            for(GHoloRow holoRow : Holo.getRows()) {
                holoRow.getHoloRowEntity().adjustLocationToHolo();
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void updateRange(GHolo Holo, double MaxRange) {
        try {
            GPM.getDManager().execute("UPDATE holo SET max_range = ? WHERE id = ?", MaxRange, Holo.getId());
            Holo.setMaxRange(MaxRange);
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void setRows(GHolo Holo, List<String> Rows) {
        shutdownHolo(Holo);
        Holo.getRows().clear();
        try {
            GPM.getDManager().execute("DELETE FROM holo_row where holo_id = ?", Holo.getId());
            for(String row : Rows) createHoloRow(Holo, row);
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void copyRows(GHolo Holo, GHolo CopyToHolo) {
        try {
            try (ResultSet resultSet = GPM.getDManager().executeAndGet("SELECT * FROM holo_row WHERE holo_id = ?", Holo.getId())) {
                shutdownHolo(CopyToHolo);
                CopyToHolo.getRows().clear();
                GPM.getDManager().execute("DELETE FROM holo_row where holo_id = ?", CopyToHolo.getId());

                TreeMap<Integer, GHoloRow> holoRowMap = new TreeMap<>();

                while(resultSet.next()) {
                    int rowNumber = resultSet.getInt("row_number");
                    String content = resultSet.getString("content");
                    double offsetX = resultSet.getDouble("o_x");
                    double offsetY = resultSet.getDouble("o_y");
                    double offsetZ = resultSet.getDouble("o_z");
                    Vector offsets = new Vector(offsetX, offsetY, offsetZ);
                    float locationYaw = resultSet.getFloat("l_yaw");
                    float locationPitch = resultSet.getFloat("l_pitch");

                    GPM.getDManager().execute("INSERT INTO holo_row (row_number, holo_id, content, o_x, o_y, o_z, l_yaw, l_pitch) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                            rowNumber,
                            CopyToHolo.getId(),
                            content,
                            offsets.getX(),
                            offsets.getY(),
                            offsets.getZ(),
                            locationYaw,
                            locationPitch
                    );

                    GHoloRow holoRow = new GHoloRow(CopyToHolo, GPM.getFormatUtil().formatBase(content));
                    holoRow.setOffsets(offsets);
                    holoRow.setLocationYaw(locationYaw);
                    holoRow.setLocationPitch(locationPitch);

                    IGHoloRowEntity holoRowEntity = GPM.getEntityUtil().createHoloRowEntity(holoRow);
                    holoRow.setHoloRowEntity(holoRowEntity);
                    holoRowEntity.startTicking();
                    holoRowEntity.rerender();

                    holoRowMap.put(rowNumber, holoRow);
                }

                for(GHoloRow holoRow : holoRowMap.values()) CopyToHolo.addRow(holoRow);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void deleteHolo(GHolo Holo) {
        try {
            GPM.getDManager().execute("DELETE FROM holo WHERE id = ?", Holo.getId());
            GPM.getDManager().execute("DELETE FROM holo_row WHERE holo_id = ?", Holo.getId());
            holos.remove(Holo);
            shutdownHolo(Holo);
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void clearHolos() {
        for(GHolo holo : holos) shutdownHolo(holo);
        holos.clear();
    }

    private void shutdownHolo(GHolo Holo) {
        GPM.getEntityUtil().stopHoloTicking(Holo);
    }

}