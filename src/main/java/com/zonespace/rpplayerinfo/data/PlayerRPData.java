package com.zonespace.rpplayerinfo.data;

import com.zonespace.rpplayerinfo.api.GenderStringConverter;
import com.zonespace.rpplayerinfo.api.PermissionStringConverter;

import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;

public class PlayerRPData implements ValueIOSerializable {
    private EPlayerPermission permissionToKill = EPlayerPermission.PERMISSION_ASK;
    private EPlayerPermission permissionToMaim = EPlayerPermission.PERMISSION_ASK;
    private EPlayerGender gender = EPlayerGender.GENDER_MALE;

    private int heightInches = 8;
    private int heightFeet = 5;

    private String description = "Empty description";
    private String name = "John Doe";
    private String race = "Human";

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

    public void setGender(EPlayerGender newGender) {
        gender = newGender;
    }

    public EPlayerGender getGender() {
        return gender;
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

    public void setRace(String str) {
        race = str;
    }

    public String getRace() {
        return race;
    }

    public void copyFrom(PlayerRPData source) {
        this.permissionToKill = source.permissionToKill;
        this.permissionToMaim = source.permissionToMaim;
        this.gender = source.gender;
        this.heightInches = source.heightInches;
        this.heightFeet = source.heightFeet;
        this.description = source.description;
        this.name = source.name;
        this.race = source.race;
    }

    @Override
    public void serialize(ValueOutput output) {
        output.putString("permissionToKill", PermissionStringConverter.permissionEnumToString(permissionToKill));
        output.putString("permissionToMaim", PermissionStringConverter.permissionEnumToString(permissionToMaim));
        output.putString("gender", GenderStringConverter.genderEnumToString(gender));
        output.putInt("heightInches", heightInches);
        output.putInt("heightFeet", heightFeet);
        output.putString("description", description);
        output.putString("name", name);
        output.putString("race", race);
    }

    @Override
    public void deserialize(ValueInput input) {
        permissionToKill = PermissionStringConverter.stringToPermissionEnum(input.getStringOr("permissionToKill", "Ask"));
        permissionToMaim = PermissionStringConverter.stringToPermissionEnum(input.getStringOr("permissionToMaim", "Ask"));
        gender = GenderStringConverter.stringToGenderEnum(input.getStringOr("gender", "Male"));
        heightInches = input.getIntOr("heightInches", 8);
        heightFeet = input.getIntOr("heightFeet", 5);
        description = input.getStringOr("description", "Empty description");
        name = input.getStringOr("name", "John Doe");
        race = input.getStringOr("race", "Human");
    }
}
