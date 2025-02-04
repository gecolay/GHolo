package dev.geco.gholo.service;

import dev.geco.gholo.GHoloMain;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;

public class PermissionService {

    public boolean hasPermission(Permissible permissible, String... permissions) {
        if(!(permissible instanceof Player)) return true;
        for(String permission : permissions) {
            if(permissible.isPermissionSet(GHoloMain.NAME + "." + permission)) return permissible.hasPermission(GHoloMain.NAME + "." + permission);
            if(permissible.hasPermission(GHoloMain.NAME + "." + permission)) return true;
        }
        return permissible.hasPermission(GHoloMain.NAME + ".*");
    }

}