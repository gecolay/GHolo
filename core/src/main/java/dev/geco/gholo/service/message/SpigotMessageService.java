package dev.geco.gholo.service.message;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.service.MessageService;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class SpigotMessageService extends MessageService {

    public SpigotMessageService(GHoloMain gHoloMain) {
        super(gHoloMain);
    }

    public String toFormattedMessage(String text, Object... rawReplaceList) { return org.bukkit.ChatColor.translateAlternateColorCodes(AMPERSAND_CHAR, replaceHexColorsDirectly(replaceText(text, rawReplaceList))); }

    public void sendMessage(@NotNull CommandSender target, String message, Object... replaceList) {
        String translatedMessage = getTranslatedMessage(message, getLanguageForTarget(target), replaceList);
        if(translatedMessage.isEmpty()) return;
        target.sendMessage(translatedMessage);
    }

}