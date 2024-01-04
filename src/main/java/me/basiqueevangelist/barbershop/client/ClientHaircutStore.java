package me.basiqueevangelist.barbershop.client;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import me.basiqueevangelist.barbershop.network.HaircutS2CPacket;
import me.basiqueevangelist.barbershop.network.RequestHaircutC2SPacket;
import me.basiqueevangelist.barbershop.network.TheBarbershopNetworking;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public final class ClientHaircutStore {
    private static final Cache<UUID, Entry> CACHE = CacheBuilder.newBuilder()
        .expireAfterAccess(5, TimeUnit.SECONDS)
        .<UUID, Entry>removalListener(notification -> {
            if (notification.getValue() != null) notification.getValue().destroy();
        })
        .build();

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            CACHE.cleanUp();
        });
    }

    public static @Nullable Entry get(UUID id) {
        var entry = CACHE.getIfPresent(id);

        if (entry == null) {
            entry = new Entry();
            CACHE.put(id, entry);

            TheBarbershopNetworking.CHANNEL.clientHandle().send(new RequestHaircutC2SPacket(id));

            return null;
        } else if (!entry.filledIn) {
            return null;
        }

        return entry;
    }

    static void acceptPacket(HaircutS2CPacket packet) {
        try {
            var entry = CACHE.get(packet.id(), Entry::new);

            entry.id = packet.id();
            entry.ownerId = packet.ownerId();
            entry.ownerName = packet.ownerName().orElse(null);
            entry.name = packet.name();
            entry.data = packet.data();
            entry.filledIn = true;
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public static class Entry {
        private boolean filledIn = false;
        private UUID id;
        private UUID ownerId;
        private String ownerName;
        private String name;
        private byte[] data;
        private DownloadedTexture tx;

        private Entry() {

        }

        public @NotNull UUID id() {
            return id;
        }

        public @NotNull UUID ownerId() {
            return ownerId;
        }

        public @Nullable String ownerName() {
            return ownerName;
        }

        public @NotNull String name() {
            return name;
        }

        public @NotNull DownloadedTexture texture() {
            if (tx == null) {
                tx = new DownloadedTexture(data);
            }

            return tx;
        }

        private void destroy() {
            data = null;

            if (tx != null) {
                tx.close();
                tx = null;
            }
        }
    }
}
