package breadmod

import net.minecraftforge.common.ForgeConfigSpec
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue

object ModConfiguration {
    val DECAY_CHANCE_PER_TICK: ConfigValue<Int>
    val EFFECT_DISTANCE_MULTIPLIER: ConfigValue<Double>

    val SPECIFICATION: ForgeConfigSpec = ForgeConfigSpec.Builder().also {
        it.push("BreadMod Configurations")

        DECAY_CHANCE_PER_TICK = it
            .comment("Chance (1 in ...) for every tick to cause damage to decayable armor (e.g. bread)")
            .define("Decay Chance", 120)
        EFFECT_DISTANCE_MULTIPLIER = it
            .comment("How many blocks does one amplification point on a potion effect extend in dopable armor?")
            .define("Doped Armor Distance Multipler", 1.5)

        it.pop()
    }.build()
}