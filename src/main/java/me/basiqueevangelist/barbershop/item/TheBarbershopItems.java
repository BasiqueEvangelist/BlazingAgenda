package me.basiqueevangelist.barbershop.item;

import io.wispforest.owo.registration.reflect.ItemRegistryContainer;
import net.minecraft.item.Item;

public class TheBarbershopItems implements ItemRegistryContainer {
    public static final Item EMPTY_TEMPLATE = new Item(new Item.Settings());
    public static final TemplateItem TEMPLATE = new TemplateItem(new Item.Settings());
}
