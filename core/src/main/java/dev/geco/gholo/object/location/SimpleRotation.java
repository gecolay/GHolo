package dev.geco.gholo.object.location;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class SimpleRotation {

    private float yaw;
    private float pitch;

    public SimpleRotation(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public float getYaw() { return yaw; }

    public SimpleRotation setYaw(float yaw) {
        this.yaw = yaw;
        return this;
    }

    public float getPitch() { return pitch; }

    public SimpleRotation setPitch(float pitch) {
        this.pitch = pitch;
        return this;
    }

    @Override
    public String toString() {
        JSONObject simpleRotation = new JSONObject();
        simpleRotation.put("yaw", yaw);
        simpleRotation.put("pitch", pitch);
        return simpleRotation.toJSONString();
    }

    public static @Nullable SimpleRotation fromString(@NotNull String string) {
        JSONParser parser = new JSONParser();
        try {
            JSONObject data = (JSONObject) parser.parse(string);
            float yaw = ((Number) data.get("yaw")).floatValue();
            float pitch = ((Number) data.get("pitch")).floatValue();
            return new SimpleRotation(yaw, pitch);
        } catch (Throwable e) { e.printStackTrace(); }
        return null;
    }

    @Override
    public @NotNull SimpleRotation clone() { try { return (SimpleRotation) super.clone(); } catch(CloneNotSupportedException e) { throw new Error(e); } }

}