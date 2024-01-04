package me.basiqueevangelist.barbershop.screen;

import io.wispforest.owo.registration.reflect.AutoRegistryContainer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandlerType;

public class TheBarbershopScreenHandlers implements AutoRegistryContainer<ScreenHandlerType<?>> {
    public static final ScreenHandlerType<BarberStationScreenHandler> BARBER_STATION = new ScreenHandlerType<>(BarberStationScreenHandler::new, FeatureFlags.DEFAULT_ENABLED_FEATURES);

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
