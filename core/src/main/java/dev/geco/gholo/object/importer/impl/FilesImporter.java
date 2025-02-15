package dev.geco.gholo.object.importer.impl;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.object.GHolo;
import dev.geco.gholo.object.GHoloData;
import dev.geco.gholo.object.GHoloRow;
import dev.geco.gholo.object.importer.GHoloImporter;
import dev.geco.gholo.object.importer.GHoloImporterResult;
import dev.geco.gholo.object.location.SimpleLocation;
import dev.geco.gholo.object.location.SimpleOffset;
import dev.geco.gholo.object.location.SimpleRotation;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FilesImporter extends GHoloImporter {

    @Override
    public @NotNull String getType() { return "files"; }

    @Override
    public @NotNull GHoloImporterResult importHolos(@NotNull GHoloMain gHoloMain, boolean override) {
        int imported = 0;

        File holoFileDir = new File( gHoloMain.getDataFolder(), "files");
        if(!holoFileDir.exists()) return new GHoloImporterResult(false, 0);

        for(File file : holoFileDir.listFiles()) {
            try {
                String id = file.getName().replace(" ", "").replace(".yml", "");
                if(!override && gHoloMain.getHoloService().getHolo(id) != null) continue;

                FileConfiguration fileContent = YamlConfiguration.loadConfiguration(file);

                World world = Bukkit.getWorld(fileContent.getString("Holo.location.world", ""));
                if(world == null) throw new RuntimeException("Can not import holo with id '" + id + "', because the world is invalid!");
                double x = fileContent.getDouble("Holo.location.x", 0);
                double y = fileContent.getDouble("Holo.location.x", 0);
                double z = fileContent.getDouble("Holo.location.x", 0);
                SimpleLocation location = new SimpleLocation(world, x, y, z);

                float yaw = (float) fileContent.getDouble("Holo.rotation.yaw", 0);
                float pitch = (float) fileContent.getDouble("Holo.rotation.pitch", 0);
                SimpleRotation rotation = new SimpleRotation(yaw, pitch);

                GHolo holo = new GHolo(UUID.randomUUID(), id, location);
                holo.setRotation(rotation);

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
                            row.setOffset(new SimpleOffset(offsetX != null ? offsetX : 0, offsetY != null ? offsetY : 0, offsetZ != null ? offsetZ : 0));
                        }

                        if(rowSection.containsKey("rotation")) {
                            Map<?, ?> offset = (Map<?, ?>) rowSection.get("rotation");
                            Double rowYaw = (Double) offset.get("yaw");
                            Double rowPitch = (Double) offset.get("pitch");
                            row.setRotation(new SimpleRotation(rowYaw != null ? rowYaw.floatValue() : 0, rowPitch != null ? rowPitch.floatValue() : 0));
                        }

                        if(rowSection.containsKey("data")) {
                            deserializeData(row.getRawData(), (HashMap<String, Object>) rowSection.get("data"));
                        }

                        holo.addRow(row);
                    }
                }

                gHoloMain.getHoloService().writeHolo(holo, override);
                for(GHoloRow row : holo.getRows()) gHoloMain.getHoloService().writeHoloRow(row, row.getPosition());

                imported++;
            } catch(Throwable e) { e.printStackTrace(); }
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
            Map<?, ?> scaleMap = (Map<?, ?>) rawData.get("scale");
            float scaleX = scaleMap.containsKey("x") ? ((Number) scaleMap.get("x")).floatValue() : 1f;
            float scaleY = scaleMap.containsKey("y") ? ((Number) scaleMap.get("y")).floatValue() : 1f;
            float scaleZ = scaleMap.containsKey("z") ? ((Number) scaleMap.get("z")).floatValue() : 1f;
            data.setScale(new Vector3f(scaleX, scaleY, scaleZ));
        }
        if(rawData.containsKey("brightness")) data.setBrightness(((Number) rawData.get("brightness")).byteValue());
        if(rawData.containsKey("permission")) data.setPermission((String) rawData.get("permission"));
    }

}