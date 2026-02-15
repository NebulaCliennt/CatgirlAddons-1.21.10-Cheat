package catgirlroutes.module.impl.dungeons


import catgirlroutes.CatgirlRoutes.Companion.mc
import catgirlroutes.module.Category
import catgirlroutes.module.Module
import catgirlroutes.module.settings.Setting.Companion.withDependency
import catgirlroutes.module.settings.impl.BooleanSetting
import catgirlroutes.module.settings.impl.KeyBindSetting
import catgirlroutes.module.settings.impl.NumberSetting
import catgirlroutes.utils.ClientListener.scheduleTask
import catgirlroutes.utils.PlayerUtils.leftClick
import catgirlroutes.utils.PlayerUtils.swapFromName
import catgirlroutes.utils.SwapState
import catgirlroutes.utils.clock.Executor
import catgirlroutes.utils.clock.Executor.Companion.register
import catgirlroutes.utils.dungeon.DungeonUtils.isSecret
import net.minecraft.util.BlockPos
import org.lwjgl.input.Keyboard

object GhostBlocks : Module(  // todo: add delay, range, option to gkey skulls maybeidk
    "Ghost Blocks",
    category = Category.DUNGEON,
    description = "Creates ghost blocks where you are looking when the key bind is pressed."
) {
    private val gKey: BooleanSetting = BooleanSetting("GKey", false)
    private val swingHand = BooleanSetting("Swing Hand", true, "Swings the player's hand.").withDependency { gKey.enabled }
    private val gKeyDelay = NumberSetting("Delay", 50.0, 50.0, 1000.0, 50.0, "Delay for gkey").withDependency { gKey.enabled }
    private val gKeyKeyBind: KeyBindSetting = KeyBindSetting("GKey Key", Keyboard.KEY_NONE, "Creates ghost blocks where you are looking.").withDependency { gKey.enabled }

    private val swapStonk: BooleanSetting = BooleanSetting("Swap Stonk", false);

    private var running = false
    private var prevSlot = -1
    private val swapKeyBind: KeyBindSetting = KeyBindSetting("Swap Key", Keyboard.KEY_NONE, "Automatically makes a ghost block using a swap to pickaxe.").withDependency { swapStonk.enabled }
        .onPress {
            if (!this.swapStonk.enabled || !this.enabled) return@onPress
            if (this.running) return@onPress;

            this.running = true;
            this.prevSlot = mc.thePlayer.inventory.currentItem;

            val swapResult = swapFromName("pickaxe")
            if (swapResult == SwapState.UNKNOWN) {
                this.running = false
                return@onPress
            }

            scheduleTask(1) { leftClick() }
            scheduleTask(2) {
                mc.thePlayer.inventory.currentItem = this.prevSlot
                this.running = false
            }
        }

    init {
        gKeyLoop()

        this.addSettings(this.gKey, this.swingHand, this.gKeyDelay, this.gKeyKeyBind, this.swapStonk, this.swapKeyBind)
    }

    private fun gKeyLoop() {
        Executor(gKeyDelay.value.toLong()) {
            if (!gKeyKeyBind.value.isDown() || !gKey.enabled || !this.enabled || mc.currentScreen != null) return@Executor
            val blockPos = mc.objectMouseOver.blockPos

            if (isSecret(mc.theWorld.getBlockState(blockPos), blockPos)) return@Executor
            if (swingHand.value) mc.thePlayer.swingItem()
            toAir(blockPos)
        }.register()
    }

    private fun toAir(blockPos: BlockPos?): Boolean {
        if (blockPos != null) {
            mc.theWorld.setBlockToAir(mc.objectMouseOver.blockPos)
            return true
        }
        return false
    }
}
