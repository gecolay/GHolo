package dev.geco.gholo.objects;

import org.bukkit.util.*;

public class GHoloRow {

    private final GHolo holo;
    private String content;
    private Vector offsets = new Vector();
    private float locationYaw = 0;
    private float locationPitch = 0;
    private IGHoloRowEntity holoRowEntity;

    public GHoloRow(GHolo Holo, String Content) {
        holo = Holo;
        content = Content;
    }

    public GHolo getHolo() { return holo; }

    public int getRow() { return holo.getRows().indexOf(this); }

    public String getContent() { return content; }

    public void setContent(String Content) { content = Content; }

    public Vector getOffsets() { return offsets.clone(); }

    public void setOffsets(Vector Offsets) { offsets = Offsets.clone(); }

    public float getLocationYaw() { return locationYaw; }

    public void setLocationYaw(float LocationYaw) { locationYaw = LocationYaw; }

    public float getLocationPitch() { return locationPitch; }

    public void setLocationPitch(float LocationPitch) { locationPitch = LocationPitch; }

    public IGHoloRowEntity getHoloRowEntity() { return holoRowEntity; }

    public void setHoloRowEntity(IGHoloRowEntity HoloRowEntity) { holoRowEntity = HoloRowEntity; }

}