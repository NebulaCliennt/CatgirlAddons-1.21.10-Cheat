package catgirlroutes.ui.animations.impl

import catgirlroutes.ui.animations.Animation


class EaseOutQuadAnimation(duration: Long): Animation<Double>(duration) {

    override fun get(start: Double, end: Double, reverse: Boolean): Double {
        val startVal = if (reverse) end else start
        val endVal = if (reverse) start else end
        if (!isAnimating()) return endVal
        return startVal + (endVal - startVal) * easeOutQuad()
    }

    private fun easeOutQuad(): Float {
        val percent = getPercent() / 100f
        return 1 - (1 - percent) * (1 - percent)
    }
}