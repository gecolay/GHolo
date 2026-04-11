package dev.geco.gholo.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.geco.gholo.GHoloMain;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDate;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.logging.Level;

public class UpdateService {

    private final String GITHUB_REMOTE_URL = "https://api.github.com/repos/gecolay/gholo/releases/latest";
    private final String MODRINTH_REMOTE_URL = "https://api.modrinth.com/v2/project/gholo/version";
    private final String SPIGOT_REMOTE_URL = "https://api.spigotmc.org/legacy/update.php?resource=121144";
    private final String PAPER_REMOTE_URL = "https://hangar.papermc.io/api/v1/projects/gecolay/gholo/latest?channel=release";
    private final GHoloMain gHoloMain;
    private LocalDate lastCheckDate = null;
    private String latestVersion = null;
    private boolean isLatestVersion = true;

    public UpdateService(GHoloMain gHoloMain) {
        this.gHoloMain = gHoloMain;
    }

    public void checkForUpdates() {
        if(!gHoloMain.getConfigService().CHECK_FOR_UPDATE) return;
        checkVersion(() -> {
            if(isLatestVersion) return;
            for(Player player : Bukkit.getOnlinePlayers()) {
                if(!gHoloMain.getPermissionService().hasPermission(player, "Update")) continue;
                gHoloMain.getMessageService().sendMessage(player, "Plugin.plugin-update", "%Name%", GHoloMain.NAME, "%NewVersion%", latestVersion, "%Version%", gHoloMain.getDescription().getVersion(), "%Path%", gHoloMain.getDescription().getWebsite());
            }
            gHoloMain.getMessageService().sendMessage(Bukkit.getConsoleSender(), "Plugin.plugin-update", "%Name%", GHoloMain.NAME, "%NewVersion%", latestVersion, "%Version%", gHoloMain.getDescription().getVersion(), "%Path%", gHoloMain.getDescription().getWebsite());
        });
    }

    public void checkForUpdates(Player player) {
        if(!gHoloMain.getConfigService().CHECK_FOR_UPDATE) return;
        if(!gHoloMain.getPermissionService().hasPermission(player, "Update")) return;
        checkVersion(() -> {
            if(isLatestVersion) return;
            gHoloMain.getMessageService().sendMessage(player, "Plugin.plugin-update", "%Name%", GHoloMain.NAME, "%NewVersion%", latestVersion, "%Version%", gHoloMain.getDescription().getVersion(), "%Path%", gHoloMain.getDescription().getWebsite());
        });
    }

    private void getGitHubVersion(Consumer<String> versionConsumer) {
        gHoloMain.getTaskService().run(() -> {
            try(InputStream inputStream = new URL(GITHUB_REMOTE_URL).openStream(); InputStreamReader reader = new InputStreamReader(inputStream)) {
                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                String tag = json.get("tag_name").getAsString();
                if(tag != null && versionConsumer != null) versionConsumer.accept(tag);
            } catch(IOException e) {
                if (e.getMessage().contains("50")) return;
                gHoloMain.getLogger().log(Level.WARNING, "Could not get github remote version!", e);
            }
        }, false);
    }

    private void getModrinthVersion(Consumer<String> versionConsumer) {
        gHoloMain.getTaskService().run(() -> {
            try {
                URLConnection connection = new URL(MODRINTH_REMOTE_URL).openConnection();
                connection.setRequestProperty("User-Agent", GHoloMain.NAME + "/" + gHoloMain.getDescription().getVersion());
                try(InputStream inputStream = connection.getInputStream(); InputStreamReader reader = new InputStreamReader(inputStream)) {
                    JsonArray jsonArray = JsonParser.parseReader(reader).getAsJsonArray();
                    if(jsonArray.isEmpty()) return;
                    String tag = jsonArray.get(0).getAsJsonObject().get("version_number").getAsString();
                    if(tag != null && versionConsumer != null) versionConsumer.accept(tag);
                }
            } catch(IOException e) {
                if (e.getMessage().contains("50")) return;
                gHoloMain.getLogger().log(Level.WARNING, "Could not get modrinth remote version!", e);
            }
        }, false);
    }

    private void getSpigotVersion(Consumer<String> versionConsumer) {
        gHoloMain.getTaskService().run(() -> {
            try(InputStream inputStream = new URL(SPIGOT_REMOTE_URL).openStream(); Scanner scanner = new Scanner(inputStream)) {
                if(scanner.hasNext() && versionConsumer != null) versionConsumer.accept(scanner.next());
            } catch(IOException e) {
                if(e.getMessage().contains("50")) return;
                gHoloMain.getLogger().log(Level.WARNING, "Could not get spigot remote version!", e);
            }
        }, false);
    }

    private void getPaperVersion(Consumer<String> versionConsumer) {
        gHoloMain.getTaskService().run(() -> {
            try(InputStream inputStream = new URL(PAPER_REMOTE_URL).openStream(); Scanner scanner = new Scanner(inputStream)) {
                if(scanner.hasNext() && versionConsumer != null) versionConsumer.accept(scanner.next());
            } catch(IOException e) {
                if(e.getMessage().contains("50")) return;
                gHoloMain.getLogger().log(Level.WARNING, "Could not get paper remote version!", e);
            }
        }, false);
    }

    private void checkVersion(Runnable runnable) {
        LocalDate today = LocalDate.now();
        if(lastCheckDate != null && lastCheckDate.equals(today)) {
            runnable.run();
            return;
        }
        lastCheckDate = today;
        try {
            switch(gHoloMain.getSource()) {
                case "github":
                    getGitHubVersion((version) -> setLatestVersion(version, runnable));
                    break;
                case "modrinth":
                    getModrinthVersion((version) -> setLatestVersion(version, runnable));
                    break;
                case "spigot":
                    getSpigotVersion((version) -> setLatestVersion(version, runnable));
                    break;
                case "paper":
                    getPaperVersion((version) -> setLatestVersion(version, runnable));
                    break;
            }
        } catch(Throwable e) {
            gHoloMain.getLogger().log(Level.WARNING, "Could not check version!", e);
            isLatestVersion = true;
        }
    }

    private void setLatestVersion(String version, Runnable runnable) {
        latestVersion = version;
        if(latestVersion == null) {
            isLatestVersion = true;
            return;
        }
        String localVersion = gHoloMain.getDescription().getVersion();
        String[] localVersionParts = localVersion.split("\\.");
        String[] remoteVersionParts = latestVersion.split("\\.");
        int minLength = Math.min(localVersionParts.length, remoteVersionParts.length);
        for(int i = 0; i < minLength; i++) {
            int localPart = Integer.parseInt(localVersionParts[i]);
            int remotePart = Integer.parseInt(remoteVersionParts[i]);
            if(localPart < remotePart) {
                isLatestVersion = false;
                runnable.run();
                return;
            } else if(localPart > remotePart) {
                isLatestVersion = true;
                return;
            }
        }
        isLatestVersion = localVersionParts.length >= remoteVersionParts.length;
        runnable.run();
    }

}