package de.dafuqs.spectrum.mixin;

import de.dafuqs.spectrum.items.magic_items.BuildingStaffItem;
import de.dafuqs.spectrum.registries.SpectrumBlockTags;
import de.dafuqs.spectrum.registries.SpectrumBlocks;
import de.dafuqs.spectrum.registries.SpectrumItems;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.GlassBottleItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(GlassBottleItem.class)
public abstract class GlassBottleItemMixin {
	
	@Shadow protected abstract ItemStack fill(ItemStack stack, PlayerEntity player, ItemStack outputStack);
	
	@Inject(method = "use(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/TypedActionResult;",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/fluid/FluidState;isIn(Lnet/minecraft/tag/Tag;)Z"),
			cancellable = true,
			locals = LocalCapture.CAPTURE_FAILHARD)
	public void onUse(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult> cir, List list, ItemStack handStack, HitResult areaEffectCloudEntity, BlockPos blockPos) {
		BlockState blockState = world.getBlockState(blockPos);
		if (blockState.isIn(SpectrumBlockTags.DECAY)) {
			world.setBlockState(blockPos, Blocks.AIR.getDefaultState());
			world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ITEM_BOTTLE_FILL_DRAGONBREATH, SoundCategory.NEUTRAL, 1.0F, 1.0F);
			if(blockState.isOf(SpectrumBlocks.FADING)) {
				cir.setReturnValue(TypedActionResult.success(this.fill(handStack, user, SpectrumItems.BOTTLE_OF_FADING.getDefaultStack()), world.isClient()));
			} else if(blockState.isOf(SpectrumBlocks.FAILING)) {
				cir.setReturnValue(TypedActionResult.success(this.fill(handStack, user, SpectrumItems.BOTTLE_OF_FAILING.getDefaultStack()), world.isClient()));
			} else if(blockState.isOf(SpectrumBlocks.RUIN)) {
				cir.setReturnValue(TypedActionResult.success(this.fill(handStack, user, SpectrumItems.BOTTLE_OF_RUIN.getDefaultStack()), world.isClient()));
			} else if(blockState.isOf(SpectrumBlocks.TERROR)) {
				cir.setReturnValue(TypedActionResult.success(this.fill(handStack, user, SpectrumItems.BOTTLE_OF_TERROR.getDefaultStack()), world.isClient()));
			}
		}
		
	}
	
}