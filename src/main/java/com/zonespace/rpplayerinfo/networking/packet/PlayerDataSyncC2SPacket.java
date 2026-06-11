package com.zonespace.rpplayerinfo.networking.packet;

import java.util.UUID;

import com.zonespace.rpplayerinfo.RPPlayerInfo;
import com.zonespace.rpplayerinfo.api.GenderStringConverter;
import com.zonespace.rpplayerinfo.api.PermissionStringConverter;
import com.zonespace.rpplayerinfo.data.EPlayerGender;
import com.zonespace.rpplayerinfo.data.EPlayerPermission;
import com.zonespace.rpplayerinfo.data.ModAttachments;
import com.zonespace.rpplayerinfo.data.PlayerRPData;
import com.zonespace.rpplayerinfo.networking.ModMessages;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PlayerDataSyncC2SPacket(EPlayerPermission permissionToKill, EPlayerPermission permissionToMaim, EPlayerGender gender, int heightInches, int heightFeet, String description, String name, String race) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<PlayerDataSyncC2SPacket> TYPE =
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(RPPlayerInfo.MODID, "player_data_sync_c2s"));

    public static final StreamCodec<FriendlyByteBuf, PlayerDataSyncC2SPacket> STREAM_CODEC =
        CustomPacketPayload.codec(PlayerDataSyncC2SPacket::toBytes, PlayerDataSyncC2SPacket::fromBytes);

    public static PlayerDataSyncC2SPacket fromBytes(FriendlyByteBuf buf) {
        EPlayerPermission permissionToKill = PermissionStringConverter.stringToPermissionEnum(buf.readUtf());
        EPlayerPermission permissionToMaim = PermissionStringConverter.stringToPermissionEnum(buf.readUtf());
        EPlayerGender gender = GenderStringConverter.stringToGenderEnum(buf.readUtf());
        String description = buf.readUtf();
        String name = buf.readUtf();
        String race = buf.readUtf();
        int heightInches = buf.readInt();
        int heightFeet = buf.readInt();
        return new PlayerDataSyncC2SPacket(permissionToKill, permissionToMaim, gender, heightInches, heightFeet, description, name, race);
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUtf(PermissionStringConverter.permissionEnumToString(permissionToKill));
        buf.writeUtf(PermissionStringConverter.permissionEnumToString(permissionToMaim));
        buf.writeUtf(GenderStringConverter.genderEnumToString(gender));
        buf.writeUtf(description);
        buf.writeUtf(name);
        buf.writeUtf(race);
        buf.writeInt(heightInches);
        buf.writeInt(heightFeet);
    }

    @Override
    public CustomPacketPayload.Type<PlayerDataSyncC2SPacket> type() {
        return TYPE;
    }

    public static void handle(PlayerDataSyncC2SPacket packet, IPayloadContext context) {
        // on the server
        if (!(context.player() instanceof ServerPlayer player)) {
            return;
        }

        PlayerRPData rpData = player.getData(ModAttachments.PLAYER_RP_DATA);
        rpData.setPermissionToKill(packet.permissionToKill());
        rpData.setPermissionToMaim(packet.permissionToMaim());
        rpData.setGender(packet.gender());
        rpData.setHeightInches(packet.heightInches());
        rpData.setHeightFeet(packet.heightFeet());
        rpData.setDescription(packet.description());
        rpData.setName(packet.name());
        rpData.setRace(packet.race());

        UUID playerUUID = player.getUUID();

        for (ServerPlayer serverPlayer : player.getServer().getPlayerList().getPlayers()) {
            ModMessages.sendToPlayer(new PlayerToPlayerSyncS2CPacket(
                packet.permissionToKill(),
                packet.permissionToMaim(),
                packet.gender(),
                packet.heightInches(),
                packet.heightFeet(),
                packet.description(),
                packet.name(),
                packet.race(),
                playerUUID
            ), serverPlayer);
        }
    }
}
