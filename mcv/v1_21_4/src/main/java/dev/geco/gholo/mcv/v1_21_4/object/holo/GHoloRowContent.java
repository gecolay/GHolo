package dev.geco.gholo.mcv.v1_21_4.object.holo;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.mcv.v1_21_4.object.holo.type.GHoloRowBlockContent;
import dev.geco.gholo.mcv.v1_21_4.object.holo.type.GHoloRowEntityContent;
import dev.geco.gholo.mcv.v1_21_4.object.holo.type.GHoloRowItemContent;
import dev.geco.gholo.mcv.v1_21_4.object.holo.type.GHoloRowItemStackContent;
import dev.geco.gholo.mcv.v1_21_4.object.holo.type.GHoloRowTextContent;
import dev.geco.gholo.object.holo.GHoloData;
import dev.geco.gholo.object.holo.GHoloRow;
import dev.geco.gholo.object.holo.GHoloUpdateType;
import dev.geco.gholo.object.holo.IGHoloRowContent;
import dev.geco.gholo.mcv.v1_21_4.object.holo.type.IGHoloRowContentType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

public class GHoloRowContent implements IGHoloRowContent {

    private final GHoloRow holoRow;
    private final GHoloMain gHoloMain;
    private GHoloRowTextContent textContent;
    private GHoloRowEntityContent entityContent;
    private GHoloRowBlockContent blockContent;
    private GHoloRowItemContent itemContent;
    private GHoloRowItemStackContent itemStackContent;
    private final HashMap<UUID, IGHoloRowContentType> currentContentTypes = new HashMap<>();

    public GHoloRowContent(GHoloRow holoRow) {
        this.holoRow = holoRow;
        this.gHoloMain = GHoloMain.getInstance();
    }

    @Override
    public void loadHoloRow() {
        String permission = getPermission();
        for(Player player : holoRow.getHolo().getRawLocation().getWorld().getPlayers()) {
            if(permission != null && !gHoloMain.getPermissionService().hasPermission(player, permission)) continue;
            String content = holoRow.getContent();
            content = gHoloMain.getTextFormatUtil().formatText(content, player);
            updateContent(content, player);
        }
    }

    @Override
    public void loadHoloRow(@NotNull Player player) {
        if(!player.getWorld().equals(holoRow.getHolo().getRawLocation().getWorld())) return;
        String permission = getPermission();
        if(permission != null && !gHoloMain.getPermissionService().hasPermission(player, permission)) return;
        String content = holoRow.getContent();
        content = gHoloMain.getTextFormatUtil().formatText(content, player);
        updateContent(content, player);
    }

    private void updateContent(String content, Player player) {
        UUID playerUuid = player.getUniqueId();
        String[] contentSplit = content.split(":", 2);
        IGHoloRowContentType contentType = switch(contentSplit[0].toLowerCase()) {
            case "entity" -> {
                if(entityContent == null) entityContent = new GHoloRowEntityContent(holoRow, gHoloMain);
                yield entityContent;
            }
            case "block" -> {
                if(blockContent == null) blockContent = new GHoloRowBlockContent(holoRow, gHoloMain);
                yield blockContent;
            }
            case "item" -> {
                if(itemContent == null) itemContent = new GHoloRowItemContent(holoRow, gHoloMain);
                yield itemContent;
            }
            case "itemstack" -> {
                if(itemStackContent == null) itemStackContent = new GHoloRowItemStackContent(holoRow, gHoloMain);
                yield itemStackContent;
            }
            default -> {
                if(textContent == null) textContent = new GHoloRowTextContent(holoRow, gHoloMain);
                yield textContent;
            }
        };
        IGHoloRowContentType currentContentType = currentContentTypes.get(playerUuid);
        if(currentContentType != contentType) {
            if(currentContentType != null) currentContentType.unload(player);
            contentType.load(player, contentType instanceof GHoloRowTextContent ? content : contentSplit[1], true);
            currentContentTypes.put(playerUuid, contentType);
        } else contentType.load(player, contentType instanceof GHoloRowTextContent ? content : contentSplit[1], false);
    }

    @Override
    public void publishUpdate(@NotNull GHoloUpdateType updateType) {
        if(updateType == GHoloUpdateType.CONTENT) {
            loadHoloRow();
            return;
        }
        if(updateType == GHoloUpdateType.PERMISSION) {
            unloadHoloRow();
            loadHoloRow();
            return;
        }
        for(IGHoloRowContentType holoRowContentType : currentContentTypes.values()) holoRowContentType.publishUpdate(updateType);
    }

    @Override
    public void unloadHoloRow() { for(Player player : holoRow.getHolo().getRawLocation().getWorld().getPlayers()) unloadHoloRow(player); }

    @Override
    public void unloadHoloRow(@NotNull Player player) {
        IGHoloRowContentType holoRowContentType = currentContentTypes.get(player.getUniqueId());
        if(holoRowContentType == null) return;
        holoRowContentType.unload(player);
        currentContentTypes.remove(player.getUniqueId());
    }

    private String getPermission() {
        GHoloData rowData = holoRow.getRawData();
        GHoloData holoData = holoRow.getHolo().getRawData();
        return rowData.getPermission() != null ? rowData.getPermission() : (holoData.getPermission() != null ? holoData.getPermission() : GHoloData.DEFAULT_PERMISSION);
    }

}