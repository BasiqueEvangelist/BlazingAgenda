package me.basiqueevangelist.blazingagenda.client;

import me.basiqueevangelist.blazingagenda.cca.BlazingAgendaCCA;
import net.minecraft.entity.Entity;
import net.minecraft.util.Util;

import java.util.UUID;

public class CostumeContext {
    public static UUID COSTUME_ID = Util.NIL_UUID;

    public static UUID costumeId() {
        if (!Util.NIL_UUID.equals(COSTUME_ID)) return COSTUME_ID;

        Entity current = EntityContext.current();

        if (current == null) return Util.NIL_UUID;

        return current.getComponent(BlazingAgendaCCA.COSTUME).costumeId();
    }
}
