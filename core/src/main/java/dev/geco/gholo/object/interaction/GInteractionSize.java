package dev.geco.gholo.object.interaction;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class GInteractionSize implements Cloneable {

    private float width;
    private float height;

    public GInteractionSize(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public float getWidth() { return width; }

    public GInteractionSize setWidth(float width) {
        this.width = width;
        return this;
    }

    public float getHeight() { return height; }

    public GInteractionSize setHeight(float height) {
        this.height = height;
        return this;
    }

    @Override
    public String toString() {
        JSONObject interactionSize = new JSONObject();
        interactionSize.put("width", width);
        interactionSize.put("height", height);
        return interactionSize.toJSONString();
    }

    public static @Nullable GInteractionSize fromString(@NotNull String string) {
        JSONParser parser = new JSONParser();
        try {
            JSONObject data = (JSONObject) parser.parse(string);
            float width = ((Number) data.get("width")).floatValue();
            float height = ((Number) data.get("height")).floatValue();
            return new GInteractionSize(width, height);
        } catch(Throwable e) { e.printStackTrace(); }
        return null;
    }

    @Override
    public @NotNull GInteractionSize clone() { try { return (GInteractionSize) super.clone(); } catch(CloneNotSupportedException e) { throw new Error(e); } }

}