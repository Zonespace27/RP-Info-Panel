package com.zonespace.rpplayerinfo.networking.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

import com.zonespace.rpplayerinfo.client.ClientPlayerRPData;
import com.zonespace.rpplayerinfo.data.EPlayerGender;
import com.zonespace.rpplayerinfo.data.EPlayerPermission;
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

            EPlayerPermission permissionToKill = ClientPlayerRPData.getPermissionToKill();
            EPlayerPermission permissionToMaim = ClientPlayerRPData.getPermissionToMaim();
            EPlayerGender gender = ClientPlayerRPData.getGender();
            int heightInches = ClientPlayerRPData.getHeightInches();
            int heightFeet = ClientPlayerRPData.getHeightFeet();
            String description = ClientPlayerRPData.getDescription();
            String name = ClientPlayerRPData.getName();
            String race = ClientPlayerRPData.getRace();
            ModMessages.sendToPlayer(new PlayerDataSyncS2CPacket(permissionToKill, permissionToMaim, gender, heightInches, heightFeet, description, name, race), player);
        });
        return true;
    }

}
