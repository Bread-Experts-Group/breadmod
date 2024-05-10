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
        val BREAD_AMULET_FEED_TIME_TICKS: ConfigValue<Int>
        val BREAD_AMULET_FEED_AMOUNT: ConfigValue<Int>
        val BREAD_AMULET_STACKS: ConfigValue<Boolean>

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
            BREAD_AMULET_FEED_TIME_TICKS = builder
                .comment("Time, in ticks, before the bread amulet will feed someone")
                .define("breadAmuletFeedTime", 20 * 10)
            BREAD_AMULET_FEED_AMOUNT = builder
                .comment("The amount the bread amulet will feed someone - one hunger icon is 2")
                .define("breadAmuletFeedAmount", 2)
            BREAD_AMULET_STACKS = builder
                .comment("Allows the bread amulet to stack effects with other bread amulets")
                .define("breadAmuletStacks", false)

            builder.pop()
        }
    }
}