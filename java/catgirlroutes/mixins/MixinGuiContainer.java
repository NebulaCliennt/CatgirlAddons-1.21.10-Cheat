package catgirlroutes.mixins;

import catgirlroutes.events.impl.GuiContainerEvent;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GuiContainer.class, priority = 10)
public abstract class MixinGuiContainer {

    @Unique
    private final GuiContainer catgirlAddons$gui = (GuiContainer) (Object) this;

    @Shadow
    public Container inventorySlots;

    @Inject(method = "drawSlot", at = @At("HEAD"), cancellable = true)
    private void drawSlot(Slot slotIn, CallbackInfo ci) {
        if (MinecraftForge.EVENT_BUS.post(new GuiContainerEvent.DrawSlotEvent(inventorySlots, catgirlAddons$gui, slotIn))) ci.cancel();
    }

    @Inject(method = "handleMouseClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/PlayerControllerMP;windowClick(IIIILnet/minecraft/entity/player/EntityPlayer;)Lnet/minecraft/item/ItemStack;"), cancellable = true)
    private void onMouseClick(Slot slot, int slotId, int clickedButton, int clickType, CallbackInfo ci) {
        if (slot == null) return;
        if (MinecraftForge.EVENT_BUS.post(new GuiContainerEvent.SlotClickEvent(inventorySlots, catgirlAddons$gui, slot, slotId, clickedButton, clickType))) ci.cancel();
    }
}
