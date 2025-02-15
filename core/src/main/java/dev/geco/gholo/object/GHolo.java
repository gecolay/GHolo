package dev.geco.gholo.object;

import dev.geco.gholo.object.location.SimpleLocation;
import dev.geco.gholo.object.location.SimpleRotation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GHolo {

    private final UUID uuid;
    private String id;
    private SimpleLocation location;
    private SimpleRotation rotation = new SimpleRotation(0f, 0f);
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

    public @NotNull SimpleRotation getRotation() { return rotation.clone(); }

    public @NotNull SimpleRotation getRawRotation() { return rotation; }

    public @NotNull GHolo setRotation(@NotNull SimpleRotation rotation) {
        this.rotation = rotation.clone();
        return this;
    }

    public @NotNull List<GHoloRow> getRows() { return rows; }

    public @Nullable GHoloRow getRow(int position) { return rows.get(position); }

    public @NotNull GHolo addRow(@NotNull GHoloRow holoRow) {
        rows.add(holoRow);
        return this;
    }

    public @NotNull GHolo insertRow(@NotNull GHoloRow holoRow, int position) {
        rows.add(position, holoRow);
        return this;
    }

    public @NotNull GHolo removeRow(int position) {
        rows.remove(position);
        return this;
    }

    public @NotNull GHoloData getData() { return data.clone(); }

    public @NotNull GHoloData getRawData() { return data; }

    public @NotNull GHolo setData(@NotNull GHoloData data) {
        this.data = data.clone();
        return this;
    }

}