package dev.geco.gholo.object.simple;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.geco.gholo.GHoloMain;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Level;

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
        JsonObject simpleRotation = new JsonObject();
        if(yaw != null) simpleRotation.addProperty("yaw", yaw);
        if(pitch != null) simpleRotation.addProperty("pitch", pitch);
        return simpleRotation.toString();
    }

    public static @Nullable SimpleRotation fromString(@NotNull String string) {
        try {
            JsonObject data = (JsonObject) JsonParser.parseString(string);
            Float yaw = data.has("yaw") && !data.get("yaw").isJsonNull() ? data.get("yaw").getAsFloat() : null;
            Float pitch = data.has("pitch") && !data.get("pitch").isJsonNull() ? data.get("pitch").getAsFloat() : null;
            return new SimpleRotation(yaw, pitch);
        } catch(Throwable e) { GHoloMain.getInstance().getLogger().log(Level.SEVERE, "Could not load rotation data!", e); }
        return null;
    }

    @Override
    public @NotNull SimpleRotation clone() { try { return (SimpleRotation) super.clone(); } catch(CloneNotSupportedException e) { throw new Error(e); } }

}