package me.basiqueevangelist.blazingagenda.item;

import me.basiqueevangelist.blazingagenda.BlazingAgendaPermissions;
import me.basiqueevangelist.blazingagenda.screen.FashionScrapbookScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;

public class FashionScrapbookItem extends Item {
    public FashionScrapbookItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        var stack = user.getStackInHand(hand);

        if (world.isClient) return TypedActionResult.success(stack);

        if (!BlazingAgendaPermissions.canManageAssets(user)) {
            user.sendMessage(Text.translatable("item.blazing-agenda.fashion_scrapbook.notEnoughPermissions"));
            return TypedActionResult.fail(stack);
        }

        var data = FashionScrapbookScreenHandler.Data.gather((ServerPlayerEntity) user);

        var factory = new ExtendedScreenHandlerFactory<FashionScrapbookScreenHandler.Data>() {
            @Override
            public FashionScrapbookScreenHandler.Data getScreenOpeningData(ServerPlayerEntity player) {
                return data;
            }

            @Override
            public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
                return new FashionScrapbookScreenHandler(syncId, playerInventory, data, hand);
            }

            @Override
            public Text getDisplayName() {
                return Text.empty();
            }
        };

        user.openHandledScreen(factory);

        return TypedActionResult.success(stack);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("item.blazing-agenda.fashion_scrapbook.requiresPermissions"));
    }
}
