package breadmodadvanced.registry

import net.minecraftforge.common.ForgeConfigSpec
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue
import org.apache.commons.lang3.tuple.Pair

@Suppress("PropertyName")
object ModConfigurationAdv {
    val COMMON_SPECIFICATION: Pair<Common, ForgeConfigSpec> = ForgeConfigSpec.Builder().configure { Common(it) }

    val COMMON: Common = COMMON_SPECIFICATION.left

    class Common(builder: ForgeConfigSpec.Builder) {
        val DIESEL_GENERATOR_MAX_BURN_TIME_TICKS: ConfigValue<Int>
        val DIESEL_GENERATOR_RF_PER_TICK: ConfigValue<Int>

        init {
            builder.push("common")

            DIESEL_GENERATOR_MAX_BURN_TIME_TICKS = builder
                .comment("How long the diesel generator will burn for, in ticks")
                .define("dieselGeneratorMaxBurnTime", 200000)
            DIESEL_GENERATOR_RF_PER_TICK = builder
                .comment("How much RF the diesel generator will produce per tick")
                .define("dieselGeneratorRfPerTick", 512)

            builder.pop()
        }
    }
}