package dev.geco.gholo.object.simple;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class SimpleRotation implements Cloneable {

    private Float yaw;
    private Float pitch;

    public SimpleRotation(@Nullable Float yaw, @Nullable Float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public @Nullable Float getYaw() { return yaw; }

    public @NotNull SimpleRotation setYaw(@Nullable Float yaw) {
        this.yaw = yaw;
        return this;
    }

    public @Nullable Float getPitch() { return pitch; }

    public @NotNull SimpleRotation setPitch(@Nullable Float pitch) {
        this.pitch = pitch;
        return this;
    }

    @Override
    public @NotNull String toString() {
        JSONObject simpleRotation = new JSONObject();
        if(yaw != null) simpleRotation.put("yaw", yaw);
        if(pitch != null) simpleRotation.put("pitch", pitch);
        return simpleRotation.toJSONString();
    }

    public static @Nullable SimpleRotation fromString(@NotNull String string) {
        JSONParser parser = new JSONParser();
        try {
            JSONObject data = (JSONObject) parser.parse(string);
            Float yaw = data.get("yaw") != null ? ((Number) data.get("yaw")).floatValue() : null;
            Float pitch = data.get("pitch") != null ? ((Number) data.get("pitch")).floatValue() : null;
            return new SimpleRotation(yaw, pitch);
        } catch(Throwable ignored) { }
        return null;
    }

    @Override
    public @NotNull SimpleRotation clone() { try { return (SimpleRotation) super.clone(); } catch(CloneNotSupportedException e) { throw new Error(e); } }

}