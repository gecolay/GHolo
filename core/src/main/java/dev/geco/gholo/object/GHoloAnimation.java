package dev.geco.gholo.object;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GHoloAnimation {

    private final String id;
    private final long ticks;
    private final List<String> content;
    private final int size;
    private int rowId = 0;

    public GHoloAnimation(@NotNull String id, long ticks, @NotNull List<String> content) {
        this.id = id;
        this.ticks = ticks;
        this.content = content;
        size = content.size();
    }

    public @NotNull String getId() { return id; }

    public long getTicks() { return ticks; }

    public @NotNull List<String> getContent() { return content; }

    public int getSize() { return size; }

    public int getRowId() { return rowId; }

    public @NotNull GHoloAnimation setRowId(int rowId) {
        this.rowId = rowId;
        return this;
    }

    public @Nullable String getCurrentContent() { return content.get(rowId); }

}