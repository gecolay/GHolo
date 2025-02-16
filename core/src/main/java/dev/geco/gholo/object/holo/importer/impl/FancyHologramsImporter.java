package dev.geco.gholo.object.holo.importer.impl;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.object.holo.GHolo;
import dev.geco.gholo.object.holo.GHoloData;
import dev.geco.gholo.object.holo.GHoloRow;
import dev.geco.gholo.object.holo.importer.GHoloImporter;
import dev.geco.gholo.object.holo.importer.GHoloImporterResult;
import dev.geco.gholo.object.location.SimpleLocation;
import dev.geco.gholo.object.location.SimpleRotation;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.io.File;
import java.util.List;
import java.util.UUID;

public class FancyHologramsImporter extends GHoloImporter {

    @Override
    public @NotNull String getType() { return "fancy_holograms"; }

    @Override
    public @NotNull GHoloImporterResult importHolos(@NotNull GHoloMain gHoloMain, boolean override) {
        int imported = 0;

        File contentFile = new File("plugins/FancyHolograms/holograms.yml");
        if(!contentFile.exists()) return new GHoloImporterResult(false, 0);

        FileConfiguration fileContent = YamlConfiguration.loadConfiguration(contentFile);
        int version = fileContent.getInt("version");
        if(version != 2) return new GHoloImporterResult(false, 0);

        for(String id : fileContent.getConfigurationSection("holograms").getKeys(false)) {
            try {
                String type = fileContent.getString("holograms." + id + ".type", "");
                if(!type.equalsIgnoreCase("TEXT")) continue;

                if(!override && gHoloMain.getHoloService().getHolo(id) != null) continue;

                String locationPath = "holograms." + id + ".location.";

                String worldString = fileContent.getString(locationPath + "world", "");
                World world = Bukkit.getWorld(worldString);
                if(world == null) throw new RuntimeException("Can not import holo with id '" + id + "', because the world is invalid!");
                double x = fileContent.getDouble(locationPath + "x");
                double y = fileContent.getDouble(locationPath + "y");
                double z = fileContent.getDouble(locationPath + "z");
                float yaw = (float) fileContent.getDouble(locationPath + "yaw");
                float pitch = (float) fileContent.getDouble(locationPath + "pitch");
                SimpleLocation location = new SimpleLocation(world, x, y ,z);
                SimpleRotation rotation = new SimpleRotation(yaw, pitch);

                GHolo holo = new GHolo(UUID.randomUUID(), id, location);
                holo.setRotation(rotation);
                GHoloData data = holo.getRawData();

                double range = fileContent.getDouble("holograms." + id + ".visibility_distance", GHoloData.DEFAULT_RANGE);
                if(GHoloData.DEFAULT_RANGE != range) data.setRange(range);

                String backgroundColor = fileContent.getString("holograms." + id + ".background", GHoloData.DEFAULT_BACKGROUND_COLOR);
                if(!GHoloData.DEFAULT_BACKGROUND_COLOR.equalsIgnoreCase(backgroundColor)) data.setBackgroundColor(backgroundColor);

                boolean textShadow = fileContent.getBoolean("holograms." + id + ".text_shadow", GHoloData.DEFAULT_HAS_TEXT_SHADOW);
                if(GHoloData.DEFAULT_HAS_TEXT_SHADOW != textShadow) data.setTextShadow(textShadow);

                String textAlignment = fileContent.getString("holograms." + id + ".text_alignment", GHoloData.DEFAULT_TEXT_ALIGNMENT);
                if(!GHoloData.DEFAULT_TEXT_ALIGNMENT.equalsIgnoreCase(textAlignment)) data.setTextAlignment(textAlignment);

                String billboard = fileContent.getString("holograms." + id + ".billboard", GHoloData.DEFAULT_BILLBOARD);
                if(!GHoloData.DEFAULT_BILLBOARD.equalsIgnoreCase(billboard)) data.setBillboard(billboard);

                boolean seeThrough = fileContent.getBoolean("holograms." + id + ".see_through", GHoloData.DEFAULT_CAN_SEE_THROUGH);
                if(GHoloData.DEFAULT_CAN_SEE_THROUGH != seeThrough) data.setSeeThrough(seeThrough);

                Vector3f defaultScale = GHoloData.DEFAULT_SCALE;
                float scaleX = (float) fileContent.getDouble("holograms." + id + ".scale_x", defaultScale.x);
                float scaleY = (float) fileContent.getDouble("holograms." + id + ".scale_y", defaultScale.y);
                float scaleZ = (float) fileContent.getDouble("holograms." + id + ".scale_z", defaultScale.z);
                if(scaleX != defaultScale.x || scaleY != defaultScale.y || scaleZ != defaultScale.z) data.setScale(new Vector3f(scaleX, scaleY, scaleZ));

                String brightness = fileContent.getString("holograms." + id + ".brightness");
                if(brightness != null) data.setBrightness(Byte.parseByte(brightness));

                gHoloMain.getHoloService().writeHolo(holo, override);

                List<String> rows = fileContent.getStringList("holograms." + id + ".text");
                for(String rowContent : rows) {
                    GHoloRow row = new GHoloRow(holo, rowContent);
                    gHoloMain.getHoloService().writeHoloRow(row, row.getPosition());
                }

                imported++;
            } catch(Throwable e) { e.printStackTrace(); }
        }

        return new GHoloImporterResult(true, imported);
    }

}