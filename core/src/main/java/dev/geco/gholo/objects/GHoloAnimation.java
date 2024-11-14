package dev.geco.gholo.objects;

import java.util.*;

public class GHoloAnimation {

    private final String id;
    private final long ticks;
    private final List<String> content;
    private final int size;
    private long currentTick = 0;
    private int row = 0;

    public GHoloAnimation(String Id, long Ticks, List<String> Content) {

        id = Id;
        ticks = Ticks;
        content = Content;
        size = Content.size();
    }

    public String getId() { return id; }

    public long getTicks() { return ticks; }

    public long getCurrentTick() { return currentTick; }

    public void setCurrentTick(long CurrentTick) { currentTick = CurrentTick; }

    public List<String> getContent() { return content; }

    public int getSize() { return size; }

    public int getRow() { return row; }

    public void setRow(int Row) { row = Row; }

    public String getCurrentContent() { return content.get(row); }

}