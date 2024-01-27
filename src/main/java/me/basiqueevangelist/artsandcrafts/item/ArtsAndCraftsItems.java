package me.basiqueevangelist.artsandcrafts.item;

import io.wispforest.owo.itemgroup.Icon;
import io.wispforest.owo.itemgroup.OwoItemGroup;
import io.wispforest.owo.itemgroup.OwoItemSettings;
import io.wispforest.owo.itemgroup.gui.ItemGroupButton;
import io.wispforest.owo.registration.reflect.ItemRegistryContainer;
import me.basiqueevangelist.artsandcrafts.ArtsAndCrafts;

public class ArtsAndCraftsItems implements ItemRegistryContainer {
    public static final OwoItemGroup GROUP = OwoItemGroup.builder(ArtsAndCrafts.id("item_group"), () -> Icon.of(ArtsAndCraftsItems.SCISSORS))
        .initializer(group ->
            group.addButton(ItemGroupButton.github(group, "https://github.com/BasiqueEvangelist/ArtsAndCrafts")))
        .build();

    public static final TemplateItem TEMPLATE = new TemplateItem(new OwoItemSettings().group(GROUP));
    public static final ScissorsItem SCISSORS = new ScissorsItem(new OwoItemSettings().group(GROUP).maxCount(1).maxDamage(500));
}
