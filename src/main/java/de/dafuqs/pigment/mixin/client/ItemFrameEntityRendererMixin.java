package de.dafuqs.pigment.mixin.client;

import de.dafuqs.pigment.entity.PigmentEntityTypes;
import net.minecraft.client.render.entity.ItemFrameEntityRenderer;
import net.minecraft.entity.decoration.ItemFrameEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemFrameEntityRenderer.class)
public class ItemFrameEntityRendererMixin<T extends ItemFrameEntity> {

    @Inject(at=@At("HEAD"), method="Lnet/minecraft/client/render/entity/ItemFrameEntityRenderer;getLight(Lnet/minecraft/entity/decoration/ItemFrameEntity;II)I", cancellable = true)
    private void getLight(T itemFrame, int glowLight, int regularLight, CallbackInfoReturnable<Integer> callbackInfoReturnable) {
        if(itemFrame.getType() == PigmentEntityTypes.INVISIBLE_GLOW_ITEM_FRAME) {
            callbackInfoReturnable.setReturnValue(glowLight);
        }
    }

}