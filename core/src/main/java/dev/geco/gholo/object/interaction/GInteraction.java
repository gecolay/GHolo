package dev.geco.gholo.object.interaction;

import dev.geco.gholo.object.simple.SimpleLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GInteraction {

    private final UUID uuid;
    private String id;
    private SimpleLocation location;
    private final List<GInteractionAction> actions = new ArrayList<>();
    private GInteractionData data = new GInteractionData();
    private IGInteractionEntity interactionEntity = null;

    public GInteraction(@NotNull UUID uuid, @NotNull String id, @NotNull SimpleLocation location) {
        this.uuid = uuid;
        this.id = id;
        this.location = location;
    }

    public @NotNull UUID getUuid() { return uuid; }

    public @NotNull String getId() { return id; }

    public @NotNull GInteraction setId(@NotNull String id) {
        this.id = id;
        return this;
    }

    public @NotNull SimpleLocation getRawLocation() { return location.clone(); }

    public @NotNull SimpleLocation getLocation() { return location; }

    public @NotNull GInteraction setLocation(@Nullable SimpleLocation location) {
        this.location = location;
        return this;
    }

    public @NotNull List<GInteractionAction> getActions() { return actions; }

    public @Nullable GInteractionAction getAction(int position) throws IndexOutOfBoundsException { return actions.get(position); }

    public @NotNull GInteraction addAction(@NotNull GInteractionAction interactionAction) {
        actions.add(interactionAction);
        return this;
    }

    public @NotNull GInteraction insertAction(@NotNull GInteractionAction interactionAction, int position) throws IndexOutOfBoundsException {
        actions.add(position, interactionAction);
        return this;
    }

    public @NotNull GInteraction removeAction(int position) throws IndexOutOfBoundsException {
        actions.remove(position);
        return this;
    }

    public @NotNull GInteractionData getData() { return data.clone(); }

    public @NotNull GInteractionData getRawData() { return data; }

    public @NotNull GInteraction setData(@NotNull GInteractionData data) {
        this.data = data.clone();
        return this;
    }

    public @Nullable IGInteractionEntity getInteractionEntity() { return interactionEntity; }

    public @NotNull GInteraction setInteractionEntity(@Nullable IGInteractionEntity interactionEntity) {
        this.interactionEntity = interactionEntity;
        return this;
    }

}