package dev.geco.gholo.object.holo;

import dev.geco.gholo.object.simple.SimpleRotation;
import dev.geco.gholo.object.simple.SimpleSize;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.Objects;

public class GHoloData implements Cloneable {

    public static final double DEFAULT_RANGE = 120d;
    public static final String DEFAULT_BACKGROUND_COLOR = "#000000";
    public static final byte DEFAULT_TEXT_OPACITY = 0;
    public static final boolean DEFAULT_HAS_TEXT_SHADOW = false;
    public static final String DEFAULT_TEXT_ALIGNMENT = "center";
    public static final String DEFAULT_BILLBOARD = "center";
    public static final boolean DEFAULT_CAN_SEE_THROUGH = false;
    public static final Vector3f DEFAULT_SCALE = new Vector3f(1f, 1f, 1f);
    public static final SimpleRotation DEFAULT_ROTATION = new SimpleRotation(null, null);
    public static final Byte DEFAULT_BRIGHTNESS = null;
    public static final String DEFAULT_PERMISSION = null;
    public static final SimpleSize DEFAULT_SIZE = new SimpleSize(1f, 1f);

    private Double range = DEFAULT_RANGE;
    private String backgroundColor = DEFAULT_BACKGROUND_COLOR;
    private Byte textOpacity = DEFAULT_TEXT_OPACITY;
    private Boolean hasTextShadow = DEFAULT_HAS_TEXT_SHADOW;
    private String textAlignment = DEFAULT_TEXT_ALIGNMENT;
    private String billboard = DEFAULT_BILLBOARD;
    private Boolean canSeeThrough = DEFAULT_CAN_SEE_THROUGH;
    private Vector3f scale = DEFAULT_SCALE;
    private SimpleRotation rotation = DEFAULT_ROTATION;
    private Byte brightness = DEFAULT_BRIGHTNESS;
    private String permission = DEFAULT_PERMISSION;
    private SimpleSize size = DEFAULT_SIZE;

    public Double getRange() { return range; }

    public GHoloData setRange(Double range) {
        this.range = range;
        return this;
    }

    public String getBackgroundColor() { return backgroundColor; }

    public GHoloData setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    public Byte getTextOpacity() { return textOpacity; }

    public GHoloData setTextOpacity(Byte textOpacity) {
        this.textOpacity = textOpacity;
        return this;
    }

    public Boolean getTextShadow() { return hasTextShadow; }

    public GHoloData setTextShadow(Boolean hasTextShadow) {
        this.hasTextShadow = hasTextShadow;
        return this;
    }

    public String getTextAlignment() { return textAlignment; }

    public GHoloData setTextAlignment(String textAlignment) {
        this.textAlignment = textAlignment;
        return this;
    }

    public String getBillboard() { return billboard; }

    public GHoloData setBillboard(String billboard) {
        this.billboard = billboard;
        return this;
    }

    public Boolean getSeeThrough() { return canSeeThrough; }

    public GHoloData setSeeThrough(Boolean canSeeThrough) {
        this.canSeeThrough = canSeeThrough;
        return this;
    }

    public Vector3f getScale() { return scale; }

    public Vector3f getRawScale() { try { return (Vector3f) scale.clone(); } catch(CloneNotSupportedException e) { throw new Error(e); } }

    public GHoloData setScale(Vector3f scale) {
        this.scale = scale;
        return this;
    }

    public SimpleRotation getRotation() { return rotation.clone(); }

    public SimpleRotation getRawRotation() { return rotation; }

    public GHoloData setRotation(SimpleRotation rotation) {
        this.rotation = rotation.clone();
        return this;
    }

    public Byte getBrightness() { return brightness; }

    public GHoloData setBrightness(Byte brightness) {
        this.brightness = brightness;
        return this;
    }

    public String getPermission() { return permission; }

    public GHoloData setPermission(String permission) {
        this.permission = permission;
        return this;
    }

    public SimpleSize getSize() { return size.clone(); }

    public SimpleSize getRawSize() { return size; }

    public GHoloData setSize(SimpleSize size) {
        this.size = size.clone();
        return this;
    }

    @Override
    public String toString() {
        JSONObject data = new JSONObject();
        if(range != DEFAULT_RANGE) data.put("range", range);
        if(!Objects.equals(backgroundColor, DEFAULT_BACKGROUND_COLOR)) data.put("background_color", backgroundColor);
        if(textOpacity != DEFAULT_TEXT_OPACITY) data.put("text_opacity", textOpacity);
        if(hasTextShadow != DEFAULT_HAS_TEXT_SHADOW) data.put("text_shadow", hasTextShadow);
        if(!Objects.equals(textAlignment, DEFAULT_TEXT_ALIGNMENT)) data.put("text_alignment", textAlignment);
        if(!Objects.equals(billboard, DEFAULT_BILLBOARD)) data.put("billboard", billboard);
        if(canSeeThrough != DEFAULT_CAN_SEE_THROUGH) data.put("see_through", canSeeThrough);
        if(!Objects.equals(scale, DEFAULT_SCALE)) {
            JSONObject scaleData = new JSONObject();
            scaleData.put("x", scale.x);
            scaleData.put("y", scale.y);
            scaleData.put("z", scale.z);
            data.put("scale", scaleData);
        }
        if(!Objects.equals(rotation, DEFAULT_ROTATION)) {
            JSONObject rotationData = new JSONObject();
            rotationData.put("yaw", rotation.getYaw());
            rotationData.put("pitch", rotation.getPitch());
            data.put("rotation", rotationData);
        }
        if(brightness != DEFAULT_BRIGHTNESS) data.put("brightness", brightness);
        if(!Objects.equals(permission, DEFAULT_PERMISSION)) data.put("permission", permission);
        if(!Objects.equals(size, DEFAULT_SIZE)) {
            JSONObject sizeData = new JSONObject();
            sizeData.put("width", size.getWidth());
            sizeData.put("height", size.getHeight());
            data.put("size", sizeData);
        }
        return data.toJSONString();
    }

    public @NotNull GHoloData loadString(@NotNull String string) {
        JSONParser parser = new JSONParser();
        try {
            JSONObject data = (JSONObject) parser.parse(string);
            if(data.containsKey("range")) range = ((Number) data.get("range")).doubleValue();
            if(data.containsKey("background_color")) backgroundColor = (String) data.get("background_color");
            if(data.containsKey("text_opacity")) textOpacity = ((Number) data.get("text_opacity")).byteValue();
            if(data.containsKey("text_shadow")) hasTextShadow = (Boolean) data.get("text_shadow");
            if(data.containsKey("text_alignment")) textAlignment = (String) data.get("text_alignment");
            if(data.containsKey("billboard")) billboard = (String) data.get("billboard");
            if(data.containsKey("see_through")) canSeeThrough = (Boolean) data.get("see_through");
            if(data.containsKey("scale")) {
                JSONObject scaleData = (JSONObject) data.get("scale");
                float x = ((Number) scaleData.get("x")).floatValue();
                float y = ((Number) scaleData.get("y")).floatValue();
                float z = ((Number) scaleData.get("z")).floatValue();
                scale = new Vector3f(x, y, z);
            }
            if(data.containsKey("rotation")) {
                JSONObject rotationData = (JSONObject) data.get("rotation");
                float yaw = ((Number) rotationData.get("yaw")).floatValue();
                float pitch = ((Number) rotationData.get("pitch")).floatValue();
                rotation = new SimpleRotation(yaw, pitch);
            }
            if(data.containsKey("brightness")) brightness = ((Number) data.get("brightness")).byteValue();
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

    public static @NotNull GHoloData fromString(@NotNull String string) { return new GHoloData().loadString(string); }

    @Override
    public @NotNull GHoloData clone() { try { return (GHoloData) super.clone(); } catch(CloneNotSupportedException e) { throw new Error(e); } }

}