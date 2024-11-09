package dev.geco.gholo.mcv.x.objects;

import org.bukkit.*;
import org.bukkit.craftbukkit.v1_21_R2.*;

import net.minecraft.network.chat.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.phys.*;

public class HoloEntity extends Display.TextDisplay {

    public HoloEntity(Location Location) {

        super(EntityType.TEXT_DISPLAY, ((CraftWorld) Location.getWorld()).getHandle());

        persist = false;
        setPos(Location.getX(), Location.getY(), Location.getZ());

        setNoGravity(true);
        setInvulnerable(true);
        setText(Component.literal("Test"));
    }

    public void tick() { }

    public void move(MoverType MoverType, Vec3 Vec3) { }

    protected void handlePortal() { }

    public boolean dismountsUnderwater() { return false; }

}