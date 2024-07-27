package me.basiqueevangelist.blazingagenda.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import me.basiqueevangelist.blazingagenda.haircut.HaircutsState;
import me.basiqueevangelist.blazingagenda.mixin.PersistentStateManagerAccessor;
import me.basiqueevangelist.blazingagenda.network.BlazingAgendaNetworking;
import me.basiqueevangelist.blazingagenda.network.ReloadAllS2CPacket;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.literal;

public final class ReloadCommand {
    private ReloadCommand() {

    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("blazing-agenda")
            .then(literal("reload")
                .requires(Permissions.require("blazing-agenda.reload", 3))
                .executes(ReloadCommand::reload)));
    }

    private static int reload(CommandContext<ServerCommandSource> ctx) {
        var server = ctx.getSource().getServer();

        server.getOverworld().getPersistentStateManager().save();
        ((PersistentStateManagerAccessor) server.getOverworld().getPersistentStateManager()).getLoadedStates().remove("blazing-agenda");
        HaircutsState.get(server);

        BlazingAgendaNetworking.CHANNEL.serverHandle(server).send(new ReloadAllS2CPacket());

        ctx.getSource().sendFeedback(() -> Text.translatable("command.blazing-agenda.reload_successful"), true);

        return 0;
    }
}
