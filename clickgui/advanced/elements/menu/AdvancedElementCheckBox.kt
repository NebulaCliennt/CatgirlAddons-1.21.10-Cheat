package catgirlroutes.ui.clickgui.advanced.elements.menu

import catgirlroutes.module.Module
import catgirlroutes.module.settings.impl.BooleanSetting
import catgirlroutes.ui.clickgui.advanced.AdvancedMenu
import catgirlroutes.ui.clickgui.advanced.elements.AdvancedElement
import catgirlroutes.ui.clickgui.advanced.elements.AdvancedElementType
import catgirlroutes.ui.clickgui.util.ColorUtil
import catgirlroutes.ui.clickgui.util.ColorUtil.textcolor
import catgirlroutes.ui.clickgui.util.FontUtil
import net.minecraft.client.gui.Gui
import java.awt.Color

/**
 * Provides a checkbox element for the advanced gui.
 *
 * @author Aton
 */
class AdvancedElementCheckBox(
    parent: AdvancedMenu, module: Module, setting: BooleanSetting,
) : AdvancedElement<BooleanSetting>(parent, module, setting, AdvancedElementType.CHECK_BOX) {


    /**
     * Render the element
     */
    override fun renderElement(mouseX: Int, mouseY: Int, partialTicks: Float) : Int{
        val temp = ColorUtil.clickGUIColor
        val color = Color(temp.red, temp.green, temp.blue, 200).rgb

        /** Rendering the name and the checkbox */
        FontUtil.drawString(setting.name, 1, 2, textcolor)
        Gui.drawRect(
            (settingWidth - 13), 2, settingWidth - 1, 13,
            if (setting.enabled) color else -0x1000000
        )
        if (isCheckHovered(mouseX, mouseY)) Gui.drawRect(
            settingWidth - 13,  2, settingWidth -1,
            13, 0x55111111
        )
        return this.settingHeight
    }

    /**
     * Handles mouse clicks for this element and returns true if an action was performed
     */
    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int): Boolean {
        if (mouseButton == 0 && isCheckHovered(mouseX, mouseY)) {
            setting.toggle()
            return true
        }
        return super.mouseClicked(mouseX, mouseY, mouseButton)
    }

    /**
     * Checks whether this element is hovered
     */
    private fun isCheckHovered(mouseX: Int, mouseY: Int): Boolean {
        return mouseX >= parent.x + x + settingWidth - 13 && mouseX <= parent.x + x + settingWidth - 1 && mouseY >= parent.y + y + 2 && mouseY <= parent.y + y + settingHeight - 2
    }
}