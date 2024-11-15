package dev.geco.gholo.manager;

import java.io.*;
import java.util.*;

import org.bukkit.configuration.file.*;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.objects.*;

public class HoloAnimationManager {

    private final GHoloMain GPM;

    public HoloAnimationManager(GHoloMain GPluginMain) { GPM = GPluginMain; }

    public static final char AMIMATION_CHAR = '%';

    private final HashMap<String, GHoloAnimation> animations = new HashMap<>();

    private final List<UUID> taskIds = new ArrayList<>();

    public HashMap<String, GHoloAnimation> getAnimationSet() { return animations; }

    public Collection<GHoloAnimation> getAnimations() { return animations.values(); }

    public void loadHoloAnimations() {
        stopHoloAnimations();
        File animationsFile = new File(GPM.getDataFolder(), "animations.yml");
        if(!animationsFile.exists()) GPM.saveResource("animations.yml", false);
        FileConfiguration animationsData = YamlConfiguration.loadConfiguration(animationsFile);
        try {
            for(String id : Objects.requireNonNull(animationsData.getConfigurationSection("Animations")).getKeys(false)) {
                animations.put(id.toLowerCase(), new GHoloAnimation(id.toLowerCase(), animationsData.getLong("Animations." + id + ".ticks", 20), animationsData.getStringList("Animations." + id + ".content")));
            }
            startHoloAnimations();
        } catch (Throwable e) { e.printStackTrace(); }
    }

    private void startHoloAnimations() {
        for(GHoloAnimation animation : animations.values()) {
            UUID taskId = GPM.getTManager().runAtFixedRate(() -> {
                animation.setRow(animation.getRow() + 1 >= animation.getSize() ? 0 : animation.getRow() + 1);
            }, false, 0, animation.getTicks());
            taskIds.add(taskId);
        }
    }

    public void stopHoloAnimations() {
        for(UUID taskId : taskIds) GPM.getTManager().cancel(taskId);
        taskIds.clear();
        animations.clear();
    }

}