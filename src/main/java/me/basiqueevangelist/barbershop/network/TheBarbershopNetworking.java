package me.basiqueevangelist.barbershop.network;

import com.mojang.authlib.GameProfile;
import io.wispforest.owo.network.OwoNetChannel;
import me.basiqueevangelist.barbershop.TheBarbershop;
import me.basiqueevangelist.barbershop.haircut.HaircutLimits;
import me.basiqueevangelist.barbershop.haircut.HaircutsState;
import me.basiqueevangelist.barbershop.screen.BarberStationScreenHandler;
import net.minecraft.text.Text;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TheBarbershopNetworking {
    public static final OwoNetChannel CHANNEL = OwoNetChannel.create(TheBarbershop.id("channel"));

    public static void init() {
        CHANNEL.registerServerbound(RequestHaircutC2SPacket.class, (packet, access) -> {
            // todo: ratelimiting

            HaircutsState state = HaircutsState.get(access.runtime());

            var haircut = state.haircuts().get(packet.haircutId());

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
                haircut.targetTexture(),
                data
            ));
        });

        CHANNEL.registerClientboundDeferred(HaircutS2CPacket.class);
    }
}
