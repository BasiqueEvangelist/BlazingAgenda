package me.basiqueevangelist.barbershop.cca;

import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent;
import me.basiqueevangelist.barbershop.haircut.HaircutsState;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Util;

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

        TheBarbershopCCA.HAIRCUT.sync(entity);
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        if (tag.contains("HaircutId", NbtElement.INT_ARRAY_TYPE))
            haircutId = tag.getUuid("HaircutId");
        else
            haircutId = Util.NIL_UUID;
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        if (!haircutId.equals(Util.NIL_UUID))
            tag.putUuid("HaircutId", haircutId);
    }

    @Override
    public void writeSyncPacket(PacketByteBuf buf, ServerPlayerEntity recipient) {
        buf.writeUuid(haircutId);
    }

    @Override
    public void applySyncPacket(PacketByteBuf buf) {
        haircutId = buf.readUuid();
    }

    @Override
    public void serverTick() {
        if (entity.getEntityWorld().getTime() % 16 != 0) return;

        var state = HaircutsState.get(entity.getServer());

        if (!haircutId.equals(Util.NIL_UUID) && state.haircuts().get(haircutId) == null) {
            haircutId = Util.NIL_UUID;
            TheBarbershopCCA.HAIRCUT.sync(entity);
        }
    }
}
