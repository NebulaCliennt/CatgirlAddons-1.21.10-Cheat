package catgirlroutes.utils.etherwarpshittemp

import catgirlroutes.CatgirlRoutes.Companion.mc
import net.minecraft.block.Block
import net.minecraft.client.multiplayer.WorldClient
import net.minecraft.util.BlockPos
import net.minecraft.util.Vec3


object WorldUtils {
    private val validEwBlocks: ArrayList<Block> = ArrayList<Block>()

    init {
        validEwBlocks.add(Block.getBlockFromName("minecraft:air"))
        validEwBlocks.add(Block.getBlockFromName("minecraft:fire"))
        validEwBlocks.add(Block.getBlockFromName("minecraft:carpet"))
        validEwBlocks.add(Block.getBlockFromName("minecraft:skull"))
        validEwBlocks.add(Block.getBlockFromName("minecraft:lever"))
        validEwBlocks.add(Block.getBlockFromName("minecraft:stone_button"))
        validEwBlocks.add(Block.getBlockFromName("minecraft:wooden_button"))
        validEwBlocks.add(Block.getBlockFromName("minecraft:torch"))
        validEwBlocks.add(Block.getBlockFromName("minecraft:string"))
        validEwBlocks.add(Block.getBlockFromName("minecraft:tripwire_hook"))
        validEwBlocks.add(Block.getBlockFromName("minecraft:tripwire"))
        validEwBlocks.add(Block.getBlockFromName("minecraft:rail"))
        validEwBlocks.add(Block.getBlockFromName("minecraft:activator_rail"))
        validEwBlocks.add(Block.getBlockFromName("minecraft:snow_layer"))
        validEwBlocks.add(Block.getBlockFromName("minecraft:carrots"))
        validEwBlocks.add(Block.getBlockFromName("minecraft:wheat"))
        validEwBlocks.add(Block.getBlockFromName("minecraft:potatoes"))
        validEwBlocks.add(Block.getBlockFromName("minecraft:nether_wart"))
        validEwBlocks.add(Block.getBlockFromName("minecraft:pumpkin_stem"))
        validEwBlocks.add(Block.getBlockFromName("minecraft:melon_stem"))
        validEwBlocks.add(Block.getBlockFromName("minecraft:redstone_torch"))
        validEwBlocks.add(Block.getBlockFromName("minecraft:redstone_wire"))
        validEwBlocks.add(Block.getBlockFromName("minecraft:red_flower"))
        validEwBlocks.add(Block.getBlockFromName("minecraft:yellow_flower"))
        validEwBlocks.add(Block.getBlockFromName("minecraft:sapling"))
        validEwBlocks.add(Block.getBlockFromName("minecraft:flower_pot"))
        validEwBlocks.add(Block.getBlockFromName("minecraft:deadbush"))
        validEwBlocks.add(Block.getBlockFromName("minecraft:tallgrass"))
        validEwBlocks.add(Block.getBlockFromName("minecraft:ladder"))
        validEwBlocks.add(Block.getBlockFromName("minecraft:double_plant"))
        validEwBlocks.add(Block.getBlockFromName("minecraft:unpowered_repeater"))
        validEwBlocks.add(Block.getBlockFromName("minecraft:powered_repeater"))
        validEwBlocks.add(Block.getBlockFromName("minecraft:unpowered_comparator"))
        validEwBlocks.add(Block.getBlockFromName("minecraft:powered_comparator"))
        validEwBlocks.add(Block.getBlockFromName("minecraft:web"))
        validEwBlocks.add(Block.getBlockFromName("minecraft:waterlily"))
        validEwBlocks.add(Block.getBlockFromName("minecraft:water"))
        validEwBlocks.add(Block.getBlockFromName("minecraft:lava"))
        validEwBlocks.add(Block.getBlockFromName("minecraft:torch"))
        validEwBlocks.add(Block.getBlockFromName("minecraft:vine"))
    }

    fun isValidEtherwarpBlock(pos: Vec3?): Boolean {
        val world: WorldClient = mc.theWorld
        val blockPos = BlockPos(pos)
        if (validEwBlocks.contains(world.getBlockState(blockPos).block)) {
            return false
        }
        if (!validEwBlocks.contains(world.getBlockState(blockPos.add(0, 2, 0)).block)) {
            return false
        }
        return validEwBlocks.contains(world.getBlockState(blockPos.add(0, 1, 0)).block)
    }
}