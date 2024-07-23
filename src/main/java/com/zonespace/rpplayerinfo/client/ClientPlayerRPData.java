package com.zonespace.rpplayerinfo.client;

import com.zonespace.rpplayerinfo.data.EPlayerGender;
import com.zonespace.rpplayerinfo.data.EPlayerPermission;

public class ClientPlayerRPData {
    private static EPlayerPermission permissionToKill = EPlayerPermission.PERMISSION_ASK;
    private static EPlayerPermission permissionToMaim = EPlayerPermission.PERMISSION_ASK;

    private static EPlayerGender gender = EPlayerGender.GENDER_MALE;

    private static int heightInches = 8;
    private static int heightFeet = 5;

    private static String description = "Empty description";
    private static String name = "John Doe";
    private static String race = "Human";

    public static void setPermissionToKill(EPlayerPermission new_ptk) {
        ClientPlayerRPData.permissionToKill = new_ptk;
    }

    public static EPlayerPermission getPermissionToKill() {
        return ClientPlayerRPData.permissionToKill;
    }
    
    public static void setPermissionToMaim(EPlayerPermission new_ptm) {
        ClientPlayerRPData.permissionToMaim = new_ptm;
    }

    public static EPlayerPermission getPermissionToMaim() {
        return ClientPlayerRPData.permissionToMaim;
    }

    public static void setHeightInches(int inches) {
        ClientPlayerRPData.heightInches = inches;
    }

    public static int getHeightInches() {
        return ClientPlayerRPData.heightInches;
    }

    public static void setHeightFeet(int feet) {
        ClientPlayerRPData.heightFeet = feet;
    }

    public static int getHeightFeet() {
        return ClientPlayerRPData.heightFeet;
    }

    public static void setDescription(String desc) {
        ClientPlayerRPData.description = desc;
    }

    public static String getDescription() {
        return ClientPlayerRPData.description;
    }

    public static void setName(String str) {
        ClientPlayerRPData.name = str;
    }

    public static String getName() {
        return ClientPlayerRPData.name;
    }

    public static void setGender(EPlayerGender gender) {
        ClientPlayerRPData.gender = gender;
    }

    public static EPlayerGender getGender() {
        return gender;
    }

    public static void setRace(String race) {
        ClientPlayerRPData.race = race;
    }

    public static String getRace() {
        return race;
    }
}
