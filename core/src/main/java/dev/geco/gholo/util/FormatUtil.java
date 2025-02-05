package dev.geco.gholo.util;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.object.GHoloAnimation;
import dev.geco.gholo.service.HoloAnimationService;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Map;

public class FormatUtil {

    private final GHoloMain gHoloMain;

    public FormatUtil(GHoloMain gHoloMain) {
        this.gHoloMain = gHoloMain;
    }

    public String formatBase(String text) {
        for(Map.Entry<String, String> symbol : gHoloMain.getConfigService().SYMBOLS.entrySet()) {
            String key = symbol.getKey();
            String value = symbol.getValue();
            text = text.replace(key, value);
        }
        return text;
    }

    public String formatPlaceholders(String text, Player player) {
        if(HoloAnimationService.countAnimationChars(text) < 2) return gHoloMain.getMessageService().toFormattedMessage(text);
        text = formatPlaceholdersWithAnimations(text, player, gHoloMain.getHoloAnimationService().getAnimations());
        return gHoloMain.getMessageService().toFormattedMessage(formatBase(text));
    }

    public Object formatPlaceholdersComponent(String text, Player player) {
        if(HoloAnimationService.countAnimationChars(text) < 2) return gHoloMain.getMessageService().toFormattedComponent(text);
        text = formatPlaceholdersWithAnimations(text, player, gHoloMain.getHoloAnimationService().getAnimations());
        return gHoloMain.getMessageService().toFormattedComponent(formatBase(text));
    }

    private String formatPlaceholdersWithAnimations(String text, Player player, Collection<GHoloAnimation> animations) {
        for(GHoloAnimation animation : animations) text = text.replace(HoloAnimationService.AMIMATION_CHAR + animation.getId() + HoloAnimationService.AMIMATION_CHAR, animation.getCurrentContent());
        return formatPlaceholdersWithoutAnimations(text, player);
    }

    private String formatPlaceholdersWithoutAnimations(String text, Player player) {
        if(gHoloMain.hasPlaceholderAPILink()) text = PlaceholderAPI.setPlaceholders(player, text);
        return text;
    }

}