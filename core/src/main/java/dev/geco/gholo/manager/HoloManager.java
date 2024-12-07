package dev.geco.gholo.manager;

import java.sql.*;
import java.util.*;

import org.bukkit.*;
import org.bukkit.entity.*;

import dev.geco.gholo.*;
import dev.geco.gholo.objects.*;

public class HoloManager {

    private final GHoloMain GPM;
    private final List<GHolo> holos = new ArrayList<>();

    public HoloManager(GHoloMain GPluginMain) { GPM = GPluginMain; }

    public void createTables() {
        try {
            GPM.getDManager().execute("CREATE TABLE IF NOT EXISTS holo (id TEXT, l_world TEXT, l_x REAL, l_y REAL, l_z REAL, default_data TEXT);");
            GPM.getDManager().execute("CREATE TABLE IF NOT EXISTS holo_row (row_number INTEGER, holo_id TEXT, content TEXT, o_x REAL, o_y REAL, o_z REAL, l_yaw REAL, l_pitch REAL, data TEXT);");
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public List<GHolo> getHolos() { return new ArrayList<>(holos); }

    public GHolo getHolo(String Id) { return holos.stream().filter(holo -> holo.getId().equalsIgnoreCase(Id)).findFirst().orElse(null); }

    public int getHoloCount() { return holos.size(); }

    public int getHoloRowCount() { return holos.stream().mapToInt(holo -> holo.getRows().size()).sum(); }

    public void loadHolos() {
        unloadHolos();
        try {
            try(ResultSet resultSet = GPM.getDManager().executeAndGet("SELECT * FROM holo")) {
                while(resultSet.next()) {
                    try {
                        String id = resultSet.getString("id");

                        UUID worldUuid = UUID.fromString(resultSet.getString("l_world"));
                        World world = Bukkit.getWorld(worldUuid);
                        if(world == null) continue;
                        double locationX = resultSet.getDouble("l_x");
                        double locationY = resultSet.getDouble("l_y");
                        double locationZ = resultSet.getDouble("l_z");
                        Location location = new Location(world, locationX, locationY, locationZ);
                        GHolo holo = new GHolo(id, location);

                        String defaultDataString = resultSet.getString("default_data");
                        holo.getRawDefaultData().loadString(defaultDataString);

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

                                String rowDataString = rowResultSet.getString("data");
                                holoRow.getRawData().loadString(rowDataString);

                                holoRowMap.put(row, holoRow);
                            }

                            for(GHoloRow holoRow : holoRowMap.values()) {
                                holo.addRow(holoRow);
                                GPM.getEntityUtil().loadHoloRowEntity(holoRow);
                                GPM.getHoloAnimationManager().updateSubscriptionStatus(holoRow);
                            }
                        }
                    } catch (Throwable e) { e.printStackTrace(); }
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void loadHolosForPlayer(Player Player) {
        for(GHolo holo : holos) loadHoloForPlayer(holo, Player);
    }

    public void loadHolo(GHolo Holo) {
        for(Player player : Holo.getRawLocation().getWorld().getPlayers()) loadHoloForPlayer(Holo, player);
    }

    public void loadHoloForPlayer(GHolo Holo, Player Player) {
        for(GHoloRow row : Holo.getRows()) row.getHoloRowEntity().loadHoloRow(Player);
    }

    public void unloadHolo(GHolo Holo) {
        for(Player player : Holo.getRawLocation().getWorld().getPlayers()) unloadHoloForPlayer(Holo, player);
    }

    public void unloadHoloForPlayer(GHolo Holo, Player Player) {
        for(GHoloRow row : Holo.getRows()) row.getHoloRowEntity().unloadHoloRow(Player);
    }

    public GHolo createHolo(String Id, Location Location) {
        try {
            GHolo holo = new GHolo(Id, Location);

            GPM.getDManager().execute("INSERT INTO holo (id, l_world, l_x, l_y, l_z, default_data) VALUES (?, ?, ?, ?, ?, ?)",
                    Id,
                    Location.getWorld().getUID().toString(),
                    Location.getX(),
                    Location.getY(),
                    Location.getZ(),
                    holo.getRawDefaultData().toString()
            );

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

            GHoloRow holoRow = new GHoloRow(Holo, GPM.getFormatUtil().formatBase(Content));
            holoRow.setPosition(position);

            GPM.getDManager().execute("INSERT INTO holo_row (row_number, holo_id, content, o_x, o_y, o_z, l_yaw, l_pitch, data) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    row,
                    Holo.getId(),
                    Content,
                    position.getX(),
                    position.getY(),
                    position.getZ(),
                    position.getYaw(),
                    position.getPitch(),
                    holoRow.getRawData().toString()
            );

            Holo.addRow(holoRow);

            GPM.getEntityUtil().loadHoloRowEntity(holoRow);
            GPM.getHoloAnimationManager().updateSubscriptionStatus(holoRow);

            return holoRow;
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    public GHoloRow insertHoloRow(GHolo Holo, int Row, String Content, boolean UpdateOffset) {
        try {
            double offset = GPM.getCManager().DEFAULT_SIZE_BETWEEN_ROWS;
            double rowOffset = offset * Row;
            Location position = new Location(Holo.getRawLocation().getWorld(), 0, -rowOffset, 0, 0, 0);

            if(UpdateOffset) {
                GPM.getDManager().execute("UPDATE holo_row SET o_y = o_y - ? WHERE holo_id = ? AND row_number >= ?", offset, Holo.getId(), Row);
                for(GHoloRow holoRow : Holo.getRows().subList(Row, Holo.getRows().size())) {
                    Location rowPosition = holoRow.getPosition();
                    rowPosition.setY(rowPosition.getY() - offset);
                    holoRow.setPosition(rowPosition);
                    holoRow.getHoloRowEntity().publishUpdate(GHoloRowUpdateType.LOCATION);
                }
            }

            GPM.getDManager().execute("UPDATE holo_row SET row_number = row_number + 1 WHERE holo_id = ? AND row_number >= ?", Holo.getId(), Row);

            GHoloRow holoRow = new GHoloRow(Holo, GPM.getFormatUtil().formatBase(Content));
            holoRow.setPosition(position);

            GPM.getDManager().execute("INSERT INTO holo_row (row_number, holo_id, content, o_x, o_y, o_z, l_yaw, l_pitch, data) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    Row,
                    Holo.getId(),
                    Content,
                    position.getX(),
                    position.getY(),
                    position.getZ(),
                    position.getYaw(),
                    position.getPitch(),
                    holoRow.getRawData().toString()
            );

            Holo.insertRow(holoRow, Row);

            GPM.getEntityUtil().loadHoloRowEntity(holoRow);
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

    public void updateHoloRowData(GHoloRow HoloRow, GHoloData RowData) {
        try {
            GPM.getDManager().execute("UPDATE holo_row SET data = ? WHERE row_number = ? AND holo_id = ?", RowData.toString(), HoloRow.getRow(), HoloRow.getHolo().getId());
            HoloRow.setData(RowData);
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void removeHoloRow(GHoloRow HoloRow, boolean UpdateOffset) {
        try {
            GHolo holo = HoloRow.getHolo();
            int row = HoloRow.getRow();
            double offset = GPM.getCManager().DEFAULT_SIZE_BETWEEN_ROWS;
            GPM.getDManager().execute("DELETE FROM holo_row where holo_id = ? AND row_number = ?", holo.getId(), row);
            if(UpdateOffset) {
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
            HoloRow.getHoloRowEntity().unloadHoloRow();
            GPM.getHoloAnimationManager().unsubscribe(HoloRow);
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void updateHoloId(GHolo Holo, String Id) {
        try {
            GPM.getDManager().execute("UPDATE holo SET id = ? WHERE id = ?", Id, Holo.getId());
            GPM.getDManager().execute("UPDATE holo_row SET holo_id = ? WHERE holo_id = ?", Id, Holo.getId());
            Holo.setId(Id);
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void updateHoloLocation(GHolo Holo, Location Location) {
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

    public void updateHoloData(GHolo Holo, GHoloData RowData) {
        try {
            GPM.getDManager().execute("UPDATE holo SET default_data = ? WHERE id = ?", RowData.toString(), Holo.getId());
            Holo.setDefaultData(RowData);
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void setHoloRows(GHolo Holo, List<String> Rows) {
        unloadHolo(Holo);
        Holo.getRows().clear();
        try {
            GPM.getDManager().execute("DELETE FROM holo_row where holo_id = ?", Holo.getId());
            for(String row : Rows) createHoloRow(Holo, row);
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void copyHoloRows(GHolo Holo, GHolo CopyToHolo) {
        try {
            try (ResultSet resultSet = GPM.getDManager().executeAndGet("SELECT * FROM holo_row WHERE holo_id = ?", Holo.getId())) {
                unloadHolo(CopyToHolo);
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
                    String rowDataString = resultSet.getString("data");

                    GPM.getDManager().execute("INSERT INTO holo_row (row_number, holo_id, content, o_x, o_y, o_z, l_yaw, l_pitch, data) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                            rowNumber,
                            CopyToHolo.getId(),
                            content,
                            position.getX(),
                            position.getY(),
                            position.getZ(),
                            locationYaw,
                            locationPitch,
                            rowDataString
                    );

                    GHoloRow holoRow = new GHoloRow(CopyToHolo, GPM.getFormatUtil().formatBase(content));
                    holoRow.setPosition(position);

                    holoRow.getRawData().loadString(rowDataString);

                    holoRowMap.put(rowNumber, holoRow);
                }

                for(GHoloRow holoRow : holoRowMap.values()) {
                    CopyToHolo.addRow(holoRow);
                    GPM.getEntityUtil().loadHoloRowEntity(holoRow);
                    GPM.getHoloAnimationManager().updateSubscriptionStatus(holoRow);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void removeHolo(GHolo Holo) {
        try {
            GPM.getDManager().execute("DELETE FROM holo WHERE id = ?", Holo.getId());
            GPM.getDManager().execute("DELETE FROM holo_row WHERE holo_id = ?", Holo.getId());
            GPM.getDManager().execute("DELETE FROM holo_row_data WHERE holo_id = ?", Holo.getId());
            holos.remove(Holo);
            for(GHoloRow holoRow : Holo.getRows()) GPM.getHoloAnimationManager().unsubscribe(holoRow);
            unloadHolo(Holo);
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void unloadHolos() {
        for(GHolo holo : holos) {
            for(GHoloRow holoRow : holo.getRows()) GPM.getHoloAnimationManager().unsubscribe(holoRow);
            unloadHolo(holo);
        }
        holos.clear();
    }

}