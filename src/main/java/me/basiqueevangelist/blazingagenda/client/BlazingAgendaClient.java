package me.basiqueevangelist.blazingagenda.client;

import me.basiqueevangelist.blazingagenda.client.gui.BarberStationScreen;
import me.basiqueevangelist.blazingagenda.client.gui.FashionMagazineScreen;
import me.basiqueevangelist.blazingagenda.screen.BlazingAgendaScreenHandlers;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class BlazingAgendaClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlazingAgendaClientNetworking.init();
        ClientCostumeStore.init();

        HandledScreens.register(BlazingAgendaScreenHandlers.BARBER_STATION, BarberStationScreen::new);
        HandledScreens.register(BlazingAgendaScreenHandlers.FASHION_MAGAZINE, FashionMagazineScreen::new);
    }
}
