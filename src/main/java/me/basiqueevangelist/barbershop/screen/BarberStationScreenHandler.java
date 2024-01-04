package me.basiqueevangelist.barbershop.screen;

import io.wispforest.owo.client.screens.SlotGenerator;
import me.basiqueevangelist.barbershop.haircut.HaircutLimits;
import me.basiqueevangelist.barbershop.haircut.HaircutsState;
import me.basiqueevangelist.barbershop.item.TemplateItem;
import me.basiqueevangelist.barbershop.item.TheBarbershopItems;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

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

    public Consumer<HaircutList> haircutList;

    public BarberStationScreenHandler(int syncId, PlayerInventory inv) {
        super(TheBarbershopScreenHandlers.BARBER_STATION, syncId);
        this.inv = inv;

        SlotGenerator.begin(this::addSlot, 0, -10000)
            .playerInventory(inv);

        addServerboundMessage(UploadHaircut.class, this::onUploadHaircut);
        addServerboundMessage(ListHaircuts.class, this::onListHaircuts);
        addServerboundMessage(DeleteHaircut.class, this::onDeleteHaircut);
        addServerboundMessage(ExchangeHaircut.class, this::onExchangeHaircut);

        addClientboundMessage(UploadSucceeded.class, packet -> {
            if (uploadSucceeded != null) uploadSucceeded.accept(packet);
        });

        addClientboundMessage(UploadRejected.class, packet -> {
            if (uploadRejected != null) uploadRejected.accept(packet);
        });

        addClientboundMessage(HaircutList.class, packet -> {
            if (haircutList != null) haircutList.accept(packet);
        });
    }

    @SuppressWarnings("UnstableApiUsage")
    private void onExchangeHaircut(ExchangeHaircut packet) {
        HaircutsState state = HaircutsState.get(player().getServer());

        var cut = state.haircuts().get(packet.id());
        if (!cut.ownerId().equals(player().getUuid())) return;

        var storage = PlayerInventoryStorage.of(inv);

        NbtCompound tag = new NbtCompound();
        tag.put(TemplateItem.HAIRCUT, packet.id());
        var template = ItemVariant.of(TheBarbershopItems.TEMPLATE, tag);

        try (var tx = Transaction.openOuter()) {
            var total = storage.extract(ItemVariant.of(TheBarbershopItems.EMPTY_TEMPLATE), packet.max(), tx);

            storage.offerOrDrop(template, total, tx);

            tx.commit();
        }
    }

    private void onDeleteHaircut(DeleteHaircut packet) {
        HaircutsState state = HaircutsState.get(player().getServer());

        var cut = state.haircuts().get(packet.id());
        if (!cut.ownerId().equals(player().getUuid())) return;

        state.deleteHaircut(cut);
    }

    private void onListHaircuts(ListHaircuts packet) {
        HaircutsState state = HaircutsState.get(player().getServer());
        List<HaircutEntry> haircuts = new ArrayList<>();

        for (var haircut : state.haircuts().values()) {
            if (!haircut.ownerId().equals(player().getUuid())) continue;

            byte[] data;

            try {
                data = Files.readAllBytes(state.resolve(haircut));
            } catch (IOException e) {
                // todo: make this better uwu
                throw new RuntimeException("explosion", e);
            }

            haircuts.add(new HaircutEntry(haircut.id(), haircut.name(), data));
        }

        sendMessage(new HaircutList(haircuts));
    }

    private void onUploadHaircut(UploadHaircut packet) {
        HaircutsState state = HaircutsState.get(player().getServer());

        if (state.totalHaircutsSize(player().getUuid()) + packet.pngData().length > HaircutLimits.maxTotalSize((ServerPlayerEntity) player())) {
            // Not enough space.
            sendMessage(new UploadRejected(packet.name(), Text.translatable("message.thebarbershop.notEnoughSpace")));
            return;
        }

        var haircut = state.add(player().getUuid(), packet.name(), packet.pngData());

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
    public record ExchangeHaircut(UUID id, int max) {}

    public record ListHaircuts() { }
    public record HaircutList(List<HaircutEntry> haircuts) { }

    public record HaircutEntry(UUID id, String name, byte[] data) { }
}
