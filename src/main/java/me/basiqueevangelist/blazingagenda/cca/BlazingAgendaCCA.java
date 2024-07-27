package me.basiqueevangelist.blazingagenda.cca;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import me.basiqueevangelist.blazingagenda.BlazingAgenda;
import net.minecraft.entity.Entity;

public class BlazingAgendaCCA implements EntityComponentInitializer {
    public static final ComponentKey<HaircutComponent> HAIRCUT = ComponentRegistry.getOrCreate(BlazingAgenda.id("haircut"), HaircutComponent.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerFor(Entity.class, HAIRCUT, HaircutComponent::new);
    }
}
