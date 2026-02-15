package catgirlroutes.events.impl

import net.minecraftforge.fml.common.eventhandler.Cancelable
import net.minecraftforge.fml.common.eventhandler.Event

open class ClickEvent : Event() {
    @Cancelable
    class LeftClick : ClickEvent()

    @Cancelable
    class RightClick : ClickEvent()

    @Cancelable
    class MiddleClick : ClickEvent()
}