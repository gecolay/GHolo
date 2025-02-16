package dev.geco.gholo.object.holo.importer;

public class GHoloImporterResult {

    private final boolean success;
    private final long count;

    public GHoloImporterResult(boolean success, long count) {
        this.success = success;
        this.count = count;
    }

    public boolean hasSucceeded() { return success; }

    public long getCount() { return count; }

}