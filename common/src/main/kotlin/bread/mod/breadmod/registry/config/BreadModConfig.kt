package bread.mod.breadmod.registry.config

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import dev.architectury.platform.Platform
import org.apache.logging.log4j.LogManager
import java.io.File
import java.io.FileInputStream
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.pathString

/**
 * Abstract helper class for registering config values.
 *
 * @author Logan McLean
 */
abstract class BreadModConfig {
    var json: JsonObject = JsonObject()
    private val configFolder = Platform.getConfigFolder()
    val valueList = mutableListOf<ConfigValue<*>>()

    /**
     * Attempts to retrieve the provided [name] from the config file.
     *
     * @return the value if it exists in file, else builds a default value
     * @author Logan McLean
     */
    inline fun <reified T> getOrDefault(
        name: String,
        configBuilder: ConfigValue.Builder<T>
    ): ConfigValue<T> {
        val jsonValue = json.getAsJsonObject(name)
        val logger = LogManager.getLogger()

        return if (jsonValue != null) {
            val jValue = jsonValue.get("value")
            val cValue = when (T::class) {
                Boolean::class -> jValue.asBoolean
                Int::class -> jValue.asInt
                String::class -> jValue.asString
                Double::class -> jValue.asDouble
                Float::class -> jValue.asFloat
                else -> throw UnsupportedOperationException(T::class.qualifiedName)
            }

            val jDefaultValue = jsonValue.get("default_value")
            val cDefaultValue = when (T::class) {
                Boolean::class -> jDefaultValue.asBoolean
                Int::class -> jDefaultValue.asInt
                String::class -> jDefaultValue.asString
                Double::class -> jDefaultValue.asDouble
                Float::class -> jDefaultValue.asFloat
                else -> throw UnsupportedOperationException(T::class.qualifiedName)
            }
            val configValue = ConfigValue(
                name,
                cValue as T,
                cDefaultValue as T,
                jsonValue.get("comment").asString
            )
            valueList.add(configValue)
            configValue
        } else {
            logger.error("$name does not exist in config, creating default.")
            val builder = configBuilder.build()
            valueList.add(builder)
            builder
        }
    }

    /**
     * Overwrites the current [value] with [newValue].
     * * Config file automatically updates upon calling this function
     *
     * @author Logan McLean
     */
    fun <T> setConfigValue(value: ConfigValue<T>, newValue: T) {
        value.value = newValue
        valueList.remove(value)
        valueList.add(value)
        saveConfig()
    }

    fun configLocation(): Path = Path("${configFolder.pathString}/${fileName()}.json")

    fun configExists(): Boolean = Files.exists(configLocation())

    /**
     * Reads the config from disk.
     *
     * @return [JsonObject]
     * @author Logan McLean
     */
    protected fun readConfig(): JsonObject =
        if (configExists()) {
            val inputFile = FileInputStream(File(configLocation().toString()))
            val inputFileString: String = inputFile.readBytes().decodeToString()
            inputFile.close()
            Gson().fromJson(inputFileString, JsonObject::class.java)
        } else JsonObject()

    /**
     * Writes the current config values to disk under the specified [fileName].
     *
     * @author Logan McLean
     */
    protected fun saveConfig() {
        LogManager.getLogger().info("Writing config: ${fileName()}")
        valueList.forEach { value ->
            json.add(value.name, JsonObject().also { valObj ->
                when (value.value) {
                    is String -> valObj.addProperty("value", value.value as String)
                    is Boolean -> valObj.addProperty("value", value.value as Boolean)
                    is Int -> valObj.addProperty("value", value.value as Int)
                    is Double -> valObj.addProperty("value", value.value as Double)
                    is Float -> valObj.addProperty("value", value.value as Float)
                }
                when (value.defaultValue) {
                    is String -> valObj.addProperty("default_value", value.defaultValue)
                    is Boolean -> valObj.addProperty("default_value", value.defaultValue)
                    is Int -> valObj.addProperty("default_value", value.defaultValue)
                    is Double -> valObj.addProperty("default_value", value.defaultValue)
                    is Float -> valObj.addProperty("default_value", value.defaultValue)
                }
                valObj.addProperty("comment", value.comment)
            })
        }

        if (!configExists()) {
            LogManager.getLogger().info("config file does not exist, creating.")
            Files.createFile(configLocation())
        }
        Files.write(
            configLocation(),
            GsonBuilder().setPrettyPrinting().create().toJson(json).encodeToByteArray()
        )
    }

    protected abstract fun registerValues()

    protected abstract fun fileName(): String

    /**
     * Initializes each config value defined in [registerValues].
     * #### This should only be called during the mod's initialization stage.
     */
    fun initialize() {
        json = readConfig()
        registerValues()
        saveConfig()
    }
}