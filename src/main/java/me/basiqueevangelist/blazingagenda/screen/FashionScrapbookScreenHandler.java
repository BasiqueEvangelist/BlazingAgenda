package me.basiqueevangelist.blazingagenda.screen;

import com.mojang.authlib.GameProfile;
import io.wispforest.endec.Endec;
import io.wispforest.endec.impl.BuiltInEndecs;
import io.wispforest.endec.impl.StructEndecBuilder;
import io.wispforest.owo.client.screens.SlotGenerator;
import me.basiqueevangelist.blazingagenda.BlazingAgendaPermissions;
import me.basiqueevangelist.blazingagenda.haircut.BlazingAgendaState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class FashionScrapbookScreenHandler extends ScreenHandler {
    public Data data;

    private Hand hand;

    public Runnable refreshHandler;

    public Consumer<UploadSucceeded> uploadSucceeded;
    public Consumer<UploadRejected> uploadRejected;

    public FashionScrapbookScreenHandler(int syncId, PlayerInventory inv, Data data, Hand hand) {
        this(syncId, inv, data);

        this.hand = hand;
    }

    public FashionScrapbookScreenHandler(int syncId, PlayerInventory inv, Data data) {
        super(BlazingAgendaScreenHandlers.FASHION_SCRAPBOOK, syncId);
        this.data = data;

        SlotGenerator.begin(this::addSlot, 0, -10000)
            .playerInventory(inv);

        addServerboundMessage(DeleteCostume.class, this::deleteCostume);
        addClientboundMessage(Data.class, newData -> {
            this.data = newData;

            if (refreshHandler != null) refreshHandler.run();
        });

        addServerboundMessage(UploadCostume.class, this::onUploadCostume);
        addServerboundMessage(UpdateCostume.class, this::onUpdateCostume);
        addClientboundMessage(UploadSucceeded.class, packet -> {
            if (uploadSucceeded != null) uploadSucceeded.accept(packet);
        });
        addClientboundMessage(UploadRejected.class, packet -> {
            if (uploadRejected != null) uploadRejected.accept(packet);
        });
    }

    private void deleteCostume(DeleteCostume packet) {
        if (!(player() instanceof ServerPlayerEntity player)) return;
        BlazingAgendaState state = BlazingAgendaState.get(player.server);

        var cut = state.costumes().get(packet.id());
        if (!cut.ownerId().equals(player.getUuid()) && !BlazingAgendaPermissions.canDelete(player)) return;

        state.deleteHaircut(cut);

        sendMessage(Data.gather(player));
    }

    private void onUploadCostume(UploadCostume packet) {
        ServerPlayerEntity player = (ServerPlayerEntity) player();

        if (!BlazingAgendaPermissions.canManageAssets(player)) {
            sendMessage(new UploadRejected(packet.name(), Text.translatable("message.blazing-agenda.permissionDenied")));
            return;
        }

        BlazingAgendaState state = BlazingAgendaState.get(player.server);

        var haircut = state.addCostume(player.getUuid(), packet.name(), packet.pngData());

        sendMessage(new UploadSucceeded(haircut.name(), haircut.id()));
        sendMessage(Data.gather(player));
    }

    private void onUpdateCostume(UpdateCostume packet) {
        ServerPlayerEntity player = (ServerPlayerEntity) player();

        if (!BlazingAgendaPermissions.canManageAssets(player)) {
            sendMessage(new UploadRejected(packet.id().toString(), Text.translatable("message.blazing-agenda.permissionDenied")));
            return;
        }

        BlazingAgendaState state = BlazingAgendaState.get(player.server);

        var costume = state.costumes().get(packet.id());

        if (costume == null) return;

        state.updateCostume(costume, packet.pngData());

        sendMessage(Data.gather(player));
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return null;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    public record DeleteCostume(UUID id) { }

    public record UploadCostume(String name, byte[] pngData) { }
    public record UpdateCostume(UUID id, byte[] pngData) { }
    public record UploadSucceeded(String name, UUID id) { }
    public record UploadRejected(String name, Text errorMessage) { }

    public record Data(List<CostumeEntry> costumes) {
        public static final Endec<Data> ENDEC = StructEndecBuilder.of(
            CostumeEntry.ENDEC.listOf().fieldOf("costumes", Data::costumes),
            Data::new
        );

        public static Data gather(ServerPlayerEntity player) {
            BlazingAgendaState state = BlazingAgendaState.get(player.server);
            List<CostumeEntry> costumes = new ArrayList<>();

            for (var costume : state.costumes().values()) {
                String ownerName = player.server.getUserCache().getByUuid(costume.ownerId()).map(GameProfile::getName).orElse(costume.ownerId().toString());

                byte[] data;

                try {
                    data = Files.readAllBytes(state.resolve(costume));
                } catch (IOException e) {
                    // todo: make this better uwu
                    throw new RuntimeException("explosion", e);
                }

                boolean canDelete = costume.ownerId().equals(player.getUuid()) || BlazingAgendaPermissions.canDelete(player);

                costumes.add(new CostumeEntry(costume.id(), costume.name(), ownerName, canDelete, data));
            }

            return new Data(costumes);
        }
    }

    public record CostumeEntry(UUID id, String name, String ownerName, boolean canDelete, byte[] data) {
        public static final Endec<CostumeEntry> ENDEC = StructEndecBuilder.of(
            BuiltInEndecs.UUID.fieldOf("id", CostumeEntry::id),
            Endec.STRING.fieldOf("name", CostumeEntry::name),
            Endec.STRING.fieldOf("ownerName", CostumeEntry::ownerName),
            Endec.BOOLEAN.fieldOf("canDelete", CostumeEntry::canDelete),
            Endec.BYTES.fieldOf("data", CostumeEntry::data),
            CostumeEntry::new
        );
    }
}
