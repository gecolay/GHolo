package dev.geco.gholo.object;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class GHoloData implements Cloneable {

    public static final double DEFAULT_RANGE = 120d;
    public static final String DEFAULT_BACKGROUND_COLOR = "#000000";
    public static final byte DEFAULT_TEXT_OPACITY = 0;
    public static final boolean DEFAULT_HAS_TEXT_SHADOW = false;
    public static final String DEFAULT_TEXT_ALIGNMENT = "center";
    public static final String DEFAULT_BILLBOARD = "center";
    public static final boolean DEFAULT_CAN_SEE_THROUGH = false;
    public static final Vector3f DEFAULT_SCALE = new Vector3f(1f, 1f, 1f);
    public static final Byte DEFAULT_BRIGHTNESS = null;
    public static final String DEFAULT_PERMISSION = null;

    private Double range;
    private String backgroundColor;
    private Byte textOpacity;
    private Boolean hasTextShadow;
    private String textAlignment;
    private String billboard;
    private Boolean canSeeThrough;
    private Vector3f scale;
    private Byte brightness;
    private String permission;

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

    public GHoloData setScale(Vector3f scale) {
        this.scale = scale;
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

    @Override
    public String toString() {
        JSONObject data = new JSONObject();
        if(range != null) data.put("range", range);
        if(backgroundColor != null) data.put("background_color", backgroundColor);
        if(textOpacity != null) data.put("text_opacity", textOpacity);
        if(hasTextShadow != null) data.put("text_shadow", hasTextShadow);
        if(textAlignment != null) data.put("text_alignment", textAlignment);
        if(billboard != null) data.put("billboard", billboard);
        if(canSeeThrough != null) data.put("see_through", canSeeThrough);
        if(scale != null) {
            JSONObject scaleData = new JSONObject();
            scaleData.put("x", scale.x);
            scaleData.put("y", scale.y);
            scaleData.put("z", scale.z);
            data.put("scale", scaleData);
        }
        if(brightness != null) data.put("brightness", brightness);
        if(permission != null) data.put("permission", permission);
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
            if(data.containsKey("brightness")) brightness = ((Number) data.get("brightness")).byteValue();
            if(data.containsKey("permission")) permission = (String) data.get("permission");
        } catch (Throwable e) { e.printStackTrace(); }
        return this;
    }

    public static @NotNull GHoloData fromString(@NotNull String string) { return new GHoloData().loadString(string); }

    @Override
    public @NotNull GHoloData clone() { try { return (GHoloData) super.clone(); } catch(CloneNotSupportedException e) { throw new Error(e); } }

}