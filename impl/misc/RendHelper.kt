package catgirlroutes.module.impl.misc

import catgirlroutes.CatgirlRoutes.Companion.mc
import catgirlroutes.events.impl.PacketReceiveEvent
import catgirlroutes.module.Category
import catgirlroutes.module.Module
import catgirlroutes.module.settings.impl.BooleanSetting
import catgirlroutes.utils.ChatUtils.commandAny
import catgirlroutes.utils.ChatUtils.modMessage
import catgirlroutes.utils.ClientListener.scheduleTask
import catgirlroutes.utils.MovementUtils.jump
import catgirlroutes.utils.PlayerUtils.swapFromName
import net.minecraft.network.play.server.S02PacketChat
import net.minecraft.network.play.server.S2DPacketOpenWindow
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.event.entity.player.PlayerInteractEvent
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

object RendHelper : Module(
    name = "Rend Helper",
    category = Category.MISC
) {

    var dpsPhase = false
    var clickedBone = true

    @SubscribeEvent
    fun onChat(event: ClientChatReceivedEvent) {
        if (event.message.unformattedText == "[NPC] Elle: POW! SURELY THAT'S IT! I don't think he has any more in him!") {
            dpsPhase = true
            clickedBone = false
        }
    }

    @SubscribeEvent
    fun onWorldLoad(event: WorldEvent.Unload) {
        dpsPhase = false
    }

    @SubscribeEvent
    fun onInteract(event: PlayerInteractEvent) {
        if (event.action == PlayerInteractEvent.Action.RIGHT_CLICK_AIR) {
            if (clickedBone) return
            val item = mc.thePlayer.heldItem.displayName
            if (item.contains("Bonemerang")) {
                clickedBone = true
                    scheduleTask(1) {
                        swapFromName("Blade of the Volcano")
                        commandAny("eq")
                        jump()
                }
            }
        }
    }
}