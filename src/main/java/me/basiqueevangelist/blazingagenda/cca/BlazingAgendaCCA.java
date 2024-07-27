package me.basiqueevangelist.blazingagenda.cca;

import me.basiqueevangelist.blazingagenda.BlazingAgenda;
import net.minecraft.entity.Entity;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;

public class BlazingAgendaCCA implements EntityComponentInitializer {
    public static final ComponentKey<CostumeComponent> COSTUME = ComponentRegistry.getOrCreate(BlazingAgenda.id("haircut"), CostumeComponent.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerFor(Entity.class, COSTUME, CostumeComponent::new);
    }
}
