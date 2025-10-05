package dev.geco.gholo.object.holo;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.object.simple.SimpleRotation;
import dev.geco.gholo.object.simple.SimpleSize;
import dev.geco.gholo.object.simple.SimpleVector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

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
        JSONObject data = new JSONObject();
        if(range != DEFAULT_RANGE) data.put("range", range);
        if(!Objects.equals(backgroundColor, DEFAULT_BACKGROUND_COLOR)) data.put("background_color", backgroundColor);
        if(textOpacity != DEFAULT_TEXT_OPACITY) data.put("text_opacity", textOpacity);
        if(hasTextShadow != DEFAULT_HAS_TEXT_SHADOW) data.put("text_shadow", hasTextShadow);
        if(!Objects.equals(textAlignment, DEFAULT_TEXT_ALIGNMENT)) data.put("text_alignment", textAlignment);
        if(!Objects.equals(billboard, DEFAULT_BILLBOARD)) data.put("billboard", billboard);
        if(canSeeThrough != DEFAULT_CAN_SEE_THROUGH) data.put("see_through", canSeeThrough);
        if(scale.getX() != DEFAULT_SCALE.getX() || scale.getY() != DEFAULT_SCALE.getY() || scale.getZ() != DEFAULT_SCALE.getZ()) {
            JSONObject scaleData = new JSONObject();
            if(scale.getX() != DEFAULT_SCALE.getX()) scaleData.put("x", scale.getX());
            if(scale.getY() != DEFAULT_SCALE.getY()) scaleData.put("y", scale.getY());
            if(scale.getZ() != DEFAULT_SCALE.getZ()) scaleData.put("z", scale.getZ());
            data.put("scale", scaleData);
        }
        if(!Objects.equals(rotation.getYaw(), DEFAULT_ROTATION.getYaw()) || !Objects.equals(rotation.getPitch(), DEFAULT_ROTATION.getPitch())) {
            JSONObject rotationData = new JSONObject();
            if(!Objects.equals(rotation.getYaw(), DEFAULT_ROTATION.getYaw())) rotationData.put("yaw", rotation.getYaw());
            if(!Objects.equals(rotation.getPitch(), DEFAULT_ROTATION.getPitch())) rotationData.put("pitch", rotation.getPitch());
            data.put("rotation", rotationData);
        }
        if(brightness != DEFAULT_BRIGHTNESS) data.put("brightness", brightness);
        if(!Objects.equals(permission, DEFAULT_PERMISSION)) data.put("permission", permission);
        if(size.getWidth() != DEFAULT_SIZE.getWidth() || size.getHeight() != DEFAULT_SIZE.getHeight()) {
            JSONObject sizeData = new JSONObject();
            if(size.getWidth() != DEFAULT_SIZE.getWidth()) sizeData.put("width", size.getWidth());
            if(size.getHeight() != DEFAULT_SIZE.getHeight()) sizeData.put("height", size.getHeight());
            data.put("size", sizeData);
        }
        return data.toJSONString();
    }

    public @NotNull GHoloData loadString(@NotNull String string) {
        JSONParser parser = new JSONParser();
        try {
            JSONObject data = (JSONObject) parser.parse(string);
            if(data.get("range") != null) range = ((Number) data.get("range")).doubleValue();
            if(data.get("background_color") != null) backgroundColor = (String) data.get("background_color");
            if(data.get("text_opacity") != null) textOpacity = ((Number) data.get("text_opacity")).byteValue();
            if(data.get("text_shadow") != null) hasTextShadow = (Boolean) data.get("text_shadow");
            if(data.get("text_alignment") != null) textAlignment = (String) data.get("text_alignment");
            if(data.get("billboard") != null) billboard = (String) data.get("billboard");
            if(data.get("see_through") != null) canSeeThrough = (Boolean) data.get("see_through");
            if(data.get("scale") != null) {
                JSONObject scaleData = (JSONObject) data.get("scale");
                double x = scaleData.get("x") != null ? ((Number) scaleData.get("x")).doubleValue() : DEFAULT_SCALE.getX();
                double y = scaleData.get("y") != null ? ((Number) scaleData.get("y")).doubleValue() : DEFAULT_SCALE.getY();
                double z = scaleData.get("z") != null ? ((Number) scaleData.get("z")).doubleValue() : DEFAULT_SCALE.getZ();
                scale = new SimpleVector(x, y, z);
            }
            if(data.get("rotation") != null) {
                JSONObject rotationData = (JSONObject) data.get("rotation");
                Float yaw = rotationData.get("yaw") != null ? Float.valueOf(((Number) rotationData.get("yaw")).floatValue()) : DEFAULT_ROTATION.getYaw();
                Float pitch = rotationData.get("pitch") != null ? Float.valueOf(((Number) rotationData.get("pitch")).floatValue()) : DEFAULT_ROTATION.getPitch();
                rotation = new SimpleRotation(yaw, pitch);
            }
            if(data.get("brightness") != null) brightness = ((Number) data.get("brightness")).byteValue();
            if(data.get("permission") != null) permission = (String) data.get("permission");
            if(data.get("size") != null) {
                JSONObject sizeData = (JSONObject) data.get("size");
                float width = sizeData.get("width") != null ? ((Number) sizeData.get("width")).floatValue() : DEFAULT_SIZE.getWidth();
                float height = sizeData.get("height") != null ? ((Number) sizeData.get("height")).floatValue() : DEFAULT_SIZE.getWidth();
                size = new SimpleSize(width, height);
            }
        } catch(Throwable e) { GHoloMain.getInstance().getLogger().log(Level.SEVERE, "Could not load holo data!", e); }
        return this;
    }

    public static @NotNull GHoloData fromString(@NotNull String string) { return new GHoloData().loadString(string); }

    @Override
    public @NotNull GHoloData clone() { try { return (GHoloData) super.clone(); } catch(CloneNotSupportedException e) { throw new Error(e); } }

}