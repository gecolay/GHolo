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
    protected UUID taskId;

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
        setViewRange((float) 10 / 64);

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

    public void tick() { }

    public void move(MoverType MoverType, Vec3 Vec3) { }

    protected void handlePortal() { }

    public boolean dismountsUnderwater() { return false; }

    @Override
    public void spawnHoloRow(Player Player) {
        ServerPlayer player = ((CraftPlayer) Player).getHandle();
        player.connection.send(new ClientboundAddEntityPacket(getId(), getUUID(), getX(), getY(), getZ(), getXRot(), getYRot(), getType(), 0, getDeltaMovement(), getYHeadRot()));
        player.connection.send(getDataPacket(Player));
    }

    @Override
    public void rerender() {
        for(Player player : holoRow.getHolo().getPlayers()) {
            removeHoloRow(player);
            spawnHoloRow(player);
        }
    }

    @Override
    public void updateHoloRowRange(double Range) {
        setViewRange((float) Range / 64);
    }

    @Override
    public void updateHoloRowContent(String Content) {
        startTicking();
        for(Player player : holoRow.getHolo().getPlayers()) {
            ServerPlayer player2 = ((CraftPlayer) player).getHandle();
            player2.connection.send(getDataPacket(player));
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

    public void startTicking() {
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
    }

    @Override
    public void adjustLocationToHolo() {
        Location location = holoRow.getHolo().getLocation();
        location.add(holoRow.getOffsets());
        setPos(location.getX(), location.getY(), location.getZ());
        rerender();
    }

    @Override
    public void removeHoloRow(Player Player) {
        ServerPlayer player = ((CraftPlayer) Player).getHandle();
        player.connection.send(new ClientboundRemoveEntitiesPacket(getId()));
    }

}