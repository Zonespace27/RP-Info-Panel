package com.zonespace.rpplayerinfo.networking.packet;

import com.zonespace.rpplayerinfo.RPPlayerInfo;
import com.zonespace.rpplayerinfo.data.ModAttachments;
import com.zonespace.rpplayerinfo.data.PlayerRPData;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record RoundRobinSyncC2SPacket() implements CustomPacketPayload {
    public static final RoundRobinSyncC2SPacket INSTANCE = new RoundRobinSyncC2SPacket();

    public static final CustomPacketPayload.Type<RoundRobinSyncC2SPacket> TYPE =
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(RPPlayerInfo.MODID, "round_robin_sync_c2s"));

    public static final StreamCodec<FriendlyByteBuf, RoundRobinSyncC2SPacket> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public CustomPacketPayload.Type<RoundRobinSyncC2SPacket> type() {
        return TYPE;
    }

    public static void handle(RoundRobinSyncC2SPacket packet, IPayloadContext context) {
        // on the server: send the player's stored data back down to them
        if (!(context.player() instanceof ServerPlayer player)) {
            return;
        }

        PlayerRPData rpData = player.getData(ModAttachments.PLAYER_RP_DATA);

        context.reply(new PlayerDataSyncS2CPacket(
            rpData.getPermissionToKill(),
            rpData.getPermissionToMaim(),
            rpData.getGender(),
            rpData.getHeightInches(),
            rpData.getHeightFeet(),
            rpData.getDescription(),
            rpData.getName(),
            rpData.getRace()
        ));
    }
}
