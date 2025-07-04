package dev.geco.gholo.object.holo;

import dev.geco.gholo.object.simple.SimpleLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GHolo {

    private final UUID uuid;
    private String id;
    private SimpleLocation location;
    private final List<GHoloRow> rows = new ArrayList<>();
    private GHoloData data = new GHoloData();

    public GHolo(@NotNull UUID uuid, @NotNull String id, @NotNull SimpleLocation location) {
        this.uuid = uuid;
        this.id = id;
        this.location = location.clone();
    }

    public @NotNull UUID getUuid() { return uuid; }

    public @NotNull String getId() { return id; }

    public @NotNull GHolo setId(@NotNull String id) {
        this.id = id;
        return this;
    }

    public @NotNull SimpleLocation getLocation() { return location.clone(); }

    public @NotNull SimpleLocation getRawLocation() { return location; }

    public @NotNull GHolo setLocation(@NotNull SimpleLocation location) {
        this.location = location.clone();
        return this;
    }

    public @NotNull List<GHoloRow> getRows() { return rows; }

    public @Nullable GHoloRow getRow(int position) { return (position >= 0 && position < rows.size()) ? rows.get(position) : null; }

    public @NotNull GHolo addRow(@NotNull GHoloRow holoRow) {
        rows.add(holoRow);
        return this;
    }

    public @NotNull GHolo insertRow(@NotNull GHoloRow holoRow, int position) {
        rows.add(position, holoRow);
        return this;
    }

    public @NotNull GHolo removeRow(int position) {
        if(position >= 0 && position < rows.size()) rows.remove(position);
        return this;
    }

    public @NotNull GHoloData getData() { return data.clone(); }

    public @NotNull GHoloData getRawData() { return data; }

    public @NotNull GHolo setData(@NotNull GHoloData data) {
        this.data = data.clone();
        return this;
    }

}