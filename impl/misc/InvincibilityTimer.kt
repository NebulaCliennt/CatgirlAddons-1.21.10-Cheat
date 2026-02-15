package catgirlroutes.module.impl.misc

import catgirlroutes.CatgirlRoutes.Companion.mc
import catgirlroutes.module.Module
import catgirlroutes.module.settings.RegisterHudElement
import catgirlroutes.module.settings.impl.BooleanSetting
import catgirlroutes.module.settings.impl.NumberSetting
import catgirlroutes.ui.hud.HudElement
import catgirlroutes.utils.dungeon.DungeonUtils.getMageCooldownMultiplier
import catgirlroutes.utils.dungeon.DungeonUtils.inBoss
import catgirlroutes.utils.dungeon.DungeonUtils.inDungeons
import catgirlroutes.utils.render.HUDRenderUtils.renderRect
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent
import java.awt.Color

object InvincibilityTimer : Module(
    name = "Invincibility Timer"
) {
    private val cataLevel = NumberSetting("Catacombs level", 0.0, 0.0, 50.0, 1.0)
    private val dungeonOnly = BooleanSetting("Dungeons only", false)
    private val bossOnly = BooleanSetting("Boss only", false)

    init {
        this.addSettings(this.cataLevel, this.dungeonOnly, this.bossOnly)
    }
    var spiritTicks = 0
    var bonzoTicks = 0
    var phoenixTicks = 0
    var phoenix = false

    @SubscribeEvent
    fun onChat(event: ClientChatReceivedEvent) {
        val msg = event.message.unformattedText

        when {
            "Second Wind Activated! Your Spirit Mask saved your life!" in msg -> spiritTicks = (600 * getMageCooldownMultiplier()).toInt()
            "Bonzo's Mask saved your life!" in msg -> bonzoTicks = ((7200 - cataLevel.value.toInt() * 72) * getMageCooldownMultiplier()).toInt()
            "Your Phoenix Pet saved you from certain death!" in msg -> phoenixTicks = 1200
            "You summoned your" in msg || "Autopet equipped your" in msg -> phoenix = msg.contains("Phoenix")
            "You despawned your Phoenix!" in msg -> phoenix = false
        }
    }

    @SubscribeEvent
    fun onTick(event: ClientTickEvent) {
        if (event.phase !== TickEvent.Phase.START) return
        spiritTicks -= 1
        phoenixTicks -= 1
        bonzoTicks -= 1
    }

    ////

    @RegisterHudElement
    object TimerHud : HudElement(
        this,
        0, 0,
        mc.fontRendererObj.getStringWidth("Phoenix: 30.2") + 6,
        (mc.fontRendererObj.FONT_HEIGHT + 2) * 3,
        preview = {
            mc.fontRendererObj.drawStringWithShadow("Bonzo: §a✔", 6.0F, 0.0f, Color.PINK.rgb)
            mc.fontRendererObj.drawStringWithShadow("Spirit: §a✔", 6.0F, 10.0f, Color.PINK.rgb)
            mc.fontRendererObj.drawStringWithShadow("Phoenix: §c30.2", 6.0F, 20.0f, Color.PINK.rgb)
            renderRect(0.0, 2.0, 3.0, 3.0, Color.PINK)
        }
    ) {
        override fun renderHud() {
            if ((dungeonOnly.enabled && !inDungeons) || (bossOnly.enabled && !inBoss)) return

            val name = mc.thePlayer?.inventory?.armorInventory?.get(3)?.displayName
            val offset = when {
                name?.contains("Bonzo") == true -> 0.0
                name?.contains("Spirit") == true -> 10.0
                else -> 10000.0  // some shit to hide some shit
            }

            val bonzoReady = stupid("Bonzo", bonzoTicks, phoenix && offset == 0.0 && phoenixTicks < 0.0)
            val spiritReady = stupid("Spirit", spiritTicks, phoenix && offset == 10.0 && phoenixTicks < 0.0)
            val phoenixReady = stupid("Phoenix", phoenixTicks, phoenix && ((offset == 10.0 && spiritTicks < 0.0) || (offset == 0.0 && bonzoTicks < 0.0)))

            mc.fontRendererObj.drawStringWithShadow(bonzoReady, 6.0F, 0.0f, Color.PINK.rgb)
            mc.fontRendererObj.drawStringWithShadow(spiritReady, 6.0F, 10.0f, Color.PINK.rgb)
            mc.fontRendererObj.drawStringWithShadow(phoenixReady, 6.0F, 20.0f, Color.PINK.rgb)

            renderRect(0.0, offset + 2.0, 3.0, 3.0, Color.PINK)
            if (phoenix) renderRect(0.0, 22.0, 3.0, 3.0, Color.PINK)
        }

        private fun stupid(name: String, ticks: Int, highlight: Boolean): String {  // stupid thing for colour highlight
            val c = if (ticks < 0 && highlight) "§c" else if (ticks <= 0) "§a" else "§c"
            val time = if (ticks <= 0) "✔" else "%.1f".format(ticks / 20.0)
            return "$name: $c$time"
        }
    }
}