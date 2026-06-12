package com.zonespace.rpplayerinfo.networking;

import com.zonespace.rpplayerinfo.networking.packet.PlayerDataSyncC2SPacket;
import com.zonespace.rpplayerinfo.networking.packet.PlayerDataSyncS2CPacket;
import com.zonespace.rpplayerinfo.networking.packet.PlayerLoginSyncS2CPacket;
import com.zonespace.rpplayerinfo.networking.packet.PlayerToPlayerSyncS2CPacket;
import com.zonespace.rpplayerinfo.networking.packet.RoundRobinSyncC2SPacket;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class ModMessages {
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1.0").optional();

        registrar.playToClient(PlayerDataSyncS2CPacket.TYPE, PlayerDataSyncS2CPacket.STREAM_CODEC, PlayerDataSyncS2CPacket::handle);
        registrar.playToClient(PlayerToPlayerSyncS2CPacket.TYPE, PlayerToPlayerSyncS2CPacket.STREAM_CODEC, PlayerToPlayerSyncS2CPacket::handle);
        registrar.playToClient(PlayerLoginSyncS2CPacket.TYPE, PlayerLoginSyncS2CPacket.STREAM_CODEC, PlayerLoginSyncS2CPacket::handle);

        registrar.playToServer(PlayerDataSyncC2SPacket.TYPE, PlayerDataSyncC2SPacket.STREAM_CODEC, PlayerDataSyncC2SPacket::handle);
        registrar.playToServer(RoundRobinSyncC2SPacket.TYPE, RoundRobinSyncC2SPacket.STREAM_CODEC, RoundRobinSyncC2SPacket::handle);
    }

    public static void sendToPlayer(CustomPacketPayload message, ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, message);
    }
}
