package me.basiqueevangelist.barbershop.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.basiqueevangelist.barbershop.client.EntityMorphing;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.BiFunction;
import java.util.function.Function;

@Mixin(RenderLayer.class)
public class RenderLayerMixin {
    @ModifyExpressionValue(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Util;memoize(Ljava/util/function/Function;)Ljava/util/function/Function;"))
    private static Function<Identifier, RenderLayer> mald(Function<Identifier, RenderLayer> original) {
        return id -> original.apply(EntityMorphing.morphTextureId(id));
    }

    @ModifyExpressionValue(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Util;memoize(Ljava/util/function/BiFunction;)Ljava/util/function/BiFunction;"))
    private static BiFunction<Identifier, Boolean, RenderLayer> mald(BiFunction<Identifier, Boolean, RenderLayer> original) {
        return (id, bool) -> original.apply(EntityMorphing.morphTextureId(id), bool);
    }
}
