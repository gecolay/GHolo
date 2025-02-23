package dev.geco.gholo.object.holo.importer.impl;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.object.holo.GHolo;
import dev.geco.gholo.object.holo.GHoloData;
import dev.geco.gholo.object.holo.GHoloRow;
import dev.geco.gholo.object.holo.importer.GHoloImporter;
import dev.geco.gholo.object.holo.importer.GHoloImporterResult;
import dev.geco.gholo.object.simple.SimpleLocation;
import dev.geco.gholo.object.simple.SimpleOffset;
import dev.geco.gholo.object.simple.SimpleRotation;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.TreeMap;
import java.util.UUID;

public class MigrationV1Importer extends GHoloImporter {

    @Override
    public @NotNull String getType() { return "migration-v1"; }

    @Override
    public @NotNull GHoloImporterResult importHolos(@NotNull GHoloMain gHoloMain, boolean override) {
        int imported = 0;

        try {
            ResultSet migrateTableSet = gHoloMain.getDataService().executeAndGet("""
                        SELECT name
                        FROM sqlite_master
                        WHERE type='table' AND name='holo'
                        UNION ALL
                        SELECT table_name
                        FROM information_schema.tables
                        WHERE table_schema = DATABASE() AND table_name = 'holo';
                    """);

            if(!migrateTableSet.next()) return new GHoloImporterResult(true, imported);
        } catch(SQLException e) {
            e.printStackTrace();
            return new GHoloImporterResult(false, imported);
        }

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
                        } catch (IllegalArgumentException e) {
                            world = Bukkit.getWorld(worldString);
                        }
                        if(world == null) continue;
                        double locationX = resultSet.getDouble("l_x");
                        double locationY = resultSet.getDouble("l_y");
                        double locationZ = resultSet.getDouble("l_z");
                        SimpleLocation location = new SimpleLocation(world, locationX, locationY, locationZ);
                        GHolo holo = new GHolo(UUID.randomUUID(), id, location);

                        String defaultDataString = resultSet.getString("default_data");
                        loadLegacyDataString(holo.getRawData(), defaultDataString);

                        try(ResultSet rowResultSet = gHoloMain.getDataService().executeAndGet("SELECT * FROM holo_row where holo_id = ?", holo.getId())) {
                            TreeMap<Integer, GHoloRow> holoRowMap = new TreeMap<>();

                            while(rowResultSet.next()) {
                                int row = rowResultSet.getInt("row_number");
                                String content = rowResultSet.getString("content");
                                GHoloRow holoRow = new GHoloRow(holo, content);

                                double offsetX = rowResultSet.getDouble("o_x");
                                double offsetY = rowResultSet.getDouble("o_y");
                                double offsetZ = rowResultSet.getDouble("o_z");
                                SimpleOffset offset = new SimpleOffset(offsetX, offsetY, offsetZ);
                                holoRow.setOffset(offset);

                                float locationYaw = rowResultSet.getFloat("l_yaw");
                                float locationPitch = rowResultSet.getFloat("l_pitch");
                                SimpleRotation rotation = new SimpleRotation(locationYaw, locationPitch);
                                holoRow.getRawData().setRotation(rotation);

                                String rowDataString = rowResultSet.getString("data");
                                loadLegacyDataString(holoRow.getRawData(), rowDataString);

                                holoRowMap.put(row, holoRow);
                            }

                            for(GHoloRow holoRow : holoRowMap.values()) holo.addRow(holoRow);
                        }

                        gHoloMain.getHoloService().writeHolo(holo, override);
                        for(GHoloRow row : holo.getRows()) gHoloMain.getHoloService().writeHoloRow(row, row.getPosition());

                        imported++;
                    } catch(Throwable e) { e.printStackTrace(); }
                }
            }

            gHoloMain.getDataService().execute("DROP TABLE holo");
            gHoloMain.getDataService().execute("DROP TABLE holo_row");
        } catch(Throwable e) { e.printStackTrace(); }

        return new GHoloImporterResult(true, imported);
    }

    private void loadLegacyDataString(GHoloData data, String dataString) {
        for(String dataPart : dataString.split("§§")) {
            try {
                String[] dataSplit = dataPart.split("§");
                switch (dataSplit[0]) {
                    case "range":
                        data.setRange(Double.parseDouble(dataSplit[1]));
                        break;
                    case "background_color":
                        data.setBackgroundColor(dataSplit[1]);
                        break;
                    case "text_opacity":
                        data.setTextOpacity(Byte.parseByte(dataSplit[1]));
                        break;
                    case "text_shadow":
                        data.setTextShadow(Boolean.parseBoolean(dataSplit[1]));
                        break;
                    case "text_alignment":
                        data.setTextAlignment(dataSplit[1]);
                        break;
                    case "billboard":
                        data.setBillboard(dataSplit[1]);
                        break;
                    case "see_through":
                        data.setSeeThrough(Boolean.parseBoolean(dataSplit[1]));
                        break;
                    case "scale":
                        String[] scaleSplit = dataSplit[1].split(",");
                        data.setScale(new Vector3f(Float.parseFloat(scaleSplit[0]), Float.parseFloat(scaleSplit[1]), Float.parseFloat(scaleSplit[2])));
                        break;
                    case "brightness":
                        data.setBrightness(Byte.parseByte(dataSplit[1]));
                        break;
                    case "permission":
                        data.setPermission(dataSplit[1]);
                        break;
                }
            } catch(Throwable e) { e.printStackTrace(); }
        }
    }

}