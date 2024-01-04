package me.basiqueevangelist.barbershop.cca;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import me.basiqueevangelist.barbershop.TheBarbershop;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

public class TheBarbershopCCA implements EntityComponentInitializer {
    public static final ComponentKey<HaircutComponent> HAIRCUT = ComponentRegistry.getOrCreate(new Identifier(TheBarbershop.MOD_ID, "haircut"), HaircutComponent.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerFor(Entity.class, HAIRCUT, HaircutComponent::new);
    }
}
