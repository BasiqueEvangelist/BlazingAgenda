package me.basiqueevangelist.blazingagenda.item;

import io.wispforest.owo.itemgroup.Icon;
import io.wispforest.owo.itemgroup.OwoItemGroup;
import io.wispforest.owo.itemgroup.gui.ItemGroupButton;
import io.wispforest.owo.registration.reflect.ItemRegistryContainer;
import me.basiqueevangelist.blazingagenda.BlazingAgenda;
import net.minecraft.item.Item;

public class BlazingAgendaItems implements ItemRegistryContainer {
    public static final OwoItemGroup GROUP = OwoItemGroup.builder(BlazingAgenda.id("item_group"), () -> Icon.of(BlazingAgendaItems.FASHION_MAGAZINE))
        .initializer(group -> {
            group.addButton(ItemGroupButton.github(group, "https://github.com/BasiqueEvangelist/BlazingAgenda"));
            group.addButton(ItemGroupButton.modrinth(group, "https://modrinth.com/mod/blazing-agenda"));
            group.addButton(ItemGroupButton.discord(group, "https://wispforest.io/discord"));
        })
        .build();

    public static final FashionMagazineItem FASHION_MAGAZINE = new FashionMagazineItem(new Item.Settings().group(GROUP).maxCount(1));
    public static final FashionScrapbookItem FASHION_SCRAPBOOK = new FashionScrapbookItem(new Item.Settings().group(GROUP).maxCount(1));
}
