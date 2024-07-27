package me.basiqueevangelist.blazingagenda.item;

import io.wispforest.owo.registration.reflect.AutoRegistryContainer;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Uuids;

import java.util.UUID;

public class BlazingAgendaComponents implements AutoRegistryContainer<ComponentType<?>> {
    public static final ComponentType<UUID> HAIRCUT_ID = ComponentType.<UUID>builder()
        .codec(Uuids.CODEC)
        .packetCodec(Uuids.PACKET_CODEC)
        .build();

    @Override
    public Registry<ComponentType<?>> getRegistry() {
        return Registries.DATA_COMPONENT_TYPE;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<ComponentType<?>> getTargetFieldType() {
        return (Class<ComponentType<?>>)(Object) ComponentType.class;
    }
}
