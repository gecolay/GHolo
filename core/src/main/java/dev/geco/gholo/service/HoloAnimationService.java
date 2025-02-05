package dev.geco.gholo.service;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.object.GHoloAnimation;
import dev.geco.gholo.object.GHoloRow;
import dev.geco.gholo.object.GHoloRowUpdateType;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class HoloAnimationService {

    public static final char AMIMATION_CHAR = '%';

    private final GHoloMain gHoloMain;
    private final HashMap<String, GHoloAnimation> animations = new HashMap<>();
    private final ConcurrentHashMap<String, ConcurrentLinkedQueue<GHoloRow>> animationSubscriber = new ConcurrentHashMap<>();
    private final ConcurrentLinkedQueue<GHoloRow> placeholderAPISubscriber = new ConcurrentLinkedQueue<>();
    private final List<UUID> taskIds = new ArrayList<>();

    public HoloAnimationService(GHoloMain gHoloMain) {
        this.gHoloMain = gHoloMain;
    }

    public Collection<GHoloAnimation> getAnimations() { return animations.values(); }

    public void loadHoloAnimations() {
        stopHoloAnimations();
        File animationsFile = new File(gHoloMain.getDataFolder(), "animations.yml");
        if(!animationsFile.exists()) gHoloMain.saveResource("animations.yml", false);
        FileConfiguration animationsData = YamlConfiguration.loadConfiguration(animationsFile);
        try {
            for(String id : animationsData.getConfigurationSection("Animations").getKeys(false)) {
                animations.put(id.toLowerCase(), new GHoloAnimation(id.toLowerCase(), animationsData.getLong("Animations." + id + ".ticks", 20), animationsData.getStringList("Animations." + id + ".content")));
                animationSubscriber.put(id.toLowerCase(), new ConcurrentLinkedQueue<>());
            }
            startHoloAnimations();
        } catch(Throwable e) { e.printStackTrace(); }
    }

    public void updateSubscriptionStatus(GHoloRow holoRow) {
        for(GHoloAnimation animation : animations.values()) {
            // Nested animations should be no problem, because they are covered by their parent animation cycle
            if(holoRow.getContent().contains(AMIMATION_CHAR + animation.getId() + AMIMATION_CHAR)) animationSubscriber.get(animation.getId()).add(holoRow);
            else animationSubscriber.get(animation.getId()).remove(holoRow);
        }
        if(!gHoloMain.hasPlaceholderAPILink() || countAnimationChars(holoRow.getContent()) < 2) return;
        placeholderAPISubscriber.add(holoRow);
    }

    public void unsubscribe(GHoloRow holoRow) {
        for(ConcurrentLinkedQueue<GHoloRow> holoRows : animationSubscriber.values()) holoRows.remove(holoRow);
        placeholderAPISubscriber.remove(holoRow);
    }

    private void startHoloAnimations() {
        for(GHoloAnimation animation : animations.values()) {
            UUID taskId = gHoloMain.getTaskService().runAtFixedRate(() -> {
                animation.setRowId(animation.getRowId() + 1 >= animation.getSize() ? 0 : animation.getRowId() + 1);
                for(GHoloRow holoRow : animationSubscriber.get(animation.getId().toLowerCase())) {
                    holoRow.getHoloRowEntity().publishUpdate(GHoloRowUpdateType.CONTENT);
                }
            }, false, 0, animation.getTicks());
            taskIds.add(taskId);
        }
        if(!gHoloMain.hasPlaceholderAPILink()) return;
        UUID placeholderAPITaskId = gHoloMain.getTaskService().runAtFixedRate(() -> {
            for(GHoloRow holoRow : placeholderAPISubscriber) {
                holoRow.getHoloRowEntity().publishUpdate(GHoloRowUpdateType.CONTENT);
            }
        }, false, 0, 10);
        taskIds.add(placeholderAPITaskId);
    }

    public void stopHoloAnimations() {
        for(UUID taskId : taskIds) gHoloMain.getTaskService().cancel(taskId);
        taskIds.clear();
        animations.clear();
        animationSubscriber.clear();
        placeholderAPISubscriber.clear();
    }

    public static int countAnimationChars(String text) {
        int count = 0;
        for(int i = 0; i < text.length(); i++) if(text.charAt(i) == AMIMATION_CHAR) count++;
        return count;
    }

}