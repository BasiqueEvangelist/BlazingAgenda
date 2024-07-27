package me.basiqueevangelist.blazingagenda.cca;

import me.basiqueevangelist.blazingagenda.haircut.HaircutsState;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Util;
import org.ladysnake.cca.api.v3.component.Component;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;

import java.util.UUID;

public class HaircutComponent implements Component, AutoSyncedComponent, ServerTickingComponent {
    private final Entity entity;
    private UUID haircutId = Util.NIL_UUID;

    public HaircutComponent(Entity entity) {
        this.entity = entity;
    }

    public UUID haircutId() {
        return haircutId;
    }

    public void setHaircutId(UUID haircutId) {
        this.haircutId = haircutId;

        BlazingAgendaCCA.HAIRCUT.sync(entity);
    }

    @Override
    public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        if (tag.contains("HaircutId", NbtElement.INT_ARRAY_TYPE))
            haircutId = tag.getUuid("HaircutId");
        else
            haircutId = Util.NIL_UUID;
    }

    @Override
    public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        if (!haircutId.equals(Util.NIL_UUID))
            tag.putUuid("HaircutId", haircutId);
    }

    @Override
    public void writeSyncPacket(RegistryByteBuf buf, ServerPlayerEntity recipient) {
        buf.writeUuid(haircutId);
    }

    @Override
    public void applySyncPacket(RegistryByteBuf buf) {
        haircutId = buf.readUuid();
    }

    @Override
    public void serverTick() {
        if (entity.getEntityWorld().getTime() % 16 != 0) return;

        var state = HaircutsState.get(entity.getServer());

        if (!haircutId.equals(Util.NIL_UUID) && state.haircuts().get(haircutId) == null) {
            haircutId = Util.NIL_UUID;
            BlazingAgendaCCA.HAIRCUT.sync(entity);
        }
    }
}
