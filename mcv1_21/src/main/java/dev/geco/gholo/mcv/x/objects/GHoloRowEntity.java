package dev.geco.gholo.mcv.x.objects;

import java.lang.reflect.*;
import java.util.*;

import org.joml.Vector3f;

import org.bukkit.*;
import org.bukkit.craftbukkit.v1_21_R1.*;
import org.bukkit.craftbukkit.v1_21_R1.entity.*;
import org.bukkit.craftbukkit.v1_21_R1.util.*;
import org.bukkit.entity.Player;

import net.minecraft.network.chat.*;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.*;
import net.minecraft.server.level.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.phys.*;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.objects.*;

public class GHoloRowEntity extends Display.TextDisplay implements IGHoloRowEntity {

    protected final GHoloRow holoRow;
    protected final GHoloMain GPM;
    protected final EntityDataAccessor<Component> HOLO_TEXT_DATA;
    protected final EntityDataAccessor<Vector3f> HOLO_SIZE_DATA;

    public GHoloRowEntity(GHoloRow HoloRow) {
        super(EntityType.TEXT_DISPLAY, ((CraftWorld) HoloRow.getHolo().getLocation().getWorld()).getHandle());

        holoRow = HoloRow;
        GPM = GHoloMain.getInstance();

        persist = false;
        Location location = HoloRow.getHolo().getLocation();
        Location position = holoRow.getPosition();
        location.add(position);
        setPos(location.getX(), location.getY(), location.getZ());
        setRot(position.getYaw(), position.getPitch());

        setNoGravity(true);
        setInvulnerable(true);
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

        EntityDataAccessor<Vector3f> sizeAccessor = null;
        try {
            List<Field> textFieldList = new ArrayList<>();
            for(Field field : Display.class.getDeclaredFields()) if(field.getType().equals(EntityDataAccessor.class)) textFieldList.add(field);
            Field textField = textFieldList.get(4);
            textField.setAccessible(true);
            sizeAccessor = (EntityDataAccessor<Vector3f>) textField.get(this);
        } catch (Throwable ignored) { }
        HOLO_SIZE_DATA = sizeAccessor;

        handleUpdate(GHoloRowUpdateType.RANGE);
        handleUpdate(GHoloRowUpdateType.BACKGROUND_COLOR);
        handleUpdate(GHoloRowUpdateType.TEXT_OPACITY);
        handleUpdate(GHoloRowUpdateType.TEXT_SHADOW);
        handleUpdate(GHoloRowUpdateType.BILLBOARD);
        handleUpdate(GHoloRowUpdateType.SEE_THROUGH);
        handleUpdate(GHoloRowUpdateType.SIZE);
    }

    @Override
    public void tick() { }

    @Override
    public void move(MoverType MoverType, Vec3 Vec3) { }

    @Override
    public void handlePortal() { }

    @Override
    public boolean dismountsUnderwater() { return false; }

    @Override
    public void loadHoloRow() {
        ClientboundAddEntityPacket addEntityPacket = new ClientboundAddEntityPacket(getId(), getUUID(), getX(), getY(), getZ(), getXRot(), getYRot(), getType(), 0, getDeltaMovement(), getYHeadRot());
        for(Player player : holoRow.getHolo().getRawLocation().getWorld().getPlayers()) {
            ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
            serverPlayer.connection.send(addEntityPacket);
            serverPlayer.connection.send(getDataPacket(player));
        }
    }

    @Override
    public void loadHoloRow(Player Player) {
        ServerPlayer serverPlayer = ((CraftPlayer) Player).getHandle();
        serverPlayer.connection.send(new ClientboundAddEntityPacket(getId(), getUUID(), getX(), getY(), getZ(), getXRot(), getYRot(), getType(), 0, getDeltaMovement(), getYHeadRot()));
        serverPlayer.connection.send(getDataPacket(Player));
    }

    @Override
    public void publishUpdate(GHoloRowUpdateType UpdateType) {
        if(UpdateType == GHoloRowUpdateType.LOCATION) {
            Location location = holoRow.getHolo().getLocation();
            Location position = holoRow.getPosition();
            location.add(position);
            setPos(location.getX(), location.getY(), location.getZ());
            setRot(position.getYaw(), position.getPitch());
            ClientboundTeleportEntityPacket teleportEntityPacket = new ClientboundTeleportEntityPacket(this);
            for(Player player : holoRow.getHolo().getRawLocation().getWorld().getPlayers()) {
                ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
                serverPlayer.connection.send(teleportEntityPacket);
            }
            return;
        }
        handleUpdate(UpdateType);
        finishUpdate();
    }

    private void handleUpdate(GHoloRowUpdateType UpdateType) {
        GHoloData defaultData = holoRow.getHolo().getDefaultData();
        GHoloData data = holoRow.getData();
        switch (UpdateType) {
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
            case BILLBOARD:
                String billboard = data.getBillboard() != null ? data.getBillboard() : (defaultData.getBillboard() != null ? defaultData.getBillboard() : GHoloData.DEFAULT_BILLBOARD);
                setBillboard(billboard);
                break;
            case SEE_THROUGH:
                boolean seeThrough = data.getSeeThrough() != null ? data.getSeeThrough() : (defaultData.getSeeThrough() != null ? defaultData.getSeeThrough() : GHoloData.DEFAULT_SEE_THROUGH);
                setSeeThrough(seeThrough);
                break;
            case SIZE:
                float size = data.getSize() != null ? data.getSize() : (defaultData.getSize() != null ? defaultData.getSize() : GHoloData.DEFAULT_SIZE);
                setSize(size);
                break;
        }
    }

    private void setBackgroundColor(String Color) {
        Color = Color.startsWith("#") ? Color.substring(1) : Color;
        if(Color.length() == 6) Color = Color + "40";
        Color = Color.substring(Color.length() - 2) + Color.substring(0, Color.length() - 2);
        entityData.set(DATA_BACKGROUND_COLOR_ID, (int) Long.parseLong(Color, 16));
    }

    private void setRealTextOpacity(byte TextOpacity) {
        int clampedPercent = Math.max(0, Math.min(TextOpacity, 100));
        int valueInRange26To255 = 255 - (clampedPercent * 231 / 100);
        byte signedAlphaValue = (byte) (valueInRange26To255 > 127 ? valueInRange26To255 - 256 : valueInRange26To255);
        setTextOpacity(signedAlphaValue);
    }

    private void setBillboard(String Billboard) { setBillboardConstraints(BillboardConstraints.valueOf(Billboard.toUpperCase())); }

    private void setTextShadow(boolean Shadow) {
        byte currentFlags = getFlags();
        if(Shadow) setFlags((byte) (currentFlags | 1));
        else setFlags((byte) (currentFlags & ~1));
    }

    private void setSeeThrough(boolean SeeThrough) {
        byte currentFlags = getFlags();
        if(SeeThrough) setFlags((byte) (currentFlags | 2));
        else setFlags((byte) (currentFlags & ~2));
    }

    private void setSize(float Size) { entityData.set(HOLO_SIZE_DATA, new Vector3f(Size)); }

    private void finishUpdate() {
        for(Player player : holoRow.getHolo().getRawLocation().getWorld().getPlayers()) {
            ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
            serverPlayer.connection.send(getDataPacket(player));
        }
    }

    private ClientboundSetEntityDataPacket getDataPacket(Player Player) {
        String content = holoRow.getContent();
        content = GPM.getFormatUtil().formatPlaceholders(content, Player);
        Component contentComponent = CraftChatMessage.fromString(content, false, true)[0];
        List<SynchedEntityData.DataValue<?>> data = getEntityData().getNonDefaultValues();
        if(data == null) data = new ArrayList<>();
        else data.removeIf(dataValue -> dataValue.id() == HOLO_TEXT_DATA.id());
        // TODO: Currently only required for the reset of e.g. the billboard to "fixed"
        List<SynchedEntityData.DataValue<?>> defaultData = getEntityData().packDirty();
        if(defaultData != null) data.addAll(defaultData);
        //
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