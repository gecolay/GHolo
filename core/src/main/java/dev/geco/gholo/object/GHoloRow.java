package dev.geco.gholo.object;

import dev.geco.gholo.object.location.SimpleOffset;
import dev.geco.gholo.object.location.SimpleRotation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GHoloRow {

    private final GHolo holo;
    private String content;
    private SimpleOffset offset = new SimpleOffset(0, 0, 0);
    private SimpleRotation rotation = new SimpleRotation(0f, 0f);
    private GHoloData data = new GHoloData();
    private IGHoloRowEntity holoRowEntity = null;

    public GHoloRow(@NotNull GHolo holo, @NotNull String content) {
        this.holo = holo;
        this.content = content;
    }

    public @NotNull GHolo getHolo() { return holo; }

    public int getPosition() { return holo.getRows().indexOf(this); }

    public @NotNull String getContent() { return content; }

    public @NotNull GHoloRow setContent(@NotNull String content) {
        this.content = content;
        return this;
    }

    public @NotNull SimpleOffset getOffset() { return offset.clone(); }

    public @NotNull SimpleOffset getRawOffset() { return offset; }

    public @NotNull GHoloRow setOffset(@NotNull SimpleOffset offset) {
        this.offset = offset.clone();
        return this;
    }

    public @NotNull SimpleRotation getRotation() { return rotation.clone(); }

    public @NotNull SimpleRotation getRawRotation() { return rotation; }

    public @NotNull GHoloRow setRotation(@NotNull SimpleRotation rotation) {
        this.rotation = rotation.clone();
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