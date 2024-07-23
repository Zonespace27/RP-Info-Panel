package com.zonespace.rpplayerinfo.api;

import com.zonespace.rpplayerinfo.data.EPlayerPermission;

public class PermissionStringConverter {
    public static EPlayerPermission stringToPermissionEnum(String permissionString) {
        switch(permissionString) {
            case "Yes":
                return EPlayerPermission.PERMISSION_YES;
            case "Ask":
                return EPlayerPermission.PERMISSION_ASK;
            case "No":
            default:
                return EPlayerPermission.PERMISSION_NO;
        }
    }

    public static String permissionEnumToString(EPlayerPermission permissionEnum) {
        switch(permissionEnum) {
            case PERMISSION_YES:
                return "Yes";
            case PERMISSION_ASK:
                return "Ask";
            case PERMISSION_NO:
            default:
                return "No";
        }
    }
}
