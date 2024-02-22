package me.basiqueevangelist.artsandcrafts.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import me.basiqueevangelist.artsandcrafts.haircut.HaircutsState;
import me.basiqueevangelist.artsandcrafts.mixin.PersistentStateManagerAccessor;
import me.basiqueevangelist.artsandcrafts.network.ArtsAndCraftsNetworking;
import me.basiqueevangelist.artsandcrafts.network.ReloadAllS2CPacket;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.literal;

public final class ReloadCommand {
    private ReloadCommand() {

    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("artsandcrafts")
            .then(literal("reload")
                .requires(Permissions.require("artsandcrafts.reload", 3))
                .executes(ReloadCommand::reload)));
    }

    private static int reload(CommandContext<ServerCommandSource> ctx) {
        var server = ctx.getSource().getServer();

        server.getOverworld().getPersistentStateManager().save();
        ((PersistentStateManagerAccessor) server.getOverworld().getPersistentStateManager()).getLoadedStates().remove("artsandcrafts");
        HaircutsState.get(server);

        ArtsAndCraftsNetworking.CHANNEL.serverHandle(server).send(new ReloadAllS2CPacket());

        ctx.getSource().sendFeedback(() -> Text.translatable("command.artsandcrafts.reload_successful"), true);

        return 0;
    }
}
