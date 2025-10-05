package dev.geco.gholo.service;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.object.holo.GHolo;
import dev.geco.gholo.object.holo.GHoloData;
import dev.geco.gholo.object.holo.GHoloRow;
import dev.geco.gholo.object.holo.GHoloUpdateType;
import dev.geco.gholo.object.simple.SimpleLocation;
import dev.geco.gholo.object.simple.SimpleVector;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.UUID;
import java.util.logging.Level;

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
                    data TEXT
                );
            """);
            gHoloMain.getDataService().execute("""
                CREATE TABLE IF NOT EXISTS gholo_holo_row (
                    position INTEGER,
                    holo_uuid TEXT,
                    content TEXT,
                    `offset` TEXT,
                    data TEXT
                );
            """);
        } catch(SQLException e) { gHoloMain.getLogger().log(Level.SEVERE, "Could not create holo database tables!", e); }
    }

    public List<GHolo> getHolos() { return new ArrayList<>(holos); }

    public List<GHolo> getNearHolos(Location location, double range) { return holos.stream().filter(holo -> holo.getRawLocation().getWorld().equals(location.getWorld()) && holo.getRawLocation().distance(location) <= range).toList(); }

    public GHolo getHolo(String holoId) { return holos.stream().filter(holo -> holo.getId().equalsIgnoreCase(holoId)).findFirst().orElse(null); }

    public int getHoloCount() { return holos.size(); }

    public int getHoloRowCount() { return holos.stream().mapToInt(holo -> holo.getRows().size()).sum(); }

    public GHolo createHolo(String holoId, SimpleLocation location) {
        try {
            GHolo holo = new GHolo(UUID.randomUUID(), holoId, location);
            writeHolo(holo, false);
            holos.add(holo);
            return holo;
        } catch(SQLException e) { gHoloMain.getLogger().log(Level.SEVERE, "Could not create holo '" + holoId + "'!", e); }
        return null;
    }

    public GHoloRow addHoloRow(GHolo holo, String content) {
        try {
            int position = holo.getRows().size();
            double sizeBetweenRows = gHoloMain.getConfigService().DEFAULT_SIZE_BETWEEN_ROWS;
            double rowOffset = sizeBetweenRows * position;
            SimpleVector offset = new SimpleVector(0, -rowOffset, 0);

            GHoloRow holoRow = new GHoloRow(holo, gHoloMain.getTextFormatUtil().replaceSymbols(content));
            holoRow.setOffset(offset);
            writeHoloRow(holoRow, position);
            holo.addRow(holoRow);

            gHoloMain.getEntityUtil().createHoloRowEntity(holoRow);
            gHoloMain.getHoloAnimationService().updateSubscriptionStatus(holoRow);

            return holoRow;
        } catch(SQLException e) { gHoloMain.getLogger().log(Level.SEVERE, "Could not create holo row for holo '" + holo.getId() + "'!", e); }
        return null;
    }

    public GHoloRow insertHoloRow(GHolo holo, int position, String content, boolean updateOffsets) {
        try {
            double sizeBetweenRows = gHoloMain.getConfigService().DEFAULT_SIZE_BETWEEN_ROWS;
            double rowOffset = sizeBetweenRows * position;
            SimpleVector offset = new SimpleVector(0, -rowOffset, 0);

            if(updateOffsets) {
                try(ResultSet moveOffsetResultSet = gHoloMain.getDataService().executeAndGet("SELECT position, `offset` FROM gholo_holo_row WHERE holo_uuid = ? AND position >= ?", holo.getUuid().toString(), position)) {
                    while(moveOffsetResultSet.next()) {
                        int movePosition = moveOffsetResultSet.getInt("position");
                        SimpleVector moveOffset = SimpleVector.fromString(moveOffsetResultSet.getString("offset"));
                        if(moveOffset == null) continue;
                        moveOffset.setY(moveOffset.getY() - sizeBetweenRows);
                        gHoloMain.getDataService().execute("UPDATE gholo_holo_row SET `offset` = ? WHERE holo_uuid = ? AND position = ?", moveOffset.toString(), holo.getUuid().toString(), movePosition);
                    }
                }
                for(GHoloRow updateHoloRow : holo.getRows().subList(position, holo.getRows().size())) {
                    SimpleVector moveOffset = updateHoloRow.getOffset();
                    moveOffset.setY(moveOffset.getY() - sizeBetweenRows);
                    updateHoloRow.setOffset(moveOffset);
                    if(updateHoloRow.getHoloRowContent() != null) updateHoloRow.getHoloRowContent().publishUpdate(GHoloUpdateType.LOCATION);
                }
            }

            gHoloMain.getDataService().execute("UPDATE gholo_holo_row SET position = position + 1 WHERE holo_uuid = ? AND position >= ?", holo.getUuid().toString(), position);

            GHoloRow holoRow = new GHoloRow(holo, gHoloMain.getTextFormatUtil().replaceSymbols(content));
            holoRow.setOffset(offset);
            writeHoloRow(holoRow, position);
            holo.insertRow(holoRow, position);

            gHoloMain.getEntityUtil().createHoloRowEntity(holoRow);
            gHoloMain.getHoloAnimationService().updateSubscriptionStatus(holoRow);

            return holoRow;
        } catch(SQLException e) { gHoloMain.getLogger().log(Level.SEVERE, "Could not insert holo row for holo '" + holo.getId() + "'!", e); }
        return null;
    }

    public void updateHoloRowContent(GHoloRow holoRow, String content) {
        try {
            gHoloMain.getDataService().execute("UPDATE gholo_holo_row SET content = ? WHERE position = ? AND holo_uuid = ?", content, holoRow.getPosition(), holoRow.getHolo().getUuid().toString());
            holoRow.setContent(gHoloMain.getTextFormatUtil().replaceSymbols(content));
            if(holoRow.getHoloRowContent() != null) holoRow.getHoloRowContent().publishUpdate(GHoloUpdateType.CONTENT);
            gHoloMain.getHoloAnimationService().updateSubscriptionStatus(holoRow);
        } catch(SQLException e) { gHoloMain.getLogger().log(Level.SEVERE, "Could not update holo row content of holo '" + holoRow.getHolo().getId() + "'!", e); }
    }

    public void updateHoloRowOffset(GHoloRow holoRow, SimpleVector offset) {
        try {
            gHoloMain.getDataService().execute("UPDATE gholo_holo_row SET `offset` = ? WHERE position = ? AND holo_uuid = ?",
                    offset.toString(),
                    holoRow.getPosition(),
                    holoRow.getHolo().getUuid().toString()
            );
            holoRow.setOffset(offset);
            if(holoRow.getHoloRowContent() != null) holoRow.getHoloRowContent().publishUpdate(GHoloUpdateType.LOCATION);
        } catch(SQLException e) { gHoloMain.getLogger().log(Level.SEVERE, "Could not update holo row `offset` of holo '" + holoRow.getHolo().getId() + "'!", e); }
    }

    public void updateHoloRowData(GHoloRow holoRow, GHoloData data) {
        try {
            gHoloMain.getDataService().execute("UPDATE gholo_holo_row SET data = ? WHERE position = ? AND holo_uuid = ?", data.toString(), holoRow.getPosition(), holoRow.getHolo().getUuid().toString());
            holoRow.setData(data);
        } catch(SQLException e) { gHoloMain.getLogger().log(Level.SEVERE, "Could not update holo row data of holo '" + holoRow.getHolo().getId() + "'!", e); }
    }

    public void removeHoloRow(GHoloRow holoRow, boolean updateOffsets) {
        try {
            GHolo holo = holoRow.getHolo();
            int position = holoRow.getPosition();
            double sizeBetweenRows = gHoloMain.getConfigService().DEFAULT_SIZE_BETWEEN_ROWS;
            gHoloMain.getDataService().execute("DELETE FROM gholo_holo_row where holo_uuid = ? AND position = ?", holo.getUuid().toString(), position);
            if(updateOffsets) {
                try(ResultSet moveOffsetResultSet = gHoloMain.getDataService().executeAndGet("SELECT position, `offset` FROM gholo_holo_row WHERE holo_uuid = ? AND position > ?", holo.getUuid().toString(), position)) {
                    while(moveOffsetResultSet.next()) {
                        int movePosition = moveOffsetResultSet.getInt("position");
                        SimpleVector moveOffset = SimpleVector.fromString(moveOffsetResultSet.getString("offset"));
                        if(moveOffset == null) continue;
                        moveOffset.setY(moveOffset.getY() + sizeBetweenRows);
                        gHoloMain.getDataService().execute("UPDATE gholo_holo_row SET `offset` = ? WHERE holo_uuid = ? AND position = ?", moveOffset.toString(), holo.getUuid().toString(), movePosition);
                    }
                }
                for(GHoloRow updateHoloRow : holo.getRows().subList(position + 1, holo.getRows().size())) {
                    SimpleVector moveOffset = updateHoloRow.getOffset();
                    moveOffset.setY(moveOffset.getY() + sizeBetweenRows);
                    updateHoloRow.setOffset(moveOffset);
                    if(updateHoloRow.getHoloRowContent() != null) updateHoloRow.getHoloRowContent().publishUpdate(GHoloUpdateType.LOCATION);
                }
            }
            gHoloMain.getDataService().execute("UPDATE gholo_holo_row SET position = position - 1 WHERE holo_uuid = ? AND position > ?", holo.getUuid().toString(), position);
            holo.removeRow(position);
            if(holoRow.getHoloRowContent() != null) holoRow.getHoloRowContent().unloadHoloRow();
            gHoloMain.getHoloAnimationService().unsubscribe(holoRow);
        } catch(SQLException e) { gHoloMain.getLogger().log(Level.SEVERE, "Could not remove holo row of holo '" + holoRow.getHolo().getId() + "'!", e); }
    }

    public void updateHoloId(GHolo holo, String holoId) {
        try {
            gHoloMain.getDataService().execute("UPDATE gholo_holo SET id = ? WHERE uuid = ?", holoId, holo.getUuid().toString());
            holo.setId(holoId);
        } catch(SQLException e) { gHoloMain.getLogger().log(Level.SEVERE, "Could not rename holo '" + holo.getId() + "' to '" + holoId + "'!", e); }
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
            for(GHoloRow holoRow : holo.getRows()) if(holoRow.getHoloRowContent() != null) holoRow.getHoloRowContent().publishUpdate(GHoloUpdateType.LOCATION);
        } catch(SQLException e) { gHoloMain.getLogger().log(Level.SEVERE, "Could not update holo location of holo '" + holo.getId() + "'!", e); }
    }

    public void updateHoloData(GHolo holo, GHoloData data) {
        try {
            gHoloMain.getDataService().execute("UPDATE gholo_holo SET data = ? WHERE uuid = ?", data.toString(), holo.getUuid().toString());
            holo.setData(data);
        } catch(SQLException e) { gHoloMain.getLogger().log(Level.SEVERE, "Could not update holo data of holo '" + holo.getId() + "'!", e); }
    }

    public void setAllHoloRowContent(GHolo holo, List<String> rows) {
        unloadHolo(holo);
        holo.getRows().clear();
        try {
            gHoloMain.getDataService().execute("DELETE FROM gholo_holo_row where holo_uuid = ?", holo.getUuid().toString());
            for(String row : rows) addHoloRow(holo, row);
        } catch(SQLException e) { gHoloMain.getLogger().log(Level.SEVERE, "Could not set all holo rows of holo '" + holo.getId() + "'!", e); }
    }

    public void copyHolo(GHolo holo, String holoId) {
        try {
            GHolo newHolo = new GHolo(UUID.randomUUID(), holoId, holo.getLocation());
            newHolo.setData(holo.getData());
            writeHolo(newHolo, false);
            holos.add(newHolo);
            for(GHoloRow row : holo.getRows()) {
                GHoloRow newRow = new GHoloRow(newHolo, row.getContent());
                newRow.setOffset(row.getOffset());
                newRow.setData(row.getData());
                writeHoloRow(newRow, row.getPosition());
                newHolo.addRow(newRow);
                gHoloMain.getEntityUtil().createHoloRowEntity(newRow);
                gHoloMain.getHoloAnimationService().updateSubscriptionStatus(newRow);
            }
        } catch(SQLException e) { gHoloMain.getLogger().log(Level.SEVERE, "Could not copy holo '" + holo.getId() + "' to '" + holoId + "'!", e); }
    }

    public void removeHolo(GHolo holo) {
        try {
            gHoloMain.getDataService().execute("DELETE FROM gholo_holo WHERE uuid = ?", holo.getUuid().toString());
            gHoloMain.getDataService().execute("DELETE FROM gholo_holo_row WHERE holo_uuid = ?", holo.getUuid().toString());
            holos.remove(holo);
            for(GHoloRow holoRow : holo.getRows()) gHoloMain.getHoloAnimationService().unsubscribe(holoRow);
            unloadHolo(holo);
        } catch(SQLException e) { gHoloMain.getLogger().log(Level.SEVERE, "Could not remove holo '" + holo.getId() + "'!", e); }
    }

    public void loadHolos(@Nullable World world) {
        try {
            List<UUID> loadedHolos = holos.stream().map(GHolo::getUuid).toList();
            try(ResultSet resultSet = gHoloMain.getDataService().executeAndGet("SELECT * FROM gholo_holo")) {
                holowhile: while(resultSet.next()) {
                    String id = resultSet.getString("id");
                    try {
                        UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                        if(loadedHolos.contains(uuid)) continue;

                        SimpleLocation location = SimpleLocation.fromString(resultSet.getString("location"));
                        if(location == null || location.getWorld() == null || (world != null && world.equals(location.getWorld()))) continue;

                        GHolo holo = new GHolo(uuid, id, location);

                        String dataString = resultSet.getString("data");
                        holo.getRawData().loadString(dataString);

                        try(ResultSet rowResultSet = gHoloMain.getDataService().executeAndGet("SELECT * FROM gholo_holo_row where holo_uuid = ?", uuid.toString())) {
                            TreeMap<Integer, GHoloRow> holoRowMap = new TreeMap<>();

                            while(rowResultSet.next()) {
                                int position = rowResultSet.getInt("position");
                                String content = gHoloMain.getTextFormatUtil().replaceSymbols(rowResultSet.getString("content"));
                                SimpleVector offset = SimpleVector.fromString(rowResultSet.getString("offset"));
                                if(offset == null) {
                                    gHoloMain.getLogger().warning("Could not load holo row '" + position + "' of holo '" + id + "', invalid location!");
                                    continue holowhile;
                                }
                                GHoloRow holoRow = new GHoloRow(holo, content);
                                holoRow.setOffset(offset);

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

                        holos.add(holo);
                    } catch(Throwable e) { gHoloMain.getLogger().log(Level.SEVERE, "Could not load holo '" + id + "'!", e); }
                }
            }
        } catch(SQLException e) { gHoloMain.getLogger().log(Level.SEVERE, "Could not load holos!", e); }
    }

    public void loadHolosForPlayer(Player player) { for(GHolo holo : holos) loadHoloForPlayer(holo, player); }

    public void loadHolo(GHolo holo) { for(Player player : holo.getRawLocation().getWorld().getPlayers()) loadHoloForPlayer(holo, player); }

    public void loadHoloForPlayer(GHolo holo, Player player) { for(GHoloRow row : holo.getRows()) if(row.getHoloRowContent() != null) row.getHoloRowContent().loadHoloRow(player); }

    public void unloadHolosForPlayer(Player player) { for(GHolo holo : holos) unloadHoloForPlayer(holo, player); }

    public void unloadHolo(GHolo holo) { for(Player player : holo.getRawLocation().getWorld().getPlayers()) unloadHoloForPlayer(holo, player); }

    public void unloadHoloForPlayer(GHolo holo, Player player) { for(GHoloRow row : holo.getRows()) if(row.getHoloRowContent() != null) row.getHoloRowContent().unloadHoloRow(player); }

    public void clearHolosCurrentContentForPlayer(Player player) { for(GHolo holo : holos) clearHoloCurrentContentForPlayer(holo, player); }

    private void clearHoloCurrentContentForPlayer(GHolo holo, Player player) { for(GHoloRow row : holo.getRows()) if(row.getHoloRowContent() != null) row.getHoloRowContent().getCurrentContentTypes().remove(player.getUniqueId()); }

    public void unloadHolos(@Nullable World world) {
        for(GHolo holo : holos) {
            if(world != null && world.equals(holo.getRawLocation().getWorld())) continue;
            for(GHoloRow holoRow : holo.getRows()) gHoloMain.getHoloAnimationService().unsubscribe(holoRow);
            unloadHolo(holo);
        }
        if(world == null) holos.clear();
        else holos.removeIf(holo -> holo.getRawLocation().getWorld().equals(world));
    }

    public void writeHolo(GHolo holo, boolean override) throws SQLException {
        if(override) {
            ResultSet resultSet = gHoloMain.getDataService().executeAndGet("SELECT uuid FROM gholo_holo WHERE id = ?", holo.getId());
            while (resultSet.next()) {
                String uuid = resultSet.getString("uuid");
                gHoloMain.getDataService().execute("DELETE FROM gholo_holo WHERE uuid = ?", uuid);
                gHoloMain.getDataService().execute("DELETE FROM gholo_holo_row WHERE holo_uuid = ?", uuid);
            }
        }
        gHoloMain.getDataService().execute("INSERT INTO gholo_holo (uuid, id, location, data) VALUES (?, ?, ?, ?)",
                holo.getUuid().toString(),
                holo.getId(),
                holo.getRawLocation().toString(),
                holo.getData().toString()
        );
    }

    public void writeHoloRow(GHoloRow holoRow, int position) throws SQLException {
        gHoloMain.getDataService().execute("INSERT INTO gholo_holo_row (position, holo_uuid, content, `offset`, data) VALUES (?, ?, ?, ?, ?)",
                position,
                holoRow.getHolo().getUuid().toString(),
                holoRow.getContent(),
                holoRow.getRawOffset().toString(),
                holoRow.getRawData().toString()
        );
    }

}