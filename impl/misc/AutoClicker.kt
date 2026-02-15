package catgirlroutes.module.impl.misc

import catgirlroutes.CatgirlRoutes.Companion.mc
import catgirlroutes.module.Category
import catgirlroutes.module.Module
import catgirlroutes.module.settings.Setting.Companion.withDependency
import catgirlroutes.module.settings.Setting.Companion.withInputTransform
import catgirlroutes.module.settings.impl.BooleanSetting
import catgirlroutes.module.settings.impl.NumberSetting
import catgirlroutes.utils.PlayerUtils
import kotlinx.coroutines.*
import net.minecraft.block.Block
import net.minecraft.block.BlockLiquid
import net.minecraft.client.settings.KeyBinding
import net.minecraft.init.Blocks
import net.minecraft.util.MathHelper
import net.minecraftforge.client.event.MouseEvent
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import kotlin.random.Random


object AutoClicker: Module(
    "Auto Clicker",
    category = Category.MISC,
    description = "A simple auto clicker for both left and right click. Activates when the corresponding key is being held down. "
) {
    private val leftClick = BooleanSetting("Left Click", true, description = "Toggles the auto clicker for left click.")
    private val leftMaxCps: NumberSetting = NumberSetting("Left max CPS", 12.0, 1.0, 20.0, 1.0, description = "Maximum cps for left click.")
        .withInputTransform { input, setting ->
            MathHelper.clamp_double(input, leftMinCps.value, setting.max)
        }.withDependency { leftClick.enabled }

    private val leftMinCps: NumberSetting = NumberSetting("Left min CPS", 10.0, 1.0, 20.0, 1.0, description = "Minimum CPS for left click.")
        .withInputTransform { input, setting ->
            MathHelper.clamp_double(input, setting.min, leftMaxCps.value)
        }.withDependency { leftClick.enabled }

    private val rightClick = BooleanSetting("Right Click", false, description = "Toggles the auto clicker for right click.")
    private val rightMaxCps: NumberSetting = NumberSetting("Right max CPS", 12.0, 1.0, 20.0, 1.0, description = "Maximum cps for left click.")
        .withInputTransform { input, setting ->
            MathHelper.clamp_double(input, rightMinCps.value, setting.max)
        }.withDependency { rightClick.enabled }

    private val rightMinCps: NumberSetting = NumberSetting("Right min CPS", 10.0, 1.0, 20.0, 1.0, description = "Minimum CPS for left click.")
        .withInputTransform { input, setting ->
            MathHelper.clamp_double(input, setting.min, rightMaxCps.value)
        }.withDependency { rightClick.enabled }

    init {
        this.addSettings(leftClick, leftMaxCps, leftMinCps, rightClick, rightMaxCps, rightMinCps)
    }

    private var leftClicking = false
    private var rightClicking = false
    private var leftClickJob: Job? = null
    private var rightClickJob: Job? = null

    @OptIn(DelicateCoroutinesApi::class)
    @SubscribeEvent
    fun onMouse(event: MouseEvent) {
        if (!this.enabled) return
        if (event.button != 0 && event.button != 1) return

        if (event.buttonstate) {
            if (event.button == 0 && leftClick.value && !breakBlock()) {
                event.isCanceled = true
                leftClicking = true
                leftClickJob?.cancel()
                leftClickJob = GlobalScope.launch { leftClicker() }
            } else if (event.button == 1 && rightClick.value) {
                event.isCanceled = true
                rightClicking = true
                rightClickJob?.cancel()
                rightClickJob = GlobalScope.launch { rightClicker() }
            }
        } else {
            if (event.button == 0) {
                leftClicking = false
                leftClickJob?.cancel()
                leftClickJob = null
            } else {
                rightClicking = false
                rightClickJob?.cancel()
                rightClickJob = null
            }
        }
    }

    private suspend fun leftClicker() {
        while (leftClicking) {
            if (!breakBlock()) PlayerUtils.leftClick()
            delay(Random.nextDouble(1000.0 / leftMaxCps.value, 1000.0 / leftMinCps.value).toLong())
        }
    }

    private suspend fun rightClicker() {
        while (rightClicking) {
            PlayerUtils.rightClick()
            delay(Random.nextDouble(1000.0 / rightMaxCps.value, 1000.0 / rightMinCps.value).toLong())
        }
    }

    @SubscribeEvent
    fun onWorldLoad(event: WorldEvent.Unload) {
        leftClicking = false
        leftClickJob?.cancel()
        leftClickJob = null

        rightClicking = false
        rightClickJob?.cancel()
        rightClickJob = null
    }

    private var breakHeld: Boolean = false

    private fun breakBlock(): Boolean {
        if (mc.objectMouseOver != null) {
            if (mc.objectMouseOver.blockPos != null) {
                val block: Block = mc.theWorld.getBlockState(mc.objectMouseOver.blockPos).block
                if (block !== Blocks.air && block !is BlockLiquid) {
                    if (!breakHeld) {
                        KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.keyCode, true)
                        KeyBinding.onTick(mc.gameSettings.keyBindAttack.keyCode)
                        breakHeld = true
                    }
                    return true
                }
                if (breakHeld) {
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.keyCode, false)
                    breakHeld = false
                }
            }
        }
        return false
    }
}