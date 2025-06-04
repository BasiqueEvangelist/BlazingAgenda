package me.basiqueevangelist.blazingagenda.client;

import me.basiqueevangelist.blazingagenda.client.gui.FashionMagazineScreen;
import me.basiqueevangelist.blazingagenda.client.gui.FashionScrapbookScreen;
import me.basiqueevangelist.blazingagenda.item.FashionMagazineItem;
import me.basiqueevangelist.blazingagenda.screen.BlazingAgendaScreenHandlers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class BlazingAgendaClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlazingAgendaClientNetworking.init();
        ClientCostumeStore.init();

        TooltipComponentCallback.EVENT.register(data -> {
            if (data instanceof FashionMagazineItem.Data d) {
                return new CostumeTooltipComponent(d.entry());
            }

            return null;
        });

        HandledScreens.register(BlazingAgendaScreenHandlers.FASHION_MAGAZINE, FashionMagazineScreen::new);
        HandledScreens.register(BlazingAgendaScreenHandlers.FASHION_SCRAPBOOK, FashionScrapbookScreen::new);
    }
}
