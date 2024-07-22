package com.zonespace.rpplayerinfo.networking.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

import com.zonespace.rpplayerinfo.api.PermissionStringConverter;
import com.zonespace.rpplayerinfo.client.ClientPlayerRPData;
import com.zonespace.rpplayerinfo.data.EPlayerPermission;

public class PlayerDataSyncS2CPacket {
    private final EPlayerPermission permissionToKill;
    private final EPlayerPermission permissionToMaim;
    private final int heightInches;
    private final int heightFeet;
    private final String description;
    private final String name;

    public PlayerDataSyncS2CPacket(EPlayerPermission permissionToKill, EPlayerPermission permissionToMaim, int heightInches, int heightFeet, String description, String name) {
        this.permissionToKill = permissionToKill;
        this.permissionToMaim = permissionToMaim;
        this.heightInches = heightInches;
        this.heightFeet = heightFeet;
        this.description = description;
        this.name = name;
    }

    public PlayerDataSyncS2CPacket(FriendlyByteBuf buf) {
        permissionToKill = PermissionStringConverter.stringToPermissionEnum(buf.readUtf());
        permissionToMaim = PermissionStringConverter.stringToPermissionEnum(buf.readUtf());
        heightInches = buf.readInt();
        heightFeet = buf.readInt();
        description = buf.readUtf();
        name = buf.readUtf();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUtf(PermissionStringConverter.permissionEnumToString(permissionToKill));
        buf.writeUtf(PermissionStringConverter.permissionEnumToString(permissionToMaim));
        buf.writeInt(heightInches);
        buf.writeInt(heightFeet);
        buf.writeUtf(description);
        buf.writeUtf(name);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            // on the client
            ClientPlayerRPData.setPermissionToKill(permissionToKill);
            ClientPlayerRPData.setPermissionToMaim(permissionToMaim);
            ClientPlayerRPData.setHeightInches(heightInches);
            ClientPlayerRPData.setHeightFeet(heightFeet);
            ClientPlayerRPData.setDescription(description);
            ClientPlayerRPData.setName(name);
        });
        return true;
    }
}