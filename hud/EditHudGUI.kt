package catgirlroutes.ui.hud

import catgirlroutes.CatgirlRoutes
import catgirlroutes.ui.misc.elements.impl.MiscElementButton
import catgirlroutes.utils.ChatUtils.modMessage
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.ScaledResolution
import org.lwjgl.input.Mouse
import java.io.IOException

/**
 * The GUI for editing the positions and scale of HUD elements.
 *
 * @author Aton
 */
object EditHudGUI : GuiScreen() {

    private val hudElements: ArrayList<HudElement> = arrayListOf()
    private var draggingElement: HudElement? = null
    private var startOffsetX = 0
    private var startOffsetY = 0

    private lateinit var resetButton: MiscElementButton

    fun addHUDElements(newElements: List<HudElement>) {
        val nonDuplicate = newElements.filter { !hudElements.contains(it) }
        hudElements.addAll(0, nonDuplicate)
    }

    override fun initGui() {
        val sr = ScaledResolution(mc)
        this.resetButton = MiscElementButton(
            "Reset HUD",
            sr.scaledWidth_double / 2.0 - 60.0 / 2.0,
            sr.scaledHeight_double - 40.0,
            60.0, 20.0
        ) { modMessage("Resetting elements") }

        hudElements.forEach { it.setDimensions() }
        super.initGui()
    }

    /**
     * Draw a previes of all hud elements, regardless of whether they are visible.
     */
    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {

        // Render a reset Button if not dragging elements
        if (this.draggingElement == null) resetButton.render(mouseX, mouseY)

        for (element in hudElements) {
            element.renderPreview(element == draggingElement)
        }

        mouseDrag(mouseX, mouseY)

        super.drawScreen(mouseX, mouseY, partialTicks)
    }

    @Throws(IOException::class)
    override fun handleMouseInput() {
        super.handleMouseInput()
        val mouseX = Mouse.getEventX() * super.width / super.mc.displayWidth
        val mouseY = super.height - Mouse.getEventY() * super.height / super.mc.displayHeight - 1

        //Scaling mouse coords neccessary here if the gui scale is changed

        var i = Mouse.getEventDWheel()
        if (i != 0) {
            if (i > 1) {
                i = 1
            }
            if (i < -1) {
                i = -1
            }
            if (isShiftKeyDown()) {
                i *= 7
            }
            /** Check all hud elements for scroll action. this is used to change the scale
             * Reversed order is used to guarantee that the panel rendered on top will be handled first. */
            for (element in hudElements.reversed()) {
                if (isCursorOnElement(mouseX, mouseY, element) && element.enabled) {
                    element.scroll(i)
                    return
                }
            }
        }
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        if (mouseButton != 0) return
        if (resetButton.mouseClicked(mouseX, mouseY, mouseButton) && !isCursorOnElement(mouseX, mouseY)) {
            for (element in hudElements.reversed()) {
                if (element.enabled) element.resetElement()
            }
        } else {
            for (element in hudElements.reversed()) {
                if (isCursorOnElement(mouseX, mouseY, element) && element.enabled) {
                    draggingElement = element
                    startOffsetX = mouseX - element.x
                    startOffsetY = mouseY - element.y
                    break
                }
            }
        }
        super.mouseClicked(mouseX, mouseY, mouseButton)
    }

    private fun mouseDrag(mouseX: Int, mouseY: Int) {
        if (draggingElement != null) {
            draggingElement!!.x = mouseX - startOffsetX
            draggingElement!!.y = mouseY - startOffsetY
        }
    }

    override fun mouseReleased(mouseX: Int, mouseY: Int, state: Int) {
        draggingElement = null
        super.mouseReleased(mouseX, mouseY, state)
    }

    override fun onGuiClosed() {
        CatgirlRoutes.moduleConfig.saveConfig()
    }

    override fun doesGuiPauseGame(): Boolean {
        return false
    }

    private fun isCursorOnElement(mouseX: Int, mouseY: Int, element: HudElement): Boolean {
        val scale = element.scale.value
        return mouseX > (element.x - 2.5 * scale) && mouseX < (element.x + element.width * scale + scale) // to the left by 2.5, to the right by 1
                && mouseY > (element.y - 2 * scale) && mouseY < (element.y + element.height * scale - 2 * scale) // to minus 2 bottom, plus 2 top
    }

    private fun isCursorOnElement(mouseX: Int, mouseY: Int): Boolean {
        for (element in hudElements) {
            return isCursorOnElement(mouseX, mouseY, element)
        }
        return false
    }
}