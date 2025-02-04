package dev.geco.gholo.object;

import org.bukkit.Location;

public class GHoloRow {

    private final GHolo holo;
    private String content;
    private Location position = new Location(null, 0, 0, 0);
    private GHoloData data = new GHoloData();
    private IGHoloRowEntity holoRowEntity;

    public GHoloRow(GHolo holo, String content) {
        this.holo = holo;
        this.content = content;
    }

    public GHolo getHolo() { return holo; }

    public int getRowId() { return holo.getRows().indexOf(this); }

    public String getContent() { return content; }

    public GHoloRow setContent(String content) {
        this.content = content;
        return this;
    }

    public Location getPosition() { return position.clone(); }

    public Location getRawPosition() { return position; }

    public GHoloRow setPosition(Location position) {
        this.position = position.clone();
        return this;
    }

    public GHoloData getData() { return data.clone(); }

    public GHoloData getRawData() { return data; }

    public GHoloRow setData(GHoloData data) {
        this.data = data.clone();
        return this;
    }

    public IGHoloRowEntity getHoloRowEntity() { return holoRowEntity; }

    public GHoloRow setHoloRowEntity(IGHoloRowEntity holoRowEntity) {
        this.holoRowEntity = holoRowEntity;
        return this;
    }

}