package catgirlroutes.ui.clickguinew.elements.menu

import catgirlroutes.module.settings.impl.StringSetting
import catgirlroutes.ui.clickgui.util.ColorUtil
import catgirlroutes.ui.clickgui.util.FontUtil
import catgirlroutes.ui.clickgui.util.FontUtil.fontHeight
import catgirlroutes.ui.clickguinew.elements.Element
import catgirlroutes.ui.clickguinew.elements.ElementType
import catgirlroutes.ui.clickguinew.elements.ModuleButton
import catgirlroutes.ui.misc.elements.impl.MiscElementText
import java.awt.Color

class ElementTextField(parent: ModuleButton, setting: StringSetting) :
    Element<StringSetting>(parent, setting, ElementType.TEXT_FIELD) {

    private val textField = MiscElementText(
        0.0,
        fontHeight + 3.0,
        width,
        13.0,
        this.setting.text,
        this.setting.length,
        this.setting.placeholder,
        thickness = 1.0,
        radius = 3.0
    )

    override fun renderElement(mouseX: Int, mouseY: Int, partialTicks: Float): Double {
        FontUtil.drawString(displayName, 0.0, 0.0)
        this.setting.text = this.textField.text
        this.textField.apply {
            text = setting.text
            outlineColour = Color(ColorUtil.outlineColor) // todo do something about this cuz it doesn't seem like meta
            outlineFocusColour = ColorUtil.clickGUIColor
            render(mouseX - xAbsolute.toInt(), mouseY - yAbsolute.toInt())
        }
        return super.renderElement(mouseX, mouseY, partialTicks)
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int): Boolean {
        return this.textField.mouseClicked(mouseX - xAbsolute.toInt(), mouseY - yAbsolute.toInt(), mouseButton)
    }

    override fun mouseClickMove(mouseX: Int, mouseY: Int, clickedMouseButton: Int, timeSinceLastClick: Long) {
        this.textField.mouseClickMove(mouseX - xAbsolute.toInt(), mouseY - yAbsolute.toInt(), clickedMouseButton, timeSinceLastClick)
    }

    override fun keyTyped(typedChar: Char, keyCode: Int): Boolean {
        return this.textField.keyTyped(typedChar, keyCode)
    }

}