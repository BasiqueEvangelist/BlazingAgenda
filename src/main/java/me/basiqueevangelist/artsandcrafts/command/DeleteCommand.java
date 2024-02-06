package me.basiqueevangelist.artsandcrafts.command;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import me.basiqueevangelist.artsandcrafts.cca.ArtsAndCraftsCCA;
import me.basiqueevangelist.artsandcrafts.haircut.HaircutsState;
import me.basiqueevangelist.artsandcrafts.item.ArtsAndCraftsItems;
import me.basiqueevangelist.artsandcrafts.item.TemplateItem;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public final class DeleteCommand {
    private static final SimpleCommandExceptionType NO_HAIRCUT_HELD = new SimpleCommandExceptionType(Text.translatable("error.artsandcrafts.noHaircutHeld"));
    private static final SimpleCommandExceptionType NO_HAIRCUT_TARGET = new SimpleCommandExceptionType(Text.translatable("error.artsandcrafts.noHaircutTarget"));

    private DeleteCommand() {

    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("artsandcrafts")
            .then(literal("delete")
                .requires(Permissions.require("artsandcrafts.deleteHaircut", 3))
                .then(literal("inhand")
                    .executes(DeleteCommand::deleteInHand))
                .then(literal("target")
                    .then(argument("target", EntityArgumentType.entity())
                        .executes(DeleteCommand::deleteTarget)))));
    }

    private static int deleteTarget(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        var entity = EntityArgumentType.getEntity(ctx, "target");
        var component = entity.getComponent(ArtsAndCraftsCCA.HAIRCUT);

        if (component.haircutId().equals(Util.NIL_UUID))
            throw NO_HAIRCUT_TARGET.create();

        var state = HaircutsState.get(ctx.getSource().getServer());
        var haircut = state.haircuts().get(component.haircutId());

        if (haircut == null)
            throw NO_HAIRCUT_TARGET.create();

        state.deleteHaircut(haircut);

        String owner = ctx.getSource().getServer()
            .getUserCache()
            .getByUuid(haircut.ownerId())
            .map(GameProfile::getName)
            .orElseGet(() -> haircut.ownerId().toString());

        // TODO: translate.
        ctx.getSource().sendFeedback(() -> Text.literal("Deleted ")
            .append(Text.literal(haircut.name())
                .formatted(Formatting.GRAY))
            .append(Text.literal(" by "))
            .append(Text.literal(owner)
                .formatted(Formatting.AQUA)), true);

        return 1;
    }

    private static int deleteInHand(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        var player = ctx.getSource().getPlayerOrThrow();
        var handStack = player.getStackInHand(Hand.MAIN_HAND);

        if (!handStack.isOf(ArtsAndCraftsItems.TEMPLATE) || !handStack.has(TemplateItem.HAIRCUT))
            throw NO_HAIRCUT_HELD.create();

        var haircutId = handStack.get(TemplateItem.HAIRCUT);
        var state = HaircutsState.get(ctx.getSource().getServer());
        var haircut = state.haircuts().get(haircutId);

        if (haircut == null)
            throw NO_HAIRCUT_HELD.create();

        state.deleteHaircut(haircut);

        String owner = ctx.getSource().getServer()
            .getUserCache()
            .getByUuid(haircut.ownerId())
            .map(GameProfile::getName)
            .orElseGet(() -> haircut.ownerId().toString());

        // TODO: translate.
        ctx.getSource().sendFeedback(() -> Text.literal("Deleted ")
            .append(Text.literal(haircut.name())
                .formatted(Formatting.GRAY))
            .append(Text.literal(" by "))
            .append(Text.literal(owner)
                .formatted(Formatting.AQUA)), true);

        return 0;
    }
}
