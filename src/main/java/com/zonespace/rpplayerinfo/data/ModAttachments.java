package com.zonespace.rpplayerinfo.data;

import java.util.function.Supplier;

import com.zonespace.rpplayerinfo.RPPlayerInfo;

import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class ModAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
        DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, RPPlayerInfo.MODID);

    public static final Supplier<AttachmentType<PlayerRPData>> PLAYER_RP_DATA =
        ATTACHMENT_TYPES.register("player_rp_data", () -> AttachmentType.serializable(PlayerRPData::new).copyOnDeath().build());
}
