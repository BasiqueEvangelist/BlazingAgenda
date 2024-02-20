package me.basiqueevangelist.artsandcrafts.haircut;

import me.lucko.fabric.api.permissions.v0.Options;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.network.ServerPlayerEntity;

public final class HaircutLimits {
    private HaircutLimits() {

    }

    public static boolean canUpload(ServerPlayerEntity player) {
        return Permissions.check(player, "artsandcrafts.uploadHaircut", true);
    }

    public static boolean canApply(ServerPlayerEntity player) {
        return Permissions.check(player, "artsandcrafts.applyHaircut", true);
    }

    public static boolean canCopy(ServerPlayerEntity player) {
        return Permissions.check(player, "artsandcrafts.copyHaircut", true);
    }

    public static int maxHaircutSlots(ServerPlayerEntity player) {
        return Options.get(player, "artsandcrafts.maxHaircutSlots", 50, Integer::parseInt);
    }

    public static int maxTotalStorage(ServerPlayerEntity player) {
        return Options.get(player, "artsandcrafts.maxTotalStorage", 1024 * 1024, Integer::parseInt);
    }
}
