package catgirlroutes.ui.clickgui.elements.menu

import catgirlroutes.module.settings.impl.DropdownSetting
import catgirlroutes.ui.clickgui.elements.Element
import catgirlroutes.ui.clickgui.elements.ElementType
import catgirlroutes.ui.clickgui.elements.ModuleButton
import catgirlroutes.ui.clickgui.util.ColorUtil
import catgirlroutes.ui.clickgui.util.FontUtil
import net.minecraft.client.gui.Gui

class ElementDropdown(parent: ModuleButton, setting: DropdownSetting)
    : Element<DropdownSetting>(parent, setting, ElementType.DROPDOWN) {

    override fun renderElement(mouseX: Int, mouseY: Int, partialTicks: Float): Int {
        FontUtil.drawString(displayName, 1, 3)

        if (setting.enabled) {
            FontUtil.drawString("⬇", width - 11, 2)
            Gui.drawRect(0, 13, width, 15, ColorUtil.tabColorBg)
            Gui.drawRect((width * 0.4).toInt(), 12, (width * 0.6).toInt(), 15, ColorUtil.tabColor)
        } else {
            FontUtil.drawString("⬅", width - 13, 2)
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
        return mouseX >= xAbsolute + width - 13 && mouseX <= xAbsolute + width - 1 && mouseY >= yAbsolute + 2 && mouseY <= yAbsolute + height - 2
    }
}