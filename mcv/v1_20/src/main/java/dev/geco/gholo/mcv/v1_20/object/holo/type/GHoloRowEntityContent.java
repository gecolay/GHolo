package dev.geco.gholo.mcv.v1_20.object.holo.type;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.object.holo.GHoloData;
import dev.geco.gholo.object.holo.GHoloRow;
import dev.geco.gholo.object.holo.GHoloUpdateType;
import dev.geco.gholo.object.holo.IGHoloRowContentType;
import dev.geco.gholo.object.simple.SimpleLocation;
import dev.geco.gholo.object.simple.SimpleOffset;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class GHoloRowEntityContent extends Entity implements IGHoloRowContentType {

    private final GHoloRow holoRow;
    private final GHoloMain gHoloMain;
    private final HashMap<UUID, EntityType<?>> currentEntityTypes = new HashMap<>();

    public GHoloRowEntityContent(GHoloRow holoRow, GHoloMain gHoloMain) {
        super(EntityType.ITEM, ((CraftWorld) holoRow.getHolo().getRawLocation().getWorld()).getHandle());
        this.holoRow = holoRow;
        this.gHoloMain = gHoloMain;
        persist = false;
        setNoGravity(true);
        for(GHoloUpdateType updateType : GHoloUpdateType.values()) handleUpdate(updateType);
    }

    @Override
    public void load(Player player, String content, boolean create) {
        ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
        ResourceLocation resourceLocation = ResourceLocation.tryParse(content.toLowerCase());
        if(resourceLocation == null) return;
        try {
            EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.get(resourceLocation);
            if(create || currentEntityTypes.get(player.getUniqueId()) != entityType) {
                currentEntityTypes.put(player.getUniqueId(), entityType);
                serverPlayer.connection.send(new ClientboundAddEntityPacket(getId(), uuid, getX(), getY(), getZ(), getXRot(), getYRot(), entityType, 0, getDeltaMovement(), getYHeadRot()));
            }
            serverPlayer.connection.send(getDataPacket());
        } catch(Throwable ignored) { }
    }

    private ClientboundSetEntityDataPacket getDataPacket() {
        List<SynchedEntityData.DataValue<?>> data = getEntityData().getNonDefaultValues();
        if(data == null) data = new ArrayList<>();
        List<SynchedEntityData.DataValue<?>> defaultResetData = getEntityData().packDirty();
        if(defaultResetData != null) data.addAll(defaultResetData);
        return new ClientboundSetEntityDataPacket(getId(), data);
    }

    @Override
    public void publishUpdate(@NotNull GHoloUpdateType updateType) {
        handleUpdate(updateType);
        if(updateType == GHoloUpdateType.LOCATION) {
            ClientboundTeleportEntityPacket teleportEntityPacket = new ClientboundTeleportEntityPacket(this);
            String permission = getPermission();
            for(Player player : holoRow.getHolo().getRawLocation().getWorld().getPlayers()) {
                if(permission != null && !gHoloMain.getPermissionService().hasPermission(player, permission)) continue;
                ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
                serverPlayer.connection.send(teleportEntityPacket);
            }
            return;
        }
        finishUpdate();
    }

    private void handleUpdate(GHoloUpdateType updateType) {
        GHoloData rowData = holoRow.getRawData();
        GHoloData holoData = holoRow.getHolo().getRawData();
        switch(updateType) {
            case LOCATION -> {
                SimpleLocation location = holoRow.getHolo().getLocation();
                SimpleOffset offset = holoRow.getRawOffset();
                location.add(offset);
                setPos(location.getX(), location.getY(), location.getZ());
                float yaw = rowData.getRotation().getYaw() != null ? rowData.getRotation().getYaw() : (holoData.getRotation().getYaw() != null ? holoData.getRotation().getYaw() : 0f);
                float pitch = rowData.getRotation().getPitch() != null ? rowData.getRotation().getPitch() : (holoData.getRotation().getPitch() != null ? holoData.getRotation().getPitch() : 0f);
                setRot(yaw, pitch);
            }
        }
    }

    private void finishUpdate() {
        String permission = getPermission();
        for(Player player : holoRow.getHolo().getRawLocation().getWorld().getPlayers()) {
            if(permission != null && !gHoloMain.getPermissionService().hasPermission(player, permission)) continue;
            ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
            serverPlayer.connection.send(getDataPacket());
        }
    }

    private String getPermission() {
        GHoloData rowData = holoRow.getRawData();
        GHoloData holoData = holoRow.getHolo().getRawData();
        return rowData.getPermission() != null ? rowData.getPermission() : (holoData.getPermission() != null ? holoData.getPermission() : GHoloData.DEFAULT_PERMISSION);
    }

    @Override
    public void unload(Player player) {
        ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
        serverPlayer.connection.send(new ClientboundRemoveEntitiesPacket(getId()));
    }

    @Override
    protected void defineSynchedData() {}

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {}

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {}

}