package me.basiqueevangelist.blazingagenda.network;

import com.mojang.authlib.GameProfile;
import io.wispforest.owo.network.OwoNetChannel;
import me.basiqueevangelist.blazingagenda.BlazingAgenda;
import me.basiqueevangelist.blazingagenda.haircut.HaircutsState;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

public class BlazingAgendaNetworking {
    public static final OwoNetChannel CHANNEL = OwoNetChannel.create(BlazingAgenda.id("channel"));

    public static void init() {
        CHANNEL.registerServerbound(RequestHaircutC2SPacket.class, (packet, access) -> {
            // todo: ratelimiting

            HaircutsState state = HaircutsState.get(access.runtime());

            var haircut = state.haircuts().get(packet.haircutId());

            if (haircut == null) return;

            byte[] data;

            try {
                data = Files.readAllBytes(state.resolve(haircut));
            } catch (IOException e) {
                // todo: make this better uwu
                throw new RuntimeException("explosion", e);
            }

            Optional<String> ownerName = access.runtime().getUserCache().getByUuid(haircut.ownerId()).map(GameProfile::getName);

            CHANNEL.serverHandle(access.player()).send(new HaircutS2CPacket(
                haircut.id(),
                haircut.ownerId(),
                ownerName,
                haircut.name(),
                data
            ));
        });

        CHANNEL.registerClientboundDeferred(HaircutS2CPacket.class);
        CHANNEL.registerClientboundDeferred(ReloadAllS2CPacket.class);
    }
}
