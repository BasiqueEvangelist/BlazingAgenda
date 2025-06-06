package me.basiqueevangelist.blazingagenda.haircut;

import me.basiqueevangelist.blazingagenda.network.BlazingAgendaNetworking;
import me.basiqueevangelist.blazingagenda.network.ReloadS2CPacket;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WorldSavePath;
import net.minecraft.world.PersistentState;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BlazingAgendaState extends PersistentState {
    private final Map<UUID, CostumeEntry> costumes = new HashMap<>();
    private final Path costumesFolder;
    private final MinecraftServer server;

    private BlazingAgendaState(MinecraftServer server) {
        this.server = server;
        Path blazingFolder = server.getSavePath(WorldSavePath.ROOT).resolve("data").resolve("blazing-agenda");

        this.costumesFolder = blazingFolder.resolve("costumes").normalize();

        try {
            Files.createDirectories(blazingFolder);
            Files.createDirectories(costumesFolder);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private BlazingAgendaState(MinecraftServer server, NbtCompound tag) {
        this(server);

        NbtList costumesTag = tag.getList("Costumes", NbtElement.COMPOUND_TYPE);

        for (int i = 0; i < costumesTag.size(); i++) {
            NbtCompound costumeTag = costumesTag.getCompound(i);
            var entry = CostumeEntry.read(costumeTag);

            costumes.put(entry.id(), entry);
        }
    }

    public static BlazingAgendaState get(MinecraftServer server) {
        return server.getOverworld().getPersistentStateManager().getOrCreate(
            new Type<>(
                () -> new BlazingAgendaState(server),
                (tag, registries) -> new BlazingAgendaState(server, tag),
                null
            ),
            "blazing-agenda"
        );
    }

    public CostumeEntry addCostume(UUID playerId, String name, byte[] data) {
        UUID costumeId = UUID.randomUUID();

        while (costumes.containsKey(costumeId)) costumeId = UUID.randomUUID();

        Path imgPath = costumesFolder.resolve(costumeId + ".png");

        try {
            Files.write(imgPath, data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        var entry = new CostumeEntry(costumeId, playerId, name, data.length, costumesFolder.relativize(imgPath).toString());
        costumes.put(costumeId, entry);
        return entry;
    }

    public void updateCostume(CostumeEntry costume, byte[] data) {
        var imgPath = resolve(costume);

        try {
            Files.write(imgPath, data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        BlazingAgendaNetworking.CHANNEL.serverHandle(server).send(new ReloadS2CPacket(costume.id));
    }

    public Path resolve(CostumeEntry costume) {
        return costumesFolder.resolve(costume.path());
    }

    public void deleteHaircut(CostumeEntry costume) {
        costumes.remove(costume.id);

        try {
            Files.deleteIfExists(resolve(costume));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<UUID, CostumeEntry> costumes() {
        return costumes;
    }

    @Override
    public boolean isDirty() {
        return true;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        NbtList costumesTag = new NbtList();
        tag.put("Costumes", costumesTag);

        for (var entry : costumes.entrySet()) {
            NbtCompound costumeTag = entry.getValue().write(new NbtCompound());
            costumeTag.putUuid("UUID", entry.getKey());
            costumesTag.add(costumeTag);
        }

        return tag;
    }

    public record CostumeEntry(UUID id, UUID ownerId, String name, int sizeInBytes, String path) {
        public static CostumeEntry read(NbtCompound tag) {
            UUID id = tag.getUuid("UUID");
            UUID ownerId = tag.getUuid("OwnerUUID");
            String name = tag.getString("Name");
            int sizeInBytes = tag.getInt("SizeInBytes");
            String path = tag.getString("Path");

            return new CostumeEntry(id, ownerId, name, sizeInBytes, path);
        }

        public NbtCompound write(NbtCompound tag) {
            tag.putUuid("UUID", id);
            tag.putUuid("OwnerUUID", ownerId);
            tag.putString("Name", name);
            tag.putInt("SizeInBytes", sizeInBytes);
            tag.putString("Path", path);

            return tag;
        }
    }
}
