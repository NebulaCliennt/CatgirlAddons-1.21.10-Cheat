package catgirlroutes.utils.etherwarpshittemp

import net.minecraft.util.Vec3
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin


object VectorUtils {
    fun fromPitchYaw(pitch: Float, yaw: Float): Vec3 {
        val f = cos(-yaw * 0.017453292 - Math.PI)
        val f1 = sin(-yaw * 0.017453292 - Math.PI)
        val f2 = -cos(-pitch * 0.017453292)
        val f3 = sin(-pitch * 0.017453292)
        return (Vec3(f1 * f2, f3, f * f2)).normalize()
    }

    fun scale(v: Vec3, scalar: Double): Vec3 {
        return Vec3(v.xCoord * scalar, v.yCoord * scalar, v.zCoord * scalar)
    }

    fun toUnit(v: Vec3): Vec3 {
        val len = v.lengthVector()
        return Vec3(v.xCoord / len, v.yCoord / len, v.zCoord / len)
    }

    fun ceilVec(vec3: Vec3): Vec3 {
        return Vec3(ceil(vec3.xCoord), ceil(vec3.yCoord), ceil(vec3.zCoord))
    }

    fun floorVec(vec3: Vec3): Vec3 {
        return Vec3(floor(vec3.xCoord), floor(vec3.yCoord), floor(vec3.zCoord))
    }
}