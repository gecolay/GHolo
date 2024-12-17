package dev.geco.gholo.manager;

import org.bukkit.entity.*;
import org.bukkit.permissions.*;

import dev.geco.gholo.GHoloMain;

public class PManager {

    private final GHoloMain GPM;

    public PManager(GHoloMain GPluginMain) { GPM = GPluginMain; }

    public boolean hasPermission(Permissible Permissible, String... Permissions) {

        if(!(Permissible instanceof Player)) return true;

        for(String permission : Permissions) {

            if(Permissible.isPermissionSet(GHoloMain.NAME + "." + permission)) return Permissible.hasPermission(GHoloMain.NAME + "." + permission);
            if(Permissible.hasPermission(GHoloMain.NAME + "." + permission)) return true;
        }

        return Permissible.hasPermission(GHoloMain.NAME + ".*");
    }

}