package dev.geco.gholo.objects;

import org.bukkit.*;

public class GHoloRow {

    private final GHolo holo;
    private String content;
    private Location position = new Location(null, 0, 0, 0);
    private GHoloData data = new GHoloData();
    private IGHoloRowEntity holoRowEntity;

    public GHoloRow(GHolo Holo, String Content) {
        holo = Holo;
        content = Content;
    }

    public GHolo getHolo() { return holo; }

    public int getRow() { return holo.getRows().indexOf(this); }

    public String getContent() { return content; }

    public void setContent(String Content) { content = Content; }

    public Location getPosition() { return position.clone(); }

    public void setPosition(Location Position) { position = Position.clone(); }

    public GHoloData getData() { return data.clone(); }

    public void setData(GHoloData Data) { data = Data.clone(); }

    public IGHoloRowEntity getHoloRowEntity() { return holoRowEntity; }

    public void setHoloRowEntity(IGHoloRowEntity HoloRowEntity) { holoRowEntity = HoloRowEntity; }

}