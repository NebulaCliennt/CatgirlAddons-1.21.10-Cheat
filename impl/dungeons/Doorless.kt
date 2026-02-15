package catgirlroutes.module.impl.dungeons

import catgirlroutes.CatgirlRoutes.Companion.mc
import catgirlroutes.events.impl.PacketReceiveEvent
import catgirlroutes.events.impl.PacketSentEvent
import catgirlroutes.module.Category
import catgirlroutes.module.Module
import catgirlroutes.module.settings.impl.BooleanSetting
import catgirlroutes.module.settings.impl.NumberSetting
import catgirlroutes.utils.ChatUtils.debugMessage
import catgirlroutes.utils.ClientListener.scheduleTask
import catgirlroutes.utils.MovementUtils.restartMovement
import catgirlroutes.utils.MovementUtils.stopMovement
import catgirlroutes.utils.PacketUtils.sendPacket
import catgirlroutes.utils.PlayerUtils.airClick
import catgirlroutes.utils.dungeon.DungeonUtils.inDungeons
import catgirlroutes.utils.rotation.RotationUtils.snapTo
import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import net.minecraft.block.Block
import net.minecraft.block.BlockSkull
import net.minecraft.block.state.IBlockState
import net.minecraft.init.Blocks
import net.minecraft.item.Item
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook
import net.minecraft.network.play.server.S08PacketPlayerPosLook
import net.minecraft.tileentity.TileEntitySkull
import net.minecraft.util.BlockPos
import net.minecraft.util.Vec3
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.util.*
import kotlin.math.*

object Doorless: Module(
    "Doorless",
    category = Category.DUNGEON,
    tag = TagType.WHIP
){
    private var doorClip = BooleanSetting("Clip", false)
    private var doorMotion = BooleanSetting("Motion", false)
    private val regenDelay = NumberSetting("Skulls regeneration delay", 10.0, 0.0, 20.0, 1.0, unit = "t")

    init {
        this.addSettings(
            doorClip,
            doorMotion,
            regenDelay
        )
    }

    private var inDoor = false
    private var lastUse = System.currentTimeMillis()
    private val validBlocks = listOf(Blocks.coal_block, Blocks.stained_hardened_clay)
    private val skullIds = listOf("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2JjYmJmOTRkNjAzNzQzYTFlNzE0NzAyNmUxYzEyNDBiZDk4ZmU4N2NjNGVmMDRkY2FiNTFhMzFjMzA5MTRmZCJ9fX0=", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWQ5ZDgwYjc5NDQyY2YxYTNhZmVhYTIzN2JkNmFkYWFhY2FiMGMyODgzMGZiMzZiNTcwNGNmNGQ5ZjU5MzdjNCJ9fX0=")

    private var initialCoords = Vec3(0.0, 0.0, 0.0)
    private var initialYaw = 0F
    private var initialPitch = 0F
    private var xOffset = 0
    private var zOffset = 0

    @SubscribeEvent
    fun onPacket(event: PacketSentEvent) {
        if (event.packet !is C03PacketPlayer) return
        if (!inDungeons || inDoor || !mc.thePlayer.isCollidedVertically) return
        if (mc.thePlayer == null || mc.thePlayer.heldItem == null || mc.thePlayer.heldItem.item != Item.getItemById(368)) return
        if (System.currentTimeMillis() - lastUse < 1000) return
        if (!event.packet.isMoving) return
        val posX = event.packet.positionX
        val posY = event.packet.positionY
        val posZ = event.packet.positionZ
        if (posX > 0 || posZ > 0 || posX < -200 || posZ < -200) return
        val xDec = (posX + 200) % 1
        val zDec = (posZ + 200) % 1
        var yaw = -1F
        var pitch = -1F
        zOffset = 0
        xOffset = 0

        if (isWithinTolerence(zDec, 0.7) && xDec > 0.3 && xDec < 0.7 &&
            (validBlocks.contains(getBlockFloor(posX + 1, posY, posZ + 2)) || validBlocks.contains(getBlockFloor(posX - 1, posY, posZ + 2)))) {
            yaw = 0F
            pitch = 77F
            ++zOffset
        } else if (isWithinTolerence(xDec, 0.3) && zDec > 0.3 && zDec < 0.7 &&
            (validBlocks.contains(getBlockFloor(posX - 2, posY, posZ + 1)) || validBlocks.contains(getBlockFloor(posX - 2, posY, posZ - 1)))) {
            yaw = 90F
            pitch = 77F
            --xOffset
        } else if (isWithinTolerence(zDec, 0.3) && xDec > 0.3 && xDec < 0.7 &&
            (validBlocks.contains(getBlockFloor(posX - 1, posY, posZ - 2)) || validBlocks.contains(getBlockFloor(posX + 1, posY, posZ - 2)))) {
            yaw = 180F
            pitch = 77F
            --zOffset
        } else if (isWithinTolerence(xDec, 0.7) && zDec > 0.3 && zDec < 0.7 &&
            (validBlocks.contains(getBlockFloor(posX + 2, posY, posZ - 1)) || validBlocks.contains(getBlockFloor(posX + 2, posY, posZ + 1)))) {
            yaw = 270F
            pitch = 77F
            ++xOffset
        } else if (isWithinTolerence(zDec, 0.95) && xDec > 0.3 && xDec < 0.7 &&
            (validBlocks.contains(getBlockFloor(posX + 1, posY, posZ + 2)) || validBlocks.contains(getBlockFloor(posX - 1, posY, posZ + 2)))) {
            yaw = 0F
            pitch = 84F
            ++zOffset
        } else if (isWithinTolerence(xDec, 0.05) && zDec > 0.3 && zDec < 0.7 &&
            (validBlocks.contains(getBlockFloor(posX - 2, posY, posZ + 1)) || validBlocks.contains(getBlockFloor(posX - 2, posY, posZ - 1)))) {
            yaw = 90F
            pitch = 84F
            --xOffset
        } else if (isWithinTolerence(zDec, 0.05) && xDec > 0.3 && xDec < 0.7 &&
            (validBlocks.contains(getBlockFloor(posX - 1, posY, posZ - 2)) || validBlocks.contains(getBlockFloor(posX + 1, posY, posZ - 2)))) {
            yaw = 180F
            pitch = 84F
            --zOffset
        } else if (isWithinTolerence(xDec, 0.95) && zDec > 0.3 && zDec < 0.7 &&
            (validBlocks.contains(getBlockFloor(posX + 2, posY, posZ - 1)) || validBlocks.contains(getBlockFloor(posX + 2, posY, posZ + 1)))) {
            yaw = 270F
            pitch = 84F
            ++xOffset
        }

        if (yaw < 0F || pitch < 0F) return
        val tileEntity = mc.theWorld?.getTileEntity(getBlockPosFloor(posX + xOffset, posY + 1, posZ + zOffset)) as? TileEntitySkull
            ?: return
        if (tileEntity.playerProfile == null) return
        val skullId = tileEntity.playerProfile.properties.get("textures").first().value
        if (!skullIds.contains(skullId)) return
        inDoor = true
        initialYaw = mc.thePlayer.rotationYaw
        initialPitch = mc.thePlayer.rotationPitch
        initialCoords = Vec3(posX, posY, posZ)
        mc.thePlayer.swingItem()
        sendPacket(C06PacketPlayerPosLook(posX, posY, posZ, yaw, pitch, event.packet.isOnGround))
        airClick()
        waitingForS08 = true
        event.isCanceled = true
    }

    private var waitingForS08 = false

    @SubscribeEvent
    fun onPacketReceive(event: PacketReceiveEvent) {
        if (event.packet !is S08PacketPlayerPosLook || !waitingForS08) return
        val posX = event.packet.x
        val posY = event.packet.y
        val posZ = event.packet.z
        inDoor = false
        lastUse = System.currentTimeMillis()
        if ((posY - initialCoords.yCoord) != 1.0) {
            waitingForS08 = false
            return
        }
        var distance = mc.thePlayer.capabilities.walkSpeed * 1000 * 0.0085
        if (mc.theWorld.getBlockState(BlockPos(floor(posX), floor(posY - 1), floor(posZ))).block == Blocks.cobblestone_wall) {
            distance = mc.thePlayer.capabilities.walkSpeed * 1000 * 0.0032
            setBlockAt(posX + xOffset, posY - 1, posZ + zOffset, 0);
            setBlockAt(posX + xOffset * 2, posY - 1, posZ + zOffset * 2, 0);
            setBlockAt(posX + xOffset * 3, posY - 1, posZ + zOffset * 3, 0);
            setBlockAt(posX + xOffset * 2 + if (zOffset != 0) 1 else 0, posY - 1, posZ + zOffset * 2 + if (xOffset != 0) 1 else 0, 20)
            setBlockAt(posX + xOffset * 2 - if (zOffset != 0) 1 else 0, posY - 1, posZ + zOffset * 2 - if (xOffset != 0) 1 else 0, 20)
            setBlockAt(posX + xOffset * 4, posY - 1, posZ + zOffset * 4, 0)
        }
        stopMovement()
        scheduleTask(0) {
            snapTo(initialYaw, initialPitch)
            if (doorClip.value) clipForward(distance)
        }
        scheduleTask(1) {
            restartMovement()
            if (doorMotion.value) {
                val yawInRadians = (Math.round(mc.thePlayer.rotationYaw / 90) * 90) * (Math.PI / 180);

                val walkSpeed = mc.thePlayer.capabilities.walkSpeed
                val velocity = walkSpeed * 2.7 + (0.22 * (walkSpeed / 1.32));

                mc.thePlayer.motionX = velocity * -sin(yawInRadians)
                mc.thePlayer.motionZ = velocity * cos(yawInRadians);
            }
        }
        setBlockAt(posX + xOffset, posY, posZ + zOffset, 0)
        setBlockAt(posX + xOffset, posY + 1, posZ + zOffset, 0)

        setBlockAt(posX + xOffset * 2, posY, posZ + zOffset * 2, 0)
        setBlockAt(posX + xOffset * 2, posY + 1, posZ + zOffset * 2, 0)

        setBlockAt(posX + xOffset * 3, posY, posZ + zOffset * 3, 0)
        setBlockAt(posX + xOffset * 3, posY + 1, posZ + zOffset * 3, 0)

        setBlockAt(posX + xOffset * 4, posY, posZ + zOffset * 4, 0)
        setBlockAt(posX + xOffset * 4, posY + 1, posZ + zOffset * 4, 0)
        setBlockAt(posX + xOffset * 2 + (if (zOffset != 0) 1 else 0), posY, posZ + zOffset * 2 + (if (xOffset != 0) 1 else 0), 20)
        setBlockAt(posX + xOffset * 2 + (if (zOffset != 0) 1 else 0), posY + 1, posZ + zOffset * 2 + (if (xOffset != 0) 1 else 0), 20)
        setBlockAt(posX + xOffset * 2 - (if (zOffset != 0) 1 else 0), posY, posZ + zOffset * 2 - (if (xOffset != 0) 1 else 0), 20)
        setBlockAt(posX + xOffset * 2 - (if (zOffset != 0) 1 else 0), posY + 1, posZ + zOffset * 2 - (if (xOffset != 0) 1 else 0), 20)

        scheduleTask(regenDelay.value.toInt()) {
            setBlockAt(posX + xOffset * 4, posY, posZ + zOffset * 4)
        }

        waitingForS08 = false
    }

    private fun clipForward(distance: Double) {
        val radians = mc.thePlayer.rotationYaw * Math.PI / 180
        var newX = mc.thePlayer.posX
        var newZ = mc.thePlayer.posZ

        if (abs(-sin(radians)) > abs(cos(radians))) newX += distance * sign(-sin(radians))
        else newZ += distance * sign(cos(radians))
        mc.thePlayer.setPosition(newX, mc.thePlayer.posY, newZ)
        debugMessage(distance)
    }

    private fun isWithinTolerence(n1: Double, n2: Double): Boolean {
        return abs(n1 - n2) < 1e-4;
    }

    private fun getBlockPosFloor(x: Double, y: Double, z: Double): BlockPos {
        return BlockPos(floor(x), floor(y), floor(z));
    }

    private fun getBlockFloor(x: Double, y: Double, z: Double): Block {
        return mc.theWorld.getBlockState(BlockPos(floor(x), floor(y), floor(z))).block;
    }

    private fun setBlockAt(x: Double, y: Double, z: Double, id: Int) {
        val blockPos = getBlockPosFloor(x, y, z)
        mc.theWorld.setBlockState(blockPos, Block.getStateById(id))
        mc.theWorld.markBlockForUpdate(blockPos)
    }

    private fun setBlockAt(x: Double, y: Double, z: Double) {
        val blockPos = getBlockPosFloor(x, y, z)
        val skullBlockState: IBlockState = Blocks.skull.defaultState
            .withProperty(BlockSkull.FACING, net.minecraft.util.EnumFacing.DOWN) // doesn't really change anything visually. cba to fix
        mc.theWorld.setBlockState(blockPos, skullBlockState)

        val tileEntity = mc.theWorld.getTileEntity(blockPos) as? TileEntitySkull
        if (tileEntity != null) {
            val gameProfile = GameProfile(UUID.randomUUID(), null)
            gameProfile.properties.put("textures", Property("textures", skullIds[0]))
            tileEntity.playerProfile = gameProfile
            tileEntity.markDirty()
            mc.theWorld.markBlockForUpdate(blockPos)
        }
    }
}