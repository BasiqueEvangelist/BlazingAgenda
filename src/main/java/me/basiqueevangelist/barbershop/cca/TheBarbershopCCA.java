package me.basiqueevangelist.barbershop.cca;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import me.basiqueevangelist.barbershop.TheBarbershop;
import net.minecraft.entity.Entity;

public class TheBarbershopCCA implements EntityComponentInitializer {
    public static final ComponentKey<HaircutComponent> HAIRCUT = ComponentRegistry.getOrCreate(TheBarbershop.id("haircut"), HaircutComponent.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerFor(Entity.class, HAIRCUT, HaircutComponent::new);
    }
}
