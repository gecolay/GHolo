package dev.geco.gholo.object.interaction.exporter.impl;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.object.holo.GHolo;
import dev.geco.gholo.object.holo.GHoloData;
import dev.geco.gholo.object.holo.GHoloRow;
import dev.geco.gholo.object.interaction.exporter.GInteractionExporter;
import dev.geco.gholo.object.interaction.exporter.GInteractionExporterResult;
import dev.geco.gholo.object.location.SimpleLocation;
import dev.geco.gholo.object.location.SimpleOffset;
import dev.geco.gholo.object.location.SimpleRotation;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FilesExporter extends GInteractionExporter {

    private final String FILE_FORMAT = ".yml";

    @Override
    public @NotNull String getType() { return "files"; }

    @Override
    public @NotNull GInteractionExporterResult exportInteractions(@NotNull GHoloMain gHoloMain, boolean override) {
        int exported = 0;

        List<GHolo> holos = gHoloMain.getHoloService().getHolos();
        if(holos.isEmpty()) return new GInteractionExporterResult(true, exported);

        File holoFileDir = new File(gHoloMain.getDataFolder(), "interactions");
        if(holoFileDir.exists()) holoFileDir.mkdir();

        for(GHolo holo : holos) {
            try {
                File holoFile = new File(holoFileDir.getPath(), holo.getId() + FILE_FORMAT);
                if(!override && holoFile.exists()) continue;
                getHoloFileStructure(holo).save(holoFile);
            } catch(Throwable e) { e.printStackTrace(); }
        }

        return new GInteractionExporterResult(true, exported);
    }

    private static FileConfiguration getHoloFileStructure(GHolo holo) {
        FileConfiguration structure = new YamlConfiguration();
        structure.set("Holo.location", serializeLocation(holo.getRawLocation()));
        structure.set("Holo.rotation", serializeRotation(holo.getRawRotation()));
        Map<String, Object> holoData = serializeData(holo.getRawData());
        if(!holoData.isEmpty()) structure.set("Holo.data", holoData);
        List<Map<String, Object>> rows = new ArrayList<>();
        for (GHoloRow holoRow : holo.getRows()) {
            Map<String, Object> rowMap = new HashMap<>();
            rowMap.put("content", holoRow.getContent());
            Map<String, Object> offset = serializeOffset(holoRow.getRawOffset());
            if(!offset.isEmpty()) rowMap.put("offset", offset);
            Map<String, Object> rotation = serializeRotation(holoRow.getRawRotation());
            if(!rotation.isEmpty()) rowMap.put("rotation", rotation);
            Map<String, Object> rowData = serializeData(holoRow.getRawData());
            if(!rowData.isEmpty()) rowMap.put("data", rowData);
            rows.add(rowMap);
        }
        structure.set("Holo.rows", rows);
        return structure;
    }

    private static Map<String, Object> serializeLocation(SimpleLocation location) {
        Map<String, Object> locationMap = new HashMap<>();
        locationMap.put("world", location.getWorld().getName());
        locationMap.put("x", location.getX());
        locationMap.put("y", location.getY());
        locationMap.put("z", location.getZ());
        return locationMap;
    }

    private static Map<String, Object> serializeOffset(SimpleOffset offset) {
        Map<String, Object> offsetMap = new HashMap<>();
        if(offset.getX() != 0) offsetMap.put("x", offset.getX());
        if(offset.getY() != 0) offsetMap.put("y", offset.getY());
        if(offset.getZ() != 0) offsetMap.put("z", offset.getZ());
        return offsetMap;
    }

    private static Map<String, Object> serializeRotation(SimpleRotation rotation) {
        Map<String, Object> rotationMap = new HashMap<>();
        if(rotation.getYaw() != 0) rotationMap.put("yaw", rotation.getYaw());
        if(rotation.getPitch() != 0) rotationMap.put("pitch", rotation.getPitch());
        return rotationMap;
    }

    private static Map<String, Object> serializeData(GHoloData data) {
        Map<String, Object> dataMap = new HashMap<>();
        if(data.getRange() != GHoloData.DEFAULT_RANGE) dataMap.put("range", data.getRange());
        if(!Objects.equals(data.getBackgroundColor(), GHoloData.DEFAULT_BACKGROUND_COLOR)) dataMap.put("backgroundColor", data.getBackgroundColor());
        if(data.getTextOpacity() != GHoloData.DEFAULT_TEXT_OPACITY) dataMap.put("textOpacity", data.getTextOpacity());
        if(data.getTextShadow() != GHoloData.DEFAULT_HAS_TEXT_SHADOW) dataMap.put("textShadow", data.getTextShadow());
        if(!Objects.equals(data.getTextAlignment(), GHoloData.DEFAULT_TEXT_ALIGNMENT)) dataMap.put("textAlignment", data.getTextAlignment());
        if(!Objects.equals(data.getBillboard(), GHoloData.DEFAULT_BILLBOARD)) dataMap.put("billboard", data.getBillboard());
        if(data.getSeeThrough() != GHoloData.DEFAULT_CAN_SEE_THROUGH) dataMap.put("seeThrough", data.getSeeThrough());
        if(!Objects.equals(data.getScale(), GHoloData.DEFAULT_SCALE)) {
            Map<String, Object> scaleMap = new HashMap<>();
            scaleMap.put("x", data.getScale().x);
            scaleMap.put("y", data.getScale().y);
            scaleMap.put("z", data.getScale().z);
            dataMap.put("scale", scaleMap);
        }
        if(data.getBrightness() != GHoloData.DEFAULT_BRIGHTNESS) dataMap.put("brightness", data.getBrightness());
        if(!Objects.equals(data.getPermission(), GHoloData.DEFAULT_PERMISSION)) dataMap.put("permission", data.getPermission());
        return dataMap;
    }

}