package dev.geco.gholo.mcv.v1_20_2.object;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.object.GHoloData;
import dev.geco.gholo.object.GHoloRow;
import dev.geco.gholo.object.GHoloRowUpdateType;
import dev.geco.gholo.object.IGHoloRowEntity;
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
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R2.util.CraftChatMessage;
import org.bukkit.entity.Player;
import org.joml.Vector3f;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class GHoloRowEntity extends Display.TextDisplay implements IGHoloRowEntity {

    protected final GHoloRow holoRow;
    protected final GHoloMain gHoloMain;
    protected final EntityDataAccessor<Component> holoTextData;
    protected final EntityDataAccessor<Vector3f> holoScaleData;

    public GHoloRowEntity(GHoloRow holoRow) {
        super(EntityType.TEXT_DISPLAY, ((CraftWorld) holoRow.getHolo().getRawLocation().getWorld()).getHandle());
        this.holoRow = holoRow;
        gHoloMain = GHoloMain.getInstance();
        persist = false;
        entityData.set(DATA_LINE_WIDTH_ID, 10000);
        EntityDataAccessor<Component> textAccessor = null;
        try {
            List<Field> textFieldList = new ArrayList<>();
            for(Field field : TextDisplay.class.getDeclaredFields()) if(field.getType().equals(EntityDataAccessor.class)) textFieldList.add(field);
            Field textField = textFieldList.get(0);
            textField.setAccessible(true);
            textAccessor = (EntityDataAccessor<Component>) textField.get(this);
        } catch (Throwable e) { e.printStackTrace(); }
        holoTextData = textAccessor;
        EntityDataAccessor<Vector3f> scaleAccessor = null;
        try {
            List<Field> textFieldList = new ArrayList<>();
            for(Field field : Display.class.getDeclaredFields()) if(field.getType().equals(EntityDataAccessor.class)) textFieldList.add(field);
            Field textField = textFieldList.get(4);
            textField.setAccessible(true);
            scaleAccessor = (EntityDataAccessor<Vector3f>) textField.get(this);
        } catch (Throwable e) { e.printStackTrace(); }
        holoScaleData = scaleAccessor;
        for(GHoloRowUpdateType updateType : GHoloRowUpdateType.values()) handleUpdate(updateType);
    }

    @Override
    public void loadHoloRow() {
        ClientboundAddEntityPacket addEntityPacket = new ClientboundAddEntityPacket(getId(), uuid, getX(), getY(), getZ(), getXRot(), getYRot(), getType(), 0, getDeltaMovement(), getYHeadRot());
        String permission = getPermission();
        for(Player player : holoRow.getHolo().getRawLocation().getWorld().getPlayers()) {
            if(permission != null && !gHoloMain.getPermissionService().hasPermission(player, permission)) continue;
            ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
            serverPlayer.connection.send(addEntityPacket);
            serverPlayer.connection.send(getDataPacket(player));
        }
    }

    @Override
    public void loadHoloRow(Player player) {
        ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
        if(!serverPlayer.level().equals(level())) return;
        String permission = getPermission();
        if(permission != null && !gHoloMain.getPermissionService().hasPermission(player, permission)) return;
        serverPlayer.connection.send(new ClientboundAddEntityPacket(getId(), uuid, getX(), getY(), getZ(), getXRot(), getYRot(), getType(), 0, getDeltaMovement(), getYHeadRot()));
        serverPlayer.connection.send(getDataPacket(player));
    }

    @Override
    public void publishUpdate(GHoloRowUpdateType updateType) {
        handleUpdate(updateType);
        if(updateType == GHoloRowUpdateType.LOCATION) {
            ClientboundTeleportEntityPacket teleportEntityPacket = new ClientboundTeleportEntityPacket(this);
            String permission = getPermission();
            for(Player player : holoRow.getHolo().getRawLocation().getWorld().getPlayers()) {
                if(permission != null && !gHoloMain.getPermissionService().hasPermission(player, permission)) continue;
                ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
                serverPlayer.connection.send(teleportEntityPacket);
            }
            return;
        } else if(updateType == GHoloRowUpdateType.PERMISSION) {
            unloadHoloRow();
            loadHoloRow();
            return;
        }
        finishUpdate();
    }

    private void handleUpdate(GHoloRowUpdateType updateType) {
        GHoloData defaultData = holoRow.getHolo().getRawDefaultData();
        GHoloData data = holoRow.getRawData();
        switch (updateType) {
            case LOCATION:
                Location location = holoRow.getHolo().getLocation();
                Location position = holoRow.getRawPosition();
                location.add(position);
                setPos(location.getX(), location.getY(), location.getZ());
                setRot(position.getYaw(), position.getPitch());
                break;
            case RANGE:
                double range = data.getRange() != null ? data.getRange() : (defaultData.getRange() != null ? defaultData.getRange() : GHoloData.DEFAULT_RANGE);
                setViewRange((float) (range / 64));
                break;
            case BACKGROUND_COLOR:
                String backgroundColor = data.getBackgroundColor() != null ? data.getBackgroundColor() : (defaultData.getBackgroundColor() != null ? defaultData.getBackgroundColor() : GHoloData.DEFAULT_BACKGROUND_COLOR);
                setBackgroundColor(backgroundColor);
                break;
            case TEXT_OPACITY:
                byte textOpacity = data.getTextOpacity() != null ? data.getTextOpacity() : (defaultData.getTextOpacity() != null ? defaultData.getTextOpacity() : GHoloData.DEFAULT_TEXT_OPACITY);
                setRealTextOpacity(textOpacity);
                break;
            case TEXT_SHADOW:
                boolean textShadow = data.getTextShadow() != null ? data.getTextShadow() : (defaultData.getTextShadow() != null ? defaultData.getTextShadow() : GHoloData.DEFAULT_HAS_TEXT_SHADOW);
                setTextShadow(textShadow);
                break;
            case TEXT_ALIGNMENT:
                String textAlignment = data.getTextAlignment() != null ? data.getTextAlignment() : (defaultData.getTextAlignment() != null ? defaultData.getTextAlignment() : GHoloData.DEFAULT_TEXT_ALIGNMENT);
                setTextAlignment(textAlignment);
            case BILLBOARD:
                String billboard = data.getBillboard() != null ? data.getBillboard() : (defaultData.getBillboard() != null ? defaultData.getBillboard() : GHoloData.DEFAULT_BILLBOARD);
                setBillboard(billboard);
                break;
            case SEE_THROUGH:
                boolean seeThrough = data.getSeeThrough() != null ? data.getSeeThrough() : (defaultData.getSeeThrough() != null ? defaultData.getSeeThrough() : GHoloData.DEFAULT_CAN_SEE_THROUGH);
                setSeeThrough(seeThrough);
                break;
            case SCALE:
                Vector3f scale = data.getScale() != null ? data.getScale() : (defaultData.getScale() != null ? defaultData.getScale() : GHoloData.DEFAULT_SCALE);
                entityData.set(holoScaleData, scale);
                break;
            case BRIGHTNESS:
                Byte brigthness = data.getBrightness() != null ? data.getBrightness() : (defaultData.getBrightness() != null ? defaultData.getBrightness() : GHoloData.DEFAULT_BRIGHTNESS);
                setBrightnessOverride(brigthness != null ? new Brightness(brigthness, Brightness.FULL_BRIGHT.sky()) : null);
        }
    }

    private void setBackgroundColor(String color) {
        color = color.startsWith("#") ? color.substring(1) : color;
        if(color.length() == 6) color = color + "40";
        color = color.substring(color.length() - 2) + color.substring(0, color.length() - 2);
        entityData.set(DATA_BACKGROUND_COLOR_ID, (int) Long.parseLong(color, 16));
    }

    private void setRealTextOpacity(byte textOpacity) {
        int clampedPercent = java.lang.Math.max(0, java.lang.Math.min(textOpacity, 100));
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
            serverPlayer.connection.send(getDataPacket(player));
        }
    }

    private String getPermission() {
        GHoloData defaultData = holoRow.getHolo().getRawDefaultData();
        GHoloData data = holoRow.getRawData();
        return data.getPermission() != null ? data.getPermission() : (defaultData.getPermission() != null ? defaultData.getPermission() : GHoloData.DEFAULT_PERMISSION);
    }

    private ClientboundSetEntityDataPacket getDataPacket(Player player) {
        String content = holoRow.getContent();
        Component contentComponent = gHoloMain.supportsPaperFeature() ? PaperAdventure.asVanilla((net.kyori.adventure.text.Component) gHoloMain.getFormatUtil().formatPlaceholdersComponent(content, player)) : CraftChatMessage.fromString(gHoloMain.getFormatUtil().formatPlaceholders(content, player), false, true)[0];
        List<SynchedEntityData.DataValue<?>> data = getEntityData().getNonDefaultValues();
        if(data == null) data = new ArrayList<>();
        else data.removeIf(dataValue -> dataValue.id() == holoTextData.getId());
        List<SynchedEntityData.DataValue<?>> defaultResetData = getEntityData().packDirty();
        if(defaultResetData != null) data.addAll(defaultResetData);
        data.add(new SynchedEntityData.DataValue<>(holoTextData.getId(), holoTextData.getSerializer(), contentComponent));
        return new ClientboundSetEntityDataPacket(getId(), data);
    }

    @Override
    public void unloadHoloRow() {
        ClientboundRemoveEntitiesPacket removeEntityPacket = new ClientboundRemoveEntitiesPacket(getId());
        for(Player player : holoRow.getHolo().getRawLocation().getWorld().getPlayers()) {
            ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
            serverPlayer.connection.send(removeEntityPacket);
        }
    }

    @Override
    public void unloadHoloRow(Player player) {
        ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
        serverPlayer.connection.send(new ClientboundRemoveEntitiesPacket(getId()));
    }

}