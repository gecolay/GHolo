package dev.geco.gholo.object.simple;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class SimpleOffset implements Cloneable {

    private double x;
    private double y;
    private double z;

    public SimpleOffset(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() { return x; }

    public @NotNull SimpleOffset setX(double x) {
        this.x = x;
        return this;
    }

    public double getY() { return y; }

    public @NotNull SimpleOffset setY(double y) {
        this.y = y;
        return this;
    }

    public double getZ() { return z; }

    public @NotNull SimpleOffset setZ(double z) {
        this.z = z;
        return this;
    }

    @Override
    public @NotNull String toString() {
        JSONObject simpleLocation = new JSONObject();
        if(x != 0) simpleLocation.put("x", x);
        if(y != 0) simpleLocation.put("y", y);
        if(z != 0) simpleLocation.put("z", z);
        return simpleLocation.toJSONString();
    }

    public static @Nullable SimpleOffset fromString(@NotNull String string) {
        JSONParser parser = new JSONParser();
        try {
            JSONObject data = (JSONObject) parser.parse(string);
            double x = data.get("x") != null ? ((Number) data.get("x")).doubleValue() : 0;
            double y = data.get("y") != null ? ((Number) data.get("y")).doubleValue() : 0;
            double z = data.get("z") != null ? ((Number) data.get("z")).doubleValue() : 0;
            return new SimpleOffset(x, y, z);
        } catch(Throwable ignored) { }
        return null;
    }

    @Override
    public @NotNull SimpleOffset clone() { try { return (SimpleOffset) super.clone(); } catch(CloneNotSupportedException e) { throw new Error(e); } }

}