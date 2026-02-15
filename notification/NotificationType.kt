package catgirlroutes.ui.notification

import catgirlroutes.ui.clickgui.util.ColorUtil
import java.awt.Color

enum class NotificationType(private val colour: Color) {
    INFO(Color.WHITE),
    WARNING(Color(255, 204, 0)),
    ERROR(Color(208, 3, 3));

    fun getColour(): Color {
        return if (this == INFO) {
            ColorUtil.clickGUIColor
        } else {
            this.colour
        }
    }
}