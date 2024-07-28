package com.zonespace.rpplayerinfo.data;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

public class PlayerRPDataProvider implements ICapabilitySerializable<CompoundTag> {
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
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        createPlayerRPData().loadNBTData(nbt);
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == PLAYER_RP_DATA) {
            return optional.cast();
        }
        return LazyOptional.empty();
    }
    
}
