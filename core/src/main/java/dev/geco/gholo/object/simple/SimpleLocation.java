package dev.geco.gholo.object.simple;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class SimpleLocation extends Location {

    public SimpleLocation(@NotNull World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    public SimpleLocation add(SimpleOffset offset) {
        set(getX() + offset.getX(), getY() + offset.getY(), getZ() + offset.getZ());
        return this;
    }

    @Override
    public String toString() {
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
            if(world == null) throw new IllegalArgumentException("World '" + data.get("world") + "' does not exist!");
            double x = ((Number) data.get("x")).doubleValue();
            double y = ((Number) data.get("y")).doubleValue();
            double z = ((Number) data.get("z")).doubleValue();
            return new SimpleLocation(world, x, y, z);
        } catch(Throwable e) { e.printStackTrace(); }
        return null;
    }

    public static @NotNull SimpleLocation fromBukkitLocation(@NotNull Location location) { return new SimpleLocation(location.getWorld(), location.getX(), location.getY(), location.getZ()); }

    @Override
    public @NotNull SimpleLocation clone() { return (SimpleLocation) super.clone(); }

}