package dev.geco.gholo.object.simple;

import dev.geco.gholo.GHoloMain;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.logging.Level;

public class SimpleSize implements Cloneable {

    private float width;
    private float height;

    public SimpleSize(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public float getWidth() { return width; }

    public @NotNull SimpleSize setWidth(float width) {
        this.width = width;
        return this;
    }

    public float getHeight() { return height; }

    public @NotNull SimpleSize setHeight(float height) {
        this.height = height;
        return this;
    }

    @Override
    public @NotNull String toString() {
        JSONObject simpleSize = new JSONObject();
        simpleSize.put("width", width);
        simpleSize.put("height", height);
        return simpleSize.toJSONString();
    }

    public static @Nullable SimpleSize fromString(@NotNull String string) {
        JSONParser parser = new JSONParser();
        try {
            JSONObject data = (JSONObject) parser.parse(string);
            float width = ((Number) data.get("width")).floatValue();
            float height = ((Number) data.get("height")).floatValue();
            return new SimpleSize(width, height);
        } catch(Throwable e) { GHoloMain.getInstance().getLogger().log(Level.SEVERE, "Could not load size data!", e); }
        return null;
    }

    @Override
    public @NotNull SimpleSize clone() { try { return (SimpleSize) super.clone(); } catch(CloneNotSupportedException e) { throw new Error(e); } }

}