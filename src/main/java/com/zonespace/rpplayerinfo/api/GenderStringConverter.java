package com.zonespace.rpplayerinfo.api;

import com.zonespace.rpplayerinfo.data.EPlayerGender;

public class GenderStringConverter {
    public static EPlayerGender stringToGenderEnum(String genderString) {
        switch(genderString) {
            case "Male":
                return EPlayerGender.GENDER_MALE;
            case "Female":
                return EPlayerGender.GENDER_FEMALE;
            case "Neuter":
            default:
                return EPlayerGender.GENDER_NEUTER;
        }
    }

    public static String genderEnumToString(EPlayerGender genderEnum) {
        switch(genderEnum) {
            case GENDER_MALE:
                return "Male";
            case GENDER_FEMALE:
                return "Female";
            case GENDER_NEUTER:
            default:
                return "Neuter";
        }
    }
}
