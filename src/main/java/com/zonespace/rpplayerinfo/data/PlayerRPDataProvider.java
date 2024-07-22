package com.zonespace.rpplayerinfo.data;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

public class PlayerRPDataProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    // TODO possibly look into converting this into the nicer system?
    public static Capability<PlayerRPData> PLAYER_RP_DATA = CapabilityManager.get(new CapabilityToken<PlayerRPData>() { });

    private PlayerRPData rpData = null;
    private final LazyOptional<PlayerRPData> optional = LazyOptional.of(this::createPlayerRPData);

    private PlayerRPData createPlayerRPData() {
        if(this.rpData == null) {
            this.rpData = new PlayerRPData();
        }
        return this.rpData;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        createPlayerRPData().saveNBTData(nbt);
        System.out.println("Serialized");
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        createPlayerRPData().loadNBTData(nbt);
        System.out.println("Deserialized");
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == PLAYER_RP_DATA) {
            return optional.cast();
        }
        return LazyOptional.empty();
    }
    
}
