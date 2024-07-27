package me.basiqueevangelist.blazingagenda;

import io.wispforest.owo.registration.reflect.FieldRegistrationHandler;
import me.basiqueevangelist.blazingagenda.block.BlazingAgendaBlocks;
import me.basiqueevangelist.blazingagenda.command.DeleteCommand;
import me.basiqueevangelist.blazingagenda.command.ReloadCommand;
import me.basiqueevangelist.blazingagenda.item.BlazingAgendaItems;
import me.basiqueevangelist.blazingagenda.item.EarlyUseOnEntity;
import me.basiqueevangelist.blazingagenda.network.BlazingAgendaNetworking;
import me.basiqueevangelist.blazingagenda.screen.BlazingAgendaScreenHandlers;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;

public class BlazingAgenda implements ModInitializer {
	public static final String MOD_ID = "blazing-agenda";

	public static Identifier id(String path) {
		return new Identifier(MOD_ID, path);
	}

	@Override
	public void onInitialize() {
		BlazingAgendaNetworking.init();
		FieldRegistrationHandler.register(BlazingAgendaBlocks.class, MOD_ID, false);
		FieldRegistrationHandler.register(BlazingAgendaScreenHandlers.class, MOD_ID, false);
		FieldRegistrationHandler.register(BlazingAgendaItems.class, MOD_ID, false);
		FieldRegistrationHandler.processSimple(BlazingAgendaSounds.class, false);

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			DeleteCommand.register(dispatcher);
			ReloadCommand.register(dispatcher);
		});

		UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
			if (player.isSpectator()) return ActionResult.PASS;

			var stack = player.getStackInHand(hand);

			if (stack.getItem() instanceof EarlyUseOnEntity early) {
				return early.useOn(stack, player, entity, hand);
			}

			return ActionResult.PASS;
		});

		BlazingAgendaItems.GROUP.initialize();
	}
}