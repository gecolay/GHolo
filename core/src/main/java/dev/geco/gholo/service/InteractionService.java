package dev.geco.gholo.service;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.api.event.GPlayerInteractionEvent;
import dev.geco.gholo.object.interaction.GInteraction;
import dev.geco.gholo.object.interaction.GInteractionAction;
import dev.geco.gholo.object.interaction.GInteractionData;
import dev.geco.gholo.object.interaction.GInteractionUpdateType;
import dev.geco.gholo.object.location.SimpleLocation;
import dev.geco.gholo.object.location.SimpleRotation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.UUID;

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
                    width REAL,
                    height REAL,
                    rotation TEXT,
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
        } catch(SQLException e) { e.printStackTrace(); }
    }

    public List<GInteraction> getInteractions() { return new ArrayList<>(interactions); }

    public List<GInteraction> getNearInteractions(Location location, double range) { return interactions.stream().filter(interaction -> interaction.getRawLocation().getWorld().equals(location.getWorld()) && interaction.getRawLocation().distance(location) <= range).toList(); }

    public GInteraction getInteraction(String interactionId) { return interactions.stream().filter(interaction -> interaction.getId().equalsIgnoreCase(interactionId)).findFirst().orElse(null); }

    public int getInteractionCount() { return interactions.size(); }

    public int getInteractionActionCount() { return interactions.stream().mapToInt(interaction -> interaction.getActions().size()).sum(); }

    public void loadInteractions() {
        try {
            try(ResultSet resultSet = gHoloMain.getDataService().executeAndGet("SELECT * FROM gholo_interaction")) {
                while(resultSet.next()) {
                    try {
                        UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                        String id = resultSet.getString("id");
                        SimpleLocation location = SimpleLocation.fromString(resultSet.getString("location"));
                        if(location == null) throw new RuntimeException("Could not load interaction '" + id + "', invalid location");
                        SimpleRotation rotation = SimpleRotation.fromString(resultSet.getString("rotation"));
                        if(rotation == null) throw new RuntimeException("Could not load interaction '" + id + "', invalid rotation");
                        GInteraction interaction = new GInteraction(uuid, id, location);
                        interaction.setWidth(resultSet.getFloat("width"));
                        interaction.setHeight(resultSet.getFloat("height"));
                        interaction.setRotation(rotation);

                        String dataString = resultSet.getString("data");
                        interaction.getRawData().loadString(dataString);

                        interactions.add(interaction);

                        try(ResultSet rowResultSet = gHoloMain.getDataService().executeAndGet("SELECT * FROM gholo_interaction_action where interaction_uuid = ?", uuid.toString())) {
                            TreeMap<Integer, GInteractionAction> interactionActionMap = new TreeMap<>();

                            while(rowResultSet.next()) {
                                int position = rowResultSet.getInt("position");
                                String type = rowResultSet.getString("type");
                                String parameter = rowResultSet.getString("parameter");

                                GInteractionAction holoRow = new GInteractionAction(interaction, type, parameter);

                                interactionActionMap.put(position, holoRow);
                            }

                            for(GInteractionAction interactionAction : interactionActionMap.values()) {
                                interaction.addAction(interactionAction);
                            }
                        }

                        gHoloMain.getEntityUtil().createInteractionEntity(interaction);
                        interactionMap.put(interaction.getInteractionEntity().getId(), interaction);
                    } catch(Throwable e) { e.printStackTrace(); }
                }
            }
        } catch(Throwable e) { e.printStackTrace(); }
    }

    public void loadInteractionsForPlayer(Player player) { for(GInteraction interaction : interactions) loadInteractionForPlayer(interaction, player); }

    public void loadInteraction(GInteraction interaction) { for(Player player : interaction.getRawLocation().getWorld().getPlayers()) loadInteractionForPlayer(interaction, player); }

    public void loadInteractionForPlayer(GInteraction interaction, Player player) { interaction.getInteractionEntity().loadInteraction(player); }

    public void unloadInteraction(GInteraction interaction) { for(Player player : interaction.getRawLocation().getWorld().getPlayers()) unloadInteractionForPlayer(interaction, player); }

    public void unloadInteractionForPlayer(GInteraction interaction, Player player) { interaction.getInteractionEntity().unloadInteraction(player); }

    public GInteraction createInteraction(String interactionId, SimpleLocation location) {
        try {
            GInteraction interaction = new GInteraction(UUID.randomUUID(), interactionId, location);
            writeInteraction(interaction, false);
            interactions.add(interaction);
            gHoloMain.getEntityUtil().createInteractionEntity(interaction);
            interactionMap.put(interaction.getInteractionEntity().getId(), interaction);
            return interaction;
        } catch(Throwable e) { e.printStackTrace(); }
        return null;
    }

    public GInteractionAction addInteractionAction(GInteraction interaction, String type, String parameter) {
        try {
            int position = interaction.getActions().size();

            GInteractionAction interactionAction = new GInteractionAction(interaction, type, parameter);
            writeInteractionAction(interactionAction, position);
            interaction.addAction(interactionAction);

            return interactionAction;
        } catch(Throwable e) { e.printStackTrace(); }
        return null;
    }

    public GInteractionAction insertInteractionAction(GInteraction interaction, int position, String type, String parameter) {
        try {
            gHoloMain.getDataService().execute("UPDATE gholo_interaction_action SET position = position + 1 WHERE interaction_uuid = ? AND position >= ?", interaction.getUuid().toString(), position);

            GInteractionAction interactionAction = new GInteractionAction(interaction, type, parameter);
            writeInteractionAction(interactionAction, position);
            interaction.insertAction(interactionAction, position);

            return interactionAction;
        } catch(Throwable e) { e.printStackTrace(); }
        return null;
    }

    public GInteractionAction updateInteractionAction(GInteractionAction interactionAction, String type, String parameter) {
        try {
            gHoloMain.getDataService().execute("UPDATE gholo_interaction_action SET type = ?, parameter = ? WHERE interaction_uuid = ? AND position = ?", type, parameter, interactionAction.getInteraction().getUuid().toString(), interactionAction.getPosition());

            interactionAction.setType(type);
            interactionAction.setParameter(parameter);

            return interactionAction;
        } catch(Throwable e) { e.printStackTrace(); }
        return null;
    }

    public void removeInteractionAction(GInteractionAction interactionAction) {
        try {
            GInteraction interaction = interactionAction.getInteraction();
            int position = interactionAction.getPosition();
            gHoloMain.getDataService().execute("DELETE FROM gholo_interaction_action where interaction_uuid = ? AND position = ?", interaction.getUuid().toString(), position);
            gHoloMain.getDataService().execute("UPDATE gholo_interaction_action SET position = position - 1 WHERE interaction_uuid = ? AND position > ?", interaction.getUuid().toString(), position);
            interaction.removeAction(position);
        } catch(Throwable e) { e.printStackTrace(); }
    }

    public void updateInteractionId(GInteraction interaction, String interactionId) {
        try {
            gHoloMain.getDataService().execute("UPDATE gholo_interaction SET id = ? WHERE uuid = ?", interactionId, interaction.getUuid().toString());
            interaction.setId(interactionId);
        } catch(Throwable e) { e.printStackTrace(); }
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
            interaction.getInteractionEntity().publishUpdate(GInteractionUpdateType.LOCATION);
        } catch(Throwable e) { e.printStackTrace(); }
    }

    public void updateInteractionRotation(GInteraction interaction, SimpleRotation rotation) {
        try {
            gHoloMain.getDataService().execute("UPDATE gholo_interaction SET rotation = ? WHERE uuid = ?",
                    rotation.toString(),
                    interaction.getUuid().toString()
            );
            interaction.setRotation(rotation);
            interaction.getInteractionEntity().publishUpdate(GInteractionUpdateType.LOCATION);
        } catch(Throwable e) { e.printStackTrace(); }
    }

    public void updateInteractionData(GInteraction interaction, GInteractionData data) {
        try {
            gHoloMain.getDataService().execute("UPDATE gholo_interaction SET data = ? WHERE uuid = ?", data.toString(), interaction.getUuid().toString());
            interaction.setData(data);
        } catch(Throwable e) { e.printStackTrace(); }
    }

    public void copyInteraction(GInteraction interaction, String interactionId) {
        try {
            GInteraction newInteraction = new GInteraction(UUID.randomUUID(), interactionId, interaction.getLocation());
            newInteraction.setData(interaction.getData());
            newInteraction.setRotation(interaction.getRotation());
            writeInteraction(newInteraction, false);
            interactions.add(newInteraction);
            for(GInteractionAction interactionAction : interaction.getActions()) {
                GInteractionAction newInteractionAction = new GInteractionAction(newInteraction, interactionAction.getType(), interactionAction.getParameter());
                writeInteractionAction(newInteractionAction, interactionAction.getPosition());
                newInteraction.addAction(newInteractionAction);
            }
        } catch(Throwable e) { e.printStackTrace(); }
    }

    public void removeInteraction(GInteraction interaction) {
        try {
            gHoloMain.getDataService().execute("DELETE FROM gholo_interaction WHERE uuid = ?", interaction.getUuid().toString());
            gHoloMain.getDataService().execute("DELETE FROM gholo_interaction_action WHERE interaction_uuid = ?", interaction.getUuid().toString());
            interactions.remove(interaction);
            unloadInteraction(interaction);
            interactionMap.remove(interaction.getInteractionEntity().getId());
            lastInteractionMap.remove(interaction.getInteractionEntity().getId());
        } catch(Throwable e) { e.printStackTrace(); }
    }

    public void unloadInteractions() {
        for(GInteraction interaction : interactions) unloadInteraction(interaction);
        interactions.clear();
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
        gHoloMain.getDataService().execute("INSERT INTO gholo_interaction (uuid, id, location, width, height, rotation, data) VALUES (?, ?, ?, ?, ?, ?, ?)",
                interaction.getUuid().toString(),
                interaction.getId(),
                interaction.getRawLocation().toString(),
                interaction.getWidth(),
                interaction.getHeight(),
                interaction.getRotation().toString(),
                interaction.getData().toString()
        );
    }

    public void writeInteractionAction(GInteractionAction interactionAction, int position) throws SQLException {
        gHoloMain.getDataService().execute("INSERT INTO gholo_interaction_action (position, interaction_uuid, type, parameter) VALUES (?, ?, ?, ?)",
                position,
                interactionAction.getInteraction().getUuid().toString(),
                interactionAction.getType(),
                interactionAction.getParameter()
        );
    }

    public void clearPlayerInteractions(Player player) {
        UUID playerId = player.getUniqueId();
        for(HashMap<UUID, Long> lastHoloRowInteractions : lastInteractionMap.values()) lastHoloRowInteractions.remove(playerId);
    }

    public void clearInteractions() {
        interactionMap.clear();
        lastInteractionMap.clear();
    }

    public boolean callInteraction(int entityId, Player player, boolean mainHand) {
        GInteraction interaction = interactionMap.get(entityId);
        if(interaction == null) return false;
        HashMap<UUID, Long> lastInteractions = lastInteractionMap.computeIfAbsent(entityId, k -> new HashMap<>());
        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();
        Long lastInteractionTime = lastInteractions.get(playerId);
        if(lastInteractionTime != null && (currentTime - lastInteractionTime) < INTERACTION_COOLDOWN_MILLIS) return true;
        gHoloMain.getTaskService().run(() -> {
            GPlayerInteractionEvent interactionEvent = new GPlayerInteractionEvent(interaction, player, mainHand);
            Bukkit.getPluginManager().callEvent(interactionEvent);
            if(!interactionEvent.isCancelled()) lastInteractions.put(playerId, currentTime);
        }, true);
        return true;
    }

}