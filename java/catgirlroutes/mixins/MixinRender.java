package catgirlroutes.mixins;

import catgirlroutes.events.impl.RenderEntityEvent;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ Render.class })
public abstract class MixinRender {
    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
    public void shouldRender(final Entity entityIn, final ICamera camera, final double camX, final double camY, final double camZ, final CallbackInfoReturnable<Boolean> cir) {
        if (MinecraftForge.EVENT_BUS.post(new RenderEntityEvent(entityIn, camera, camX, camY, camZ))) {
            cir.setReturnValue(false);
        }
    }
}
