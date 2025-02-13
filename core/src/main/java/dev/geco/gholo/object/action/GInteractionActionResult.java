package dev.geco.gholo.object.action;

public class GInteractionActionResult {

    private final boolean success;

    public GInteractionActionResult(boolean success) {
        this.success = success;
    }

    public boolean hasSucceeded() { return success; }

}