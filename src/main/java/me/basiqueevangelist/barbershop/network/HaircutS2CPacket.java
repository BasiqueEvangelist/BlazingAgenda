package me.basiqueevangelist.barbershop.network;

import com.mojang.authlib.GameProfile;
import net.minecraft.util.Identifier;

import java.util.Optional;
import java.util.UUID;

public record HaircutS2CPacket(UUID id, UUID ownerId, Optional<String> ownerName, String name, Identifier targetTexture,
                               byte[] data) {
}
