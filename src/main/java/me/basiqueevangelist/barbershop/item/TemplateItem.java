package me.basiqueevangelist.barbershop.item;

import io.wispforest.owo.nbt.NbtKey;
import me.basiqueevangelist.barbershop.cca.TheBarbershopCCA;
import me.basiqueevangelist.barbershop.client.ClientHaircutStore;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.item.TooltipData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class TemplateItem extends Item {
    public static final NbtKey<UUID> HAIRCUT = new NbtKey<>("HaircutId", NbtKey.Type.of(NbtElement.INT_ARRAY_TYPE, NbtCompound::getUuid, NbtCompound::putUuid));

    public TemplateItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if (FabricLoader.getInstance().getEnvironmentType() != EnvType.CLIENT) return;
        if (!stack.has(HAIRCUT)) return;

        var entry = ClientHaircutStore.get(stack.get(HAIRCUT));
        if (entry == null) {
            tooltip.add(Text.translatable("item.thebarbershop.template.notLoaded"));
            return;
        }

        String owner = entry.ownerName() != null ? entry.ownerName() : entry.ownerId().toString();

        tooltip.add(Text.literal("")
            .append(Text.literal(entry.name())
                .formatted(Formatting.GRAY))
            .append(Text.literal(" by "))
            .append(Text.literal(owner)
                .formatted(Formatting.AQUA)));

        if (context.isAdvanced()) {
            tooltip.add(Text.literal(entry.targetTexture().toString())
                .formatted(Formatting.DARK_GRAY));
        }
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (!user.getEntityWorld().isClient) {
            entity.getComponent(TheBarbershopCCA.HAIRCUT).setHaircutId(stack.get(HAIRCUT));
        }

        return ActionResult.SUCCESS;
    }

    @Override
    public Optional<TooltipData> getTooltipData(ItemStack stack) {
        if (FabricLoader.getInstance().getEnvironmentType() != EnvType.CLIENT) return Optional.empty();
        if (!stack.has(HAIRCUT)) return Optional.empty();

        var entry = ClientHaircutStore.get(stack.get(HAIRCUT));

        if (entry == null) return Optional.empty();

        return Optional.of(new Data(entry));
    }

    public record Data(ClientHaircutStore.Entry entry) implements TooltipData {

    }
}
