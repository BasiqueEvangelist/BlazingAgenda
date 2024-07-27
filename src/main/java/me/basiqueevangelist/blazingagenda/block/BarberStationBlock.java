package me.basiqueevangelist.blazingagenda.block;

import me.basiqueevangelist.blazingagenda.screen.BarberStationScreenHandler;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BarberStationBlock extends Block {
    public BarberStationBlock() {
        super(AbstractBlock.Settings.copy(Blocks.FLETCHING_TABLE));
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        var factory = new NamedScreenHandlerFactory() {
            @Override
            public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
                return new BarberStationScreenHandler(syncId, playerInventory);
            }

            @Override
            public Text getDisplayName() {
                return Text.empty();
            }
        };

        player.openHandledScreen(factory);

        return ActionResult.SUCCESS;
    }
}
