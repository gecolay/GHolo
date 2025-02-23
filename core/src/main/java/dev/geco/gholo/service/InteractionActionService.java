package dev.geco.gholo.service;

import dev.geco.gholo.object.interaction.action.GInteractionActionType;
import dev.geco.gholo.object.interaction.action.impl.CommandAction;
import dev.geco.gholo.object.interaction.action.impl.ConnectAction;
import dev.geco.gholo.object.interaction.action.impl.MessageAction;
import dev.geco.gholo.object.interaction.action.impl.ServerCommandAction;
import dev.geco.gholo.object.interaction.action.impl.TeleportAction;

import java.util.HashMap;

public class InteractionActionService {

    private final HashMap<String, GInteractionActionType> interactionActions = new HashMap<>();

    public HashMap<String, GInteractionActionType> getInteractionActions() { return interactionActions; }

    public GInteractionActionType getInteractionAction(String type) { return interactionActions.get(type.toLowerCase()); }

    public void registerInteractionAction(GInteractionActionType interactionAction) { this.interactionActions.put(interactionAction.getType().toLowerCase(), interactionAction); }

    public void registerDefaultInteractionActions() {
        registerInteractionAction(new CommandAction());
        registerInteractionAction(new ConnectAction());
        registerInteractionAction(new MessageAction());
        registerInteractionAction(new ServerCommandAction());
        registerInteractionAction(new TeleportAction());
    }

    public void unregisterInteractionActions() { interactionActions.clear(); }

}