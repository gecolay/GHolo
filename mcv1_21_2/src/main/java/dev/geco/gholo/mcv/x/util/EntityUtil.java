package dev.geco.gholo.mcv.x.util;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_21_R2.CraftWorld;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.mcv.x.objects.*;
import dev.geco.gholo.objects.*;
import dev.geco.gholo.util.*;

public class EntityUtil implements IEntityUtil {

    private final GHoloMain GPM;

    public EntityUtil(GHoloMain GPluginMain) { GPM = GPluginMain; }

    public void startHoloTicking(GHolo Holo) {
        ServerLevel level = ((CraftWorld) Holo.getLocation().getWorld()).getHandle();
        UUID taskId = GPM.getTManager().runAtFixedRate(() -> {
            Location location = Holo.getLocation();
            for(Player player : level.players()) {
                if(player.distanceToSqr(location.getX(), location.getY(), location.getZ()) <= Holo.getMaxRange()) {
                    if(!Holo.getPlayers().contains(player.getBukkitEntity())) {
                        Holo.getPlayers().add(player.getBukkitEntity());
                        // Holo.spawnRows();
                    }
                } else {
                    if(Holo.getPlayers().contains(player.getBukkitEntity())) {
                        Holo.getPlayers().remove(player.getBukkitEntity());
                        // Holo.deleteRows();
                    }
                }
            }
        }, 0, 1);
        Holo.addTask(taskId);
    }

    public void stopHoloTicking(GHolo Holo) {
        for(UUID taskId : Holo.getTasks()) GPM.getTManager().cancel(taskId);
    }

    public IGHoloRowEntity createHoloRowEntity(GHoloRow HoloRow) {
        return new GHoloRowEntity(HoloRow);
    }

}