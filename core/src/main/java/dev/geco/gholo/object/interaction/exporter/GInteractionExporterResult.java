package dev.geco.gholo.object.interaction.exporter;

public class GInteractionExporterResult {

    private final boolean success;
    private final long count;

    public GInteractionExporterResult(boolean success, long count) {
        this.success = success;
        this.count = count;
    }

    public boolean hasSucceeded() { return success; }

    public long getCount() { return count; }

}