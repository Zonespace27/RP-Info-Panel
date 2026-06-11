package com.zonespace.rpplayerinfo;

import com.zonespace.rpplayerinfo.client.KeybindHandler;
import com.zonespace.rpplayerinfo.data.ModAttachments;
import com.zonespace.rpplayerinfo.data.PlayerRPData;
import com.zonespace.rpplayerinfo.networking.ModMessages;
import com.zonespace.rpplayerinfo.networking.packet.PlayerDataSyncS2CPacket;
import com.zonespace.rpplayerinfo.networking.packet.PlayerLoginSyncS2CPacket;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

@Mod(RPPlayerInfo.MODID)
public class RPPlayerInfo
{
    public static final String MODID = "rpplayerinfo";

    public RPPlayerInfo(IEventBus modEventBus)
    {
        ModAttachments.ATTACHMENT_TYPES.register(modEventBus);

        NeoForge.EVENT_BUS.register(this);
    }

    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void registerKeyMappings(net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent event) {
            KeybindHandler.registerKeyMappings(event);
        }

        @SubscribeEvent
        public static void clientSetup(FMLClientSetupEvent event) {
            KeybindHandler.register();
        }
    }

    @EventBusSubscriber(modid = RPPlayerInfo.MODID, bus = EventBusSubscriber.Bus.MOD)
    public static class CommonModEvents {

        @SubscribeEvent
        public static void registerPayloads(final RegisterPayloadHandlersEvent event)
        {
            ModMessages.register(event);
        }

    }

    @EventBusSubscriber(modid = MODID)
    public static class ModEvents
    {
        @SubscribeEvent
        public static void onPlayerCloned(PlayerEvent.Clone event) {
            // The RP data attachment itself is copied by copyOnDeath(); we just need to
            // ask the respawned player's client to re-upload so everyone gets re-synced.
            if(event.isWasDeath() && event.getEntity() instanceof ServerPlayer serverPlayer) {
                ModMessages.sendToPlayer(new PlayerLoginSyncS2CPacket(), serverPlayer);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void playerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if(!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        PlayerRPData rpData = player.getData(ModAttachments.PLAYER_RP_DATA);
        ModMessages.sendToPlayer(new PlayerDataSyncS2CPacket(rpData.getPermissionToKill(), rpData.getPermissionToMaim(), rpData.getGender(), rpData.getHeightInches(), rpData.getHeightFeet(), rpData.getDescription(), rpData.getName(), rpData.getRace()), player);
        for(ServerPlayer serverPlayer : player.getServer().getPlayerList().getPlayers()) {
            ModMessages.sendToPlayer(new PlayerLoginSyncS2CPacket(), serverPlayer);
        }
    }
}
