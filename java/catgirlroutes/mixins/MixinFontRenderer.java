package catgirlroutes.mixins;

import catgirlroutes.module.impl.misc.CatMode;
import catgirlroutes.module.impl.misc.NickHider;
import net.minecraft.client.gui.FontRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = FontRenderer.class)
public class MixinFontRenderer {

    @ModifyVariable(method = "renderStringAtPos", at = @At("HEAD"), argsOnly = true)
    private String modifyRenderStringAtPos(String text) {
        String modifiedText = CatMode.replaceText(text);
        modifiedText = NickHider.replaceText(modifiedText);
        modifiedText = NickHider.replaceTextTeam(modifiedText);
        return modifiedText;
    }

    @ModifyVariable(method = "getStringWidth", at = @At(value = "HEAD"), argsOnly = true)
    private String modifyGetStringWidth(String text) {
        String modifiedText = CatMode.replaceText(text);
        modifiedText = NickHider.replaceText(modifiedText);
        modifiedText = NickHider.replaceTextTeam(modifiedText);
        return modifiedText;
    }
}
