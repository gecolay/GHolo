package dev.geco.gholo.object.holo.exporter.impl;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.object.holo.GHolo;
import dev.geco.gholo.object.holo.GHoloData;
import dev.geco.gholo.object.holo.GHoloRow;
import dev.geco.gholo.object.holo.exporter.GHoloExporter;
import dev.geco.gholo.object.holo.exporter.GHoloExporterResult;
import dev.geco.gholo.object.simple.SimpleLocation;
import dev.geco.gholo.object.simple.SimpleVector;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;

public class FilesExporter extends GHoloExporter {

    @Override
    public @NotNull String getType() { return "files"; }

    @Override
    public @NotNull GHoloExporterResult exportHolos(@NotNull GHoloMain gHoloMain, boolean override) {
        int exported = 0;

        List<GHolo> holos = gHoloMain.getHoloService().getHolos();
        if(holos.isEmpty()) return new GHoloExporterResult(true, exported);

        File holoFileDir = new File(gHoloMain.getDataFolder(), "holos");
        if(holoFileDir.exists()) holoFileDir.mkdir();

        for(GHolo holo : holos) {
            try {
                File holoFile = new File(holoFileDir.getPath(), holo.getId() + ".yml");
                if(!override && holoFile.exists()) continue;
                getHoloFileStructure(holo).save(holoFile);
                exported++;
            } catch(Throwable e) { gHoloMain.getLogger().log(Level.WARNING, "Could not export holo '" + holo.getId() + "'!", e); }
        }

        return new GHoloExporterResult(true, exported);
    }

    private static FileConfiguration getHoloFileStructure(GHolo holo) {
        FileConfiguration structure = new YamlConfiguration();
        structure.set("Holo.location", serializeLocation(holo.getRawLocation()));
        Map<String, Object> holoData = serializeData(holo.getRawData());
        if(!holoData.isEmpty()) structure.set("Holo.data", holoData);
        List<Map<String, Object>> rows = new ArrayList<>();
        for(GHoloRow holoRow : holo.getRows()) {
            Map<String, Object> rowMap = new HashMap<>();
            rowMap.put("content", holoRow.getContent());
            Map<String, Object> offset = serializeOffset(holoRow.getRawOffset());
            if(!offset.isEmpty()) rowMap.put("offset", offset);
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

    private static Map<String, Object> serializeOffset(SimpleVector offset) {
        Map<String, Object> offsetMap = new HashMap<>();
        if(offset.getX() != 0) offsetMap.put("x", offset.getX());
        if(offset.getY() != 0) offsetMap.put("y", offset.getY());
        if(offset.getZ() != 0) offsetMap.put("z", offset.getZ());
        return offsetMap;
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
        if(data.getRawScale().x != GHoloData.DEFAULT_SCALE.x || data.getRawScale().y != GHoloData.DEFAULT_SCALE.y || data.getRawScale().z != GHoloData.DEFAULT_SCALE.z) {
            Map<String, Object> scaleMap = new HashMap<>();
            if(data.getRawScale().x != GHoloData.DEFAULT_SCALE.x) scaleMap.put("x", data.getRawScale().x);
            if(data.getRawScale().y != GHoloData.DEFAULT_SCALE.y) scaleMap.put("y", data.getRawScale().y);
            if(data.getRawScale().z != GHoloData.DEFAULT_SCALE.z) scaleMap.put("z", data.getRawScale().z);
            dataMap.put("scale", scaleMap);
        }
        if(!Objects.equals(data.getRawRotation().getYaw(), GHoloData.DEFAULT_ROTATION.getYaw()) || !Objects.equals(data.getRawRotation().getPitch(), GHoloData.DEFAULT_ROTATION.getPitch())) {
            Map<String, Object> rotationMap = new HashMap<>();
            if(!Objects.equals(data.getRawRotation().getYaw(), GHoloData.DEFAULT_ROTATION.getYaw())) rotationMap.put("yaw", data.getRawRotation().getYaw());
            if(!Objects.equals(data.getRawRotation().getPitch(), GHoloData.DEFAULT_ROTATION.getPitch())) rotationMap.put("pitch", data.getRawRotation().getPitch());
            dataMap.put("rotation", rotationMap);
        }
        if(data.getBrightness() != GHoloData.DEFAULT_BRIGHTNESS) dataMap.put("brightness", data.getBrightness());
        if(!Objects.equals(data.getPermission(), GHoloData.DEFAULT_PERMISSION)) dataMap.put("permission", data.getPermission());
        if(data.getRawSize().getWidth() != GHoloData.DEFAULT_SIZE.getWidth() || data.getRawSize().getHeight() != GHoloData.DEFAULT_SIZE.getHeight()) {
            Map<String, Object> sizeMap = new HashMap<>();
            if(data.getRawSize().getWidth() != GHoloData.DEFAULT_SIZE.getWidth()) sizeMap.put("width", data.getRawSize().getWidth());
            if(data.getRawSize().getHeight() != GHoloData.DEFAULT_SIZE.getHeight()) sizeMap.put("height", data.getRawSize().getHeight());
            dataMap.put("size", sizeMap);
        }
        return dataMap;
    }

}