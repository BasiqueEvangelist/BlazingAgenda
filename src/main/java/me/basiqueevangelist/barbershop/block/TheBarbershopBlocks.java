package me.basiqueevangelist.barbershop.block;

import io.wispforest.owo.itemgroup.OwoItemSettings;
import io.wispforest.owo.registration.reflect.BlockRegistryContainer;
import me.basiqueevangelist.barbershop.item.TheBarbershopItems;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;

public class TheBarbershopBlocks implements BlockRegistryContainer {
    public static final BarberStationBlock BARBER_STATION = new BarberStationBlock();

    @Override
    public BlockItem createBlockItem(Block block, String identifier) {
        return new BlockItem(block, new OwoItemSettings().group(TheBarbershopItems.GROUP));
    }
}
