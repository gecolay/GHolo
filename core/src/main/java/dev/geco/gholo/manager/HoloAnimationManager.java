package dev.geco.gholo.manager;

import java.io.*;
import java.util.*;

import org.bukkit.configuration.file.*;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.objects.*;

public class HoloAnimationManager {

    public static final char AMIMATION_CHAR = '%';

    private final GHoloMain GPM;
    private final HashMap<String, GHoloAnimation> animations = new HashMap<>();
    private final HashMap<String, List<GHoloRow>> animationSubscriber = new HashMap<>();
    private final List<GHoloRow> placeholderAPISubscriber = new ArrayList<>();
    private final List<UUID> taskIds = new ArrayList<>();

    public HoloAnimationManager(GHoloMain GPluginMain) { GPM = GPluginMain; }

    public Collection<GHoloAnimation> getAnimations() { return animations.values(); }

    public void loadHoloAnimations() {
        stopHoloAnimations();
        File animationsFile = new File(GPM.getDataFolder(), "animations.yml");
        if(!animationsFile.exists()) GPM.saveResource("animations.yml", false);
        FileConfiguration animationsData = YamlConfiguration.loadConfiguration(animationsFile);
        try {
            for(String id : Objects.requireNonNull(animationsData.getConfigurationSection("Animations")).getKeys(false)) {
                animations.put(id.toLowerCase(), new GHoloAnimation(id.toLowerCase(), animationsData.getLong("Animations." + id + ".ticks", 20), animationsData.getStringList("Animations." + id + ".content")));
                animationSubscriber.put(id.toLowerCase(), new ArrayList<>());
            }
            startHoloAnimations();
        } catch (Throwable e) { e.printStackTrace(); }
    }

    public void updateSubscriptionStatus(GHoloRow HoloRow) {
        for(GHoloAnimation animation : animations.values()) {
            // Nested animations should be no problem, because they are covered by their parent animation cycle
            if(HoloRow.getContent().contains(AMIMATION_CHAR + animation.getId() + AMIMATION_CHAR)) animationSubscriber.get(animation.getId()).add(HoloRow);
            else animationSubscriber.get(animation.getId()).remove(HoloRow);
        }
        if(!GPM.getCManager().L_PLACEHOLDER_API || !GPM.hasPlaceholderAPILink() || countAnimationChars(HoloRow.getContent()) < 2) return;
        placeholderAPISubscriber.add(HoloRow);
    }

    public void unsubscribe(GHoloRow HoloRow) {
        for(List<GHoloRow> holoRows : animationSubscriber.values()) holoRows.remove(HoloRow);
        placeholderAPISubscriber.remove(HoloRow);
    }

    private void startHoloAnimations() {
        for(GHoloAnimation animation : animations.values()) {
            UUID taskId = GPM.getTManager().runAtFixedRate(() -> {
                animation.setRow(animation.getRow() + 1 >= animation.getSize() ? 0 : animation.getRow() + 1);
                for(GHoloRow holoRow : animationSubscriber.get(animation.getId().toLowerCase())) {
                    holoRow.getHoloRowEntity().publishUpdate(GHoloRowUpdateType.CONTENT);
                }
            }, false, 0, animation.getTicks());
            taskIds.add(taskId);
        }
        if(!GPM.getCManager().L_PLACEHOLDER_API || !GPM.hasPlaceholderAPILink()) return;
        UUID placeholderAPITaskId = GPM.getTManager().runAtFixedRate(() -> {
            for(Iterator<GHoloRow> it = placeholderAPISubscriber.iterator(); it.hasNext(); ) {
                it.next().getHoloRowEntity().publishUpdate(GHoloRowUpdateType.CONTENT);
            }
        }, false, 0, 10);
        taskIds.add(placeholderAPITaskId);
    }

    public void stopHoloAnimations() {
        for(UUID taskId : taskIds) GPM.getTManager().cancel(taskId);
        taskIds.clear();
        animations.clear();
        animationSubscriber.clear();
        placeholderAPISubscriber.clear();
    }

    public static int countAnimationChars(String Text) {
        int count = 0;
        for(int i = 0; i < Text.length(); i++) if(Text.charAt(i) == AMIMATION_CHAR) count++;
        return count;
    }

}