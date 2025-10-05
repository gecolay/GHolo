package dev.geco.gholo.object.simple;

import dev.geco.gholo.GHoloMain;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

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
        JSONObject simpleVector = new JSONObject();
        if(x != 0) simpleVector.put("x", x);
        if(y != 0) simpleVector.put("y", y);
        if(z != 0) simpleVector.put("z", z);
        return simpleVector.toJSONString();
    }

    public static @Nullable SimpleVector fromString(@NotNull String string) {
        JSONParser parser = new JSONParser();
        try {
            JSONObject data = (JSONObject) parser.parse(string);
            double x = data.get("x") != null ? ((Number) data.get("x")).doubleValue() : 0;
            double y = data.get("y") != null ? ((Number) data.get("y")).doubleValue() : 0;
            double z = data.get("z") != null ? ((Number) data.get("z")).doubleValue() : 0;
            return new SimpleVector(x, y, z);
        } catch(Throwable e) { GHoloMain.getInstance().getLogger().log(Level.SEVERE, "Could not load vector data!", e); }
        return null;
    }

    @Override
    public @NotNull SimpleVector clone() { try { return (SimpleVector) super.clone(); } catch(CloneNotSupportedException e) { throw new Error(e); } }

}