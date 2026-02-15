package catgirlroutes.mixins.entity;

import catgirlroutes.module.impl.render.CgaUser;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(value = AbstractClientPlayer.class, priority = Integer.MAX_VALUE)
public class MixinAbstractClientPlayer {

    @Shadow
    private NetworkPlayerInfo playerInfo;

    @Inject(method = "getLocationCape", at = @At("RETURN"), cancellable = true)
    public void getLocationCape(CallbackInfoReturnable<ResourceLocation> cir) {
        CgaUser.INSTANCE.capeHook(cir, playerInfo);
    }

}