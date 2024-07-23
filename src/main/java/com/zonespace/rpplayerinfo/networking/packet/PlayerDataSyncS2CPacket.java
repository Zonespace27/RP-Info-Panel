package com.zonespace.rpplayerinfo.networking.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

import com.zonespace.rpplayerinfo.api.GenderStringConverter;
import com.zonespace.rpplayerinfo.api.PermissionStringConverter;
import com.zonespace.rpplayerinfo.client.ClientPlayerRPData;
import com.zonespace.rpplayerinfo.data.EPlayerGender;
import com.zonespace.rpplayerinfo.data.EPlayerPermission;

public class PlayerDataSyncS2CPacket {
    private final EPlayerPermission permissionToKill;
    private final EPlayerPermission permissionToMaim;
    private final EPlayerGender gender;
    private final int heightInches;
    private final int heightFeet;
    private final String description;
    private final String name;
    private final String race;

    public PlayerDataSyncS2CPacket(EPlayerPermission permissionToKill, EPlayerPermission permissionToMaim, EPlayerGender gender, int heightInches, int heightFeet, String description, String name, String race) {
        this.permissionToKill = permissionToKill;
        this.permissionToMaim = permissionToMaim;
        this.gender = gender;
        this.heightInches = heightInches;
        this.heightFeet = heightFeet;
        this.description = description;
        this.name = name;
        this.race = race;
    }

    public PlayerDataSyncS2CPacket(FriendlyByteBuf buf) {
        permissionToKill = PermissionStringConverter.stringToPermissionEnum(buf.readUtf());
        permissionToMaim = PermissionStringConverter.stringToPermissionEnum(buf.readUtf());
        gender = GenderStringConverter.stringToGenderEnum(buf.readUtf());
        heightInches = buf.readInt();
        heightFeet = buf.readInt();
        description = buf.readUtf();
        name = buf.readUtf();
        race = buf.readUtf();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUtf(PermissionStringConverter.permissionEnumToString(permissionToKill));
        buf.writeUtf(PermissionStringConverter.permissionEnumToString(permissionToMaim));
        buf.writeUtf(GenderStringConverter.genderEnumToString(gender));
        buf.writeInt(heightInches);
        buf.writeInt(heightFeet);
        buf.writeUtf(description);
        buf.writeUtf(name);
        buf.writeUtf(race);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            // on the client
            ClientPlayerRPData.setPermissionToKill(permissionToKill);
            ClientPlayerRPData.setPermissionToMaim(permissionToMaim);
            ClientPlayerRPData.setGender(gender);
            ClientPlayerRPData.setHeightInches(heightInches);
            ClientPlayerRPData.setHeightFeet(heightFeet);
            ClientPlayerRPData.setDescription(description);
            ClientPlayerRPData.setName(name);
            ClientPlayerRPData.setRace(race);
        });
        return true;
    }
}