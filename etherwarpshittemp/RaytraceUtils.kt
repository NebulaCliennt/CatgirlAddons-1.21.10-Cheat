package catgirlroutes.utils.etherwarpshittemp
import catgirlroutes.CatgirlRoutes.Companion.mc
import net.minecraft.client.multiplayer.WorldClient
import net.minecraft.init.Blocks
import net.minecraft.util.BlockPos
import net.minecraft.util.Tuple
import net.minecraft.util.Vec3
import kotlin.math.abs
import kotlin.math.floor

object RaytraceUtils {
    private fun sig(`val`: Double): Int {
        return `val`.compareTo(0.0)
    }

    private fun frac0(`val`: Double): Double {
        return `val` - floor(`val`)
    }

    private fun frac1(`val`: Double): Double {
        return 1.0 - `val` + floor(`val`)
    }

    private fun traverseVoxels(start: Vec3, end: Vec3): Vec3? {
        var currVoxelX = floor(start.xCoord).toInt()
        var currVoxelY = floor(start.yCoord).toInt()
        var currVoxelZ = floor(start.zCoord).toInt()

        val lastVoxelX = floor(end.xCoord).toInt()
        val lastVoxelY = floor(end.yCoord).toInt()
        val lastVoxelZ = floor(end.zCoord).toInt()

        val diffX = end.xCoord - start.xCoord
        val diffY = end.yCoord - start.yCoord
        val diffZ = end.zCoord - start.zCoord

        val stepX = sig(diffX)
        val stepY = sig(diffY)
        val stepZ = sig(diffZ)

        val tDeltaX = if ((stepX == 0)) Double.MAX_VALUE else (stepX / diffX)
        val tDeltaY = if ((stepY == 0)) Double.MAX_VALUE else (stepY / diffY)
        val tDeltaZ = if ((stepZ == 0)) Double.MAX_VALUE else (stepZ / diffZ)

        var tMaxX = if ((stepX > 0)) (tDeltaX * frac1(start.xCoord)) else (tDeltaX * frac0(start.xCoord))
        var tMaxY = if ((stepY > 0)) (tDeltaY * frac1(start.yCoord)) else (tDeltaY * frac0(start.yCoord))
        var tMaxZ = if ((stepZ > 0)) (tDeltaZ * frac1(start.zCoord)) else (tDeltaZ * frac0(start.zCoord))

        val w: WorldClient = mc.theWorld
        var iterations =
            (abs((lastVoxelX - currVoxelX).toDouble()) + abs((lastVoxelY - currVoxelY).toDouble()) + abs((lastVoxelZ - currVoxelZ).toDouble())).toInt()
        while (iterations-- >= 0 && (tMaxX <= 1.0 || tMaxY <= 1.0 || tMaxZ <= 1.0)) {
            if (tMaxX < tMaxY) {
                if (tMaxX < tMaxZ) {
                    currVoxelX += stepX
                    val vec31 = Vec3(currVoxelX.toDouble(), currVoxelY.toDouble(), currVoxelZ.toDouble())
                    val iBlockState1 = w.getBlockState(BlockPos(vec31))
                    if (iBlockState1.block !== Blocks.air) return vec31
                    tMaxX += tDeltaX
                    continue
                }
                currVoxelZ += stepZ
                val vec3 = Vec3(currVoxelX.toDouble(), currVoxelY.toDouble(), currVoxelZ.toDouble())
                val iBlockState = w.getBlockState(BlockPos(vec3))
                if (iBlockState.block !== Blocks.air) return vec3
                tMaxZ += tDeltaZ
                continue
            }
            if (tMaxY < tMaxZ) {
                currVoxelY += stepY
                val vec3 = Vec3(currVoxelX.toDouble(), currVoxelY.toDouble(), currVoxelZ.toDouble())
                val iBlockState = w.getBlockState(BlockPos(vec3))
                if (iBlockState.block !== Blocks.air) return vec3
                tMaxY += tDeltaY
                continue
            }
            currVoxelZ += stepZ
            val v = Vec3(currVoxelX.toDouble(), currVoxelY.toDouble(), currVoxelZ.toDouble())
            val currentBlock = w.getBlockState(BlockPos(v))
            if (currentBlock.block !== Blocks.air) return v
            tMaxZ += tDeltaZ
        }
        return null
    }

    fun getEtherwarpBlockSuccess(pitch: Float, yaw: Float, startPos_: Vec3, distance: Double): Pair<Boolean, Vec3?> {
        val lookVec: Vec3 = VectorUtils.scale(VectorUtils.fromPitchYaw(pitch, yaw), distance)
        val startPos = startPos_.addVector(0.0, mc.thePlayer.getEyeHeight().toDouble(), 0.0)
        val endPos = lookVec.add(startPos)
        val etherSpot = traverseVoxels(startPos, endPos)
            ?: return Pair(false, null)
        return Pair(WorldUtils.isValidEtherwarpBlock(etherSpot), etherSpot)
    }
}