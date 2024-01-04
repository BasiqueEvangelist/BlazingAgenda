package me.basiqueevangelist.barbershop.haircut;

import me.lucko.fabric.api.permissions.v0.Options;
import net.minecraft.server.network.ServerPlayerEntity;

public final class HaircutLimits {
    private HaircutLimits() {

    }

    public static int maxTotalSize(ServerPlayerEntity player) {
        return Options.get(player, "thebarbershop.maxTotalSize", 1024 * 1024, Integer::parseInt);
    }
}
