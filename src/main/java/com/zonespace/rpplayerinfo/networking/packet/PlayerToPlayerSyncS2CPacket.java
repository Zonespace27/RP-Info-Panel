package com.zonespace.rpplayerinfo.networking.packet;

import java.util.UUID;
import java.util.function.Supplier;

import com.zonespace.rpplayerinfo.api.GenderStringConverter;
import com.zonespace.rpplayerinfo.api.PermissionStringConverter;
import com.zonespace.rpplayerinfo.data.EPlayerGender;
import com.zonespace.rpplayerinfo.data.EPlayerPermission;
import com.zonespace.rpplayerinfo.data.PlayerRPData;
import com.zonespace.rpplayerinfo.data.PlayerRPDataProvider;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
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
            @SuppressWarnings("resource") // this is allegedly not an issue so we suppress it
            Level level = Minecraft.getInstance().level;
            if(level == null) {
                return;
            }

            Player targetPlayer = level.getPlayerByUUID(playerUUID);
            if(targetPlayer == null) {
                return;
            }

            PlayerRPData rpData = targetPlayer.getCapability(PlayerRPDataProvider.PLAYER_RP_DATA).resolve().get();

            rpData.setPermissionToKill(permissionToKill);
            rpData.setPermissionToMaim(permissionToMaim);
            rpData.setGender(gender);
            rpData.setHeightInches(heightInches);
            rpData.setHeightFeet(heightFeet);
            rpData.setDescription(description);
            rpData.setName(name);
            rpData.setRace(race);
        });
        return true;
    }
}
