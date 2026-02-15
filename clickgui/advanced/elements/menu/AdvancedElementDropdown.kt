package catgirlroutes.ui.clickgui.advanced.elements.menu

import catgirlroutes.module.Module
import catgirlroutes.module.settings.impl.DropdownSetting
import catgirlroutes.ui.clickgui.advanced.AdvancedMenu
import catgirlroutes.ui.clickgui.advanced.elements.AdvancedElement
import catgirlroutes.ui.clickgui.advanced.elements.AdvancedElementType
import catgirlroutes.ui.clickgui.util.ColorUtil
import catgirlroutes.ui.clickgui.util.FontUtil
import net.minecraft.client.gui.Gui
import java.awt.Color

// withDependency doesn't work with advanced menu, so no dropdowns there. maybe I'll fix that some day
class AdvancedElementDropdown(
    parent: AdvancedMenu, module: Module, setting: DropdownSetting
) : AdvancedElement<DropdownSetting>(parent, module, setting, AdvancedElementType.DROPDOWN) {

    override fun renderElement(mouseX: Int, mouseY: Int, partialTicks: Float): Int {
        val temp = ColorUtil.clickGUIColor
        val color = Color(temp.red, temp.green, temp.blue, 150).rgb

        FontUtil.drawString(setting.name, 1, 3)

        if (setting.enabled) {
            FontUtil.drawString("⬇", settingWidth - 11, 2)
            Gui.drawRect(0, 13, settingWidth, 15, 0x77000000)
            Gui.drawRect(
                (settingWidth * 0.4).toInt(),
                12,
                (settingWidth * 0.6).toInt(),
                15,
                color
            )
        } else {
            FontUtil.drawString("⬅", settingWidth - 13, 2)
        }

        return super.renderElement(mouseX, mouseY, partialTicks)
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int): Boolean {
        if (mouseButton == 0 && isCheckHovered(mouseX, mouseY)) {
            setting.enabled = !setting.enabled
            parent.updateElements()
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