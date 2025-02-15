package dev.geco.gholo.object;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GHoloAnimation {

    private final String id;
    private final long ticks;
    private final List<String> content;
    private final int size;
    private int position = 0;

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

    public int getPosition() { return position; }

    public int updatePosition() {
        position = position + 1 >= size ? 0 : position + 1;
        return position;
    }

    public @NotNull GHoloAnimation setPosition(int position) {
        this.position = position;
        return this;
    }

    public @Nullable String getCurrentContent() { return content.get(position); }

}