package me.basiqueevangelist.artsandcrafts.block;

import me.basiqueevangelist.artsandcrafts.screen.BarberStationScreenHandler;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BarberStationBlock extends Block {
    public BarberStationBlock() {
        super(FabricBlockSettings.copyOf(Blocks.CARTOGRAPHY_TABLE));
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
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
