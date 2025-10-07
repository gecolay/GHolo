package dev.geco.gholo.service;

import dev.geco.gholo.GHoloMain;
import org.bukkit.Bukkit;

import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;

public class VersionService {

    private final String LATEST_VERSION = "v1_21_9";
    private final HashMap<String, String> VERSION_MAPPING = new HashMap<>(); {
        VERSION_MAPPING.put("v1_20_1", "v1_20");
        VERSION_MAPPING.put("v1_20_4", "v1_20_3");
        VERSION_MAPPING.put("v1_20_6", "v1_20_5");
        VERSION_MAPPING.put("v1_21_1", "v1_21");
        VERSION_MAPPING.put("v1_21_3", "v1_21_2");
        VERSION_MAPPING.put("v1_21_7", "v1_21_6");
        VERSION_MAPPING.put("v1_21_8", "v1_21_6");
        VERSION_MAPPING.put("v1_21_10", "v1_21_9");
    }
    private final String serverVersion;
    private String packagePath;
    private boolean available;

    public VersionService(GHoloMain gHoloMain) {
        String rawServerVersion = Bukkit.getServer().getBukkitVersion();
        serverVersion = rawServerVersion.substring(0, rawServerVersion.indexOf('-'));
        packagePath = gHoloMain.getClass().getPackage().getName() + ".mcv." + getPackageVersion();
        available = hasPackageClass("util.EntityUtil");
        if(available) return;
        packagePath = gHoloMain.getClass().getPackage().getName() + ".mcv." + LATEST_VERSION;
        available = hasPackageClass("util.EntityUtil");
    }

    public String getServerVersion() { return serverVersion; }

    public boolean isAvailable() { return available; }

    public boolean isNewerOrVersion(int version, int subVersion) {
        String[] serverVersionSplit = serverVersion.split("\\.");
        if(Integer.parseInt(serverVersionSplit[1]) > version) return true;
        return Integer.parseInt(serverVersionSplit[1]) == version && (serverVersionSplit.length > 2 ? Integer.parseInt(serverVersionSplit[2]) >= subVersion : subVersion == 0);
    }

    public Object getPackageObjectInstance(String className, Object... parameters) {
        try {
            Class<?> mcvPackageClass = Class.forName(packagePath + "." + className);
            if(parameters.length == 0) return mcvPackageClass.getConstructor().newInstance();
            Class<?>[] parameterTypes = Arrays.stream(parameters).map(Object::getClass).toArray(Class<?>[]::new);
            return mcvPackageClass.getConstructor(parameterTypes).newInstance(parameters);
        } catch(Throwable e) { GHoloMain.getInstance().getLogger().log(Level.SEVERE, "Could not get package object with class name '" + className + "'!", e); }
        return null;
    }

    public boolean hasPackageClass(String className) {
        try {
            Class.forName(packagePath + "." + className);
            return true;
        } catch(Throwable ignored) { }
        return false;
    }

    private String getPackageVersion() {
        String packageVersion = "v" + serverVersion.replace(".", "_");
        return VERSION_MAPPING.getOrDefault(packageVersion, packageVersion);
    }

}