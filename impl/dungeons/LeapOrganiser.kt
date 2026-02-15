package catgirlroutes.module.impl.dungeons

import catgirlroutes.CatgirlRoutes.Companion.display
import catgirlroutes.module.Category
import catgirlroutes.module.Module
import catgirlroutes.module.settings.Visibility
import catgirlroutes.module.settings.impl.StringSelectorSetting
import catgirlroutes.module.settings.impl.StringSetting
import catgirlroutes.ui.misc.LeapOrganiser

object LeapOrganiser : Module(
    "Leap organiser",
    Category.DUNGEON,
    tag = TagType.WHIP
) {
    val leapOrder: StringSetting = StringSetting("Leap order", "_ _ _ _", visibility = Visibility.HIDDEN)
    val player1Note: StringSetting = StringSetting("Player1 note", "ee2", description = "Player 1 note")
    val player2Note: StringSetting = StringSetting("Player2 note", "core", description = "Player 2 note")
    val player3Note: StringSetting = StringSetting("Player3 note", "ee3", description = "Player 3 note")
    val player4Note: StringSetting = StringSetting("Player4 note", "i4", description = "Player 4 note")
    val leapMenu: StringSelectorSetting = StringSelectorSetting("Leap menu", "SA", arrayListOf("SA", "Odin"))

    init {
        addSettings(this.leapOrder, this.leapMenu, this.player1Note, this.player2Note, this.player3Note, this.player4Note)
    }

    override fun onKeyBind() {
        this.toggle()
    }

    override fun onEnable() {
        display = LeapOrganiser()
        toggle()
        super.onEnable()
    }
}