package me.basiqueevangelist.blazingagenda.screen;

import io.wispforest.owo.registration.reflect.AutoRegistryContainer;
import io.wispforest.owo.serialization.CodecUtils;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;

public class BlazingAgendaScreenHandlers implements AutoRegistryContainer<ScreenHandlerType<?>> {
    public static final ExtendedScreenHandlerType<FashionMagazineScreenHandler, FashionMagazineScreenHandler.Data> FASHION_MAGAZINE = new ExtendedScreenHandlerType<>(FashionMagazineScreenHandler::new, CodecUtils.toPacketCodec(FashionMagazineScreenHandler.Data.ENDEC));
    public static final ExtendedScreenHandlerType<FashionScrapbookScreenHandler, FashionScrapbookScreenHandler.Data> FASHION_SCRAPBOOK = new ExtendedScreenHandlerType<>(FashionScrapbookScreenHandler::new, CodecUtils.toPacketCodec(FashionScrapbookScreenHandler.Data.ENDEC));

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
