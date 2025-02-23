package dev.geco.gholo.object.interaction.action;

public class GInteractionActionTypeResult {

    private final boolean success;

    public GInteractionActionTypeResult(boolean success) {
        this.success = success;
    }

    public boolean hasSucceeded() { return success; }

}