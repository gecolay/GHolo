package dev.geco.gholo.object.interaction.importer.impl;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.object.interaction.GInteraction;
import dev.geco.gholo.object.interaction.GInteractionAction;
import dev.geco.gholo.object.interaction.GInteractionData;
import dev.geco.gholo.object.interaction.action.GInteractionActionType;
import dev.geco.gholo.object.interaction.importer.GInteractionImporter;
import dev.geco.gholo.object.interaction.importer.GInteractionImporterResult;
import dev.geco.gholo.object.simple.SimpleLocation;
import dev.geco.gholo.object.simple.SimpleSize;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class FilesImporter extends GInteractionImporter {

    @Override
    public @NotNull String getType() { return "files"; }

    @Override
    public @NotNull GInteractionImporterResult importInteractions(@NotNull GHoloMain gHoloMain, boolean override) {
        int imported = 0;

        File interactionFileDir = new File(gHoloMain.getDataFolder(), "interactions");
        if(!interactionFileDir.exists()) return new GInteractionImporterResult(false, 0);

        for(File file : interactionFileDir.listFiles()) {
            String id = file.getName().replace(" ", "").replace(".yml", "");
            try {
                if(!override && gHoloMain.getInteractionService().getInteraction(id) != null) continue;

                FileConfiguration fileContent = YamlConfiguration.loadConfiguration(file);

                World world = Bukkit.getWorld(fileContent.getString("Interaction.location.world", ""));
                if(world == null) throw new RuntimeException("Can not import interaction with id '" + id + "', because the world is invalid!");
                double x = fileContent.getDouble("Interaction.location.x", 0);
                double y = fileContent.getDouble("Interaction.location.y", 0);
                double z = fileContent.getDouble("Interaction.location.z", 0);
                SimpleLocation location = new SimpleLocation(world, x, y, z);

                GInteraction interaction = new GInteraction(UUID.randomUUID(), id, location);

                if(fileContent.get("Interaction.data") != null) {
                    HashMap<String, Object> rawData = new HashMap<>();
                    for(String key : fileContent.getConfigurationSection("Interaction.data").getKeys(false)) rawData.put(key, fileContent.get("Interaction.data." + key));
                    deserializeData(interaction.getRawData(), rawData);
                }

                if(fileContent.get("Interaction.actions") != null) {
                    for(Map<?, ?> actionSection : fileContent.getMapList("Interaction.actions")) {

                        String type = (String) actionSection.get("type");
                        GInteractionActionType interactionActionType = gHoloMain.getInteractionActionService().getInteractionAction(type);
                        if(interactionActionType == null) continue;

                        String parameter = (String) actionSection.get("parameter");
                        if(parameter == null) parameter = "";

                        GInteractionAction action = new GInteractionAction(interaction, interactionActionType, parameter);

                        interaction.addAction(action);
                    }
                }

                gHoloMain.getInteractionService().writeInteraction(interaction, override);
                for(GInteractionAction action : interaction.getActions()) gHoloMain.getInteractionService().writeInteractionAction(action, action.getPosition());

                imported++;
            } catch(Throwable e) { gHoloMain.getLogger().log(Level.SEVERE, "Could not import holo '" + id + "'!", e); }
        }

        return new GInteractionImporterResult(true, imported);
    }

    private void deserializeData(GInteractionData data, HashMap<String, Object> rawData) {
        if(rawData.containsKey("permission")) data.setPermission((String) rawData.get("permission"));
        if(rawData.containsKey("size")) {
            Map<?, ?> sizeMap = (Map<?, ?>) rawData.get("size");
            float sizeWidth = sizeMap.containsKey("width") ? ((Number) sizeMap.get("width")).floatValue() : 1f;
            float sizeHeight = sizeMap.containsKey("height") ? ((Number) sizeMap.get("height")).floatValue() : 1f;
            data.setSize(new SimpleSize(sizeWidth, sizeHeight));
        }
    }

}