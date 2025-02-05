package dev.geco.gholo.service;

import dev.geco.gholo.GHoloMain;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.file.YamlConfigurationOptions;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ConfigService {

    public String L_LANG;
    public boolean L_CLIENT_LANG;
    public boolean CHECK_FOR_UPDATE;
    public double DEFAULT_SIZE_BETWEEN_ROWS;
    public int LIST_PAGE_SIZE;
    public double NEAR_RANGE;
    public HashMap<String, String> SYMBOLS = new HashMap<>();
    public List<String> FEATUREFLAGS = new ArrayList<>();

    private final GHoloMain gHoloMain;

    public ConfigService(GHoloMain gHoloMain) {
        this.gHoloMain = gHoloMain;

        try {
            File configFile = new File(gHoloMain.getDataFolder(), "config.yml");
            FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
            InputStream configSteam = gHoloMain.getResource("config.yml");
            if(configSteam != null) {
                FileConfiguration configSteamConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(configSteam, StandardCharsets.UTF_8));
                if(!config.getKeys(true).equals(configSteamConfig.getKeys(true))) {
                    config.setDefaults(configSteamConfig);
                    YamlConfigurationOptions options = (YamlConfigurationOptions) config.options();
                    options.parseComments(true).copyDefaults(true).width(500);
                    config.loadFromString(config.saveToString());
                    for(String comments : config.getKeys(true)) config.setComments(comments, configSteamConfig.getComments(comments));
                    config.save(configFile);
                }
            } else gHoloMain.saveDefaultConfig();
        } catch(Throwable e) { gHoloMain.saveDefaultConfig(); }

        reload();
    }

    public void reload() {
        gHoloMain.reloadConfig();

        L_LANG = gHoloMain.getConfig().getString("Lang.lang", "en_us").toLowerCase();
        L_CLIENT_LANG = gHoloMain.getConfig().getBoolean("Lang.client-lang", true);

        CHECK_FOR_UPDATE = gHoloMain.getConfig().getBoolean("Options.check-for-update", true);
        DEFAULT_SIZE_BETWEEN_ROWS = gHoloMain.getConfig().getDouble("Options.default-size-between-rows", 0.26);
        LIST_PAGE_SIZE = gHoloMain.getConfig().getInt("Options.list-page-size", 10);
        NEAR_RANGE = gHoloMain.getConfig().getDouble("Options.near-range", 20);
        SYMBOLS.clear();
        try {
            ConfigurationSection symbolsSection = gHoloMain.getConfig().getConfigurationSection("Options.Symbols");
            if(symbolsSection != null) {
                for(String symbol : symbolsSection.getKeys(false)) {
                    SYMBOLS.put(symbol, String.valueOf(gHoloMain.getConfig().getString("Options.Symbols." + symbol).toCharArray()[0]));
                }
            }
        } catch(Throwable ignored) { }
        FEATUREFLAGS = gHoloMain.getConfig().getStringList("Options.FeatureFlags");
    }

}
