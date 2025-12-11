package me.basiqueevangelist.blazingagenda.client;

import me.basiqueevangelist.blazingagenda.network.BlazingAgendaNetworking;
import me.basiqueevangelist.blazingagenda.network.HaircutS2CPacket;
import me.basiqueevangelist.blazingagenda.network.ReloadAllS2CPacket;
import me.basiqueevangelist.blazingagenda.network.ReloadS2CPacket;

public final class BlazingAgendaClientNetworking {
    private BlazingAgendaClientNetworking() {

    }

    public static void init() {
        BlazingAgendaNetworking.CHANNEL.registerClientbound(HaircutS2CPacket.class, (packet, access) -> {
            ClientCostumeStore.acceptPacket(packet);
        });

        BlazingAgendaNetworking.CHANNEL.registerClientbound(ReloadAllS2CPacket.class, (packet, access) -> {
            ClientCostumeStore.clear();
        });

        BlazingAgendaNetworking.CHANNEL.registerClientbound(ReloadS2CPacket.class, (packet, access) -> {
            ClientCostumeStore.drop(packet.id());
        });
    }
}
