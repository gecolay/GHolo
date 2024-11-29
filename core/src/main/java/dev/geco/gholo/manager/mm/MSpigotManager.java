package dev.geco.gholo.manager.mm;

import org.jetbrains.annotations.*;

import org.bukkit.command.*;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.manager.*;

public class MSpigotManager extends MManager {

    public MSpigotManager(GHoloMain GPluginMain) {
        super(GPluginMain);
    }

    public String toFormattedMessage(String Text, Object... RawReplaceList) { return org.bukkit.ChatColor.translateAlternateColorCodes(AMPERSAND_CHAR, replaceHexColorsDirect(replaceText(Text, RawReplaceList))); }

    public void sendMessage(@NotNull CommandSender Target, String Message, Object... ReplaceList) { Target.sendMessage(getMessageByLanguage(Message, getLanguage(Target), ReplaceList)); }

}