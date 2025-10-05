package dev.geco.gholo.mcv.v1_21_5.object.holo.type;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.object.holo.GHoloData;
import dev.geco.gholo.object.holo.GHoloRow;
import dev.geco.gholo.object.holo.GHoloUpdateType;
import dev.geco.gholo.object.holo.IGHoloRowContentType;
import dev.geco.gholo.object.simple.SimpleLocation;
import dev.geco.gholo.object.simple.SimpleVector;
import dev.geco.gholo.object.simple.SimpleSize;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Brightness;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;

public class GHoloRowBlockContent extends Display.BlockDisplay implements IGHoloRowContentType {

    private final GHoloRow holoRow;
    private final GHoloMain gHoloMain;
    private final EntityDataAccessor<BlockState> holoBlockData;
    private final EntityDataAccessor<Vector3f> holoScaleData;

    public GHoloRowBlockContent(GHoloRow holoRow, GHoloMain gHoloMain) {
        super(EntityType.BLOCK_DISPLAY, ((CraftWorld) holoRow.getHolo().getRawLocation().getWorld()).getHandle());
        this.holoRow = holoRow;
        this.gHoloMain = gHoloMain;
        persist = false;
        EntityDataAccessor<BlockState> blockAccessor = null;
        try {
            List<Field> fieldList = new ArrayList<>();
            for(Field field : Display.BlockDisplay.class.getDeclaredFields()) if(field.getType().equals(EntityDataAccessor.class)) fieldList.add(field);
            Field field = fieldList.getFirst();
            field.setAccessible(true);
            blockAccessor = (EntityDataAccessor<BlockState>) field.get(this);
        } catch(Throwable e) { gHoloMain.getLogger().log(Level.SEVERE, "Could not load field", e); }
        holoBlockData = blockAccessor;
        EntityDataAccessor<Vector3f> scaleAccessor = null;
        try {
            List<Field> fieldList = new ArrayList<>();
            for(Field field : Display.class.getDeclaredFields()) if(field.getType().equals(EntityDataAccessor.class)) fieldList.add(field);
            Field field = fieldList.get(4);
            field.setAccessible(true);
            scaleAccessor = (EntityDataAccessor<Vector3f>) field.get(this);
        } catch(Throwable e) { gHoloMain.getLogger().log(Level.SEVERE, "Could not load field", e); }
        holoScaleData = scaleAccessor;
        for(GHoloUpdateType updateType : GHoloUpdateType.values()) handleUpdate(updateType);
    }

    @Override
    public void load(Player player, String content, boolean create) {
        ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
        if(create) serverPlayer.connection.send(new ClientboundAddEntityPacket(getId(), uuid, getX(), getY(), getZ(), getXRot(), getYRot(), getType(), 0, getDeltaMovement(), getYRot()));
        serverPlayer.connection.send(getDataPacket(content));
    }

    private ClientboundSetEntityDataPacket getDataPacket(String content) {
        List<SynchedEntityData.DataValue<?>> data = getEntityData().getNonDefaultValues();
        if(data == null) data = new ArrayList<>();
        else data.removeIf(dataValue -> dataValue.id() == holoBlockData.id());
        List<SynchedEntityData.DataValue<?>> defaultResetData = getEntityData().packDirty();
        if(defaultResetData != null) data.addAll(defaultResetData);
        if(content != null) {
            ResourceLocation resourceLocation = ResourceLocation.tryParse(content.toLowerCase());
            if(resourceLocation != null) {
                Optional<Holder.Reference<Block>> blockData = BuiltInRegistries.BLOCK.get(resourceLocation);
                if(blockData.isPresent()) data.add(new SynchedEntityData.DataValue<>(holoBlockData.id(), holoBlockData.serializer(), blockData.get().value().defaultBlockState()));
            }
        }
        return new ClientboundSetEntityDataPacket(getId(), data);
    }

    @Override
    public void publishUpdate(@NotNull GHoloUpdateType updateType) {
        handleUpdate(updateType);
        if(updateType == GHoloUpdateType.LOCATION) {
            ClientboundTeleportEntityPacket teleportEntityPacket = new ClientboundTeleportEntityPacket(getId(), PositionMoveRotation.of(this), Set.of(), false);
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
                SimpleVector offset = holoRow.getRawOffset();
                location.add(offset);
                setPos(location.getX(), location.getY(), location.getZ());
                float yaw = rowData.getRotation().getYaw() != null ? rowData.getRotation().getYaw() : (holoData.getRotation().getYaw() != null ? holoData.getRotation().getYaw() : 0f);
                float pitch = rowData.getRotation().getPitch() != null ? rowData.getRotation().getPitch() : (holoData.getRotation().getPitch() != null ? holoData.getRotation().getPitch() : 0f);
                setRot(yaw, pitch);
            }
            case RANGE -> {
                double range = rowData.getRange() != GHoloData.DEFAULT_RANGE ? rowData.getRange() : (holoData.getRange() != GHoloData.DEFAULT_RANGE ? holoData.getRange() : GHoloData.DEFAULT_RANGE);
                setViewRange((float) (range / 64));
            }
            case BILLBOARD -> {
                String billboard = !Objects.equals(rowData.getBillboard(), GHoloData.DEFAULT_BILLBOARD) ? rowData.getBillboard() : (!Objects.equals(holoData.getBillboard(), GHoloData.DEFAULT_BILLBOARD) ? holoData.getBillboard() : GHoloData.DEFAULT_BILLBOARD);
                setBillboard(billboard);
            }
            case SCALE -> {
                SimpleVector scale = !Objects.equals(rowData.getRawScale(), GHoloData.DEFAULT_SCALE) ? rowData.getRawScale() : (!Objects.equals(holoData.getRawScale(), GHoloData.DEFAULT_SCALE) ? holoData.getRawScale() : GHoloData.DEFAULT_SCALE);
                entityData.set(holoScaleData, new Vector3f((float) scale.getX(), (float) scale.getY(), (float) scale.getZ()));
            }
            case BRIGHTNESS -> {
                Byte brightness = rowData.getBrightness() != GHoloData.DEFAULT_BRIGHTNESS ? rowData.getBrightness() : (holoData.getBrightness() != GHoloData.DEFAULT_BRIGHTNESS ? holoData.getBrightness() : GHoloData.DEFAULT_BRIGHTNESS);
                setBrightnessOverride(brightness != null ? new Brightness(brightness, Brightness.FULL_BRIGHT.sky()) : null);
            }
            case SIZE -> {
                SimpleSize size = !Objects.equals(rowData.getRawSize(), GHoloData.DEFAULT_SIZE) ? rowData.getRawSize() : (!Objects.equals(holoData.getRawSize(), GHoloData.DEFAULT_SIZE) ? holoData.getRawSize() : GHoloData.DEFAULT_SIZE);
                setWidth(size.getWidth());
                setHeight(size.getHeight());
            }
        }
    }

    private void setBillboard(String billboard) { setBillboardConstraints(BillboardConstraints.valueOf(billboard.toUpperCase())); }

    private void finishUpdate() {
        String permission = getPermission();
        for(Player player : holoRow.getHolo().getRawLocation().getWorld().getPlayers()) {
            if(permission != null && !gHoloMain.getPermissionService().hasPermission(player, permission)) continue;
            ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
            serverPlayer.connection.send(getDataPacket(null));
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

}