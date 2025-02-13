package dev.geco.gholo.object;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class GHoloRow {

    private final GHolo holo;
    private String content;
    private Location position = new Location(null, 0, 0, 0);
    private GHoloData data = new GHoloData();
    private IGHoloRowEntity holoRowEntity = null;

    public GHoloRow(@NotNull GHolo holo, @NotNull String content) {
        this.holo = holo;
        this.content = content;
    }

    public @NotNull GHolo getHolo() { return holo; }

    public int getRowId() { return holo.getRows().indexOf(this); }

    public @NotNull String getContent() { return content; }

    public @NotNull GHoloRow setContent(@NotNull String content) {
        this.content = content;
        return this;
    }

    public @NotNull Location getPosition() { return position.clone(); }

    public @NotNull Location getRawPosition() { return position; }

    public @NotNull GHoloRow setPosition(@NotNull Location position) {
        this.position = position.clone();
        return this;
    }

    public @NotNull GHoloData getData() { return data.clone(); }

    public @NotNull GHoloData getRawData() { return data; }

    public @NotNull GHoloRow setData(@NotNull GHoloData data) {
        this.data = data.clone();
        return this;
    }

    public @Nullable IGHoloRowEntity getHoloRowEntity() { return holoRowEntity; }

    public @NotNull GHoloRow setHoloRowEntity(@Nullable IGHoloRowEntity holoRowEntity) {
        this.holoRowEntity = holoRowEntity;
        return this;
    }

}