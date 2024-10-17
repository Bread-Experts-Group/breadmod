package bread.mod.breadmod.registry.config

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import java.util.*

class ConfigValue<T>(
    val name: String,
    var value: T?,
    val defaultValue: T?,
    val comment: String
) {
    // todo figure out codecs..
    // todo proof of concept for a ConfigValue codec is here now,
    //  but we need a generic solution to support all types a ConfigValue can hold so we don't just spam vals for each type
    /* https://docs.neoforged.net/docs/networking/streamcodecs/#using-stream-codecs */
    /* https://docs.neoforged.net/docs/datastorage/codecs/#using-codecs */
    companion object {
        val CODEC_INT: Codec<ConfigValue<Int>> = RecordCodecBuilder.create { instance ->
            instance.group(
                Codec.STRING.fieldOf("name").forGetter(ConfigValue<Int>::name),
                Codec.INT.fieldOf("value").forGetter(ConfigValue<Int>::value),
                Codec.INT.fieldOf("default_value").forGetter(ConfigValue<Int>::defaultValue),
                Codec.STRING.fieldOf("comment").forGetter(ConfigValue<Int>::comment),
            ).apply(instance) { name, value, default, comment -> ConfigValue(name, value, default, comment) }
        }

        val STREAM_CODEC_INT : StreamCodec<ByteBuf, ConfigValue<Int>> = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, ConfigValue<Int>::name,
            ByteBufCodecs.INT, ConfigValue<Int>::value,
            ByteBufCodecs.INT, ConfigValue<Int>::defaultValue,
            ByteBufCodecs.STRING_UTF8, ConfigValue<Int>::comment
        ) { name, value, default, comment -> ConfigValue(name, value, default, comment) }
    }

    /**
     * Attempts to retrieve the current [value].
     *
     * @return [value]
     * @throws NullPointerException if [value] is null
     * @author Logan McLean
     */
    fun valueOrThrow(): T = value ?: throw NullPointerException(this::class.qualifiedName)

    /**
     * Attempts to retrieve the current [defaultValue].
     *
     * @return [defaultValue]
     * @throws NullPointerException if [defaultValue] is null
     * @author Logan McLean
     */
    fun defaultOrThrow(): T = defaultValue ?: throw NullPointerException(this::class.qualifiedName)

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