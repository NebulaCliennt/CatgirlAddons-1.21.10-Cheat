package catgirlroutes.module.impl.misc

import catgirlroutes.CatgirlRoutes.Companion.mc
import catgirlroutes.events.impl.MotionUpdateEvent
import catgirlroutes.module.Category
import catgirlroutes.module.Module
import catgirlroutes.utils.ChatUtils.modMessage
import catgirlroutes.utils.MovementUtils.restartMovement
import catgirlroutes.utils.MovementUtils.stopMovement
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import kotlin.math.cos
import kotlin.math.sin

object InstantSprint: Module(
    name = "Instant Sprint",
    category = Category.MISC,
    tag = TagType.WHIP
) {
    private val forwarKeybing = (mc.gameSettings.keyBindForward)
    var active = false


    @SubscribeEvent
    fun onRender(event: RenderWorldLastEvent) {
        if(!mc.thePlayer.onGround) return
        if (!enabled) return
        if (forwarKeybing.isPressed) {
            if (active) return
            active = true
            mc.thePlayer.setVelocity(0.0, mc.thePlayer.motionY, 0.0)
            stopMovement()
        }
    }

    @SubscribeEvent
    fun onMotion(event: MotionUpdateEvent.Pre) {
        if(active) {
            if(!mc.thePlayer.onGround) return
            val multiplier = if (mc.thePlayer?.isSneaking == true) {
                0.3
            } else {
                1.0
            }
            val yaw = mc.thePlayer.rotationYaw
            val speed = mc.thePlayer.capabilities.walkSpeed * 2.806 * multiplier
            val radians = yaw * Math.PI / 180 // todo: MathUtils?
            val x = -sin(radians) * speed
            val z = cos(radians) * speed
            mc.thePlayer.motionX = x
            mc.thePlayer.motionZ = z
            restartMovement()
        }
        active = false
    }
}