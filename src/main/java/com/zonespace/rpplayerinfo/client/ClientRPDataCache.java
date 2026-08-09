package com.zonespace.rpplayerinfo.client;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.zonespace.rpplayerinfo.data.PlayerRPData;

/**
 * Client-side store of other players' RP data, keyed by UUID rather than
 * attached to player entities. Remote player entities don't exist on the
 * client until they're in tracking range, so entity capabilities would
 * silently drop data synced during login.
 */
public class ClientRPDataCache {
    private static final Map<UUID, PlayerRPData> CACHE = new ConcurrentHashMap<>();

    public static PlayerRPData get(UUID playerUUID) {
        return CACHE.computeIfAbsent(playerUUID, uuid -> new PlayerRPData());
    }

    public static void clear() {
        CACHE.clear();
    }
}
