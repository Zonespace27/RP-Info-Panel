package com.zonespace.rpplayerinfo.client;

import com.zonespace.rpplayerinfo.client.gui.RPInfoScreen;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

public class ClientHooks {
    public static void openRPInfoScreen(Player targetPlayer) {
        Minecraft.getInstance().setScreen(new RPInfoScreen(targetPlayer));
    }
}
