package catgirlroutes.events.impl

import net.minecraft.entity.Entity
import net.minecraft.network.Packet
import net.minecraft.network.play.server.S02PacketChat
import net.minecraft.network.play.server.S08PacketPlayerPosLook
import net.minecraftforge.fml.common.eventhandler.Cancelable
import net.minecraftforge.fml.common.eventhandler.Event

// too lazy to move these

class EntityRemovedEvent(val entity: Entity) : Event()

class ReceiveChatPacketEvent(val packet: S02PacketChat) : Event()

@Cancelable
class TeleportEventPre(val packet: S08PacketPlayerPosLook) : Event()

@Cancelable
class PacketSentEvent(val packet: Packet<*>) : Event()

class PacketSentEventReturn(val packet: Packet<*>) : Event()

@Cancelable
class PacketReceiveEvent(val packet: Packet<*>) : Event()

class PacketReceiveEventReturn(val packet: Packet<*>) : Event()