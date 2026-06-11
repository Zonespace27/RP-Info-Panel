package com.zonespace.rpplayerinfo.networking;

import java.util.UUID;

import com.zonespace.rpplayerinfo.data.EPlayerGender;
import com.zonespace.rpplayerinfo.data.EPlayerPermission;
import com.zonespace.rpplayerinfo.data.ModAttachments;
import com.zonespace.rpplayerinfo.data.PlayerRPData;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class RPDataClientSetter {
    public static void setRPData(EPlayerPermission permissionToKill, EPlayerPermission permissionToMaim, EPlayerGender gender, int heightInches, int heightFeet, String description, String name, String race, UUID playerUUID) {
        Level level = Minecraft.getInstance().level;
        if(level == null) {
            return;
        }

        Player targetPlayer = level.getPlayerByUUID(playerUUID);
        if(targetPlayer == null) {
            return;
        }

        PlayerRPData rpData = targetPlayer.getData(ModAttachments.PLAYER_RP_DATA);

        rpData.setPermissionToKill(permissionToKill);
        rpData.setPermissionToMaim(permissionToMaim);
        rpData.setGender(gender);
        rpData.setHeightInches(heightInches);
        rpData.setHeightFeet(heightFeet);
        rpData.setDescription(description);
        rpData.setName(name);
        rpData.setRace(race);
    }
}
