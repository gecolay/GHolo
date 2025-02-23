package dev.geco.gholo.util;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import dev.geco.gholo.GHoloMain;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.logging.Level;

public class ServerConnectUtil {

    private final GHoloMain gHoloMain;
    private static final String BUNGEE_CORD_CHANNEL = "BungeeCord";

    public ServerConnectUtil(GHoloMain gHoloMain) {
        this.gHoloMain = gHoloMain;
    }

    public void setupChannel() { Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(gHoloMain, BUNGEE_CORD_CHANNEL); }

    public void teardownChannel() { Bukkit.getServer().getMessenger().unregisterOutgoingPluginChannel(gHoloMain, BUNGEE_CORD_CHANNEL); }

    public boolean connectPlayerToServer(Player player, String server) {
        try {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("Connect");
            out.writeUTF(server);
            player.sendPluginMessage(gHoloMain, BUNGEE_CORD_CHANNEL, out.toByteArray());
            return true;
        } catch(Throwable e) { gHoloMain.getLogger().log(Level.SEVERE, "Could not send player '" + player.getName() + "' to server '" + server + "'!", e); }
        return false;
    }

}