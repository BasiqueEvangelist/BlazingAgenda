package me.basiqueevangelist.blazingagenda.item;

import me.basiqueevangelist.blazingagenda.BlazingAgendaSounds;
import me.basiqueevangelist.blazingagenda.cca.BlazingAgendaCCA;
import me.basiqueevangelist.blazingagenda.cca.CostumeComponent;
import me.basiqueevangelist.blazingagenda.client.ClientCostumeStore;
import me.basiqueevangelist.blazingagenda.BlazingAgendaPermissions;
import me.basiqueevangelist.blazingagenda.screen.FashionMagazineScreenHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.Util;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FashionMagazineItem extends Item implements EarlyUseOnEntity {
    public FashionMagazineItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        var stack = user.getStackInHand(hand);

        if (user.isSneaking()) {
            if (!stack.contains(BlazingAgendaComponents.COSTUME_ID)) return TypedActionResult.fail(stack);

            if (!world.isClient) {
                stack.remove(BlazingAgendaComponents.COSTUME_ID);
            }

            return TypedActionResult.success(stack);
        }

        if (world.isClient) return TypedActionResult.success(stack);

        var data = FashionMagazineScreenHandler.Data.gather((ServerPlayerEntity) user);

        var factory = new ExtendedScreenHandlerFactory<FashionMagazineScreenHandler.Data>() {
            @Override
            public FashionMagazineScreenHandler.Data getScreenOpeningData(ServerPlayerEntity player) {
                return data;
            }

            @Override
            public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
                return new FashionMagazineScreenHandler(syncId, playerInventory, data, hand);
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
    public ActionResult useOn(ItemStack stack, PlayerEntity user, Entity entity, Hand hand) {
        if (user instanceof ServerPlayerEntity spe && !BlazingAgendaPermissions.canApply(spe)) return ActionResult.PASS;

        if (entity instanceof EnderDragonPart part) entity = part.owner;

        UUID haircutId = stack.getOrDefault(BlazingAgendaComponents.COSTUME_ID, Util.NIL_UUID);
        CostumeComponent component = entity.getComponent(BlazingAgendaCCA.COSTUME);

        if (component.costumeId().equals(haircutId))
            return ActionResult.FAIL;

        user.getWorld().playSoundFromEntity(user, entity, BlazingAgendaSounds.FASHION_MAGAZINE_APPLY, SoundCategory.PLAYERS, 1.0F, 1.0F);

        if (user.getWorld().isClient) return ActionResult.SUCCESS;

        component.setCostumeId(haircutId);
        stack.damage(1, user, LivingEntity.getSlotForHand(hand));

        return ActionResult.SUCCESS;
    }

    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            var entry = ClientCostumeStore.get(stack.getOrDefault(BlazingAgendaComponents.COSTUME_ID, Util.NIL_UUID));
            if (entry != null) {
                String owner = entry.ownerName() != null ? entry.ownerName() : entry.ownerId().toString();

                tooltip.add(Text.translatable("text.blazing-agenda.costumeName", entry.name(), owner));
            } else {
                tooltip.add(Text.translatable("item.blazing-agenda.fashion_magazine.notLoaded"));
            }
        }

        if (type.isAdvanced()) {
            tooltip.add(Text.translatable("item.blazing-agenda.fashion_magazine.costumeId", stack.get(BlazingAgendaComponents.COSTUME_ID)));
        }
    }

    @Override
    public Text getName(ItemStack stack) {
        if (FabricLoader.getInstance().getEnvironmentType() != EnvType.CLIENT) return getName();
        if (!stack.contains(BlazingAgendaComponents.COSTUME_ID)) return getName();

        var entry = ClientCostumeStore.get(stack.get(BlazingAgendaComponents.COSTUME_ID));
        if (entry == null) {
            return Text.translatable("item.blazing-agenda.fashion_magazine.withName", Text.translatable("item.blazing-agenda.fashion_magazine.notLoaded"));
        }

        return Text.translatable("item.blazing-agenda.fashion_magazine.withName", entry.name());
    }

    @Override
    public Optional<TooltipData> getTooltipData(ItemStack stack) {
        if (FabricLoader.getInstance().getEnvironmentType() != EnvType.CLIENT) return Optional.empty();
        if (!stack.contains(BlazingAgendaComponents.COSTUME_ID)) return Optional.empty();

        var entry = ClientCostumeStore.get(stack.get(BlazingAgendaComponents.COSTUME_ID));

        if (entry == null) return Optional.empty();

        return Optional.of(new Data(entry));
    }

    public record Data(ClientCostumeStore.Entry entry) implements TooltipData { }
}
