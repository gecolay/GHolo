package dev.geco.gholo.object.interaction;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.object.simple.SimpleSize;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.logging.Level;

public class GInteractionData implements Cloneable {

    public static final String DEFAULT_PERMISSION = null;
    public static final SimpleSize DEFAULT_SIZE = new SimpleSize(1f, 1f);

    private String permission = DEFAULT_PERMISSION;
    private SimpleSize size = new SimpleSize(1f, 1f);

    public @Nullable String getPermission() { return permission; }

    public @NotNull GInteractionData setPermission(@Nullable String permission) {
        this.permission = permission;
        return this;
    }

    public @NotNull SimpleSize getSize() { return size.clone(); }

    public @NotNull SimpleSize getRawSize() { return size; }

    public @NotNull GInteractionData setSize(@NotNull SimpleSize size) {
        this.size = size.clone();
        return this;
    }

    @Override
    public @NotNull String toString() {
        JsonObject data = new JsonObject();
        if(!Objects.equals(permission, DEFAULT_PERMISSION)) data.addProperty("permission", permission);
        if(size.getWidth() != DEFAULT_SIZE.getWidth() || size.getHeight() != DEFAULT_SIZE.getHeight()) {
            JsonObject sizeData = new JsonObject();
            sizeData.addProperty("width", size.getWidth());
            sizeData.addProperty("height", size.getHeight());
            data.add("size", sizeData);
        }
        return data.toString();
    }

    public @NotNull GInteractionData loadString(@NotNull String string) {
        try {
            JsonObject data = (JsonObject) JsonParser.parseString(string);
            if(data.has("permission") && !data.get("permission").isJsonNull()) permission = data.get("permission").getAsString();
            if(data.has("size") && !data.get("size").isJsonNull()) {
                JsonObject sizeData = data.getAsJsonObject("size");
                float width = sizeData.has("width") && !sizeData.get("width").isJsonNull() ? sizeData.get("width").getAsFloat() : DEFAULT_SIZE.getWidth();
                float height = sizeData.has("height") && !sizeData.get("height").isJsonNull() ? sizeData.get("height").getAsFloat() : DEFAULT_SIZE.getWidth();
                size = new SimpleSize(width, height);
            }
        } catch(Throwable e) { GHoloMain.getInstance().getLogger().log(Level.SEVERE, "Could not load interaction data!", e); }
        return this;
    }

    public static @NotNull GInteractionData fromString(@NotNull String string) { return new GInteractionData().loadString(string); }

    @Override
    public @NotNull GInteractionData clone() { try { return (GInteractionData) super.clone(); } catch(CloneNotSupportedException e) { throw new Error(e); } }

}