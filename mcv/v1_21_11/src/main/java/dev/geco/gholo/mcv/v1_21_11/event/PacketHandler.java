package dev.geco.gholo.mcv.v1_21_11.event;

import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.event.IPacketHandler;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class PacketHandler implements IPacketHandler {

    protected final GHoloMain gHoloMain;

    public PacketHandler(GHoloMain gHoloMain) {
        this.gHoloMain = gHoloMain;
    }

    @Override
    public void setupPlayerPacketHandlers() { for(Player player : Bukkit.getOnlinePlayers()) setupPlayerPacketHandler(player); }

    @Override
    public void setupPlayerPacketHandler(Player player) {
        ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
        ChannelPipeline channelPipeline = getPipeline(serverPlayer);
        if(channelPipeline == null) return;
        if(channelPipeline.get(GHoloMain.NAME) != null) channelPipeline.remove(GHoloMain.NAME);
        channelPipeline.addBefore("packet_handler", GHoloMain.NAME, new ChannelDuplexHandler() {
            @Override
            public void channelRead(ChannelHandlerContext channelHandlerContext, Object packet) throws Exception {
                if(handlePacket(packet, player)) return;
                super.channelRead(channelHandlerContext, packet);
            }
        });
    }

    @Override
    public void removePlayerPacketHandlers() { for(Player player : Bukkit.getOnlinePlayers()) removePlayerPacketHandler(player); }

    @Override
    public void removePlayerPacketHandler(Player player) {
        ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
        ChannelPipeline channelPipeline = getPipeline(serverPlayer);
        if(channelPipeline != null && channelPipeline.get(GHoloMain.NAME) != null) channelPipeline.remove(GHoloMain.NAME);
    }

    private ChannelPipeline getPipeline(ServerPlayer serverPlayer) { return serverPlayer.connection.connection.channel.pipeline(); }

    private boolean handlePacket(Object packet, Player player) {
        if(!(packet instanceof ServerboundInteractPacket serverboundInteractPacket)) return false;
        int targetId = serverboundInteractPacket.getEntityId();
        boolean mainHand = serverboundInteractPacket.isAttack();
        boolean secondaryAction = serverboundInteractPacket.isUsingSecondaryAction();
        return gHoloMain.getInteractionService().callInteraction(targetId, player, mainHand, secondaryAction);
    }

}