package catgirlroutes.ui.animations.impl

import catgirlroutes.ui.animations.Animation
import kotlin.math.pow

class EaseInOutCubicAnimation(duration: Long): Animation<Double>(duration) {
    override fun get(start: Double, end: Double, reverse: Boolean): Double {
        if (!isAnimating()) return if (reverse) start else end
        return if (reverse) end + (start - end) * easeInOutCubic() else start + (end - start) * easeInOutCubic()
    }

    private fun easeInOutCubic(): Float {
        val x = getPercent() / 100f
        return if (x < 0.5) { 4 * x * x * x } else { 1 - (-2 * x + 2).pow(3) / 2 }
    }
}