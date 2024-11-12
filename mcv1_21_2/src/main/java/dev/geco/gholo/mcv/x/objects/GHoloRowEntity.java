package dev.geco.gholo.mcv.x.objects;

import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_21_R2.*;

import net.minecraft.network.chat.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.phys.*;

import dev.geco.gholo.objects.*;

import java.util.List;

public class GHoloRowEntity extends Display.TextDisplay implements IGHoloRowEntity {

    protected final GHoloRow holoRow;

    public GHoloRowEntity(GHoloRow HoloRow) {
        super(EntityType.TEXT_DISPLAY, ((CraftWorld) HoloRow.getHolo().getLocation().getWorld()).getHandle());

        holoRow = HoloRow;

        persist = false;
        Location location = HoloRow.getHolo().getLocation();
        setPos(location.getX(), location.getY(), location.getZ());

        setNoGravity(true);
        setInvulnerable(true);

        // set everything from the HoloRow
        //setText(Component.literal(HoloRow.getContent()));
        //setBillboardConstraints(BillboardConstraints.CENTER);
    }

    public void spawnHoloRow(ServerPlayer Player) {

        List<SynchedEntityData.DataValue<?>> a = getEntityData().getNonDefaultValues();
        /*for(SynchedEntityData.DataValue<?> b : a) {
            if(b.id() == 1) {
                a.remove(b);
                a.add(new SynchedEntityData.DataValue<>(b.id(), (EntityDataSerializer<Object>) b.serializer(), ""));
            }
        }*/


        //a.add(new SynchedEntityData.DataValue<Object>(1, (EntityDataSerializer<Object>) a.get(0).serializer(), Component.literal("")));
        // should be the same for all players. it is a copy of the values. just override the "text" for the player at the moment we send it

        Player.connection.send(new ClientboundAddEntityPacket(getId(), getUUID(), getX(), getY(), getZ(), getXRot(), getYRot(), getType(), 0, getDeltaMovement(), getYHeadRot()));
        Player.connection.send(new ClientboundSetEntityDataPacket(getId(), a));
    }

    public void removeHoloRow(ServerPlayer Player) {
        /*if(task != null) GHoloMain.getInstance().getTManager().cancel(task);
        for(Player player : level().players()) {
            if(holo.getPlayers().contains(player.getBukkitEntity())) {
                holo.getPlayers().remove(player.getBukkitEntity());
                ((ServerPlayer) player).connection.send(new ClientboundRemoveEntitiesPacket(getId()));
            }
        }*/
        discard();
    }

    public void tick() { }

    public void move(MoverType MoverType, Vec3 Vec3) { }

    protected void handlePortal() { }

    public boolean dismountsUnderwater() { return false; }

}