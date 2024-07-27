package me.basiqueevangelist.blazingagenda.item;

import io.wispforest.owo.nbt.NbtKey;
import me.basiqueevangelist.blazingagenda.cca.BlazingAgendaCCA;
import me.basiqueevangelist.blazingagenda.cca.HaircutComponent;
import me.basiqueevangelist.blazingagenda.client.ClientHaircutStore;
import me.basiqueevangelist.blazingagenda.haircut.HaircutLimits;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.item.TooltipData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class TemplateItem extends Item implements EarlyUseOnEntity {
    public static final NbtKey<UUID> HAIRCUT = new NbtKey<>("HaircutId", NbtKey.Type.of(NbtElement.INT_ARRAY_TYPE, NbtCompound::getUuid, NbtCompound::putUuid));

    public TemplateItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public Text getName(ItemStack stack) {
        if (FabricLoader.getInstance().getEnvironmentType() != EnvType.CLIENT) return getName();
        if (!stack.has(HAIRCUT)) return getName();

        var entry = ClientHaircutStore.get(stack.get(HAIRCUT));
        if (entry == null) {
            return Text.translatable("item.blazing-agenda.template.withName", Text.translatable("item.blazing-agenda.template.notLoaded"));
        }

        return Text.translatable("item.blazing-agenda.template.withName", entry.name());
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if (!stack.has(HAIRCUT)) {
            if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
                appendExtendableTooltip(tooltip);
            }

            return;
        }

        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            var entry = ClientHaircutStore.get(stack.get(HAIRCUT));
            if (entry != null) {
                String owner = entry.ownerName() != null ? entry.ownerName() : entry.ownerId().toString();

                tooltip.add(Text.translatable("text.blazing-agenda.haircutName", entry.name(), owner));
            } else {
                tooltip.add(Text.translatable("item.blazing-agenda.template.notLoaded"));
            }
        }

        if (context.isAdvanced()) {
            tooltip.add(Text.translatable("item.blazing-agenda.template.haircutId", stack.get(HAIRCUT)));
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
        if (!stack.has(HAIRCUT)) return Optional.empty();

        var entry = ClientHaircutStore.get(stack.get(HAIRCUT));

        if (entry == null) return Optional.empty();

        return Optional.of(new Data(entry));
    }

    @Override
    public ActionResult useOn(ItemStack stack, PlayerEntity user, Entity entity, Hand hand) {
        if (user instanceof ServerPlayerEntity spe && !HaircutLimits.canCopy(spe)) return ActionResult.PASS;
        if (stack.has(HAIRCUT)) return ActionResult.PASS;

        HaircutComponent component = entity.getComponent(BlazingAgendaCCA.HAIRCUT);

        if (component.haircutId().equals(Util.NIL_UUID)) return ActionResult.PASS;

        if (!user.getWorld().isClient) {
            if (stack.getCount() > 1) {
                if (!user.getAbilities().creativeMode) stack.decrement(1);

                var filledStack = stack.copyWithCount(1);
                filledStack.put(HAIRCUT, component.haircutId());
                user.getInventory().offerOrDrop(filledStack);
            } else {
                stack.put(HAIRCUT, component.haircutId());
            }

            user.getWorld().playSoundFromEntity(null, user, SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, user.getSoundCategory(), 1.0F, 1.0F);
        }

        return ActionResult.SUCCESS;
    }

    public record Data(ClientHaircutStore.Entry entry) implements TooltipData {

    }
}
