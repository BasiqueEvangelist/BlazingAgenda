package me.basiqueevangelist.blazingagenda;

import me.lucko.fabric.api.permissions.v0.Options;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

public final class BlazingAgendaPermissions {
    private BlazingAgendaPermissions() {

    }

    public static boolean canManageAssets(PlayerEntity player) {
        return Permissions.check(player, "blazing-agenda.manageAssets", 3);
    }

    public static boolean canApply(ServerPlayerEntity player) {
        return Permissions.check(player, "blazing-agenda.applyCostume", true);
    }

    public static boolean canDelete(ServerPlayerEntity player) {
        return Permissions.check(player, "blazing-agenda.deleteCostume", 3);
    }

    public static int maxCostumeSlots(ServerPlayerEntity player) {
        return Options.get(player, "blazing-agenda.maxCostumeSlots", 100, Integer::parseInt);
    }

    public static int maxTotalStorage(ServerPlayerEntity player) {
        return Options.get(player, "blazing-agenda.maxTotalStorage", 128 * 1024 * 1024, Integer::parseInt);
    }
}
