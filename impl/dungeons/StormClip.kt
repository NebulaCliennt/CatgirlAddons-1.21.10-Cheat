package catgirlroutes.module.impl.dungeons;

import catgirlroutes.events.impl.PacketReceiveEvent
import catgirlroutes.module.Category
import catgirlroutes.module.Module
import catgirlroutes.module.settings.impl.NumberSetting
import catgirlroutes.utils.ClientListener.scheduleTask
import catgirlroutes.utils.PlayerUtils.relativeClip
import net.minecraft.network.play.server.S08PacketPlayerPosLook
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

object StormClip : Module(
    "Storm Clip",
    category = Category.DUNGEON,
    description = "Clips you down into p2 at start of boss."
){
    private val stormClipDistance: NumberSetting = NumberSetting("Storm Clip distance", 30.0, 20.0, 50.0, 1.0, "Distance to clip down")
    private var clipped = false
    init {
        this.addSettings(
            stormClipDistance
        )
    }
    @SubscribeEvent
    fun onWorldUnload(event: WorldEvent.Unload) {
        clipped = false
    }
    @SubscribeEvent
    fun onPacket(event: PacketReceiveEvent) {
        if (event.packet !is S08PacketPlayerPosLook || clipped) return
        if (event.packet.x == 73.5 && event.packet.y == 221.5 && event.packet.z == 14.5) {
            clipped = true
            scheduleTask(1) { relativeClip(0.0, stormClipDistance.value * -1, 0.0) }
        }
    }
}
