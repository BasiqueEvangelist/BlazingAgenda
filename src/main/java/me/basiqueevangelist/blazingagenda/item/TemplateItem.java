package me.basiqueevangelist.blazingagenda.item;

import me.basiqueevangelist.blazingagenda.cca.BlazingAgendaCCA;
import me.basiqueevangelist.blazingagenda.cca.HaircutComponent;
import me.basiqueevangelist.blazingagenda.client.ClientHaircutStore;
import me.basiqueevangelist.blazingagenda.haircut.HaircutLimits;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.*;

import java.util.List;
import java.util.Optional;

public class TemplateItem extends Item implements EarlyUseOnEntity {
    public TemplateItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public Text getName(ItemStack stack) {
        if (FabricLoader.getInstance().getEnvironmentType() != EnvType.CLIENT) return getName();
        if (!stack.contains(BlazingAgendaComponents.HAIRCUT_ID)) return getName();

        var entry = ClientHaircutStore.get(stack.get(BlazingAgendaComponents.HAIRCUT_ID));
        if (entry == null) {
            return Text.translatable("item.blazing-agenda.template.withName", Text.translatable("item.blazing-agenda.template.notLoaded"));
        }

        return Text.translatable("item.blazing-agenda.template.withName", entry.name());
    }

    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
        if (!stack.contains(BlazingAgendaComponents.HAIRCUT_ID)) {
            if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
                appendExtendableTooltip(tooltip);
            }

            return;
        }

        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            var entry = ClientHaircutStore.get(stack.get(BlazingAgendaComponents.HAIRCUT_ID));
            if (entry != null) {
                String owner = entry.ownerName() != null ? entry.ownerName() : entry.ownerId().toString();

                tooltip.add(Text.translatable("text.blazing-agenda.haircutName", entry.name(), owner));
            } else {
                tooltip.add(Text.translatable("item.blazing-agenda.template.notLoaded"));
            }
        }

        if (type.isAdvanced()) {
            tooltip.add(Text.translatable("item.blazing-agenda.template.haircutId", stack.get(BlazingAgendaComponents.HAIRCUT_ID)));
        }
    }

    @Environment(EnvType.CLIENT)
    private void appendExtendableTooltip(List<Text> tooltip) {
        if (Screen.hasShiftDown()) {
            tooltip.add(Text.translatable("item.blazing-agenda.empty_template.tooltip"));
        } else {
            tooltip.add(Text.translatable("text.blazing-agenda.tooltip_hint"));
        }
    }

    @Override
    public Optional<TooltipData> getTooltipData(ItemStack stack) {
        if (FabricLoader.getInstance().getEnvironmentType() != EnvType.CLIENT) return Optional.empty();
        if (!stack.contains(BlazingAgendaComponents.HAIRCUT_ID)) return Optional.empty();

        var entry = ClientHaircutStore.get(stack.get(BlazingAgendaComponents.HAIRCUT_ID));

        if (entry == null) return Optional.empty();

        return Optional.of(new Data(entry));
    }

    @Override
    public ActionResult useOn(ItemStack stack, PlayerEntity user, Entity entity, Hand hand) {
        if (user instanceof ServerPlayerEntity spe && !HaircutLimits.canCopy(spe)) return ActionResult.PASS;
        if (stack.contains(BlazingAgendaComponents.HAIRCUT_ID)) return ActionResult.PASS;

        HaircutComponent component = entity.getComponent(BlazingAgendaCCA.HAIRCUT);

        if (component.haircutId().equals(Util.NIL_UUID)) return ActionResult.PASS;

        if (!user.getWorld().isClient) {
            if (stack.getCount() > 1) {
                if (!user.getAbilities().creativeMode) stack.decrement(1);

                var filledStack = stack.copyWithCount(1);
                filledStack.set(BlazingAgendaComponents.HAIRCUT_ID, component.haircutId());
                user.getInventory().offerOrDrop(filledStack);
            } else {
                stack.set(BlazingAgendaComponents.HAIRCUT_ID, component.haircutId());
            }

            user.getWorld().playSoundFromEntity(null, user, SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, user.getSoundCategory(), 1.0F, 1.0F);
        }

        return ActionResult.SUCCESS;
    }

    public record Data(ClientHaircutStore.Entry entry) implements TooltipData {

    }
}
