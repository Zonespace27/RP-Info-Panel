package com.zonespace.rpplayerinfo.networking.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

import com.zonespace.rpplayerinfo.data.EPlayerGender;
import com.zonespace.rpplayerinfo.data.EPlayerPermission;
import com.zonespace.rpplayerinfo.data.PlayerRPData;
import com.zonespace.rpplayerinfo.data.PlayerRPDataProvider;
import com.zonespace.rpplayerinfo.networking.ModMessages;

public class RoundRobinSyncC2SPacket {

    public RoundRobinSyncC2SPacket() {}

    public RoundRobinSyncC2SPacket(FriendlyByteBuf buf) {}

    public void toBytes(FriendlyByteBuf buf) {}

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            // on the server
            ServerPlayer player = context.getSender();

            PlayerRPData rpData = player.getCapability(PlayerRPDataProvider.PLAYER_RP_DATA).resolve().get();

            EPlayerPermission permissionToKill = rpData.getPermissionToKill();
            EPlayerPermission permissionToMaim = rpData.getPermissionToMaim();
            EPlayerGender gender = rpData.getGender();
            int heightInches = rpData.getHeightInches();
            int heightFeet = rpData.getHeightFeet();
            String description = rpData.getDescription();
            String name = rpData.getName();
            String race = rpData.getRace();
            ModMessages.sendToPlayer(new PlayerDataSyncS2CPacket(permissionToKill, permissionToMaim, gender, heightInches, heightFeet, description, name, race), player);
        });
        return true;
    }

}
