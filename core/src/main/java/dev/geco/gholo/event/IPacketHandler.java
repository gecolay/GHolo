package dev.geco.gholo.event;

import org.bukkit.entity.Player;

public interface IPacketHandler {

    void setupPlayerPacketHandlers();

    void setupPlayerPacketHandler(Player player);

    void removePlayerPacketHandlers();

    void removePlayerPacketHandler(Player player);

}
