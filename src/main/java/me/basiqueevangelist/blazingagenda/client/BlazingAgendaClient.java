package me.basiqueevangelist.blazingagenda.client;

import me.basiqueevangelist.blazingagenda.BlazingAgenda;
import me.basiqueevangelist.blazingagenda.client.gui.BarberStationScreen;
import me.basiqueevangelist.blazingagenda.item.BlazingAgendaItems;
import me.basiqueevangelist.blazingagenda.item.TemplateItem;
import me.basiqueevangelist.blazingagenda.screen.BlazingAgendaScreenHandlers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.item.ModelPredicateProviderRegistry;

public class BlazingAgendaClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlazingAgendaClientNetworking.init();
        ClientHaircutStore.init();

        TooltipComponentCallback.EVENT.register(data -> {
            if (data instanceof TemplateItem.Data d) {
                return new TemplateTooltipComponent(d.entry());
            }

            return null;
        });

        ModelPredicateProviderRegistry.register(
            BlazingAgendaItems.TEMPLATE,
            BlazingAgenda.id("is_filled"),
            (stack, world, entity, seed) -> stack.has(TemplateItem.HAIRCUT) ? 1 : 0
        );

        HandledScreens.register(BlazingAgendaScreenHandlers.BARBER_STATION, BarberStationScreen::new);
    }
}
