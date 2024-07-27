package me.basiqueevangelist.blazingagenda.client;

import me.basiqueevangelist.blazingagenda.cca.BlazingAgendaCCA;
import net.minecraft.entity.Entity;
import net.minecraft.util.Util;

import java.util.UUID;

public class HaircutContext {
    public static UUID HAIRCUT_ID = Util.NIL_UUID;

    public static UUID haircutId() {
        if (!Util.NIL_UUID.equals(HAIRCUT_ID)) return HAIRCUT_ID;

        Entity current = EntityContext.current();

        if (current == null) return Util.NIL_UUID;

        return current.getComponent(BlazingAgendaCCA.HAIRCUT).haircutId();
    }
}
