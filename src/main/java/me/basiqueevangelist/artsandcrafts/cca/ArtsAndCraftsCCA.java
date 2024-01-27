package me.basiqueevangelist.artsandcrafts.cca;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import me.basiqueevangelist.artsandcrafts.ArtsAndCrafts;
import net.minecraft.entity.Entity;

public class ArtsAndCraftsCCA implements EntityComponentInitializer {
    public static final ComponentKey<HaircutComponent> HAIRCUT = ComponentRegistry.getOrCreate(ArtsAndCrafts.id("haircut"), HaircutComponent.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerFor(Entity.class, HAIRCUT, HaircutComponent::new);
    }
}
