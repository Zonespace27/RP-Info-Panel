package com.zonespace.rpplayerinfo.networking.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

import com.zonespace.rpplayerinfo.api.PermissionStringConverter;
import com.zonespace.rpplayerinfo.data.EPlayerPermission;
import com.zonespace.rpplayerinfo.data.PlayerRPDataProvider;

public class PlayerDataSyncC2SPacket {
    private final EPlayerPermission permissionToKill;
    private final EPlayerPermission permissionToMaim;
    private final int heightInches;
    private final int heightFeet;
    private final String description;

    public PlayerDataSyncC2SPacket(EPlayerPermission permissionToKill, EPlayerPermission permissionToMaim, int heightInches, int heightFeet, String description) {
        this.permissionToKill = permissionToKill;
        this.permissionToMaim = permissionToMaim;
        this.heightInches = heightInches;
        this.heightFeet = heightFeet;
        this.description = description;
    }

    public PlayerDataSyncC2SPacket(FriendlyByteBuf buf) {
        permissionToKill = PermissionStringConverter.stringToPermissionEnum(buf.readUtf());
        permissionToMaim = PermissionStringConverter.stringToPermissionEnum(buf.readUtf());
        heightInches = buf.readInt();
        heightFeet = buf.readInt();
        description = buf.readUtf();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUtf(PermissionStringConverter.permissionEnumToString(permissionToKill));
        buf.writeUtf(PermissionStringConverter.permissionEnumToString(permissionToMaim));
        buf.writeInt(heightInches);
        buf.writeInt(heightFeet);
        buf.writeUtf(description);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            // on the server
            ServerPlayer player = context.getSender();

            player.getCapability(PlayerRPDataProvider.PLAYER_RP_DATA).ifPresent(rpData -> {
                rpData.setPermissionToKill(permissionToKill);
                rpData.setPermissionToMaim(permissionToMaim);
                rpData.setHeightInches(heightInches);
                rpData.setHeightFeet(heightFeet);
                rpData.setDescription(description);
            });
        });
        return true;
    }
}