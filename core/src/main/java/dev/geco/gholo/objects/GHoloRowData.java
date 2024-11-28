package dev.geco.gholo.objects;

import java.util.*;

public class GHoloRowData {

    private final boolean nullDefaultValues;
    private final double defaultRange = 120d;
    private final String defaultBackgroundColor = "#000000";
    private final byte defaultTextOpacity = 0;
    private final boolean defaultTextShadow = false;
    private final String defaultBillboard = "center";
    private final boolean defaultSeeThrough = false;
    private final float defaultSize = 1f;

    private Double range;
    private String backgroundColor;
    private Byte textOpacity;
    private Boolean textShadow;
    private String billboard;
    private Boolean seeThrough;
    private Float size;

    public GHoloRowData(boolean NullDefaultValues) {
        nullDefaultValues = NullDefaultValues;
        if(nullDefaultValues) return;
        range = defaultRange;
        backgroundColor = defaultBackgroundColor;
        textOpacity = defaultTextOpacity;
        textShadow = defaultTextShadow;
        billboard = defaultBillboard;
        seeThrough = defaultSeeThrough;
        size = defaultSize;
    }

    public Double getRange() { return range; }

    public double getDefaultRange() { return defaultRange; }

    public void setRange(Double Range) { range = nullDefaultValues && Range == defaultRange ? null : Range; }

    public String getBackgroundColor() { return backgroundColor; }

    public String getDefaultBackgroundColor() { return defaultBackgroundColor; }

    public void setBackgroundColor(String BackgroundColor) { backgroundColor = nullDefaultValues && defaultBackgroundColor.equalsIgnoreCase(BackgroundColor) ? null : BackgroundColor; }

    public Byte getTextOpacity() { return textOpacity; }

    public byte getDefaultTextOpacity() { return defaultTextOpacity; }

    public void setTextOpacity(Byte TextOpacity) { textOpacity = nullDefaultValues && TextOpacity == defaultTextOpacity ? null : TextOpacity; }

    public Boolean getTextShadow() { return textShadow; }

    public boolean getDefaultTextShadow() { return defaultTextShadow; }

    public void setTextShadow(Boolean TextShadow) { textShadow = nullDefaultValues && TextShadow == defaultTextShadow ? null : TextShadow; }

    public String getBillboard() { return billboard; }

    public String getDefaultBillboard() { return defaultBillboard; }

    public void setBillboard(String Billboard) { billboard = nullDefaultValues && defaultBillboard.equalsIgnoreCase(Billboard) ? null : Billboard; }

    public Boolean getSeeThrough() { return seeThrough; }

    public boolean getDefaultSeeThrough() { return defaultSeeThrough; }

    public void setSeeThrough(Boolean SeeThrough) { seeThrough = nullDefaultValues && SeeThrough == defaultSeeThrough ? null : SeeThrough; }

    public Float getSize() { return size; }

    public float getDefaultSize() { return defaultSize; }

    public void setSize(Float Size) { size = nullDefaultValues && Size == defaultSize ? null : Size; }

    public HashMap<String, String> getData() {
        HashMap<String, String> data = new HashMap<>();
        data.put("range", range == defaultRange ? null : range.toString());
        data.put("background_color", defaultBackgroundColor.equalsIgnoreCase(backgroundColor) ? null : backgroundColor);
        data.put("text_opacity", textOpacity == defaultTextOpacity ? null : textOpacity.toString());
        data.put("text_shadow", textShadow == defaultTextShadow ? null : textShadow.toString());
        data.put("billboard", defaultBillboard.equalsIgnoreCase(billboard) ? null : billboard);
        data.put("see_through", seeThrough == defaultSeeThrough ? null : seeThrough.toString());
        data.put("size", size == defaultSize ? null : size.toString());
        return data;
    }

}