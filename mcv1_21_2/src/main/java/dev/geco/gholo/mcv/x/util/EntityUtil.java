package dev.geco.gholo.mcv.x.util;

import java.util.*;

import org.bukkit.*;
import org.bukkit.craftbukkit.v1_21_R2.*;
import org.bukkit.craftbukkit.v1_21_R2.entity.*;

import net.minecraft.server.level.*;
import net.minecraft.world.entity.player.*;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.mcv.x.objects.*;
import dev.geco.gholo.objects.*;
import dev.geco.gholo.util.*;

public class EntityUtil implements IEntityUtil {

    private final GHoloMain GPM;

    public EntityUtil(GHoloMain GPluginMain) { GPM = GPluginMain; }

    public void startHoloTicking(GHolo Holo) {
        ServerLevel level = ((CraftWorld) Holo.getRawLocation().getWorld()).getHandle();
        UUID taskId = GPM.getTManager().runAtFixedRate(() -> {
            Location location = Holo.getRawLocation();
            for(Player player : level.players()) {
                if(player.distanceToSqr(location.getX(), location.getY(), location.getZ()) <= Holo.getMaxRange() * Holo.getMaxRange()) {
                    if(!Holo.getPlayers().contains((CraftPlayer)player.getBukkitEntity())) {
                        Holo.getPlayers().add((CraftPlayer)player.getBukkitEntity());
                        for(GHoloRow row : Holo.getRows()) {
                            row.getHoloRowEntity().spawnHoloRow((CraftPlayer) player.getBukkitEntity());
                        }
                    }
                } else {
                    if(Holo.getPlayers().contains((CraftPlayer) player.getBukkitEntity())) {
                        Holo.getPlayers().remove((CraftPlayer) player.getBukkitEntity());
                        for(GHoloRow row : Holo.getRows()) {
                            row.getHoloRowEntity().removeHoloRow((CraftPlayer) player.getBukkitEntity());
                        }
                    }
                }
            }
        }, 0, 1);
        Holo.addTask(taskId);
    }

    public void stopHoloTicking(GHolo Holo) {
        for(UUID taskId : Holo.getTasks()) GPM.getTManager().cancel(taskId);
        for(GHoloRow row : Holo.getRows()) {
            row.getHoloRowEntity().stopTicking();
        }
        for(org.bukkit.entity.Player player : Holo.getPlayers()) {
            for(GHoloRow row : Holo.getRows()) {
                row.getHoloRowEntity().removeHoloRow(player);
            }
        }
    }

    public IGHoloRowEntity createHoloRowEntity(GHoloRow HoloRow) {
        return new GHoloRowEntity(HoloRow);
    }

}