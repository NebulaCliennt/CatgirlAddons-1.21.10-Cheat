package catgirlroutes.mixins.entity;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayerSP.class)
public abstract class MovementUpdateEvent extends MixinAbstractClientPlayer { // why is it not in MixinEntityPlayerSP?

    @Inject(method = "onUpdateWalkingPlayer", at = @At("HEAD"))
    private void onUpdateWalkingPlayerPre(CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.post(new catgirlroutes.events.impl.MovementUpdateEvent.Pre());
    }

    @Inject(method = "onUpdateWalkingPlayer", at = @At("RETURN"))
    private void onUpdateWalkingPlayerPost(CallbackInfo ci) {
        //ServerRotateUtils.INSTANCE.handlePost();
    }
}