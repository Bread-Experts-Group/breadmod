package breadmod.registry

import net.minecraftforge.common.ForgeConfigSpec
import net.minecraftforge.common.ForgeConfigSpec.Builder
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue
import org.apache.commons.lang3.tuple.Pair

@Suppress("PropertyName")
object ModConfiguration {
    val COMMON_SPECIFICATION: Pair<Common, ForgeConfigSpec> = Builder().configure { Common(it) }
    val CLIENT_SPECIFICATION: Pair<Client, ForgeConfigSpec> = Builder().configure { Client(it) }

    val COMMON: Common = COMMON_SPECIFICATION.left
    val CLIENT: Client = CLIENT_SPECIFICATION.left

    class Client(builder: Builder) {
        val ALT_TOOLGUN_MODEL: ConfigValue<Boolean>

        init {
            builder.push("client")

            ALT_TOOLGUN_MODEL = builder
                .comment("Toggle for the alternative tool gun model")
                .define("useAltToolgunModel", false)

            builder.pop()
        }
    }

    class Common(builder: Builder) {
        val DECAY_CHANCE_PER_TICK: ConfigValue<Int>
        val EFFECT_DISTANCE_MULTIPLIER: ConfigValue<Double>
        val ULTIMATE_BREAD_MAX_CREATIVE_TIME_TICKS: ConfigValue<Long>
        val BREAD_AMULET_FEED_TIME_TICKS: ConfigValue<Int>
        val BREAD_AMULET_FEED_AMOUNT: ConfigValue<Int>
        val BREAD_AMULET_STACKS: ConfigValue<Boolean>
        val HAPPY_BLOCK_DIVISIONS: ConfigValue<Int>
        val HAPPY_BLOCK_SPREAD_RADIUS: ConfigValue<Double>
        val GENERATOR_MAX_BURN_TIME_TICKS: ConfigValue<Int>
        val GENERATOR_RF_PER_TICK: ConfigValue<Int>

        init {
            builder.push("common")

            DECAY_CHANCE_PER_TICK = builder
                .comment("Chance (1 in ...) for every tick to cause damage to decayable armor (e.g. bread)")
                .define("decayChance", 120)
            EFFECT_DISTANCE_MULTIPLIER = builder
                .comment("How many blocks does one amplification point on a potion effect extend in dopable armor?")
                .define("dopedArmorDistanceMultiplier", 1.5)
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
            HAPPY_BLOCK_DIVISIONS = builder
                .comment("How many happy blocks will be created after the first explosion")
                .define("happyBlockDiv", 8)
            HAPPY_BLOCK_SPREAD_RADIUS = builder
                .comment("How far the happy block will spread it's divided blocks")
                .define("happyBlockRng", 0.5)
            GENERATOR_MAX_BURN_TIME_TICKS = builder
                .comment("How long the generator will burn for, in ticks")
                .define("generatorMaxBurnTime", 20000)
            GENERATOR_RF_PER_TICK = builder
                .comment("How much RF the generator will produce per tick")
                .define("generatorRfPerTick", 64)

            builder.pop()
        }
    }
}