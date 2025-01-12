package dev.geco.gholo.mcv.v1_20_5.objects;

import java.lang.reflect.*;
import java.util.*;

import org.joml.*;

import org.bukkit.*;
import org.bukkit.craftbukkit.*;
import org.bukkit.craftbukkit.entity.*;
import org.bukkit.craftbukkit.util.*;
import org.bukkit.entity.Player;

import io.papermc.paper.adventure.*;

import net.minecraft.network.chat.*;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.*;
import net.minecraft.server.level.*;
import net.minecraft.util.*;
import net.minecraft.world.entity.*;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.objects.*;

public class GHoloRowEntity extends Display.TextDisplay implements IGHoloRowEntity {

    protected final GHoloRow holoRow;
    protected final GHoloMain GPM;
    protected final EntityDataAccessor<Component> HOLO_TEXT_DATA;
    protected final EntityDataAccessor<Vector3f> HOLO_SCALE_DATA;

    public GHoloRowEntity(GHoloRow HoloRow) {
        super(EntityType.TEXT_DISPLAY, ((CraftWorld) HoloRow.getHolo().getRawLocation().getWorld()).getHandle());

        holoRow = HoloRow;
        GPM = GHoloMain.getInstance();

        persist = false;
        entityData.set(DATA_LINE_WIDTH_ID, 10000);

        EntityDataAccessor<Component> textAccessor = null;
        try {
            List<Field> textFieldList = new ArrayList<>();
            for(Field field : TextDisplay.class.getDeclaredFields()) if(field.getType().equals(EntityDataAccessor.class)) textFieldList.add(field);
            Field textField = textFieldList.getFirst();
            textField.setAccessible(true);
            textAccessor = (EntityDataAccessor<Component>) textField.get(this);
        } catch (Throwable ignored) { }
        HOLO_TEXT_DATA = textAccessor;

        EntityDataAccessor<Vector3f> scaleAccessor = null;
        try {
            List<Field> textFieldList = new ArrayList<>();
            for(Field field : Display.class.getDeclaredFields()) if(field.getType().equals(EntityDataAccessor.class)) textFieldList.add(field);
            Field textField = textFieldList.get(4);
            textField.setAccessible(true);
            scaleAccessor = (EntityDataAccessor<Vector3f>) textField.get(this);
        } catch (Throwable ignored) { }
        HOLO_SCALE_DATA = scaleAccessor;

        for(GHoloRowUpdateType updateType : GHoloRowUpdateType.values()) handleUpdate(updateType);
    }

    @Override
    public void loadHoloRow() {
        ClientboundAddEntityPacket addEntityPacket = new ClientboundAddEntityPacket(getId(), uuid, getX(), getY(), getZ(), getXRot(), getYRot(), getType(), 0, getDeltaMovement(), getYHeadRot());
        String permission = getPermission();
        for(Player player : holoRow.getHolo().getRawLocation().getWorld().getPlayers()) {
            if(permission != null && !GPM.getPManager().hasPermission(player, permission)) continue;
            ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
            serverPlayer.connection.send(addEntityPacket);
            serverPlayer.connection.send(getDataPacket(player));
        }
    }

    @Override
    public void loadHoloRow(Player Player) {
        ServerPlayer serverPlayer = ((CraftPlayer) Player).getHandle();
        if(!serverPlayer.level().equals(level())) return;
        String permission = getPermission();
        if(permission != null && !GPM.getPManager().hasPermission(Player, permission)) return;
        serverPlayer.connection.send(new ClientboundAddEntityPacket(getId(), uuid, getX(), getY(), getZ(), getXRot(), getYRot(), getType(), 0, getDeltaMovement(), getYHeadRot()));
        serverPlayer.connection.send(getDataPacket(Player));
    }

    @Override
    public void publishUpdate(GHoloRowUpdateType UpdateType) {
        handleUpdate(UpdateType);
        if(UpdateType == GHoloRowUpdateType.LOCATION) {
            ClientboundTeleportEntityPacket teleportEntityPacket = new ClientboundTeleportEntityPacket(this);
            String permission = getPermission();
            for(Player player : holoRow.getHolo().getRawLocation().getWorld().getPlayers()) {
                if(permission != null && !GPM.getPManager().hasPermission(player, permission)) continue;
                ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
                serverPlayer.connection.send(teleportEntityPacket);
            }
            return;
        } else if(UpdateType == GHoloRowUpdateType.PERMISSION) {
            unloadHoloRow();
            loadHoloRow();
            return;
        }
        finishUpdate();
    }

    private void handleUpdate(GHoloRowUpdateType UpdateType) {
        GHoloData defaultData = holoRow.getHolo().getRawDefaultData();
        GHoloData data = holoRow.getRawData();
        switch (UpdateType) {
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
                boolean textShadow = data.getTextShadow() != null ? data.getTextShadow() : (defaultData.getTextShadow() != null ? defaultData.getTextShadow() : GHoloData.DEFAULT_TEXT_SHADOW);
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
                boolean seeThrough = data.getSeeThrough() != null ? data.getSeeThrough() : (defaultData.getSeeThrough() != null ? defaultData.getSeeThrough() : GHoloData.DEFAULT_SEE_THROUGH);
                setSeeThrough(seeThrough);
                break;
            case SCALE:
                Vector3f scale = data.getScale() != null ? data.getScale() : (defaultData.getScale() != null ? defaultData.getScale() : GHoloData.DEFAULT_SCALE);
                entityData.set(HOLO_SCALE_DATA, scale);
                break;
            case BRIGHTNESS:
                Byte brigthness = data.getBrightness() != null ? data.getBrightness() : (defaultData.getBrightness() != null ? defaultData.getBrightness() : GHoloData.DEFAULT_BRIGHTNESS);
                setBrightnessOverride(brigthness != null ? new Brightness(brigthness, Brightness.FULL_BRIGHT.sky()) : null);
        }
    }

    private void setBackgroundColor(String Color) {
        Color = Color.startsWith("#") ? Color.substring(1) : Color;
        if(Color.length() == 6) Color = Color + "40";
        Color = Color.substring(Color.length() - 2) + Color.substring(0, Color.length() - 2);
        entityData.set(DATA_BACKGROUND_COLOR_ID, (int) Long.parseLong(Color, 16));
    }

    private void setRealTextOpacity(byte TextOpacity) {
        int clampedPercent = java.lang.Math.max(0, java.lang.Math.min(TextOpacity, 100));
        int valueInRange26To255 = 255 - (clampedPercent * 231 / 100);
        byte signedAlphaValue = (byte) (valueInRange26To255 > 127 ? valueInRange26To255 - 256 : valueInRange26To255);
        setTextOpacity(signedAlphaValue);
    }

    private void setTextAlignment(String TextAlignment) {
        setFlags((byte) (TextAlignment.equalsIgnoreCase(Align.LEFT.name()) ? getFlags() | FLAG_ALIGN_LEFT : getFlags() & ~FLAG_ALIGN_LEFT));
        setFlags((byte) (TextAlignment.equalsIgnoreCase(Align.RIGHT.name()) ? getFlags() | FLAG_ALIGN_RIGHT : getFlags() & ~FLAG_ALIGN_RIGHT));
    }

    private void setBillboard(String Billboard) { setBillboardConstraints(BillboardConstraints.valueOf(Billboard.toUpperCase())); }

    private void setTextShadow(boolean Shadow) { setFlags((byte) (Shadow ? getFlags() | FLAG_SHADOW : getFlags() & ~FLAG_SHADOW)); }

    private void setSeeThrough(boolean SeeThrough) { setFlags((byte) (SeeThrough ? getFlags() | FLAG_SEE_THROUGH : getFlags() & ~FLAG_SEE_THROUGH)); }

    private void finishUpdate() {
        String permission = getPermission();
        for(Player player : holoRow.getHolo().getRawLocation().getWorld().getPlayers()) {
            if(permission != null && !GPM.getPManager().hasPermission(player, permission)) continue;
            ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
            serverPlayer.connection.send(getDataPacket(player));
        }
    }

    private String getPermission() {
        GHoloData defaultData = holoRow.getHolo().getRawDefaultData();
        GHoloData data = holoRow.getRawData();
        return data.getPermission() != null ? data.getPermission() : (defaultData.getPermission() != null ? defaultData.getPermission() : GHoloData.DEFAULT_PERMISSION);
    }

    private ClientboundSetEntityDataPacket getDataPacket(Player Player) {
        String content = holoRow.getContent();
        Component contentComponent = GPM.supportsPaperFeature() ? PaperAdventure.asVanilla((net.kyori.adventure.text.Component) GPM.getFormatUtil().formatPlaceholdersComponent(content, Player)) : CraftChatMessage.fromString(GPM.getFormatUtil().formatPlaceholders(content, Player), false, true)[0];
        List<SynchedEntityData.DataValue<?>> data = getEntityData().getNonDefaultValues();
        if(data == null) data = new ArrayList<>();
        else data.removeIf(dataValue -> dataValue.id() == HOLO_TEXT_DATA.id());
        List<SynchedEntityData.DataValue<?>> defaultResetData = getEntityData().packDirty();
        if(defaultResetData != null) data.addAll(defaultResetData);
        data.add(new SynchedEntityData.DataValue<>(HOLO_TEXT_DATA.id(), HOLO_TEXT_DATA.serializer(), contentComponent));
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
    public void unloadHoloRow(Player Player) {
        ServerPlayer serverPlayer = ((CraftPlayer) Player).getHandle();
        serverPlayer.connection.send(new ClientboundRemoveEntitiesPacket(getId()));
    }

}