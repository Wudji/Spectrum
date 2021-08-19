package de.dafuqs.spectrum.items.armor;

import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.sound.SpectrumSoundEvents;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.World;

import java.util.List;

public class GlowVisionHelmet extends ArmorItem {

    public GlowVisionHelmet(ArmorMaterial material, EquipmentSlot slot, Settings settings) {
        super(material, slot, settings);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if(world != null && world.getTimeOfDay() % 20 == 0 && entity instanceof PlayerEntity && slot == EquipmentSlot.HEAD.getEntitySlotId()) {
            PlayerEntity playerEntity = (PlayerEntity) entity;
            int lightLevelAtPlayerPos = world.getLightLevel(playerEntity.getBlockPos());

            if(lightLevelAtPlayerPos < 7) {
                StatusEffectInstance nightVisionInstance = playerEntity.getStatusEffect(StatusEffects.NIGHT_VISION);
                if (nightVisionInstance == null || nightVisionInstance.getDuration() < 10 * 20) { // prevent "night vision running out" flashing
                    // no / short night vision => search for glow ink and add night vision if found

                    PlayerInventory playerInventory = null;
                    int glowInkStackSlot = -1;
                    if(!playerEntity.isCreative()) {
                        playerInventory = playerEntity.getInventory();
                        glowInkStackSlot = playerInventory.getSlotWithStack(new ItemStack(Items.GLOW_INK_SAC));
                    }
                    if (glowInkStackSlot >= 0 || playerEntity.isCreative()) {
                        if (!world.isClient) {
                            if(!playerEntity.isCreative()) {
                                ItemStack glowInkStack = playerInventory.getStack(glowInkStackSlot);
                                glowInkStack.decrement(1);
                                playerInventory.setStack(glowInkStackSlot, glowInkStack);
                            }
                            StatusEffectInstance newNightVisionInstance = new StatusEffectInstance(StatusEffects.NIGHT_VISION, 20 * SpectrumCommon.CONFIG.GlowVisionGogglesDuration);
                            playerEntity.addStatusEffect(newNightVisionInstance);

                            world.playSoundFromEntity(null, playerEntity, SpectrumSoundEvents.ITEM_ARMOR_EQUIP_GLOW_VISION, SoundCategory.PLAYERS, 0.2F, 1.0F);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void appendTooltip(ItemStack itemStack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
        super.appendTooltip(itemStack, world, tooltip, tooltipContext);
        tooltip.add(new TranslatableText("item.spectrum.glow_vision_helmet.tooltip"));
    }
}