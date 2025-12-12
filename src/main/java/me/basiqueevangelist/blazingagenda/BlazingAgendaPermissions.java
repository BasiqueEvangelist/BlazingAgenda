package me.basiqueevangelist.blazingagenda;

import me.lucko.fabric.api.permissions.v0.Options;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

public final class BlazingAgendaPermissions {
    private BlazingAgendaPermissions() {

    }

    public static boolean canManageAssets(PlayerEntity player) {
        return Permissions.check(player, "blazing-agenda.manageAssets",  BlazingAgenda.CONFIG.everybodyCanManageAssets() ? 0 : 3);
    }

    public static boolean canApplyCostume(ServerPlayerEntity player) {
        return Permissions.check(player, "blazing-agenda.applyCostume", BlazingAgenda.CONFIG.everybodyCanApplyCostumes() ? 0 : 3);
    }

    public static boolean canDeleteCostumeOther(ServerPlayerEntity player) {
        return Permissions.check(player, "blazing-agenda.deleteCostumeOther", BlazingAgenda.CONFIG.everybodyCanDeleteOtherCostumes() ? 0 : 3);
    }

    public static boolean canUpdateCostumeOther(ServerPlayerEntity player) {
        return Permissions.check(player, "blazing-agenda.updateCostumeOther", BlazingAgenda.CONFIG.everybodyCanUpdateOtherCostumes() ? 0 : 3);
    }

    public static int maxCostumeSlots(ServerPlayerEntity player) {
        return Options.get(player, "blazing-agenda.maxCostumeSlots", BlazingAgenda.CONFIG.maxCostumeSlots(), Integer::parseInt);
    }

    public static int maxTotalStorage(ServerPlayerEntity player) {
        return Options.get(player, "blazing-agenda.maxTotalStorage", BlazingAgenda.CONFIG.maxTotalStorage(), Integer::parseInt);
    }
}
