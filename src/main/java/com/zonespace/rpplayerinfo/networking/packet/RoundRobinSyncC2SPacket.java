package com.zonespace.rpplayerinfo.networking.packet;

import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

import com.zonespace.rpplayerinfo.client.ClientPlayerRPData;
import com.zonespace.rpplayerinfo.data.EPlayerPermission;
import com.zonespace.rpplayerinfo.data.PlayerRPData;
import com.zonespace.rpplayerinfo.data.PlayerRPDataProvider;
import com.zonespace.rpplayerinfo.networking.ModMessages;

public class RoundRobinSyncC2SPacket {
    private static final String MESSAGE_DRINK_WATER = "message.tutorialmod.drink_water";
    private static final String MESSAGE_NO_WATER = "message.tutorialmod.no_water";

    public RoundRobinSyncC2SPacket() {

    }

    public RoundRobinSyncC2SPacket(FriendlyByteBuf buf) {

    }

    public void toBytes(FriendlyByteBuf buf) {

    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            // HERE WE ARE ON THE SERVER!
            ServerPlayer player = context.getSender();
            //ServerLevel level = player.getLevel();

            /*EPlayerPermission permissionToKill = EPlayerPermission.PERMISSION_ASK;
            EPlayerPermission permissionToMaim = EPlayerPermission.PERMISSION_ASK;
            int heightInches = 0;
            int heightFeet = 0;
            String description = "Empty packet description";*/

            /*PlayerRPData rpdata = player.getCapability(PlayerRPDataProvider.PLAYER_RP_DATA).resolve().get();

            if(rpdata == null) {
                return;
            }

            permissionToKill = rpdata.getPermissionToKill();
            permissionToMaim = rpdata.getPermissionToMaim();
            heightInches = rpdata.getHeightInches();
            heightFeet = rpdata.getHeightFeet();
            description = rpdata.getDescription();*/



            /*player.getCapability(PlayerRPDataProvider.PLAYER_RP_DATA).ifPresent(rpdata -> {
                EPlayerPermission permissionToKill = rpdata.getPermissionToKill();
                EPlayerPermission permissionToMaim = rpdata.getPermissionToMaim();
                int heightInches = rpdata.getHeightInches();
                int heightFeet = rpdata.getHeightFeet();
                String description = rpdata.getDescription();
                String name = rpdata.getName();
                ModMessages.sendToPlayer(new PlayerDataSyncS2CPacket(permissionToKill, permissionToMaim, heightInches, heightFeet, description, name), player);
            });*/

            EPlayerPermission permissionToKill = ClientPlayerRPData.getPermissionToKill();
            EPlayerPermission permissionToMaim = ClientPlayerRPData.getPermissionToMaim();
            int heightInches = ClientPlayerRPData.getHeightInches();
            int heightFeet = ClientPlayerRPData.getHeightFeet();
            String description = ClientPlayerRPData.getDescription();
            String name = ClientPlayerRPData.getName();
            ModMessages.sendToPlayer(new PlayerDataSyncS2CPacket(permissionToKill, permissionToMaim, heightInches, heightFeet, description, name), player);

            //ModMessages.sendToPlayer(new PlayerDataSyncS2CPacket(permissionToKill, permissionToMaim, heightInches, heightFeet, description), player);
        });
        return true;
    }

}
