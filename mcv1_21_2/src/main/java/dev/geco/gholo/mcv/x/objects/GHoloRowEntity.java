package dev.geco.gholo.mcv.x.objects;

import java.lang.reflect.*;
import java.util.*;

import org.bukkit.*;
import org.bukkit.craftbukkit.v1_21_R2.*;
import org.bukkit.craftbukkit.v1_21_R2.entity.*;
import org.bukkit.craftbukkit.v1_21_R2.util.*;
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
    protected final EntityDataAccessor<Component> textDataAccessor;

    public GHoloRowEntity(GHoloRow HoloRow) {
        super(EntityType.TEXT_DISPLAY, ((CraftWorld) HoloRow.getHolo().getLocation().getWorld()).getHandle());

        holoRow = HoloRow;
        GPM = GHoloMain.getInstance();

        persist = false;
        Location location = HoloRow.getHolo().getLocation();
        location.add(holoRow.getOffsets());
        setPos(location.getX(), location.getY(), location.getZ());
        setRot(holoRow.getLocationYaw(), holoRow.getLocationPitch());

        setNoGravity(true);
        setInvulnerable(true);
        setAlignment(3);
        setSeeThrough(true);
        setShadow(true);

        entityData.set(DATA_LINE_WIDTH_ID, 1000);
        //setViewRange((float) (holoRow.getHolo().getRange() / 64));

        EntityDataAccessor<Component> dataAccessor;
        try {
            List<Field> textFieldList = new ArrayList<>();
            for(Field field : Display.TextDisplay.class.getDeclaredFields()) if(field.getType().equals(EntityDataAccessor.class)) textFieldList.add(field);
            Field textField = textFieldList.getFirst();
            textField.setAccessible(true);
            dataAccessor = (EntityDataAccessor<Component>) textField.get(this);
        } catch (Throwable e) {
            dataAccessor = null;
        }
        textDataAccessor = dataAccessor;
    }

    private void setBackgroundColor(int BackgroundColor) { entityData.set(DATA_BACKGROUND_COLOR_ID, java.awt.Color.decode("").getRGB()); }

    private void setAlignment(int Alignment) { setBillboardConstraints(BillboardConstraints.values()[Alignment]); }

    private void setShadow(boolean Shadow) {
        byte flags = getFlags();
        if(Shadow) flags |= (byte) (1 << 1);
        else flags &= ~(byte) (1 << 1);
        setFlags(flags);
    }

    private void setSeeThrough(boolean SeeThrough) {
        byte flags = getFlags();
        if(SeeThrough) flags |= (byte) (1 << 2);
        else flags &= ~(byte) (1 << 2);
        setFlags(flags);
    }

    @Override
    public void tick() { }

    @Override
    public void move(MoverType MoverType, Vec3 Vec3) { }

    @Override
    protected void handlePortal() { }

    @Override
    public boolean dismountsUnderwater() { return false; }

    @Override
    public void spawnHoloRow() {
        ClientboundAddEntityPacket addEntityPacket = new ClientboundAddEntityPacket(getId(), getUUID(), getX(), getY(), getZ(), getXRot(), getYRot(), getType(), 0, getDeltaMovement(), getYHeadRot());
        for(Player player : holoRow.getHolo().getRawLocation().getWorld().getPlayers()) {
            ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
            serverPlayer.connection.send(addEntityPacket);
            serverPlayer.connection.send(getDataPacket(player));
        }
    }

    @Override
    public void spawnHoloRow(Player Player) {
        ServerPlayer serverPlayer = ((CraftPlayer) Player).getHandle();
        serverPlayer.connection.send(new ClientboundAddEntityPacket(getId(), getUUID(), getX(), getY(), getZ(), getXRot(), getYRot(), getType(), 0, getDeltaMovement(), getYHeadRot()));
        serverPlayer.connection.send(getDataPacket(Player));
    }

    @Override
    public void publishUpdate(GHoloRowUpdateType UpdateType) {
        switch (UpdateType) {
            case CONTENT:
                for(Player player : holoRow.getHolo().getRawLocation().getWorld().getPlayers()) {
                    ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
                    serverPlayer.connection.send(getDataPacket(player));
                }
            case LOCATION:
                Location location = holoRow.getHolo().getLocation();
                location.add(holoRow.getOffsets());
                setPos(location.getX(), location.getY(), location.getZ());
                ClientboundTeleportEntityPacket teleportEntityPacket = new ClientboundTeleportEntityPacket(getId(), PositionMoveRotation.of(this), Set.of(), false);
                for(Player player : holoRow.getHolo().getRawLocation().getWorld().getPlayers()) {
                    ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
                    serverPlayer.connection.send(teleportEntityPacket);
                }
                break;
            case RANGE:
                //setViewRange((float) (holoRow.getHolo().getRange() / 64));
                for(Player player : holoRow.getHolo().getRawLocation().getWorld().getPlayers()) {
                    ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
                    serverPlayer.connection.send(getDataPacket(player));
                }
            case BACKGROUND_COLOR:
                break;
            case TEXT_OPACITY:
                break;
            case SHADOW:
                break;
            case ALIGNMENT:

                break;
        }
    }

    private ClientboundSetEntityDataPacket getDataPacket(Player Player) {
        String content = holoRow.getContent();
        content = GPM.getFormatUtil().formatPlaceholders(content, Player);
        Component contentComponent = CraftChatMessage.fromJSON(GPM.getMManager().getAsJSON(content));
        List<SynchedEntityData.DataValue<?>> data = getEntityData().getNonDefaultValues();
        if(data == null) data = new ArrayList<>();
        else data.removeIf(dataValue -> dataValue.id() == textDataAccessor.id());
        data.add(new SynchedEntityData.DataValue<>(textDataAccessor.id(), textDataAccessor.serializer(), contentComponent));
        return new ClientboundSetEntityDataPacket(getId(), data);
    }

    @Override
    public void removeHoloRow() {
        ClientboundRemoveEntitiesPacket removeEntityPacket = new ClientboundRemoveEntitiesPacket(getId());
        for(Player player : holoRow.getHolo().getRawLocation().getWorld().getPlayers()) {
            ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
            serverPlayer.connection.send(removeEntityPacket);
        }
    }

    @Override
    public void removeHoloRow(Player Player) {
        ServerPlayer serverPlayer = ((CraftPlayer) Player).getHandle();
        serverPlayer.connection.send(new ClientboundRemoveEntitiesPacket(getId()));
    }

}