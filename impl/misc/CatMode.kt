package catgirlroutes.module.impl.misc

import catgirlroutes.CatgirlRoutes.Companion.mc
import catgirlroutes.module.Category
import catgirlroutes.module.Module
import catgirlroutes.module.settings.Setting.Companion.withDependency
import catgirlroutes.module.settings.impl.BooleanSetting
import catgirlroutes.module.settings.impl.NumberSetting
import catgirlroutes.module.settings.impl.StringSelectorSetting
import catgirlroutes.utils.render.FallingKittens
import net.minecraft.client.renderer.GlStateManager
import net.minecraftforge.client.event.GuiScreenEvent
import net.minecraftforge.client.event.sound.PlaySoundSourceEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

object CatMode: Module( // todo: add cat model mode from skytils april fools!!!?!
    "Cat mode",
    category = Category.MISC,
    description = "MEOWMEOWMEOWMEOWMEOWMEOWMEOW"
) {

    private val sound: BooleanSetting = BooleanSetting("Meowound", false, "Meow sound everywhere")
    private val text: BooleanSetting = BooleanSetting("Meow meow?", false, "Meow everywhere")
    private val fallingCats: BooleanSetting = BooleanSetting("Catocalypsis", false, "THEY'RE EVERYWHERE")
    private val darken: BooleanSetting = BooleanSetting("Darken", false, "Makes the kittens darker so they don't distract you").withDependency { fallingCats.enabled }
    private val catTexture: StringSelectorSetting = StringSelectorSetting("Type", "trans", arrayListOf("trans", "flushed", "bread", "cut", "toast")).withDependency { fallingCats.enabled }
    private val catSize: NumberSetting = NumberSetting("Size", 15.0, 10.0, 50.0, 1.0, unit = "px").withDependency { fallingCats.enabled }
    private val catSpeed: NumberSetting = NumberSetting("Speed", 1.0, 0.5, 3.0, 0.1).withDependency { fallingCats.enabled }

    init {
        addSettings(this.sound, this.text, this.fallingCats, this.darken, this.catTexture, this.catSize, this.catSpeed)
    }

    private var fallingKittens = FallingKittens()

    @SubscribeEvent
    fun onBackgroundDrawn(event: GuiScreenEvent.BackgroundDrawnEvent) {
        if(!this.fallingCats.enabled || mc.theWorld == null) return
        if (darken.enabled) GlStateManager.color(0.5f, 0.5f, 0.5f, 0.7f)
        fallingKittens.drawKittens("${this.catTexture.selected}.png", this.catSize.value.toInt(), this.catSpeed.value.toFloat())
        if (darken.enabled) GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
    }

    @SubscribeEvent
    fun onSound(event: PlaySoundSourceEvent) {
        if (event.name == "mob.cat.meow" || !this.sound.enabled) return
        if (event.isCancelable) {
            event.isCanceled = true
        }
        if (mc.theWorld == null) return
        mc.theWorld.playSound(
            event.sound.xPosF.toDouble(),
            event.sound.yPosF.toDouble(),
            event.sound.zPosF.toDouble(),
            "mob.cat.meow",
            event.sound.volume,
            event.sound.pitch,
            false
        )
    }

    @JvmStatic
    fun replaceText(text: String?): String? {
        if (text == null || !this.enabled || !this.text.enabled) return text

        val replacement = "meow"
        return text.split("\\s+".toRegex())
            .joinToString(" ") { replacement }
    }

}