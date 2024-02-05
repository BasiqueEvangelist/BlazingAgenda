package me.basiqueevangelist.artsandcrafts.client;

import me.basiqueevangelist.artsandcrafts.ArtsAndCrafts;
import me.basiqueevangelist.artsandcrafts.client.gui.BarberStationScreen;
import me.basiqueevangelist.artsandcrafts.item.ArtsAndCraftsItems;
import me.basiqueevangelist.artsandcrafts.item.TemplateItem;
import me.basiqueevangelist.artsandcrafts.screen.ArtsAndCraftsScreenHandlers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.item.ModelPredicateProviderRegistry;

public class ArtsAndCraftsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ArtsAndCraftsClientNetworking.init();
        ClientHaircutStore.init();

        TooltipComponentCallback.EVENT.register(data -> {
            if (data instanceof TemplateItem.Data d) {
                return new TemplateTooltipComponent(d.entry());
            }

            return null;
        });

        ModelPredicateProviderRegistry.register(
            ArtsAndCraftsItems.TEMPLATE,
            ArtsAndCrafts.id("is_filled"),
            (stack, world, entity, seed) -> stack.has(TemplateItem.HAIRCUT) ? 1 : 0
        );

        HandledScreens.register(ArtsAndCraftsScreenHandlers.BARBER_STATION, BarberStationScreen::new);
    }
}
