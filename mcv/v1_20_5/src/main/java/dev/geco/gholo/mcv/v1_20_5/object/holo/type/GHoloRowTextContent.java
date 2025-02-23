package dev.geco.gholo.mcv.v1_20_5.object.holo.type;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.object.holo.GHoloData;
import dev.geco.gholo.object.holo.GHoloRow;
import dev.geco.gholo.object.holo.GHoloUpdateType;
import dev.geco.gholo.object.simple.SimpleLocation;
import dev.geco.gholo.object.simple.SimpleOffset;
import io.papermc.paper.adventure.PaperAdventure;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Brightness;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GHoloRowTextContent extends Display.TextDisplay implements IGHoloRowContentType {

    private final GHoloRow holoRow;
    private final GHoloMain gHoloMain;
    private final EntityDataAccessor<Component> holoTextData;
    private final EntityDataAccessor<Vector3f> holoScaleData;

    public GHoloRowTextContent(GHoloRow holoRow, GHoloMain gHoloMain) {
        super(EntityType.TEXT_DISPLAY, ((CraftWorld) holoRow.getHolo().getRawLocation().getWorld()).getHandle());
        this.holoRow = holoRow;
        this.gHoloMain = gHoloMain;
        persist = false;
        entityData.set(DATA_LINE_WIDTH_ID, 10000);
        EntityDataAccessor<Component> textAccessor = null;
        try {
            List<Field> fieldList = new ArrayList<>();
            for(Field field : TextDisplay.class.getDeclaredFields()) if(field.getType().equals(EntityDataAccessor.class)) fieldList.add(field);
            Field field = fieldList.getFirst();
            field.setAccessible(true);
            textAccessor = (EntityDataAccessor<Component>) field.get(this);
        } catch(Throwable e) { e.printStackTrace(); }
        holoTextData = textAccessor;
        EntityDataAccessor<Vector3f> scaleAccessor = null;
        try {
            List<Field> fieldList = new ArrayList<>();
            for(Field field : Display.class.getDeclaredFields()) if(field.getType().equals(EntityDataAccessor.class)) fieldList.add(field);
            Field field = fieldList.get(4);
            field.setAccessible(true);
            scaleAccessor = (EntityDataAccessor<Vector3f>) field.get(this);
        } catch(Throwable e) { e.printStackTrace(); }
        holoScaleData = scaleAccessor;
        for(GHoloUpdateType updateType : GHoloUpdateType.values()) handleUpdate(updateType);
    }

    @Override
    public void load(Player player, String content, boolean create) {
        ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
        if(create) serverPlayer.connection.send(new ClientboundAddEntityPacket(getId(), uuid, getX(), getY(), getZ(), getXRot(), getYRot(), getType(), 0, getDeltaMovement(), getYHeadRot()));
        serverPlayer.connection.send(getDataPacket(content));
    }

    private ClientboundSetEntityDataPacket getDataPacket(String content) {
        List<SynchedEntityData.DataValue<?>> data = getEntityData().getNonDefaultValues();
        if(data == null) data = new ArrayList<>();
        else data.removeIf(dataValue -> dataValue.id() == holoTextData.id());
        List<SynchedEntityData.DataValue<?>> defaultResetData = getEntityData().packDirty();
        if(defaultResetData != null) data.addAll(defaultResetData);
        if(content != null) {
            Component textData = gHoloMain.supportsPaperFeature() ? PaperAdventure.asVanilla((net.kyori.adventure.text.Component) gHoloMain.getTextFormatUtil().toFormattedComponent(content)) : CraftChatMessage.fromString(gHoloMain.getTextFormatUtil().toFormattedText(content), false, true)[0];
            data.add(new SynchedEntityData.DataValue<>(holoTextData.id(), holoTextData.serializer(), textData));
        }
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
            case RANGE -> {
                double range = rowData.getRange() != GHoloData.DEFAULT_RANGE ? rowData.getRange() : (holoData.getRange() != GHoloData.DEFAULT_RANGE ? holoData.getRange() : GHoloData.DEFAULT_RANGE);
                setViewRange((float) (range / 64));
            }
            case BACKGROUND_COLOR -> {
                String backgroundColor = !Objects.equals(rowData.getBackgroundColor(), GHoloData.DEFAULT_BACKGROUND_COLOR) ? rowData.getBackgroundColor() : (!Objects.equals(holoData.getBackgroundColor(), GHoloData.DEFAULT_BACKGROUND_COLOR) ? holoData.getBackgroundColor() : GHoloData.DEFAULT_BACKGROUND_COLOR);
                setBackgroundColor(backgroundColor);
            }
            case TEXT_OPACITY -> {
                byte textOpacity = rowData.getTextOpacity() != GHoloData.DEFAULT_TEXT_OPACITY ? rowData.getTextOpacity() : (holoData.getTextOpacity() != GHoloData.DEFAULT_TEXT_OPACITY ? holoData.getTextOpacity() : GHoloData.DEFAULT_TEXT_OPACITY);
                setRealTextOpacity(textOpacity);
            }
            case TEXT_SHADOW -> {
                boolean textShadow = rowData.getTextShadow() != GHoloData.DEFAULT_HAS_TEXT_SHADOW ? rowData.getTextShadow() : (holoData.getTextShadow() != GHoloData.DEFAULT_HAS_TEXT_SHADOW ? holoData.getTextShadow() : GHoloData.DEFAULT_HAS_TEXT_SHADOW);
                setTextShadow(textShadow);
            }
            case TEXT_ALIGNMENT -> {
                String textAlignment = !Objects.equals(rowData.getTextAlignment(), GHoloData.DEFAULT_TEXT_ALIGNMENT) ? rowData.getTextAlignment() : (!Objects.equals(holoData.getTextAlignment(), GHoloData.DEFAULT_TEXT_ALIGNMENT) ? holoData.getTextAlignment() : GHoloData.DEFAULT_TEXT_ALIGNMENT);
                setTextAlignment(textAlignment);
            }
            case BILLBOARD -> {
                String billboard = !Objects.equals(rowData.getBillboard(), GHoloData.DEFAULT_BILLBOARD) ? rowData.getBillboard() : (!Objects.equals(holoData.getBillboard(), GHoloData.DEFAULT_BILLBOARD) ? holoData.getBillboard() : GHoloData.DEFAULT_BILLBOARD);
                setBillboard(billboard);
            }
            case SEE_THROUGH -> {
                boolean seeThrough = rowData.getSeeThrough() != GHoloData.DEFAULT_CAN_SEE_THROUGH ? rowData.getSeeThrough() : (holoData.getSeeThrough() != GHoloData.DEFAULT_CAN_SEE_THROUGH ? holoData.getSeeThrough() : GHoloData.DEFAULT_CAN_SEE_THROUGH);
                setSeeThrough(seeThrough);
            }
            case SCALE -> {
                Vector3f scale = !Objects.equals(rowData.getRawScale(), GHoloData.DEFAULT_SCALE) ? rowData.getRawScale() : (!Objects.equals(holoData.getRawScale(), GHoloData.DEFAULT_SCALE) ? holoData.getRawScale() : GHoloData.DEFAULT_SCALE);
                entityData.set(holoScaleData, scale);
            }
            case BRIGHTNESS -> {
                Byte brightness = rowData.getBrightness() != GHoloData.DEFAULT_BRIGHTNESS ? rowData.getBrightness() : (holoData.getBrightness() != GHoloData.DEFAULT_BRIGHTNESS ? holoData.getBrightness() : GHoloData.DEFAULT_BRIGHTNESS);
                setBrightnessOverride(brightness != null ? new Brightness(brightness, Brightness.FULL_BRIGHT.sky()) : null);
            }
        }
    }

    private void setBackgroundColor(String color) {
        if(color.equalsIgnoreCase("transparent")) {
            entityData.set(DATA_BACKGROUND_COLOR_ID, 0);
            return;
        }
        color = color.startsWith("#") ? color.substring(1) : color;
        if(color.length() == 6) color = color + "40";
        color = color.substring(color.length() - 2) + color.substring(0, color.length() - 2);
        entityData.set(DATA_BACKGROUND_COLOR_ID, (int) Long.parseLong(color, 16));
    }

    private void setRealTextOpacity(byte textOpacity) {
        int clampedPercent = Math.max(0, Math.min(textOpacity, 100));
        int valueInRange26To255 = 255 - (clampedPercent * 231 / 100);
        byte signedAlphaValue = (byte) (valueInRange26To255 > 127 ? valueInRange26To255 - 256 : valueInRange26To255);
        setTextOpacity(signedAlphaValue);
    }

    private void setTextAlignment(String textAlignment) {
        setFlags((byte) (textAlignment.equalsIgnoreCase(Align.LEFT.name()) ? getFlags() | FLAG_ALIGN_LEFT : getFlags() & ~FLAG_ALIGN_LEFT));
        setFlags((byte) (textAlignment.equalsIgnoreCase(Align.RIGHT.name()) ? getFlags() | FLAG_ALIGN_RIGHT : getFlags() & ~FLAG_ALIGN_RIGHT));
    }

    private void setBillboard(String billboard) { setBillboardConstraints(BillboardConstraints.valueOf(billboard.toUpperCase())); }

    private void setTextShadow(boolean hasTextShadow) { setFlags((byte) (hasTextShadow ? getFlags() | FLAG_SHADOW : getFlags() & ~FLAG_SHADOW)); }

    private void setSeeThrough(boolean canSeeThrough) { setFlags((byte) (canSeeThrough ? getFlags() | FLAG_SEE_THROUGH : getFlags() & ~FLAG_SEE_THROUGH)); }

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