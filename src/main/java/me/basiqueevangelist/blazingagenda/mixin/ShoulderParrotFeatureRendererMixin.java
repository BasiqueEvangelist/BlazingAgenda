package me.basiqueevangelist.blazingagenda.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import me.basiqueevangelist.blazingagenda.client.ClientCostumeStore;
import net.minecraft.client.render.entity.feature.ShoulderParrotFeatureRenderer;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ShoulderParrotFeatureRenderer.class)
public class ShoulderParrotFeatureRendererMixin {
    @ModifyExpressionValue(method = "method_17958", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/ParrotEntityRenderer;getTexture(Lnet/minecraft/entity/passive/ParrotEntity$Variant;)Lnet/minecraft/util/Identifier;"))
    private Identifier useHaircut(Identifier original, @Local(argsOnly = true) NbtCompound tag) {
        var ccaTag = tag.getCompound("cardinal_components");
        var haircutComponent = ccaTag.getCompound("blazing-agenda:haircut");

        if (!haircutComponent.contains("HaircutId")) return original;

        var haircutId = haircutComponent.getUuid("HaircutId");

        if (haircutId.equals(Util.NIL_UUID)) return original;

        var entry = ClientCostumeStore.get(haircutId);

        if (entry == null) return original;

        return entry.texture().id();
    }
}
