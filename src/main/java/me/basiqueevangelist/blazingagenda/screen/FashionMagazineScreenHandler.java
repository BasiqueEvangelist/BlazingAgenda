package me.basiqueevangelist.blazingagenda.screen;

import com.mojang.authlib.GameProfile;
import io.wispforest.endec.Endec;
import io.wispforest.endec.impl.BuiltInEndecs;
import io.wispforest.endec.impl.StructEndecBuilder;
import io.wispforest.endec.impl.StructField;
import io.wispforest.owo.client.screens.SlotGenerator;
import io.wispforest.owo.serialization.CodecUtils;
import io.wispforest.owo.serialization.endec.MinecraftEndecs;
import me.basiqueevangelist.blazingagenda.haircut.BlazingAgendaState;
import me.basiqueevangelist.blazingagenda.haircut.HaircutLimits;
import me.basiqueevangelist.blazingagenda.item.BlazingAgendaComponents;
import me.basiqueevangelist.blazingagenda.item.BlazingAgendaItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.Uuids;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FashionMagazineScreenHandler extends ScreenHandler {
    public final Data data;

    private Hand hand;

    public FashionMagazineScreenHandler(int syncId, PlayerInventory inv, Data data, Hand hand) {
        this(syncId, inv, data);

        this.hand = hand;
    }

    public FashionMagazineScreenHandler(int syncId, PlayerInventory inv, Data data) {
        super(BlazingAgendaScreenHandlers.FASHION_MAGAZINE, syncId);
        this.data = data;

        SlotGenerator.begin(this::addSlot, 0, -10000)
            .playerInventory(inv);

        addServerboundMessage(SetCostumeId.class, this::setCostumeId);
    }

    private void setCostumeId(SetCostumeId packet) {
        if (!(player() instanceof ServerPlayerEntity player)) return;

        var handStack = player.getStackInHand(hand);

        if (!handStack.isOf(BlazingAgendaItems.FASHION_MAGAZINE)) return;

        handStack.set(BlazingAgendaComponents.COSTUME_ID, packet.id());
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return null;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    public record SetCostumeId(UUID id) { }

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

                costumes.add(new CostumeEntry(costume.id(), costume.name(), ownerName, data));
            }

            return new Data(costumes);
        }
    }

    public record CostumeEntry(UUID id, String name, String ownerName, byte[] data) {
        public static final Endec<CostumeEntry> ENDEC = StructEndecBuilder.of(
            BuiltInEndecs.UUID.fieldOf("id", CostumeEntry::id),
            Endec.STRING.fieldOf("name", CostumeEntry::name),
            Endec.STRING.fieldOf("ownerName", CostumeEntry::ownerName),
            Endec.BYTES.fieldOf("data", CostumeEntry::data),
            CostumeEntry::new
        );
    }
}
