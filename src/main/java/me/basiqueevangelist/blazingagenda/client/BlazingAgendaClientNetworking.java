package me.basiqueevangelist.blazingagenda.client;

import me.basiqueevangelist.blazingagenda.network.BlazingAgendaNetworking;
import me.basiqueevangelist.blazingagenda.network.HaircutS2CPacket;
import me.basiqueevangelist.blazingagenda.network.ReloadAllS2CPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class BlazingAgendaClientNetworking {
    private static final Logger LOGGER = LoggerFactory.getLogger("BlazingAgenda/ClientNetworking");

    private BlazingAgendaClientNetworking() {

    }

    public static void init() {
        BlazingAgendaNetworking.CHANNEL.registerClientbound(HaircutS2CPacket.class, (packet, access) -> {
            ClientCostumeStore.acceptPacket(packet);
        });

        BlazingAgendaNetworking.CHANNEL.registerClientbound(ReloadAllS2CPacket.class, (packet, access) -> {
            ClientCostumeStore.clear();
        });
    }
}
