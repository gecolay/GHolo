package dev.geco.gholo.util;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.object.holo.GHoloAnimation;
import dev.geco.gholo.service.HoloAnimationService;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Map;

public class TextFormatUtil {

    private final GHoloMain gHoloMain;

    public TextFormatUtil(GHoloMain gHoloMain) {
        this.gHoloMain = gHoloMain;
    }

    public String replaceSymbols(String text) {
        for(Map.Entry<String, String> symbol : gHoloMain.getConfigService().SYMBOLS.entrySet()) {
            String key = symbol.getKey();
            String value = symbol.getValue();
            text = text.replace(key, value);
        }
        return text;
    }

    public String formatText(String text, Player player) {
        if(HoloAnimationService.countAnimationChars(text) < 2) return text;
        text = replaceAnimationsAndPlaceholders(text, player, gHoloMain.getHoloAnimationService().getAnimations());
        return replaceSymbols(text);
    }

    public String toFormattedText(String text) {
        return gHoloMain.getMessageService().toFormattedMessage(text);
    }

    public Object toFormattedComponent(String text) {
        return gHoloMain.getMessageService().toFormattedComponent(text);
    }

    private String replaceAnimationsAndPlaceholders(String text, Player player, Collection<GHoloAnimation> animations) {
        for(GHoloAnimation animation : animations) text = text.replace(HoloAnimationService.AMIMATION_CHAR + animation.getId() + HoloAnimationService.AMIMATION_CHAR, animation.getCurrentContent());
        return replacePlaceholders(text, player);
    }

    private String replacePlaceholders(String text, Player player) {
        if(gHoloMain.hasPlaceholderAPILink()) text = PlaceholderAPI.setPlaceholders(player, text);
        return text;
    }

}