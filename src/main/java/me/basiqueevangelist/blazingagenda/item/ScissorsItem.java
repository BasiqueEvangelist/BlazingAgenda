package me.basiqueevangelist.blazingagenda.item;

import me.basiqueevangelist.blazingagenda.BlazingAgendaSounds;
import me.basiqueevangelist.blazingagenda.cca.BlazingAgendaCCA;
import me.basiqueevangelist.blazingagenda.cca.HaircutComponent;
import me.basiqueevangelist.blazingagenda.haircut.HaircutLimits;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.Util;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class ScissorsItem extends Item implements EarlyUseOnEntity {
    public ScissorsItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
        return ingredient.isOf(Items.IRON_INGOT);
    }

    public ActionResult useOn(ItemStack stack, PlayerEntity user, Entity entity, Hand hand) {
        if (user instanceof ServerPlayerEntity spe && !HaircutLimits.canApply(spe)) return ActionResult.PASS;

        if (hand == Hand.OFF_HAND) return ActionResult.PASS;

        if (entity instanceof EnderDragonPart part) entity = part.owner;

        ItemStack offStack = user.getOffHandStack();
        UUID haircutId = Util.NIL_UUID;
        HaircutComponent component = entity.getComponent(BlazingAgendaCCA.HAIRCUT);

        if (offStack.isOf(BlazingAgendaItems.TEMPLATE)) {
            haircutId = offStack.getOrDefault(BlazingAgendaComponents.HAIRCUT_ID, Util.NIL_UUID);
        }

        if (component.haircutId().equals(haircutId))
            return ActionResult.FAIL;

        user.getWorld().playSoundFromEntity(user, entity, BlazingAgendaSounds.SCISSORS_USE, SoundCategory.PLAYERS, 1.0F, 1.0F);

        if (user.getWorld().isClient) return ActionResult.SUCCESS;

        component.setHaircutId(haircutId);
        stack.damage(1, user, LivingEntity.getSlotForHand(hand));

        return ActionResult.SUCCESS;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (user instanceof ServerPlayerEntity spe && !HaircutLimits.canApply(spe)) return TypedActionResult.pass(stack);
        if (hand == Hand.OFF_HAND) return TypedActionResult.pass(stack);
        if (!user.isSneaking()) return TypedActionResult.pass(stack);

        ItemStack offStack = user.getOffHandStack();
        UUID haircutId = Util.NIL_UUID;
        HaircutComponent component = user.getComponent(BlazingAgendaCCA.HAIRCUT);

        if (offStack.isOf(BlazingAgendaItems.TEMPLATE)) {
            haircutId = offStack.getOrDefault(BlazingAgendaComponents.HAIRCUT_ID, Util.NIL_UUID);
        }

        if (component.haircutId().equals(haircutId))
            return TypedActionResult.fail(stack);

        user.getWorld().playSoundFromEntity(user, user, BlazingAgendaSounds.SCISSORS_USE, SoundCategory.PLAYERS, 1.0F, 1.0F);

        if (user.getWorld().isClient) return TypedActionResult.success(stack);

        component.setHaircutId(haircutId);
        stack.damage(1, user, LivingEntity.getSlotForHand(hand));

        return TypedActionResult.success(stack);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        if (Screen.hasShiftDown()) {
            tooltip.add(Text.translatable("item.blazing-agenda.scissors.tooltip.1"));
            tooltip.add(Text.translatable("item.blazing-agenda.scissors.tooltip.2"));
        } else {
            tooltip.add(Text.translatable("text.blazing-agenda.tooltip_hint"));
        }
    }
}
