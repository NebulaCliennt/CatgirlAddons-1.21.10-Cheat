package catgirlroutes.module.impl.player

import catgirlroutes.CatgirlRoutes.Companion.mc
import catgirlroutes.module.Category
import catgirlroutes.module.Module
import net.minecraft.client.settings.KeyBinding.setKeyBindState
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent

/**
 * Module that makes you always sprint.
 * @author Aton
 */
object AutoSprint : Module(
    "Auto Sprint",
    category = Category.PLAYER,
    description = "A simple auto sprint module."
){
    @SubscribeEvent
    fun onTick(event: TickEvent.ClientTickEvent) {
        if (event.phase != TickEvent.Phase.START) return
        setKeyBindState(mc.gameSettings.keyBindSprint.keyCode, true)
    }
}