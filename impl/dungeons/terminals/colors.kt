package catgirlroutes.module.impl.dungeons.terminals

import catgirlroutes.events.impl.PacketReceiveEvent
import net.minecraft.network.play.server.S2DPacketOpenWindow
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

object colors {

    @SubscribeEvent
    fun onPacketS2D(event: PacketReceiveEvent) {
        if (event.packet !is S2DPacketOpenWindow) return

    }

}