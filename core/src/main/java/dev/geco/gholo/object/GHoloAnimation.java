package dev.geco.gholo.object;

import java.util.List;

public class GHoloAnimation {

    private final String id;
    private final long ticks;
    private final List<String> content;
    private final int size;
    private int rowId = 0;

    public GHoloAnimation(String id, long ticks, List<String> content) {
        this.id = id;
        this.ticks = ticks;
        this.content = content;
        size = content.size();
    }

    public String getId() { return id; }

    public long getTicks() { return ticks; }

    public List<String> getContent() { return content; }

    public int getSize() { return size; }

    public int getRowId() { return rowId; }

    public GHoloAnimation setRowId(int rowId) {
        this.rowId = rowId;
        return this;
    }

    public String getCurrentContent() { return content.get(rowId); }

}