package me.basiqueevangelist.blazingagenda;

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
        return Permissions.check(player, "blazing-agenda.applyHaircut", true);
    }

    public static boolean canCopy(ServerPlayerEntity player) {
        return Permissions.check(player, "blazing-agenda.copyHaircut", true);
    }

    public static boolean canDelete(ServerPlayerEntity player) {
        return Permissions.check(player, "blazing-agenda.deleteHaircut", 3);
    }
}
