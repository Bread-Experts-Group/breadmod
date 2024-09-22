package bread.mod.breadmod.registry.config

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
     * Attempts to retrieve the current [defaultValue].
     *
     * @return [defaultValue]
     * @throws NullPointerException if [defaultValue] is null
     * @author Logan McLean
     */
    fun defaultValueOrThrow(): T =
        if (defaultValue != null) defaultValue else throw NullPointerException(this::class.qualifiedName)

    class Builder<T> {
        lateinit var name: String
        var value: T? = null
        var defaultValue: T? = null
        lateinit var comment: String

        fun define(defaultValue: T, name: String): Builder<T> =
            this.also { it.name = name; it.value = defaultValue; it.defaultValue = defaultValue }

        fun comment(comment: String): Builder<T> = this.also { it.comment = comment }

        fun build(): ConfigValue<T> {
            Objects.requireNonNull(value, "value cannot be null.")
            Objects.requireNonNull(defaultValue, "default value cannot be null.")
            return ConfigValue(name, value, defaultValue, comment)
        }
    }
}