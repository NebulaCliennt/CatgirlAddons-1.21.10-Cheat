package catgirlroutes.module.impl.misc

import catgirlroutes.CatgirlRoutes.Companion.mc
import catgirlroutes.module.Category
import catgirlroutes.module.Module
import catgirlroutes.module.settings.AlwaysActive
import catgirlroutes.module.settings.impl.BooleanSetting
import catgirlroutes.module.settings.impl.StringSetting
import catgirlroutes.utils.dungeon.DungeonUtils.dungeonTeammatesNoSelf
import catgirlroutes.utils.dungeon.DungeonUtils.inDungeons

@AlwaysActive
object NickHider: Module(
    "Nick hider",
    Category.MISC
){
    private var nameInput = StringSetting("Name", "meow")
    private var hideTeammates = BooleanSetting("Hide teammates")

    init {
        addSettings(nameInput, hideTeammates)
    }

    @JvmStatic
    fun replaceText(text: String?): String? {
        if (text == null || mc.thePlayer == null || !this.enabled) return text
        val replacement = nameInput.value
        return text.replace(Regex(mc.thePlayer.name), replacement)
    }

    @JvmStatic
    fun replaceTextTeam(text: String?): String? {
        if (text == null || mc.thePlayer == null || !this.enabled || !inDungeons || !hideTeammates.value) return text

        var result = text
        dungeonTeammatesNoSelf.forEach { teammate ->
            val name = teammate.name
            val replacement = obfuscateString(name)
            result = result!!.replace(Regex(name), replacement)
        }
        return result
    }

    private fun obfuscateString(input: String): String {
        val replacements = listOf('I', 'l')
        return input.map {
            replacements.random()
        }.joinToString("")
    }
}
