package me.basiqueevangelist.barbershop;

import io.wispforest.owo.registration.reflect.FieldRegistrationHandler;
import me.basiqueevangelist.barbershop.block.TheBarbershopBlocks;
import me.basiqueevangelist.barbershop.command.DeleteCommand;
import me.basiqueevangelist.barbershop.item.TheBarbershopItems;
import me.basiqueevangelist.barbershop.network.TheBarbershopNetworking;
import me.basiqueevangelist.barbershop.screen.TheBarbershopScreenHandlers;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.util.Identifier;

public class TheBarbershop implements ModInitializer {
	public static final String MOD_ID = "thebarbershop";

	public static Identifier id(String path) {
		return new Identifier(MOD_ID, path);
	}

	@Override
	public void onInitialize() {
		TheBarbershopNetworking.init();
		FieldRegistrationHandler.register(TheBarbershopBlocks.class, MOD_ID, false);
		FieldRegistrationHandler.register(TheBarbershopScreenHandlers.class, MOD_ID, false);
		FieldRegistrationHandler.register(TheBarbershopItems.class, MOD_ID, false);
		FieldRegistrationHandler.processSimple(TheBarbershopSounds.class, false);

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			DeleteCommand.register(dispatcher);
		});

		TheBarbershopItems.GROUP.initialize();
	}
}