package dev.geco.gholo.object.simple;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.geco.gholo.GHoloMain;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
        JsonObject simpleSize = new JsonObject();
        simpleSize.addProperty("width", width);
        simpleSize.addProperty("height", height);
        return simpleSize.toString();
    }

    public static @Nullable SimpleSize fromString(@NotNull String string) {
        try {
            JsonObject data = (JsonObject) JsonParser.parseString(string);
            float width = data.get("width").getAsFloat();
            float height = data.get("height").getAsFloat();
            return new SimpleSize(width, height);
        } catch(Throwable e) { GHoloMain.getInstance().getLogger().log(Level.SEVERE, "Could not load size data!", e); }
        return null;
    }

    @Override
    public @NotNull SimpleSize clone() { try { return (SimpleSize) super.clone(); } catch(CloneNotSupportedException e) { throw new Error(e); } }

}