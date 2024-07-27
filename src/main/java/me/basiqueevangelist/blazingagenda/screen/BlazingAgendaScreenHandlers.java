package me.basiqueevangelist.blazingagenda.screen;

import io.wispforest.owo.registration.reflect.AutoRegistryContainer;
import io.wispforest.owo.serialization.CodecUtils;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.ScreenHandlerType;

public class BlazingAgendaScreenHandlers implements AutoRegistryContainer<ScreenHandlerType<?>> {
    public static final ScreenHandlerType<BarberStationScreenHandler> BARBER_STATION = new ScreenHandlerType<>(BarberStationScreenHandler::new, FeatureSet.empty());
    public static final ExtendedScreenHandlerType<FashionMagazineScreenHandler, FashionMagazineScreenHandler.Data> FASHION_MAGAZINE = new ExtendedScreenHandlerType<>(FashionMagazineScreenHandler::new, CodecUtils.toPacketCodec(FashionMagazineScreenHandler.Data.ENDEC));

    @Override
    public Registry<ScreenHandlerType<?>> getRegistry() {
        return Registries.SCREEN_HANDLER;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<ScreenHandlerType<?>> getTargetFieldType() {
        return (Class<ScreenHandlerType<?>>) (Object) ScreenHandlerType.class;
    }
}
