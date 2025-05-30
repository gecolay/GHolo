package dev.geco.gholo.service;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.api.event.GInteractionPlayerEvent;
import dev.geco.gholo.object.interaction.GInteractType;
import dev.geco.gholo.object.interaction.GInteraction;
import dev.geco.gholo.object.interaction.GInteractionAction;
import dev.geco.gholo.object.interaction.GInteractionData;
import dev.geco.gholo.object.interaction.GInteractionUpdateType;
import dev.geco.gholo.object.interaction.action.GInteractionActionType;
import dev.geco.gholo.object.simple.SimpleLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.UUID;
import java.util.logging.Level;

public class InteractionService {

    public static final double INTERACTION_COOLDOWN_SECONDS = 0.5;
    private static final long INTERACTION_COOLDOWN_MILLIS = (long) (INTERACTION_COOLDOWN_SECONDS * 1000);

    private final GHoloMain gHoloMain;
    private final List<GInteraction> interactions = new ArrayList<>();
    private final HashMap<Integer, GInteraction> interactionMap = new HashMap<>();
    private final HashMap<Integer, HashMap<UUID, Long>> lastInteractionMap = new HashMap<>();

    public InteractionService(GHoloMain gHoloMain) {
        this.gHoloMain = gHoloMain;
    }

    public void createTables() {
        try {
            gHoloMain.getDataService().execute("""
                CREATE TABLE IF NOT EXISTS gholo_interaction (
                    uuid TEXT,
                    id TEXT,
                    location TEXT,
                    data TEXT
                );
            """);
            gHoloMain.getDataService().execute("""
                CREATE TABLE IF NOT EXISTS gholo_interaction_action (
                    position INTEGER,
                    interaction_uuid TEXT,
                    type TEXT,
                    parameter TEXT
                );
            """);
        } catch(SQLException e) { gHoloMain.getLogger().log(Level.SEVERE, "Could not create interaction database tables!", e); }
    }

    public List<GInteraction> getInteractions() { return new ArrayList<>(interactions); }

    public List<GInteraction> getNearInteractions(Location location, double range) { return interactions.stream().filter(interaction -> interaction.getRawLocation().getWorld().equals(location.getWorld()) && interaction.getRawLocation().distance(location) <= range).toList(); }

    public boolean hasInteractions() { return !interactions.isEmpty(); }

    public GInteraction getInteraction(String interactionId) { return interactions.stream().filter(interaction -> interaction.getId().equalsIgnoreCase(interactionId)).findFirst().orElse(null); }

    public int getInteractionCount() { return interactions.size(); }

    public int getInteractionActionCount() { return interactions.stream().mapToInt(interaction -> interaction.getActions().size()).sum(); }

    public GInteraction createInteraction(String interactionId, SimpleLocation location) {
        try {
            GInteraction interaction = new GInteraction(UUID.randomUUID(), interactionId, location);
            writeInteraction(interaction, false);
            interactions.add(interaction);
            gHoloMain.getEntityUtil().createInteractionEntity(interaction);
            interactionMap.put(interaction.getInteractionEntity().getId(), interaction);
            // Lazy load player packet handlers because we now need them
            if(interactions.size() == 1) gHoloMain.getPacketHandler().setupPlayerPacketHandlers();
            return interaction;
        } catch(SQLException e) { gHoloMain.getLogger().log(Level.SEVERE, "Could not create interaction '" + interactionId + "'!", e); }
        return null;
    }

    public GInteractionAction addInteractionAction(GInteraction interaction, GInteractionActionType interactionActionType, String parameter) {
        try {
            int position = interaction.getActions().size();

            GInteractionAction interactionAction = new GInteractionAction(interaction, interactionActionType, parameter);
            writeInteractionAction(interactionAction, position);
            interaction.addAction(interactionAction);

            return interactionAction;
        } catch(SQLException e) { gHoloMain.getLogger().log(Level.SEVERE, "Could not create interaction action for interaction '" + interaction.getId() + "'!", e); }
        return null;
    }

    public GInteractionAction insertInteractionAction(GInteraction interaction, int position, GInteractionActionType interactionActionType, String parameter) {
        try {
            gHoloMain.getDataService().execute("UPDATE gholo_interaction_action SET position = position + 1 WHERE interaction_uuid = ? AND position >= ?", interaction.getUuid().toString(), position);

            GInteractionAction interactionAction = new GInteractionAction(interaction, interactionActionType, parameter);
            writeInteractionAction(interactionAction, position);
            interaction.insertAction(interactionAction, position);

            return interactionAction;
        } catch(SQLException e) { gHoloMain.getLogger().log(Level.SEVERE, "Could not insert interaction action for interaction '" + interaction.getId() + "'!", e); }
        return null;
    }

    public void updateInteractionAction(GInteractionAction interactionAction, GInteractionActionType interactionActionType, String parameter) {
        try {
            gHoloMain.getDataService().execute("UPDATE gholo_interaction_action SET type = ?, parameter = ? WHERE interaction_uuid = ? AND position = ?", interactionActionType.getType(), parameter, interactionAction.getInteraction().getUuid().toString(), interactionAction.getPosition());
            interactionAction.setInteractionActionType(interactionActionType);
            interactionAction.setParameter(parameter);
        } catch(SQLException e) { gHoloMain.getLogger().log(Level.SEVERE, "Could not update interaction action type / parameter of interaction '" + interactionAction.getInteraction().getId() + "'!", e); }
    }

    public void removeInteractionAction(GInteractionAction interactionAction) {
        try {
            GInteraction interaction = interactionAction.getInteraction();
            int position = interactionAction.getPosition();
            gHoloMain.getDataService().execute("DELETE FROM gholo_interaction_action where interaction_uuid = ? AND position = ?", interaction.getUuid().toString(), position);
            gHoloMain.getDataService().execute("UPDATE gholo_interaction_action SET position = position - 1 WHERE interaction_uuid = ? AND position > ?", interaction.getUuid().toString(), position);
            interaction.removeAction(position);
        } catch(SQLException e) { gHoloMain.getLogger().log(Level.SEVERE, "Could not remove interaction action of interaction '" + interactionAction.getInteraction().getId() + "'!", e); }
    }

    public void updateInteractionId(GInteraction interaction, String interactionId) {
        try {
            gHoloMain.getDataService().execute("UPDATE gholo_interaction SET id = ? WHERE uuid = ?", interactionId, interaction.getUuid().toString());
            interaction.setId(interactionId);
        } catch(SQLException e) { gHoloMain.getLogger().log(Level.SEVERE, "Could not rename interaction '" + interaction.getId() + "' to '" + interactionId + "'!", e); }
    }

    public void updateInteractionLocation(GInteraction interaction, SimpleLocation location) {
        try {
            gHoloMain.getDataService().execute("UPDATE gholo_interaction SET location = ? WHERE uuid = ?",
                    location.toString(),
                    interaction.getUuid().toString()
            );
            if(!interaction.getRawLocation().getWorld().equals(location.getWorld())) {
                unloadInteraction(interaction);
                interaction.setLocation(location);
                gHoloMain.getEntityUtil().createInteractionEntity(interaction);
                return;
            }
            interaction.setLocation(location);
            if(interaction.getInteractionEntity() != null) interaction.getInteractionEntity().publishUpdate(GInteractionUpdateType.LOCATION);
        } catch(SQLException e) { gHoloMain.getLogger().log(Level.SEVERE, "Could not update interaction location of interaction '" + interaction.getId() + "'!", e); }
    }

    public void updateInteractionData(GInteraction interaction, GInteractionData data) {
        try {
            gHoloMain.getDataService().execute("UPDATE gholo_interaction SET data = ? WHERE uuid = ?", data.toString(), interaction.getUuid().toString());
            interaction.setData(data);
        } catch(SQLException e) { gHoloMain.getLogger().log(Level.SEVERE, "Could not update interaction data of interaction '" + interaction.getId() + "'!", e); }
    }

    public void copyInteraction(GInteraction interaction, String interactionId) {
        try {
            GInteraction newInteraction = new GInteraction(UUID.randomUUID(), interactionId, interaction.getLocation());
            newInteraction.setData(interaction.getData());
            writeInteraction(newInteraction, false);
            interactions.add(newInteraction);
            for(GInteractionAction interactionAction : interaction.getActions()) {
                GInteractionAction newInteractionAction = new GInteractionAction(newInteraction, interactionAction.getInteractionActionType(), interactionAction.getParameter());
                writeInteractionAction(newInteractionAction, interactionAction.getPosition());
                newInteraction.addAction(newInteractionAction);
            }
        } catch(SQLException e) { gHoloMain.getLogger().log(Level.SEVERE, "Could not copy interaction '" + interaction.getId() + "' to '" + interactionId + "'!", e); }
    }

    public void removeInteraction(GInteraction interaction) {
        try {
            gHoloMain.getDataService().execute("DELETE FROM gholo_interaction WHERE uuid = ?", interaction.getUuid().toString());
            gHoloMain.getDataService().execute("DELETE FROM gholo_interaction_action WHERE interaction_uuid = ?", interaction.getUuid().toString());
            interactions.remove(interaction);
            unloadInteraction(interaction);
            interactionMap.remove(interaction.getInteractionEntity().getId());
            lastInteractionMap.remove(interaction.getInteractionEntity().getId());
            // Unload player packet handlers because we no longer need them
            if(interactions.isEmpty()) gHoloMain.getPacketHandler().removePlayerPacketHandlers();
        } catch(SQLException e) { gHoloMain.getLogger().log(Level.SEVERE, "Could not remove interaction '" + interaction.getId() + "'!", e); }
    }

    public void loadInteractions(@Nullable World world) {
        try {
            List<UUID> loadedInteractions = interactions.stream().map(GInteraction::getUuid).toList();
            try(ResultSet resultSet = gHoloMain.getDataService().executeAndGet("SELECT * FROM gholo_interaction")) {
                interactionwhile: while(resultSet.next()) {
                    String id = resultSet.getString("id");
                    try {
                        UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                        if(loadedInteractions.contains(uuid)) continue;

                        SimpleLocation location = SimpleLocation.fromString(resultSet.getString("location"));
                        if(location == null || location.getWorld() == null || (world != null && world.equals(location.getWorld()))) continue;

                        GInteraction interaction = new GInteraction(uuid, id, location);

                        String dataString = resultSet.getString("data");
                        interaction.getRawData().loadString(dataString);

                        try(ResultSet rowResultSet = gHoloMain.getDataService().executeAndGet("SELECT * FROM gholo_interaction_action where interaction_uuid = ?", uuid.toString())) {
                            TreeMap<Integer, GInteractionAction> interactionActionMap = new TreeMap<>();

                            while(rowResultSet.next()) {
                                int position = rowResultSet.getInt("position");
                                String type = rowResultSet.getString("type");
                                GInteractionActionType interactionActionType = gHoloMain.getInteractionActionService().getInteractionAction(type);
                                if(interactionActionType == null) {
                                    gHoloMain.getLogger().warning("Could not load interaction action '" + position + "' of interaction '" + id + "', invalid type!");
                                    continue interactionwhile;
                                }
                                String parameter = rowResultSet.getString("parameter");

                                GInteractionAction interactionAction = new GInteractionAction(interaction, interactionActionType, parameter);

                                interactionActionMap.put(position, interactionAction);
                            }

                            for(GInteractionAction interactionAction : interactionActionMap.values()) {
                                interaction.addAction(interactionAction);
                            }
                        }

                        interactions.add(interaction);
                        gHoloMain.getEntityUtil().createInteractionEntity(interaction);
                        interactionMap.put(interaction.getInteractionEntity().getId(), interaction);
                    } catch(Throwable e) { gHoloMain.getLogger().log(Level.SEVERE, "Could not load interaction '" + id + "'!", e); }
                }
            }
        } catch(SQLException e) { gHoloMain.getLogger().log(Level.SEVERE, "Could not load interactions!", e); }
    }

    public void loadInteractionsForPlayer(Player player) { for(GInteraction interaction : interactions) loadInteractionForPlayer(interaction, player); }

    public void loadInteraction(GInteraction interaction) { for(Player player : interaction.getRawLocation().getWorld().getPlayers()) loadInteractionForPlayer(interaction, player); }

    public void loadInteractionForPlayer(GInteraction interaction, Player player) { if(interaction.getInteractionEntity() != null) interaction.getInteractionEntity().loadInteraction(player); }

    public void unloadInteractionsForPlayer(Player player) { for(GInteraction interaction : interactions) unloadInteractionForPlayer(interaction, player); }

    public void unloadInteraction(GInteraction interaction) { for(Player player : interaction.getRawLocation().getWorld().getPlayers()) unloadInteractionForPlayer(interaction, player); }

    public void unloadInteractionForPlayer(GInteraction interaction, Player player) { if(interaction.getInteractionEntity() != null) interaction.getInteractionEntity().unloadInteraction(player); }

    public void unloadInteractions(@Nullable World world) {
        for(GInteraction interaction : interactions) {
            if(world != null && world.equals(interaction.getRawLocation().getWorld())) continue;
            unloadInteraction(interaction);
        }
        if(world == null) interactions.clear();
        else interactions.removeIf(interaction -> interaction.getRawLocation().getWorld().equals(world));
    }

    public void writeInteraction(GInteraction interaction, boolean override) throws SQLException {
        if(override) {
            ResultSet resultSet = gHoloMain.getDataService().executeAndGet("SELECT uuid FROM gholo_interaction WHERE id = ?", interaction.getId());
            while (resultSet.next()) {
                String uuid = resultSet.getString("uuid");
                gHoloMain.getDataService().execute("DELETE FROM gholo_interaction WHERE uuid = ?", uuid);
                gHoloMain.getDataService().execute("DELETE FROM gholo_interaction_action WHERE interaction_uuid = ?", uuid);
            }
        }
        gHoloMain.getDataService().execute("INSERT INTO gholo_interaction (uuid, id, location, data) VALUES (?, ?, ?, ?)",
                interaction.getUuid().toString(),
                interaction.getId(),
                interaction.getRawLocation().toString(),
                interaction.getData().toString()
        );
    }

    public void writeInteractionAction(GInteractionAction interactionAction, int position) throws SQLException {
        gHoloMain.getDataService().execute("INSERT INTO gholo_interaction_action (position, interaction_uuid, type, parameter) VALUES (?, ?, ?, ?)",
                position,
                interactionAction.getInteraction().getUuid().toString(),
                interactionAction.getInteractionActionType().getType(),
                interactionAction.getParameter()
        );
    }

    public void clearPlayerInteractions(Player player) {
        UUID playerId = player.getUniqueId();
        for(HashMap<UUID, Long> lastInteraction : lastInteractionMap.values()) lastInteraction.remove(playerId);
    }

    public void clearInteractions() {
        interactionMap.clear();
        lastInteractionMap.clear();
    }

    public boolean callInteraction(int entityId, Player player, boolean mainHand, boolean secondaryAction) {
        GInteraction interaction = interactionMap.get(entityId);
        if(interaction == null) return false;
        gHoloMain.getTaskService().run(() -> {
            HashMap<UUID, Long> lastInteractions = lastInteractionMap.computeIfAbsent(entityId, k -> new HashMap<>());
            UUID playerId = player.getUniqueId();
            long currentTime = System.currentTimeMillis();
            Long lastInteractionTime = lastInteractions.get(playerId);
            if(lastInteractionTime != null && (currentTime - lastInteractionTime) < INTERACTION_COOLDOWN_MILLIS) return;
            GInteractType interactType = mainHand ? (secondaryAction ? GInteractType.SHIFT_LEFT_CLICK : GInteractType.LEFT_CLICK) : (secondaryAction ? GInteractType.SHIFT_RIGHT_CLICK : GInteractType.RIGHT_CLICK);
            GInteractionPlayerEvent interactionEvent = new GInteractionPlayerEvent(interaction, player, interactType);
            Bukkit.getPluginManager().callEvent(interactionEvent);
            if(!interactionEvent.isCancelled()) lastInteractions.put(playerId, currentTime);
        }, true);
        return true;
    }

}