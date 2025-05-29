package dev.geco.gholo.object.simple;

import dev.geco.gholo.GHoloMain;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.logging.Level;

public class SimpleLocation extends Location {

    public SimpleLocation(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    public @NotNull SimpleLocation add(@NotNull SimpleOffset offset) {
        setX(getX() + offset.getX());
        setY(getY() + offset.getY());
        setZ(getZ() + offset.getZ());
        return this;
    }

    @Override
    public @NotNull String toString() {
        JSONObject simpleLocation = new JSONObject();
        simpleLocation.put("world", getWorld().getName());
        simpleLocation.put("x", getX());
        simpleLocation.put("y", getY());
        simpleLocation.put("z", getZ());
        return simpleLocation.toJSONString();
    }

    public static @Nullable SimpleLocation fromString(@NotNull String string) {
        JSONParser parser = new JSONParser();
        try {
            JSONObject data = (JSONObject) parser.parse(string);
            World world = Bukkit.getWorld((String) data.get("world"));
            double x = ((Number) data.get("x")).doubleValue();
            double y = ((Number) data.get("y")).doubleValue();
            double z = ((Number) data.get("z")).doubleValue();
            return new SimpleLocation(world, x, y, z);
        } catch(Throwable e) { GHoloMain.getInstance().getLogger().log(Level.SEVERE, "Could not load location data!", e); }
        return null;
    }

    public static @NotNull SimpleLocation fromBukkitLocation(@NotNull Location location) { return new SimpleLocation(location.getWorld(), location.getX(), location.getY(), location.getZ()); }

    @Override
    public @NotNull SimpleLocation clone() { return (SimpleLocation) super.clone(); }

}