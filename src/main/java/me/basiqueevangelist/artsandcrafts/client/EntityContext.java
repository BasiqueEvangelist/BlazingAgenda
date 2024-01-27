package me.basiqueevangelist.artsandcrafts.client;

import net.minecraft.entity.Entity;

public class EntityContext {
    private static Entity current;

    public static Entity current() {
        return current;
    }

    public static void setCurrent(Entity to) {
        current = to;
    }
}
