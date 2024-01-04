package me.basiqueevangelist.barbershop.mixin;

import me.basiqueevangelist.barbershop.client.EntityMorphing;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(VertexConsumerProvider.Immediate.class)
public class VertexConsumerProviderImmediateMixin {
    @ModifyVariable(method = "getBuffer", at = @At("HEAD"), argsOnly = true)
    private RenderLayer morphLayer(RenderLayer layer) {
        return EntityMorphing.morphRenderLayer(layer);
    }
}
