package com.zonespace.rpplayerinfo.networking.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

import com.zonespace.rpplayerinfo.api.GenderStringConverter;
import com.zonespace.rpplayerinfo.api.PermissionStringConverter;
import com.zonespace.rpplayerinfo.data.EPlayerGender;
import com.zonespace.rpplayerinfo.data.EPlayerPermission;
import com.zonespace.rpplayerinfo.data.PlayerRPDataProvider;
import com.zonespace.rpplayerinfo.networking.ModMessages;

public class PlayerDataSyncC2SPacket {
    private final EPlayerPermission permissionToKill;
    private final EPlayerPermission permissionToMaim;
    private final EPlayerGender gender;
    private final int heightInches;
    private final int heightFeet;
    private final String description;
    private final String name;
    private final String race;

    public PlayerDataSyncC2SPacket(EPlayerPermission permissionToKill, EPlayerPermission permissionToMaim, EPlayerGender gender, int heightInches, int heightFeet, String description, String name, String race) {
        this.permissionToKill = permissionToKill;
        this.permissionToMaim = permissionToMaim;
        this.gender = gender;
        this.heightInches = heightInches;
        this.heightFeet = heightFeet;
        this.description = description;
        this.name = name;
        this.race = race;
    }

    public PlayerDataSyncC2SPacket(FriendlyByteBuf buf) {
        permissionToKill = PermissionStringConverter.stringToPermissionEnum(buf.readUtf());
        permissionToMaim = PermissionStringConverter.stringToPermissionEnum(buf.readUtf());
        gender = GenderStringConverter.stringToGenderEnum(buf.readUtf());
        description = buf.readUtf();
        name = buf.readUtf();
        race = buf.readUtf();
        heightInches = buf.readInt();
        heightFeet = buf.readInt();
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

    @SuppressWarnings("null")
    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            // on the server
            ServerPlayer player = context.getSender();

            player.getCapability(PlayerRPDataProvider.PLAYER_RP_DATA).ifPresent(rpData -> {
                rpData.setPermissionToKill(permissionToKill);
                rpData.setPermissionToMaim(permissionToMaim);
                rpData.setGender(gender);
                rpData.setHeightInches(heightInches);
                rpData.setHeightFeet(heightFeet);
                rpData.setDescription(description);
                rpData.setName(name);
                rpData.setRace(race);
            });

            UUID playerUUID = player.getUUID();
            if(player.getServer() == null) {
                return;
            }
            
            for(ServerPlayer serverPlayer : player.getServer().getPlayerList().getPlayers()) {
                ModMessages.sendToPlayer(new PlayerToPlayerSyncS2CPacket(
                    permissionToKill, 
                    permissionToMaim, 
                    gender, 
                    heightInches, 
                    heightFeet, 
                    description, 
                    name, 
                    race, 
                    playerUUID
                ), serverPlayer);
            }
        });
        return true;
    }
}