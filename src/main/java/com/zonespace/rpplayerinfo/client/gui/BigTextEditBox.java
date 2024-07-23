package com.zonespace.rpplayerinfo.client.gui;

import javax.annotation.Nullable;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

public class BigTextEditBox extends EditBox {

    public BigTextEditBox(Font p_94114_, int p_94115_, int p_94116_, int p_94117_, int p_94118_, Component p_94119_) {
        this(p_94114_, p_94115_, p_94116_, p_94117_, p_94118_, (EditBox)null, p_94119_);
   }

    public BigTextEditBox(Font p_94106_, int p_94107_, int p_94108_, int p_94109_, int p_94110_, @Nullable EditBox p_94111_, Component p_94112_) {
        super(p_94106_, p_94107_, p_94108_, p_94109_, p_94110_, p_94111_, p_94112_);
    }

    // Ideally this should allow for a player to input a large amt of info into a chatbox and it should newline when one fills and etc. but it's beyond my knowledge atm
}
