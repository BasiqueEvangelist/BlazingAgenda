package me.basiqueevangelist.blazingagenda.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

public interface EarlyUseOnEntity {
    ActionResult useOn(ItemStack stack, PlayerEntity user, Entity entity, Hand hand);
}
