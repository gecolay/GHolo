package dev.geco.gholo.object;

import org.joml.Vector3f;

import java.util.HashMap;

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

    @Override
    public String toString() {
        HashMap<String, Object> stringMap = new HashMap<>();
        if(range != null) stringMap.put("range", range);
        if(backgroundColor != null) stringMap.put("background_color", backgroundColor);
        if(textOpacity != null) stringMap.put("text_opacity", textOpacity);
        if(hasTextShadow != null) stringMap.put("text_shadow", hasTextShadow);
        if(textAlignment != null) stringMap.put("text_alignment", textAlignment);
        if(billboard != null) stringMap.put("billboard", billboard);
        if(canSeeThrough != null) stringMap.put("see_through", canSeeThrough);
        if(scale != null) stringMap.put("scale", scale.x + "," + scale.y + "," + scale.z);
        if(brightness != null) stringMap.put("brightness", brightness);
        if(permission != null) stringMap.put("permission", permission);
        StringBuilder dataString = new StringBuilder();
        for(HashMap.Entry<String, Object> entry : stringMap.entrySet()) {
            if(!dataString.isEmpty()) dataString.append("§§");
            dataString.append(entry.getKey()).append("§").append(entry.getValue());
        }
        return dataString.toString();
    }

    public void loadString(String string) {
        for(String dataPart : string.split("§§")) {
            try {
                String[] dataSplit = dataPart.split("§");
                switch (dataSplit[0]) {
                    case "range":
                        range = Double.parseDouble(dataSplit[1]);
                        break;
                    case "background_color":
                        backgroundColor = dataSplit[1];
                        break;
                    case "text_opacity":
                        textOpacity = Byte.parseByte(dataSplit[1]);
                        break;
                    case "text_shadow":
                        hasTextShadow = Boolean.parseBoolean(dataSplit[1]);
                        break;
                    case "text_alignment":
                        textAlignment = dataSplit[1];
                        break;
                    case "billboard":
                        billboard = dataSplit[1];
                        break;
                    case "see_through":
                        canSeeThrough = Boolean.parseBoolean(dataSplit[1]);
                        break;
                    case "scale":
                        String[] scaleSplit = dataSplit[1].split(",");
                        scale = new Vector3f(Float.parseFloat(scaleSplit[0]), Float.parseFloat(scaleSplit[1]), Float.parseFloat(scaleSplit[2]));
                        break;
                    case "brightness":
                        brightness = Byte.parseByte(dataSplit[1]);
                        break;
                    case "permission":
                        permission = dataSplit[1];
                        break;
                }
            } catch (Throwable e) { e.printStackTrace(); }
        }
    }

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
    public GHoloData clone() { try { return (GHoloData) super.clone(); } catch (CloneNotSupportedException e) { throw new Error(e); } }

}