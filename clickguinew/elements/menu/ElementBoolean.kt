package catgirlroutes.ui.clickguinew.elements.menu

import catgirlroutes.module.settings.impl.BooleanSetting
import catgirlroutes.ui.clickgui.util.ColorUtil
import catgirlroutes.ui.clickguinew.elements.Element
import catgirlroutes.ui.clickguinew.elements.ElementType
import catgirlroutes.ui.clickguinew.elements.ModuleButton
import catgirlroutes.ui.misc.elements.impl.MiscElementBoolean
import java.awt.Color

class ElementBoolean(parent: ModuleButton, setting: BooleanSetting) :
    Element<BooleanSetting>(parent, setting, ElementType.BOOLEAN) {

    private val booleanElement = MiscElementBoolean(
        text = displayName,
        enabled = this.setting.enabled,
        width = 10.0,
        height = 10.0,
        thickness = 1.0,
        radius = 3.0,
        gap = 0.0,
        colour = Color(ColorUtil.buttonColor)
    )

    override fun renderElement(mouseX: Int, mouseY: Int, partialTicks: Float): Double {
        this.booleanElement.render(mouseX - xAbsolute.toInt(), mouseY - yAbsolute.toInt())
        return super.renderElement(mouseX, mouseY, partialTicks)
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int): Boolean {
        if (this.booleanElement.mouseClicked(mouseX - xAbsolute.toInt(), mouseY - yAbsolute.toInt(), mouseButton)) {
            this.setting.toggle()
            return true
        }
        return super.mouseClicked(mouseX, mouseY, mouseButton)
    }

}