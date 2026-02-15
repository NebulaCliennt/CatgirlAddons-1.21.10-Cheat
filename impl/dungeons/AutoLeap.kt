package catgirlroutes.module.impl.dungeons

import catgirlroutes.CatgirlRoutes.Companion.mc
import catgirlroutes.module.Category
import catgirlroutes.module.Module
import catgirlroutes.module.settings.Setting.Companion.withDependency
import catgirlroutes.module.settings.impl.ActionSetting
import catgirlroutes.module.settings.impl.BooleanSetting
import catgirlroutes.module.settings.impl.StringSelectorSetting
import catgirlroutes.utils.ChatUtils.stripControlCodes
import catgirlroutes.utils.dungeon.DungeonClass
import catgirlroutes.utils.dungeon.DungeonUtils.dungeonTeammatesNoSelf
import catgirlroutes.utils.dungeon.DungeonUtils.inBoss
import catgirlroutes.utils.dungeon.DungeonUtils.inDungeons
import catgirlroutes.utils.dungeon.LeapUtils.leap
import net.minecraft.util.StringUtils.stripControlCodes
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.client.event.MouseEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

//TODO: Change the lines that are being checked to the right ones.
//      Add a way to select the player names.
//      Make the triggers for the auto leap.
//      Add S2D cancel.
//      Add fail safes.

object AutoLeap : Module(
    name = "Auto Leap",
    category = Category.DUNGEON,
    tag = TagType.WHIP
) {
    private val fastLeap = BooleanSetting("Fast leap", false)
    private val autoLeap = BooleanSetting("Auto leap", false)

    private var leapMode = StringSelectorSetting("Leap mode", "Name", arrayListOf("Name", "Class"))

    private var clearLeap = StringSelectorSetting("Clear leap", "None", arrayListOf("None")).withDependency {leapMode.selected == "Name"}
    private var s1leap = StringSelectorSetting("S1 leap", "None", arrayListOf("None")).withDependency {leapMode.selected == "Name"}
    private var s2leap = StringSelectorSetting("S2 leap", "None", arrayListOf("None")).withDependency {leapMode.selected == "Name"}
    private var s3leap = StringSelectorSetting("S3 leap", "None", arrayListOf("None")).withDependency {leapMode.selected == "Name"}
    private var s4leap = StringSelectorSetting("S4 leap", "None", arrayListOf("None")).withDependency {leapMode.selected == "Name"}
    var action = ActionSetting("update") {updateTeammates()}.withDependency {leapMode.selected == "Name"}

    private var clearLeapClass = StringSelectorSetting("Clear leap", "None", arrayListOf("Mage", "Bers", "Arch", "Tank", "Heal", "None")).withDependency {leapMode.selected == "Class"}
    private var s1leapClass = StringSelectorSetting("S1 leap", "None", arrayListOf("Mage", "Bers", "Arch", "Tank", "Heal", "None")).withDependency {leapMode.selected == "Class"}
    private var s2leapClass = StringSelectorSetting("S2 leap", "None", arrayListOf("Mage", "Bers", "Arch", "Tank", "Heal", "None")).withDependency {leapMode.selected == "Class"}
    private var s3leapClass = StringSelectorSetting("S3 leap", "None", arrayListOf("Mage", "Bers", "Arch", "Tank", "Heal", "None")).withDependency {leapMode.selected == "Class"}
    private var s4leapClass = StringSelectorSetting("S4 leap", "None", arrayListOf("Mage", "Bers", "Arch", "Tank", "Heal", "None")).withDependency {leapMode.selected == "Class"}

    init {
        this.addSettings(
            fastLeap,
            autoLeap,
            leapMode,
            clearLeap,
            s1leap,
            s2leap,
            s3leap,
            s4leap,
            action,
            clearLeapClass,
            s1leapClass,
            s2leapClass,
            s3leapClass,
            s4leapClass
        )
    }

    private val classEnumMapping = arrayListOf(DungeonClass.Mage, DungeonClass.Berserk, DungeonClass.Archer, DungeonClass.Tank, DungeonClass.Healer, DungeonClass.Unknown)

    private fun updateTeammates() {
        val teammates = arrayListOf<String>()
        if (dungeonTeammatesNoSelf.isEmpty()) return
        dungeonTeammatesNoSelf.forEach{teammate ->
            teammates.add(teammate.name)
        }
        teammates.add("None")
        clearLeap.options = teammates
        s1leap.options = teammates
        s2leap.options = teammates
        s3leap.options = teammates
        s4leap.options = teammates
    }

    /*
    s1 min (89, 100, 48) max (113, 160, 122)
    s2 min (19, 100, 121) max (91, 160, 145)
    s3 min (-6, 100, 50) max (19, 160, 123)
    s4 min (17, 100, 27) max (90, 160, 50)
     */

    @SubscribeEvent
    fun onMouse(event: MouseEvent) {
        if (!event.buttonstate || !inDungeons || !fastLeap.value) return
        if (event.button != 0) return
        if (mc.thePlayer.heldItem == null) return
        if (mc.thePlayer.heldItem.displayName.stripControlCodes() != "Infinileap") return
        handleLeap()
    }

    @SubscribeEvent
    fun onChat(event: ClientChatReceivedEvent) {
        if (!inDungeons || event.type.toInt() == 2 || !inBoss || !autoLeap.value) return
        val message = stripControlCodes(event.message.unformattedText)
        if (message.contains("(7/7)") || message.contains("(8/8)")) {
            handleLeap()
        }
    }

    private fun handleLeap() {
        val posX = mc.thePlayer.posX
        val posY = mc.thePlayer.posY
        val posZ = mc.thePlayer.posZ
        when {
            posX in 89.0..113.0 && posY in 100.0..160.0 && posZ in 48.0..122.0 -> {
                if (leapMode.selected == "Name") leap(s1leap.selected)
                else if (leapMode.selected == "Class") leap(classEnumMapping[s1leapClass.index])
            }
            posX in 19.0..91.0 && posY in 100.0..160.0 && posZ in 121.0..145.0 -> {
                if (leapMode.selected == "Name") leap(s2leap.selected)
                else if (leapMode.selected == "Class") leap(classEnumMapping[s2leapClass.index])
            }
            posX in -6.0..19.0 && posY in 100.0..160.0 && posZ in 50.0..123.0 -> {
                if (leapMode.selected == "Name") leap(s3leap.selected)
                else if (leapMode.selected == "Class") leap(classEnumMapping[s3leapClass.index])
            }
            posX in 17.0..90.0 && posY in 100.0..160.0 && posZ in 27.0..50.0 -> {
                if (leapMode.selected == "Name") leap(s4leap.selected)
                else if (leapMode.selected == "Class") leap(classEnumMapping[s4leapClass.index])
            }
            !inBoss -> {
                if (leapMode.selected == "Name") leap(clearLeap.selected)
                else if (leapMode.selected == "Class") leap(classEnumMapping[clearLeapClass.index])
            }
        }
    }
}