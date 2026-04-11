package dev.geco.gholo.object.holo;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.object.simple.SimpleRotation;
import dev.geco.gholo.object.simple.SimpleSize;
import dev.geco.gholo.object.simple.SimpleVector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.logging.Level;

public class GHoloData implements Cloneable {

    public static final double DEFAULT_RANGE = 120d;
    public static final String DEFAULT_BACKGROUND_COLOR = "#000000";
    public static final byte DEFAULT_TEXT_OPACITY = 0;
    public static final boolean DEFAULT_HAS_TEXT_SHADOW = false;
    public static final String DEFAULT_TEXT_ALIGNMENT = "center";
    public static final String DEFAULT_BILLBOARD = "center";
    public static final boolean DEFAULT_CAN_SEE_THROUGH = false;
    public static final SimpleVector DEFAULT_SCALE = new SimpleVector(1f, 1f, 1f);
    public static final SimpleRotation DEFAULT_ROTATION = new SimpleRotation(null, null);
    public static final Byte DEFAULT_BRIGHTNESS = null;
    public static final String DEFAULT_PERMISSION = null;
    public static final SimpleSize DEFAULT_SIZE = new SimpleSize(1f, 1f);

    private double range = DEFAULT_RANGE;
    private String backgroundColor = DEFAULT_BACKGROUND_COLOR;
    private byte textOpacity = DEFAULT_TEXT_OPACITY;
    private boolean hasTextShadow = DEFAULT_HAS_TEXT_SHADOW;
    private String textAlignment = DEFAULT_TEXT_ALIGNMENT;
    private String billboard = DEFAULT_BILLBOARD;
    private boolean canSeeThrough = DEFAULT_CAN_SEE_THROUGH;
    private SimpleVector scale = new SimpleVector(1f, 1f, 1f);
    private SimpleRotation rotation = new SimpleRotation(null, null);
    private Byte brightness = DEFAULT_BRIGHTNESS;
    private String permission = DEFAULT_PERMISSION;
    private SimpleSize size = new SimpleSize(1f, 1f);

    public double getRange() { return range; }

    public @NotNull GHoloData setRange(double range) {
        this.range = range;
        return this;
    }

    public @Nullable String getBackgroundColor() { return backgroundColor; }

    public @NotNull GHoloData setBackgroundColor(@Nullable String backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    public byte getTextOpacity() { return textOpacity; }

    public @NotNull GHoloData setTextOpacity(byte textOpacity) {
        this.textOpacity = textOpacity;
        return this;
    }

    public boolean getTextShadow() { return hasTextShadow; }

    public @NotNull GHoloData setTextShadow(boolean hasTextShadow) {
        this.hasTextShadow = hasTextShadow;
        return this;
    }

    public @NotNull String getTextAlignment() { return textAlignment; }

    public @NotNull GHoloData setTextAlignment(@NotNull String textAlignment) {
        this.textAlignment = textAlignment;
        return this;
    }

    public @NotNull String getBillboard() { return billboard; }

    public @NotNull GHoloData setBillboard(@NotNull String billboard) {
        this.billboard = billboard;
        return this;
    }

    public boolean getSeeThrough() { return canSeeThrough; }

    public @NotNull GHoloData setSeeThrough(boolean canSeeThrough) {
        this.canSeeThrough = canSeeThrough;
        return this;
    }

    public @NotNull SimpleVector getScale() { return scale; }

    public @NotNull SimpleVector getRawScale() { return scale.clone(); }

    public @NotNull GHoloData setScale(@NotNull SimpleVector scale) {
        this.scale = scale;
        return this;
    }

    public @NotNull SimpleRotation getRotation() { return rotation.clone(); }

    public @NotNull SimpleRotation getRawRotation() { return rotation; }

    public @NotNull GHoloData setRotation(@NotNull SimpleRotation rotation) {
        this.rotation = rotation.clone();
        return this;
    }

    public @Nullable Byte getBrightness() { return brightness; }

    public @NotNull GHoloData setBrightness(@Nullable Byte brightness) {
        this.brightness = brightness;
        return this;
    }

    public @Nullable String getPermission() { return permission; }

    public @NotNull GHoloData setPermission(@Nullable String permission) {
        this.permission = permission;
        return this;
    }

    public @NotNull SimpleSize getSize() { return size.clone(); }

    public @NotNull SimpleSize getRawSize() { return size; }

    public @NotNull GHoloData setSize(@NotNull SimpleSize size) {
        this.size = size.clone();
        return this;
    }

    @Override
    public @NotNull String toString() {
        JsonObject data = new JsonObject();
        if(range != DEFAULT_RANGE) data.addProperty("range", range);
        if(!Objects.equals(backgroundColor, DEFAULT_BACKGROUND_COLOR)) data.addProperty("background_color", backgroundColor);
        if(textOpacity != DEFAULT_TEXT_OPACITY) data.addProperty("text_opacity", textOpacity);
        if(hasTextShadow != DEFAULT_HAS_TEXT_SHADOW) data.addProperty("text_shadow", hasTextShadow);
        if(!Objects.equals(textAlignment, DEFAULT_TEXT_ALIGNMENT)) data.addProperty("text_alignment", textAlignment);
        if(!Objects.equals(billboard, DEFAULT_BILLBOARD)) data.addProperty("billboard", billboard);
        if(canSeeThrough != DEFAULT_CAN_SEE_THROUGH) data.addProperty("see_through", canSeeThrough);
        if(scale.getX() != DEFAULT_SCALE.getX() || scale.getY() != DEFAULT_SCALE.getY() || scale.getZ() != DEFAULT_SCALE.getZ()) {
            JsonObject scaleData = new JsonObject();
            if(scale.getX() != DEFAULT_SCALE.getX()) scaleData.addProperty("x", scale.getX());
            if(scale.getY() != DEFAULT_SCALE.getY()) scaleData.addProperty("y", scale.getY());
            if(scale.getZ() != DEFAULT_SCALE.getZ()) scaleData.addProperty("z", scale.getZ());
            data.add("scale", scaleData);
        }
        if(!Objects.equals(rotation.getYaw(), DEFAULT_ROTATION.getYaw()) || !Objects.equals(rotation.getPitch(), DEFAULT_ROTATION.getPitch())) {
            JsonObject rotationData = new JsonObject();
            if(!Objects.equals(rotation.getYaw(), DEFAULT_ROTATION.getYaw())) rotationData.addProperty("yaw", rotation.getYaw());
            if(!Objects.equals(rotation.getPitch(), DEFAULT_ROTATION.getPitch())) rotationData.addProperty("pitch", rotation.getPitch());
            data.add("rotation", rotationData);
        }
        if(brightness != DEFAULT_BRIGHTNESS) data.addProperty("brightness", brightness);
        if(!Objects.equals(permission, DEFAULT_PERMISSION)) data.addProperty("permission", permission);
        if(size.getWidth() != DEFAULT_SIZE.getWidth() || size.getHeight() != DEFAULT_SIZE.getHeight()) {
            JsonObject sizeData = new JsonObject();
            if(size.getWidth() != DEFAULT_SIZE.getWidth()) sizeData.addProperty("width", size.getWidth());
            if(size.getHeight() != DEFAULT_SIZE.getHeight()) sizeData.addProperty("height", size.getHeight());
            data.add("size", sizeData);
        }
        return data.toString();
    }

    public @NotNull GHoloData loadString(@NotNull String string) {
        try {
            JsonObject data = (JsonObject) JsonParser.parseString(string);
            if(data.has("range") && !data.get("range").isJsonNull()) range = data.get("range").getAsDouble();
            if(data.has("background_color") && !data.get("background_color").isJsonNull()) backgroundColor = data.get("background_color").getAsString();
            if(data.has("text_opacity") && !data.get("text_opacity").isJsonNull()) textOpacity = data.get("text_opacity").getAsByte();
            if(data.has("text_shadow") && !data.get("text_shadow").isJsonNull()) hasTextShadow = data.get("text_shadow").getAsBoolean();
            if(data.has("text_alignment") && !data.get("text_alignment").isJsonNull()) textAlignment = data.get("text_alignment").getAsString();
            if(data.has("billboard") && !data.get("billboard").isJsonNull()) billboard = data.get("billboard").getAsString();
            if(data.has("see_through") && !data.get("see_through").isJsonNull()) canSeeThrough = data.get("see_through").getAsBoolean();
            if(data.has("scale") && !data.get("scale").isJsonNull()) {
                JsonObject scaleData = data.getAsJsonObject("scale");
                double x = scaleData.has("x") && !scaleData.get("x").isJsonNull() ? scaleData.get("x").getAsDouble() : DEFAULT_SCALE.getX();
                double y = scaleData.has("y") && !scaleData.get("y").isJsonNull() ? scaleData.get("y").getAsDouble() : DEFAULT_SCALE.getY();
                double z = scaleData.has("z") && !scaleData.get("z").isJsonNull() ? scaleData.get("z").getAsDouble() : DEFAULT_SCALE.getZ();
                scale = new SimpleVector(x, y, z);
            }
            if(data.has("rotation") && !data.get("rotation").isJsonNull()) {
                JsonObject rotationData = data.getAsJsonObject("rotation");
                Float yaw = rotationData.has("yaw") && !rotationData.get("yaw").isJsonNull() ? Float.valueOf(rotationData.get("yaw").getAsFloat()) : DEFAULT_ROTATION.getYaw();
                Float pitch = rotationData.has("pitch") && !rotationData.get("pitch").isJsonNull() ? Float.valueOf(rotationData.get("pitch").getAsFloat()) : DEFAULT_ROTATION.getYaw();
                rotation = new SimpleRotation(yaw, pitch);
            }
            if(data.has("brightness") && !data.get("brightness").isJsonNull()) brightness = data.get("brightness").getAsByte();
            if(data.has("permission") && !data.get("permission").isJsonNull()) permission = data.get("permission").getAsString();
            if(data.has("size") && !data.get("size").isJsonNull()) {
                JsonObject sizeData = data.getAsJsonObject("size");
                float width = sizeData.has("width") && !sizeData.get("width").isJsonNull() ? sizeData.get("width").getAsFloat() : DEFAULT_SIZE.getWidth();
                float height = sizeData.has("height") && !sizeData.get("height").isJsonNull() ? sizeData.get("height").getAsFloat() : DEFAULT_SIZE.getWidth();
                size = new SimpleSize(width, height);
            }
        } catch(Throwable e) { GHoloMain.getInstance().getLogger().log(Level.SEVERE, "Could not load holo data!", e); }
        return this;
    }

    public static @NotNull GHoloData fromString(@NotNull String string) { return new GHoloData().loadString(string); }

    @Override
    public @NotNull GHoloData clone() { try { return (GHoloData) super.clone(); } catch(CloneNotSupportedException e) { throw new Error(e); } }

}