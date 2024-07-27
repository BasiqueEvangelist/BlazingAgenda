package me.basiqueevangelist.blazingagenda.cca;

import me.basiqueevangelist.blazingagenda.haircut.BlazingAgendaState;
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

public class CostumeComponent implements Component, AutoSyncedComponent, ServerTickingComponent {
    private final Entity entity;
    private UUID costumeId = Util.NIL_UUID;

    public CostumeComponent(Entity entity) {
        this.entity = entity;
    }

    public UUID costumeId() {
        return costumeId;
    }

    public void setCostumeId(UUID haircutId) {
        this.costumeId = haircutId;

        BlazingAgendaCCA.COSTUME.sync(entity);
    }

    @Override
    public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        if (tag.contains("CostumeId", NbtElement.INT_ARRAY_TYPE))
            costumeId = tag.getUuid("CostumeId");
        else
            costumeId = Util.NIL_UUID;
    }

    @Override
    public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        if (!costumeId.equals(Util.NIL_UUID))
            tag.putUuid("CostumeId", costumeId);
    }

    @Override
    public void writeSyncPacket(RegistryByteBuf buf, ServerPlayerEntity recipient) {
        buf.writeUuid(costumeId);
    }

    @Override
    public void applySyncPacket(RegistryByteBuf buf) {
        costumeId = buf.readUuid();
    }

    @Override
    public void serverTick() {
        if (entity.getEntityWorld().getTime() % 16 != 0) return;

        var state = BlazingAgendaState.get(entity.getServer());

        if (!costumeId.equals(Util.NIL_UUID) && state.costumes().get(costumeId) == null) {
            costumeId = Util.NIL_UUID;
            BlazingAgendaCCA.COSTUME.sync(entity);
        }
    }
}
