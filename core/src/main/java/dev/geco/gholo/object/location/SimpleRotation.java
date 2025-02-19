package dev.geco.gholo.object.location;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class SimpleRotation implements Cloneable {

    private Float yaw;
    private Float pitch;

    public SimpleRotation(Float yaw, Float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public Float getYaw() { return yaw; }

    public SimpleRotation setYaw(Float yaw) {
        this.yaw = yaw;
        return this;
    }

    public Float getPitch() { return pitch; }

    public SimpleRotation setPitch(Float pitch) {
        this.pitch = pitch;
        return this;
    }

    @Override
    public String toString() {
        JSONObject simpleRotation = new JSONObject();
        if(yaw != null) simpleRotation.put("yaw", yaw);
        if(pitch != null) simpleRotation.put("pitch", pitch);
        return simpleRotation.toJSONString();
    }

    public static @Nullable SimpleRotation fromString(@NotNull String string) {
        JSONParser parser = new JSONParser();
        try {
            JSONObject data = (JSONObject) parser.parse(string);
            Float yaw = data.containsKey("yaw") ? ((Number) data.get("yaw")).floatValue() : null;
            Float pitch = data.containsKey("pitch") ? ((Number) data.get("pitch")).floatValue() : null;
            return new SimpleRotation(yaw, pitch);
        } catch(Throwable e) { e.printStackTrace(); }
        return null;
    }

    @Override
    public @NotNull SimpleRotation clone() { try { return (SimpleRotation) super.clone(); } catch(CloneNotSupportedException e) { throw new Error(e); } }

}