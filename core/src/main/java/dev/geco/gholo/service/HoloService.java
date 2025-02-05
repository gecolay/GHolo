package dev.geco.gholo.service;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.object.GHolo;
import dev.geco.gholo.object.GHoloData;
import dev.geco.gholo.object.GHoloRow;
import dev.geco.gholo.object.GHoloRowUpdateType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.UUID;

public class HoloService {

    private final GHoloMain gHoloMain;
    private final List<GHolo> holos = new ArrayList<>();

    public HoloService(GHoloMain gHoloMain) {
        this.gHoloMain = gHoloMain;
    }

    public void createTables() {
        try {
            gHoloMain.getDataService().execute("CREATE TABLE IF NOT EXISTS holo (id TEXT, l_world TEXT, l_x REAL, l_y REAL, l_z REAL, default_data TEXT);");
            gHoloMain.getDataService().execute("CREATE TABLE IF NOT EXISTS holo_row (`row_number` INTEGER, holo_id TEXT, content TEXT, o_x REAL, o_y REAL, o_z REAL, l_yaw REAL, l_pitch REAL, data TEXT);");
        } catch(SQLException e) { e.printStackTrace(); }
    }

    public List<GHolo> getHolos() { return new ArrayList<>(holos); }

    public List<GHolo> getNearHolos(Location location, double range) { return holos.stream().filter(holo -> holo.getRawLocation().getWorld().equals(location.getWorld()) && holo.getRawLocation().distance(location) <= range).toList(); }

    public GHolo getHolo(String holoId) { return holos.stream().filter(holo -> holo.getId().equalsIgnoreCase(holoId)).findFirst().orElse(null); }

    public int getHoloCount() { return holos.size(); }

    public int getHoloRowCount() { return holos.stream().mapToInt(holo -> holo.getRows().size()).sum(); }

    public void loadHolos() {
        unloadHolos();
        try {
            try(ResultSet resultSet = gHoloMain.getDataService().executeAndGet("SELECT * FROM holo")) {
                while(resultSet.next()) {
                    try {
                        String id = resultSet.getString("id");
                        String worldString = resultSet.getString("l_world");
                        World world;
                        try {
                            UUID worldUuid = UUID.fromString(worldString);
                            world = Bukkit.getWorld(worldUuid);
                            if(world == null) continue;
                            gHoloMain.getDataService().execute("UPDATE holo SET l_world = ? WHERE id = ?", world.getName(), id);
                        } catch (IllegalArgumentException e) {
                            world = Bukkit.getWorld(worldString);
                        }
                        if(world == null) continue;
                        double locationX = resultSet.getDouble("l_x");
                        double locationY = resultSet.getDouble("l_y");
                        double locationZ = resultSet.getDouble("l_z");
                        Location location = new Location(world, locationX, locationY, locationZ);
                        GHolo holo = new GHolo(id, location);

                        String defaultDataString = resultSet.getString("default_data");
                        holo.getRawDefaultData().loadString(defaultDataString);

                        holos.add(holo);

                        try(ResultSet rowResultSet = gHoloMain.getDataService().executeAndGet("SELECT * FROM holo_row where holo_id = ?", holo.getId())) {
                            TreeMap<Integer, GHoloRow> holoRowMap = new TreeMap<>();

                            while(rowResultSet.next()) {
                                int row = rowResultSet.getInt("row_number");
                                String content = gHoloMain.getFormatUtil().formatBase(rowResultSet.getString("content"));
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
                                gHoloMain.getEntityUtil().loadHoloRowEntity(holoRow);
                                gHoloMain.getHoloAnimationService().updateSubscriptionStatus(holoRow);
                            }
                        }
                    } catch(Throwable e) { e.printStackTrace(); }
                }
            }
        } catch(Throwable e) { e.printStackTrace(); }
    }

    public void loadHolosForPlayer(Player player) { for(GHolo holo : holos) loadHoloForPlayer(holo, player); }

    public void loadHolo(GHolo holo) { for(Player player : holo.getRawLocation().getWorld().getPlayers()) loadHoloForPlayer(holo, player); }

    public void loadHoloForPlayer(GHolo holo, Player player) { for(GHoloRow row : holo.getRows()) row.getHoloRowEntity().loadHoloRow(player); }

    public void unloadHolo(GHolo holo) { for(Player player : holo.getRawLocation().getWorld().getPlayers()) unloadHoloForPlayer(holo, player); }

    public void unloadHoloForPlayer(GHolo holo, Player player) { for(GHoloRow row : holo.getRows()) row.getHoloRowEntity().unloadHoloRow(player); }

    public GHolo createHolo(String holoId, Location location) {
        try {
            GHolo holo = new GHolo(holoId, location);

            gHoloMain.getDataService().execute("INSERT INTO holo (id, l_world, l_x, l_y, l_z, default_data) VALUES (?, ?, ?, ?, ?, ?)",
                    holoId,
                    location.getWorld().getName(),
                    location.getX(),
                    location.getY(),
                    location.getZ(),
                    holo.getRawDefaultData().toString()
            );

            holos.add(holo);

            return holo;
        } catch(Throwable e) { e.printStackTrace(); }
        return null;
    }

    public GHoloRow createHoloRow(GHolo holo, String content) {
        try {
            int row = holo.getRows().size();
            double offset = gHoloMain.getConfigService().DEFAULT_SIZE_BETWEEN_ROWS;
            double rowOffset = offset * row;
            Location position = new Location(holo.getRawLocation().getWorld(), 0, -rowOffset, 0, 0, 0);

            GHoloRow holoRow = new GHoloRow(holo, gHoloMain.getFormatUtil().formatBase(content));
            holoRow.setPosition(position);

            gHoloMain.getDataService().execute("INSERT INTO holo_row (`row_number`, holo_id, content, o_x, o_y, o_z, l_yaw, l_pitch, data) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    row,
                    holo.getId(),
                    content,
                    position.getX(),
                    position.getY(),
                    position.getZ(),
                    position.getYaw(),
                    position.getPitch(),
                    holoRow.getRawData().toString()
            );

            holo.addRow(holoRow);

            gHoloMain.getEntityUtil().loadHoloRowEntity(holoRow);
            gHoloMain.getHoloAnimationService().updateSubscriptionStatus(holoRow);

            return holoRow;
        } catch(Throwable e) { e.printStackTrace(); }
        return null;
    }

    public GHoloRow insertHoloRow(GHolo holo, int rowId, String content, boolean updateOffset) {
        try {
            double offset = gHoloMain.getConfigService().DEFAULT_SIZE_BETWEEN_ROWS;
            double rowOffset = offset * rowId;
            Location position = new Location(holo.getRawLocation().getWorld(), 0, -rowOffset, 0, 0, 0);

            if(updateOffset) {
                gHoloMain.getDataService().execute("UPDATE holo_row SET o_y = o_y - ? WHERE holo_id = ? AND `row_number` >= ?", offset, holo.getId(), rowId);
                for(GHoloRow holoRow : holo.getRows().subList(rowId, holo.getRows().size())) {
                    Location rowPosition = holoRow.getPosition();
                    rowPosition.setY(rowPosition.getY() - offset);
                    holoRow.setPosition(rowPosition);
                    holoRow.getHoloRowEntity().publishUpdate(GHoloRowUpdateType.LOCATION);
                }
            }

            gHoloMain.getDataService().execute("UPDATE holo_row SET `row_number` = `row_number` + 1 WHERE holo_id = ? AND `row_number` >= ?", holo.getId(), rowId);

            GHoloRow holoRow = new GHoloRow(holo, gHoloMain.getFormatUtil().formatBase(content));
            holoRow.setPosition(position);

            gHoloMain.getDataService().execute("INSERT INTO holo_row (`row_number`, holo_id, content, o_x, o_y, o_z, l_yaw, l_pitch, data) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    rowId,
                    holo.getId(),
                    content,
                    position.getX(),
                    position.getY(),
                    position.getZ(),
                    position.getYaw(),
                    position.getPitch(),
                    holoRow.getRawData().toString()
            );

            holo.insertRow(holoRow, rowId);

            gHoloMain.getEntityUtil().loadHoloRowEntity(holoRow);
            gHoloMain.getHoloAnimationService().updateSubscriptionStatus(holoRow);

            return holoRow;
        } catch(Throwable e) { e.printStackTrace(); }
        return null;
    }

    public void updateHoloRowContent(GHoloRow holoRow, String content) {
        try {
            gHoloMain.getDataService().execute("UPDATE holo_row SET content = ? WHERE `row_number` = ? AND holo_id = ?", content, holoRow.getRowId(), holoRow.getHolo().getId());
            holoRow.setContent(gHoloMain.getFormatUtil().formatBase(content));
            holoRow.getHoloRowEntity().publishUpdate(GHoloRowUpdateType.CONTENT);
            gHoloMain.getHoloAnimationService().updateSubscriptionStatus(holoRow);
        } catch(Throwable e) { e.printStackTrace(); }
    }

    public void updateHoloRowPosition(GHoloRow holoRow, Location position) {
        try {
            gHoloMain.getDataService().execute("UPDATE holo_row SET o_x = ?, o_y = ?, o_z = ?, l_yaw = ?, l_pitch = ? WHERE `row_number` = ? AND holo_id = ?",
                    position.getX(),
                    position.getY(),
                    position.getZ(),
                    position.getYaw(),
                    position.getPitch(),
                    holoRow.getRowId(),
                    holoRow.getHolo().getId()
            );
            holoRow.setPosition(position);
            holoRow.getHoloRowEntity().publishUpdate(GHoloRowUpdateType.LOCATION);
        } catch(Throwable e) { e.printStackTrace(); }
    }

    public void updateHoloRowData(GHoloRow holoRow, GHoloData rowData) {
        try {
            gHoloMain.getDataService().execute("UPDATE holo_row SET data = ? WHERE `row_number` = ? AND holo_id = ?", rowData.toString(), holoRow.getRowId(), holoRow.getHolo().getId());
            holoRow.setData(rowData);
        } catch(Throwable e) { e.printStackTrace(); }
    }

    public void removeHoloRow(GHoloRow holoRow, boolean updateOffset) {
        try {
            GHolo holo = holoRow.getHolo();
            int row = holoRow.getRowId();
            double offset = gHoloMain.getConfigService().DEFAULT_SIZE_BETWEEN_ROWS;
            gHoloMain.getDataService().execute("DELETE FROM holo_row where holo_id = ? AND `row_number` = ?", holo.getId(), row);
            if(updateOffset) {
                gHoloMain.getDataService().execute("UPDATE holo_row SET o_y = o_y + ? WHERE holo_id = ? AND `row_number` > ?", offset, holo.getId(), row);
                for(GHoloRow updateHoloRow : holo.getRows().subList(row + 1, holo.getRows().size())) {
                    Location rowPosition = updateHoloRow.getPosition();
                    rowPosition.setY(rowPosition.getY() + offset);
                    updateHoloRow.setPosition(rowPosition);
                    updateHoloRow.getHoloRowEntity().publishUpdate(GHoloRowUpdateType.LOCATION);
                }
            }
            gHoloMain.getDataService().execute("UPDATE holo_row SET `row_number` = `row_number` - 1 WHERE holo_id = ? AND `row_number` > ?", holo.getId(), row);
            holo.removeRow(row);
            holoRow.getHoloRowEntity().unloadHoloRow();
            gHoloMain.getHoloAnimationService().unsubscribe(holoRow);
        } catch(Throwable e) { e.printStackTrace(); }
    }

    public void updateHoloId(GHolo holo, String holoId) {
        try {
            gHoloMain.getDataService().execute("UPDATE holo SET id = ? WHERE id = ?", holoId, holo.getId());
            gHoloMain.getDataService().execute("UPDATE holo_row SET holo_id = ? WHERE holo_id = ?", holoId, holo.getId());
            holo.setId(holoId);
        } catch(Throwable e) { e.printStackTrace(); }
    }

    public void updateHoloLocation(GHolo holo, Location location) {
        try {
            gHoloMain.getDataService().execute("UPDATE holo SET l_world = ?, l_x = ?, l_y = ?, l_z = ? WHERE id = ?",
                    location.getWorld().getName(),
                    location.getX(),
                    location.getY(),
                    location.getZ(),
                    holo.getId()
            );
            if(!holo.getRawLocation().getWorld().equals(location.getWorld())) {
                unloadHolo(holo);
                holo.setLocation(location);
                for(GHoloRow holoRow : holo.getRows()) gHoloMain.getEntityUtil().loadHoloRowEntity(holoRow);
                return;
            }
            holo.setLocation(location);
            for(GHoloRow holoRow : holo.getRows()) holoRow.getHoloRowEntity().publishUpdate(GHoloRowUpdateType.LOCATION);
        } catch(Throwable e) { e.printStackTrace(); }
    }

    public void updateHoloData(GHolo holo, GHoloData rowData) {
        try {
            gHoloMain.getDataService().execute("UPDATE holo SET default_data = ? WHERE id = ?", rowData.toString(), holo.getId());
            holo.setDefaultData(rowData);
        } catch(Throwable e) { e.printStackTrace(); }
    }

    public void setHoloRows(GHolo holo, List<String> rows) {
        unloadHolo(holo);
        holo.getRows().clear();
        try {
            gHoloMain.getDataService().execute("DELETE FROM holo_row where holo_id = ?", holo.getId());
            for(String row : rows) createHoloRow(holo, row);
        } catch(Throwable e) { e.printStackTrace(); }
    }

    public void copyHoloRows(GHolo holo, GHolo copyToHolo) {
        try {
            try (ResultSet resultSet = gHoloMain.getDataService().executeAndGet("SELECT * FROM holo_row WHERE holo_id = ?", holo.getId())) {
                unloadHolo(copyToHolo);
                for(GHoloRow holoRow : copyToHolo.getRows()) gHoloMain.getHoloAnimationService().unsubscribe(holoRow);
                copyToHolo.getRows().clear();
                gHoloMain.getDataService().execute("DELETE FROM holo_row where holo_id = ?", copyToHolo.getId());

                TreeMap<Integer, GHoloRow> holoRowMap = new TreeMap<>();

                while(resultSet.next()) {
                    int rowId = resultSet.getInt("row_number");
                    String content = resultSet.getString("content");
                    double offsetX = resultSet.getDouble("o_x");
                    double offsetY = resultSet.getDouble("o_y");
                    double offsetZ = resultSet.getDouble("o_z");
                    float locationYaw = resultSet.getFloat("l_yaw");
                    float locationPitch = resultSet.getFloat("l_pitch");
                    Location position = new Location(copyToHolo.getRawLocation().getWorld(), offsetX, offsetY, offsetZ, locationYaw, locationPitch);
                    String rowDataString = resultSet.getString("data");

                    gHoloMain.getDataService().execute("INSERT INTO holo_row (`row_number`, holo_id, content, o_x, o_y, o_z, l_yaw, l_pitch, data) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                            rowId,
                            copyToHolo.getId(),
                            content,
                            position.getX(),
                            position.getY(),
                            position.getZ(),
                            locationYaw,
                            locationPitch,
                            rowDataString
                    );

                    GHoloRow holoRow = new GHoloRow(copyToHolo, gHoloMain.getFormatUtil().formatBase(content));
                    holoRow.setPosition(position);

                    holoRow.getRawData().loadString(rowDataString);

                    holoRowMap.put(rowId, holoRow);
                }

                for(GHoloRow holoRow : holoRowMap.values()) {
                    copyToHolo.addRow(holoRow);
                    gHoloMain.getEntityUtil().loadHoloRowEntity(holoRow);
                    gHoloMain.getHoloAnimationService().updateSubscriptionStatus(holoRow);
                }
            }
        } catch(Throwable e) { e.printStackTrace(); }
    }

    public void removeHolo(GHolo holo) {
        try {
            gHoloMain.getDataService().execute("DELETE FROM holo WHERE id = ?", holo.getId());
            gHoloMain.getDataService().execute("DELETE FROM holo_row WHERE holo_id = ?", holo.getId());
            holos.remove(holo);
            for(GHoloRow holoRow : holo.getRows()) gHoloMain.getHoloAnimationService().unsubscribe(holoRow);
            unloadHolo(holo);
        } catch(Throwable e) { e.printStackTrace(); }
    }

    public void unloadHolos() {
        for(GHolo holo : holos) {
            for(GHoloRow holoRow : holo.getRows()) gHoloMain.getHoloAnimationService().unsubscribe(holoRow);
            unloadHolo(holo);
        }
        holos.clear();
    }

}