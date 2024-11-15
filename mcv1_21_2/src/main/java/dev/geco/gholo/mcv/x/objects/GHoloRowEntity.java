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

        setBillboardConstraints(BillboardConstraints.CENTER);
        entityData.set(DATA_LINE_WIDTH_ID, 1000);
        setViewRange((float) (holoRow.getHolo().getRange() / 64));

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
    public void rerender() {
        for(Player player : holoRow.getHolo().getRawLocation().getWorld().getPlayers()) {
            removeHoloRow(player);
            spawnHoloRow(player);
        }
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
                rerender();
                break;
            case RANGE:
                setViewRange((float) (holoRow.getHolo().getRange() / 64));
                rerender();
        }
    }

    private ClientboundSetEntityDataPacket getDataPacket(Player Player) {
        String content = holoRow.getContent();
        content = GPM.getFormatUtil().formatPlaceholders(content, Player);
        Component contentComponent = CraftChatMessage.fromJSON(GPM.getMManager().getAsJSON(content));
        List<SynchedEntityData.DataValue<?>> data = getEntityData().getNonDefaultValues();
        if(data == null) data = new ArrayList<>();
        else data.removeIf(a -> a.id() == 23);
        data.add(new SynchedEntityData.DataValue<>(textDataAccessor.id(), textDataAccessor.serializer(), contentComponent));
        return new ClientboundSetEntityDataPacket(getId(), data);
    }

    /*public void startTicking() {
        if(GPM.getFormatUtil().countAnimationChars(holoRow.getContent()) < 2) {
            stopTicking();
            return;
        }
        taskId = GPM.getTManager().runAtFixedRate(() -> {
            for(Player player : holoRow.getHolo().getPlayers()) {
                ServerPlayer player2 = ((CraftPlayer) player).getHandle();
                player2.connection.send(getDataPacket(player));
            }
        }, 0, 1);
    }

    public void stopTicking() {
        if(taskId != null) GPM.getTManager().cancel(taskId);
        taskId = null;
    }*/

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