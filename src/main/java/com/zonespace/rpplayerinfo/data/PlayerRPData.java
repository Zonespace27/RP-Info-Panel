package com.zonespace.rpplayerinfo.data;

import com.zonespace.rpplayerinfo.api.PermissionStringConverter;
import net.minecraft.nbt.CompoundTag;

public class PlayerRPData {
    private EPlayerPermission permissionToKill = EPlayerPermission.PERMISSION_ASK;
    private EPlayerPermission permissionToMaim = EPlayerPermission.PERMISSION_ASK;
    private int heightInches = 8;
    private int heightFeet = 5;
    private String description = "Empty description";
    private String name = "John Doe";

    public void setPermissionToKill(EPlayerPermission new_ptk) {
        this.permissionToKill = new_ptk;
    }

    public EPlayerPermission getPermissionToKill() {
        return permissionToKill;
    }
    
    public void setPermissionToMaim(EPlayerPermission new_ptm) {
        permissionToMaim = new_ptm;
    }

    public EPlayerPermission getPermissionToMaim() {
        return permissionToMaim;
    }

    public void setHeightInches(int inches) {
        heightInches = inches;
    }

    public int getHeightInches() {
        return heightInches;
    }

    public void setHeightFeet(int feet) {
        heightFeet = feet;
    }

    public int getHeightFeet() {
        return heightFeet;
    }

    public void setDescription(String desc) {
        description = desc;
    }

    public String getDescription() {
        return description;
    }

    public void setName(String str) {
        name = str;
    }

    public String getName() {
        return name;
    }

    public void copyFrom(PlayerRPData source) {
        this.permissionToKill = source.permissionToKill;
        this.permissionToMaim = source.permissionToMaim;
        this.heightInches = source.heightInches;
        this.heightFeet = source.heightFeet;
        this.description = source.description;
        this.name = source.name;
    }

    public void saveNBTData(CompoundTag nbt) {
        nbt.putString("permissionToKill", PermissionStringConverter.permissionEnumToString(this.permissionToKill));
        nbt.putString("permissionToMaim", PermissionStringConverter.permissionEnumToString(permissionToMaim));
        nbt.putInt("heightInches", heightInches);
        nbt.putInt("heightFeet", heightFeet);
        nbt.putString("description", description);
        nbt.putString("name", name);
    }

    public void loadNBTData(CompoundTag nbt) {
        permissionToKill = PermissionStringConverter.stringToPermissionEnum(nbt.getString("permissionToKill"));
        permissionToMaim = PermissionStringConverter.stringToPermissionEnum(nbt.getString("permissionToMaim"));
        heightInches = nbt.getInt("heightInches");
        heightFeet = nbt.getInt("heightFeet");
        description = nbt.getString("description");
        name = nbt.getString("name");
    }
}
