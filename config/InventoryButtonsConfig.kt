package catgirlroutes.config

import catgirlroutes.ui.misc.inventorybuttons.InventoryButton
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import java.io.File


object InventoryButtonsConfig {
    var allButtons: MutableList<InventoryButton> = mutableListOf()
    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()
    private val file = File("config/catgirlroutes/buttons_data.json")

    fun load() {
        if (file.exists()) {
            allButtons = gson.fromJson(file.readText(), object : TypeToken<List<InventoryButton>>() {}.type)
        } else {
            allButtons = createDefaultInventoryButtons()
            save()
        }
    }

    fun save() {
        file.writeText(gson.toJson(allButtons))
    }
}

fun createDefaultInventoryButtons(): MutableList<InventoryButton> {
    val inventoryButtons = mutableListOf<InventoryButton>()
    val o = 19
    // Below crafting
    inventoryButtons.add(InventoryButton(88, 63))
    inventoryButtons.add(InventoryButton(88 + o, 63))
    inventoryButtons.add(InventoryButton(88 + o * 2, 63))
    inventoryButtons.add(InventoryButton(88 + o * 3, 63))

    // Above crafting
    inventoryButtons.add(InventoryButton(88, 5))
    inventoryButtons.add(InventoryButton(88 + o, 5))
    inventoryButtons.add(InventoryButton(88 + o * 2, 5))
    inventoryButtons.add(InventoryButton(88 + o * 3, 5))

    // Crafting square
    inventoryButtons.add(InventoryButton(88, 26))
    inventoryButtons.add(InventoryButton(88 + 18, 26))
    inventoryButtons.add(InventoryButton(88, 26 + 18))
    inventoryButtons.add(InventoryButton(88 + 18, 26 + 18))

    // Crafting result
    inventoryButtons.add(InventoryButton(144, 36))

    // Player menu area
    inventoryButtons.add(InventoryButton(60, 9)) // top right
    inventoryButtons.add(InventoryButton(60, 60)) // bottom right
    inventoryButtons.add(InventoryButton(27, 9)) // top left
    inventoryButtons.add(InventoryButton(27, 60)) // bottom left

    // Right side
    for (i in 0..7) {
        inventoryButtons.add(InventoryButton(2 + 175, 5 + o * i))
    }

    // Top side
    for (i in 0..7) {
        inventoryButtons.add(InventoryButton(5 + 4 + o * i, -18))
    }

    // Left side
    for (i in 0..7) {
        if (i > 3) {
            inventoryButtons.add(InventoryButton(-18, 5 + o * i))
        } else {
            inventoryButtons.add(InventoryButton(-18, 5 + o * i, "/eq", "barrier", true)) // eq buttons
            inventoryButtons.add(InventoryButton(-18 - 18 - 1, 5 + o * i))
        }

    }

    // Bottom side
    for (i in 0..7) {
        inventoryButtons.add(InventoryButton(5 + 4 + o * i, 2 + 165))
    }

    return inventoryButtons
}
