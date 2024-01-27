package me.basiqueevangelist.artsandcrafts.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.util.UUID;

public final class EntityMorphing {
    private EntityMorphing() {

    }

    public static Identifier morphTextureId(Identifier original) {
        Entity current = EntityContext.current();

        if (current == null) return original;

        UUID haircutId = HaircutContext.haircutId();

        if (haircutId.equals(Util.NIL_UUID)) return original;

        var entry = ClientHaircutStore.get(haircutId);

        if (entry == null) return original;

        var currentRenderer = MinecraftClient.getInstance().getEntityRenderDispatcher().getRenderer(current);

        if (!original.equals(currentRenderer.getTexture(current))) return original;

        return entry.texture().id();
    }

    public static RenderLayer morphRenderLayer(RenderLayer original) {
        Entity current = EntityContext.current();

        if (current == null) return original;

        UUID haircutId = HaircutContext.haircutId();

        if (haircutId.equals(Util.NIL_UUID)) return original;

        var entry = ClientHaircutStore.get(haircutId);

        if (entry == null) return original;

        if (!(original instanceof RenderLayer.MultiPhase multiPhase)) return original;
        if (!(multiPhase.getPhases().texture instanceof RenderPhase.Texture tex)) return original;
        if (tex.getId().isEmpty()) return original;

        Identifier newId = morphTextureId(tex.getId().get());

        if (newId.equals(tex.getId().get())) return original;

        // TODO: cache this

        RenderLayer.MultiPhaseParameters origParams = multiPhase.getPhases();
        RenderLayer.MultiPhaseParameters newParams = RenderLayer.MultiPhaseParameters.builder()
            .texture(new RenderPhase.Texture(newId, tex.blur, tex.mipmap))
            .program(origParams.program)
            .transparency(origParams.transparency)
            .depthTest(origParams.depthTest)
            .cull(origParams.cull)
            .lightmap(origParams.lightmap)
            .overlay(origParams.overlay)
            .layering(origParams.layering)
            .target(origParams.target)
            .texturing(origParams.texturing)
            .writeMaskState(origParams.writeMaskState)
            .lineWidth(origParams.lineWidth)
            .colorLogic(origParams.colorLogic)
            .build(origParams.outlineMode);

        return RenderLayer.of(
            original.name,
            original.getVertexFormat(),
            original.getDrawMode(),
            original.getExpectedBufferSize(),
            original.hasCrumbling(),
            original.translucent,
            newParams
        );
    }
}
