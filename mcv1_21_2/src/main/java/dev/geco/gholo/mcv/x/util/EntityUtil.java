package dev.geco.gholo.mcv.x.util;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.mcv.x.objects.*;
import dev.geco.gholo.objects.*;
import dev.geco.gholo.util.*;

public class EntityUtil implements IEntityUtil {

    private final GHoloMain GPM;

    public EntityUtil(GHoloMain GPluginMain) { GPM = GPluginMain; }

    @Override
    public void spawnHolo(GHolo Holo) {
        for(org.bukkit.entity.Player player : Holo.getRawLocation().getWorld().getPlayers()) spawnHolo(Holo, player);
    }

    @Override
    public void spawnHolo(GHolo Holo, org.bukkit.entity.Player Player) {
        for(GHoloRow row : Holo.getRows()) row.getHoloRowEntity().spawnHoloRow(Player);
    }

    @Override
    public void removeHolo(GHolo Holo) {
        for(org.bukkit.entity.Player player : Holo.getRawLocation().getWorld().getPlayers()) removeHolo(Holo, player);
    }

    @Override
    public void removeHolo(GHolo Holo, org.bukkit.entity.Player Player) {
        for(GHoloRow row : Holo.getRows()) row.getHoloRowEntity().removeHoloRow(Player);
    }

    @Override
    public IGHoloRowEntity createHoloRowEntity(GHoloRow HoloRow) {
        GHoloRowEntity holoRowEntity = new GHoloRowEntity(HoloRow);
        HoloRow.setHoloRowEntity(holoRowEntity);
        holoRowEntity.spawnHoloRow();
        return holoRowEntity;
    }

}