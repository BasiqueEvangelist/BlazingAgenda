package me.basiqueevangelist.barbershop.haircut;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.WorldSavePath;
import net.minecraft.world.PersistentState;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class HaircutsState extends PersistentState {
    private final Map<UUID, HaircutEntry> haircuts = new HashMap<>();
    private final MinecraftServer server;
    private final Path haircutsFolder;

    private HaircutsState(MinecraftServer server) {
        this.server = server;
        this.haircutsFolder = server.getSavePath(WorldSavePath.ROOT).resolve("data").resolve("haircuts").normalize();

        try {
            Files.createDirectories(haircutsFolder);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private HaircutsState(MinecraftServer server, NbtCompound tag) {
        this(server);

        NbtList haircutsTag = tag.getList("Haircuts", NbtElement.COMPOUND_TYPE);

        for (int i = 0; i < haircutsTag.size(); i++) {
            NbtCompound haircutTag = haircutsTag.getCompound(i);
            var entry = HaircutEntry.read(haircutTag);

            haircuts.put(entry.id(), entry);
        }
    }

    public static HaircutsState get(MinecraftServer server) {
        return server.getOverworld().getPersistentStateManager().getOrCreate(
            tag -> new HaircutsState(server, tag),
            () -> new HaircutsState(server),
            "thebarbershop"
        );
    }

    public int totalHaircutsSize(UUID playerId) {
        int totalSize = 0;

        for (var value : haircuts.values()) {
            if (!value.ownerId.equals(playerId)) continue;

            totalSize += value.sizeInBytes();
        }

        return totalSize;
    }

    public HaircutEntry add(UUID playerId, String name, byte[] data) {
        UUID haircutId = UUID.randomUUID();

        while (haircuts.containsKey(haircutId)) haircutId = UUID.randomUUID();

        Path imgPath = haircutsFolder.resolve(haircutId + ".png");

        try {
            Files.write(imgPath, data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        var entry = new HaircutEntry(haircutId, playerId, name, data.length, haircutsFolder.relativize(imgPath).toString());
        haircuts.put(haircutId, entry);
        return entry;
    }

    public Path resolve(HaircutEntry haircut) {
        return haircutsFolder.resolve(haircut.path());
    }

    public void deleteHaircut(HaircutEntry cut) {
        haircuts.remove(cut.id);

        try {
            Files.deleteIfExists(resolve(cut));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<UUID, HaircutEntry> haircuts() {
        return haircuts;
    }

    @Override
    public boolean isDirty() {
        return true;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        NbtList haircutsTag = new NbtList();
        tag.put("Haircuts", haircutsTag);

        for (var entry : haircuts.entrySet()) {
            NbtCompound haircutTag = entry.getValue().write(new NbtCompound());
            haircutTag.putUuid("UUID", entry.getKey());
            haircutsTag.add(haircutTag);
        }

        return tag;
    }

    public record HaircutEntry(UUID id, UUID ownerId, String name, int sizeInBytes, String path) {
        public static HaircutEntry read(NbtCompound tag) {
            UUID id = tag.getUuid("UUID");
            UUID ownerId = tag.getUuid("OwnerUUID");
            String name = tag.getString("Name");
            int sizeInBytes = tag.getInt("SizeInBytes");
            String path = tag.getString("Path");

            return new HaircutEntry(id, ownerId, name, sizeInBytes, path);
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
