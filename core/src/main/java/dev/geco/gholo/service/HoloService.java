package dev.geco.gholo.service;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.object.GHolo;
import dev.geco.gholo.object.GHoloData;
import dev.geco.gholo.object.GHoloRow;
import dev.geco.gholo.object.GHoloUpdateType;
import dev.geco.gholo.object.location.SimpleLocation;
import dev.geco.gholo.object.location.SimpleOffset;
import dev.geco.gholo.object.location.SimpleRotation;
import org.bukkit.Location;
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
            gHoloMain.getDataService().execute("""
                CREATE TABLE IF NOT EXISTS gholo_holo (
                    uuid TEXT,
                    id TEXT,
                    location TEXT,
                    rotation TEXT,
                    data TEXT
                );
            """);
            gHoloMain.getDataService().execute("""
                CREATE TABLE IF NOT EXISTS gholo_holo_row (
                    position INTEGER,
                    holo_uuid TEXT,
                    content TEXT,
                    offset TEXT,
                    rotation TEXT,
                    data TEXT
                );
            """);
        } catch(SQLException e) { e.printStackTrace(); }
    }

    public List<GHolo> getHolos() { return new ArrayList<>(holos); }

    public List<GHolo> getNearHolos(Location location, double range) { return holos.stream().filter(holo -> holo.getRawLocation().getWorld().equals(location.getWorld()) && holo.getRawLocation().distance(location) <= range).toList(); }

    public GHolo getHolo(String holoId) { return holos.stream().filter(holo -> holo.getId().equalsIgnoreCase(holoId)).findFirst().orElse(null); }

    public int getHoloCount() { return holos.size(); }

    public int getHoloRowCount() { return holos.stream().mapToInt(holo -> holo.getRows().size()).sum(); }

    public void loadHolos() {
        try {
            try(ResultSet resultSet = gHoloMain.getDataService().executeAndGet("SELECT * FROM gholo_holo")) {
                while(resultSet.next()) {
                    try {
                        UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                        String id = resultSet.getString("id");
                        SimpleLocation location = SimpleLocation.fromString(resultSet.getString("location"));
                        if(location == null) throw new RuntimeException("Could not load holo '" + id + "', invalid location");
                        SimpleRotation rotation = SimpleRotation.fromString(resultSet.getString("rotation"));
                        if(rotation == null) throw new RuntimeException("Could not load holo '" + id + "', invalid rotation");
                        GHolo holo = new GHolo(uuid, id, location);
                        holo.setRotation(rotation);

                        String dataString = resultSet.getString("data");
                        holo.getRawData().loadString(dataString);

                        holos.add(holo);

                        try(ResultSet rowResultSet = gHoloMain.getDataService().executeAndGet("SELECT * FROM gholo_holo_row where holo_uuid = ?", uuid.toString())) {
                            TreeMap<Integer, GHoloRow> holoRowMap = new TreeMap<>();

                            while(rowResultSet.next()) {
                                int position = rowResultSet.getInt("position");
                                String content = gHoloMain.getFormatUtil().formatBase(rowResultSet.getString("content"));
                                SimpleOffset offset = SimpleOffset.fromString(rowResultSet.getString("offset"));
                                if(offset == null) throw new RuntimeException("Could not load holo row '" + position + "' of holo '" + id + "', invalid location");
                                SimpleRotation rowRotation = SimpleRotation.fromString(rowResultSet.getString("rotation"));
                                if(rowRotation == null) throw new RuntimeException("Could not load holo row '" + position + "' of holo '" + id + "', invalid rotation");
                                GHoloRow holoRow = new GHoloRow(holo, content);
                                holoRow.setOffset(offset);
                                holoRow.setRotation(rowRotation);

                                String rowDataString = rowResultSet.getString("data");
                                holoRow.getRawData().loadString(rowDataString);

                                holoRowMap.put(position, holoRow);
                            }

                            for(GHoloRow holoRow : holoRowMap.values()) {
                                holo.addRow(holoRow);
                                gHoloMain.getEntityUtil().createHoloRowEntity(holoRow);
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

    public GHolo createHolo(String holoId, SimpleLocation location) {
        try {
            GHolo holo = new GHolo(UUID.randomUUID(), holoId, location);

            gHoloMain.getDataService().execute("INSERT INTO gholo_holo (uuid, id, location, rotation, data) VALUES (?, ?, ?, ?, ?)",
                    holo.getUuid().toString(),
                    holoId,
                    location.toString(),
                    holo.getRotation().toString(),
                    holo.getData().toString()
            );

            holos.add(holo);

            return holo;
        } catch(Throwable e) { e.printStackTrace(); }
        return null;
    }

    public GHoloRow createHoloRow(GHolo holo, String content) {
        try {
            int position = holo.getRows().size();
            double sizeBetweenRows = gHoloMain.getConfigService().DEFAULT_SIZE_BETWEEN_ROWS;
            double rowOffset = sizeBetweenRows * position;
            SimpleOffset offset = new SimpleOffset(0, -rowOffset, 0);

            GHoloRow holoRow = new GHoloRow(holo, gHoloMain.getFormatUtil().formatBase(content));
            holoRow.setOffset(offset);

            gHoloMain.getDataService().execute("INSERT INTO gholo_holo_row (position, holo_uuid, content, offset, rotation, data) VALUES (?, ?, ?, ?, ?, ?)",
                    position,
                    holo.getUuid().toString(),
                    content,
                    offset.toString(),
                    holo.getRotation().toString(),
                    holoRow.getRawData().toString()
            );

            holo.addRow(holoRow);

            gHoloMain.getEntityUtil().createHoloRowEntity(holoRow);
            gHoloMain.getHoloAnimationService().updateSubscriptionStatus(holoRow);

            return holoRow;
        } catch(Throwable e) { e.printStackTrace(); }
        return null;
    }

    public GHoloRow insertHoloRow(GHolo holo, int position, String content, boolean updateOffsets) {
        try {
            double sizeBetweenRows = gHoloMain.getConfigService().DEFAULT_SIZE_BETWEEN_ROWS;
            double rowOffset = sizeBetweenRows * position;
            SimpleOffset offset = new SimpleOffset(0, -rowOffset, 0);

            if(updateOffsets) {
                try (ResultSet moveOffsetResultSet = gHoloMain.getDataService().executeAndGet("SELECT position, offset FROM gholo_holo_row WHERE holo_uuid = ? AND position >= ?", holo.getUuid().toString(), position)) {
                    while(moveOffsetResultSet.next()) {
                        int movePosition = moveOffsetResultSet.getInt("position");
                        SimpleOffset moveOffset = SimpleOffset.fromString(moveOffsetResultSet.getString("offset"));
                        moveOffset.setY(moveOffset.getY() - sizeBetweenRows);
                        gHoloMain.getDataService().execute("UPDATE gholo_holo_row SET offset = ? WHERE holo_uuid = ? AND position = ?", moveOffset, holo.getUuid().toString(), movePosition);
                    }
                }
                for(GHoloRow updateHoloRow : holo.getRows().subList(position, holo.getRows().size())) {
                    SimpleOffset moveOffset = updateHoloRow.getOffset();
                    moveOffset.setY(moveOffset.getY() - sizeBetweenRows);
                    updateHoloRow.setOffset(moveOffset);
                    updateHoloRow.getHoloRowEntity().publishUpdate(GHoloUpdateType.LOCATION);
                }
            }

            gHoloMain.getDataService().execute("UPDATE gholo_holo_row SET position = position + 1 WHERE holo_uuid = ? AND position >= ?", holo.getUuid().toString(), position);

            GHoloRow holoRow = new GHoloRow(holo, gHoloMain.getFormatUtil().formatBase(content));
            holoRow.setOffset(offset);

            gHoloMain.getDataService().execute("INSERT INTO gholo_holo_row (position, holo_uuid, content, offset, rotation, data) VALUES (?, ?, ?, ?, ?, ?)",
                    position,
                    holo.getUuid().toString(),
                    content,
                    offset.toString(),
                    holo.getRotation().toString(),
                    holoRow.getRawData().toString()
            );

            holo.insertRow(holoRow, position);

            gHoloMain.getEntityUtil().createHoloRowEntity(holoRow);
            gHoloMain.getHoloAnimationService().updateSubscriptionStatus(holoRow);

            return holoRow;
        } catch(Throwable e) { e.printStackTrace(); }
        return null;
    }

    public void updateHoloRowContent(GHoloRow holoRow, String content) {
        try {
            gHoloMain.getDataService().execute("UPDATE gholo_holo_row SET content = ? WHERE position = ? AND holo_uuid = ?", content, holoRow.getPosition(), holoRow.getHolo().getUuid().toString());
            holoRow.setContent(gHoloMain.getFormatUtil().formatBase(content));
            holoRow.getHoloRowEntity().publishUpdate(GHoloUpdateType.CONTENT);
            gHoloMain.getHoloAnimationService().updateSubscriptionStatus(holoRow);
        } catch(Throwable e) { e.printStackTrace(); }
    }

    public void updateHoloRowOffset(GHoloRow holoRow, SimpleOffset offset) {
        try {
            gHoloMain.getDataService().execute("UPDATE gholo_holo_row SET offset = ? WHERE position = ? AND holo_uuid = ?",
                    offset.toString(),
                    holoRow.getPosition(),
                    holoRow.getHolo().getUuid().toString()
            );
            holoRow.setOffset(offset);
            holoRow.getHoloRowEntity().publishUpdate(GHoloUpdateType.LOCATION);
        } catch(Throwable e) { e.printStackTrace(); }
    }

    public void updateHoloRowRotation(GHoloRow holoRow, SimpleRotation rotation) {
        try {
            gHoloMain.getDataService().execute("UPDATE gholo_holo_row SET rotation = ? WHERE position = ? AND holo_uuid = ?",
                    rotation.toString(),
                    holoRow.getPosition(),
                    holoRow.getHolo().getUuid().toString()
            );
            holoRow.setRotation(rotation);
            holoRow.getHoloRowEntity().publishUpdate(GHoloUpdateType.LOCATION);
        } catch(Throwable e) { e.printStackTrace(); }
    }

    public void updateHoloRowData(GHoloRow holoRow, GHoloData data) {
        try {
            gHoloMain.getDataService().execute("UPDATE gholo_holo_row SET data = ? WHERE position = ? AND holo_uuid = ?", data.toString(), holoRow.getPosition(), holoRow.getHolo().getUuid().toString());
            holoRow.setData(data);
        } catch(Throwable e) { e.printStackTrace(); }
    }

    public void removeHoloRow(GHoloRow holoRow, boolean updateOffsets) {
        try {
            GHolo holo = holoRow.getHolo();
            int position = holoRow.getPosition();
            double sizeBetweenRows = gHoloMain.getConfigService().DEFAULT_SIZE_BETWEEN_ROWS;
            gHoloMain.getDataService().execute("DELETE FROM gholo_holo_row where holo_uuid = ? AND position = ?", holo.getUuid().toString(), position);
            if(updateOffsets) {
                try (ResultSet moveOffsetResultSet = gHoloMain.getDataService().executeAndGet("SELECT position, offset FROM gholo_holo_row WHERE holo_uuid = ? AND position > ?", holo.getUuid().toString(), position)) {
                    while(moveOffsetResultSet.next()) {
                        int movePosition = moveOffsetResultSet.getInt("position");
                        SimpleOffset moveOffset = SimpleOffset.fromString(moveOffsetResultSet.getString("offset"));
                        moveOffset.setY(moveOffset.getY() + sizeBetweenRows);
                        gHoloMain.getDataService().execute("UPDATE gholo_holo_row SET offset = ? WHERE holo_uuid = ? AND position = ?", moveOffset, holo.getUuid().toString(), movePosition);
                    }
                }
                for(GHoloRow updateHoloRow : holo.getRows().subList(position + 1, holo.getRows().size())) {
                    SimpleOffset moveOffset = updateHoloRow.getOffset();
                    moveOffset.setY(moveOffset.getY() + sizeBetweenRows);
                    updateHoloRow.setOffset(moveOffset);
                    updateHoloRow.getHoloRowEntity().publishUpdate(GHoloUpdateType.LOCATION);
                }
            }
            gHoloMain.getDataService().execute("UPDATE gholo_holo_row SET position = position - 1 WHERE holo_uuid = ? AND position > ?", holo.getUuid().toString(), position);
            holo.removeRow(position);
            holoRow.getHoloRowEntity().unloadHoloRow();
            gHoloMain.getHoloAnimationService().unsubscribe(holoRow);
        } catch(Throwable e) { e.printStackTrace(); }
    }

    public void updateHoloId(GHolo holo, String holoId) {
        try {
            gHoloMain.getDataService().execute("UPDATE gholo_holo SET id = ? WHERE uuid = ?", holoId, holo.getUuid());
            holo.setId(holoId);
        } catch(Throwable e) { e.printStackTrace(); }
    }

    public void updateHoloLocation(GHolo holo, SimpleLocation location) {
        try {
            gHoloMain.getDataService().execute("UPDATE gholo_holo SET location = ? WHERE uuid = ?",
                    location.toString(),
                    holo.getUuid().toString()
            );
            if(!holo.getRawLocation().getWorld().equals(location.getWorld())) {
                unloadHolo(holo);
                holo.setLocation(location);
                for(GHoloRow holoRow : holo.getRows()) gHoloMain.getEntityUtil().createHoloRowEntity(holoRow);
                return;
            }
            holo.setLocation(location);
            for(GHoloRow holoRow : holo.getRows()) holoRow.getHoloRowEntity().publishUpdate(GHoloUpdateType.LOCATION);
        } catch(Throwable e) { e.printStackTrace(); }
    }

    public void updateHoloRotation(GHolo holo, SimpleRotation rotation) {
        try {
            gHoloMain.getDataService().execute("UPDATE gholo_holo SET rotation = ? WHERE uuid = ?",
                    rotation.toString(),
                    holo.getUuid().toString()
            );
            holo.setRotation(rotation);
            for(GHoloRow holoRow : holo.getRows()) holoRow.getHoloRowEntity().publishUpdate(GHoloUpdateType.LOCATION);
        } catch(Throwable e) { e.printStackTrace(); }
    }

    public void updateHoloData(GHolo holo, GHoloData data) {
        try {
            gHoloMain.getDataService().execute("UPDATE gholo_holo SET data = ? WHERE uuid = ?", data.toString(), holo.getUuid().toString());
            holo.setData(data);
        } catch(Throwable e) { e.printStackTrace(); }
    }

    public void setAllHoloRowContent(GHolo holo, List<String> rows) {
        unloadHolo(holo);
        holo.getRows().clear();
        try {
            gHoloMain.getDataService().execute("DELETE FROM gholo_holo_row where holo_uuid = ?", holo.getUuid().toString());
            for(String row : rows) createHoloRow(holo, row);
        } catch(Throwable e) { e.printStackTrace(); }
    }

    public void copyHolo(GHolo holo, String holoId) {
        try {

        } catch(Throwable e) { e.printStackTrace(); }
    }

    public void removeHolo(GHolo holo) {
        try {
            gHoloMain.getDataService().execute("DELETE FROM gholo_holo WHERE uuid = ?", holo.getUuid().toString());
            gHoloMain.getDataService().execute("DELETE FROM gholo_holo_row WHERE holo_uuid = ?", holo.getUuid().toString());
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