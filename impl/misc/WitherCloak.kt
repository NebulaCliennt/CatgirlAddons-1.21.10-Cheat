package catgirlroutes.module.impl.misc

import catgirlroutes.CatgirlRoutes.Companion.mc
import catgirlroutes.module.Category
import catgirlroutes.module.Module
import catgirlroutes.module.settings.RegisterHudElement
import catgirlroutes.module.settings.impl.BooleanSetting
import catgirlroutes.module.settings.impl.ColorSetting
import catgirlroutes.module.settings.impl.StringSetting
import catgirlroutes.ui.clickgui.util.FontUtil.drawStringWithShadow
import catgirlroutes.ui.hud.HudElement
import catgirlroutes.utils.Utils.equalsOneOf
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.monster.EntityCreeper
import net.minecraft.util.StringUtils
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.client.event.RenderLivingEvent
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.awt.Color

object WitherCloak: Module(
    "Wither cloak",
    Category.MISC
){

    private val enabledText = StringSetting("Title")
    private val textColor = ColorSetting("Title color", Color.PINK, false)
    private val timer = BooleanSetting("Timer")
    private val hideCloak = BooleanSetting("Hide cloak")

    init {
        addSettings(enabledText, textColor, timer, hideCloak)
    }

    var inCloak = false
    var lastCloak: Long = 0
    var cloakCd: Long = 0

    @RegisterHudElement
    object CloakHud : HudElement(
        this,
        width = mc.fontRendererObj.getStringWidth(enabledText.value),
        height = mc.fontRendererObj.FONT_HEIGHT + 2
    ) {
        override fun renderHud() {
            if (inCloak) {
                drawStringWithShadow(enabledText.value, 0.0, 0.0, textColor.value.rgb)
                return
            } else if (System.currentTimeMillis() < lastCloak + cloakCd && timer.value) {
                val timeLeft: Double = ((lastCloak + cloakCd) - System.currentTimeMillis()) / 1000.toDouble()
                val xPad = mc.fontRendererObj.getStringWidth(enabledText.value) / 2 - (mc.fontRendererObj.getStringWidth(String.format("%.1f", timeLeft)) / 2)
                drawStringWithShadow(String.format("%.1f", timeLeft), xPad.toDouble(), 0.0, textColor.value.rgb)
            }
        }

        override fun setDimensions() {
            this.width = mc.fontRendererObj.getStringWidth(enabledText.value)
        }
    }

    @SubscribeEvent
    fun onChat(event: ClientChatReceivedEvent) {
        if (event.type.toInt() == 2) return
        val message = StringUtils.stripControlCodes(event.message.unformattedText)
        if (message == "Creeper Veil De-activated!") {
            inCloak = false
            lastCloak = System.currentTimeMillis()
            cloakCd = 5000.toLong()
        } else if (message.equalsOneOf("Creeper Veil De-activated! (Expired)", "Not enough mana! Creeper Veil De-activated!")) {
            inCloak = false
            lastCloak = System.currentTimeMillis()
            cloakCd = 10000.toLong()
        } else if (message == "Creeper Veil Activated!") {
            inCloak = true
        }
    }

    @SubscribeEvent
    fun onWorldUnload(event: WorldEvent.Unload) {
        inCloak = false
        lastCloak = 0
        cloakCd = 0
    }

    @SubscribeEvent
    fun onPreRenderEntity(event: RenderLivingEvent.Pre<EntityLivingBase>) {
        if (mc.thePlayer == null || event.entity !is EntityCreeper || !hideCloak.value) return
        val creeper = event.entity as EntityCreeper
        if (creeper.health != 20f || !creeper.isInvisible || !creeper.powered || mc.thePlayer.getDistanceToEntity(creeper) > 6f) return
        mc.theWorld.removeEntity(creeper)
        event.isCanceled = true
    }

    /*
    Not enough mana! Creeper Veil De-activated!
    Creeper Veil De-activated! (Expired)
    Creeper Veil Activated!
    Creeper Veil De-activated!
     */
}