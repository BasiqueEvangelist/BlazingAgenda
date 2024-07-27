package me.basiqueevangelist.blazingagenda.block;

import io.wispforest.owo.itemgroup.OwoItemSettings;
import io.wispforest.owo.registration.reflect.BlockRegistryContainer;
import me.basiqueevangelist.blazingagenda.item.BlazingAgendaItems;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;

public class BlazingAgendaBlocks implements BlockRegistryContainer {
    public static final BarberStationBlock BARBER_STATION = new BarberStationBlock();

    @Override
    public BlockItem createBlockItem(Block block, String identifier) {
        return new BlockItem(block, new OwoItemSettings().group(BlazingAgendaItems.GROUP));
    }
}
