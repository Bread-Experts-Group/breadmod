package bread.mod.breadmod.registry.config

import bread.mod.breadmod.ModMainCommon.MOD_ID

object CommonConfig : BreadModConfig() {
    lateinit var HAPPY_BLOCK_SPREAD_RADIUS: ConfigValue<Double>
    lateinit var HAPPY_BLOCK_DIVISIONS: ConfigValue<Int>

    override fun fileName(): String = "$MOD_ID-common"

    override fun registerValues() {
        HAPPY_BLOCK_DIVISIONS = getOrDefault(
            "happy_block_div",
            ConfigValue.Builder<Int>()
                .define(8, "happy_block_div")
                .comment("How many happy blocks will be created after the first explosion")
        )
        HAPPY_BLOCK_SPREAD_RADIUS = getOrDefault(
            "happy_block_rng",
            ConfigValue.Builder<Double>()
                .define(0.5, "happy_block_rng")
                .comment("How far the happy block will spread it's divided blocks")
        )
    }
}