package dev.geco.gholo.objects;

import java.util.*;

import org.joml.*;

public class GHoloData implements Cloneable {

    public static final double DEFAULT_RANGE = 120d;
    public static final String DEFAULT_BACKGROUND_COLOR = "#000000";
    public static final byte DEFAULT_TEXT_OPACITY = 0;
    public static final boolean DEFAULT_TEXT_SHADOW = false;
    public static final String DEFAULT_TEXT_ALIGNMENT = "center";
    public static final String DEFAULT_BILLBOARD = "center";
    public static final boolean DEFAULT_SEE_THROUGH = false;
    public static final Vector3f DEFAULT_SCALE = new Vector3f(1f, 1f, 1f);
    public static final Byte DEFAULT_BRIGHTNESS = null;
    public static final String DEFAULT_PERMISSION = null;

    private Double range;
    private String backgroundColor;
    private Byte textOpacity;
    private Boolean textShadow;
    private String textAlignment;
    private String billboard;
    private Boolean seeThrough;
    private Vector3f scale;
    private Byte brightness;
    private String permission;

    @Override
    public String toString() {
        HashMap<String, Object> stringMap = new HashMap<>();
        if(range != null) stringMap.put("range", range);
        if(backgroundColor != null) stringMap.put("background_color", backgroundColor);
        if(textOpacity != null) stringMap.put("text_opacity", textOpacity);
        if(textShadow != null) stringMap.put("text_shadow", textShadow);
        if(textAlignment != null) stringMap.put("text_alignment", textAlignment);
        if(billboard != null) stringMap.put("billboard", billboard);
        if(seeThrough != null) stringMap.put("see_through", seeThrough);
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

    public void loadString(String DataString) {
        for(String dataPart : DataString.split("§§")) {
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
                        textShadow = Boolean.parseBoolean(dataSplit[1]);
                        break;
                    case "text_alignment":
                        textAlignment = dataSplit[1];
                        break;
                    case "billboard":
                        billboard = dataSplit[1];
                        break;
                    case "see_through":
                        seeThrough = Boolean.parseBoolean(dataSplit[1]);
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

    public void setRange(Double Range) { range = Range; }

    public String getBackgroundColor() { return backgroundColor; }

    public void setBackgroundColor(String BackgroundColor) { backgroundColor = BackgroundColor; }

    public Byte getTextOpacity() { return textOpacity; }

    public void setTextOpacity(Byte TextOpacity) { textOpacity = TextOpacity; }

    public Boolean getTextShadow() { return textShadow; }

    public void setTextShadow(Boolean TextShadow) { textShadow = TextShadow; }

    public String getTextAlignment() { return textAlignment; }

    public void setTextAlignment(String TextAlignment) { textAlignment = TextAlignment; }

    public String getBillboard() { return billboard; }

    public void setBillboard(String Billboard) { billboard = Billboard; }

    public Boolean getSeeThrough() { return seeThrough; }

    public void setSeeThrough(Boolean SeeThrough) { seeThrough = SeeThrough; }

    public Vector3f getScale() { return scale; }

    public void setScale(Vector3f Scale) { scale = Scale; }

    public Byte getBrightness() { return brightness; }

    public void setBrightness(Byte Brightness) { brightness = Brightness; }

    public String getPermission() { return permission; }

    public void setPermission(String Permission) { permission = Permission; }

    @Override
    public GHoloData clone() { try { return (GHoloData) super.clone(); } catch (CloneNotSupportedException e) { throw new Error(e); } }

}