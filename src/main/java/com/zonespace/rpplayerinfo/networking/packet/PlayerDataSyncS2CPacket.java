package com.zonespace.rpplayerinfo.networking.packet;

import com.zonespace.rpplayerinfo.RPPlayerInfo;
import com.zonespace.rpplayerinfo.api.GenderStringConverter;
import com.zonespace.rpplayerinfo.api.PermissionStringConverter;
import com.zonespace.rpplayerinfo.client.ClientPlayerRPData;
import com.zonespace.rpplayerinfo.data.EPlayerGender;
import com.zonespace.rpplayerinfo.data.EPlayerPermission;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PlayerDataSyncS2CPacket(EPlayerPermission permissionToKill, EPlayerPermission permissionToMaim, EPlayerGender gender, int heightInches, int heightFeet, String description, String name, String race) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<PlayerDataSyncS2CPacket> TYPE =
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(RPPlayerInfo.MODID, "player_data_sync_s2c"));

    public static final StreamCodec<FriendlyByteBuf, PlayerDataSyncS2CPacket> STREAM_CODEC =
        CustomPacketPayload.codec(PlayerDataSyncS2CPacket::toBytes, PlayerDataSyncS2CPacket::fromBytes);

    public static PlayerDataSyncS2CPacket fromBytes(FriendlyByteBuf buf) {
        EPlayerPermission permissionToKill = PermissionStringConverter.stringToPermissionEnum(buf.readUtf());
        EPlayerPermission permissionToMaim = PermissionStringConverter.stringToPermissionEnum(buf.readUtf());
        EPlayerGender gender = GenderStringConverter.stringToGenderEnum(buf.readUtf());
        String description = buf.readUtf();
        String name = buf.readUtf();
        String race = buf.readUtf();
        int heightInches = buf.readInt();
        int heightFeet = buf.readInt();
        return new PlayerDataSyncS2CPacket(permissionToKill, permissionToMaim, gender, heightInches, heightFeet, description, name, race);
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
    public CustomPacketPayload.Type<PlayerDataSyncS2CPacket> type() {
        return TYPE;
    }

    public static void handle(PlayerDataSyncS2CPacket packet, IPayloadContext context) {
        // on the client
        ClientPlayerRPData.setPermissionToKill(packet.permissionToKill());
        ClientPlayerRPData.setPermissionToMaim(packet.permissionToMaim());
        ClientPlayerRPData.setGender(packet.gender());
        ClientPlayerRPData.setHeightInches(packet.heightInches());
        ClientPlayerRPData.setHeightFeet(packet.heightFeet());
        ClientPlayerRPData.setDescription(packet.description());
        ClientPlayerRPData.setName(packet.name());
        ClientPlayerRPData.setRace(packet.race());
    }
}
