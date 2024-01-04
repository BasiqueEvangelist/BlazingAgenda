package me.basiqueevangelist.barbershop.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.basiqueevangelist.barbershop.cca.TheBarbershopCCA;
import me.basiqueevangelist.barbershop.client.ClientHaircutStore;
import me.basiqueevangelist.barbershop.client.EntityContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;

@Mixin(RenderLayer.class)
public class RenderLayerMixin {
    @ModifyExpressionValue(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Util;memoize(Ljava/util/function/Function;)Ljava/util/function/Function;"))
    private static Function<Identifier, RenderLayer> mald(Function<Identifier, RenderLayer> original) {
        return id -> {
            Entity current = EntityContext.current();

            if (current == null) return original.apply(id);

            UUID haircutId = current.getComponent(TheBarbershopCCA.HAIRCUT).haircutId();

            if (haircutId.equals(Util.NIL_UUID)) return original.apply(id);

            var entry = ClientHaircutStore.get(haircutId);

            if (entry == null) return original.apply(id);

            if (!entry.targetTexture().equals(id)) return original.apply(id);

            return original.apply(entry.texture().id());
        };
    }

    @ModifyExpressionValue(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Util;memoize(Ljava/util/function/BiFunction;)Ljava/util/function/BiFunction;"))
    private static BiFunction<Identifier, Boolean, RenderLayer> mald(BiFunction<Identifier, Boolean, RenderLayer> original) {
        return (id, bool) -> {
            Entity current = EntityContext.current();

            if (current == null) return original.apply(id, bool);

            UUID haircutId = current.getComponent(TheBarbershopCCA.HAIRCUT).haircutId();

            if (haircutId.equals(Util.NIL_UUID)) return original.apply(id, bool);

            var entry = ClientHaircutStore.get(haircutId);

            if (entry == null) return original.apply(id, bool);

            if (!entry.targetTexture().equals(id)) return original.apply(id, bool);

            return original.apply(entry.texture().id(), bool);
        };
    }
}
