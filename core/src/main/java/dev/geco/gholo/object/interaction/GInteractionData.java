package dev.geco.gholo.object.interaction;

import dev.geco.gholo.object.simple.SimpleSize;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.Objects;

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
        JSONObject data = new JSONObject();
        if(!Objects.equals(permission, DEFAULT_PERMISSION)) data.put("permission", permission);
        if(size.getWidth() != DEFAULT_SIZE.getWidth() || size.getHeight() != DEFAULT_SIZE.getHeight()) {
            JSONObject sizeData = new JSONObject();
            sizeData.put("width", size.getWidth());
            sizeData.put("height", size.getHeight());
            data.put("size", sizeData);
        }
        return data.toJSONString();
    }

    public @NotNull GInteractionData loadString(@NotNull String string) {
        JSONParser parser = new JSONParser();
        try {
            JSONObject data = (JSONObject) parser.parse(string);
            if(data.get("permission") != null) permission = (String) data.get("permission");
            if(data.get("size") != null) {
                JSONObject sizeData = (JSONObject) data.get("size");
                float width = sizeData.get("width") != null ? ((Number) sizeData.get("width")).floatValue() : DEFAULT_SIZE.getWidth();
                float height = sizeData.get("height") != null ? ((Number) sizeData.get("height")).floatValue() : DEFAULT_SIZE.getWidth();
                size = new SimpleSize(width, height);
            }
        } catch(Throwable e) { e.printStackTrace(); }
        return this;
    }

    public static @NotNull GInteractionData fromString(@NotNull String string) { return new GInteractionData().loadString(string); }

    @Override
    public @NotNull GInteractionData clone() { try { return (GInteractionData) super.clone(); } catch(CloneNotSupportedException e) { throw new Error(e); } }

}