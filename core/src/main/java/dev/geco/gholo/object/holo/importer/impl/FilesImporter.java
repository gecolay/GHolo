package dev.geco.gholo.object.holo.importer.impl;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.object.holo.GHolo;
import dev.geco.gholo.object.holo.GHoloData;
import dev.geco.gholo.object.holo.GHoloRow;
import dev.geco.gholo.object.holo.importer.GHoloImporter;
import dev.geco.gholo.object.holo.importer.GHoloImporterResult;
import dev.geco.gholo.object.simple.SimpleLocation;
import dev.geco.gholo.object.simple.SimpleVector;
import dev.geco.gholo.object.simple.SimpleRotation;
import dev.geco.gholo.object.simple.SimpleSize;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class FilesImporter extends GHoloImporter {

    @Override
    public @NotNull String getType() { return "files"; }

    @Override
    public @NotNull GHoloImporterResult importHolos(@NotNull GHoloMain gHoloMain, boolean override) {
        int imported = 0;

        File holoFileDir = new File(gHoloMain.getDataFolder(), "holos");
        if(!holoFileDir.exists()) return new GHoloImporterResult(true, 0);

        for(File file : holoFileDir.listFiles()) {
            String id = file.getName().replace(" ", "").replace(".yml", "");
            try {
                if(!override && gHoloMain.getHoloService().getHolo(id) != null) continue;

                FileConfiguration fileContent = YamlConfiguration.loadConfiguration(file);

                World world = Bukkit.getWorld(fileContent.getString("Holo.location.world", ""));
                if(world == null) throw new RuntimeException("Can not import holo with id '" + id + "', because the world is invalid!");
                double x = fileContent.getDouble("Holo.location.x", 0);
                double y = fileContent.getDouble("Holo.location.y", 0);
                double z = fileContent.getDouble("Holo.location.z", 0);
                SimpleLocation location = new SimpleLocation(world, x, y, z);

                GHolo holo = new GHolo(UUID.randomUUID(), id, location);

                if(fileContent.get("Holo.data") != null) {
                    HashMap<String, Object> rawData = new HashMap<>();
                    for(String key : fileContent.getConfigurationSection("Holo.data").getKeys(false)) rawData.put(key, fileContent.get("Holo.data." + key));
                    deserializeData(holo.getRawData(), rawData);
                }

                if(fileContent.get("Holo.rows") != null) {
                    for(Map<?, ?> rowSection : fileContent.getMapList("Holo.rows")) {

                        String content = (String) rowSection.get("content");
                        if(content == null) content = "";

                        GHoloRow row = new GHoloRow(holo, content);

                        if(rowSection.containsKey("offset")) {
                            Map<?, ?> offset = (Map<?, ?>) rowSection.get("offset");
                            Double offsetX = (Double) offset.get("x");
                            Double offsetY = (Double) offset.get("y");
                            Double offsetZ = (Double) offset.get("z");
                            row.setOffset(new SimpleVector(offsetX != null ? offsetX : 0, offsetY != null ? offsetY : 0, offsetZ != null ? offsetZ : 0));
                        }

                        if(rowSection.containsKey("data")) deserializeData(row.getRawData(), (HashMap<String, Object>) rowSection.get("data"));

                        holo.addRow(row);
                    }
                }

                gHoloMain.getHoloService().writeHolo(holo, override);
                for(GHoloRow row : holo.getRows()) gHoloMain.getHoloService().writeHoloRow(row, row.getPosition());

                imported++;
            } catch(Throwable e) { gHoloMain.getLogger().log(Level.SEVERE, "Could not import holo '" + id + "'!", e); }
        }

        return new GHoloImporterResult(true, imported);
    }

    private void deserializeData(GHoloData data, HashMap<String, Object> rawData) {
        if(rawData.containsKey("range")) data.setRange(((Number) rawData.get("range")).doubleValue());
        if(rawData.containsKey("backgroundColor")) data.setBackgroundColor((String) rawData.get("backgroundColor"));
        if(rawData.containsKey("textOpacity")) data.setTextOpacity(((Number) rawData.get("textOpacity")).byteValue());
        if(rawData.containsKey("textShadow")) data.setTextShadow((Boolean) rawData.get("textShadow"));
        if(rawData.containsKey("textAlignment")) data.setTextAlignment((String) rawData.get("textAlignment"));
        if(rawData.containsKey("billboard")) data.setBillboard((String) rawData.get("billboard"));
        if(rawData.containsKey("seeThrough")) data.setSeeThrough((Boolean) rawData.get("seeThrough"));
        if(rawData.containsKey("scale")) {
            try {
                MemorySection scaleMap = (MemorySection) rawData.get("scale");
                float scaleX = (float) scaleMap.getDouble("x", 1f);
                float scaleY = (float) scaleMap.getDouble("y", 1f);
                float scaleZ = (float) scaleMap.getDouble("z", 1f);
                data.setScale(new SimpleVector(scaleX, scaleY, scaleZ));
            } catch(Throwable e) {
                Map<?, ?> scaleMap = (Map<?, ?>) rawData.get("scale");
                float scaleX = scaleMap.containsKey("x") ? ((Number) scaleMap.get("x")).floatValue() : 1f;
                float scaleY = scaleMap.containsKey("y") ? ((Number) scaleMap.get("y")).floatValue() : 1f;
                float scaleZ = scaleMap.containsKey("z") ? ((Number) scaleMap.get("z")).floatValue() : 1f;
                data.setScale(new SimpleVector(scaleX, scaleY, scaleZ));
            }
        }
        if(rawData.containsKey("rotation")) {
            try {
                MemorySection rotationMap = (MemorySection) rawData.get("rotation");
                Float rotationYaw = rotationMap.get("yaw") != null ? (float) rotationMap.getDouble("yaw") : null;
                Float rotationPitch = rotationMap.get("pitch") != null ? (float) rotationMap.getDouble("pitch") : null;
                data.setRotation(new SimpleRotation(rotationYaw, rotationPitch));
            } catch(Throwable e) {
                Map<?, ?> rotationMap = (Map<?, ?>) rawData.get("rotation");
                Float rotationYaw = rotationMap.containsKey("yaw") ? ((Number) rotationMap.get("yaw")).floatValue() : null;
                Float rotationPitch = rotationMap.containsKey("pitch") ? ((Number) rotationMap.get("pitch")).floatValue() : null;
                data.setRotation(new SimpleRotation(rotationYaw, rotationPitch));
            }
        }
        if(rawData.containsKey("brightness")) data.setBrightness(((Number) rawData.get("brightness")).byteValue());
        if(rawData.containsKey("permission")) data.setPermission((String) rawData.get("permission"));
        if(rawData.containsKey("size")) {
            try {
                MemorySection sizeMap = (MemorySection) rawData.get("size");
                float sizeWidth = (float) sizeMap.getDouble("width", 1f);
                float sizeHeight = (float) sizeMap.getDouble("height", 1f);
                data.setSize(new SimpleSize(sizeWidth, sizeHeight));
            } catch(Throwable e) {
                Map<?, ?> sizeMap = (Map<?, ?>) rawData.get("size");
                float sizeWidth = sizeMap.containsKey("width") ? ((Number) sizeMap.get("width")).floatValue() : 1f;
                float sizeHeight = sizeMap.containsKey("height") ? ((Number) sizeMap.get("height")).floatValue() : 1f;
                data.setSize(new SimpleSize(sizeWidth, sizeHeight));
            }
        }
    }

}