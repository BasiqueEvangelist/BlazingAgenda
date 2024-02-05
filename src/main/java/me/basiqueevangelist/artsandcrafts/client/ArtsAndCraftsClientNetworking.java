package me.basiqueevangelist.artsandcrafts.client;

import me.basiqueevangelist.artsandcrafts.network.ArtsAndCraftsNetworking;
import me.basiqueevangelist.artsandcrafts.network.HaircutS2CPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ArtsAndCraftsClientNetworking {
    private static final Logger LOGGER = LoggerFactory.getLogger("ArtsAndCrafts/ClientNetworking");

    private ArtsAndCraftsClientNetworking() {

    }

    public static void init() {
        ArtsAndCraftsNetworking.CHANNEL.registerClientbound(HaircutS2CPacket.class, (packet, access) -> {
            ClientHaircutStore.acceptPacket(packet);
        });
    }
}
