package catgirlroutes.module.impl.dungeons

import catgirlroutes.CatgirlRoutes.Companion.mc
import catgirlroutes.module.Category
import catgirlroutes.module.Module
import catgirlroutes.module.settings.Setting.Companion.withDependency
import catgirlroutes.module.settings.impl.BooleanSetting
import catgirlroutes.utils.BlockAura
import catgirlroutes.utils.BlockAura.blockArray
import catgirlroutes.utils.ChatUtils.modMessage
import catgirlroutes.utils.ClientListener.scheduleTask
import catgirlroutes.utils.MovementUtils.setKey
import catgirlroutes.utils.PlayerUtils.swapToSlot
import catgirlroutes.utils.dungeon.DungeonUtils.inBoss
import catgirlroutes.utils.render.WorldRenderUtils.drawSquare
import catgirlroutes.utils.rotation.RotationUtils.snapTo
import net.minecraft.entity.Entity
import net.minecraft.entity.item.EntityArmorStand
import net.minecraft.network.play.client.C02PacketUseEntity
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition
import net.minecraft.util.BlockPos
import net.minecraft.util.Vec3
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import java.awt.Color

object Relics: Module(
    "Relics",
    Category.DUNGEON
){
    private val relicAura: BooleanSetting = BooleanSetting("Aura", false)
    private val relicBlink: BooleanSetting = BooleanSetting("Blink", false).withDependency {relicAura.value}
    private val relicLook: BooleanSetting = BooleanSetting("Look", false).withDependency {relicAura.value}

    init {
        addSettings(
            relicAura,
            relicBlink,
            relicLook
        )
    }

    data class C04(val x: Double, val y: Double, val z: Double, val onGround: Boolean)

    private val orangePackets = listOf(
        C04(89.49532717466354, 6.0, 55.435900926589966, true),
        C04(88.59915296836067, 6.0625, 55.02760376077882, true),
        C04(87.53016898146899, 6.0625, 54.54057442086289, true),
        C04(86.36683084341635, 6.0, 54.01055730889261, true),
        C04(85.15197533284594, 6.0, 53.45706887065439, true),
        C04(83.90899133361363, 6.0, 52.89076508678534, true),
        C04(82.65064917778804, 6.0, 52.31746412338913, true),
        C04(81.3839214674885, 6.0, 51.74034269952732, true),
        C04(80.11261524391439, 6.0, 51.161135304009, true),
        C04(78.838809151802, 6.0, 50.580788967833946, true),
        C04(77.56363813130916, 6.0, 49.99982076998808, true),
        C04(76.28772185983404, 6.0, 49.418513035630504, true),
        C04(75.01139868127532, 6.0, 48.83701991431601, true),
        C04(73.73485333142317, 6.0, 48.25542557171128, true),
        C04(72.45818667603069, 6.0, 47.67377596227566, true),
        C04(71.18145378780551, 6.0, 47.09209617714687, true),
        C04(69.90468473644947, 6.0, 46.5103999160877, true),
        C04(68.62789594002169, 6.0, 45.928694659169494, true),
        C04(67.3510963627835, 6.0, 45.34698449051168, true),
        C04(66.07429089922212, 6.0, 44.765271640043736, true),
        C04(64.79748222172792, 6.0, 44.183557325307284, true),
        C04(63.52067178942619, 6.0, 43.60184221108014, true),
        C04(62.243860398999445, 6.0, 43.02012666033102, true),
        C04(60.96704848543637, 6.0, 42.43841087124088, true),
        C04(59.69023628624084, 6.0, 41.856694952016525, true),
        C04(58.41342393108996, 6.0, 41.27497896173888, true)
    )

    private val redPackets = listOf(
        C04(23.485512018203735, 6.0, 57.397353172302246, true),
        C04(24.352063615464257, 6.0, 56.92946117591761, true),
        C04(25.38571284880744, 6.0, 56.37134530044137, true),
        C04(26.51059740204898, 6.0, 55.763967181259176, true),
        C04(27.685296445741105, 6.0, 55.12969187388936, true),
        C04(28.887194204378453, 6.0, 54.48073070006328, true),
        C04(30.103942463100815, 5.921599998474121, 53.823751042220735, false),
        C04(30.790707572238603, 5.766367993957519, 53.45293423265625, false),
        C04(31.438084322275124, 5.535840625044555, 53.10338505151027, false),
        C04(32.049617664496495, 5.231523797587011, 52.773189412782905, false),
        C04(32.628533504666045, 5.0, 52.46060549816404, true),
        C04(33.71585894068581, 5.0, 51.873507311886215, true),
        C04(34.870050703992874, 5.0, 51.250304849168316, true),
        C04(36.06075148623942, 5.0, 50.60738944922443, true),
        C04(37.2713861951423, 5.0, 49.95371078430497, true),
        C04(38.492904829263736, 5.0, 49.29415537602625, true),
        C04(39.720366087244756, 5.0, 48.63139126550067, true),
        C04(40.951072018229986, 5.0, 47.96687520334481, true),
        C04(42.18354954088129, 5.0, 47.30140257548772, true),
        C04(43.416994352694616, 5.0, 46.63540766269709, true),
        C04(44.65096730445176, 5.0, 45.96912758229962, true),
        C04(45.88522862065172, 5.0, 45.30269180037073, true),
        C04(47.11964738385575, 5.0, 44.636171005395816, true)
    )

    @SubscribeEvent
    fun onRender(event: RenderWorldLastEvent) {
        if ((!relicBlink.value && !relicLook.value) || !inBoss) return

        val posX = mc.thePlayer.posX
        val posY = mc.thePlayer.posY
        val posZ = mc.thePlayer.posZ

        if (posX in 90.0..90.7 && posY == 6.0 && posZ in 55.0..55.7) {
            drawSquare(90.35, 6.0, 55.35, 0.7, 0.7, Color.GREEN, phase = false)
        } else drawSquare(90.35, 6.0, 55.35, 0.7, 0.7, Color.RED, phase = false)
        if (posX in 22.3..23.0 && posY == 6.0 && posZ in 58.0..58.7) {
            drawSquare(22.65, 6.0, 58.35, 0.7, 0.7, Color.GREEN, phase = false)
        } else drawSquare(22.65, 6.0, 58.35, 0.7, 0.7, Color.RED, phase = false)
    }

    @SubscribeEvent
    fun onLoad(event: WorldEvent.Load) {

    }

    @SubscribeEvent
    fun onTick(event: TickEvent.ClientTickEvent) {
        if (event.phase != TickEvent.Phase.START) return

    }

    private var auraCooldown = false

    @SubscribeEvent
    fun onRenderWorld(event: RenderWorldLastEvent) {
        if (auraCooldown || !relicAura.value) return
        val armorStands = mc.theWorld?.loadedEntityList?.firstOrNull {
            it is EntityArmorStand && it.inventory?.get(4)?.displayName?.contains("Relic") == true && mc.thePlayer.getDistanceToEntity(it) < 4.5 } ?: return
        auraCooldown = true
        interactWithEntity(armorStands)
        scheduleTask(20) {auraCooldown = false}
    }

    private fun interactWithEntity(entity: Entity) {
        val objectMouseOver = mc.objectMouseOver?.hitVec ?: return
        mc.netHandler.addToSendQueue(C02PacketUseEntity(entity, Vec3(objectMouseOver.xCoord - entity.posX, objectMouseOver.yCoord - entity.posY, objectMouseOver.zCoord - entity.posZ)))
        relicBlink()
    }

    private fun relicBlink() {
        val posX = mc.thePlayer.posX
        val posY = mc.thePlayer.posY
        val posZ = mc.thePlayer.posZ

        if (posX in 90.0..90.7 && posY == 6.0 && posZ in 55.0..55.7) {
            if (Blink.packetArray >= orangePackets.size && relicBlink.value) {
                modMessage("Blinking orange")
                snapTo(111F, 0F)
                swapToSlot(8)
                blockArray.add(BlockAura.BlockAuraAction(BlockPos(57.0, 7.0, 42.0), 6.0))
                scheduleTask(0) {
                    orangePackets.forEach{packet ->
                        mc.netHandler.networkManager.sendPacket(C04PacketPlayerPosition(packet.x, packet.y, packet.z, packet.onGround))
                        Blink.packetArray -= 1
                    }
                    mc.thePlayer.setPosition(orangePackets.last().x, orangePackets.last().y, orangePackets.last().z)
                }
            } else if (relicLook.value) {
                snapTo(111F, 0F)
                setKey("w", true)
                swapToSlot(8)
                blockArray.add(BlockAura.BlockAuraAction(BlockPos(57.0, 7.0, 42.0), 6.0))
            }
        } else if (posX in 22.3..23.0 && posY == 6.0 && posZ in 58.0..58.7) {
            if (relicBlink.value && Blink.packetArray >= redPackets.size) {
                modMessage("Blinking red")
                snapTo(-120F, 0F)
                swapToSlot(8)
                blockArray.add(BlockAura.BlockAuraAction(BlockPos(51.0, 7.0, 42.0), 6.0))
                scheduleTask(0) {
                    redPackets.forEach{packet ->
                        mc.netHandler.networkManager.sendPacket(C04PacketPlayerPosition(packet.x, packet.y, packet.z, packet.onGround))
                        Blink.packetArray -= 1
                    }
                    mc.thePlayer.setPosition(redPackets.last().x, redPackets.last().y, redPackets.last().z)
                }
            } else if (relicLook.value) {
                snapTo(-120F, 0F)
                setKey("w", true)
                swapToSlot(8)
                blockArray.add(BlockAura.BlockAuraAction(BlockPos(51.0, 7.0, 42.0), 6.0))
            }
        }
    }
}