package me.basiqueevangelist.blazingagenda;

import io.wispforest.owo.registration.reflect.SimpleFieldProcessingSubject;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;

import java.lang.reflect.Field;

public class BlazingAgendaSounds implements SimpleFieldProcessingSubject<SoundEvent> {
    public static final SoundEvent FASHION_MAGAZINE_APPLY = SoundEvent.of(BlazingAgenda.id("item.fashion_magazine.apply"));

    @Override
    public Class<SoundEvent> getTargetFieldType() {
        return SoundEvent.class;
    }

    @Override
    public void processField(SoundEvent value, String identifier, Field field) {
        Registry.register(Registries.SOUND_EVENT, value.getId(), value);
    }
}
