package catgirlroutes.module.impl.misc

import catgirlroutes.CatgirlRoutes.Companion.mc
import catgirlroutes.events.impl.BlockChangeEvent
import catgirlroutes.events.impl.PacketReceiveEvent
import catgirlroutes.module.Category
import catgirlroutes.module.Module
import catgirlroutes.utils.ChatUtils.debugMessage
import catgirlroutes.utils.LocationManager
import catgirlroutes.utils.rotation.RotationUtils.getYawAndPitch
import net.minecraft.block.Block
import net.minecraft.init.Blocks
import net.minecraft.network.play.server.S08PacketPlayerPosLook
import net.minecraft.util.BlockPos
import net.minecraft.util.Vec3
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin

object DojoHelper: Module(
    name = "Dojo Helper (DO NOT USE)",
    category = Category.MISC,
    tag = Module.TagType.HARAM
) {
    var swiftnessBlocks = arrayListOf<Vec3>()
    var airTicks = 0
    var lastX = 0.0
    var lastZ = 0.0

    @SubscribeEvent
    fun swiftness(event: ClientTickEvent) {
        if (event.phase != TickEvent.Phase.START) return
        if (swiftnessBlocks.isEmpty()) return
        val goal = swiftnessBlocks[0]

        if (mc.thePlayer.posY < 100 || mc.thePlayer.posY > 102) return

        if (mc.thePlayer?.onGround == true) {
            airTicks = 0
        } else {
            airTicks += 1
        }

        val (yaw, pitch) = getYawAndPitch(goal.xCoord, goal.yCoord, goal.zCoord)

        val speed = if(!mc.thePlayer.isSneaking) {
            mc.thePlayer.capabilities.walkSpeed
        } else {
            mc.thePlayer.capabilities.walkSpeed * 3 / 10 /// doesnt let me do 0.3???
        }

        val radians = yaw * Math.PI / 180 // todo: MathUtils?
        val x = -sin(radians) * speed * 2.806
        val z = cos(radians) * speed * 2.806

        if (
            abs(mc.thePlayer.posX - goal.xCoord) <= 0.25 &&
            abs(mc.thePlayer.posZ - goal.zCoord) <= 0.25
        ) {
            debugMessage("1")
            mc.thePlayer.setVelocity(0.0, mc.thePlayer.motionY, 0.0)
            lastX = 0.0
            lastZ = 0.0
            if (swiftnessBlocks.isEmpty()) return
            swiftnessBlocks.removeFirst()
            return
        }

        if (airTicks < 2) {
            mc.thePlayer.motionX = x
            mc.thePlayer.motionZ = z
            lastX = x
            lastZ = z
        } else {
            //assume max acceleration
            lastX = lastX * 0.91 + 0.02 * -sin(radians)
            lastZ = lastZ * 0.91 + 0.02 * cos(radians)
            mc.thePlayer.motionX = lastX * 0.91 + 0.02 * -sin(radians)
            mc.thePlayer.motionZ = lastZ * 0.91 + 0.02 * cos(radians)
        }

    }

    @SubscribeEvent
    fun onS08(event: PacketReceiveEvent) {
        if (event.packet !is S08PacketPlayerPosLook) return
        swiftnessBlocks.clear()
    }

    @SubscribeEvent
    fun shouldJump(event: ClientTickEvent) {
        if (mc.thePlayer?.posY != 100.0) return
        if (!LocationManager.inSkyblock) return
        val playerPos = BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ)
        val blockID = Block.getIdFromBlock(mc.theWorld.getBlockState(playerPos).block)
        if (blockID == 0) {
            mc.thePlayer.motionY = 0.42
        }
    }

    @SubscribeEvent
    fun onBlockChange(event: BlockChangeEvent) {
        if (event.pos.x in -232..-184 && event.pos.y in 99..101 && event.pos.z in -619..-571) {
            if (event.old.block == Blocks.air && event.update.block == Blocks.wool) {
                swiftnessBlocks.add(Vec3(floor(event.pos.x.toDouble()) + 0.5, event.pos.y.toDouble(), floor(event.pos.z.toDouble()) + 0.5))
            }
        }
    }
}
