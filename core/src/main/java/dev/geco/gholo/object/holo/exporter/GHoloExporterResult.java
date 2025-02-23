package dev.geco.gholo.object.holo.exporter;

public class GHoloExporterResult {

    private final boolean success;
    private final long count;

    public GHoloExporterResult(boolean success, long count) {
        this.success = success;
        this.count = count;
    }

    public boolean hasSucceeded() { return success; }

    public long getCount() { return count; }

}