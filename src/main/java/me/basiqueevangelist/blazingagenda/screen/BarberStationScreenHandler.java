package me.basiqueevangelist.blazingagenda.screen;

import com.mojang.authlib.GameProfile;
import io.wispforest.owo.client.screens.SlotGenerator;
import me.basiqueevangelist.blazingagenda.haircut.HaircutLimits;
import me.basiqueevangelist.blazingagenda.haircut.BlazingAgendaState;
import me.basiqueevangelist.blazingagenda.item.BlazingAgendaComponents;
import me.basiqueevangelist.blazingagenda.item.BlazingAgendaItems;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.component.ComponentChanges;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class BarberStationScreenHandler extends ScreenHandler {
    private final PlayerInventory inv;
    // todo: make this good
    public Consumer<UploadSucceeded> uploadSucceeded;
    public Consumer<UploadRejected> uploadRejected;

    public Consumer<InfoResponse> infoResponse;

    public BarberStationScreenHandler(int syncId, PlayerInventory inv) {
        super(BlazingAgendaScreenHandlers.BARBER_STATION, syncId);
        this.inv = inv;

        SlotGenerator.begin(this::addSlot, 0, -10000)
            .playerInventory(inv);

        addServerboundMessage(UploadHaircut.class, this::onUploadHaircut);
        addServerboundMessage(RequestInfo.class, this::onRequestInfo);
        addServerboundMessage(DeleteHaircut.class, this::onDeleteHaircut);

        addClientboundMessage(UploadSucceeded.class, packet -> {
            if (uploadSucceeded != null) uploadSucceeded.accept(packet);
        });

        addClientboundMessage(UploadRejected.class, packet -> {
            if (uploadRejected != null) uploadRejected.accept(packet);
        });

        addClientboundMessage(InfoResponse.class, packet -> {
            if (infoResponse != null) infoResponse.accept(packet);
        });
    }

    private void onDeleteHaircut(DeleteHaircut packet) {
        ServerPlayerEntity player = (ServerPlayerEntity) player();
        BlazingAgendaState state = BlazingAgendaState.get(player.server);

        var cut = state.costumes().get(packet.id());
        if (!cut.ownerId().equals(player.getUuid()) && !HaircutLimits.canDelete(player)) return;

        state.deleteHaircut(cut);
    }

    private void onRequestInfo(RequestInfo packet) {
        ServerPlayerEntity player = (ServerPlayerEntity) player();
        BlazingAgendaState state = BlazingAgendaState.get(player.server);
        List<HaircutEntry> haircuts = new ArrayList<>();

        for (var haircut : state.costumes().values()) {
            String ownerName = player.server.getUserCache().getByUuid(haircut.ownerId()).map(GameProfile::getName).orElse(haircut.ownerId().toString());

            byte[] data;

            try {
                data = Files.readAllBytes(state.resolve(haircut));
            } catch (IOException e) {
                // todo: make this better uwu
                throw new RuntimeException("explosion", e);
            }

            boolean canDelete = haircut.ownerId().equals(player.getUuid()) || HaircutLimits.canDelete(player);

            haircuts.add(new HaircutEntry(haircut.id(), haircut.name(), ownerName, canDelete, data));
        }

        sendMessage(new InfoResponse(
            haircuts,
            HaircutLimits.canUpload(player)
        ));
    }

    private void onUploadHaircut(UploadHaircut packet) {
        ServerPlayerEntity player = (ServerPlayerEntity) player();

        if (!HaircutLimits.canUpload(player)) {
            sendMessage(new UploadRejected(packet.name(), Text.translatable("message.blazing-agenda.permissionDenied")));
            return;
        }

        BlazingAgendaState state = BlazingAgendaState.get(player.server);

        var haircut = state.add(player.getUuid(), packet.name(), packet.pngData());

        sendMessage(new UploadSucceeded(haircut.name(), haircut.id()));
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return null;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    public record UploadHaircut(String name, byte[] pngData) { }
    public record UploadSucceeded(String name, UUID id) { }
    public record UploadRejected(String name, Text errorMessage) { }

    public record DeleteHaircut(UUID id) {}

    public record RequestInfo() { }
    public record InfoResponse(List<HaircutEntry> haircuts, boolean canUpload) { }

    public record HaircutEntry(UUID id, String name, String ownerName, boolean canDelete, byte[] data) { }
}
