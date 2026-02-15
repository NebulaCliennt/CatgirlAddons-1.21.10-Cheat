package catgirlroutes.config

import com.google.gson.*
import java.io.File
import java.io.IOException

/**
 * Util for managing data like inventorybuttons, autoroutes, autop3, etc configs
 */
object DataManager { // todo add shit

    /**
     * Saves a `JsonObject` wrapped in a JSON array to a file.
     *
     * Creates the file and its parent directories if they do not exist.
     *
     * @param path The file path where the data will be saved.
     * @param data The `JsonObject` to be saved inside a JSON array.
     */
    fun saveDataToFile(path: File, dataList: List<JsonObject>) {
        try {
            path.parentFile?.mkdirs()

            if (!path.exists()) {
                path.createNewFile()
            }

            path.bufferedWriter().use {
                val gson = GsonBuilder().setPrettyPrinting().create()
                val jsonArray = JsonArray().apply { dataList.forEach { add(it) } }
                it.write(gson.toJson(jsonArray))
            }
        } catch (e: IOException) {
            println("Error saving to ${path.path}")
            e.printStackTrace()
        }
    }

    /**
     * Loads a JSON array from a file and returns a list of `JsonObject`s.
     *
     * @param path The file path to load data from.
     * @return A list of `JsonObject`s parsed from the JSON array, or an empty list if an error occurs.
     */
    fun loadDataFromFile(path: File): List<JsonObject> {
        return try {
            path.bufferedReader().use { reader ->
                val jsonContent = reader.readText()
                val gson: Gson = GsonBuilder().setPrettyPrinting().create()
                val jsonArray = gson.fromJson(jsonContent, JsonArray::class.java)
                jsonArray.map { it.asJsonObject }
            }
        } catch (e: java.nio.file.NoSuchFileException) {
            println("File not found: ${path.path}")
            emptyList()
        } catch (e: IOException) {
            e.printStackTrace()
            emptyList()
        } catch (e: JsonSyntaxException) {
            println("Invalid JSON syntax in file: ${path.path}")
            emptyList()
        } catch (e: Exception) {
            println("Error loading data from file: ${path.path}")
            e.printStackTrace()
            emptyList()
        }
    }

}