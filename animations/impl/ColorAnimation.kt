package catgirlroutes.ui.animations.impl

import catgirlroutes.utils.render.Color
import java.awt.Color as JavaColor

// TODO: fix it
class ColorAnimation(duration: Long) {

    private val anim = LinearAnimation<Int>(duration) // temporary fix to weird colors

    fun start(bypass: Boolean = false): Boolean {
        return anim.start(bypass)
    }

    fun isAnimating(): Boolean {
        return anim.isAnimating()
    }

    fun percent(): Int {
        return anim.getPercent()
    }

    fun get(start: JavaColor, end: JavaColor, reverse: Boolean): JavaColor {
        return Color(
            anim.get(start.red, end.red, reverse),
            anim.get(start.green, end.green, reverse),
            anim.get(start.blue, end.blue, reverse),
            anim.get(start.alpha, end.alpha, reverse) / 255f,
        ).javaColor
    }
}