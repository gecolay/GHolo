package dev.geco.gholo.object.interaction;

import dev.geco.gholo.object.simple.SimpleSize;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.Objects;

public class GInteractionData implements Cloneable {

    public static final String DEFAULT_PERMISSION = null;
    public static final SimpleSize DEFAULT_SIZE = new SimpleSize(1f, 1f);

    private String permission = DEFAULT_PERMISSION;
    private SimpleSize size = DEFAULT_SIZE;

    public String getPermission() { return permission; }

    public GInteractionData setPermission(String permission) {
        this.permission = permission;
        return this;
    }

    public SimpleSize getSize() { return size.clone(); }

    public SimpleSize getRawSize() { return size; }

    public GInteractionData setSize(SimpleSize size) {
        this.size = size.clone();
        return this;
    }

    @Override
    public String toString() {
        JSONObject data = new JSONObject();
        if(!Objects.equals(permission, DEFAULT_PERMISSION)) data.put("permission", permission);
        if(!Objects.equals(size, DEFAULT_SIZE)) {
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
            if(data.containsKey("permission")) permission = (String) data.get("permission");
            if(data.containsKey("size")) {
                JSONObject sizeData = (JSONObject) data.get("size");
                float width = ((Number) sizeData.get("width")).floatValue();
                float height = ((Number) sizeData.get("height")).floatValue();
                size = new SimpleSize(width, height);
            }
        } catch(Throwable e) { e.printStackTrace(); }
        return this;
    }

    public static @NotNull GInteractionData fromString(@NotNull String string) { return new GInteractionData().loadString(string); }

    @Override
    public @NotNull GInteractionData clone() { try { return (GInteractionData) super.clone(); } catch(CloneNotSupportedException e) { throw new Error(e); } }

}