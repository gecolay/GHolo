package dev.geco.gholo.service;

import dev.geco.gholo.GHoloMain;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDate;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.logging.Level;

public class UpdateService {

    private final String REMOTE_URL = "https://api.spigotmc.org/legacy/update.php?resource=";
    private final GHoloMain gHoloMain;
    private LocalDate lastCheckDate = null;
    private String latestVersion = null;
    private boolean isLatestVersion = true;

    public UpdateService(GHoloMain gHoloMain) {
        this.gHoloMain = gHoloMain;
    }

    public void checkForUpdates() {
        if(!gHoloMain.getConfigService().CHECK_FOR_UPDATE) return;
        checkVersion();
        if(isLatestVersion) return;
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(!gHoloMain.getPermissionService().hasPermission(player, "Update")) continue;
            gHoloMain.getMessageService().sendMessage(player, "Plugin.plugin-update", "%Name%", gHoloMain.NAME, "%NewVersion%", latestVersion, "%Version%", gHoloMain.getDescription().getVersion(), "%Path%", gHoloMain.getDescription().getWebsite());
        }
        gHoloMain.getMessageService().sendMessage(Bukkit.getConsoleSender(), "Plugin.plugin-update", "%Name%", gHoloMain.NAME, "%NewVersion%", latestVersion, "%Version%", gHoloMain.getDescription().getVersion(), "%Path%", gHoloMain.getDescription().getWebsite());
    }

    public void checkForUpdates(Player player) {
        if(!gHoloMain.getConfigService().CHECK_FOR_UPDATE) return;
        if(!gHoloMain.getPermissionService().hasPermission(player, "Update")) return;
        checkVersion();
        if(isLatestVersion) return;
        gHoloMain.getMessageService().sendMessage(player, "Plugin.plugin-update", "%Name%", gHoloMain.NAME, "%NewVersion%", latestVersion, "%Version%", gHoloMain.getDescription().getVersion(), "%Path%", gHoloMain.getDescription().getWebsite());
    }

    private void getSpigotVersion(Consumer<String> versionConsumer) {
        gHoloMain.getTaskService().run(() -> {
            try(InputStream inputStream = new URL(REMOTE_URL + gHoloMain.RESOURCE_ID).openStream();
                Scanner scanner = new Scanner(inputStream)) {
                if(scanner.hasNext() && versionConsumer != null) versionConsumer.accept(scanner.next());
            } catch(IOException e) {
                if(e.getMessage().contains("50")) return;
                gHoloMain.getLogger().log(Level.SEVERE, "Could not get remote version!", e);
            }
        }, false);
    }

    private void checkVersion() {
        LocalDate today = LocalDate.now();
        if(lastCheckDate != null && lastCheckDate.equals(today)) return;
        lastCheckDate = today;
        try {
            getSpigotVersion(spigotVersion -> {
                latestVersion = spigotVersion;
                if(latestVersion == null) {
                    isLatestVersion = true;
                    return;
                }
                String pluginVersion = gHoloMain.getDescription().getVersion();
                String[] pluginVersionParts = getShortVersion(pluginVersion).split("\\.");
                String[] spigotVersionParts = getShortVersion(latestVersion).split("\\.");
                int minLength = Math.min(pluginVersionParts.length, spigotVersionParts.length);
                for(int i = 0; i < minLength; i++) {
                    int pluginPart = Integer.parseInt(pluginVersionParts[i]);
                    int spigotPart = Integer.parseInt(spigotVersionParts[i]);
                    if(pluginPart < spigotPart) {
                        isLatestVersion = false;
                        return;
                    } else if(pluginPart > spigotPart) {
                        isLatestVersion = true;
                        return;
                    }
                }
                isLatestVersion = pluginVersionParts.length >= spigotVersionParts.length;
            });
        } catch(Throwable e) { isLatestVersion = true; }
    }

    private String getShortVersion(String version) { return version.replaceAll("[\\[\\] ]", ""); }

}