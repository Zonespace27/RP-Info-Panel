package com.zonespace.rpplayerinfo.networking.packet;

import java.util.UUID;
import java.util.function.Supplier;

import com.zonespace.rpplayerinfo.api.GenderStringConverter;
import com.zonespace.rpplayerinfo.api.PermissionStringConverter;
import com.zonespace.rpplayerinfo.data.EPlayerGender;
import com.zonespace.rpplayerinfo.data.EPlayerPermission;
import com.zonespace.rpplayerinfo.networking.RPDataClientSetter;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class PlayerToPlayerSyncS2CPacket {
    private final EPlayerPermission permissionToKill;
    private final EPlayerPermission permissionToMaim;
    private final EPlayerGender gender;
    private final int heightInches;
    private final int heightFeet;
    private final String description;
    private final String name;
    private final String race;

    private final UUID playerUUID;

    public PlayerToPlayerSyncS2CPacket(EPlayerPermission permissionToKill, EPlayerPermission permissionToMaim, EPlayerGender gender, int heightInches, int heightFeet, String description, String name, String race, UUID playerUUID) {
        this.permissionToKill = permissionToKill;
        this.permissionToMaim = permissionToMaim;
        this.gender = gender;
        this.heightInches = heightInches;
        this.heightFeet = heightFeet;
        this.description = description;
        this.name = name;
        this.race = race;

        this.playerUUID = playerUUID;
    }

    public PlayerToPlayerSyncS2CPacket(FriendlyByteBuf buf) {
        permissionToKill = PermissionStringConverter.stringToPermissionEnum(buf.readUtf());
        permissionToMaim = PermissionStringConverter.stringToPermissionEnum(buf.readUtf());
        gender = GenderStringConverter.stringToGenderEnum(buf.readUtf());
        description = buf.readUtf();
        name = buf.readUtf();
        race = buf.readUtf();
        heightInches = buf.readInt();
        heightFeet = buf.readInt();

        playerUUID = buf.readUUID();
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

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            // on the client
            RPDataClientSetter.setRPData(
                permissionToKill, 
                permissionToMaim, 
                gender, 
                heightInches, 
                heightFeet, 
                description, 
                name, 
                race, 
                playerUUID
            );
        });
        return true;
    }
}
