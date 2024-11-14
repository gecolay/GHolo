package dev.geco.gholo.mcv.x.objects;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_21_R2.*;

import net.minecraft.network.chat.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.phys.*;

import dev.geco.gholo.objects.*;
import org.bukkit.craftbukkit.v1_21_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class GHoloRowEntity extends Display.TextDisplay implements IGHoloRowEntity {

    protected final GHoloRow holoRow;
    protected final EntityDataAccessor<Component> textDataAccessor;

    public GHoloRowEntity(GHoloRow HoloRow) {
        super(EntityType.TEXT_DISPLAY, ((CraftWorld) HoloRow.getHolo().getLocation().getWorld()).getHandle());

        holoRow = HoloRow;

        persist = false;
        Location location = HoloRow.getHolo().getLocation();
        location.add(holoRow.getOffsets());
        setPos(location.getX(), location.getY(), location.getZ());
        setRot(holoRow.getLocationYaw(), holoRow.getLocationPitch());

        setNoGravity(true);
        setInvulnerable(true);

        setBillboardConstraints(BillboardConstraints.CENTER);
        setWidth(1000);

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

    public ClientboundBundlePacket getSpawnPacket(ServerPlayer Player) {

        List<Packet<? super ClientGamePacketListener>> packages = new ArrayList<>();

        ClientboundAddEntityPacket addEntityPacket = new ClientboundAddEntityPacket(getId(), getUUID(), getX(), getY(), getZ(), getXRot(), getYRot(), getType(), 0, getDeltaMovement(), getYHeadRot());
        packages.add(addEntityPacket);

        List<SynchedEntityData.DataValue<?>> data = getEntityData().getNonDefaultValues();
        if(data == null) data = new ArrayList<>();
        else data.removeIf(a -> a.id() == 23);
        data.add(new SynchedEntityData.DataValue<>(textDataAccessor.id(), textDataAccessor.serializer(), Component.literal(holoRow.getContent())));
        ClientboundSetEntityDataPacket setEntityDataPacket = new ClientboundSetEntityDataPacket(getId(), data);
        packages.add(setEntityDataPacket);

        return new ClientboundBundlePacket(packages);
    }

    public void tick() { }

    public void move(MoverType MoverType, Vec3 Vec3) { }

    protected void handlePortal() { }

    public boolean dismountsUnderwater() { return false; }

    @Override
    public void spawnHoloRow(Player Player) {
        ServerPlayer player = ((CraftPlayer) Player).getHandle();
        player.connection.send(getSpawnPacket(player));
    }

    @Override
    public void rerender() {
        for(Player player : holoRow.getHolo().getPlayers()) {
            removeHoloRow(player);
            spawnHoloRow(player);
        }
    }

    @Override
    public void updateHoloRowContent(String Content) {
        for(Player player : holoRow.getHolo().getPlayers()) {
            List<SynchedEntityData.DataValue<?>> data = getEntityData().getNonDefaultValues();
            if(data == null) data = new ArrayList<>();
            else data.removeIf(a -> a.id() == 23);
            data.add(new SynchedEntityData.DataValue<>(textDataAccessor.id(), textDataAccessor.serializer(), Component.literal(holoRow.getContent())));
            ClientboundSetEntityDataPacket setEntityDataPacket = new ClientboundSetEntityDataPacket(getId(), data);
            ServerPlayer player2 = ((CraftPlayer) player).getHandle();
            player2.connection.send(setEntityDataPacket);
        }
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
        discard();
    }

}