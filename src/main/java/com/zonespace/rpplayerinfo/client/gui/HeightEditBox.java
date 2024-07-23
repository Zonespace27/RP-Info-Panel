package com.zonespace.rpplayerinfo.client.gui;

import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;

public class HeightEditBox extends EditBox {
    private String storedSuggestion;

    public HeightEditBox(Font p_94114_, int p_94115_, int p_94116_, int p_94117_, int p_94118_, Component p_94119_) {
        this(p_94114_, p_94115_, p_94116_, p_94117_, p_94118_, (EditBox)null, p_94119_);
   }

    public HeightEditBox(Font p_94106_, int p_94107_, int p_94108_, int p_94109_, int p_94110_, @Nullable EditBox p_94111_, Component p_94112_) {
        super(p_94106_, p_94107_, p_94108_, p_94109_, p_94110_, p_94111_, p_94112_);
    }

    public boolean charTyped(char p_94122_, int p_94123_) {
        if(!Character.isDigit(p_94122_)) {
            return false;
        }
        boolean returnValue = super.charTyped(p_94122_, p_94123_);
        if(returnValue) {
            setSuggestion("", false); // hack to make sure the suggestion disappears when there's even 1 char inputted
        }
        return returnValue;

    }

    public void setValue(@SuppressWarnings("null") String p_94145_) {
        super.setValue(p_94145_);
        if(getValue() == "") {
            setSuggestion(storedSuggestion, false);
        } else {
            setSuggestion("", false);
        }
    }

    public void deleteChars(int p_94181_) {
        super.deleteChars(p_94181_);
        if(getValue() == "") {
            setSuggestion(storedSuggestion, false);
        }
    }

    public void setSuggestion(@Nullable String suggestionString, boolean modifyStoredSuggestion) {
        if(modifyStoredSuggestion)
            this.storedSuggestion = suggestionString;
        super.setSuggestion(suggestionString);
    }
}
