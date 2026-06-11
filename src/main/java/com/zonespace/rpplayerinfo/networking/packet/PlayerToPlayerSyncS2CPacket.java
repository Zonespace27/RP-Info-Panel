package com.zonespace.rpplayerinfo.networking.packet;

import java.util.UUID;

import com.zonespace.rpplayerinfo.RPPlayerInfo;
import com.zonespace.rpplayerinfo.api.GenderStringConverter;
import com.zonespace.rpplayerinfo.api.PermissionStringConverter;
import com.zonespace.rpplayerinfo.data.EPlayerGender;
import com.zonespace.rpplayerinfo.data.EPlayerPermission;
import com.zonespace.rpplayerinfo.networking.RPDataClientSetter;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PlayerToPlayerSyncS2CPacket(EPlayerPermission permissionToKill, EPlayerPermission permissionToMaim, EPlayerGender gender, int heightInches, int heightFeet, String description, String name, String race, UUID playerUUID) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<PlayerToPlayerSyncS2CPacket> TYPE =
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(RPPlayerInfo.MODID, "player_to_player_sync_s2c"));

    public static final StreamCodec<FriendlyByteBuf, PlayerToPlayerSyncS2CPacket> STREAM_CODEC =
        CustomPacketPayload.codec(PlayerToPlayerSyncS2CPacket::toBytes, PlayerToPlayerSyncS2CPacket::fromBytes);

    public static PlayerToPlayerSyncS2CPacket fromBytes(FriendlyByteBuf buf) {
        EPlayerPermission permissionToKill = PermissionStringConverter.stringToPermissionEnum(buf.readUtf());
        EPlayerPermission permissionToMaim = PermissionStringConverter.stringToPermissionEnum(buf.readUtf());
        EPlayerGender gender = GenderStringConverter.stringToGenderEnum(buf.readUtf());
        String description = buf.readUtf();
        String name = buf.readUtf();
        String race = buf.readUtf();
        int heightInches = buf.readInt();
        int heightFeet = buf.readInt();
        UUID playerUUID = buf.readUUID();
        return new PlayerToPlayerSyncS2CPacket(permissionToKill, permissionToMaim, gender, heightInches, heightFeet, description, name, race, playerUUID);
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
        buf.writeUUID(playerUUID);
    }

    @Override
    public CustomPacketPayload.Type<PlayerToPlayerSyncS2CPacket> type() {
        return TYPE;
    }

    public static void handle(PlayerToPlayerSyncS2CPacket packet, IPayloadContext context) {
        // on the client
        RPDataClientSetter.setRPData(
            packet.permissionToKill(),
            packet.permissionToMaim(),
            packet.gender(),
            packet.heightInches(),
            packet.heightFeet(),
            packet.description(),
            packet.name(),
            packet.race(),
            packet.playerUUID()
        );
    }
}
