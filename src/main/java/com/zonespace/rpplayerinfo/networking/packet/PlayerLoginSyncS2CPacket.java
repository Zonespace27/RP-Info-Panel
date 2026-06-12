package com.zonespace.rpplayerinfo.networking.packet;

import com.zonespace.rpplayerinfo.RPPlayerInfo;
import com.zonespace.rpplayerinfo.client.ClientPlayerRPData;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PlayerLoginSyncS2CPacket() implements CustomPacketPayload {
    public static final PlayerLoginSyncS2CPacket INSTANCE = new PlayerLoginSyncS2CPacket();

    public static final CustomPacketPayload.Type<PlayerLoginSyncS2CPacket> TYPE =
        new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(RPPlayerInfo.MODID, "player_login_sync_s2c"));

    public static final StreamCodec<FriendlyByteBuf, PlayerLoginSyncS2CPacket> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public CustomPacketPayload.Type<PlayerLoginSyncS2CPacket> type() {
        return TYPE;
    }

    public static void handle(PlayerLoginSyncS2CPacket packet, IPayloadContext context) {
        // on the client: upload our data back to the server so it can broadcast it to everyone
        context.reply(new PlayerDataSyncC2SPacket(ClientPlayerRPData.getPermissionToKill(), ClientPlayerRPData.getPermissionToMaim(), ClientPlayerRPData.getGender(), ClientPlayerRPData.getHeightInches(), ClientPlayerRPData.getHeightFeet(), ClientPlayerRPData.getDescription(), ClientPlayerRPData.getName(), ClientPlayerRPData.getRace()));
    }
}
