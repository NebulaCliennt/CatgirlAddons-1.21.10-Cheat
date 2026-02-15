package catgirlroutes.module.impl.misc

import catgirlroutes.module.Category
import catgirlroutes.module.Module
import catgirlroutes.utils.ChatUtils
import catgirlroutes.utils.ChatUtils.stripControlCodes
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

// https://github.com/appable0/AmbientAddons/blob/master/src/main/kotlin/com/ambientaddons/features/misc/NPCDialogue.kt
object AutoDialogue : Module(
    "Auto Dialogue",
    Category.MISC,
    "Automatically continues dialogue with NPCs"
) {
    @SubscribeEvent
    fun onChat(event: ClientChatReceivedEvent) {
        if (event.message == null || event.type == 2.toByte()) return
        if (!event.message.unformattedText.stripControlCodes().startsWith("Select an option: ")) return
        val command = event.message.siblings.getOrNull(0)?.chatStyle?.chatClickEvent?.value ?: return
        ChatUtils.commandAny(command)
    }
}