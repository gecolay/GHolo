package dev.geco.gholo.service;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.object.GHolo;
import dev.geco.gholo.object.GHoloData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.joml.Vector3f;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class HoloImportService {

    public List<String> AVAILABLE_PLUGIN_IMPORTS = new ArrayList<>(); {
        AVAILABLE_PLUGIN_IMPORTS.add("holographic_displays");
        AVAILABLE_PLUGIN_IMPORTS.add("decent_holograms");
        AVAILABLE_PLUGIN_IMPORTS.add("fancy_holograms");
    }

    private final GHoloMain gHoloMain;

    public HoloImportService(GHoloMain gHoloMain) {
        this.gHoloMain = gHoloMain;
    }

    public int importFromPlugin(String plugin) {
        try {
            switch(plugin.toLowerCase()) {
                case "holographic_displays": return importHolographicDisplays();
                case "decent_holograms": return importDecentHolograms();
                case "fancy_holograms": return importFancyHolograms();
                default: return -1;
            }
        } catch(Throwable e) { e.printStackTrace(); }
        return -1;
    }

    private int importHolographicDisplays() {
        int imported = 0;

        File contentFile = new File("plugins/HolographicDisplays/database.yml");
        if(!contentFile.exists()) return imported;

        FileConfiguration fileContent = YamlConfiguration.loadConfiguration(contentFile);
        for(String line : fileContent.getConfigurationSection("").getKeys(false)) {
            if(gHoloMain.getHoloService().getHolo(line) != null) continue;

            String[] args;
            if(fileContent.contains(line + ".location")) {
                args = fileContent.getString(line + ".location", "").split(",");
            } else {
                String basePath = line + ".position.";
                args = new String[4];
                args[0] = fileContent.getString(basePath + "world");
                args[1] = fileContent.getString(basePath + "x");
                args[2] = fileContent.getString(basePath + "y");
                args[3] = fileContent.getString(basePath + "z");
            }
            World world = Bukkit.getWorld(args[0]);
            if(world == null) continue;

            List<String> rows = fileContent.getStringList(line + ".lines");
            List<String> removeContent = fileContent.getStringList(line + ".lines");
            for(String removeContentLine : removeContent) if(removeContentLine.equalsIgnoreCase("null")) rows.remove("null");

            GHolo holo = gHoloMain.getHoloService().createHolo(line, new Location(world, Double.parseDouble(args[1]), Double.parseDouble(args[2]) - 0.51, Double.parseDouble(args[3])));

            for(String row : rows) gHoloMain.getHoloService().createHoloRow(holo, row);

            imported++;
        }

        return imported;
    }

    private int importDecentHolograms() {
        int imported = 0;

        File hologramsDir = new File("plugins/DecentHolograms/holograms");
        if(!hologramsDir.exists()) return imported;

        for(File file : hologramsDir.listFiles()) {
            String name = file.getName().replace(".yml", "");
            if(gHoloMain.getHoloService().getHolo(name) != null) continue;

            FileConfiguration fileContent = YamlConfiguration.loadConfiguration(file);
            if(!fileContent.getBoolean("enabled", false)) continue;

            String[] args = fileContent.getString("location", "").split(":");
            World world = Bukkit.getWorld(args[0]);
            if(world == null) continue;
            Location location = new Location(world, Double.parseDouble(args[1].replace(",", ".")), Double.parseDouble(args[2].replace(",", ".")) - 0.41, Double.parseDouble(args[3].replace(",", ".")));

            GHolo holo = gHoloMain.getHoloService().createHolo(name, location);
            GHoloData data = holo.getDefaultData();

            double range = fileContent.getDouble("display-range", GHoloData.DEFAULT_RANGE);
            if(GHoloData.DEFAULT_RANGE != range) data.setRange(range);

            gHoloMain.getHoloService().updateHoloData(holo, data);

            for(Object section : fileContent.getList("pages")) {
                if(!(section instanceof LinkedHashMap<?,?>)) continue;

                Object lines = ((LinkedHashMap<?, ?>) section).get("lines");
                if(!(lines instanceof ArrayList)) continue;

                for(Object contentMap : (ArrayList<?>) lines) {
                    if(!(contentMap instanceof LinkedHashMap)) continue;

                    String content = (String) ((LinkedHashMap<?, ?>) contentMap).get("content");
                    gHoloMain.getHoloService().createHoloRow(holo, content);
                }
            }

            imported++;
        }

        return imported;
    }

    private int importFancyHolograms() {
        int imported = 0;

        File contentFile = new File("plugins/FancyHolograms/holograms.yml");
        if(!contentFile.exists()) return imported;

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
                Location location = new Location(world, x, y ,z, yaw, pitch);

                GHolo holo = gHoloMain.getHoloService().createHolo(hologram, location);
                GHoloData data = holo.getDefaultData();

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

                gHoloMain.getHoloService().setHoloRows(holo, rows);

                imported++;
            }
        }

        return imported;
    }

}
