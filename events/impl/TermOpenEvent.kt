package catgirlroutes.events.impl

import net.minecraft.network.play.server.S2DPacketOpenWindow
import net.minecraftforge.fml.common.eventhandler.Event

open class TermOpenEvent : Event() {
    class open(val packet: S2DPacketOpenWindow) : TermOpenEvent()
}