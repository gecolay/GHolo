package dev.geco.gholo.util;

import java.util.*;
import java.util.Map.*;

import org.bukkit.entity.*;

import me.clip.placeholderapi.*;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.manager.*;
import dev.geco.gholo.objects.*;

public class FormatUtil {

    private final GHoloMain GPM;

    public FormatUtil(GHoloMain GPluginMain) { GPM = GPluginMain; }

    public String formatBase(String Text) {
        for(Entry<String, String> symbol : GPM.getCManager().SYMBOLS.entrySet()) {
            String key = symbol.getKey();
            String value = symbol.getValue();
            Text = Text.replace(key, value);
        }
        return Text;
    }

    public String formatPlaceholders(String Text, Player Player) {
        if(HoloAnimationManager.countAnimationChars(Text) < 2) return GPM.getMManager().toFormattedMessage(Text);
        String text = formatPlaceholdersWithAnimations(Text, Player, GPM.getHoloAnimationManager().getAnimations());
        return GPM.getMManager().toFormattedMessage(formatBase(text));
    }

    private String formatPlaceholdersWithAnimations(String Text, Player Player, Collection<GHoloAnimation> Animations) {
        for(GHoloAnimation animation : Animations) Text = Text.replace(HoloAnimationManager.AMIMATION_CHAR + animation.getId() + HoloAnimationManager.AMIMATION_CHAR, animation.getCurrentContent());
        return formatPlaceholdersWithoutAnimations(Text, Player);
    }

    private String formatPlaceholdersWithoutAnimations(String Text, Player Player) {
        if(GPM.getCManager().L_PLACEHOLDER_API && GPM.hasPlaceholderAPILink()) Text = PlaceholderAPI.setPlaceholders(Player, Text);
        return Text;
    }

}