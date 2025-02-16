package dev.geco.gholo.object.interaction.importer;

public class GInteractionImporterResult {

    private final boolean success;
    private final long count;

    public GInteractionImporterResult(boolean success, long count) {
        this.success = success;
        this.count = count;
    }

    public boolean hasSucceeded() { return success; }

    public long getCount() { return count; }

}