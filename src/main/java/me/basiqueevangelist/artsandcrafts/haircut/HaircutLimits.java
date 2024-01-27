package me.basiqueevangelist.artsandcrafts.haircut;

import me.lucko.fabric.api.permissions.v0.Options;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.network.ServerPlayerEntity;

public final class HaircutLimits {
    private HaircutLimits() {

    }

    public static boolean canCreate(ServerPlayerEntity player) {
        return Permissions.check(player, "artsandcrafts.createHaircut", true);
    }

    public static int maxTotalSlots(ServerPlayerEntity player) {
        return Options.get(player, "artsandcrafts.maxTotalSlots", 50, Integer::parseInt);
    }

    public static int maxTotalSize(ServerPlayerEntity player) {
        return Options.get(player, "artsandcrafts.maxTotalSize", 1024 * 1024, Integer::parseInt);
    }
}
