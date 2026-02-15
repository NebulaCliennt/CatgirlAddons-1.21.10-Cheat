package catgirlroutes.module.impl.dungeons

import catgirlroutes.CatgirlRoutes.Companion.mc
import catgirlroutes.module.Category
import catgirlroutes.module.Module
import catgirlroutes.module.settings.impl.NumberSetting
import catgirlroutes.utils.ClientListener.scheduleTask
import catgirlroutes.utils.MovementUtils.restartMovement
import catgirlroutes.utils.MovementUtils.stopMovement
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import kotlin.math.abs

object CoreClip: Module(
    "Core clip",
    category = Category.DUNGEON
){
    private var clipDelay = NumberSetting("Delay", 0.0, 0.0, 10.0, 1.0)

    init {
        addSettings(clipDelay)
    }

    private var passedTicks = 0

    @SubscribeEvent
    fun onTick(event: TickEvent.ClientTickEvent) {
        if (event.phase != TickEvent.Phase.START) return
        if (mc.thePlayer == null) return
        if (mc.thePlayer.posY != 115.0) return
        if (mc.thePlayer.posX !in 52.0..57.0) return
        if (!mc.thePlayer.isCollidedHorizontally) {
            passedTicks = 0
            return
        }
        if (isWithinTolerence(mc.thePlayer.posZ, 53.7)) {
            if (passedTicks < clipDelay.value) {
                passedTicks++
                return
            }
            passedTicks = 0
            mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY, 53.7624)
            scheduleTask(0) {mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY, 55.301)}
            stopMovement()
            scheduleTask(0) {restartMovement()}
        } else if (isWithinTolerence(mc.thePlayer.posZ, 55.3)) {
            if (passedTicks < clipDelay.value) {
                passedTicks++
                return
            }
            passedTicks = 0
            mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY, 55.2376)
            scheduleTask(0) {mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY, 53.699)}
            stopMovement()
            scheduleTask(0) {restartMovement()}
        }
    }

    private fun isWithinTolerence(n1: Double, n2: Double): Boolean {
        return abs(n1 - n2) < 1e-4
    }
}