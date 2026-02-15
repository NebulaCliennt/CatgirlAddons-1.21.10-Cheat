package catgirlroutes.ui.clickgui.advanced.elements.menu

import catgirlroutes.module.Module
import catgirlroutes.module.settings.impl.DummySetting
import catgirlroutes.module.settings.impl.KeyBindSetting
import catgirlroutes.ui.clickgui.advanced.AdvancedMenu
import catgirlroutes.ui.clickgui.advanced.elements.AdvancedElement
import catgirlroutes.ui.clickgui.advanced.elements.AdvancedElementType
import catgirlroutes.ui.clickgui.util.FontUtil
import org.lwjgl.input.Keyboard
import org.lwjgl.input.Mouse

/**
 * Provides a key bind element.
 *
 * @author Aton
 */
class AdvancedElementKeyBind(parent: AdvancedMenu, module: Module, setting: KeyBindSetting) :
    AdvancedElement<KeyBindSetting>(parent, module, setting, AdvancedElementType.KEY_BIND) {

    private val keyBlackList = intArrayOf()

    /**
     * Render the element
     */
    override fun renderElement(mouseX: Int, mouseY: Int, partialTicks: Float): Int {
        val displayName = "Key Bind"

        val keyName = if (setting.value.key > 0)
            Keyboard.getKeyName(setting.value.key) ?: "Err"
        else if (setting.value.key < 0)
            Mouse.getButtonName(setting.value.key + 100)
        else
            ".."
        val displayValue = "[$keyName]"

        // Rendering the text and the keybind.
        FontUtil.drawString(displayName, 1, 2, -0x1)
        FontUtil.drawString(displayValue, this.settingWidth - FontUtil.getStringWidth(displayValue), 2, -0x1)
        return this.settingHeight
    }

    /**
     * Handles mouse clicks for this element and returns true if an action was performed.
     * Used to interact with the element and to register mouse binds.
     */
    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int): Boolean {
        if (mouseButton == 0 && isCheckHovered(mouseX, mouseY) && !listening) {
            listening = true
            return true
        } else if (listening) {
            setting.value.key = -100 + mouseButton
            listening = false
        }
        return super.mouseClicked(mouseX, mouseY, mouseButton)
    }

    /**
     * Register key strokes. Used to set the key bind.
     */
    override fun keyTyped(typedChar: Char, keyCode: Int): Boolean {
        if (listening) {
            if (keyCode == Keyboard.KEY_ESCAPE) {
                setting.value.key = Keyboard.KEY_NONE
                listening = false
            } else if (keyCode == Keyboard.KEY_NUMPADENTER || keyCode == Keyboard.KEY_RETURN) {
                listening = false
            } else if (!keyBlackList.contains(keyCode)) {
                setting.value.key = keyCode
                listening = false
            }
            return true
        }
        return super.keyTyped(typedChar, keyCode)
    }

    /**
     * Checks whether this element is hovered
     */
    private fun isCheckHovered(mouseX: Int, mouseY: Int): Boolean {
        return mouseX >= parent.x + x && mouseX <= parent.x + x + settingWidth && mouseY >= parent.y + y  && mouseY <= parent.y + y + settingHeight
    }
}