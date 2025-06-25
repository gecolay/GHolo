package dev.geco.gholo.object.interaction.exporter.impl;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.object.interaction.GInteraction;
import dev.geco.gholo.object.interaction.GInteractionAction;
import dev.geco.gholo.object.interaction.GInteractionData;
import dev.geco.gholo.object.interaction.exporter.GInteractionExporter;
import dev.geco.gholo.object.interaction.exporter.GInteractionExporterResult;
import dev.geco.gholo.object.simple.SimpleLocation;
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

public class FilesExporter extends GInteractionExporter {

    @Override
    public @NotNull String getType() { return "files"; }

    @Override
    public @NotNull GInteractionExporterResult exportInteractions(@NotNull GHoloMain gHoloMain, boolean override) {
        int exported = 0;

        List<GInteraction> interactions = gHoloMain.getInteractionService().getInteractions();
        if(interactions.isEmpty()) return new GInteractionExporterResult(true, exported);

        File interactionFileDir = new File(gHoloMain.getDataFolder(), "interactions");
        if(interactionFileDir.exists()) interactionFileDir.mkdir();

        for(GInteraction interaction : interactions) {
            try {
                File interactionFile = new File(interactionFileDir.getPath(), interaction.getId() + ".yml");
                if(!override && interactionFile.exists()) continue;
                getInteractionFileStructure(interaction).save(interactionFile);
                exported++;
            } catch(Throwable e) { gHoloMain.getLogger().log(Level.WARNING, "Could not export interaction '" + interaction.getId() + "'!", e); }
        }

        return new GInteractionExporterResult(true, exported);
    }

    private static FileConfiguration getInteractionFileStructure(GInteraction interaction) {
        FileConfiguration structure = new YamlConfiguration();
        structure.set("Interaction.location", serializeLocation(interaction.getRawLocation()));
        Map<String, Object> interactionData = serializeData(interaction.getRawData());
        if(!interactionData.isEmpty()) structure.set("Interaction.data", interactionData);
        List<Map<String, Object>> actions = new ArrayList<>();
        for(GInteractionAction interactionAction : interaction.getActions()) {
            Map<String, Object> actionMap = new HashMap<>();
            actionMap.put("type", interactionAction.getInteractionActionType().getType());
            actionMap.put("parameter", interactionAction.getParameter());
            actions.add(actionMap);
        }
        structure.set("Interaction.actions", actions);
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

    private static Map<String, Object> serializeData(GInteractionData data) {
        Map<String, Object> dataMap = new HashMap<>();
        if(!Objects.equals(data.getPermission(), GInteractionData.DEFAULT_PERMISSION)) dataMap.put("permission", data.getPermission());
        if(data.getRawSize().getWidth() != GInteractionData.DEFAULT_SIZE.getWidth() || data.getRawSize().getHeight() != GInteractionData.DEFAULT_SIZE.getHeight()) {
            Map<String, Object> sizeMap = new HashMap<>();
            if(data.getRawSize().getWidth() != GInteractionData.DEFAULT_SIZE.getWidth()) sizeMap.put("width", data.getRawSize().getWidth());
            if(data.getRawSize().getHeight() != GInteractionData.DEFAULT_SIZE.getHeight()) sizeMap.put("height", data.getRawSize().getHeight());
            dataMap.put("size", sizeMap);
        }
        return dataMap;
    }

}