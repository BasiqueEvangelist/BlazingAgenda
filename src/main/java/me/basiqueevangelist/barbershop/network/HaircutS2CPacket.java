package me.basiqueevangelist.barbershop.network;

import java.util.Optional;
import java.util.UUID;

public record HaircutS2CPacket(UUID id, UUID ownerId, Optional<String> ownerName, String name, byte[] data) {
}
