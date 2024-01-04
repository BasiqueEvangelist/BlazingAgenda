package me.basiqueevangelist.barbershop.client;

import me.basiqueevangelist.barbershop.network.HaircutS2CPacket;
import me.basiqueevangelist.barbershop.network.TheBarbershopNetworking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class TheBarbershopClientNetworking {
    private static final Logger LOGGER = LoggerFactory.getLogger("TheBarbershop/ClientNetworking");

    private TheBarbershopClientNetworking() {

    }

    public static void init() {
        TheBarbershopNetworking.CHANNEL.registerClientbound(HaircutS2CPacket.class, (packet, access) -> {
            ClientHaircutStore.acceptPacket(packet);
        });
    }
}
