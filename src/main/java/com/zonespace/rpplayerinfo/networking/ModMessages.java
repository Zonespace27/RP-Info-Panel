package com.zonespace.rpplayerinfo.networking;

import org.lwjgl.system.windows.MSG;

import com.zonespace.rpplayerinfo.RPPlayerInfo;
import com.zonespace.rpplayerinfo.networking.packet.PlayerDataSyncC2SPacket;
import com.zonespace.rpplayerinfo.networking.packet.PlayerDataSyncS2CPacket;
import com.zonespace.rpplayerinfo.networking.packet.RoundRobinSyncC2SPacket;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModMessages {
    private static SimpleChannel INSTANCE;

    private static int packetId = 0;
    private static int id() {
        return packetId++;
    }

    public static void register() {
        SimpleChannel net = NetworkRegistry.ChannelBuilder.named(new ResourceLocation(RPPlayerInfo.MODID, "messages"))
            .networkProtocolVersion(() -> "1.0")
            .clientAcceptedVersions(s -> true)
            .serverAcceptedVersions(s -> true)
            .simpleChannel();

        INSTANCE = net;

        net.messageBuilder(PlayerDataSyncS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
            .decoder(PlayerDataSyncS2CPacket::new)
            .encoder(PlayerDataSyncS2CPacket::toBytes)
            .consumerMainThread(PlayerDataSyncS2CPacket::handle)
            .add();

        net.messageBuilder(PlayerDataSyncC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
            .decoder(PlayerDataSyncC2SPacket::new)
            .encoder(PlayerDataSyncC2SPacket::toBytes)
            .consumerMainThread(PlayerDataSyncC2SPacket::handle)
            .add();

        net.messageBuilder(RoundRobinSyncC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
            .decoder(RoundRobinSyncC2SPacket::new)
            .encoder(RoundRobinSyncC2SPacket::toBytes)
            .consumerMainThread(RoundRobinSyncC2SPacket::handle)
            .add();
    }

    @SuppressWarnings("hiding")
    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }

    @SuppressWarnings("hiding")
    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }
}
