package dev.geco.gholo.manager;

import java.sql.*;
import java.util.*;

import org.bukkit.*;
import org.bukkit.entity.*;

import dev.geco.gholo.*;
import dev.geco.gholo.objects.*;

public class HoloManager {

    private final GHoloMain GPM;

    public HoloManager(GHoloMain GPluginMain) { GPM = GPluginMain; }

    public void createTables() {
        try {
            GPM.getDManager().execute("CREATE TABLE IF NOT EXISTS holo (id TEXT, l_world TEXT, l_x REAL, l_y REAL, l_z REAL);");
            GPM.getDManager().execute("CREATE TABLE IF NOT EXISTS holo_row (row_number INTEGER, holo_id TEXT, content TEXT, o_x REAL, o_y REAL, o_z REAL, l_yaw REAL, l_pitch REAL);");
            // TODO: Maybe remove this table again
            GPM.getDManager().execute("CREATE TABLE IF NOT EXISTS holo_row_data (row_number INTEGER, holo_id TEXT, property TEXT, value TEXT);");
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

                    try(ResultSet dataResultSet = GPM.getDManager().executeAndGet("SELECT * FROM holo_row_data where holo_id = ? and row_number = -1", id)) {
                        GHoloRowData rowData = parseHoloRowData(dataResultSet, false);
                        holo.setDefaultRowData(rowData);
                    }

                    holos.add(holo);

                    try(ResultSet rowResultSet = GPM.getDManager().executeAndGet("SELECT * FROM holo_row where holo_id = ?", holo.getId())) {

                        TreeMap<Integer, GHoloRow> holoRowMap = new TreeMap<>();

                        while(rowResultSet.next()) {
                            int row = rowResultSet.getInt("row_number");
                            String content = GPM.getFormatUtil().formatBase(rowResultSet.getString("content"));
                            GHoloRow holoRow = new GHoloRow(holo, content);

                            double offsetX = rowResultSet.getDouble("o_x");
                            double offsetY = rowResultSet.getDouble("o_y");
                            double offsetZ = rowResultSet.getDouble("o_z");
                            float locationYaw = rowResultSet.getFloat("l_yaw");
                            float locationPitch = rowResultSet.getFloat("l_pitch");
                            Location position = new Location(world, offsetX, offsetY, offsetZ, locationYaw, locationPitch);
                            holoRow.setPosition(position);

                            try(ResultSet dataResultSet = GPM.getDManager().executeAndGet("SELECT * FROM holo_row_data where holo_id = ? and row_number = ?", id, row)) {
                                GHoloRowData rowData = parseHoloRowData(dataResultSet, true);
                                holoRow.setRowData(rowData);
                            }

                            holoRowMap.put(row, holoRow);
                        }

                        for(GHoloRow holoRow : holoRowMap.values()) {
                            holo.addRow(holoRow);
                            GPM.getEntityUtil().createHoloRowEntity(holoRow);
                            GPM.getHoloAnimationManager().updateSubscriptionStatus(holoRow);
                        }
                    }
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private GHoloRowData parseHoloRowData(ResultSet ResultSet, boolean NullDefaultValues) throws SQLException {
        GHoloRowData rowData = new GHoloRowData(NullDefaultValues);
        while(ResultSet.next()) {
            String value = ResultSet.getString("value");
            switch (ResultSet.getString("property").toLowerCase()) {
                case "range":
                    rowData.setRange(Double.parseDouble(value));
                    break;
                case "background_color":
                    rowData.setBackgroundColor(value);
                    break;
                case "text_opacity":
                    rowData.setTextOpacity(Byte.parseByte(value));
                    break;
                case "text_shadow":
                    rowData.setTextShadow(Boolean.parseBoolean(value));
                    break;
                case "billboard":
                    rowData.setBillboard(value);
                    break;
                case "see_through":
                    rowData.setSeeThrough(Boolean.parseBoolean(value));
                    break;
                case "size":
                    rowData.setSize(Float.parseFloat(value));
                    break;
            }
        }
        return rowData;
    }

    public void spawnHolosToPlayer(Player Player) {
        for(GHolo holo : holos) GPM.getEntityUtil().spawnHolo(holo, Player);
    }

    public GHolo createHolo(String Id, Location Location) {
        try {
            GPM.getDManager().execute("INSERT INTO holo (id, l_world, l_x, l_y, l_z) VALUES (?, ?, ?, ?, ?)",
                    Id,
                    Location.getWorld().getUID().toString(),
                    Location.getX(),
                    Location.getY(),
                    Location.getZ()
            );

            GHolo holo = new GHolo(Id, Location);
            holo.setDefaultRowData(new GHoloRowData(true));
            holos.add(holo);

            return holo;
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    public GHoloRow createHoloRow(GHolo Holo, String Content) {
        try {
            int row = Holo.getRows().size();
            double offset = GPM.getCManager().DEFAULT_SIZE_BETWEEN_ROWS;
            double rowOffset = offset * row;
            Location position = new Location(Holo.getRawLocation().getWorld(), 0, -rowOffset, 0, 0, 0);
            GPM.getDManager().execute("INSERT INTO holo_row (row_number, holo_id, content, o_x, o_y, o_z, l_yaw, l_pitch) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                    row,
                    Holo.getId(),
                    Content,
                    position.getX(),
                    position.getY(),
                    position.getZ(),
                    position.getYaw(),
                    position.getPitch()
            );

            GHoloRow holoRow = new GHoloRow(Holo, GPM.getFormatUtil().formatBase(Content));
            holoRow.setPosition(position);
            holoRow.setRowData(new GHoloRowData(false));
            Holo.addRow(holoRow);

            GPM.getEntityUtil().createHoloRowEntity(holoRow);
            GPM.getHoloAnimationManager().updateSubscriptionStatus(holoRow);

            return holoRow;
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    public GHoloRow insertHoloRow(GHolo Holo, int Row, String Content, boolean updateOffset) {
        try {
            double offset = GPM.getCManager().DEFAULT_SIZE_BETWEEN_ROWS;
            double rowOffset = offset * Row;
            Location position = new Location(Holo.getRawLocation().getWorld(), 0, -rowOffset, 0, 0, 0);

            if(updateOffset) {
                GPM.getDManager().execute("UPDATE holo_row SET o_y = o_y - ? WHERE holo_id = ? AND row_number >= ?", offset, Holo.getId(), Row);
                for(GHoloRow holoRow : Holo.getRows().subList(Row, Holo.getRows().size())) {
                    Location rowPosition = holoRow.getPosition();
                    rowPosition.setY(position.getY() - offset);
                    holoRow.setPosition(rowPosition);
                    holoRow.getHoloRowEntity().publishUpdate(GHoloRowUpdateType.LOCATION);
                }
            }

            GPM.getDManager().execute("UPDATE holo_row SET row_number = row_number + 1 WHERE holo_id = ? AND row_number >= ?", Holo.getId(), Row);

            GPM.getDManager().execute("INSERT INTO holo_row (row_number, holo_id, content, o_x, o_y, o_z, l_yaw, l_pitch) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                    Row,
                    Holo.getId(),
                    Content,
                    position.getX(),
                    position.getY(),
                    position.getZ(),
                    position.getYaw(),
                    position.getPitch()
            );

            GHoloRow holoRow = new GHoloRow(Holo, GPM.getFormatUtil().formatBase(Content));
            holoRow.setPosition(position);
            holoRow.setRowData(new GHoloRowData(false));
            Holo.insertRow(holoRow, Row);

            GPM.getEntityUtil().createHoloRowEntity(holoRow);
            GPM.getHoloAnimationManager().updateSubscriptionStatus(holoRow);

            return holoRow;
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    public void updateHoloRowContent(GHoloRow HoloRow, String Content) {
        try {
            GPM.getDManager().execute("UPDATE holo_row SET content = ? WHERE row_number = ? AND holo_id = ?", Content, HoloRow.getRow(), HoloRow.getHolo().getId());
            String content = GPM.getFormatUtil().formatBase(Content);
            HoloRow.setContent(content);
            HoloRow.getHoloRowEntity().publishUpdate(GHoloRowUpdateType.CONTENT);
            GPM.getHoloAnimationManager().updateSubscriptionStatus(HoloRow);
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void updateHoloRowPosition(GHoloRow HoloRow, Location Position) {
        try {
            GPM.getDManager().execute("UPDATE holo_row SET o_x = ?, o_y = ?, o_z = ?, l_yaw = ?, l_pitch = ? WHERE row_number = ? AND holo_id = ?",
                    Position.getX(),
                    Position.getY(),
                    Position.getZ(),
                    Position.getYaw(),
                    Position.getPitch(),
                    HoloRow.getRow(),
                    HoloRow.getHolo().getId()
            );
            HoloRow.setPosition(Position);
            HoloRow.getHoloRowEntity().publishUpdate(GHoloRowUpdateType.LOCATION);
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
                    Location rowPosition = holoRow.getPosition();
                    rowPosition.setY(rowPosition.getY() + offset);
                    holoRow.setPosition(rowPosition);
                    holoRow.getHoloRowEntity().publishUpdate(GHoloRowUpdateType.LOCATION);
                }
            }
            GPM.getDManager().execute("UPDATE holo_row SET row_number = row_number - 1 WHERE holo_id = ? AND row_number > ?", holo.getId(), row);
            holo.removeRow(row);
            HoloRow.getHoloRowEntity().removeHoloRow();
            GPM.getHoloAnimationManager().unsubscribe(HoloRow);
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
            for(GHoloRow holoRow : Holo.getRows()) holoRow.getHoloRowEntity().publishUpdate(GHoloRowUpdateType.LOCATION);
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void updateData(GHolo Holo, GHoloRow HoloRow) {
        try {
            int row = HoloRow != null ? HoloRow.getRow() : -1;
            GHoloRowData rowData = HoloRow != null ? HoloRow.getRowData() : Holo.getDefaultRowData();
            GPM.getDManager().execute("DELETE FROM holo_row_data where row_number = ? AND holo_id = ?", row, Holo.getId());
            for(Map.Entry<String, String> data : rowData.getData().entrySet()) {
                if(data.getValue() == null) continue;
                GPM.getDManager().execute("INSERT INTO holo_row_data (row_number, holo_id, property, value) VALUES (?, ?, ?, ?)", row, Holo.getId(), data.getKey(), data.getValue());
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void setRows(GHolo Holo, List<String> Rows) {
        GPM.getEntityUtil().removeHolo(Holo);
        Holo.getRows().clear();
        try {
            GPM.getDManager().execute("DELETE FROM holo_row where holo_id = ?", Holo.getId());
            for(String row : Rows) createHoloRow(Holo, row);
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void copyRows(GHolo Holo, GHolo CopyToHolo) {
        try {
            try (ResultSet resultSet = GPM.getDManager().executeAndGet("SELECT * FROM holo_row WHERE holo_id = ?", Holo.getId())) {
                GPM.getEntityUtil().removeHolo(CopyToHolo);
                for(GHoloRow holoRow : CopyToHolo.getRows()) GPM.getHoloAnimationManager().unsubscribe(holoRow);
                CopyToHolo.getRows().clear();
                GPM.getDManager().execute("DELETE FROM holo_row where holo_id = ?", CopyToHolo.getId());

                TreeMap<Integer, GHoloRow> holoRowMap = new TreeMap<>();

                while(resultSet.next()) {
                    int rowNumber = resultSet.getInt("row_number");
                    String content = resultSet.getString("content");
                    double offsetX = resultSet.getDouble("o_x");
                    double offsetY = resultSet.getDouble("o_y");
                    double offsetZ = resultSet.getDouble("o_z");
                    float locationYaw = resultSet.getFloat("l_yaw");
                    float locationPitch = resultSet.getFloat("l_pitch");
                    Location position = new Location(CopyToHolo.getRawLocation().getWorld(), offsetX, offsetY, offsetZ, locationYaw, locationPitch);

                    GPM.getDManager().execute("INSERT INTO holo_row (row_number, holo_id, content, o_x, o_y, o_z, l_yaw, l_pitch) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                            rowNumber,
                            CopyToHolo.getId(),
                            content,
                            position.getX(),
                            position.getY(),
                            position.getZ(),
                            locationYaw,
                            locationPitch
                    );

                    GHoloRow holoRow = new GHoloRow(CopyToHolo, GPM.getFormatUtil().formatBase(content));
                    holoRow.setPosition(position);
                    //TODO: copy data

                    holoRowMap.put(rowNumber, holoRow);
                }

                for(GHoloRow holoRow : holoRowMap.values()) {
                    CopyToHolo.addRow(holoRow);
                    GPM.getEntityUtil().createHoloRowEntity(holoRow);
                    GPM.getHoloAnimationManager().updateSubscriptionStatus(holoRow);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void deleteHolo(GHolo Holo) {
        try {
            GPM.getDManager().execute("DELETE FROM holo WHERE id = ?", Holo.getId());
            GPM.getDManager().execute("DELETE FROM holo_row WHERE holo_id = ?", Holo.getId());
            GPM.getDManager().execute("DELETE FROM holo_row_data WHERE holo_id = ?", Holo.getId());
            holos.remove(Holo);
            for(GHoloRow holoRow : Holo.getRows()) GPM.getHoloAnimationManager().unsubscribe(holoRow);
            GPM.getEntityUtil().removeHolo(Holo);
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void clearHolos() {
        for(GHolo holo : holos) {
            for(GHoloRow holoRow : holo.getRows()) GPM.getHoloAnimationManager().unsubscribe(holoRow);
            GPM.getEntityUtil().removeHolo(holo);
        }
        holos.clear();
    }

}