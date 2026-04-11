package dev.geco.gholo.object.simple;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.geco.gholo.GHoloMain;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Level;

public class SimpleVector implements Cloneable {

    private double x;
    private double y;
    private double z;

    public SimpleVector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() { return x; }

    public @NotNull SimpleVector setX(double x) {
        this.x = x;
        return this;
    }

    public double getY() { return y; }

    public @NotNull SimpleVector setY(double y) {
        this.y = y;
        return this;
    }

    public double getZ() { return z; }

    public @NotNull SimpleVector setZ(double z) {
        this.z = z;
        return this;
    }

    @Override
    public @NotNull String toString() {
        JsonObject simpleVector = new JsonObject();
        if(x != 0) simpleVector.addProperty("x", x);
        if(y != 0) simpleVector.addProperty("y", y);
        if(z != 0) simpleVector.addProperty("z", z);
        return simpleVector.toString();
    }

    public static @Nullable SimpleVector fromString(@NotNull String string) {
        try {
            JsonObject data = (JsonObject) JsonParser.parseString(string);
            double x = data.has("x") && !data.get("x").isJsonNull() ? data.get("x").getAsDouble() : 0;
            double y = data.has("y") && !data.get("y").isJsonNull() ? data.get("y").getAsDouble() : 0;
            double z = data.has("z") && !data.get("z").isJsonNull() ? data.get("z").getAsDouble() : 0;
            return new SimpleVector(x, y, z);
        } catch(Throwable e) { GHoloMain.getInstance().getLogger().log(Level.SEVERE, "Could not load vector data!", e); }
        return null;
    }

    @Override
    public @NotNull SimpleVector clone() { try { return (SimpleVector) super.clone(); } catch(CloneNotSupportedException e) { throw new Error(e); } }

}