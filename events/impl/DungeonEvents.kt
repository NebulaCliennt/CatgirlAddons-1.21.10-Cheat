package catgirlroutes.events.impl

import catgirlroutes.utils.dungeon.tiles.Room
import net.minecraftforge.fml.common.eventhandler.Event

data class RoomEnterEvent(val room: Room?) : Event()