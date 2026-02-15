package catgirlroutes.mixins;

import catgirlroutes.module.impl.player.Animations;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {ItemRenderer.class}, priority = 1010)
public class MixinItemRenderer {

    @Shadow
    @Final
    private RenderItem itemRenderer;

    @Shadow
    private ItemStack itemToRender;

    @Inject(method = "transformFirstPersonItem(FF)V", at = @At("HEAD"), cancellable = true)
    public void itemTransform(float equipProgress, float swingProgress, CallbackInfo ci) {
        if (Animations.INSTANCE.itemTransforHook(equipProgress, swingProgress)) ci.cancel();
    }

    @Inject(method = "doItemUsedTransformations", at = @At("HEAD"), cancellable = true)
    public void useTransform(float swingProgress, CallbackInfo ci) {
        if (Animations.INSTANCE.scaledSwing(swingProgress)) ci.cancel();
    }

    @Inject(method = "performDrinking", at = @At("HEAD"), cancellable = true)
    public void drinkTransform(AbstractClientPlayer clientPlayer, float partialTicks, CallbackInfo ci) {
        if (Animations.INSTANCE.rotationlessDrink(clientPlayer, partialTicks)) ci.cancel();
        if (Animations.INSTANCE.scaledDrinking(clientPlayer, partialTicks, itemToRender)) ci.cancel();
    }
}
