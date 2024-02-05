package me.basiqueevangelist.artsandcrafts;

import io.wispforest.owo.registration.reflect.FieldRegistrationHandler;
import me.basiqueevangelist.artsandcrafts.block.ArtsAndCraftsBlocks;
import me.basiqueevangelist.artsandcrafts.command.DeleteCommand;
import me.basiqueevangelist.artsandcrafts.item.ArtsAndCraftsItems;
import me.basiqueevangelist.artsandcrafts.item.EarlyUseOnEntity;
import me.basiqueevangelist.artsandcrafts.item.ScissorsItem;
import me.basiqueevangelist.artsandcrafts.network.ArtsAndCraftsNetworking;
import me.basiqueevangelist.artsandcrafts.screen.ArtsAndCraftsScreenHandlers;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;

public class ArtsAndCrafts implements ModInitializer {
	public static final String MOD_ID = "artsandcrafts";

	public static Identifier id(String path) {
		return new Identifier(MOD_ID, path);
	}

	@Override
	public void onInitialize() {
		ArtsAndCraftsNetworking.init();
		FieldRegistrationHandler.register(ArtsAndCraftsBlocks.class, MOD_ID, false);
		FieldRegistrationHandler.register(ArtsAndCraftsScreenHandlers.class, MOD_ID, false);
		FieldRegistrationHandler.register(ArtsAndCraftsItems.class, MOD_ID, false);
		FieldRegistrationHandler.processSimple(ArtsAndCraftsSounds.class, false);

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			DeleteCommand.register(dispatcher);
		});

		UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
			if (player.isSpectator()) return ActionResult.PASS;

			var stack = player.getStackInHand(hand);

			if (stack.getItem() instanceof EarlyUseOnEntity early) {
				return early.useOn(stack, player, entity, hand);
			}

			return ActionResult.PASS;
		});

		ArtsAndCraftsItems.GROUP.initialize();
	}
}