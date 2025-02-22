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

    public SimpleOffset setX(double x) {
        this.x = x;
        return this;
    }

    public double getY() { return y; }

    public SimpleOffset setY(double y) {
        this.y = y;
        return this;
    }

    public double getZ() { return z; }

    public SimpleOffset setZ(double z) {
        this.z = z;
        return this;
    }

    @Override
    public String toString() {
        JSONObject simpleLocation = new JSONObject();
        simpleLocation.put("x", x);
        simpleLocation.put("y", y);
        simpleLocation.put("z", z);
        return simpleLocation.toJSONString();
    }

    public static @Nullable SimpleOffset fromString(@NotNull String string) {
        JSONParser parser = new JSONParser();
        try {
            JSONObject data = (JSONObject) parser.parse(string);
            double x = ((Number) data.get("x")).doubleValue();
            double y = ((Number) data.get("y")).doubleValue();
            double z = ((Number) data.get("z")).doubleValue();
            return new SimpleOffset(x, y, z);
        } catch(Throwable e) { e.printStackTrace(); }
        return null;
    }

    @Override
    public @NotNull SimpleOffset clone() { try { return (SimpleOffset) super.clone(); } catch(CloneNotSupportedException e) { throw new Error(e); } }

}