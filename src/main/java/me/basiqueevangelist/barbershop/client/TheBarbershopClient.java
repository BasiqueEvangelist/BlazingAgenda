package me.basiqueevangelist.barbershop.client;

import me.basiqueevangelist.barbershop.TheBarbershop;
import me.basiqueevangelist.barbershop.client.gui.BarberStationScreen;
import me.basiqueevangelist.barbershop.item.TemplateItem;
import me.basiqueevangelist.barbershop.item.TheBarbershopItems;
import me.basiqueevangelist.barbershop.screen.TheBarbershopScreenHandlers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.item.ModelPredicateProviderRegistry;

public class TheBarbershopClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        TheBarbershopClientNetworking.init();
        ClientHaircutStore.init();

        TooltipComponentCallback.EVENT.register(data -> {
            if (data instanceof TemplateItem.Data d) {
                return new TemplateTooltipComponent(d.entry());
            }

            return null;
        });

        ModelPredicateProviderRegistry.register(
            TheBarbershopItems.TEMPLATE,
            TheBarbershop.id("is_filled"),
            (stack, world, entity, seed) -> stack.has(TemplateItem.HAIRCUT) ? 1 : 0
        );

        HandledScreens.register(TheBarbershopScreenHandlers.BARBER_STATION, BarberStationScreen::new);
    }
}
