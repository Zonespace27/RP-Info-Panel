package com.zonespace.rpplayerinfo.networking;

import java.util.UUID;

import com.zonespace.rpplayerinfo.client.ClientRPDataCache;
import com.zonespace.rpplayerinfo.data.EPlayerGender;
import com.zonespace.rpplayerinfo.data.EPlayerPermission;
import com.zonespace.rpplayerinfo.data.PlayerRPData;

public class RPDataClientSetter {
    public static void setRPData(EPlayerPermission permissionToKill, EPlayerPermission permissionToMaim, EPlayerGender gender, int heightInches, int heightFeet, String description, String name, String race, UUID playerUUID) {
        PlayerRPData rpData = ClientRPDataCache.get(playerUUID);

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
