package catgirlroutes.module.impl.misc

import catgirlroutes.CatgirlRoutes.Companion.mc
import catgirlroutes.module.Category
import catgirlroutes.module.Module
import catgirlroutes.module.settings.impl.NumberSetting
import catgirlroutes.utils.Island
import catgirlroutes.utils.LocationManager
import net.minecraft.init.Blocks
import net.minecraft.util.BlockPos
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import kotlin.math.floor

object F7sim : Module(
    "F7 sim",
    category = Category.MISC,
    description = "Simulates 500 speed and lava bounce on single player."
){
    private val playerSpeed = NumberSetting("Player speed", 500.0, 100.0, 500.0, 1.0)

    init {
        this.addSettings(playerSpeed)
    }

    @SubscribeEvent
    fun onTick(event: TickEvent.ClientTickEvent) {
        if (!LocationManager.currentArea.isArea(Island.SinglePlayer)) return
        mc.thePlayer.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.movementSpeed).baseValue = playerSpeed.value / 1000
        mc.thePlayer.capabilities?.setPlayerWalkSpeed((playerSpeed.value / 1000).toFloat())
        if ((mc.thePlayer.isInLava || mc.theWorld.getBlockState(BlockPos(floor(mc.thePlayer.posX), floor(mc.thePlayer.posY), floor(mc.thePlayer.posZ))).block == Blocks.rail) && mc.thePlayer.posY - floor(mc.thePlayer.posY) < 0.1) {
            mc.thePlayer.setVelocity(mc.thePlayer.motionX, 3.5, mc.thePlayer.motionZ)
        }
    }
}