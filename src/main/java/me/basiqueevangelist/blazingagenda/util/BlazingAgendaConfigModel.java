package me.basiqueevangelist.blazingagenda.util;

import io.wispforest.owo.config.Option;
import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.Modmenu;
import io.wispforest.owo.config.annotation.Sync;

@Config(name = "blazing-agenda", wrapperName = "BlazingAgendaConfig")
@Modmenu(modId = "blazing-agenda")
public class BlazingAgendaConfigModel {
    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    public boolean everybodyCanManageAssets = false;
    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    public boolean everybodyCanApplyCostumes = true;
    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    public boolean everybodyCanDeleteOtherCostumes = false;
    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    public boolean everybodyCanUpdateOtherCostumes = false;
    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    public int maxCostumeSlots = 100;
    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    public int maxTotalStorage = 128*1024*1024;
}
