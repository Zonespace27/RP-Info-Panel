package com.zonespace.rpplayerinfo.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.zonespace.rpplayerinfo.RPPlayerInfo;
import com.zonespace.rpplayerinfo.networking.ModMessages;
import com.zonespace.rpplayerinfo.networking.packet.RoundRobinSyncC2SPacket;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;

public class KeybindHandler {
    private KeybindHandler() {}

    public static final int KEY_APOSTROPHE = 39;

    public static final KeyMapping OPEN_RP_MENU_KEYBIND = new KeyMapping("key." + RPPlayerInfo.MODID + ".open_rp_menu",
		KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM.getOrCreate(KEY_APOSTROPHE), "key.categories.misc");

    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
		event.register(OPEN_RP_MENU_KEYBIND);
	}

    public static void register() {
		IEventBus eventBus = MinecraftForge.EVENT_BUS;
		eventBus.addListener(EventPriority.HIGH, KeybindHandler::handleKeyInputEvent);
	}
    
    public static void handleKeyInputEvent(TickEvent.ClientTickEvent event) {
        if(OPEN_RP_MENU_KEYBIND.consumeClick()) {
			ModMessages.sendToServer(new RoundRobinSyncC2SPacket());
            openRPMenu();
        }
    }

    @SuppressWarnings("null")
    public static void openRPMenu() {
		Minecraft mc = Minecraft.getInstance();
		LocalPlayer player = mc.player;
		if (player == null || mc.hitResult == null) {
			return;
		}
		
        HitResult rayTrace = mc.hitResult;
        if(rayTrace.getType() != HitResult.Type.ENTITY) {
			DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientHooks.openRPInfoScreen(player, true));
			return;
        }

        EntityHitResult entityRayTraceResult = (EntityHitResult) rayTrace;
        if(!(entityRayTraceResult.getEntity() instanceof Player playerTarget)) {
            return;
        }
    
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientHooks.openRPInfoScreen(playerTarget, false));
    }
}
