package catgirlroutes.ui.misc.elements

import catgirlroutes.ui.misc.elements.impl.MiscElementText

/**
 * The base class for all [MiscElement]'s
 *
 * You can implement these elements anywhere you want
 *
 * Override the [render] method to define custom drawing behavior for the element.
 * Override the interaction methods like [mouseClicked], [mouseClickMove], and [keyTyped] to define how the element
 * should respond to user input.
 *
 * This class also provides basic hover detection with the [isHovered] method, which checks whether the mouse cursor
 * is within the bounds of the element.
 */
abstract class MiscElement(
    var x: Double = 0.0,
    var y: Double = 0.0,
    var width: Double = 100.0,
    var height: Double = 20.0
) {

    /**
     * Renders the element
     */
    abstract fun render(mouseX: Int, mouseY: Int)

    /**
     * Handles mouse clicks
     * @return true if an action was performed.
     */
    open fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int): Boolean { return false }

    /**
     * Handles mouse click and move
     */
    open fun mouseClickMove(mouseX: Int, mouseY: Int, clickedMouseButton: Int, timeSinceLastClick: Long) {  }

    /**
     * Dispatches key press.
     * @return true if any of the elements used the input.
     */
    open fun keyTyped(typedChar: Char, keyCode: Int): Boolean { return false }

    /**
     * Checks if the element is being hovered over by the mouse.
     */
    open fun isHovered(mouseX: Int, mouseY: Int, xOff: Int = 0, yOff: Int = 0): Boolean {
        return mouseX >= x + xOff && mouseX <= x + width + xOff &&
                mouseY >= y + yOff && mouseY <= y + height + yOff
    }

    companion object {
        var currentlyFocused: MiscElementText? = null
    }
}