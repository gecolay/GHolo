package dev.geco.gholo.mcv.v1_21_4.object;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.object.interaction.GInteraction;
import dev.geco.gholo.object.interaction.GInteractionData;
import dev.geco.gholo.object.interaction.GInteractionUpdateType;
import dev.geco.gholo.object.interaction.IGInteractionEntity;
import dev.geco.gholo.object.location.SimpleLocation;
import dev.geco.gholo.object.location.SimpleRotation;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Interaction;
import net.minecraft.world.entity.PositionMoveRotation;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GInteractionEntity extends Interaction implements IGInteractionEntity {

    protected final GInteraction interaction;
    protected final GHoloMain gHoloMain;

    public GInteractionEntity(GInteraction interaction) {
        super(EntityType.INTERACTION, ((CraftWorld) interaction.getRawLocation().getWorld()).getHandle());
        this.interaction = interaction;
        gHoloMain = GHoloMain.getInstance();
        persist = false;
        for(GInteractionUpdateType updateType : GInteractionUpdateType.values()) handleUpdate(updateType);
    }

    @Override
    public void loadInteraction() {
        ClientboundAddEntityPacket addEntityPacket = new ClientboundAddEntityPacket(getId(), uuid, getX(), getY(), getZ(), getXRot(), getYRot(), getType(), 0, getDeltaMovement(), getYHeadRot());
        ClientboundSetEntityDataPacket setEntityDataPacket = getDataPacket();
        String permission = getPermission();
        for(Player player : interaction.getRawLocation().getWorld().getPlayers()) {
            if(permission != null && !gHoloMain.getPermissionService().hasPermission(player, permission)) continue;
            ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
            serverPlayer.connection.send(addEntityPacket);
            serverPlayer.connection.send(setEntityDataPacket);
        }
    }

    @Override
    public void loadInteraction(@NotNull Player player) {
        ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
        if(!serverPlayer.level().equals(level())) return;
        String permission = getPermission();
        if(permission != null && !gHoloMain.getPermissionService().hasPermission(player, permission)) return;
        serverPlayer.connection.send(new ClientboundAddEntityPacket(getId(), uuid, getX(), getY(), getZ(), getXRot(), getYRot(), getType(), 0, getDeltaMovement(), getYHeadRot()));
        serverPlayer.connection.send(getDataPacket());
    }

    @Override
    public void publishUpdate(@NotNull GInteractionUpdateType updateType) {
        handleUpdate(updateType);
        if(updateType == GInteractionUpdateType.LOCATION) {
            ClientboundTeleportEntityPacket teleportEntityPacket = new ClientboundTeleportEntityPacket(getId(), PositionMoveRotation.of(this), Set.of(), false);
            String permission = getPermission();
            for(Player player : interaction.getRawLocation().getWorld().getPlayers()) {
                if(permission != null && !gHoloMain.getPermissionService().hasPermission(player, permission)) continue;
                ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
                serverPlayer.connection.send(teleportEntityPacket);
            }
            return;
        } else if(updateType == GInteractionUpdateType.PERMISSION) {
            unloadInteraction();
            loadInteraction();
            return;
        }
        finishUpdate();
    }

    private void handleUpdate(GInteractionUpdateType updateType) {
        switch(updateType) {
            case LOCATION:
                SimpleLocation location = interaction.getLocation();
                setPos(location.getX(), location.getY(), location.getZ());
                SimpleRotation rotation = interaction.getRotation();
                setRot(rotation.getYaw(), rotation.getPitch());
                break;
        }
    }

    private void finishUpdate() {
        String permission = getPermission();
        ClientboundSetEntityDataPacket setEntityDataPacket = getDataPacket();
        for(Player player : interaction.getRawLocation().getWorld().getPlayers()) {
            if(permission != null && !gHoloMain.getPermissionService().hasPermission(player, permission)) continue;
            ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
            serverPlayer.connection.send(setEntityDataPacket);
        }
    }

    private String getPermission() {
        GInteractionData interactionData = interaction.getRawData();
        return interactionData.getPermission() != null ? interactionData.getPermission() : GInteractionData.DEFAULT_PERMISSION;
    }

    private ClientboundSetEntityDataPacket getDataPacket() {
        List<SynchedEntityData.DataValue<?>> data = getEntityData().getNonDefaultValues();
        if(data == null) data = new ArrayList<>();
        List<SynchedEntityData.DataValue<?>> defaultResetData = getEntityData().packDirty();
        if(defaultResetData != null) data.addAll(defaultResetData);
        return new ClientboundSetEntityDataPacket(getId(), data);
    }

    @Override
    public void unloadInteraction() {
        ClientboundRemoveEntitiesPacket removeEntityPacket = new ClientboundRemoveEntitiesPacket(getId());
        for(Player player : interaction.getRawLocation().getWorld().getPlayers()) {
            ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
            serverPlayer.connection.send(removeEntityPacket);
        }
    }

    @Override
    public void unloadInteraction(@NotNull Player player) {
        ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
        serverPlayer.connection.send(new ClientboundRemoveEntitiesPacket(getId()));
    }

}