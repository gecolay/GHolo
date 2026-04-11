package dev.geco.gholo.object.simple;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.geco.gholo.GHoloMain;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Level;

public class SimpleLocation extends Location {

    public SimpleLocation(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    public @NotNull SimpleLocation add(@NotNull SimpleVector offset) {
        setX(getX() + offset.getX());
        setY(getY() + offset.getY());
        setZ(getZ() + offset.getZ());
        return this;
    }

    @Override
    public @NotNull String toString() {
        JsonObject simpleLocation = new JsonObject();
        simpleLocation.addProperty("world", getWorld().getName());
        simpleLocation.addProperty("x", getX());
        simpleLocation.addProperty("y", getY());
        simpleLocation.addProperty("z", getZ());
        return simpleLocation.toString();
    }

    public static @Nullable SimpleLocation fromString(@NotNull String string) {
        try {
            JsonObject data = (JsonObject) JsonParser.parseString(string);
            World world = Bukkit.getWorld(data.get("world").getAsString());
            double x = data.get("x").getAsDouble();
            double y = data.get("y").getAsDouble();
            double z = data.get("z").getAsDouble();
            return new SimpleLocation(world, x, y, z);
        } catch(Throwable e) { GHoloMain.getInstance().getLogger().log(Level.SEVERE, "Could not load location data!", e); }
        return null;
    }

    public static @NotNull SimpleLocation fromBukkitLocation(@NotNull Location location) { return new SimpleLocation(location.getWorld(), location.getX(), location.getY(), location.getZ()); }

    @Override
    public @NotNull SimpleLocation clone() { return (SimpleLocation) super.clone(); }

}