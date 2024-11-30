package dev.geco.gholo.objects;

import java.util.*;

public class GHoloAnimation {

    private final String id;
    private final long ticks;
    private final List<String> content;
    private final int size;
    private int row = 0;

    public GHoloAnimation(String Id, long Ticks, List<String> Content) {
        id = Id;
        ticks = Ticks;
        content = Content;
        size = Content.size();
    }

    public String getId() { return id; }

    public long getTicks() { return ticks; }

    public List<String> getContent() { return content; }

    public int getSize() { return size; }

    public int getRow() { return row; }

    public void setRow(int Row) { row = Row; }

    public String getCurrentContent() { return content.get(row); }

}