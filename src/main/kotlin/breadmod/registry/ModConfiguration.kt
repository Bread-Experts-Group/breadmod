package breadmod.registry

import net.minecraftforge.common.ForgeConfigSpec
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue
import org.apache.commons.lang3.tuple.Pair

@Suppress("PropertyName")
object ModConfiguration {
    val COMMON_SPECIFICATION: Pair<Common, ForgeConfigSpec> = ForgeConfigSpec.Builder().configure { Common(it) }

    val COMMON: Common = COMMON_SPECIFICATION.left

    class Common(builder: ForgeConfigSpec.Builder) {
        val DECAY_CHANCE_PER_TICK: ConfigValue<Int>
        val EFFECT_DISTANCE_MULTIPLIER: ConfigValue<Double>
        val ULTIMATE_BREAD_MAX_CREATIVE_TIME_TICKS: ConfigValue<Int>

        init {
            builder.push("common")

            DECAY_CHANCE_PER_TICK = builder
                .comment("Chance (1 in ...) for every tick to cause damage to decayable armor (e.g. bread)")
                .define("decayChance", 120)
            EFFECT_DISTANCE_MULTIPLIER = builder
                .comment("How many blocks does one amplification point on a potion effect extend in dopable armor?")
                .define("dopedArmorDistanceMultipler", 1.5)
            ULTIMATE_BREAD_MAX_CREATIVE_TIME_TICKS = builder
                .comment("How long the ultimate bread will give someone creative, in ticks")
                .define("ultimateBreadMaxTicks", 20 * 20)

            builder.pop()
        }
    }
}