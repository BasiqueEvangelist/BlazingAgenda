package me.basiqueevangelist.barbershop.command;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import me.basiqueevangelist.barbershop.haircut.HaircutsState;
import me.basiqueevangelist.barbershop.item.TemplateItem;
import me.basiqueevangelist.barbershop.item.TheBarbershopItems;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;

import java.util.Optional;

import static net.minecraft.server.command.CommandManager.literal;

public final class DeleteCommand {
    private static final SimpleCommandExceptionType NO_HAIRCUT_HELD = new SimpleCommandExceptionType(Text.translatable("error.thebarbershop.noHaircutHeld"));

    private DeleteCommand() {

    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("barbershop")
            .then(literal("delete")
                .requires(Permissions.require("thebarbershop.deleteHaircut", 3))
                .executes(DeleteCommand::delete)));
    }

    private static int delete(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        var player = ctx.getSource().getPlayerOrThrow();
        var handStack = player.getStackInHand(Hand.MAIN_HAND);

        if (!handStack.isOf(TheBarbershopItems.TEMPLATE) || !handStack.has(TemplateItem.HAIRCUT))
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
