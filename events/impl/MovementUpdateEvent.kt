package catgirlroutes.events.impl

import net.minecraftforge.fml.common.eventhandler.Cancelable
import net.minecraftforge.fml.common.eventhandler.Event

open class MovementUpdateEvent : Event() {
    @Cancelable
    class Pre : MovementUpdateEvent()

    @Cancelable
    class Post : MovementUpdateEvent() // todo: add post idk
}