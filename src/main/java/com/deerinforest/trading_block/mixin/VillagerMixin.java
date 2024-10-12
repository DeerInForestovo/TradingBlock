package com.deerinforest.trading_block.mixin;

import com.deerinforest.trading_block.item.ModItems;
import com.deerinforest.trading_block.item.custom.TradingAgreement;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.village.VillagerProfession;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(VillagerEntity.class)
public class VillagerMixin {

	@Inject(method = "interactMob", at = @At("HEAD"), cancellable = true)
	public void interactMob(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
		VillagerEntity villager = (VillagerEntity) (Object) this;
		ItemStack stack = player.getStackInHand(hand);
		if (stack.getItem() == ModItems.TRADING_AGREEMENT
				&& villager.getVillagerData().getProfession() != VillagerProfession.NONE
				&& villager.getVillagerData().getProfession() != VillagerProfession.NITWIT) {
			cir.setReturnValue(ActionResult.SUCCESS);
			TradingAgreement.SignWithVillager(stack, villager.getOffers());
		}
	}
}
