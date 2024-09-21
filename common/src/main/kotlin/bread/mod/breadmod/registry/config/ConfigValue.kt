package bread.mod.breadmod.registry.config

import bread.mod.breadmod.registry.config.BreadModConfig.saveConfig
import java.util.Objects

class ConfigValue<T>(
    val name: String,
    var value: T?,
    val defaultValue: T?,
    val comment: String
) {
    /**
     * Attempts to retrieve the current [value].
     *
     * @return [value]
     * @throws NullPointerException if [value] is null
     * @author Logan McLean
     */
    fun valueOrThrow(): T = if (value != null) value!! else throw NullPointerException(this::class.qualifiedName)

    /**
     * Overwrites the current [value] with [newValue].
     * * Config file automatically updates upon calling this function
     *
     * @author Logan McLean
     */
    fun setNewValue(newValue: T) {
        value = newValue
        // todo find a cleaner solution for this
        BreadModConfig.valueList.remove(this)
        BreadModConfig.valueList.add(this)
        println(BreadModConfig.valueList)
        saveConfig()
    }

    class Builder<T> {
        lateinit var name: String
        var value: T? = null
        var defaultValue: T? = null
        lateinit var comment: String

        fun define(value: T, name: String): Builder<T> {
            this.value = value
            this.name = name
            return this
        }

        fun defaultValue(value: T): Builder<T> {
            this.defaultValue = value
            return this
        }

        fun comment(comment: String): Builder<T> {
            this.comment = comment
            return this
        }

        fun build(): ConfigValue<T> {
            Objects.requireNonNull(value, "value cannot be null.")
            Objects.requireNonNull(defaultValue, "default value cannot be null.")
            val configValue = ConfigValue(name, value, defaultValue, comment)
            BreadModConfig.valueList.add(configValue)
            return configValue
        }
    }
}