package catgirlroutes.utils.render

import catgirlroutes.CatgirlRoutes.Companion.RESOURCE_DOMAIN
import catgirlroutes.CatgirlRoutes.Companion.mc
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.ResourceLocation
import kotlin.random.Random

class FallingKittens {

    private val kittens = List(150) { Kitten() }

    fun drawKittens(type: String, size: Int, speedMultiplier: Float) {
        kittens.forEach {
            it.update(speedMultiplier)
            it.draw(type, size)
        }
    }

    class Kitten {
        private var x: Float
        private var y: Float
        private val speed: Float = Random.nextFloat() * 2 + 1
        private var rotation: Float = Random.nextFloat() * 360
        private val rotationSpeed: Float = Random.nextFloat() * 2 - 1
        private var lastUpdateTime: Long = System.nanoTime()

        init {
            val sr = ScaledResolution(mc)
            x = Random.nextFloat() * sr.scaledWidth
            y = Random.nextFloat() * sr.scaledHeight
        }

        fun update(speedMultiplier: Float) {
            val currentTime = System.nanoTime()
            var deltaTime = (currentTime - lastUpdateTime) / 10_000_000.0f
            val sr = ScaledResolution(mc)

            if (deltaTime > 250) {
                resetPosition(sr)
                deltaTime = 0f
            }

            lastUpdateTime = currentTime

            y += speed * deltaTime * 0.25f * speedMultiplier
            rotation += rotationSpeed * deltaTime * 0.2f

            if (y - 50 > sr.scaledHeight) resetPosition(sr, isOffscreen = true)
        }

        fun draw(type: String, size: Int) {
            val texture = ResourceLocation(RESOURCE_DOMAIN, "fallingkittens/$type")
            val offset = size.toFloat() / 2

            GlStateManager.pushMatrix()
            GlStateManager.translate(x - offset, y - offset, 0f)
            GlStateManager.rotate(rotation, 0f, 0f, 1f)
            GlStateManager.translate(-offset, -offset, 0f)
            mc.textureManager.bindTexture(texture)
            Gui.drawModalRectWithCustomSizedTexture(0, 0, 0f, 0f, size, size, size.toFloat(), size.toFloat())
            GlStateManager.popMatrix()
        }

        private fun resetPosition(sr: ScaledResolution, isOffscreen: Boolean = false) {
            x = Random.nextFloat() * sr.scaledWidth
            y = if (isOffscreen) -15f else Random.nextFloat() * sr.scaledHeight
        }
    }
}
