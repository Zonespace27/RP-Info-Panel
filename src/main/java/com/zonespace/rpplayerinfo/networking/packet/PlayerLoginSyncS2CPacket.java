package com.zonespace.rpplayerinfo.networking.packet;

import java.util.function.Supplier;

import com.zonespace.rpplayerinfo.client.ClientPlayerRPData;
import com.zonespace.rpplayerinfo.networking.ModMessages;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class PlayerLoginSyncS2CPacket {
    public PlayerLoginSyncS2CPacket() {}

    public PlayerLoginSyncS2CPacket(FriendlyByteBuf buf) {}

    public void toBytes(FriendlyByteBuf buf) {}

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            // on the client
            ModMessages.sendToServer(new PlayerDataSyncC2SPacket(ClientPlayerRPData.getPermissionToKill(), ClientPlayerRPData.getPermissionToMaim(), ClientPlayerRPData.getGender(), ClientPlayerRPData.getHeightInches(), ClientPlayerRPData.getHeightFeet(), ClientPlayerRPData.getDescription(), ClientPlayerRPData.getName(), ClientPlayerRPData.getRace()));
        });
        return true;
    }

}

