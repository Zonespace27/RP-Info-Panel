package com.zonespace.rpplayerinfo;

import com.zonespace.rpplayerinfo.client.KeybindHandler;
import com.zonespace.rpplayerinfo.data.PlayerRPData;
import com.zonespace.rpplayerinfo.data.PlayerRPDataProvider;
import com.zonespace.rpplayerinfo.networking.ModMessages;
import com.zonespace.rpplayerinfo.networking.packet.PlayerDataSyncS2CPacket;
import com.zonespace.rpplayerinfo.networking.packet.PlayerLoginSyncS2CPacket;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(RPPlayerInfo.MODID)
public class RPPlayerInfo
{
    public static final String MODID = "rpplayerinfo";

    public RPPlayerInfo()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        MinecraftForge.EVENT_BUS.register(this);
        
        if (FMLEnvironment.dist == Dist.CLIENT) {
            modEventBus.addListener(KeybindHandler::registerKeyMappings); // not moving this one to ClientModEvents bc of the client check
        }
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {  
        @SubscribeEvent
        public static void clientSetup(FMLClientSetupEvent event) {
            KeybindHandler.register();
        }
    }

    @Mod.EventBusSubscriber(modid = RPPlayerInfo.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class CommonModEvents {

        @SubscribeEvent
        public static void commonSetup(final FMLCommonSetupEvent event)
        {
            event.enqueueWork(() -> {
                ModMessages.register();
            });
        }

    }

    @Mod.EventBusSubscriber(modid = MODID)
    public static class ModEvents 
    {
        @SubscribeEvent
        public static void onAttachCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event) {
            if(event.getObject() instanceof Player) {
                Player ourPlayer = (Player)event.getObject();
                if(!ourPlayer.getCapability(PlayerRPDataProvider.PLAYER_RP_DATA).isPresent()) {
                    event.addCapability(new ResourceLocation(MODID, "properties"), new PlayerRPDataProvider());
                    /*if(ourPlayer instanceof ServerPlayer) {
                        ServerPlayer ourServerPlayer = (ServerPlayer)ourPlayer;
                        if(ourServerPlayer.connection != null) {
                            ModMessages.sendToPlayer(new PlayerLoginSyncS2CPacket(), ourServerPlayer);
                        }
                    }*/
                }
            }
        }

        @SubscribeEvent
        public static void onPlayerCloned(PlayerEvent.Clone event) {
            if(event.isWasDeath()) {
                event.getOriginal().reviveCaps();
                event.getOriginal().getCapability(PlayerRPDataProvider.PLAYER_RP_DATA).resolve().ifPresent(oldStore -> {
                    event.getEntity().getCapability(PlayerRPDataProvider.PLAYER_RP_DATA).resolve().ifPresent(newStore -> {
                        newStore.copyFrom(oldStore);
                        MinecraftServer theServer = event.getEntity().getLevel().getServer();
                        if(theServer != null) { 
                            ModMessages.sendToPlayer(new PlayerLoginSyncS2CPacket(), (ServerPlayer)event.getEntity());
                            ModMessages.sendToPlayer(new PlayerLoginSyncS2CPacket(), (ServerPlayer)event.getOriginal());
                        }
                    });
                });
            }
        }

        @SubscribeEvent
        public static void onPlayerChangeGameMode(PlayerEvent.PlayerChangeGameModeEvent event) {
            /*if(event.getCurrentGameMode() == GameType.SPECTATOR) { 
                event.getEntity().getCapability(PlayerRPDataProvider.PLAYER_RP_DATA).resolve().ifPresent(newStore -> {
                    MinecraftServer theServer = event.getEntity().getLevel().getServer();
                    if(theServer != null) { 
                        ModMessages.sendToPlayer(new PlayerLoginSyncS2CPacket(), (ServerPlayer)event.getEntity());
                    }
                });
            }*/
        }
        
        @SubscribeEvent
        public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
            event.register(PlayerRPDataProvider.class);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void playerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        ServerPlayer player = (ServerPlayer)event.getEntity();
        PlayerRPData rpData = player.getCapability(PlayerRPDataProvider.PLAYER_RP_DATA).resolve().get();
        ModMessages.sendToPlayer(new PlayerDataSyncS2CPacket(rpData.getPermissionToKill(), rpData.getPermissionToMaim(), rpData.getGender(), rpData.getHeightInches(), rpData.getHeightFeet(), rpData.getDescription(), rpData.getName(), rpData.getRace()), player);
        for(ServerPlayer serverPlayer : player.getLevel().getServer().getPlayerList().getPlayers()) {
            ModMessages.sendToPlayer(new PlayerLoginSyncS2CPacket(), serverPlayer);
        }
    }
}
