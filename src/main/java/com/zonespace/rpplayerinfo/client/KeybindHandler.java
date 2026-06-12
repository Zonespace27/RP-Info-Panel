package com.zonespace.rpplayerinfo.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.zonespace.rpplayerinfo.RPPlayerInfo;
import com.zonespace.rpplayerinfo.networking.packet.RoundRobinSyncC2SPacket;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.common.NeoForge;

public class KeybindHandler {
    private KeybindHandler() {}

    public static final int KEY_APOSTROPHE = 39;

    public static final KeyMapping OPEN_RP_MENU_KEYBIND = new KeyMapping("key." + RPPlayerInfo.MODID + ".open_rp_menu",
		KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM.getOrCreate(KEY_APOSTROPHE), KeyMapping.Category.MISC);

    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
		event.register(OPEN_RP_MENU_KEYBIND);
	}

    public static void register() {
		NeoForge.EVENT_BUS.addListener(EventPriority.HIGH, KeybindHandler::handleKeyInputEvent);
	}

    public static void handleKeyInputEvent(ClientTickEvent.Post event) {
        if(OPEN_RP_MENU_KEYBIND.consumeClick()) {
			ClientPacketDistributor.sendToServer(new RoundRobinSyncC2SPacket());
            openRPMenu();
        }
    }

    public static void openRPMenu() {
		Minecraft mc = Minecraft.getInstance();
		LocalPlayer player = mc.player;
		if (player == null || mc.hitResult == null) {
			return;
		}

        HitResult rayTrace = mc.hitResult;
        if(rayTrace.getType() != HitResult.Type.ENTITY) {
			ClientHooks.openRPInfoScreen(player, true);
			return;
        }

        EntityHitResult entityRayTraceResult = (EntityHitResult) rayTrace;
        if(!(entityRayTraceResult.getEntity() instanceof Player playerTarget)) {
            return;
        }

        ClientHooks.openRPInfoScreen(playerTarget, false);
    }
}
