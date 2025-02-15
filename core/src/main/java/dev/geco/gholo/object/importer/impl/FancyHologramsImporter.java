package dev.geco.gholo.object.importer.impl;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.object.GHolo;
import dev.geco.gholo.object.GHoloData;
import dev.geco.gholo.object.importer.GHoloImporter;
import dev.geco.gholo.object.importer.GHoloImporterResult;
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
        if(version == 2) {
            for(String hologram : fileContent.getConfigurationSection("holograms").getKeys(false)) {
                String type = fileContent.getString("holograms." + hologram + ".type", "");
                if(!type.equalsIgnoreCase("TEXT")) continue;

                String locationPath = "holograms." + hologram + ".location.";

                String worldString = fileContent.getString(locationPath + "world");
                World world = Bukkit.getWorld(worldString);
                if(world == null) continue;
                double x = fileContent.getDouble(locationPath + "x");
                double y = fileContent.getDouble(locationPath + "y");
                double z = fileContent.getDouble(locationPath + "z");
                float yaw = (float) fileContent.getDouble(locationPath + "yaw");
                float pitch = (float) fileContent.getDouble(locationPath + "pitch");
                SimpleLocation location = new SimpleLocation(world, x, y ,z);
                SimpleRotation rotation = new SimpleRotation(yaw, pitch);

                GHolo holo = gHoloMain.getHoloService().createHolo(hologram, location);
                holo.setRotation(rotation);
                GHoloData data = holo.getData();

                double range = fileContent.getDouble("holograms." + hologram + ".visibility_distance", GHoloData.DEFAULT_RANGE);
                if(GHoloData.DEFAULT_RANGE != range) data.setRange(range);

                String backgroundColor = fileContent.getString("holograms." + hologram + ".background", GHoloData.DEFAULT_BACKGROUND_COLOR);
                if(!GHoloData.DEFAULT_BACKGROUND_COLOR.equalsIgnoreCase(backgroundColor)) data.setBackgroundColor(backgroundColor);

                boolean textShadow = fileContent.getBoolean("holograms." + hologram + ".text_shadow", GHoloData.DEFAULT_HAS_TEXT_SHADOW);
                if(GHoloData.DEFAULT_HAS_TEXT_SHADOW != textShadow) data.setTextShadow(textShadow);

                String textAlignment = fileContent.getString("holograms." + hologram + ".text_alignment", GHoloData.DEFAULT_TEXT_ALIGNMENT);
                if(!GHoloData.DEFAULT_TEXT_ALIGNMENT.equalsIgnoreCase(textAlignment)) data.setTextAlignment(textAlignment);

                String billboard = fileContent.getString("holograms." + hologram + ".billboard", GHoloData.DEFAULT_BILLBOARD);
                if(!GHoloData.DEFAULT_BILLBOARD.equalsIgnoreCase(billboard)) data.setBillboard(billboard);

                boolean seeThrough = fileContent.getBoolean("holograms." + hologram + ".see_through", GHoloData.DEFAULT_CAN_SEE_THROUGH);
                if(GHoloData.DEFAULT_CAN_SEE_THROUGH != seeThrough) data.setSeeThrough(seeThrough);

                Vector3f defaultScale = GHoloData.DEFAULT_SCALE;
                float scaleX = (float) fileContent.getDouble("holograms." + hologram + ".scale_x", defaultScale.x);
                float scaleY = (float) fileContent.getDouble("holograms." + hologram + ".scale_y", defaultScale.y);
                float scaleZ = (float) fileContent.getDouble("holograms." + hologram + ".scale_z", defaultScale.z);
                if(scaleX != defaultScale.x || scaleY != defaultScale.y || scaleZ != defaultScale.z) data.setScale(new Vector3f(scaleX, scaleY, scaleZ));

                String brightness = fileContent.getString("holograms." + hologram + ".brightness");
                if(brightness != null) data.setBrightness(Byte.parseByte(brightness));

                gHoloMain.getHoloService().updateHoloData(holo, data);

                List<String> rows = fileContent.getStringList("holograms." + hologram + ".text");
                gHoloMain.getHoloService().setAllHoloRowContent(holo, rows);

                imported++;
            }
        }

        return new GHoloImporterResult(true, imported);
    }

}