package me.basiqueevangelist.barbershop.item;

import io.wispforest.owo.itemgroup.Icon;
import io.wispforest.owo.itemgroup.OwoItemGroup;
import io.wispforest.owo.itemgroup.OwoItemSettings;
import io.wispforest.owo.itemgroup.gui.ItemGroupButton;
import io.wispforest.owo.registration.reflect.ItemRegistryContainer;
import me.basiqueevangelist.barbershop.TheBarbershop;
import net.minecraft.item.Item;

public class TheBarbershopItems implements ItemRegistryContainer {
    public static final OwoItemGroup GROUP = OwoItemGroup.builder(TheBarbershop.id("item_group"), () -> Icon.of(TheBarbershopItems.SCISSORS))
        .initializer(group -> {
            group.addButton(ItemGroupButton.github(group, "https://github.com/BasiqueEvangelist/TheBarbershop"));
        })
        .build();

    public static final Item EMPTY_TEMPLATE = new Item(new OwoItemSettings().group(GROUP));
    public static final TemplateItem TEMPLATE = new TemplateItem(new OwoItemSettings().group(GROUP));
    public static final ScissorsItem SCISSORS = new ScissorsItem(new OwoItemSettings().group(GROUP).maxCount(1).maxDamage(500));
}
